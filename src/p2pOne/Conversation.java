package p2pOne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Conversation {

	
	private GUIConversation guiConversation;
	private Socket socket;

	private ObjectOutputStream sOutput;
	private ObjectInputStream sInput;
	
	private String convID;
	
	private boolean listening;
	
	public Conversation(int portNr, String serverAddress, GUIConversation conversationWindow) {
		
		System.out.println("Conversation initiation started");
		try {
			socket = new Socket(serverAddress, portNr);
			
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			sInput = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.convID = createConvID(portNr, serverAddress);
		
		ConvListener convL = new ConvListener();
		
		System.out.println("Conversation initiation finished!");
	}
	
	public void sendMsg(String msg) {
		System.out.println("sendMSG");
		
		Message message = new Message(msg, convID, socket.getInetAddress());
		System.out.println("Created message " + message.toString());
		try {
			sOutput.writeObject(message);
			display(message);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String createConvID(int port, String ipAddress) {
		
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(port+ipAddress);
		
		return sb.toString();
	}
	
	private void display(Message msg) {
		
		guiConversation.append(msg.toString());
	}
	
	private void display(String msg) {
		
		guiConversation.append(msg);
	}
	
	class ConvListener extends Thread {
		
		public void run() {

			listening = true;

			while(listening) {

				try {
					Message msg = (Message) sInput.readObject();
					
					if(msg.getConvID().equals(convID));
					display(msg);
				}

				catch(IOException e) { display("Sever has closed the connection " +e); listening = false;}

				catch(ClassNotFoundException e2) {}

			}

		}
	}
}

//Testing to add a comment down here!
