package ActualMessages;

import java.net.Socket;
import java.util.HashMap;

/**
 * Class to handle choke message
 */
public class ChokeHandler extends MessageHandler {

	private HashMap<Socket, Boolean> unchokedPeers;
	
	
	public ChokeHandler(HashMap<Socket, Boolean> unp) {
		this.unchokedPeers = unp;
	}
	/**
	 * When you receive a choke message, set isUnchoked to false
	 * 
	 * @param m: this is the message received
	 * @param n: this is the Node that is choking the current peer
	 */
	public int handleMessage(ActualMessage m, Socket n) {
		unchokedPeers.put(n, false);
		return 0;
	}
	@Override
	public ActualMessage creatingMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
