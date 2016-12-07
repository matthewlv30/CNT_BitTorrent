package ActualMessages;

import java.net.Socket;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fileHandlers.RemotePeerInfo;

/**
 * This interface declares the behavior of peers after receiving a message.
 * 
 */
/**
 * The message handler classes are implemented as inner classes so they can
 * easily access the state of its Server
 */
public abstract class MessageHandler implements Cloneable {

	protected static RemotePeerInfo peerInfo;
	// Bitfiled the each peer contain which entails a list of pieces of the file
	protected static BitSet myBitfield;
	// This is neighbor ID
	protected static int neighborID;
	// HashMap that contains the bitfields of connected neightboors
	protected static ConcurrentHashMap<Integer, BitSet> PeersBitField = new ConcurrentHashMap<Integer, BitSet>();
	// Socket: peer Socket, Boolean: 1 interested 0 uninterested
	protected static ConcurrentHashMap<Integer, Boolean> interestedPeers = new ConcurrentHashMap<Integer, Boolean>();
	// The neighbors that we can receive piece requests from
	private static ConcurrentHashMap<Integer, Boolean> preferredNeighbors = new ConcurrentHashMap<Integer, Boolean>();
	// Neighbor that we can receive pice requests from
	private static Integer optimisticallyUnchoked;
	// Peers who have unchoked me. T: I'm unchoked for this peerID. F: I'm choked for this peerID.
	private  static ConcurrentHashMap<Integer, Boolean> hasPeerUnchokedMe = new ConcurrentHashMap<Integer, Boolean>();

	// To run unchoke
	protected static Unchoked unchokedManager;
	// Indexr to select the right piece
	protected int indexRequest;

	// Unchoke Starts
	private static HashMap<Integer, Double> neighborByteCount = new HashMap<Integer, Double>(); // Maps

	public static void loadUnchoked() throws Exception {
		if (unchokedManager == null)
			unchokedManager = new Unchoked();
	}

	public static boolean isPeerInterested(Integer peerID) {
		if (interestedPeers.get(peerID) == null) {
			return false;
		} else {
			return interestedPeers.get(peerID);
		}
	}
	
	public static void setPeerWhoHasUnchokedMe(int peerID, boolean c) {
		hasPeerUnchokedMe.put(peerID, c);
	}
	
	public static ConcurrentHashMap<Integer, Boolean> getInterestedPeers() {
		return interestedPeers;
	}

	public static HashMap<Integer, Double> getNeighborByteCount() {
		return neighborByteCount;
	}

	public static void setNeighborLists(int peerID, double byteCount) {
		//setting preferred neighbors
		preferredNeighbors.put(peerID, false);
		//setting neighbor byte count
		neighborByteCount.put(peerID, byteCount);
	}
	

	public static void resetByteCount() {
		neighborByteCount.clear();
	}

	// This method is mainly invoked by the Unchoked object. Once this is
	// invoked, the server will take care of choking/unchoking its neighbors
	// according to the new configurations
	public static void setPreferredNeighbors(Integer[] _preferredNeighbors) {
		// place each _preferredNeighbors into a map!
		ConcurrentHashMap<Integer, Boolean> _preferredNeighborsMap = new ConcurrentHashMap<Integer, Boolean>();
		for (int i = 0; i < _preferredNeighbors.length; i++) {
			if(_preferredNeighbors[i] != null)
			_preferredNeighborsMap.put(_preferredNeighbors[i], true);
		}
		for (Map.Entry<Integer, Boolean> entry : preferredNeighbors.entrySet()) {
			// If the new map does not contain this neighbor, and the neighbor
			// is currently preferred, send a choke message
			if (!(_preferredNeighborsMap.containsKey(entry.getKey())) && (entry.getValue() == true)) {
				preferredNeighbors.put(entry.getKey(), false);

				// Optimistically unchoked neighbors do not get a choke message
				if (entry.getKey() != optimisticallyUnchoked) {
					// Tell Server to send choke message to this Integer
					// (entry.getKey()).
				}
			}

			// Now we need to send unchoke messages to those that are NOT in the
			// current map
			
			if ((_preferredNeighborsMap.containsKey(entry.getKey())) && (entry.getValue() == false)) {
				preferredNeighbors.put(entry.getKey(), true);
			}
		}
		//System.out.println(preferredNeighbors);
	}

	public static ConcurrentHashMap<Integer, Boolean> getPreferredNeighbors() {
		return preferredNeighbors;
	}

	public static void setOptimisticallyUnchoked(Integer p) {
		// send choke message to old optimisticallyUnchoked if it's not in
		// preferredNeighbors
		// if it's in preferredNeighbors, then dont do anything but set
		// else, set and send unchoke message

		if (!preferredNeighbors.containsKey(p)) {
			// send choke message to current optimistically unchoked, since it's
			// not preferred
			optimisticallyUnchoked = p;
		} else {
			optimisticallyUnchoked = p;
			// send unchoke message to this neighbor
		}
	}

	public Integer getOptimisticallyUnchoked() {
		return optimisticallyUnchoked;
	}

	public void OptimisticallyUnchoked(Integer _optimisticallyUnchoked) {
		optimisticallyUnchoked = _optimisticallyUnchoked;
	}
	// Unchoke Ends

	 public  abstract  int handleMessage(ActualMessage m, Socket n);

	public abstract ActualMessage creatingMessage();

	public static void setPeerInfo(RemotePeerInfo p) {
		peerInfo = p;
	}

	public static RemotePeerInfo getPeerInfo() {
		return peerInfo;
	}

	public void setPeerIdNeighboor(int p) {
		neighborID = p;
	}

	/**
	 * 
	 * @param peerID
	 *            adding specific peer to the list
	 * @param b
	 *            add this peers bitset
	 */
	public synchronized void addPeerBitSet(int peerID, BitSet b) {
		PeersBitField.put(peerID, b);
		System.out.println(PeersBitField);
	}

	public void setPieceIndex(int index) {
		indexRequest = index;
	}

	public int getPieceIndex() {
		return indexRequest;
	}

	public Object clone() {
		Object clone = null;

		try {
			clone = super.clone();

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return clone;
	}
}