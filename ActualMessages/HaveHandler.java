package ActualMessages;

import java.net.Socket;
import java.util.BitSet;

/**
 * Class to handle "have" message
 */
public class HaveHandler extends MessageHandler {

	/**
	 * When you receive a "have" message, determine whether or not to send an
	 * interested message. The payload of the "have" message contains a piece
	 * index field
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
			byte[] myByte = myBitfield.toByteArray();
			//check if the neigtboor list is empty
			myByte = MessageUtil.setPayload(myByte);	
			// update neightboor bitlist
			BitSet b = PeersBitField.get(neighborID);
			b.set(index * 8, (index * 8) + 8, true);
			// compare if interested or not
			if (myByte[index] < 0) {
				return 3; // Uninterested
			} else {
				return 2; // Interested
			}
		}
	}

	@Override
	public ActualMessage creatingMessage() {
		// Type of Meessage
		final byte messageType = 4;
		BitSet b = PeersBitField.get(neighborID);
		byte[] negpayload = b.toByteArray();
		//check if the neigtboor list is empty
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
}
