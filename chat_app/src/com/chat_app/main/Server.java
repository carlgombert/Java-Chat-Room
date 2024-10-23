package com.chat_app.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// server listens for client requests to connect and opens connection handlers
public class Server implements Runnable{

	private ArrayList<ConnectionHandler> connections;
	private ServerSocket server;
	private boolean done;
	private ExecutorService pool;
	
	public Server() {
		connections = new ArrayList<ConnectionHandler>();
		done = false;
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(9999);
			
			pool = Executors.newCachedThreadPool();
			while(!done) {
				Socket client = server.accept();
				
				ConnectionHandler handler = new ConnectionHandler(client);
				connections.add(handler);
				pool.execute(handler);
			}
			
		} catch (Exception e) {
			shutdown();
		}
		
	}
	
	public void broadcast(String message) {
		for(ConnectionHandler ch : connections) {
			ch.sendMessage(message);
		}
	}
	
	public void shutdown() {
		done = true;
		if(!server.isClosed()) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(ConnectionHandler ch : connections) {
			ch.shutDown();
		}
	}
	
	class ConnectionHandler implements Runnable{

		private Socket client;
		private BufferedReader in;
		private PrintWriter out;
		private String username;
		
		public ConnectionHandler(Socket client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			try {
				out = new PrintWriter(client.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				out.println("Please enter a username: ");
				username = in.readLine();
				
				broadcast(username + " joined chat!");
				
				String message;
				while((message = in.readLine()) != null){
					if(message.startsWith("/username ")) {
						String[] messageSplit = message.split(" ", 2);
						if(messageSplit.length == 2) {
							out.println("Successfully changed username to: " + messageSplit[1]);
							broadcast(username + " renamed themselves to " + messageSplit[1]);
							username = messageSplit[1];
						}
						else {
							out.println("No username provided");
						}
					}
					else if(message.startsWith("/leave")) {
						broadcast(username + " left the server");
						shutdown();
					}
					else {
						broadcast(username + ": " + message);
					}
				}
			} catch (IOException e) {
				shutdown();
			}
			
		}
		
		public void sendMessage(String message) {
			out.println(message);
		}
		
		public void shutDown() {
			try {
				in.close();
				out.close();
				if(!client.isClosed()) {
					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

}
