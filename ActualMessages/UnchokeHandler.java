package ActualMessages;

import java.net.Socket;
import java.util.HashMap;

/**
 * Class to handle unchoke message
 */
public class UnchokeHandler extends MessageHandler {

	private HashMap<Socket, Boolean> unchokedPeers;
	
	
	public UnchokeHandler(HashMap<Socket, Boolean> unp) {
		this.unchokedPeers = unp;
	}
	/**
	 * When you receive an unchoke message, set isUnchoked to true
	 * 
	 * @param m:
	 *            this is the message received
	 * @param n:
	 *            this is the Node that is choking the current peer
	 */
	public int handleMessage(ActualMessage m, Socket n) {
		unchokedPeers.put(n, true);
		return 0;
	}
	@Override
	public ActualMessage creatingMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
