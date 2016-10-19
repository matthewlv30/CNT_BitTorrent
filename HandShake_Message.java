
import java.util.Arrays;

public class HandShake_Message implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final String header = "P2PFILESHARINGPROJ";
	public byte[] arr = new byte[4];
	public int peerID;

	HandShake_Message(int peerID) {
		Arrays.fill(arr, (byte) 0);
		this.peerID = peerID;
	}

}
