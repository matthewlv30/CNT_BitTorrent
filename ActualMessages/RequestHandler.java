package ActualMessages;

import java.net.Socket;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class to handle "request" message
 */
public class RequestHandler extends MessageHandler {

	private ConcurrentHashMap<Integer, Socket> clientList;
	private BitSet myBitfield;

	public RequestHandler(BitSet b, ConcurrentHashMap<Integer, Socket> c) {
		this.clientList = c;
		this.myBitfield = b;
	}

	/**
	 * When you receive a "request" message, send a message with the requested
	 * piece
	 * 
	 * @param m:
	 *            this is the message received
	 * @param n:
	 *            this is the Node sending the message
	 */
	public int handleMessage(ActualMessage m, Socket n) {
		byte payload[] = m.getPayloadField();

		if (payload.length != 4) {
			throw new RuntimeException("Request message does not have proper payload size");
		} else {
			int index = MessageUtil.convertBytesToInt(payload);
			// int otherPeerID = n.getInfo().getPeerId();

			// Check if peer sending msg is unchoked and that my bitfield
			// has the piece at this index
			if (clientList.contains(n) && (myBitfield.get(index) == true)) {
				// TODO: send a "piece" message to n
				// TODO: work on retrieving "piece" bytes from a file
			}
		}
		return 0;
	}

	@Override
	public ActualMessage creatingMessage() {
		// Type of Meessage
		final byte messageType = 6;
		BitSet b = PeersBitField.get(neighborID);
		byte[] negpayload = b.toByteArray();
		// check if the neigtboor list is empty
		negpayload = MessageUtil.setPayload(negpayload);

		byte[] mypayload = myBitfield.toByteArray();
		byte[] payload = new byte[4];
		int i = 0;
		for (i = 0; i != mypayload.length; ++i) {
			if (mypayload[i] != negpayload[i]) {
				payload = MessageUtil.convertIntToBytes(i);
				break;
			}
		}
		negpayload[i] = mypayload[i];
		// Get the length of the message by payload + type
		int payloadSize = payload.length + MessageUtil.convertByteToInt((byte) 1);
		byte[] length = MessageUtil.convertIntToBytes(payloadSize);
		ActualMessage m = new ActualMessage(length, messageType, payload);
		return m;
	}

	//Randomly pick a piece from the list
	public static int getRandom(int[] array) {
		int rnd = new Random().nextInt(array.length);
		return array[rnd];
	}
}
