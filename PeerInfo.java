/**
 * This class is for retrieving peer-specific information from the PeerInfo.cfg file
 * 
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.LinkedList;

public class PeerInfo {
    public static final String CONFIG_FILE = "PeerInfo.cfg";
    private final LinkedList<RemotePeerInfo> peerInfo = new LinkedList<RemotePeerInfo>();

    /**
     * Read in the peer info fields and store the info for each peer into a linked list
     * @param reader: a Reader, a simple example can be seen in the Driver.java class
     * @throws Exception 
     * 
     */
    public void read (Reader reader) throws Exception {
        BufferedReader in = new BufferedReader(reader);
        int i = 0;
        for (String line; (line = in.readLine()) != null;) {
            line = line.trim();
            if (line.length() <= 0) { continue; }
            String[] tokens = line.split("\\s+");
            if (tokens.length != 4) {
                throw new ParseException (line, i);
            }
            final boolean hasFile = (tokens[3].trim().compareTo("1") == 0);
            peerInfo.add(new RemotePeerInfo(tokens[0].trim(), tokens[1].trim(), tokens[2].trim(), hasFile));
            i++;
        }
    }
    /**
     * Returns a linked list of the info for each peer
     * Info can be accessed through methods defined in the RemotePeerInfo class
     */
    public LinkedList<RemotePeerInfo> getPeerInfo () {
        return peerInfo;
    }

}