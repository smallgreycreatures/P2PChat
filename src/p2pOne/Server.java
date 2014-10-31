package p2pOne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Server extends Thread {

	private ServerSocket serverSocket;
	private int portNr;
	private static int userId;
	private ArrayList<ClientThread> clientList;
	
	private boolean running;
	
	public Server(int port) {
		
		this.portNr = port;
		clientList = new ArrayList<ClientThread>();
	}
	
	public void run() {
		
		try {
			serverSocket = new ServerSocket(portNr);
			
			running = true;
			
			while(running) {
				
				System.out.println("Server up! waiting for incoming connections");
				
				Socket socket = serverSocket.accept();
				
				if(!running) break;
				
				ClientThread client = new ClientThread(socket);
				
				clientList.add(client);
				
				client.start();
				
			} 
			System.out.println("Closing server!");
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public synchronized void broadcast(Message msg) {
		
		display("Time to broadcast");
		
		for(ClientThread clientThread: clientList) {
			
			if(clientThread.socket.getInetAddress() == msg.getToIPAddress()) {
				
				clientThread.sendMsg(msg);
				display("it did broadcast?");
			}
		}
	}
	
	public synchronized void display(String text) {
		
		System.out.println(text);
	}
	class ClientThread extends Thread {
		
		private Socket socket;
		private ObjectInputStream inputStream;
		private ObjectOutputStream outputStream;
		private int id;
		private Message msg;
		private String username;
		
		private SimpleDateFormat dateFormat;
		
		public ClientThread(Socket socket) {
			display("Initiating ClientThread" + id);
			
			this.socket = socket;
			this.id = userId;
			this.username = socket.getLocalPort() + "";
			dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
			
			try {
				inputStream = new ObjectInputStream(socket.getInputStream());
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				display("Stream for ClientThread" + id + " up!");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				display("IOException while setting up clientThread streams " + e.getMessage());
			}
		}
		
		public void run() {
			display("ClientThread" + id + " running");
			while(true) {
				
				try {
					display("ClientThread" + id + " get msg");
					msg = (Message) inputStream.readObject();
					display(msg.toString());
				}
				catch(IOException e) { display("IOException while reading msg for ClientThread" + e.getMessage()); }
				catch(ClassNotFoundException e) { display("ClassNotFoundException while reading msg for ClientThread" + e.getMessage()); }
				
				display("Time to broadcast for clientThread" +id);
				broadcast(msg);
			}
		}
		
		public void sendMsg(Message msg) {
			
			try {
				outputStream.writeObject(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public String addDate(Message msg) {
			String timeAndDate = dateFormat.format(new Date());
			dateFormat.applyPattern("HH:mm:ss");
			
			return timeAndDate + ":" + msg.getText() + "\n";
		}
	}
	
}
