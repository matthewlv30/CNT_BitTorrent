import java.nio.ByteBuffer;

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

	// TODO: Implement Convert int to Byte/Bytes
	
	// TODO: Does the bitset contain pieces I don't have?
	
	// TODO: Extract piece from file
}