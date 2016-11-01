package ActualMessages;

import java.net.Socket;
import java.util.HashMap;

/**
 * Class to handle "interested" message
 */
public class InterestedHandler extends MessageHandler {

	private HashMap<Socket, Boolean> interestedPeers;
	
	
	public InterestedHandler(HashMap<Socket, Boolean> inp) {
		this.interestedPeers = inp;
	}
	/**
	 * When you receive an "interested" message, signify in HashMap that the
	 * peer is interested
	 * 
	 * @param m:
	 *            this is the message received
	 * @param n:
	 *            this is the Node that is interested in the current peer
	 */
	public void handleMessage(ActualMessage m, Socket n) {
		interestedPeers.put(n, true);
	}
	@Override
	public ActualMessage creatingMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
