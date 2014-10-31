package p2pOne;

import java.io.Serializable;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {

	private static final long serialVersionUID = 12947329283L;
	private String text;
	private String nickName;
	private String convID;
	
	private InetAddress toIPAddress;
	
	private SimpleDateFormat dateFormat;
	
	/**
	 * Creates a Message of the format "nickname: time&date: text and 
	 * set the class variables toIPAddress and conversationID
	 * @param text
	 * @param convID
	 * @param toIP
	 * @param nickName
	 */
	public Message(String text, String convID, InetAddress toIP, String nickName) {
		
		dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
		this.text = text;
		this.convID = convID;
		this.toIPAddress = toIP;
		this.nickName = nickName;

	}
	
	/**
	 * Creates the format of the text message "nickname: time/date: text"
	 * @param text
	 * @return nickname: timeAndDate: text
	 */
	private String addInformation(String text) {
		String timeAndDate = dateFormat.format(new Date());
		dateFormat.applyPattern("HH:mm:ss");
		
		return nickName +": "+ timeAndDate + ": " + text;
	}
	
	/**
	 * 
	 * @return the text of the message
	 */
	public String getText() {
		
		return text;
	}
	
	/**
	 * 
	 * @return the conversations id
	 */
	public String getConvID() {
		
		return convID;
	}
	
	/**
	 * 
	 * @return the IP-address to the reciever
	 */
	public InetAddress getToIPAddress() {
		
		return toIPAddress;
	}
	
	/**
	 * 
	 * @return the nickname of the sender
	 */
	public String getNickName() {
		
		return nickName;
	}
	
	/**
	 * 
	 * @return nickname: time/date: text + \n
	 */
	@Override
	public String toString() {
		
		return addInformation(text) + "\n";
	}
}
