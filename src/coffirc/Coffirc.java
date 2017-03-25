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
		
		String rawInput;
		Connection connection = null;
		
		System.out.println("coffirc v0.1.0");
		
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
								connection = new Connection(commArr[1], Integer.parseInt(commArr[2])); 
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
							System.err.println("Not connected.");
						}
						else {
							// I really hate if-else-if ladders but switch:case doesn't work here
							System.out.println(command);
							string commands[] = {"disconnect", "quit", "nick", "part", "join", "list"};
							int cindex = 666;
							for(int i = 0; i<commands.length; i++){
								if(command == commands[i]) cindex = i;
							}
							switch(cindex){
								case 0:
									connection.close();
									System.err.println("Disconnected.");
									break;
								case 1:
									System.out.println("Program terminated.");
									exit(0);
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
							}
						}
					}
				}
				else {
					if (connection != null) {
						connection.sendPrivmsg(rawInput);
					}
					else {
						System.err.println("Not connected.");
					}
				}
			}
		}
	}
}
