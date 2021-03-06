package fileHandlers;
import java.io.Reader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Properties;

import ActualMessages.PeerLogger;

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
        int peerId = peersToConnect.get(0).getPeerId();

        //file manager sample
        //import common properties
        Reader cReader = new FileReader(CommonProperties.CONFIG_FILE);
		Properties cProp = CommonProperties.read(cReader);

		//for each peer, create its file handler by passing its id and common properties
		FileHandler fh = new FileHandler(peerId, cProp);

		//split the file
		fh.splitFile();
		
		System.out.println(fh.getBitmapSize());	
		
		int peerServerID = 1001;
		int myId = 1002;
		
		//log connection - one for peerId and peerServerId
		//PeerLogger peerLogger = new PeerLogger(myInfo.getPeerId());
		PeerLogger peerLogger = new PeerLogger(myId);
		PeerLogger connectingPeerLogger = new PeerLogger(peerServerID);
		peerLogger.tcpConnectionMsg(peerServerID, false);
		connectingPeerLogger.chokingMsg(myId);
		//connectingPeerLogger.tcpConnectionMsg(myInfo.getPeerId(), false);
		connectingPeerLogger.tcpConnectionMsg(myId, true);
		peerLogger.chokingMsg(peerServerID);
	}

}
