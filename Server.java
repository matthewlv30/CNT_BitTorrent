import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

import ActualMessages.ActualMessage;
import ActualMessages.MessageHandler;
import ActualMessages.MessageUtil;
import fileHandlers.RemotePeerInfo;
import java.util.HashMap;

import java.io.*;

public class Server extends Thread {

	private static int peerIndex = 1; // index for peers

	private RemotePeerInfo myInfo; // To get the file info for the configuration
									// file

	// list of clients connected to server this server
	private static ConcurrentHashMap<Integer, Socket> clientList = new ConcurrentHashMap<Integer, Socket>();

	// Socket: peer Socket, Boolean: 1 unchoked 0 choked
	// private HashMap<Socket, Boolean> unchokedPeers = new
	// HashMap<Socket,Boolean>();
	// private HashMap<Integer, Boolean> interestedPeers = new
	// HashMap<Integer,Boolean>();

	// private BitSet myBitfield;
	/**
	 * 
	 * @param sPort
	 *            port that the server is going to be listening from
	 * @param p
	 *            Object to get the Configiruation information
	 */
	Server(RemotePeerInfo p) {
		this.myInfo = p;
	}

	public void run() {
		System.out.println("The server is running.");
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(myInfo.getPort());
			while (true) {

				new Handler(listener.accept(), myInfo).start();
				System.out.println("Client " + peerIndex + " is connected!");
				peerIndex++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * A handler thread class. Handlers are spawned from the listening loop and
	 * are responsible for dealing with a single client's requests.
	 */
	public static class Handler extends Thread {
		private Message message;
		private Socket connection;
		private ObjectInputStream in; // stream read from the socket
		private ObjectOutputStream out; // stream write to the socket
		private MessageHandler clonedHandler;

		private RemotePeerInfo myServerInfo; // peerID of the corresponding
												// server connection
		// keep List of previous choked neighboors
		private ConcurrentHashMap<Integer, Boolean> isChoked = new ConcurrentHashMap<Integer, Boolean>();

		public Handler(Socket connection, RemotePeerInfo peerInfo) {
			this.connection = connection;
			this.myServerInfo = peerInfo;
		}

		public void run() {
			try {

				out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();
				in = new ObjectInputStream(connection.getInputStream());
				// Create message handler
				message = new Message(in, out);

				try {

					// Loading the Handlers
					HandlerCached.loadCache();
					MessageHandler.loadUnchoked();
					MessageHandler.setPeerInfo(myServerInfo);
					// HandShake Message && Add to List
					HandShake_Message hd = (HandShake_Message) in.readObject();
					clientList.put(hd.peerID, connection);
					message.HandShake(hd, myServerInfo.getPeerId());
					MessageHandler.setNeighborLists(hd.peerID, 0);

					// Recieve Bitfield Message with list of Pieces
					ActualMessage bitList = (ActualMessage) in.readObject();

					// Sending Servers bitlist back
					clonedHandler = (MessageHandler) HandlerCached.getHandler(bitList.getTypeField(), myServerInfo);
					// initialize Input and Output streams

					// Adding bitfield and setting PeerID of client neigtbor
					clonedHandler.setPeerIdNeighboor(hd.peerID);
					clonedHandler.addPeerBitSet(hd.peerID, MessageUtil.convertToBitSet(bitList.getPayloadField()));
					bitList = clonedHandler.creatingMessage();
					System.out.println("Bitfield sent(server): " + hd.peerID + bitList.getTypeField());
					message.sendMessage(bitList);

					// Recieve Interested or Not from Client
					bitList = (ActualMessage) in.readObject();
					System.out.println("Message Interested (server): " + hd.peerID + bitList.getTypeField());
					// If interested or not signified in the Interested
					// (HashMap)
					clonedHandler = (MessageHandler) HandlerCached.getHandler(bitList.getTypeField(), myServerInfo);
					clonedHandler.handleMessage(bitList, connection);

					// Send Have Message
					System.out.println("************** HAVE **************");
					clonedHandler.setPeerIdNeighboor(hd.peerID);
					clonedHandler = (MessageHandler) HandlerCached.getHandler(4, myServerInfo);
					bitList = clonedHandler.creatingMessage();
					System.out.println("Have (server): " + bitList.getPayloadField().toString());
					message.sendMessage(bitList);

					// Recieve Request
					bitList = (ActualMessage) in.readObject();
					System.out.println("Message recieved (server): " + bitList.getTypeField());
					clonedHandler = (MessageHandler) HandlerCached.getHandler(bitList.getTypeField(), myServerInfo);
					int type = clonedHandler.handleMessage(bitList, connection);
					
					boolean prevChoked;
					ConcurrentHashMap<Integer, Boolean> currentPref = MessageHandler.getPreferredNeighbors();
					//if isChoked is not null check the old one and change it and send choke or unchoke messsage
					if (isChoked.get(myServerInfo.getPeerId()) != null) {
						prevChoked = isChoked.get(myServerInfo.getPeerId()); // 0
						
						if (currentPref.get(hd.peerID) != prevChoked) {
							if (currentPref.get(hd.peerID) == false) {
								// TODO: send choke message
								isChoked.put(hd.peerID, false);
							} else {
								// TODO: send unchoke messsage
								isChoked.put(hd.peerID, true);
							}
						}
					}
					else {
						// is previous null then add the current
						isChoked.put(hd.peerID, currentPref.get(hd.peerID));
					}
																				
					// Send Piece Message
					System.out.println("************** PIECE **************");
					clonedHandler = (MessageHandler) HandlerCached.getHandler(type, myServerInfo);
					bitList = clonedHandler.creatingMessage();
					System.out.println("Piece (server): " + bitList.getPayloadField().toString());
					message.sendMessage(bitList);

					// while (true) {

					// }
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}
			} catch (IOException ioException) {
				System.out.println("Disconnect with Client " + peerIndex);
			} catch (Exception e) {

				e.printStackTrace();
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

		// Unchoked Changes

		// This indicates whether this peer has the complete file or not
		public boolean hasCompleteFile() {
			// TODO: Implement this
			return false;
		}

		public HashMap<Integer, Double> getNeighborByteCount() {
			return MessageHandler.getNeighborByteCount();
		}

		public void resetByteCount() {
			MessageHandler.resetByteCount();
		}

		// End of Unchoked Changes

	}
}
