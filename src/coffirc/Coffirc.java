/*
 * Copyright 2016 Kiernan Roche
 * All rights reserved.
 */

package coffirc;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Handles the program main loop.
 * @author kroche
 */
public class Coffirc {

    /**
     * @param args the command line arguments
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
                    //System.out.println("Command entered!");
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
                        if (connection == null) {
                            System.err.println("Not connected.");
                        }
                        else {
                            // I really hate if-else-if ladders but switch:case doesn't work here
                            System.out.println(command);
                            
                            if("disconnect".equals(command)){
                                connection.close();
                                System.err.println("Disconnected.");
                            }
                            else if ("quit".equals(command)) {
                                System.out.println("Program terminated.");
                                break;
                            }
                            else if ("nick".equals(command)) {
                                connection.changeNick(commArr[1]);
                            }
                            else if ("part".equals(command)) {
                                connection.partChannel();
                            }
                            else if ("join".equals(command)) {
                                if (commArr.length == 2){
                                    connection.joinChannel(commArr[1], null);
                                }
                                else {
                                    connection.joinChannel(commArr[1], commArr[2]);
                                }
                            }
                            else if ("list".equals(command)) {
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
