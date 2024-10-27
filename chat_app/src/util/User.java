package util;

public class User {
	private String name;
	private int serialNumber;
	
	public User(String name) {
		this.name = name;
		this.serialNumber = (int)(Math.random() * 10000.0);
	}
	
	public User(String name, int serialNumber) {
		this.name = name;
		this.serialNumber = serialNumber;
	}
	
	public boolean equals(User other) {
		return other.getSerialNumber() == this.getSerialNumber();
	}
	

	public int getSerialNumber() {
		return serialNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String encode() {
		String serialString  = ""+serialNumber;
		
		String nameLength = String.format("%04d", name.length());
		String serialLength = String.format("%04d", serialString.length());
		
		return nameLength + name + serialLength + serialString;
	}
	
	public static User decode(String encodedString) {
		int nameLength = Integer.parseInt(encodedString.substring(0, 4));
		String name = encodedString.substring(4, 4 + nameLength);
		
		int serialStart = 4 + nameLength;
		int serialLength = Integer.parseInt(encodedString.substring(serialStart, serialStart + 4));
		String serialString = encodedString.substring(serialStart + 4, serialStart + 4 + serialLength);
		int serialNumber = Integer.parseInt(serialString);
		
		return new User(name, serialNumber);
	}

}
