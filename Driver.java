import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.text.ParseException;
import java.util.LinkedList;

public class Driver {

	public static void main(String[] args) throws Exception {
        //Read Peer Info File and Get All info
		LinkedList<RemotePeerInfo> peersToConnect = new LinkedList<RemotePeerInfo>();
		Reader pReader = null;
		PeerInfo peerInfo = new PeerInfo();
		pReader = new FileReader(PeerInfo.CONFIG_FILE);
		peerInfo.read(pReader);
		peersToConnect = peerInfo.getPeerInfo();

        System.out.println(peersToConnect.get(0).getPeerId());
	}

}
