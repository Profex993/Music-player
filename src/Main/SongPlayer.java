package Main;

import Enums.Format;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.ArrayList;

public class SongPlayer {
    private final ArrayList<Song> songs = new ArrayList<>();
    private Clip wavPlayer;
    private final BasicPlayer mp3Player = new BasicPlayer();
    private boolean mp3Running = false;
    private FileManager fileManager;
    private int currentSongIndex = 0;

    public void getSongs(String file) {
        songs.clear();
        fileManager = new FileManager(file);
        fileManager.getFiles(songs);
        currentSongIndex = 0;
        stop();
    }

    public void play() {
        if (songs.size() > 0) {
            if (songs.get(currentSongIndex).format() == Format.WAV) {
                playWav(songs.get(currentSongIndex));
            } else {
                playMp3(songs.get(currentSongIndex));
            }
        }
    }

    public void playWav(Song s) {
        if (wavPlayer != null) {
            wavPlayer.stop();
            wavPlayer.close();
        }
        try {
            stopMp3();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(s.file().toURI().toURL());
            wavPlayer = AudioSystem.getClip();
            wavPlayer.open(audioInputStream);
            wavPlayer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void playMp3(Song s) {
        try {
            if (wavPlayer != null && wavPlayer.isRunning()) {
                wavPlayer.stop();
                wavPlayer.close();
            }
            stopMp3();
            mp3Player.open(s.file());
            mp3Player.play();
            mp3Running = true;
        } catch (BasicPlayerException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopMp3() {
        mp3Running = false;
        try {
            mp3Player.stop();
        } catch (BasicPlayerException e) {
            throw new RuntimeException(e);
        }
    }

    public void pauseMp3() {
        mp3Running = false;
        try {
            mp3Player.pause();
        } catch (BasicPlayerException e) {
            throw new RuntimeException(e);
        }
    }

    public void switchSongNext() {
        currentSongIndex++;
        if (currentSongIndex == songs.size()) {
            currentSongIndex = 0;
        }

        stop();
    }

    public void switchSongBack() {
        currentSongIndex--;
        if (currentSongIndex == -1) {
            currentSongIndex = songs.size() - 1;
        }

        stop();
    }

    public void pauseOrPlay() {
        try {
            if (wavPlayer != null) {
                if (wavPlayer.isRunning() && !mp3Running) {
                    wavPlayer.stop();
                } else if (!wavPlayer.isRunning() && mp3Running) {
                    pauseMp3();
                } else if (!mp3Running && !wavPlayer.isRunning()) {
                    if (songs.get(currentSongIndex).format() == Format.MP3) {
                        mp3Player.resume();
                        mp3Running = true;
                    } else {
                        wavPlayer.start();
                    }
                }
            } else {
                if (mp3Running) {
                    pauseMp3();
                } else {
                    mp3Player.resume();
                }
            }
        } catch (BasicPlayerException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (wavPlayer != null && wavPlayer.isRunning()) {
            wavPlayer.stop();
        }
        if (mp3Running) {
            try {
                mp3Player.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Song getCurrentSong() {
        if (songs.size() > 0) {
            return songs.get(currentSongIndex);
        } else {
            return null;
        }
    }

    public int getSongTime() {
        try {
            AudioFile audioFile = AudioFileIO.read(songs.get(currentSongIndex).file());
            return audioFile.getAudioHeader().getTrackLength();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getCurrentSOngTime() {
        if (mp3Running) {
            return 0;
        } else if (wavPlayer != null) {
            return (int) (wavPlayer.getFramePosition() / wavPlayer.getFormat().getSampleRate());
        } else {
            return 0;
        }
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
