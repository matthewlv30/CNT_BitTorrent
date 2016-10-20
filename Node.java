
import java.io.IOException;
import java.util.HashMap;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
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
	
	/**
	 * Note on handlers: handlers contains mappings from types to handler objects, e.g. handlers.put(1, new ChokeHandler(this));
	 * handlers HashMap usage example:
	 * handlers.get(messageType).handleMessage(message, peer);
	 */
	private HashMap<Integer, MessageHandler> handlers; // Integer: message type, MessageHandler: the message-handling implementation
	private HashMap<Integer, Boolean> isUnchoked; // Integer: peerID, Boolean: 1 unchoked 0 choked
	private HashMap<Integer, Boolean> isInterested; // Integer: peerID, Boolean: 1 interested 0 uninterested
	
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
		
		handlers.put(1, new ChokeHandler());
		handlers.put(2, new UnchokeHandler());
		handlers.put(3, new InterestedHandler());
		handlers.put(4, new UninterestedHandler());
	}
	
	public RemotePeerInfo getInfo() {
		return myInfo;
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
	
	/**
	 * Note: The message handler classes below are implemented as inner classes so they can easily access the state of its Node
	 */
	
	/**
	 * The message handler classes are implemented as inner classes so they can easily access the state of its Node
	 * Class to handle choke message
	 */
	public class ChokeHandler implements MessageHandler {
		
		/**
		 * When you receive a choke message, set isUnchoked to false
		 * @param m:	this is the message received
		 * @param n:	this is the Node that is choking the current peer
		 */
		public void handleMessage(ActualMessage m, Node n) {
			isUnchoked.put(n.getInfo().getPeerId(), false);
		}
	}
	
	/**
	 * Class to handle unchoke message
	 */
	public class UnchokeHandler implements MessageHandler {
		
		/**
		 * When you receive an unchoke message, set isUnchoked to true
		 * @param m:	this is the message received
		 * @param n:	this is the Node that is choking the current peer
		 */
		public void handleMessage(ActualMessage m, Node n) {
			isUnchoked.put(n.getInfo().getPeerId(), true);
		}
	}
	
	/**
	 * Class to handle "interested" message
	 */
	public class InterestedHandler implements MessageHandler {
		
		/**
		 * When you receive an "interested" message, signify in HashMap that the peer is interested
		 * @param m:	this is the message received
		 * @param n:	this is the Node that is interested in the current peer
		 */
		public void handleMessage(ActualMessage m, Node n) {
			isInterested.put(n.getInfo().getPeerId(), true);
		}
	}
	
	/**
	 * Class to handle "uninterested" message
	 */
	public class UninterestedHandler implements MessageHandler {
		
		/**
		 * When you receive an "uninterested" message, signify in HashMap that the peer is not interested
		 * @param m:	this is the message received
		 * @param n:	this is the Node that is not interested in the current peer
		 */
		public void handleMessage(ActualMessage m, Node n) {
			isInterested.put(n.getInfo().getPeerId(), false);
		}
	}
}
