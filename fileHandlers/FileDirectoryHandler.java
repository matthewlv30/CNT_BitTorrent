package fileHandlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FileDirectoryHandler {
    private final File file;
    private final File  partsDirectory;
    private static final String partsLocation = "files/parts/";

    public FileDirectoryHandler(int peerId, String fileName){
        partsDirectory = new File("./peer_" + peerId + "/" + partsLocation + fileName);
        partsDirectory.mkdirs();
        file = new File(partsDirectory.getParent() + "/../" + fileName);
    }

    public byte[][] getAllPartsAsByteArray(){
        File[] files = partsDirectory.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        });

        byte[][] byteArray = new byte[files.length][getPartAsByteArray(1).length];
        for (File file : files) {
            byteArray[Integer.parseInt(file.getName())] = getByteArrayFromFile(file);
        }

        return byteArray;
    }

    public byte[] getPartAsByteArray(int partId) {
        File file = new File(partsDirectory.getAbsolutePath() + "/" + partId);
        return getByteArrayFromFile(file);
    }

    public void writeByteArrayAsFilePart(byte[] part, int partId){
        FileOutputStream fos;
        File outFile = new File(partsDirectory.getAbsolutePath() + "/" + partId);
        try {
            fos = new FileOutputStream(outFile);
            fos.write(part);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private byte[] getByteArrayFromFile(File file){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] fileBytes = new byte[(int)file.length()];
            int bytesRead = fis.read(fileBytes, 0, (int) file.length());
            fis.close();
            assert (bytesRead == fileBytes.length);
            assert (bytesRead == (int) file.length());
            return fileBytes;
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
        return null;

    }

    public void splitFile(int partSize){
        FileInputStream inputStream;
        String newFileName;
        FileOutputStream filePart;
        int fileSize = (int)file.length();
        int nChunks = 0;
        int read = 0;
        int readLength = partSize;
        byte[] byteChunk;

        try {
            inputStream = new FileInputStream(file);
            while (fileSize > 0) {
                if (fileSize <= partSize) {
                    readLength = fileSize;
                }
                byteChunk = new byte[readLength];
                read = inputStream.read(byteChunk, 0, readLength);
                fileSize -= read;
                assert (read == byteChunk.length);
                nChunks++;
                newFileName = file.getParent() + "/parts/" + file.getName() + "/" + Integer.toString(nChunks - 1);
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

    public void mergeFile(int nParts) {
        File outFile = file;
        FileOutputStream fos;
        FileInputStream fis;
        byte[] fileBytes;
        int bytesRead = 0;
        List<File> list = new ArrayList<>();
        for (int i = 0; i < nParts; i++) {
            list.add(new File(partsDirectory.getPath() + "/" + i));
        }
        try {
            fos = new FileOutputStream(outFile);
            for (File file : list) {
                fis = new FileInputStream(file);
                fileBytes = new byte[(int) file.length()];
                bytesRead = fis.read(fileBytes, 0, (int) file.length());
                assert (bytesRead == fileBytes.length);
                assert (bytesRead == (int) file.length());
                fos.write(fileBytes);
                fos.flush();
                fileBytes = null;
                fis.close();
                fis = null;
            }
            fos.close();
            fos = null;
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}
