package p2pOne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class Server extends Thread {

	private ServerSocket serverSocket;
	private int portNr;
	private static int userId;
	private ArrayList<ClientThread> clientList;
	private InetAddress localServerAddress;
	private ArrayList<InetAddress> inetList;
	private boolean running;

	/**
	 * Initiates the ServerSocket and the ArrayList for threads
	 * @param port
	 */
	public Server(int port) {

		this.portNr = port;
		try {
			this.localServerAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try { serverSocket = new ServerSocket(portNr); }
		catch (IOException e) { display("IOException while setting up Server Socket" + e.getMessage()); }

		clientList = new ArrayList<ClientThread>();
		inetList = new ArrayList<InetAddress>();

		fillInetList();

	}

	private void fillInetList() {
		Enumeration<NetworkInterface> nets;
		try {
			nets = NetworkInterface.getNetworkInterfaces();

			for(NetworkInterface netInt: Collections.list(nets)) {
				for(InetAddress iAdd: Collections.list(netInt.getInetAddresses())) {
					inetList.add(iAdd);
					display(iAdd.getHostAddress() + " added to inetList");

				}
			}
		}
		catch(SocketException e) { display("Socket exception while adding local network addresses to list " + e.getMessage()); }
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
				System.out.println(socket.getInetAddress().getHostAddress() + " != " + serverSocket.getInetAddress().getHostAddress() + " == " + localServerAddress.getHostAddress());
				if(!socket.getInetAddress().getHostAddress().equals("127.0.0.1")) {
					System.out.println("Opening window :oooo");
					String address = socket.getInetAddress().getHostAddress();
					System.out.println(address + " " + socket.getPort());					
					StartConversation start = new StartConversation(address, 2000, "name");
					start.start();
					display("ujujuj");
				}



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

			for(InetAddress iAdd: inetList) {
				display(clientThread.socket.getInetAddress().getHostAddress() + " =? " + msg.getToIPAddress().getHostAddress() + " == " + iAdd.getHostAddress());

				if(iAdd.equals(msg.getToIPAddress())) {

					clientThread.sendMsg(msg);
					display("it did broadcast?");
				}
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

	class StartConversation extends Thread {

		private String address;
		private int port;
		private String nickName;
		public StartConversation(String address, int port, String nickName) {
			this.address = address;
			this.port = port;
			this.nickName = nickName;
		}

		public void run() {
			display("Start thread running");
			GUIConversation guiConv = new GUIConversation(port, address, nickName);
			display("Start thread done");
		}
	}
}
