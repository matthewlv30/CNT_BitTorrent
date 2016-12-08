package ActualMessages;

import java.net.Socket;

/**
 * Class to handle "interested" message
 */
public class InterestedHandler extends MessageHandler {
	/**
	 * When you receive an "interested" message, signify in HashMap that the
	 * peer is interested
	 * 
	 * @param m:  this is the message received
	 * @param n:  this is the Node that is interested in the current peer
	 */
	public  int handleMessage(ActualMessage m, Socket n) {
		// Since neighboor peer interested add to map as true
		interestedPeers.put(neighborID, true); 
		return 0;
	}

	@Override
	public  ActualMessage creatingMessage() {
		// Type of Meessage
		final byte messageType = 2;
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
