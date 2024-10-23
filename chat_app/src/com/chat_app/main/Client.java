package com.chat_app.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingUtilities;

public class Client implements Runnable{

	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private boolean done;
	public UI ui;
	
	
	@Override
	public void run() {
		try {
			client = new Socket("10.131.2.139", 9999);
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			String inMessage;
			while((inMessage = in.readLine()) != null) {
				//System.out.println(inMessage);
				ui.getMessage(inMessage);
			}
		} catch (IOException e) {
			shutdown();
		}
		
	}
	
	public void shutdown() {
		done = true;
		try {
			in.close();
			out.close();
			if(!client.isClosed()) {
				client.close();
			}
		} catch (IOException e) {
			// ignore
		}
		
	}
	
	public void sendMessage(String message) {
		if(message.equals("/leave")) {
			out.println(message);
			shutdown();
		}
		else {
			out.println(message);
		}
	}
	

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UI(new Client());
            }
        });
	}
}
