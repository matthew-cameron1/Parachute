package ca.innov8solutions.parachute.util;

import java.io.*;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public class Utils {

    private static final Random RANDOM = new Random();

    public static int findRandomPort(int start, int end) {
        int difference = end - start;
        int trys = 0;
        int temp = 0;

        while (trys < difference) {
            temp = RANDOM.nextInt(difference) + start;

            try {
                new ServerSocket(temp).close();
                break;
            } catch (Exception e) {
                trys++;
            }
        }

        return temp;
    }

    public static void copyFilesInDirectory(File fromDir, File toDir) throws IOException {
        if (!fromDir.isDirectory()) return;
        if (!toDir.exists()) toDir.mkdirs();

        File[] files = fromDir.listFiles();

        for (File file : files) {
            File path = new File(toDir, file.getName());

            if (file.isDirectory()) {
                copyFilesInDirectory(file, path);
            } else {
                Files.copy(file.toPath(), path.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public static void copyFile(File from, File toFolder) throws IOException {
//        System.out.println("From[" + from + "] to[" + toFolder + "]");

        if (from.isDirectory()) {
            copyFilesInDirectory(from, toFolder);
        } else {
            Files.copy(from.toPath(), toFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static boolean delete(File file) {
        if (!file.delete()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }

        return file.delete();
    }
    public static void replaceFile(File file, String find, String replace)
            throws IOException {

        String old = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();
        while(line != null) {
            old = old + line + System.lineSeparator();
            line = reader.readLine();
        }

        String replaced = old.replace(find, replace);

        FileWriter writer = new FileWriter(file);
        writer.write(replaced);

        reader.close();
        writer.close();
    }

    /**
     * https://stackoverflow.com/a/14541376
     */
    public static String getIpAddress() {
        BufferedReader in = null;
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            return in.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
