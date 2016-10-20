import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * This class is for messages that a peer can send (not including handshake message)
 * Each message has a structure of: 
 * 		4 byte message length | 1 byte message type | variable size message payload
 * 
 */
public class ActualMessage implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	public static final int LENGTHSIZE = 4;
	
	private byte[] length; // The message length field
	private byte type; // The message type field
	private byte[] payload; // The message payload field
	
	public ActualMessage() {
		length = new byte[4];
	}
	
	public ActualMessage(byte[] _length, byte _type, byte[] _payload) {
		if (_length.length == LENGTHSIZE) {
			length = _length;
		}
		
		type = _type;
		payload = _payload;
	}
	
	public byte[] getLengthField() {
		return length;
	}
	
	public byte getTypeField() {
		return type;
	}
	
	public byte[] getPayloadField() {
		return payload;
	}
	
}