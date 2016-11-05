package fileHandlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SplitFile {

    public void process(File inputFile, int partSize){

        FileInputStream inputStream;
        String newFileName;
        FileOutputStream filePart;
        int fileSize = (int)inputFile.length();
        int nChunks = 0;
        int read = 0;
        int readLength = partSize;
        byte[] byteChunk;

        try {
            inputStream = new FileInputStream(inputFile);
            while (fileSize > 0) {

                if (fileSize <= partSize) {
                    readLength = fileSize;
                }

                byteChunk = new byte[readLength];
                read = inputStream.read(byteChunk, 0, readLength);
                fileSize -= read;
                assert (read == byteChunk.length);
                nChunks++;
                newFileName = inputFile.getParent() + "/part-" + Integer.toString(nChunks - 1) + "-" + inputFile.getName();
                filePart = new FileOutputStream(new File(newFileName));
                filePart.write(byteChunk);
                filePart.flush();
                filePart.close();
                byteChunk = null;
                filePart = null;
            }
            inputStream.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        SplitFile sf = new SplitFile();
        sf.process(new File("peer_1001/test.dat"), 2);
    }
}
