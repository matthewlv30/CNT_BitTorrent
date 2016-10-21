import java.net.*;
import java.util.BitSet;
import java.io.*;

public class Client extends Thread{
	//Communication Instance Variables
	private Socket requestSocket; 	// socket connect to the server
	private ObjectOutputStream out; // stream write to the socket
	private ObjectInputStream in; 	// stream read from the socket
	private Message mg;				// message stream	
	
	//Info about the Client Variables 
	private RemotePeerInfo myInfo;		// To get the file info for the configuration file and Info about the Client
	private int peerServerID;       	// peerID of the Server that this client is coneected to 
	
	//Messages Instances Variables
	@SuppressWarnings("unused")
	private BitSet myBitfield;
	
	public Client(RemotePeerInfo p, int peerServerID) {
		this.peerServerID = peerServerID;
		this.myInfo = p;
	}

	public void run() {
		try {
			// create a socket to connect to the server
			requestSocket = new Socket(myInfo.getPeerAddress(), myInfo.getPort());

			// initialize inputStream and outputStream and a Message object
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			mg = new Message(in, out);
			
			// SEND HANDSHAKE MESSAGE
			System.out.println("************** Starting Handshake **************");
			HandShake_Message hand_msg = new HandShake_Message(myInfo.getPeerId());
			mg.sendMessage(hand_msg);

			// RECIEVE HANDSHAKE BACK AND CHECK IF RIGHT MEESAGE
			HandShake_check(in.readObject());

			
			// Send Bitfiled Message with Pieces
			System.out.println("************** BITFIELD **************");
			//message_type(5);
			while (true) {
				
			}
		} catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a server first.");
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found");
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// Close connections
			try {
				in.close();
				out.close();
				requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public void HandShake_check(Object obj) {
		// receive the message sent from the client
		HandShake_Message hand_msg = (HandShake_Message) obj;
		
		final String header = "P2PFILESHARINGPROJ";
		//Cheking if handshake is the Expected One
		if (hand_msg.peerID == peerServerID && header.equals(hand_msg.header)) {
			// show the message to the user
			System.out.println("Receive HandShake message -> " + hand_msg.header + " from Server " + hand_msg.peerID);
		}
	}
}
