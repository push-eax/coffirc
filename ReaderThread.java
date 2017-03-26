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

	ReaderThread(BufferedReader reader, BufferedWriter writer) {
		this.reader = reader;
		this.writer = writer;
	}

	public void run() {
		System.out.println("Started run");
		try {
			String line;
			// read until connected
			while ((line = reader.readLine()) != null) {
				//System.out.println(line);
				if (line.contains("004")) {
					System.out.println("We are now logged in.");
					break;
				}
				else if (line.startsWith("PING")) {
					// PING response
					writer.write("PONG " + line.substring(5) + "\r\n");
					//System.out.println("PONG " + line.substring(5) + "\r\n");
					writer.flush();
				}
				else if (line.contains("433")) {
					System.out.println("Nickname is already in use.");
					return;
				}
			}
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("PING")) {
					// PING response
					writer.write("PONG " + line.substring(5) + "\r\n");
					System.out.println("PONG " + line.substring(5) + "\r\n");
					writer.flush();
				}
				else {
					// Print each line the bot receives.
					System.out.println(line);
				}
			}
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
