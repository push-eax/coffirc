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
	String nickcolor;
	String headcolor;
	String resetcolor;
	String tempbuff;
	String whitecolor;
	String blackcolor;
	String navycolor;
	String greencolor;
	String redcolor;
	String marooncolor;
	String purplecolor;
	String olivecolor;
	String yellowcolor;
	String lgreencolor;
	String tealcolor;
	String cyancolor;
	String rbluecolor;
	String magentacolor;
	String greycolor;
	String lgreycolor;
	int colorcode;
	String responsecodes;

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
		responsecodes = "";
	}
	
	public void printcolor(String line){
		for(int ii = 0; ii<line.length(); ii++){
			if(line.charAt(ii) == '\003'){
				if(line.length()>ii+2){
					colorcode=16;
					if(Character.isDigit(line.charAt(ii+1))){
						colorcode = Character.getNumericValue(line.charAt(ii+1));
						ii++;
						if(Character.isDigit(line.charAt(ii+1))){
							colorcode += (Character.getNumericValue(line.charAt(ii+1))*10);
							ii++;
						}
					}
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
	}

	public void run() {
		System.out.println("Started run");
		String lineparts[];
		String lineparts2[];
		nickcolor    = "";
		headcolor    = "";
		resetcolor   = "";
		tempbuff     = "";
		whitecolor   = "";
		blackcolor   = "";
		navycolor    = "";
		greencolor   = "";
		redcolor     = "";
		marooncolor  = "";
		purplecolor  = "";
		olivecolor   = "";
		yellowcolor  = "";
		lgreencolor  = "";
		tealcolor    = "";
		cyancolor    = "";
		rbluecolor   = "";
		magentacolor = "";
		greycolor    = "";
		lgreycolor   = "";
		colorcode    = 0;
		if(!windows){
			nickcolor    = "\033[1;37m";
			headcolor    = "\033[0;33m";
			resetcolor   = "\033[m";
			whitecolor   = "\033[0;37m";
			blackcolor   = "\033[0;30m";
			navycolor    = "\033[0;34m";
			greencolor   = "\033[0;32m";
			redcolor     = "\033[1;31m";
			marooncolor  = "\033[0;31m";
			purplecolor  = "\033[0;35m";
			olivecolor   = "\033[0;33m";
			yellowcolor  = "\033[1;33m";
			lgreencolor  = "\033[1;32m";
			tealcolor    = "\033[0;36m";
			cyancolor    = "\033[1;36m";
			rbluecolor   = "\033[1;34m";
			magentacolor = "\033[1;35m";
			greycolor    = "\033[1;30m";
			lgreycolor   = "\033[0;37m";
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
					//                    0       1          2       3       4      5      6      7      8      9      10     11     12     13     14     15     16     17     18        19          20
					String keywords[] = {"JOIN", "PRIVMSG", "PART", "QUIT", "332", "333", "353", "366", "375", "372", "005", "251", "255", "265", "266", "252", "253", "254", "NOTICE", "MODE", "NICK"};
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
					case 0: //JOIN
						lineparts = line.substring(1).split("!");
						lineparts2 = lineparts[1].split(":");
						System.out.println(lineparts[0] + " has joined "+lineparts2[lineparts2.length-1]);
						break;
					case 1: //PRIVMSG
						lineparts = line.substring(1).split("!");
						lineparts2 = lineparts[lineparts.length-1].split(":");
						line = lineparts2[lineparts2.length-1];
						System.out.print(nickcolor+"["+lineparts[0]+"] "+resetcolor);
						printcolor(line);
						System.out.print(resetcolor);
						break;
					case 2: //PART
						lineparts = line.substring(1).split("!");
						System.out.println(lineparts[0] + " left the channel.");
						break;
					case 3: //QUIT
						lineparts = line.substring(1).split("!");
						lineparts2 = lineparts[lineparts.length-1].split(":");
						System.out.println(lineparts[0] + " quit ("+lineparts2[lineparts2.length-1]+")");
						break;
					case 4: //Topic
						System.out.print("Topic: ");
					case 6: //Nameslist
						lineparts = line.split(":");
						printcolor(lineparts[lineparts.length-1]);
					case 5: //Topic set time
					case 7: //End of names list
					case 19: //user mode
						break;
					case 8://MOTD start line
					case 9://MOTD
						lineparts = line.split(":");
						printcolor(lineparts[lineparts.length-1]);
						break;
					case 10: //RPL_ISUPPORT
						lineparts=line.split(" ");
						for(int i = 3; i<lineparts.length; i++){
							responsecodes+=lineparts[i]+" ";
						}
						break;
					case 11: //LUSERCLIENT
					case 12: //RPL_LUSERME
					case 13: //RPL_LOCALUSERS
					case 14: //RPL_GLOBALUSERS
						lineparts = line.split(":");
						System.out.println(lineparts[lineparts.length-1]);
						break;
					case 18: //NOTICE
						System.out.print("NOTICE ");
					case 15: //RPL_LUSEROP
					case 16: //RPL_LUSERUNKNOWN
					case 17: //RPL_LUSERCHANNELS
						lineparts = line.split(" ");
						for(int i = 3; i<lineparts.length; i++){
							System.out.print(lineparts[i]+ " ");
						}
						System.out.println();
						break;
					case 20:
						lineparts = line.substring(1).split("!");
						lineparts2 = line.split(":");
						System.out.println(nickcolor + lineparts[0] + " has changed their nick to " + lineparts2[lineparts2.length-1] + resetcolor);
						break;
					default:
						printcolor(line);
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
