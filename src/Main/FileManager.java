package Main;

import Enums.Format;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class FileManager {
    private final String path;

    public FileManager(String file) {
        path = file;
    }

    public void getFiles(ArrayList<Song> songs) {
        File dir = new File(path);
        int dirLength = Objects.requireNonNull(dir.list()).length;
        for (int i = 0; i < dirLength; i++) {
            try {
                String temp = path + "/" + (Objects.requireNonNull(dir.list()))[i];
                Song s;
                if ((Objects.requireNonNull(dir.list()))[i].matches("[A-Za-z ]*.wav")) {
                    s = getSong(new File(temp), Format.WAV);
                    songs.add(s);
                } else if ((Objects.requireNonNull(dir.list()))[i].matches("[A-Za-z ]*.mp3")) {
                    s = getSong(new File(temp), Format.MP3);
                    songs.add(s);
                } else if (!(Objects.requireNonNull(dir.list()))[i].matches(".*.jpg")) {
                    throw new RuntimeException("unknown format");
                }
            } catch (Exception e) {
                System.out.println("Unsupported file");
            }
        }
    }

    public Song getSong(File file, Format format) {
        String name, creator, album, year, genre;

        if (format == Format.MP3) {
            try {
                InputStream input = new FileInputStream(file.getPath());
                ContentHandler handler = new DefaultHandler();
                Metadata metadata = new Metadata();
                Parser parser = new Mp3Parser();
                ParseContext parseCtx = new ParseContext();
                parser.parse(input, handler, metadata, parseCtx);
                input.close();

                name = metadata.get(Metadata.TITLE);
                creator = metadata.get(Metadata.CREATOR);
                album = metadata.get("xmpDM:album");
                year = metadata.get("xmpDM:releaseDate");
                genre = metadata.get("xmpDM:genre");

                AudioFile audioFile = AudioFileIO.read(file);
                Tag tag = audioFile.getTag();
                Artwork art = tag.getFirstArtwork();
                BufferedImage image = null;
                if (art != null) {
                    image = (BufferedImage) art.getImage();
                }

                return new Song(file, format, image, name, creator, album, year, genre);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return new Song(file, format, null, file.getName().replace(".wav", ""), "", "", "", "");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ArrayList<String> getSongNames() {
        File dir = new File(path);
        int dirLength = Objects.requireNonNull(dir.list()).length;
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < dirLength; i++) {
            if ((Objects.requireNonNull(dir.list()))[i].matches(".*.wav") || (Objects.requireNonNull(dir.list()))[i].matches(".*.mp3")) {
                labels.add(Objects.requireNonNull(dir.list())[i] + "\n");
            }
        }

        return labels;
    }
}
