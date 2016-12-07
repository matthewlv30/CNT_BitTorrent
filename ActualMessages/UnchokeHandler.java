package ActualMessages;

import java.net.Socket;
import java.util.HashMap;

/**
 * Class to handle unchoke message
 */
public class UnchokeHandler extends MessageHandler {
	
	/**
	 * When you receive an unchoke message, set isUnchoked to true
	 * 
	 * @param m:
	 *            this is the message received
	 * @param n:
	 *            this is the Node that is choking the current peer
	 */
	public int handleMessage(ActualMessage m, Socket n) {
		MessageHandler.setPeerWhoHasUnchokedMe(neighborID, true);
		return 0;
	}
	@Override
	public ActualMessage creatingMessage() {
		// TODO implement creating unchoke message
		return null;
	}
}
