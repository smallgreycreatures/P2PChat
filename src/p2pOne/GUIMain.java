package p2pOne;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUIMain extends JFrame {
	
	private JTextField nickField;
	private JTextField addressField;
	private JTextField portField;
	
	private JButton connectBtn;
	
	public GUIMain() {
		
		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan ,BoxLayout.Y_AXIS));
		nickField = new JTextField(10);
		addressField = new JTextField(10);
		portField = new JTextField(10);
		connectBtn = new JButton("Connect");
		connectBtn.addActionListener(new ConnectListener());
		
		
		pan.add(nickField); pan.add(addressField); pan.add(portField); pan.add(connectBtn);
		add(pan);
		
		addWindowListener(new CloseListener());
		setSize(300, 300);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
		Server server = new Server(2000);
		server.start();
	}
	
	class ConnectListener implements ActionListener {
		
		public void actionPerformed(ActionEvent ave) {
			boolean doing = true;
			int portNr = 0;
			
			
			try {
				portNr = Integer.parseInt(portField.getText());
				doing = false;
			}
			catch(NumberFormatException e) {
				
				System.out.println("A port number consists of a sequence of integers dumbass");
			}
			String serverAddress = addressField.getText();
			String nick = nickField.getText();
			
			if(doing == false) {
			
				GUIConversation convFrame = new GUIConversation(portNr, serverAddress, nick);
			}
		}
	}
	
	class CloseListener extends WindowAdapter {
		
		@Override
		public void windowClosing(WindowEvent e) {
			System.out.println("Cloosing!");
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
	}
	
	
	public static void main(String[]args) {
		
		new GUIMain();
	}
}
