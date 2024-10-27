package util;

public class Message {
	private String contents;
	private User sender;
	
	public Message(String contents, User sender) {
		this.contents = contents;
		this.sender = sender;
	}

	public String getContents() {
		return contents;
	}

	public User getSender() {
		return sender;
	}
	
	public String encode() {
		String contentsLength = String.format("%04d", contents.length());
		String senderLength = String.format("%04d", sender.encode().length());
		
		return contentsLength + contents + senderLength + sender.encode();
	}
	
	public static Message decode(String encodedString) {
		int contentsLength = Integer.parseInt(encodedString.substring(0, 4));
		String contents = encodedString.substring(4, 4 + contentsLength);
		
		int userStart = 4 + contentsLength;
		int userLength = Integer.parseInt(encodedString.substring(userStart, userStart + 4));
		String userString = encodedString.substring(userStart + 4, userStart + 4 + userLength);
		User user = User.decode(userString);
		
		return new Message(contents, user);
	}
}
