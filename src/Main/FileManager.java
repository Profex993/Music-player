package Main;

import Enums.Format;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.images.Artwork;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManager {
    private final String filePath;
    private final Logger logger = Logger.getLogger("org.jaudiotagger");

    public FileManager(String file) {
        filePath = file;

        setupLogger();
    }

    public void setupLogger() {
        //this code makes the annoying console logging to stop... I found it on StackOverflow.com
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);
    }

    public void getFiles(ArrayList<Song> songs) {
        File dir = new File(filePath);
        int dirLength = Objects.requireNonNull(dir.list()).length;
        String[] fileNames = Objects.requireNonNull(dir.list());
        for (int i = 0; i < dirLength; i++) {
            try {
                String temp = filePath + "/" + fileNames[i];
                Song s;
                if (fileNames[i].matches(".*.wav")) {
                    s = getSong(new File(temp), Format.WAV);
                    songs.add(s);
                } else if (fileNames[i].matches(".*.mp3")) {
                    s = getSong(new File(temp), Format.MP3);
                    songs.add(s);
                } else {
                    if (!(fileNames[i].matches(".*.jpg") || fileNames[i].matches(".*.png"))) {
                        Main.openDialogWindow("Unsupported file: " + fileNames[i]);
                    }
                }
            } catch (Exception e) {
                Main.openDialogWindow("Error while loading files." + fileNames[i]);
            }
        }
    }

    public Song getSong(File file, Format format) {
        String name, creator, album, year, genre;

        if (format == Format.MP3) {
            try {
                InputStream input = new FileInputStream(file.getPath());
                Metadata metadata = new Metadata();
                Parser parser = new Mp3Parser();
                parser.parse(input, new DefaultHandler(), metadata, new ParseContext());
                input.close();

                name = metadata.get(Metadata.TITLE);
                creator = metadata.get(Metadata.CREATOR);
                album = metadata.get("xmpDM:album");
                year = metadata.get("xmpDM:releaseDate");
                genre = metadata.get("xmpDM:genre");

                Artwork art = AudioFileIO.read(file).getTag().getFirstArtwork();
                BufferedImage image = null;
                if (art != null) {
                    image = (BufferedImage) art.getImage();
                }

                return new Song(file, format, image, name, creator, album, year, genre);

            } catch (Exception e) {
                Main.openDialogWindow("Error while loading metadata." + file.getName());
            }
        } else if (format == Format.WAV) {
            try {
                return new Song(file, format, null, file.getName().replace(".wav", ""), "", "", "", "");
            } catch (Exception e) {
                Main.openDialogWindow("Error while loading metadata." + file.getName());
            }
        }
        return null;
    }

    public ArrayList<String> getSongNames() {
        File dir = new File(filePath);
        int dirLength = Objects.requireNonNull(dir.list()).length;
        String[] fileNames = Objects.requireNonNull(dir.list());
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < dirLength; i++) {
            if (fileNames[i].matches(".*.wav") || fileNames[i].matches(".*.mp3")) {
                labels.add(fileNames[i] + "\n");
            }
        }

        return labels;
    }
}
