import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;

public class Server extends Thread{

	private int sPort; // The server will be listening on this port
	private int peerID;
	private static int peerIndex = 1;		// index for peers
	private static ConcurrentHashMap<Integer,Socket> clientList  = new ConcurrentHashMap<Integer,Socket>(); 
	//list of clients connected to server
	
	Server(int sPort, int peerID) {
		this.sPort = sPort;
		this.peerID = peerID;
	}

	public void run() {
		System.out.println("The server is running.");
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(sPort);
			while (true) {
				new Handler(listener.accept(), peerID).start();
				System.out.println("Client " + peerIndex + " is connected!");
				peerIndex++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				listener.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * A handler thread class. Handlers are spawned from the listening loop and
	 * are responsible for dealing with a single client's requests.
	 */
	private static class Handler extends Thread {
		private Message message;
		private Socket connection;
		private ObjectInputStream in; 	// stream read from the socket
		private ObjectOutputStream out; // stream write to the socket
		
		private int no;					//peerID of the corresponding server connection

		public Handler(Socket connection, int peerID) {
			this.connection = connection;
			this.no = peerID;
		}

		public void run() {
			try {
				// initialize Input and Output streams
				out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();
				in = new ObjectInputStream(connection.getInputStream());
				// Create message
				message = new Message(in, out);
				
				
				try {
					// HandShake Message && Add to List
					HandShake(in.readObject());

					while (true) {

					}
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}
			} catch (IOException ioException) {
				System.out.println("Disconnect with Client " + peerIndex);
			} finally {
				// Close connections
				try {
					in.close();
					out.close();
					connection.close();
				} catch (IOException ioException) {
					System.out.println("Disconnect with Client " + peerIndex);
				}
			}
		}

		public void HandShake(Object obj) {
				// receive the message sent from the client
				HandShake_Message hand_msg = (HandShake_Message) obj;
				
				// Adding client to the Key Map
				clientList.put(hand_msg.peerID, connection);
				
				// show the message to the user
				System.out.println("Receive HandShake message ->" + hand_msg.header + " from Client " + hand_msg.peerID);
				
				// send MESSAGE back to the client
				hand_msg = new HandShake_Message(no);
				message.sendMessage(hand_msg);
				
		}

	}

}
