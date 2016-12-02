import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

import ActualMessages.ActualMessage;
import ActualMessages.MessageHandler;
import ActualMessages.MessageUtil;
import fileHandlers.CommonProperties;
import fileHandlers.RemotePeerInfo;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import java.io.*;

public class Server extends Thread {
	// Unchoked changes
	private ConcurrentHashMap<Socket,Boolean> preferredNeighbors = new ConcurrentHashMap<Socket,Boolean>(); // The neighbors that we can receive piece requests from
	private Socket optimisticallyUnchoked; // Neighbor that we can receive pice requests from
	private HashMap<Socket, Double> neighborByteCount = new HashMap<Socket, Double>(); // Maps peerID to how many bytes have been downloaded from them
	// End of Unchoked Changes

	private static int peerIndex = 1; // index for peers

	private RemotePeerInfo myInfo; // To get the file info for the configuration file

	// list of clients connected to server this server
	private static ConcurrentHashMap<Integer, Socket> clientList = new ConcurrentHashMap<Integer, Socket>();

	// Socket: peer Socket, Boolean: 1 unchoked 0 choked
	 private HashMap<Socket, Boolean> unchokedPeers = new HashMap<Socket,Boolean>();
	 private HashMap<Socket, Boolean> interestedPeers = new HashMap<Socket,Boolean>();
	
	private BitSet myBitfield;
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
		} finally {
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Unchoked Changes
	
	public void resetByteCount() {
		neighborByteCount.clear();
	}
	
	public boolean isPeerInterested(Socket peer) {
		if (interestedPeers.containsKey(peer)) {
			return true;
		} else {
			return false;
		}
	}
	
	// This indicates whether this peer has the complete file or not
	public boolean hasCompleteFile() {
		// TODO: Implement this
		return false;
	}
	
	public Map<Socket, Double> getNeighborByteCount() {
		return neighborByteCount;
	}
	
	public ConcurrentHashMap<Socket,Boolean> getPreferredNeighbors() {
		return preferredNeighbors;
	}
	
	public HashMap<Socket, Boolean> getInterestedPeers() {
		return interestedPeers;
	}
	
	public void setOptimisticallyUnchoked(Socket p) {
		// send choke message to old optimisticallyUnchoked if it's not in preferredNeighbors
		// if it's in preferredNeighbors, then dont do anything but set
		// else, set and send unchoke message
		
		if (!preferredNeighbors.containsKey(p)) {
			// send choke message to current optimistically unchoked, since it's not preferred
			optimisticallyUnchoked = p;
		} else {
			optimisticallyUnchoked = p;
			// send unchoke message to this neighbor
		}
	}
	
	// This method is mainly invoked by the Unchoked object. Once this is invoked, the server will take care of choking/unchoking its neighbors according to the new configurations
	public void setPreferredNeighbors(Socket[] _preferredNeighbors) {
		// place each _preferredNeighbors into a map!
		ConcurrentHashMap<Socket,Boolean> _preferredNeighborsMap = new ConcurrentHashMap<Socket,Boolean>();
		for (int i = 0; i < _preferredNeighbors.length; i++) {
			_preferredNeighborsMap.put(_preferredNeighbors[i], true);
		}
		
		for (Map.Entry<Socket, Boolean> entry : preferredNeighbors.entrySet()) {
			// If the new map does not contain this neighbor, and the neighbor is currently preferred, send a choke message
			if (!(_preferredNeighborsMap.contains(entry)) && (entry.getValue() == true)) {
				preferredNeighbors.put(entry.getKey(), false);
				
				// Optimistically unchoked neighbors do not get a choke message
				if (entry.getKey() != optimisticallyUnchoked) {
					// Tell Server to send choke message to this Socket (entry.getKey()).
				}
			}
			
			// Now we need to send unchoke messages to those that are NOT in the current map
			if ((_preferredNeighborsMap.contains(entry)) && (entry.getValue() == false)) {
				preferredNeighbors.put(entry.getKey(), true);
				// Tell Server to send unchoke message to this Socket (entry.getKey())
			}
		}
	}
	
	public Socket getOptimisticallyUnchoked() {
		return optimisticallyUnchoked;
	}
	
	public void OptimisticallyUnchoked(Socket _optimisticallyUnchoked) {
		optimisticallyUnchoked = _optimisticallyUnchoked;
	}
	// End of Unchoked Changes
	

	/**
	 * A handler thread class. Handlers are spawned from the listening loop and
	 * are responsible for dealing with a single client's requests.
	 */
	private static class Handler extends Thread {
		private Message message;
		private Socket connection;
		private ObjectInputStream in; // stream read from the socket
		private ObjectOutputStream out; // stream write to the socket

		private RemotePeerInfo myServerInfo; // peerID of the corresponding server connection

		public Handler(Socket connection, RemotePeerInfo peerInfo) {
			this.connection = connection;
			this.myServerInfo = peerInfo;
		}

		public void run() {
			try {
				// initialize Input and Output streams
				out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();
				in = new ObjectInputStream(connection.getInputStream());
				// Create message handler
				message = new Message(in, out);

				try {
					
					// Loading the Handlers
					HandlerCached.loadCache();
					
					// HandShake Message && Add to List
					HandShake_Message hd = (HandShake_Message) in.readObject();
					clientList.put(hd.peerID, connection);
					message.HandShake(hd, myServerInfo.getPeerId());
					
					
					// Recieve Bitfield Message with list of Pieces
					ActualMessage bitList = (ActualMessage) in.readObject();	
					
					//Sending Servers bitlist  back
					MessageHandler clonedHandler = (MessageHandler) HandlerCached.getHandler(bitList.getTypeField(),myServerInfo);
					
					//Adding bitfield and setting PeerID of client neigtbor
					clonedHandler.setPeerIdNeighboor(hd.peerID);
					clonedHandler.addPeerBitSet(hd.peerID, MessageUtil.convertToBitSet(bitList.getPayloadField()));
					bitList = clonedHandler.creatingMessage();
					System.out.println("Bitfield sent(server): " + bitList.getTypeField());
					message.sendMessage(bitList);
					
					
					// Recieve Interested or Not from Client
					bitList = (ActualMessage) in.readObject();
					System.out.println("Message recieved (server): " + bitList.getTypeField());
					// If interested or not signified in the Interested (HashMap) 
					clonedHandler = (MessageHandler) HandlerCached.getHandler(bitList.getTypeField(),myServerInfo);
					clonedHandler.handleMessage(bitList, connection);
					
					
					//Send Have Message
					System.out.println("************** HAVE **************");
					clonedHandler.setPeerIdNeighboor(hd.peerID);
					clonedHandler = (MessageHandler) HandlerCached.getHandler(4,myServerInfo);
					bitList = clonedHandler.creatingMessage();
					System.out.println("Have (server): " + bitList.getPayloadField().toString());
					message.sendMessage(bitList);
					
					//while (true) {
						
					//}
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

	}
}
