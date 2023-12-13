package Main;

import java.io.*;

public class Config {
    private static final String file = "./UserData/config.txt";

    public static void load(PanelMain panel) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String temp = br.readLine();
            panel.setCurrentVolume(Integer.parseInt(temp));
            String pathTemp = br.readLine();
            File testFile = new File(pathTemp);
            if (testFile.exists()) {
                panel.setFolder(pathTemp);
            }
            br.close();
        } catch (Exception e) {
            restoreFile();
        }
    }

    public static void save(int volume, String filePath) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(String.valueOf(volume));
            bw.newLine();
            if (!filePath.equals("")) {
                bw.write(filePath);
            }
            bw.close();
        } catch (IOException e) {
            restoreFile();
        }
    }

    private static void restoreFile() {
        try {
            File folder = new File("./UserData");
            boolean test = folder.mkdirs();
            if (test) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(String.valueOf(50));
                bw.newLine();
                bw.write("");
                bw.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
