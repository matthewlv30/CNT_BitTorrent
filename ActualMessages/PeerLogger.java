package ActualMessages;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Class for logging all the messages from each peer
 */
public class PeerLogger {
	//TODO: FIX CONFIGURATION PROPERTIES SO NOT ALL LOGS GET THE SAME MESSAGE
	//TODO: LOG WHEN DOWNLOAD IS COMPLETE
	//private static final String CONF = "logger.properties";
    private static final Logger l = Logger.getLogger("CNT4007");
    private final String peerMsgHeader;

    public PeerLogger(int peerId) {
        this.peerMsgHeader = ": Peer [" + peerId + "]";
        //String logger = "" + peerId + "";
        //Logger l = Logger.getLogger(logger);
        //InputStream in = null;
        try {
            Handler fh = new FileHandler ("log_peer_" + peerId + ".log");
            //in = PeerLogger.class.getResourceAsStream(CONF);
            //LogManager.getLogManager().readConfiguration(new FileInputStream("logger.properties"));
            //LogManager.getLogManager().readConfiguration(in);
            l.setUseParentHandlers(false);
            l.addHandler(fh);
            fh.setFormatter(new CustomFormatter());
        } catch (SecurityException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } 
    }

    /**
     * Log TCP Connection message,
     */
    public void tcpConnectionMsg(int peerId, boolean connectingPeer) {
        if (connectingPeer) {
            l.info(getPeerMsgHeader() + " makes a connection to Peer " + formatPeerId(peerId));
        } else {
            l.info(getPeerMsgHeader() + " is connected from Peer " + formatPeerId(peerId));
        }
    }

    /**
     * Log list of preferred neighbors
     */
    public void changeOfPreferredNeighborsMsg(String preferredNeighbors) {
        l.info(getPeerMsgHeader() + " has preferred neighbors " + formatPreferredNeighbors(preferredNeighbors));
    }

    /**
     * Log list of of optimistically unchoked neighbors
     */
    public void changeOfOptimisticallyUnchokedNeighborsMsg(int peerId) {
        l.info(getPeerMsgHeader() + " has the optimistically unchoked neighbor " + formatPeerId(peerId));
    }

    /**
     * Log unchoked message
     */
    public void unchokeMsg(int peerId) {
        l.info(getPeerMsgHeader() + " is unchoked by " + formatPeerId(peerId));
    }

    /**
     * Log choked message
     */
    public void chokingMsg(int peerId) {
        l.info(getPeerMsgHeader() + " is choked by " + formatPeerId(peerId));
    }

    /**
     * Log have message along with the index of the piece
     */
    public void haveMsg(int peerId, int pieceIdx) {
        l.info(getPeerMsgHeader() + " received the 'have' message from " + formatPeerId(peerId) + " for the piece [" + pieceIdx + "]");
    }

    /**
     * Log interested message
     */
    public void interestedMsg(int peerId) {
        l.info(getPeerMsgHeader() + " received the 'interested' message from " + formatPeerId(peerId));
    }

    /**
     * Log not interested message
     */
    public void notInterestedMsg(int peerId) {
        l.info(getPeerMsgHeader() + " received the 'not interested' message from " + formatPeerId(peerId));
    }

    /**
     * Log piece that has been downloaded from peer
     */
    public void downloadingPieceMsg(int peerId, int pieceIdx, int currNumberOfPieces) {
        l.info(getPeerMsgHeader() + " has downloaded the piece [" + pieceIdx + "] from peer " + formatPeerId(peerId) + " Now the number of pieces it has is [" + currNumberOfPieces + "]");
    }

    /**
     * Log when download complete
     */
    public void downloadCompletedMsg() {
        l.info(getPeerMsgHeader() + " has downloaded the complete file.");
    }

    /**
     * Return peer message header to use in the log
     */
    private String getPeerMsgHeader(){
        return this.peerMsgHeader;
    }

    /**
     * Return peerId in proper format for the log
     */
    private String formatPeerId(int peerId){
        return "[" + peerId + "]";
    }

    /**
     * Return list of preferred Neightbors in proper format
     */
    private String formatPreferredNeighbors(String preferredNeighbors ){
        return "[" + preferredNeighbors + "]";
    }

    /**
     * Class for formatting the log output
     * Fromat = [hh:mm:ss] [PeerId] msg
     */
    public class CustomFormatter extends Formatter {
        DateFormat df = new SimpleDateFormat("HH:mm:ss a");
        Date dateobj = new Date();

        @Override
        public String format(LogRecord record) {
            return "[" + df.format(dateobj) + "]" +  record.getMessage() + "\n";
        }
    }
}
