package fileHandlers;

import java.util.BitSet;
import java.util.Properties;

public class FileHandler {

    private BitSet receivedParts;
    private final double partSize;
    private final int bitsetSize;
    private final BitSet requestedParts;
    private FileDirectoryHandler location;
    private final long timeout;

    public FileHandler (int peerId, Properties config) {
        this (peerId, config.getProperty(CommonProperties.FileName.toString()),
            Integer.parseInt(config.getProperty(CommonProperties.FileSize.toString())),
            Integer.parseInt(config.getProperty(CommonProperties.PieceSize.toString())),
            Integer.parseInt(config.getProperty(CommonProperties.UnchokingInterval.toString())));
    }

    FileHandler (int peerId, String fileName, int fileSize, int pSize, long unchokingInterval) {
        partSize = (double)pSize;
        bitsetSize = (int)Math.ceil(fileSize/partSize);
        receivedParts = new BitSet(bitsetSize);
        requestedParts = new BitSet(bitsetSize);
        timeout = unchokingInterval * 2;
        location = new FileDirectoryHandler(peerId, fileName);
    }

    public synchronized void addPart(int pIndex, byte[] part) {
        final boolean isNewPiece = !receivedParts.get(pIndex);
        receivedParts.set(pIndex);

        if (isNewPiece) {
            location.writeByteArrayAsFilePart(part, pIndex);
        }
        if (isFileCompleted()) {
            location.mergeFile(receivedParts.cardinality());
        }
    }

    public static int pickRandomSetIndexFromBitSet(BitSet bitset) {
        if (bitset.isEmpty()) {
            System.out.println("The bitset is empty, cannot find a set element");
        }

        String set = bitset.toString();
        String[] indexes = set.substring(1, set.length()-1).split(",");
        return Integer.parseInt(indexes[(int)(Math.random()*(indexes.length-1))].trim());
    }

    /**
    * returns the ID of the part to request or a negative number
    * if all missing parts are already being requested or the file is
    * complete.
    */
    synchronized int getPartToRequest(BitSet availableParts) {
        availableParts.andNot(getReceivedParts());
        availableParts.andNot(requestedParts);

        if (!availableParts.isEmpty()) {
            final int partId = pickRandomSetIndexFromBitSet(availableParts);
            requestedParts.set(partId);

            // Added a timer to handle multiple requests
            // This makes the part requestable again in timeout
            new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        synchronized (requestedParts) {
                            requestedParts.clear(partId);
                        }
                    }
                }, timeout
            );

            return partId;
        }
        return -1;
    }

    public synchronized BitSet getReceivedParts () {
        return (BitSet)receivedParts.clone();
    }

    synchronized public boolean hasPart(int pieceIndex) {
        return receivedParts.get(pieceIndex);
    }
    
    public synchronized void setAllParts()
    {
        for (int i = 0; i < bitsetSize; i++) {
            receivedParts.set(i, true);
        }
    }

    public synchronized int getNumberOfReceivedParts() {
        return receivedParts.cardinality();
    }

    public byte[] getPiece(int partId) {
        return location.getPartAsByteArray(partId);
    }

    public void splitFile(){
        location.splitFile((int)partSize);
    }

    public byte[][] getAllPieces(){
        return location.getAllPartsAsByteArray();
    }

    public int getBitmapSize() {
        return bitsetSize;
    }
    
    public BitSet listPieces() {
		BitSet b = new BitSet();
    	for(int i = 0; i != bitsetSize; ++i) {
			if(getPiece(i) != null) {
				b.set(i * 8, (i + 1) * 8, true);
			}
			else {
				b.set(i * 8, (i + 1) * 8, false);
			}
		}
    	return b;
    	
    }
    

    private boolean isFileCompleted() {
        for (int i = 0; i < bitsetSize; i++) {
            if (!receivedParts.get(i)) {
                return false;
            }
        }
        return true;
    }
    
 
    
}
