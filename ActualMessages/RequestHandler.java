package ActualMessages;

import java.net.Socket;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Class to handle "request" message
 */
public class RequestHandler extends MessageHandler {

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
		int index;
		byte payload[] = m.getPayloadField();
		if (payload.length != 4) {
			throw new RuntimeException("Have message does not have proper payload size");
		} else {
			// get the index of interest
			index = MessageUtil.convertBytesToInt(payload);
			this.setPieceIndex(index);
			byte[] myByte = myBitfield.toByteArray();
			// check if the neigtboor list is empty
			myByte = MessageUtil.setPayload(myByte);
			// update neightboor bitlist
			BitSet b = PeersBitField.get(neighborID);
			b.set(index * 8, (index * 8) + 8, true);
			return 7;
		}
	}

	@Override
	public ActualMessage creatingMessage() {
		// Type of Meessage
		final byte messageType = 6;
		BitSet b = PeersBitField.get(neighborID);
		System.out.println(PeersBitField);
		byte[] negpayload = b.toByteArray();

		// check if the neigtboor list is empty
		negpayload = MessageUtil.setPayload(negpayload);
		byte[] mypayload = myBitfield.toByteArray();
		byte[] payload = new byte[4];
		// Usually this can be a field rather than a method variable
		System.out.println(myBitfield);
		Random rn = new Random();
		int i = 0;
		int answer = 0;

		Map<Integer, Boolean> randMap = new HashMap<Integer, Boolean>();
		System.out.println(negpayload.length);
		for (i = 0; i != negpayload.length; ++i) {
			// set a random inde
			Random rand = new Random();
			int n = rand.nextInt(negpayload.length);
			while (randMap.containsKey(n) && (randMap.get(n) == true)) {
				n = rand.nextInt(negpayload.length);
			}
			randMap.put(n, true);
			System.out.println(negpayload[answer]);
			System.out.println(mypayload[answer]);
//			if (negpayload[answer] < mypayload[answer]) {
//				this.setPieceIndex(answer);
//				payload = MessageUtil.convertIntToBytes(answer);
//				mypayload[answer] = negpayload[answer];
//				break;
//			}
		}

		// Get the length of the message by payload + type
		int payloadSize = payload.length + MessageUtil.convertByteToInt((byte) 1);
		byte[] length = MessageUtil.convertIntToBytes(payloadSize);
		ActualMessage m = new ActualMessage(length, messageType, payload);
		return m;
	}

}
