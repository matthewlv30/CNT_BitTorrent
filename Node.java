import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class is for containing a Peer's Client socket and Server socket
 * It is responsible for starting peer processes, including connecting to the existing network and then
 * waiting for other peers to connect to it
 * 
 */

public class Node {
	private static final int sPort = 8000; // The server will be listening on this port number
	Socket requestSocket;           //socket connect to the server
	PeerInfo myInfo;
	PeerInfo[] peersInfo;
	
	public Node(PeerInfo _myInfo, PeerInfo[] _peersInfo) {
		myInfo = _myInfo;
		peersInfo = _peersInfo;
	}
	
	// This starts the peer process, including starting up the server
	public void startPeerProcess() throws IOException {
		System.out.println("The server is running.");
        ServerSocket listener = new ServerSocket(sPort);
		
		// Client side: Have the client connect to servers before it
        for (int i = 0; i < peersInfo.length; i++) {
        	/// TODO: properly implement PeerInfo.getID()
        	if (peersInfo[i].getID() != myInfo.getID()) {
        		// Get IP address and port of this peer
        		// connect to this peer
        	} else {
        		break;
        	}
        }
		
        // Server Side
        try {
            while(true) {
            	 // Listen for other clients trying to make connections
            }
        } finally {
            listener.close();
        }
	}
	
	//send a message to the output stream
	public void sendMessage(String msg)	{
		try	{
			out.writeObject(msg);
			out.flush();
			System.out.println("Send message: " + msg + " to Client " + no);
		}
		catch(IOException ioException)	{
			ioException.printStackTrace();
		}
	}
	
	/**
 	* A handler thread class.  Handlers are spawned from the listening
 	* loop and are responsible for dealing with a single client's requests.
 	*/
	private static class ServerHandler extends Thread {
    	private String message;    //message received from the client
    	private String MESSAGE;    //uppercase message send to the client
    	private Socket connection;
    	private ObjectInputStream in;	//stream read from the socket
    	private ObjectOutputStream out;    //stream write to the socket
    	private int no;		//The index number of the client

    	public ServerHandler(Socket connection, int no) {
        	this.connection = connection;
    		this.no = no;
    	}

	    public void run() {
			try {
				//initialize Input and Output streams
				out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();
				in = new ObjectInputStream(connection.getInputStream());
				try {
					while(true)	{
						//receive the message sent from the client
						message = (String)in.readObject();
						//show the message to the user
						System.out.println("Receive message: " + message + " from client " + no);
						//Capitalize all letters in the message
						MESSAGE = message.toUpperCase();
						//send MESSAGE back to the client
						sendMessage(MESSAGE);
					}
				}
				catch(ClassNotFoundException classnot)	{
					System.err.println("Data received in unknown format");
				}
			}
			catch(IOException ioException)	{
				System.out.println("Disconnect with Client " + no);
			}
			finally	{
				//Close connections
				try{
					in.close();
					out.close();
					connection.close();
				}
				catch(IOException ioException){
					System.out.println("Disconnect with Client " + no);
				}
			}
	    }
	}
}
