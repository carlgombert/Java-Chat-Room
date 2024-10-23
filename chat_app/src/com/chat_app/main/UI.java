package com.chat_app.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI extends JFrame {

    private JTextArea chatArea;     
    private JTextField chatInput;   
    private JButton sendButton;    

    public UI(Client client) {
        setTitle("Java Message Room");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);  
        JScrollPane scrollPane = new JScrollPane(chatArea); 

        chatInput = new JTextField();
        sendButton = new JButton("Send");

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

        
        add(scrollPane, BorderLayout.CENTER);          
        JPanel bottomPanel = new JPanel(new BorderLayout()); 
        bottomPanel.add(chatInput, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);          

 
        setVisible(true);
        
        client.ui = this;
        
        Thread t = new Thread(client);
		t.start();
        //client.run();
        
    }
    
    public void getMessage(String message) {
    	chatArea.setText(chatArea.getText() + message + "\n");
    }
}