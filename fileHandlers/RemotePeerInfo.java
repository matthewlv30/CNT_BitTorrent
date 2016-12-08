package fileHandlers;
import ActualMessages.PeerLogger;

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
    protected PeerLogger pl;

    public RemotePeerInfo (int peerId) throws Exception {
        this (Integer.toString(peerId), "127.0.0.1", "8000", false);
        pl = new PeerLogger(peerId);
    }

    public RemotePeerInfo(String peerId, String peerAddress, String peerPort, boolean hasFile)  {
        this.peerId = peerId;
        this.peerAddress = peerAddress;
        this.peerPort = peerPort;
        this.hasFile = hasFile;
        pl = new PeerLogger(Integer.parseInt(peerId));
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

    public String toString() {
        return new StringBuilder(peerId).append(" address:").append(peerAddress).append(" port: ").append(peerPort).toString();
    }

    public PeerLogger getLogger() {
        return pl;
    }
}
