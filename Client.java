import java.net.*;

import ActualMessages.ActualMessage;
import ActualMessages.MessageHandler;
import ActualMessages.MessageUtil;
import fileHandlers.RemotePeerInfo;

import java.io.*;

public class Client extends Thread {
	// Communication Instance Variables
	private Socket requestSocket; // socket connect to the server
	private ObjectOutputStream out; // stream write to the socket
	private ObjectInputStream in; // stream read from the socket
	private Message mg; // message stream

	// Info about the Client Variables
	private RemotePeerInfo myInfo; // To get the file info for the configuration
									// file and Info about the Client
	private int peerServerID; // peerID of the Server that this client is
								// coneected to

	/**
	 * Note on handlers: handlers contains mappings from types to handler
	 * objects, e.g. handlers.put(1, new ChokeHandler(this)); handlers HashMap
	 * usage example: handlers.get(messageType).handleMessage(message, peer);
	 */
	// Integer: message type, MessageHandler: the message-handling
	// implementation
	// private static HashMap<Integer, MessageHandler> handlers = new
	// HashMap<Integer, MessageHandler>();
	// Socket: peer Socket, Boolean: 1 unchoked 0 choked
	// private HashMap<Socket, Boolean> unchokedPeers = new HashMap<Socket,
	// Boolean>();
	// Socket: peer Socket, Boolean: 1 interested 0 uninterested
	// private HashMap<Socket, Boolean> interestedPeers = new HashMap<Socket,
	// Boolean>();

	public Client(RemotePeerInfo p, int peerServerID) {
		this.peerServerID = peerServerID;
		this.myInfo = p;

	}

	public RemotePeerInfo getPeerInfo() {
		return this.myInfo;
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

			// Loading the Handlers
			HandlerCached.loadCache();
			MessageHandler.loadUnchoked();
			MessageHandler.setPeerInfo(myInfo);

			// SEND HANDSHAKE MESSAGE
			HandShake_Message hand_msg = new HandShake_Message(myInfo.getPeerId());
			mg.sendMessage(hand_msg);

			// RECIEVE HANDSHAKE BACK AND CHECK IF RIGHT MEESAGE
			mg.HandShake_check(in.readObject(), peerServerID);

			// Send Bitfiled Message with Pieces
			MessageHandler clonedHandler = (MessageHandler) HandlerCached.getHandler(5, myInfo);
			ActualMessage bitList = clonedHandler.creatingMessage();
			System.out.println("Bitfield sent (client): " + bitList.getPayloadField());
			mg.sendMessage(bitList);

			// Recive Back BitField and Determine Interested or Not
			bitList = (ActualMessage) in.readObject();
			// Adding bitset to list of bitlists
			clonedHandler.setPeerIdNeighboor(peerServerID);
			clonedHandler.addPeerBitSet(peerServerID, MessageUtil.convertToBitSet(bitList.getPayloadField()));
			// Get If Interested in Piece from server or not
			int type = clonedHandler.handleMessage(bitList, requestSocket);

			while (true) {
				
				//Send Interested or Not of the list of pieces recieved
				clonedHandler = (MessageHandler)
				HandlerCached.getHandler(type,myInfo);
				bitList = clonedHandler.creatingMessage();
				System.out.println("Interested(2)/Uninterested(3)(client): "+ bitList.getTypeField());
				mg.sendMessage(bitList);
				
				
				//Get Have Message
				// clonedHandler.setPeerIdNeighboor(peerServerID);
				// bitList = (ActualMessage) in.readObject();
				// System.out.println("Message recieved (client): " +
				// bitList.getTypeField());
				// clonedHandler = (MessageHandler)
				// HandlerCached.getHandler(bitList.getTypeField(),myInfo);
				// clonedHandler.handleMessage(bitList, requestSocket);
				//
				// //Send Request Message
				// System.out.println("************** REQUEST **************");
				// clonedHandler = (MessageHandler)
				// HandlerCached.getHandler(6,myInfo);
				// bitList = clonedHandler.creatingMessage();
				// System.out.println("Request (client): " +
				// bitList.getPayloadField().toString());
				// mg.sendMessage(bitList);
				//
				//
				// //Get Choke or Unchoke Message
				// clonedHandler.setPeerIdNeighboor(peerServerID);
				// bitList = (ActualMessage) in.readObject();
				// System.out.println("Message recieved (client): " +
				// bitList.getTypeField());
				// clonedHandler = (MessageHandler)
				// HandlerCached.getHandler(bitList.getTypeField(),myInfo);
				// clonedHandler.handleMessage(bitList, requestSocket);
				//
				//
				//
				// //Get Piece
				// bitList = (ActualMessage) in.readObject();
				// System.out.println("Message recieved (client): " +
				// bitList.getTypeField());
				// clonedHandler = (MessageHandler)
				// HandlerCached.getHandler(bitList.getTypeField(),myInfo);
				// clonedHandler.handleMessage(bitList, requestSocket);
			}
			
		} catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a server first.");
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found");
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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
}
