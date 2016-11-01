package ActualMessages;

import java.net.Socket;

/**
 * Class to handle "piece" message
 */
public class PieceHandler extends MessageHandler {

	/**
	 * When you receive a "piece" message, download the piece
	 * 
	 * @param m:
	 *            this is the message received
	 * @param n:
	 *            this is the Node sending the message
	 */
	public void handleMessage(ActualMessage m, Socket n) {
		byte payload[] = m.getPayloadField();

		// Retrieve the 4-byte index
		byte indexTemp[] = new byte[4];

		for (int i = 0; i < 4; i++) {
			indexTemp[i] = payload[i];
		}

		// int index = MessageUtil.convertBytesToInt(indexTemp);

		// Retrieve the rest of the payload
		// byte pieceContent[] = new byte[myInfo.getPieceSize()];
		// TODO: store rest of payload into pieceContent
	}

	@Override
	public ActualMessage creatingMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
