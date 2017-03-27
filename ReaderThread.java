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
	boolean windows;

	ReaderThread(BufferedReader reader, BufferedWriter writer) {
		this.reader = reader;
		this.writer = writer;
		try{
			logfile = new PrintWriter(".coffirc.debug.log");
		}
		catch(FileNotFoundException e){
			System.err.println("Log file error");
		}
		windows = false;
		if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
			windows = true;
		}
	}

	public void run() {
		System.out.println("Started run");
		String lineparts[];
		String lineparts2[];
		String nickcolor = "";
		String headcolor = "";
		String resetcolor = "";
		String tempbuff = "";
		String whitecolor = "";
		String blackcolor = "";
		String navycolor = "";
		String greencolor = "";
		String redcolor = "";
		String marooncolor = "";
		String purplecolor = "";
		String olivecolor = "";
		String yellowcolor = "";
		String lgreencolor = "";
		String tealcolor = "";
		String cyancolor = "";
		String rbluecolor = "";
		String magentacolor = "";
		String greycolor = "";
		String lgreycolor = "";
		int colorcode = 0;
		if(!windows){
			nickcolor    = "\033[1;37m";
			headcolor    = "\033[33m";
			resetcolor   = "\033[m";
			whitecolor   = "\033[37m";
			blackcolor   = "\033[30m";
			navycolor    = "\033[34m";
			greencolor   = "\033[32m";
			redcolor     = "\033[1;31m";
			marooncolor  = "\033[31m";
			purplecolor  = "\033[35m";
			olivecolor   = "\033[33m";
			yellowcolor  = "\033[1;33m";
			lgreencolor  = "\033[1;32m";
			tealcolor    = "\033[36m";
			cyancolor    = "\033[1;36m";
			rbluecolor   = "\033[1;34m";
			magentacolor = "\033[1;35m";
			greycolor    = "\033[1;30m";
			lgreycolor   = "\033[37m";
		}
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
					logfile.write(line+"\n");
					logfile.flush();
					String keywords[] = {"JOIN", "PRIVMSG", "PART"};
					int keyword = -1;
					for(int i = 0; i<keywords.length; i++){
						if(line.contains(keywords[i])){
							keyword = i;
							break;
						}
					}
					line = line.replace("\001", "");
					line = line.replace("\002", "");
					
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
						System.out.println(nickcolor+"["+lineparts[0]+"] "+resetcolor+message);
						break;
					case 2:
						lineparts = line.substring(1).split("!");
						System.out.println(lineparts[0] + " left the channel.");
						break;
					default:
						for(int ii = 0; ii<line.length(); ii++){
							if(line.charAt(ii) == '\003'){
								if(line.length()>ii+2){
									tempbuff = "";
									tempbuff += line.charAt(ii+1);
									tempbuff += line.charAt(ii+2);
									ii+=2;
									try{colorcode = Integer.parseInt(tempbuff);}
									catch(NumberFormatException e){colorcode = 16;}
									switch(colorcode){
									case 0:
										System.out.print(whitecolor);
										break;
									case 1:
										System.out.print(blackcolor);
										break;
									case 2:
										System.out.print(navycolor);
										break;
									case 3:
										System.out.print(greencolor);
										break;
									case 4:
										System.out.print(redcolor);
										break;
									case 5:
										System.out.print(marooncolor);
										break;
									case 6:
										System.out.print(purplecolor);
										break;
									case 7:
										System.out.print(olivecolor);
										break;
									case 8:
										System.out.print(yellowcolor);
										break;
									case 9:
										System.out.print(lgreencolor);
										break;
									case 10:
										System.out.print(tealcolor);
										break;
									case 11:
										System.out.print(cyancolor);
										break;
									case 12:
										System.out.print(rbluecolor);
										break;
									case 13:
										System.out.print(magentacolor);
										break;
									case 14:
										System.out.print(greycolor);
										break;
									case 15:
										System.out.print(lgreycolor);
										break;
									default:
										System.out.print(resetcolor);
										break;
									}
								}else{
									System.out.print(resetcolor);
									ii++;
								}
							} else System.out.print(line.charAt(ii));
						}
						System.out.println();
						break;
					}
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
