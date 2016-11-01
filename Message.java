import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Message {

	// private ObjectInputStream in; // stream read from the socket
	private ObjectOutputStream out; // stream write to the socket

	
	Message(ObjectInputStream in, ObjectOutputStream out) {
		// this.in = in;
		this.out = out;
	}

	// send a message to the output stream
	public void sendMessage(Object msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Handhsake Method one for the Server side and the other for the Client side
	 * Both of them send an object message thtough the Socket
	 */
	public void HandShake(HandShake_Message obj, int no) {
		
		// show the message to the user
		System.out.println("Receive HandShake message ->" + obj.header + " from Client " + obj.peerID);

		// send MESSAGE back to the client
		obj = new HandShake_Message(no);
		sendMessage(obj);

	}
	
	public void HandShake_check(Object obj, int peerServerID) {
		// receive the message sent from the client
		HandShake_Message hand_msg = (HandShake_Message) obj;
		
		final String header = "P2PFILESHARINGPROJ";
		//Cheking if handshake is the Expected One
		peerServerID = 1002;
		if (hand_msg.peerID == peerServerID && header.equals(hand_msg.header)) {
			// show the message to the user
			System.out.println("Receive HandShake message -> " + hand_msg.header + " from Server " + hand_msg.peerID);
		}
	}
	
}
