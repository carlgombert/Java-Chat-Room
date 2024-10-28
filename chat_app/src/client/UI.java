package client;

import javax.swing.*;

import util.Message;
import util.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI extends JFrame {

	private JFrame signInFrame;
	private JTextField usernameField;
    private JTextField passwordField;
    private JPanel chatPanel;    
    private JTextField chatInput;   
    private JButton sendButton;
    private JButton quitButton;
    private Client client;
    private JScrollPane scrollPane;

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

        passwordField = new JTextField(15);
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
                
                client.setUser(new User(usernameField.getText()));
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
        
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS)); 
        chatPanel.setBackground(Color.WHITE);
        scrollPane = new JScrollPane(chatPanel); 

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
    
    public void addMessage(Message message) {
    	
    	boolean isUser = message.getSender().equals(client.getUser());
    	boolean isAdmin = message.getSender().getName().equals("Admin");
    	
    	if(!isAdmin) {
    		JPanel messageContainer = new JPanel();
            messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.Y_AXIS));
            messageContainer.setOpaque(false);

            JLabel nameLabel = new JLabel(message.getSender().getName());
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            nameLabel.setForeground(Color.GRAY);
            nameLabel.setAlignmentX(isUser ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
            
            JLabel messageLabel = new JLabel(message.getContents());
            messageLabel.setOpaque(true);
            messageLabel.setBackground(isUser ? new Color(173, 216, 230) : new Color(220, 220, 220));
            messageLabel.setForeground(Color.BLACK);
            messageLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
            messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            messageLabel.setAlignmentX(isUser ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
           

            
            messageContainer.add(nameLabel);
            messageContainer.add(messageLabel);

            JPanel alignmentWrapper = new JPanel();
            alignmentWrapper.setLayout(new BoxLayout(alignmentWrapper, BoxLayout.X_AXIS));
            alignmentWrapper.setOpaque(false);
            if (isUser) {
                alignmentWrapper.add(Box.createHorizontalGlue());
                alignmentWrapper.add(messageContainer);
            } else {
                alignmentWrapper.add(messageContainer);
                alignmentWrapper.add(Box.createHorizontalGlue());
            }

            chatPanel.add(alignmentWrapper);
            chatPanel.revalidate();
            chatPanel.repaint();

            SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
    	}
    	else {
    		addBanner(message.getContents());
    	}
    }
    
    private void addBanner(String message) {
        JLabel updateLabel = new JLabel(message);
        updateLabel.setFont(new Font("Arial", Font.PLAIN, 12)); 
        updateLabel.setForeground(Color.GRAY); 
        updateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        chatPanel.add(updateLabel);
        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
    }
}