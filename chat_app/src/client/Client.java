package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingUtilities;

import util.Message;
import util.User;

public class Client implements Runnable{

	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private boolean done;
	private UI ui;
	private User user;
	private String hostIP = "10.131.2.139";
	
	
	@Override
	public void run() {
		try {
			client = new Socket(hostIP, 9999);
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			out.println(user.encode());
			
			String inMessage;
			while((inMessage = in.readLine()) != null) {
				ui.addMessage(Message.decode(inMessage));
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
			out.println(new Message(message, user).encode());
			shutdown();
		}
		else {
			out.println(new Message(message, user).encode());
		}
	}
	

	public UI getUi() {
		return ui;
	}

	public void setUi(UI ui) {
		this.ui = ui;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
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
