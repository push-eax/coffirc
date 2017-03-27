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
					System.out.println(line);
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
