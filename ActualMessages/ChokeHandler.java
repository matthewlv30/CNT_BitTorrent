package ActualMessages;

import java.net.Socket;

/**
 * Class to handle choke message
 */
public class ChokeHandler extends MessageHandler {
	/**
	 * When you receive a choke message, set isUnchoked to false
	 * 
	 * @param m:
	 *            this is the message received
	 * @param n:
	 *            this is the Node that is choking the current peer
	 */
	public int handleMessage(ActualMessage m, Socket n) {
		MessageHandler.setPeerWhoHasUnchokedMe(neighborID, false);
		PeerLogger pl = new PeerLogger(peerInfo.getPeerId());
		pl.chokingMsg(neighborID);
		return 0;
	}

	@Override
	public ActualMessage creatingMessage() {
		// Type of Meessage
		final byte messageType = 0;
		// setting up the payload as null since interested has no payload
		byte payload[] = new byte[0];
		// Get the length of the message whihc is 1
		int payloadSize = MessageUtil.convertByteToInt((byte) 1);
		byte[] length = MessageUtil.convertIntToBytes(payloadSize);
		// Create and return it so it can be sent
		ActualMessage m = new ActualMessage(length, messageType, payload);
		return m;
	}
}
