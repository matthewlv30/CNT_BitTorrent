import java.net.*;
import java.util.BitSet;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;

public class Server extends Thread{

	private int peerID;					// The peerID for the specific Node
	private static int peerIndex = 1;	// index for peers
	private RemotePeerInfo myInfo;		// To get the file info for the configuration file
	private BitSet myBitfield;
	private static ConcurrentHashMap<Integer,Socket> clientList  = new ConcurrentHashMap<Integer,Socket>(); 
	//list of clients connected to server this server
	/**
	 * Note on handlers: handlers contains mappings from types to handler objects, e.g. handlers.put(1, new ChokeHandler(this));
	 * handlers HashMap usage example:
	 * handlers.get(messageType).handleMessage(message, peer);
	 */
	private static HashMap<Integer, MessageHandler> handlers = new HashMap<Integer, MessageHandler>(); // Integer: message type, MessageHandler: the message-handling implementation
	private HashMap<Socket, Boolean> unchokedPeers = new HashMap<Socket, Boolean>(); // Socket: peer Socket, Boolean: 1 unchoked 0 choked
	private HashMap<Socket, Boolean> interestedPeers = new HashMap<Socket, Boolean>(); // Socket: peer Socket, Boolean: 1 interested 0 uninterested
	
	/**
	 * 
	 * @param sPort port that the server is going to be listening from
	 * @param peerID the peerID for the specific Node or Machine
	 * @param p	Object to get the Configiruation information
	 */
	Server(int peerID, RemotePeerInfo p) {				
		this.peerID = peerID;
		this.myInfo = p;
		
		int bitFieldSize = (int) Math.ceil((double)myInfo.getFileSize()/myInfo.getPieceSize());
		myBitfield = new BitSet(bitFieldSize);
		
		// Each index of a bit field indicates whether or not the piece is with the peer.
		if (myInfo.hasFile) {
			myBitfield.set(0, bitFieldSize, true);
		} else {
			myBitfield.set(0, bitFieldSize, false);
		}
		
		handlers.put(1, new ChokeHandler());
		handlers.put(2, new UnchokeHandler());
		handlers.put(3, new InterestedHandler());
		handlers.put(4, new UninterestedHandler());
		handlers.put(5, new HaveHandler());
		handlers.put(6, new BitfieldHandler());
		handlers.put(7, new RequestHandler());
		handlers.put(8, new PieceHandler());
	}

	public void run() {
		System.out.println("The server is running.");
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(myInfo.getPort());
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
						ActualMessage m = (ActualMessage) in.readObject();
						handlers.get(m.getTypeField()).handleMessage(m, connection);

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
	
	/**
	 * The message handler classes are implemented as inner classes so they can easily access the state of its Server
	 */
	
	/**
	 * Class to handle choke message
	 */
	public class ChokeHandler implements MessageHandler {
		
		/**
		 * When you receive a choke message, set isUnchoked to false
		 * @param m:	this is the message received
		 * @param n:	this is the Node that is choking the current peer
		 */
		public void handleMessage(ActualMessage m, Socket n) {
			unchokedPeers.put(n, false);
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
		public void handleMessage(ActualMessage m, Socket n) {
			unchokedPeers.put(n, true);
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
		public void handleMessage(ActualMessage m, Socket n) {
			interestedPeers.put(n, true);
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
		public void handleMessage(ActualMessage m, Socket n) {
			interestedPeers.put(n, false);
		}
	}
	
	/**
	 * Class to handle "have" message
	 */
	public class HaveHandler implements MessageHandler {
		
		/**
		 * When you receive a "have" message, determine whether or not to send an interested message.
		 * The payload of the "have" message contains a piece index field
		 * @param m:	this is the message received
		 * @param n:	this is the Node sending the message
		 */
		public void handleMessage(ActualMessage m, Socket n) {
			int index;
			byte payload[] = m.getPayloadField();
			if (payload.length != 4) {
				throw new RuntimeException("Have message does not have proper payload size");
			} else {
				index = MessageUtil.convertBytesToInt(payload);
				
				if (myBitfield.get(index) == true) {
					// TODO: send not interested message to Peer Node n
				} else {
					// TODO: send interested message to Peer Node n
				}
			}
		}
	}
	
	/**
	 * Class to handle "bitfield" message
	 */
	public class BitfieldHandler implements MessageHandler {
		
		/**
		 * When you receive a "bitfield" message, determine whether or not to send an interested message
		 * @param m:	this is the message received
		 * @param n:	this is the Node sending the message
		 */
		public void handleMessage(ActualMessage m, Socket n) {
			byte payload[] = m.getPayloadField();
		
			// First byte of the payload corresponds to piece indices 0-7, second bit is 8-15 etc.
			for (int i = 0; i < payload.length; i++) {
				for (int j = 0; j < 8; j++) {
					// Get the bit j at this byte i. If this bit is true AND
					int bit = (payload[i] >> j) & 1;
					
					// If the bit is 1, peer node n contains that piece
					// If that bit index for my bitfield is false, then send interested message
					// Note: special operations are used on myBitField to obtain proper "byte" location
					if ((bit == 1) && (myBitfield.get(j + (i*8)) == false)) {
						// TODO: send "interested" message to peer node n
						break;
					}
				}
			}
			// send "uninterested" message
		}
	}
	
	/**
	 * Class to handle "request" message
	 */
	public class RequestHandler implements MessageHandler {
		
		/**
		 * When you receive a "request" message, send a message with the requested piece
		 * @param m:	this is the message received
		 * @param n:	this is the Node sending the message
		 */
		public void handleMessage(ActualMessage m, Socket n) {
			byte payload[] = m.getPayloadField();
			
			if (payload.length != 4) {
				throw new RuntimeException("Request message does not have proper payload size");
			} else {
				int index = MessageUtil.convertBytesToInt(payload);
//				int otherPeerID = n.getInfo().getPeerId();
				
				// Check if peer sending msg is unchoked and that my bitfield has the piece at this index
				if (clientList.contains(n) && (myBitfield.get(index) == true)) {
					// TODO: send a "piece" message to n
					// TODO: work on retrieving "piece" bytes from a file
				}
			}
		}
	}
	
	/**
	 * Class to handle "piece" message
	 */
	public class PieceHandler implements MessageHandler {
		
		/**
		 * When you receive a "piece" message, download the piece
		 * @param m:	this is the message received
		 * @param n:	this is the Node sending the message
		 */
		public void handleMessage(ActualMessage m, Socket n) {
			byte payload[] = m.getPayloadField();
			
			// Retrieve the 4-byte index
			byte indexTemp[] = new byte[4];
			
			for (int i = 0; i < 4; i++) {
				indexTemp[i] = payload[i];
			}
			
			//int index = MessageUtil.convertBytesToInt(indexTemp);
			
			// Retrieve the rest of the payload
			//byte pieceContent[] = new byte[myInfo.getPieceSize()];
			// TODO: store rest of payload into pieceContent
		}
	}

}
