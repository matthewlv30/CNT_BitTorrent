import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * This class is for containing a Peer's Client socket and Server socket
 * It is responsible for starting peer processes, including connecting to the existing network and then
 * waiting for other peers to connect to it
 */

public class Node {
	private static final int sPort = 8000; // The server will be listening on this port number
	Socket requestSocket;           //socket connect to the server
	private ObjectInputStream requestIn;	//stream read from the socket
	private ObjectOutputStream requestOut;    //stream write to the socket
	
	RemotePeerInfo myInfo;
	LinkedList<RemotePeerInfo> peersInfo;
	
	/**
	 * Construct a Node.
	 * @param _myInfo:	the information specific to this peer Node (e.g. ID, host name, etc.)
	 * @param _peersInfo: Array of the information specific to neighborhood peer Nodes
	 */
	public Node(RemotePeerInfo _myInfo, LinkedList<RemotePeerInfo> _peersInfo) {
		myInfo = _myInfo;
		peersInfo = _peersInfo;
	}
	
	/**
	 * Start peer process for peer Node.
	 * This involves starting up the server, connecting to the Nodes in the existing network
	 * Then the process waits for other peers to connect to its server socket.
	 * This also initiates the messaging-process after this process begins
	 */
	public void startPeerProcess() throws IOException {
		System.out.println("The server is running.");
        ServerSocket listener = new ServerSocket(sPort);
		
		// Client side: Have the client connect to servers before it
        for (int i = 0; i < peersInfo.size(); i++) {
        	if (peersInfo.get(i).getPeerId() != myInfo.getPeerId()) {
    			// create a socket to connect to the server
    			requestSocket = new Socket(peersInfo.get(i).getPeerAddress(), peersInfo.get(i).getPort());
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
}
