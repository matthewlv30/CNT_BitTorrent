package ActualMessages;

import java.io.FileReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Properties;

import fileHandlers.CommonProperties;
import fileHandlers.FileHandler;

/**
 * This class consists of useful functions related to messages, including
 * conversions and parsing
 *
 */
public class MessageUtil {

	// Instance variables of the common properties
	public static int numberOfPreferredNeighbors;
	public static int unchokingInterval;
	public static int optimisticUnchokingInterval;
	public static String fileName;
	public static int fileSize;
	public static int pieceSize;

	/**
	 * Convert an Integer to an Array of Bytes of lenght 4
	 *
	 * @param b:
	 *            the int to be converted
	 * @return the converted integer as array bytes
	 */
	public static byte[] convertIntToBytes(int n) {
		return ByteBuffer.allocate(4).putInt(n).array();
	}

	/**
	 * Convert a single byte to an Integer
	 *
	 * @param b:
	 *            the byte to be converted
	 * @return the converted byte as an Integer
	 */
	public static int convertByteToInt(byte b) {
		return (int) b;
	}

	/**
	 * Convert a sequence of bytes to an Integer
	 *
	 * @param bytes:
	 *            the sequence of bytes to be converted
	 * @return the converted byte sequence as an Integer
	 */
	public static int convertBytesToInt(byte[] bytes) {
		ByteBuffer wrapped = ByteBuffer.wrap(bytes);
		int i = wrapped.getInt();
		return i;
	}

	/**
	 * Convert byte array to a bit set
	 *
	 * @param bytes:
	 *            byte array
	 * @return bitset
	 */
	public static BitSet convertToBitSet(byte[] bytes) {
		BitSet bitset = new BitSet();
		for (int i = 0; i < bytes.length * 8; i++) {
			if ((bytes[i / 8] & (1 << (i % 8))) > 0) {
				bitset.set(i);
			}
		}
		return bitset;
	}

	/**
	 * This is for Loading the common properties file The other methods are
	 * gettes for all the diffrent instance variables
	 */
	public static void loadCommonProperties() throws Exception {

		Reader cReader = new FileReader(CommonProperties.CONFIG_FILE);
		Properties cProp = CommonProperties.read(cReader);

		numberOfPreferredNeighbors = Integer.parseInt(cProp.getProperty("NumberOfPreferredNeighbors"));
		unchokingInterval = Integer.parseInt(cProp.getProperty("UnchokingInterval"));
		optimisticUnchokingInterval = Integer.parseInt(cProp.getProperty("OptimisticUnchokingInterval"));
		fileName = cProp.getProperty("FileName");
		fileSize = Integer.parseInt(cProp.getProperty("FileSize"));
		pieceSize = Integer.parseInt(cProp.getProperty("PieceSize"));
	}

	public static int getPreferredNeighborsNum() {
		return numberOfPreferredNeighbors;
	}

	public static int getUnchokingInterval() {
		return unchokingInterval;
	}

	public static int getOptimisticUncokingInterval() {
		return optimisticUnchokingInterval;
	}

	public static String getFileName() {
		return fileName;
	}

	public static int getFileSize() {
		return fileSize;
	}

	public static int getPieceSize() {
		return pieceSize;
	}
	
	public static byte[] setPayload(byte[] payload) {
		if (payload.length == 0) {
			int pay = (int) Math.ceil((double) MessageUtil.getFileSize() / MessageUtil.getPieceSize());
			payload = new byte[pay];
		}
		return payload;
	}
	
	public static BitSet loadPieces(int peerId) throws Exception {
		Reader cReader = new FileReader(CommonProperties.CONFIG_FILE);
		Properties cProp = CommonProperties.read(cReader);
		// for each peer, create its file handler by passing its id and common
		// properties
		FileHandler fh = new FileHandler(peerId, cProp);
		// split the file
		fh.splitFile();
		// return a list of the pieces
		return fh.listPieces();
	}
}
