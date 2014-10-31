package p2pOne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {

	private ServerSocket serverSocket;
	private int portNr;
	private static int userId;
	private ArrayList<ClientThread> clientList;
	
	private boolean running;
	
	/**
	 * Initiates the ServerSocket and the ArrayList for threads
	 * @param port
	 */
	public Server(int port) {
		
		this.portNr = port;
		
		try { serverSocket = new ServerSocket(portNr); }
		catch (IOException e) { display("IOException while setting up Server Socket" + e.getMessage()); }

		clientList = new ArrayList<ClientThread>();
	}
	
	/**
	 * Listens to incoming connections and create new Threads of them
	 */
	public void run() {
		
		try {
			
			running = true;
			
			while(running) {
				
				System.out.println("Server up! waiting for incoming connections");
				
				Socket socket = serverSocket.accept();
				
				if(!running) break;
				
				ClientThread client = new ClientThread(socket);
				
				clientList.add(client);
				
				client.start();
				userId++;
			} 
			System.out.println("Closing server!");
			

		} catch (IOException e) { display("IOException while recieving new Socket connection " + e.getMessage()); }
		
	}
	
	/**
	 * Broadcasts the message to the client in the clientList with matching IP
	 * @param msg
	 */
	public synchronized void broadcast(Message msg) {
		
		display("Time to broadcast");
		
		for(ClientThread clientThread: clientList) {
			
			if(clientThread.socket.getInetAddress().equals(msg.getToIPAddress())) {
				
				clientThread.sendMsg(msg);
				display("it did broadcast?");
			}
		}
	}
	
	/**
	 * Display text
	 * @param text
	 */
	public synchronized void display(String text) {
		
		System.out.println(text);
	}
	
	/**
	 * Class that extends Thread and represent a new instance of a 
	 * connection to a client
	 * @author Frans
	 *
	 */
	 
	class ClientThread extends Thread {
		
		private Socket socket;
		private ObjectInputStream inputStream;
		private ObjectOutputStream outputStream;
		private int id;
		private Message msg;
		private String username;
				
		/**
		 * Initiates the connection by setting up streams and give the
		 * connection an id 
		 * @param socket
		 */
		public ClientThread(Socket socket) {
			display("Initiating ClientThread" + id);
			
			this.socket = socket;
			this.id = userId;
			this.username = socket.getLocalPort() + "";
			
			try {
				inputStream = new ObjectInputStream(socket.getInputStream());
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				display("Stream for ClientThread" + id + " up!");
				
			} catch (IOException e) { display("IOException while setting up clientThread streams " + e.getMessage()); }
		}
		
		/**
		 * Listens to input from clients and prepare the message for
		 * broadcasting
		 */
		public void run() {
			display("ClientThread" + id + " running");
			
			while(true) {
				
				try {
					display("ClientThread" + id + " get msg");
					msg = (Message) inputStream.readObject();
					//display(msg.toString());
				}
				catch(IOException e) { display("IOException while reading msg for ClientThread" + e.getMessage()); }
				catch(ClassNotFoundException e) { display("ClassNotFoundException while reading msg for ClientThread" + e.getMessage()); }
				
				display("Time to broadcast for clientThread" +id);
				broadcast(msg);
			}
		}
		
		/**
		 * Sends the message to the connected client
		 * @param msg
		 */
		public void sendMsg(Message msg) {
			
			try {
				outputStream.writeObject(msg);
			} catch (IOException e) { display("IOException while sending Message " + e.getMessage()); }
		}	
	}
}
