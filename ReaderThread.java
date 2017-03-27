import java.io.*;

/**
 * Handles reading of received messages from IRC server.
 * Instantiated by the Connection class in a new thread for non-blocking output.
 * @author kroche
 */

public class ReaderThread extends Thread {
	private Thread t;
	private final BufferedReader reader;
	private final BufferedWriter writer;
	public Writer logfile;

	ReaderThread(BufferedReader reader, BufferedWriter writer) {
		this.reader = reader;
		this.writer = writer;
		try{
			logfile = new PrintWriter(".coffirc.debug.log");
		}
		catch(FileNotFoundException e){
			System.err.println("Log file error");
		}
	}

	public void run() {
		System.out.println("Started run");
		String lineparts[];
		String lineparts2[];
		try {
			String line;
			// read until connected
			while ((line = reader.readLine()) != null) {
				logfile.write(line+"\n");
				logfile.flush();
				if (line.contains("004")) {
					System.out.println("We are now logged in.");
					break;
				}
				else if (line.startsWith("PING")) {
					// PING response
					writer.write("PONG " + line.substring(5) + "\r\n");
					logfile.write("PONG " + line.substring(5) + "\r\n");
					logfile.flush();
					writer.flush();
				}
				else if (line.contains("433")) {
					System.out.println("Nickname is already in use.");
					logfile.write("Nick in use, user needs must select another\n");
					logfile.flush();
					return;
				}
			}
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("PING")) {
					// PING response
					writer.write("PONG " + line.substring(5) + "\r\n");
					logfile.write("PONG " + line.substring(5) + "\r\n");
					logfile.flush();
					writer.flush();
				}
				else {
					// Print each line the client receives.
					String keywords[] = {"JOIN", "PRIVMSG", "PART"};
					int keyword = -1;
					for(int i = 0; i<keywords.length; i++){
						if(line.contains(keywords[i])){
							keyword = i;
							break;
						}
					}
					switch(keyword){
					case 0:
						lineparts = line.substring(1).split("!");
						lineparts2 = lineparts[1].split(":");
						System.out.println(lineparts[0] + " has joined "+lineparts2[lineparts2.length-1]);
						break;
					case 1:
						lineparts = line.substring(1).split("!");
						lineparts2 = lineparts[lineparts.length-1].split(":");
						String message = lineparts2[lineparts2.length-1];
						System.out.println("["+lineparts[0]+"] "+message);
						break;
					case 2:
						lineparts = line.substring(1).split("!");
						System.out.println(lineparts[0] + " left the channel.");
						break;
					default:
						System.out.println(line);
						break;
					}
					logfile.write(line+"\n");
					logfile.flush();
				}
			}
			//logfile.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void start () {
		if (t == null) {
		   t = new Thread (this);
		   t.start ();
		}
	}
}
