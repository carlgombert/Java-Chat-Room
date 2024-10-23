package com.chat_app.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI extends JFrame {

	private JFrame signInFrame;
	private JTextField usernameField;
    private JTextField passwordField;
    private JTextArea chatArea;     
    private JTextField chatInput;   
    private JButton sendButton;
    private JButton quitButton;
    private Client client;

    public UI(Client client) {
        this.client = client;
        client.setUi(this);
        signInUI();
    }
    
    private void signInUI() {
    	signInFrame = new JFrame("Sign In");
        signInFrame.setSize(500, 500);
        signInFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        signInFrame.setLayout(new BorderLayout());

        JPanel signInPanel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14)); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        signInPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14)); 
        gbc.gridx = 1;
        gbc.gridy = 0;
        signInPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Host IP:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14)); 
        gbc.gridx = 0;
        gbc.gridy = 1;
        signInPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14)); 
        gbc.gridx = 1;
        gbc.gridy = 1;
        signInPanel.add(passwordField, gbc);

        JButton signInButton = new JButton("Sign In");
        signInButton.setFont(new Font("Arial", Font.PLAIN, 14)); 
        gbc.gridx = 1;
        gbc.gridy = 2;
        signInPanel.add(signInButton, gbc);
        
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                client.setUsername(usernameField.getText());
                if(passwordField.getText() != "") {
                	client.setHostIP(passwordField.getText());
                }
                signInFrame.dispose();  
                chatUI();         
                
            }
        });

        signInFrame.add(signInPanel, BorderLayout.CENTER);
        signInFrame.setVisible(true);
    }
    
    private void chatUI() {
    	setTitle("Java Message Room");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);  
        JScrollPane scrollPane = new JScrollPane(chatArea); 

        chatInput = new JTextField();
        sendButton = new JButton("Send");
        quitButton = new JButton("Leave");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = chatInput.getText();  
                if (!message.trim().isEmpty()) {        
                    client.sendMessage(message);
                    chatInput.setText("");   
                }
            }
        });
        
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	client.sendMessage("/leave");
                System.exit(0); 
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(quitButton);
        add(topPanel, BorderLayout.NORTH);
        
        add(scrollPane, BorderLayout.CENTER);          
        JPanel bottomPanel = new JPanel(new BorderLayout()); 
        bottomPanel.add(chatInput, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);          

        setVisible(true);
        
        Thread t = new Thread(client);
		t.start();
    }

    
    public void recieveMessage(String message) {
    	chatArea.setText(chatArea.getText() + message + "\n");
    }
}