package ActualMessages;

import java.net.Socket;
import java.util.BitSet;

/**
 * Class to handle "bitfield" message
 */
public class BitfieldHandler extends MessageHandler {

	public BitfieldHandler() {
		// Loading the common Properties
		try {
			MessageUtil.loadCommonProperties();
			int bitFieldSize = (int) Math.ceil((double) MessageUtil.getFileSize() / MessageUtil.getPieceSize()); // 306
			myBitfield = new BitSet(bitFieldSize * 8); /////////////////////////////////////////////////////////////////////////////// fix this 

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// Change to get pieces and set pieces
	public BitSet getBitfield() {
		return myBitfield;
	}

	/**
	 * When you receive a "bitfield" message, determine whether or not to send
	 * an interested message
	 * 
	 * @param m: this is the message received
	 * @param n: this is the Node sending the message
	 */
	public synchronized int handleMessage(ActualMessage m, Socket n) {
		byte payload[] = m.getPayloadField();
		// First byte of the payload corresponds to piece indices 0-7, second bit is 8-15 etc.
		for (int i = 0; i < payload.length; i++) {
			for (int j = 0; j < 8; j++) {
				// Get the bit j at this byte i. If this bit is true AND
				int bit = (payload[i] >> j) & 1;
				if ((bit == 1) && (myBitfield.length() == 0)) {
					return 2; // send Interested
				}
				else if ((bit == 1) && (myBitfield.get(j + (i * 8)) == false)) {
					return 2; // send INTERESTED
				}
			}
		}
		return 3; // send UNINTERESTED
	}

	@Override
	public synchronized ActualMessage creatingMessage() {
		// Type of Meessage
		final byte messageType = 5;
		// Each index of a bit field indicates whether or not the piece is with the peer.
		
		try {
			if (peerInfo.hasFile)
				myBitfield = MessageUtil.loadPieces(peerInfo.getPeerId());
			else {
				myBitfield.set(0, myBitfield.size(), false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// setting up the payload with the list of pieces
		
		byte[] payload = myBitfield.toByteArray();
		// Get the length of the message by payload + type
		int payloadSize = payload.length + MessageUtil.convertByteToInt((byte) 1);
		byte[] length = MessageUtil.convertIntToBytes(payloadSize);
		// Create and return it so it can be sent
		ActualMessage m = new ActualMessage(length, messageType, payload);
		return m;
	}
}
