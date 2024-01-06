package Players;

import Main.Song;
import Main.SongPlayer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class WavPlayer implements Runnable {
    private final SongPlayer songPlayer;
    private Thread thread;
    private Clip clip;
    private float currentVolume = -20.0f;
    private int currentTime = 0;

    public WavPlayer(SongPlayer songPlayer) {
        this.songPlayer = songPlayer;
        startThread();
    }

    public void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        long last = System.nanoTime();
        long time;
        long timer = 0;
        while (thread != null) {
            time = System.nanoTime();
            timer += (time - last);
            last = time;
            if (timer >= 1000000000) {
                timer = 0;
                try {
                    if (isRunning() || !isRunning() && (songPlayer.isAutoplayEnabled() || songPlayer.isLoopEnabled()) &&
                            currentTime < songPlayer.getSongLength() + 2) {
                        currentTime++;
                    } else if (currentTime == songPlayer.getSongLength() + 2 && songPlayer.isAutoplayEnabled()) {
                        currentTime = 0;
                        songPlayer.getPanelMain().resetTime();
                        songPlayer.switchSongNext();
                        songPlayer.getPanelMain().setCurrentSongLabels();
                    } else if (currentTime == songPlayer.getSongLength() + 2 && songPlayer.isLoopEnabled()) {
                        currentTime = 0;
                        songPlayer.getPanelMain().resetTime();
                        setTime(0);
                        play();
                    }
                } catch (NullPointerException ignored) {
                    //could be solved by adding if (clip != null) but doesn't work for some reason...
                }
            }
        }
    }

    public void setSong(Song song) {
        try {
            if (clip != null) {
                clip.close();
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(song.file().toURI().toURL());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void play() {
        clip.start();
        FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        fc.setValue(currentVolume);
        currentTime = 0;
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            currentTime = 0;
        }
    }

    public int currentTime() {
        if (clip != null) {
            return (int) (clip.getFramePosition() / clip.getFormat().getSampleRate());
        } else {
            return 0;
        }
    }

    public boolean isRunning() {
        if (clip != null) {
            return clip.isRunning();
        } else {
            return false;
        }
    }

    public void setVolume(int input) {
        //maximum 6.0 minimum -80.0
        float val = -80.0f;
        if (input != 0) {
            val = (float) (-40.0 + ((6.0 - (-40.0)) * (input) / 100)) / 2;
        }
        currentVolume = val;
        if (clip != null) {
            FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            fc.setValue(val);
        }
    }

    public void setTime(int newTime) {
        currentTime = newTime;
        if (clip != null) {
            clip.setFramePosition((int) clip.getFormat().getSampleRate() * newTime);
        }
    }
}
