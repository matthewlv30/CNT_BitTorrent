package ActualMessages;

import java.net.Socket;
import java.util.BitSet;


/**
 * Class to handle "have" message
 */
public class HaveHandler extends MessageHandler {
		
	/**
	 * When you receive a "have" message, determine whether or not to send
	 * an interested message. The payload of the "have" message contains a
	 * piece index field
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
			index = MessageUtil.convertBytesToInt(payload);

			if (myBitfield.get(index) == true) {
				return 3;
			} else {
				return 2;
			}
		}
	}
	@Override
	public ActualMessage creatingMessage() {
		// Type of Meessage
		final byte messageType = 4;
		BitSet b = PeersBitField.get(neighborID);
		byte[] negpayload = b.toByteArray();
		byte[] mypayload = myBitfield.toByteArray();
		byte[] payload = new byte[4];
		for(int i = 0; i != mypayload.length; ++i) {
			for(int j = 0; j != negpayload.length; ++j) {
				if(mypayload[i] != negpayload[j]) {
					payload = MessageUtil.convertIntToBytes(i);
					negpayload[j] = mypayload[i];
				}
			}
		}
		
		// Get the length of the message by payload + type
		int payloadSize = payload.length + MessageUtil.convertByteToInt((byte) 1);
		byte[] length = MessageUtil.convertIntToBytes(payloadSize);
		ActualMessage m = new ActualMessage(length, messageType, payload);
		return m;
	}
}
