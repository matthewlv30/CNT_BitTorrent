import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * This class consists of useful functions related to messages, including conversions and parsing
 *
 */
public class MessageUtil {
	/**
	 * Convert a single byte to an Integer
	 * @param b:	the byte to be converted
	 * @return 		the converted byte as an Integer
	 */
	public static int convertByteToInt(byte b) {
		return (int) b;
	}

	/**
	 * Convert a sequence of bytes to an Integer
	 * @param bytes:	the sequence of bytes to be converted
	 * @return 			the converted byte sequence as an Integer
	 */
	public static int convertBytesToInt(byte[] bytes) {
		ByteBuffer wrapped = ByteBuffer.wrap(bytes);
		int i = wrapped.getInt();
		return i;
	}

	/**
	 * Convert byter array to a bit set
	 * @param bytes:	byte array
	 * @return 			bitset
	 */
    public static BitSet convertToBitSet(byte[] bytes) {
        BitSet bitset = new BitSet();
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                bitset.set(i);
            }
        }
        return bitset;
    }

	// TODO: Implement Convert int to Byte/Bytes

	// TODO: Does the bitset contain pieces I don't have?

	// TODO: Extract piece from file
}
