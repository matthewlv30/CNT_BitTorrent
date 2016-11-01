package ActualMessages;

import java.net.Socket;
import java.util.HashMap;

/**
 * Class to handle "uninterested" message
 */
public class UninterestedHandler extends MessageHandler {
	
	private HashMap<Socket, Boolean> interestedPeers;
	
	public UninterestedHandler(HashMap<Socket, Boolean> inp) {
		this.interestedPeers = inp;
	}
	/**
	 * When you receive an "uninterested" message, signify in HashMap that
	 * the peer is not interested
	 * 
	 * @param m:
	 *            this is the message received
	 * @param n:
	 *            this is the Node that is not interested in the current
	 *            peer
	 */
	public void handleMessage(ActualMessage m, Socket n) {
		interestedPeers.put(n, false);
	}
	@Override
	public ActualMessage creatingMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
