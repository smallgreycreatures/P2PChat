package p2pOne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Conversation {

	private int portNr;
	private String serverAddress;

	private GUIConversation guiConversation;

	private Socket socketOut;
	private Socket socketLocal;

	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	private ObjectOutputStream localOutputStream;
	private ObjectInputStream localInputStream;

	private String convID;

	private boolean listeningRemote;
	private boolean listeningLocal;

	private String myNickName;

	/**
	 * Initiates the conversation by creating to Sockets that connects 
	 * to the local server and remote server. It then creates Streams 
	 * so it can communicate. It then creates to Threads that listens
	 * to the local and remote ServerSockets
	 * 
	 * @param portNr
	 * @param serverAddress
	 * @param conversationWindow
	 */
	public Conversation(int portNr, String serverAddress, GUIConversation conversationWindow, String nick) {

		this.portNr = portNr;
		this.serverAddress = serverAddress;
		this.guiConversation = conversationWindow;
		this.myNickName = nick;

		System.out.println("Conversation initiation started");
		try {
			socketOut = new Socket(serverAddress, portNr);
			socketLocal = new Socket("localhost", 2000);
			System.out.println("Sockets up");
			outputStream = new ObjectOutputStream(socketOut.getOutputStream());
			inputStream = new ObjectInputStream(socketOut.getInputStream());
			System.out.println("remote Streams up");
			localOutputStream = new ObjectOutputStream(socketLocal.getOutputStream());
			localInputStream = new ObjectInputStream(socketLocal.getInputStream());

			System.out.println("local streams up");
		} catch (UnknownHostException e) {
			display("Unknown host exception while setting up sockets/streams " + e.getMessage());
		} catch (IOException e) {
			display("IOException while setting up streams " + e.getMessage());
		}

		this.convID = createConvID(portNr, serverAddress);

		RemoteServerListener remoteServer = new RemoteServerListener();
		remoteServer.start();

		LocalServerListener localServer = new LocalServerListener();
		localServer.start();

		System.out.println("Conversation initiation finished!");
	}


	/**
	 * Sends a message through an outputstream connected to remote serversocket
	 * @param msg
	 */
	public void sendMsg(String msg) {
		System.out.println("sendMSG");

		Message message = new Message(msg, convID, socketOut.getInetAddress(), myNickName);
		System.out.println("Created message " + message.toString());
		try {
			outputStream.writeObject(message);
			//display(message);

		} catch (IOException e) { display("IOException while sending message " + e.getMessage()); }
	}

	/**
	 * Method that gives every conversation a uniqe id based on port and ipAddress
	 * @param port
	 * @param ipAddress
	 * @return uniqeConversationID
	 */
	private String createConvID(int port, String ipAddress) {


		StringBuilder sb = new StringBuilder();

		sb.append(port+ipAddress);

		return sb.toString();
	}

	/**
	 * Displays a Message in the GUI
	 * @param msg
	 */
	private synchronized void display(Message msg) {
		System.out.println(msg.toString());
		guiConversation.append(msg.toString());
	}

	/**
	 * Displays a String in the GUI.
	 * @param msg
	 */
	private synchronized void display(String msg) {

		//guiConversation.append(msg);
		System.out.println(msg);
	}

	public void exitChat() throws IOException {
		sendMsg("User disconnected");
		
		System.out.println("exitChat");

		Message message = new Message("^EXIT^CHAT^", convID, socketOut.getInetAddress(), myNickName);
		System.out.println("Created message " + message.toString());

		outputStream.writeObject(message);
		localOutputStream.writeObject(message);

		System.out.println("Exit chat done");

	}
	public synchronized void disconnect() throws IOException{
		if(outputStream != null) {
			outputStream.close();
		}
		if(inputStream != null) {
			inputStream.close();
		}
		if(localOutputStream != null) {
			localOutputStream.close();
		}
		if(localInputStream != null) {
			localInputStream.close();
		}

		if(socketOut != null) {
			socketOut.close();
		}

		if(socketLocal != null) {
			socketLocal.close();
		}
		System.out.println("Disconnected!");
	}
	/**
	 * Thread that is listening for data sent by remote serversocket 
	 * @author Frans
	 *
	 */
	class RemoteServerListener extends Thread {

		public void run() {

			listeningRemote = true;

			while(listeningRemote) {

				try {
					Message msg = (Message) inputStream.readObject();

					//if(msg.getConvID().equals(convID))

					if(msg.getText().equals("^EXIT^CHAT^")) {
						disconnect();
					}
					else {
						display(msg);
					}
				}

				catch(IOException e) { display("Sever has closed the connection " +e); listeningRemote = false;}

				catch(ClassNotFoundException e2) {}

			}
		}
	}

	/**
	 * Thread that is listening for data that is sent by local serversocket
	 * @author Frans
	 *
	 */
	class LocalServerListener  extends Thread {

		public void run() {

			listeningLocal = true;

			while(listeningLocal) {

				try {

					System.out.println("Running!");
					Message msg = (Message) localInputStream.readObject();

					//if(msg.getConvID().equals(convID))

					if(msg.getText().equals("^EXIT^CHAT^")) {
						disconnect();
					}
					else {
						display(msg);
					}

				}

				catch(IOException e) { display("Sever has closed the connection " +e); listeningLocal = false;}

				catch(ClassNotFoundException e2) {}

			}
		}
	}
}