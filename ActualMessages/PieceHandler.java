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
	public int handleMessage(ActualMessage m, Socket n) {
		byte payload[] = m.getPayloadField();

		// Retrieve the 4-byte index
		byte indexTemp[] = new byte[4];

		for (int i = 0; i < 4; i++) {
			indexTemp[i] = payload[i];
		}
		return 0;

		// int index = MessageUtil.convertBytesToInt(indexTemp);

		// Retrieve the rest of the payload
		// byte pieceContent[] = new byte[myInfo.getPieceSize()];
		// TODO: store rest of payload into pieceContent
	}

	@Override
	public ActualMessage creatingMessage() {
		// Type of Meessage
				final byte messageType = 7;
				// Each index of a bit field indicates whether or not the piece is with the peer.
						
				byte[] payload = myBitfield.toByteArray();
				//if(payload.length == 0) {
					//int pay = (int) Math.ceil((double) MessageUtil.getFileSize() / MessageUtil.getPieceSize());
					//payload = new byte[pay];
				//}
				// Get the length of the message by payload + type
				int payloadSize = payload.length + MessageUtil.convertByteToInt((byte) 1);
				byte[] length = MessageUtil.convertIntToBytes(payloadSize);
				// Create and return it so it can be sent
				ActualMessage m = new ActualMessage(length, messageType, payload);
				return m;
			}
}