/*
 * Copyright 2016 Kiernan Roche
 * All rights reserved.
 */
package coffirc;

import java.io.*;
import java.net.*;

/**
 * Manages IRC server connections.
 * @author kroche
 */

public class Connection {

    public int stage;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    
    private String nick = "default";
    private String login = "default";
    public String channel;
    
    public Connection(String server, int port) throws IOException {
        socket = new Socket(server, port);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        // start reader thread
        ReaderThread readerthread = new ReaderThread(reader, writer);
        readerthread.start();
        
        // ident to ircd
        writer.write("NICK " + nick + "\r\n");
        writer.write("USER " + login + " 8 * : coffirc v0.1.0\r\n");
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
