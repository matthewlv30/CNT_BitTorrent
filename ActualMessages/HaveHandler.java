package ActualMessages;

import java.net.Socket;
import java.util.BitSet;

/**
 * Class to handle "have" message
 */
public class HaveHandler extends MessageHandler {
	
	private BitSet myBitfield;
	
	public HaveHandler(BitSet b) {
		this.myBitfield = b;
	}
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
	public void handleMessage(ActualMessage m, Socket n) {
		int index;
		byte payload[] = m.getPayloadField();
		if (payload.length != 4) {
			throw new RuntimeException("Have message does not have proper payload size");
		} else {
			index = MessageUtil.convertBytesToInt(payload);

			if (myBitfield.get(index) == true) {
				// TODO: send not interested message to Peer Node n
			} else {
				// TODO: send interested message to Peer Node n
			}
		}
	}
	@Override
	public ActualMessage creatingMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
