package Main;

import Enums.Format;
import Players.Mp3Player;
import Players.WavPlayer;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

import java.util.ArrayList;

public class SongPlayer {
    private final ArrayList<Song> songs = new ArrayList<>();
    private final WavPlayer wavPlayer = new WavPlayer();
    private final Mp3Player mp3Player = new Mp3Player();
    private FileManager fileManager;
    private int currentSongIndex = 0;

    public void getSongs(String file) {
        songs.clear();
        fileManager = new FileManager(file);
        fileManager.getFiles(songs);
        currentSongIndex = 0;
        setCurrentSong();
        stop();
    }

    public void play() {
        if (!songs.isEmpty()) {
            if (songs.get(currentSongIndex).format() == Format.WAV) {
                mp3Player.stop();
                wavPlayer.play();
            } else {
                wavPlayer.stop();
                mp3Player.play();
            }
        }
    }

    public void setCurrentSong() {
        if (!songs.isEmpty()) {
            if (songs.get(currentSongIndex).format() == Format.WAV) {
                wavPlayer.setSong(songs.get(currentSongIndex));
            } else {
                mp3Player.setSong(songs.get(currentSongIndex));
            }
        }
    }

    public void switchSongNext() {
        currentSongIndex++;
        if (currentSongIndex == songs.size()) {
            currentSongIndex = 0;
        }
        stop();
        setCurrentSong();
    }

    public void switchSongBack() {
        currentSongIndex--;
        if (currentSongIndex == -1) {
            currentSongIndex = songs.size() - 1;
        }
        stop();
        setCurrentSong();
    }

    public void pauseOrPlay() {
        if (songs.get(currentSongIndex).format() == Format.MP3) {
            if (mp3Player.isRunning()) {
                mp3Player.pause();
            } else {
                mp3Player.resume();
            }
        } else {
            if (wavPlayer.isRunning()) {
                wavPlayer.stop();
            } else {
                wavPlayer.play();
            }
        }
    }

    public void stop() {
        if (wavPlayer.isRunning()) {
            wavPlayer.stop();
        }
        mp3Player.stop();
    }

    public Song getCurrentSong() {
        if (!songs.isEmpty()) {
            return songs.get(currentSongIndex);
        } else {
            return null;
        }
    }

    public int getSongLength() {
        try {
            AudioFile audioFile = AudioFileIO.read(songs.get(currentSongIndex).file());
            return audioFile.getAudioHeader().getTrackLength();
        } catch (Exception e) {
            Main.dialogWindow("Player error: song length.");
            return 0;
        }
    }

    public int getCurrentSongTime() {
        if (!songs.isEmpty()) {
            if (songs.get(currentSongIndex).format() == Format.MP3) {
                return mp3Player.currentTime();
            } else {
                return wavPlayer.currentTime();
            }
        }
        return 0;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public boolean isReady() {
        return !songs.isEmpty();
    }

    public boolean isPlaying() {
        return wavPlayer.isRunning() || mp3Player.isRunning();
    }

    public void setVolume(int input) {
        wavPlayer.setVolume(input);
        mp3Player.setVolume(input);
    }

    public void setTime(int input) {
        if (wavPlayer.isRunning()) {
            wavPlayer.setTime(input);
        }
        if (mp3Player.isRunning()) {
            mp3Player.setTime(input);
        }
    }
}
