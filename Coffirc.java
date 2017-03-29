import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Handles the program main loop.
 * @author kroche
 */
public class Coffirc {
	/**
	 * @param args
	 * @throws java.lang.Exception
	 */
	public static void main(String[] args) throws Exception {
		String version = "0.1.5c";//On adding a new feature, incrment lowest minor.
		                         //On fixing existing feature, append/increase letter
		                         //On simply changing stuff without committing, append -dev
		                         //When committing, make sure there is no -dev suffix
		String rawInput;
		Connection connection = null;
		//PrintWriter logfile = new PrintWriter(".coffirc.debug.log");
		
		System.out.println("coffirc v"+version);
		
		//In order to consitantly represent common problems, we should use a list of error
		//messages for the more standard ones. (ex. not connected)
		
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				/*
				if (connection != null && connection.stage == 2){
					connection.readLines();
				}*/
				
				//System.out.print("> ");
				rawInput = scanner.nextLine();
				//System.out.print("> ");
				
				if (rawInput.charAt(0) == '/') {
					String[] commArr = rawInput.substring(1).split(" ");
					String command = commArr[0];
					
					if ("connect".equals(command)){
						if (connection == null){
							if (commArr.length == 3){
								connection = new Connection(commArr[1], Integer.parseInt(commArr[2]), version); 
							}
							else {	  
								System.err.println("Usage: /connect <server> <port>");
							} 
						}
						else {
							System.err.println("Already connected. Use /disconnect.");
						}
					}
					else {
						if (connection == null && command.equals("quit") == false) {
							System.err.println("Not connected. Use the /connect command to start.");
						}
						else {
							System.out.println(command);
							String commands[] = {"disconnect", "quit", "nick", "part", "join", "list"};
							int cindex = -1;
							for(int i = 0; i<commands.length; i++){
								if(command.equals(commands[i])) cindex = i;
							}
							switch(cindex){
								case 0:
									connection.close();
									connection = null;
									if(connection !=null) System.err.println("Failed to disconnect.");
									else System.err.println("Disconnected.");
									break;
								case 1:
									System.out.println("Program quit gracefully. Goodbye!");
									if(connection != null){
										connection.close();
										connection = null;
									}
									//logfile.close();
									System.exit(0);
									break;
								case 2:
									connection.changeNick(commArr[1]);
									break;
								case 3:
									connection.partChannel();
									break;
								case 4:
									if (commArr.length == 2){
										connection.joinChannel(commArr[1], null);
									}
									else {
										connection.joinChannel(commArr[1], commArr[2]);
									}
									break;
								case 5:
									connection.listChans();
									break;
								default:
									System.err.print("Bad command. Valid commands are:");
									for(int i = 0; i<commands.length; i++){
										System.err.print(" /"+commands[i]);
									}
									System.err.println();
									break;
							}
						}
					}
				}
				else {
					if (connection != null) {
						connection.sendPrivmsg(rawInput);
					}
					else {
						System.err.println("Not connected. Use the /connect command to start.");
					}
				}
			}
		}
	}
}
