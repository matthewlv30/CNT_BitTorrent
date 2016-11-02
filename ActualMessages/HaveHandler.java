package ActualMessages;

import java.net.Socket;


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
		// TODO Auto-generated method stub
		
		return null;
	}
}
