import java.io.*;
import java.net.*;

/**
 * Manages IRC server connections.
 * @author kroche
 */

public class Connection {

	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	
	private String nick = "test_nickname";
	private String login = "test_nickname";
	private String channel;
	//public PrintWriter logfile;
	
	public Connection(String server, int port, String version) throws IOException {
		socket = new Socket(server, port);
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//logfile = new PrintWriter(".coffirc.debug.log");;
		
		// start reader thread
		ReaderThread readerthread = new ReaderThread(reader, writer);
		readerthread.start();
		
		// ident to ircd
		writer.write("NICK " + nick + "\r\n");
		writer.write("USER " + login + " 8 * : coffirc v"+version+"\r\n");
		writer.flush();
		
	}
	
	public void close() throws IOException {
		socket.close();
		// TODO: remove self object for coffirc to know when disconnected
	}
	
	private void sendLine(String line) throws IOException {
		writer.write(line + "\r\n");
		writer.flush();
	}
	
	public void sendPrivmsg(String line) throws IOException {
		this.sendLine("PRIVMSG " + channel + " :" + line);
	}
	
	public void joinChannel(String newChannel, String chanKey) throws IOException {
		channel = newChannel;
		if(chanKey == null){
			this.sendLine("JOIN " + channel);
		}
		else {
			this.sendLine("JOIN " + channel + " " + chanKey);
		}
	}
	
	public void partChannel() throws IOException {
		this.sendLine("PART " + channel);
	}
	
	public void changeNick(String newNick) throws IOException{
		this.sendLine("NICK " + newNick);
	}
	
	public void listChans() throws IOException{
		this.sendLine("LIST ");
	}	
}
