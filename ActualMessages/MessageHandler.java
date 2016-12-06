package ActualMessages;

import java.net.Socket;
import java.util.BitSet;
import java.util.HashMap;

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

	protected RemotePeerInfo peerInfo;
	// Bitfiled the each peer contain which entails a list of pieces of the file
	protected static BitSet myBitfield;
	// This is neighbor ID
	protected static int neighborID;
	// HashMap that contains the bitfields of connected neightboors
	protected static HashMap<Integer,BitSet> PeersBitField = new HashMap<Integer,BitSet>();
	// Socket: peer Socket, Boolean: 1 interested 0 uninterested
	protected static HashMap<Socket, Boolean> interestedPeers = new HashMap<Socket,Boolean>();
	
	
	
	protected int indexRequest;
	
	
	
	//Unchoke Starts 
	private static HashMap<Integer, Double> neighborByteCount = new HashMap<Integer, Double>(); // Maps peerID to how many bytes have been downloaded from them
	
	
	public HashMap<Integer, Double> getNeighborByteCount() {
		return neighborByteCount;
	}
	
	public void resetByteCount() {
		neighborByteCount.clear();
	}
	
	//Unchoke Ends
	
	public abstract int handleMessage(ActualMessage m, Socket n);
	public abstract ActualMessage creatingMessage();

	public void setPeerInfo(RemotePeerInfo p) {
		this.peerInfo = p;
	}
	public  void setPeerIdNeighboor(int p) {
		neighborID = p;
	}
	/**
	 * 
	 * @param peerID adding specific peer to the list
	 * @param b add this peers bitset
	 */
	public synchronized void addPeerBitSet(int peerID, BitSet b) {
		PeersBitField.put(peerID, b);
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