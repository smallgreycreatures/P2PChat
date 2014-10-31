package p2pOne;

import java.io.Serializable;
import java.net.InetAddress;

public class Message implements Serializable {

	private static final long serialVersionUID = 12947329283L;
	private String text;
	private String convID;
	
	private InetAddress myIPAddress;
	private InetAddress toIPAddress;
	
	public Message(String text, String convID, InetAddress toIP) {
		
		this.text = text;
		this.convID = convID;
		//this.myIPAddress = myIP;
		this.toIPAddress = toIP;
	}
	
	public String getText() {
		
		return text;
	}
	
	public String getConvID() {
		
		return convID;
	}
	
	public InetAddress getMyIPAddress() {
		
		return myIPAddress;
	}
	
	public InetAddress getToIPAddress() {
		
		return toIPAddress;
	}
	
	@Override
	public String toString() {
		
		return text +"\n";
	}
}
