package Players;

import Main.Main;
import Main.Song;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class WavPlayer {
    private Clip clip;
    private float currentVolume = -20.0f;

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
        } catch (Exception e) {
            Main.dialogWindow("Player error: wav set song.");
        }
    }

    public void play() {
        clip.start();
        FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        fc.setValue(currentVolume);
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

    public void setVolume(int input) {
        //maximum 6.0 minimum -80.0
        float val = -80.0f;
        if (input != 0) {
            val = (float) (-40.0 + ((6.0 - (-40.0)) * (input) / 100));
        }
        currentVolume = val;
        if (clip != null) {
            FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            fc.setValue(val);
        }
    }
}
