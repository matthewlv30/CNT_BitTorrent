import java.net.*;
import java.io.*;

public class Client {
	private Socket requestSocket; // socket connect to the server
	private ObjectOutputStream out; // stream write to the socket
	private ObjectInputStream in; // stream read from the socket
	private int peerID;
	// private int peerIndex = 1;
	private Message mg; // message stream

	public Client(int peerID) {
		this.peerID = peerID;
	}

	void run() {
		try {
			// create a socket to connect to the server
			requestSocket = new Socket("localhost", 8000);
			System.out.println("Connected to localhost");

			// initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());

			// get Input from standard input
			// BufferedReader bufferedReader = new BufferedReader(new
			// InputStreamReader(System.in));
			// read a sentence from the file input
			// peerID = Integer.parseInt(bufferedReader.readLine());

			// SEND HANDSHAKE MESSAGE
			System.out.println("************** Starting Handshake **************");
			mg = new Message(in, out);
			HandShake_Message hand_msg = new HandShake_Message(peerID);
			mg.sendMessage(hand_msg);

			// RECIEVE HANDSHAKE BACK
			HandShake_check(in.readObject());

			while (true) {
				//System.out.println("************** BITFIELD **************");
				//message_type(5);
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

	public int getPeerID() {
		return peerID;
	}

	public void HandShake_check(Object obj) {
		// receive the message sent from the client
		HandShake_Message hand_msg = (HandShake_Message) obj;
		
		int exPeerID = 1002; // To be removed
		
		final String header = "P2PFILESHARINGPROJ";
		//Cheking if handshake is the Expected One
		if (hand_msg.peerID == exPeerID && header.equals(hand_msg.header)) {
			// show the message to the user
			System.out.println("Receive HandShake message -> " + hand_msg.header + " from Server " + hand_msg.peerID);
		}
	}


	// main method
	public static void main(String args[]) {
		int peerID = 1001;
		Client client = new Client(peerID);
		client.run();
	}

}
