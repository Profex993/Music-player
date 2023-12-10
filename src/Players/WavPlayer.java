package Players;

import Main.Song;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class WavPlayer {
    private Clip clip;

    public WavPlayer() {
    }
    public void setSong(Song song) {
        try {
            if (clip != null) {
                clip.close();
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(song.file().toURI().toURL());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            setVolume();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void play() {
        clip.start();
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public int currentTime() {
        return (int) (clip.getFramePosition() / clip.getFormat().getSampleRate());
    }

    public boolean isRunning() {
        if (clip != null) {
            return clip.isRunning();
        } else {
            return false;
        }
    }

    public void setVolume() {
        //maximum 6.0 minimum -80.0
        FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        fc.setValue(6.0f);
    }
}
