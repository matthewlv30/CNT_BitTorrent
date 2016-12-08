package ActualMessages;

import java.net.Socket;

/**
 * Class to handle "uninterested" message
 */
public class UninterestedHandler extends MessageHandler {

	/**
	 * When you receive an "uninterested" message, signify in HashMap that the
	 * peer is not interested
	 * 
	 * @param m: this is the message received
	 * @param n: this is the Node that is not interested in the current peer
	 */
	public  int handleMessage(ActualMessage m, Socket n) {
		// Since neighboor peer uninterested add to map as false
		interestedPeers.put(neighborID, false);
		PeerLogger pl = new PeerLogger(peerInfo.getPeerId());
		pl.notInterestedMsg(neighborID);
		return 0;
	}

	@Override
	public ActualMessage creatingMessage() {
		// Type of Meessage
		final byte messageType = 3;
		// setting up the payload as null since uninterested has no payload
		byte payload[] = new byte[0];
		// Get the length of the message whihc is 1
		int payloadSize = MessageUtil.convertByteToInt((byte) 1);
		byte[] length = MessageUtil.convertIntToBytes(payloadSize);
		// Create and return it so it can be sent
		ActualMessage m = new ActualMessage(length, messageType, payload);
		return m;
	}
}
