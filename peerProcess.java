import java.io.FileReader;
import java.io.Reader;
import java.util.LinkedList;

public class peerProcess {

	public static int peerID;

	// Connect to previous servers
	public static void main(String[] args) throws Exception {
		peerID = Integer.parseInt(args[0]); // taking in argument peerID

        Reader cReader = null;
        Properties cProp = null;

		// Peer Configuration File Read and Store all Info
		LinkedList<RemotePeerInfo> peersToConnect = new LinkedList<RemotePeerInfo>();
		Reader pReader = null;
		PeerInfo peerInfo = new PeerInfo();
		pReader = new FileReader(PeerInfo.CONFIG_FILE);
		peerInfo.read(pReader);
		peersToConnect = peerInfo.getPeerInfo();

		System.out.println(peersToConnect.get(0).toString());

		// ==========================================================
		// Start Server && Clients
		Server s;

		if (peerID == peersToConnect.get(0).getPeerId()) {
			s = new Server(peersToConnect.get(0).getPort(),peerID);
			s.start();
		} else {
			//Start Server

			Client[] clientList = new Client[peersToConnect.size()];

			// Client side: Have the client connect to servers before it
			int i = 0;
			for (i = 0; i < peersToConnect.size(); i++) {
				if (peersToConnect.get(i).getPeerId() != peerID) {
					// create a socket to connect to the server
					clientList[i] = new Client(peersToConnect.get(i).getPeerId(),"localhost",peersToConnect.get(i).getPort(), peersToConnect.get(i).getPeerId());
					//clientList[i] = new Client(peerID,peersToConnect.get(i).getPeerAddress(),peersToConnect.get(i).getPort(), peersToConnect.get(i).getPeerId());
				} else {
					break;
				}
			}
			s = new Server(peersToConnect.get(i).getPort(), peerID);
			s.start();
			for(int j = 0; j != clientList.length; ++j) {
				if(clientList[j] == null) {
					break;
				}
				clientList[j].start();
			}
		}
	}
}
