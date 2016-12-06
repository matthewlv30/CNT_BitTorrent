package ActualMessages;

import java.io.FileReader;
import java.io.Reader;
import java.net.Socket;
import java.util.Properties;
import fileHandlers.CommonProperties;
import fileHandlers.FileHandler;

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
		Properties c = null;
		try {
			Reader cReader = new FileReader(CommonProperties.CONFIG_FILE);
			c = CommonProperties.read(cReader);
		} catch (Exception e) {

		}
		FileHandler f = new FileHandler(peerInfo.getPeerId(), c);
		//setting the part to the right folder
		f.addPart(getPieceIndex(),payload);
		return 0;
	}

	@Override
	public ActualMessage creatingMessage() {
		// Type of Meessage
		final byte messageType = 7;
		Properties c = null;
		try {
			Reader cReader = new FileReader(CommonProperties.CONFIG_FILE);
			c = CommonProperties.read(cReader);
		} catch (Exception e) {

		}
		FileHandler f = new FileHandler(peerInfo.getPeerId(), c);
		// Each index of a bit field indicates whether or not the piece is with the peer.
		byte[] payload = f.getPiece(getPieceIndex()); // fix to get another piece
		// Get the length of the message by payload + type
		int payloadSize = payload.length + MessageUtil.convertByteToInt((byte) 1);
		byte[] length = MessageUtil.convertIntToBytes(payloadSize);
		// Create and return it so it can be sent
		ActualMessage m = new ActualMessage(length, messageType, payload);
		return m;
	}
}
