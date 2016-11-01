package ActualMessages;

import java.net.Socket;
import java.util.BitSet;

/**
 * Class to handle "bitfield" message
 */
public class BitfieldHandler extends MessageHandler {

	// Type of Meessage
	private final byte messageType = 5;

	public BitfieldHandler() {
		// Loading the common Properties
		try {
			MessageUtil.loadCommonProperties();

			int bitFieldSize = (int) Math.ceil((double) MessageUtil.getFileSize() / MessageUtil.getPieceSize());
			myBitfield = new BitSet(bitFieldSize);

		} catch (Exception e) {
			// TODO Auto-generated catch block
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
	 * @param m:
	 *            this is the message received
	 * @param n:
	 *            this is the Node sending the message
	 */
	public void handleMessage(ActualMessage m, Socket n) {
		byte payload[] = m.getPayloadField();

		// First byte of the payload corresponds to piece indices 0-7,
		// second bit is 8-15 etc.
		for (int i = 0; i < payload.length; i++) {
			for (int j = 0; j < 8; j++) {
				// Get the bit j at this byte i. If this bit is true AND
				int bit = (payload[i] >> j) & 1;

				// If the bit is 1, peer node n contains that piece
				// If that bit index for my bitfield is false, then send
				// interested message
				// Note: special operations are used on myBitField to obtain
				// proper "byte" location
				if ((bit == 1) && (myBitfield.get(j + (i * 8)) == false)) {
					// TODO: send "interested" message to peer node n
					break;
				}
			}
		} // send "uninterested" message
	}

	public ActualMessage creatingMessage() {
		// Each index of a bit field indicates whether or not the piece is
		// with the peer.
		if (peerInfo.hasFile) {
			myBitfield.set(0, myBitfield.length(), true);
		} else {
			myBitfield.set(0, myBitfield.length(), false);
		}
		// setting up the payload with the list of pieces
		byte payload[] = myBitfield.toByteArray();
		// Get the length of the message by payload + type
		int payloadSize = myBitfield.length() + MessageUtil.convertByteToInt((byte) 1);
		byte[] length = MessageUtil.convertIntToBytes(payloadSize);
		// Create and return it so it can be sent
		ActualMessage m = new ActualMessage(length, messageType, payload);
		return m;
	}
}
