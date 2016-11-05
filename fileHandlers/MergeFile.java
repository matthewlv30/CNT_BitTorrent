package fileHandlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MergeFile {
    private static final String FILE_NAME = "test.dat";

    public static void main(String[] args) {

        File ofile = new File("MergeTest.dat");
        FileOutputStream fos;
        FileInputStream fis;
        byte[] fileBytes;
        int bytesRead = 0;
        List<File> list = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            list.add(new File("peer_1001/part-" + i + "-" + FILE_NAME));
            System.out.println(list);
        }
        try {
            fos = new FileOutputStream(ofile, true);
            for (File file : list) {
                fis = new FileInputStream(file);
                fileBytes = new byte[(int)file.length()];
                bytesRead = fis.read(fileBytes, 0, (int)file.length());
                assert (bytesRead == fileBytes.length);
                assert (bytesRead == (int)file.length());
                fos.write(fileBytes);
                fos.flush();
                fileBytes = null;
                fis.close();
                fis = null;
            }
            fos.close();
            fos = null;
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
