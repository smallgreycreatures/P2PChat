package p2pOne;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GUIConversation extends JFrame implements ActionListener {

	private JTextArea inMsgArea;
	private JScrollPane inMsgPane;
	
	private JTextArea outMsgArea;
	private JScrollPane outMsgPane;
	
	private String serverAddress;
	private int portNr;
	
	private JButton sendBtn;
	
	private String msg;

	private Conversation conv;
	public GUIConversation(int portNr, String serverAddress, String nick) {
		
		this.portNr = portNr;
		this.serverAddress = serverAddress;
		setLayout(new BorderLayout());
		
		JPanel norPan = addNorth();
		JPanel southPan = addSouth();
		
		add(norPan, BorderLayout.NORTH);
		add(southPan, BorderLayout.CENTER);
		
		conv = new Conversation(portNr, serverAddress, this, nick);
		addWindowListener(new CloseListener());
		setSize(400,400);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
	
	}
	
	private JPanel addNorth() {
		
		JPanel norPan = new JPanel();
		
		inMsgArea = new JTextArea(10,10);
		inMsgPane = new JScrollPane(inMsgArea);
		inMsgArea.setEditable(false);
		
		norPan.setLayout(new GridLayout(1,1));
		
		norPan.add(inMsgPane);
		return norPan;
	}
	
	private JPanel addSouth() {
		
		JPanel southPan = new JPanel();
		southPan.setLayout(new GridLayout(2,1));
		
		outMsgArea = new JTextArea();
		outMsgPane = new JScrollPane(outMsgArea);
		
		sendBtn = new JButton("Send");
		sendBtn.addActionListener(this);
		southPan.add(outMsgArea);
		southPan.add(sendBtn);
		
		return southPan;
		
	}
	
	public void append(String msg) {
		
		inMsgArea.append(msg);
	}
	
	public void actionPerformed(ActionEvent ave) {
		
	
		if(ave.getSource() == sendBtn) {
			System.out.println("uuuhm");
			sendMsg();
		}
	}
	
	public void sendMsg() {
		
		msg = outMsgArea.getText();
		System.out.println("before sendMSG");
		conv.sendMsg(msg);
	}
	
	public String getMsg() {
		
		return msg;
	}
	
	class CloseListener extends WindowAdapter {
		
		@Override
		public void windowClosing(WindowEvent e) {
			System.out.println("Cloosing!");
			setDefaultCloseOperation(HIDE_ON_CLOSE);
		}
	}
}
