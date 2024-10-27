package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.Message;
import util.User;

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
	
	public void broadcast(Message message) {
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
			if(ch.getUser().getName().equals(username) && !ch.isClosed()) {
				ch.sendMessage(new Message("You have been kicked from the server", new User("Admin")));
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
		private User user;
		
		public ConnectionHandler(Socket client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			try {
				out = new PrintWriter(client.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				user = User.decode(in.readLine());
				
				broadcast(new Message(user.getName() + " joined chat!", new User("Admin")));
				
				String message;
				while((message = Message.decode(in.readLine()).getContents()) != null){
					if(message.startsWith("/username ")) {
						String[] messageSplit = message.split(" ", 2);
						if(messageSplit.length == 2) {
							sendMessage(new Message("Successfully changed username to: " + messageSplit[1], new User("Admin")));
							broadcast(new Message(user.getName() + " renamed themselves to " + messageSplit[1], new User("Admin")));
							user.setName(messageSplit[1]);
						}
						else {
							sendMessage(new Message("No username provided", new User("Admin")));
						}
					}
					else if(message.startsWith("/leave")) {
						broadcast(new Message(user.getName() + " left the server", new User("Admin")));
						shutdown();
					}
					else if(message.startsWith("/kick ")) {
						String[] messageSplit = message.split(" ", 2);
						if(messageSplit.length == 2) {
							if(kick(messageSplit[1])) {
								broadcast(new Message(user.getName() + " kicked " + messageSplit[1] + " from the server", new User("Admin") ));
							}
							else {
								sendMessage(new Message("invalid username", new User("Admin")));
							}
						}
						else {
							sendMessage(new Message("No username provided", new User("Admin")));
						}
						
					}
					else {
						broadcast(new Message(message, user));
					}
				}
			} catch (IOException e) {
				shutdown();
			}
			
		}
		
		public void sendMessage(Message message) {
			out.println(message.encode());
		}
		
		public void shutdown() {
			try {
				out.close();
				if(!client.isClosed()) {
					client.close();
				}
				in.close();
			} catch (IOException e) {
				broadcast(new Message(e.toString(), new User("Admin")));
				e.printStackTrace();
			}
		}
		
		public boolean isClosed() {
			return client.isClosed();
		}
		
		public User getUser() {
			return user;
		}

	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

}