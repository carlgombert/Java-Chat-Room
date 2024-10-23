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
			ch.shutdown();
		}
	}
	
	public boolean kick(String username) {
		for(ConnectionHandler ch : connections) {
			if(ch.getUsername().equals(username) && !ch.isClosed()) {
				ch.sendMessage("You have been kicked from the server");
				ch.shutdown();
				return true;
			}
		}
		return false;
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
					else if(message.startsWith("/kick ")) {
						String[] messageSplit = message.split(" ", 2);
						if(messageSplit.length == 2) {
							if(kick(messageSplit[1])) {
								broadcast(username + " kicked " + messageSplit[1] + " from the server");
							}
							else {
								out.println("invalid username");
							}
						}
						else {
							out.println("No username provided");
						}
						
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
		
		public void shutdown() {
			try {
				out.close();
				if(!client.isClosed()) {
					client.close();
				}
				in.close();
			} catch (IOException e) {
				broadcast(e.toString());
				e.printStackTrace();
			}
		}
		
		public boolean isClosed() {
			return client.isClosed();
		}
		
		public String getUsername() {
			return username;
		}

	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

}