import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

/**
 * This class is for keeping track of information specific to peers including
 * - IP address / host name
 * - Peer ID
 * - Port # that it listens at
 * - And whether or not it has the complete file
 */

public class RemotePeerInfo {
    public final String peerId;
    public final String peerAddress;
    public final String peerPort;
    public final boolean hasFile;
    

    public final int numberOfPreferredNeighbors;
    public final int unchokingInterval;
    public final int optimisticUnchokingInterval;
    public final String fileName;
    public final int fileSize;
    public final int pieceSize;

    public RemotePeerInfo (int peerId) throws Exception {
        this (Integer.toString(peerId), "127.0.0.1", "8000", false);
    }

    public RemotePeerInfo(String peerId, String peerAddress, String peerPort, boolean hasFile) throws Exception {
        this.peerId = peerId;
        this.peerAddress = peerAddress;
        this.peerPort = peerPort;
        this.hasFile = hasFile;
        
		Reader cReader = new FileReader(CommonProperties.CONFIG_FILE);
		Properties cProp = CommonProperties.read(cReader);
		
		numberOfPreferredNeighbors = Integer.parseInt(cProp.getProperty("NumberOfPreferredNeighbors"));
		unchokingInterval = Integer.parseInt(cProp.getProperty("UnchokingInterval"));
		optimisticUnchokingInterval = Integer.parseInt(cProp.getProperty("OptimisticUnchokingInterval"));
		fileName = cProp.getProperty("FileName");
		fileSize = Integer.parseInt(cProp.getProperty("FileSize"));
		pieceSize = Integer.parseInt(cProp.getProperty("PieceSize"));
    }

    public int getPeerId() {
        return Integer.parseInt(peerId);
    }

    public int getPort() {
        return Integer.parseInt(peerPort);
    }

    public String getPeerAddress() {
        return peerAddress;
    }

    public boolean hasFile() {
        return hasFile;
    }
    
    public int getPreferredNeighborsNum() {
    	return numberOfPreferredNeighbors;
    }
    
    public int getUnchokingInterval() {
    	return unchokingInterval;
    }
    
    public int getOptimisticUncokingInterval() {
    	return optimisticUnchokingInterval;
    }
    
    public String getFileName() {
    	return fileName;
    }
    
    public int getFileSize() {
    	return fileSize;
    }
    
    public int getPieceSize() {
    	return pieceSize;
    }

    public String toString() {
        return new StringBuilder(peerId).append(" address:").append(peerAddress).append(" port: ").append(peerPort).toString();
    }
}
