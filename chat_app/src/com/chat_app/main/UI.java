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
                String message = chatInput.getText();  // Get the input text
                if (!message.trim().isEmpty()) {        // Only send if non-empty
                    client.sendMessage(message);
                    chatInput.setText("");   // Clear the input field
                }
            }
        });

        // Add components to the frame
        add(scrollPane, BorderLayout.CENTER);          // Chat area in center
        JPanel bottomPanel = new JPanel(new BorderLayout()); // Panel for input and button
        bottomPanel.add(chatInput, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);          // Bottom panel with input and button

        // Set the frame to be visible
        setVisible(true);
        
        client.run();
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