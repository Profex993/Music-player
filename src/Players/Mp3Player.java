package Players;

import Main.Main;
import Main.Song;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class Mp3Player implements Runnable {
    private final BasicPlayer player = new BasicPlayer();
    private Thread thread;
    private int currentTime = 0;
    private float currentVolume = 0.5f;

    public Mp3Player() {
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
                if (player.getStatus() == BasicPlayer.PLAYING) {
                    currentTime++;
                }
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    Main.dialogWindow("Thread error in mp3 player.");
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void setSong(Song s) {
        try {
            player.open(s.file());
        } catch (BasicPlayerException e) {
            Main.dialogWindow("Player error: mp3 set song.");
            throw new RuntimeException(e);
        }
    }

    public void play() {
        try {
            player.play();
            player.setGain(currentVolume);
            currentTime = 0;
        } catch (BasicPlayerException e) {
            Main.dialogWindow("Player error: mp3 play.");
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            currentTime = 0;
            player.stop();
        } catch (BasicPlayerException e) {
            Main.dialogWindow("Player error: mp3 stop.");
            throw new RuntimeException(e);
        }
    }

    public void pause() {
        try {
            player.pause();
        } catch (BasicPlayerException e) {
            Main.dialogWindow("Player error: mp3 pause.");
            throw new RuntimeException(e);
        }
    }

    public void resume() {
        try {
            player.resume();
        } catch (BasicPlayerException e) {
            Main.dialogWindow("Player error: mp3 resume.");
            throw new RuntimeException(e);
        }
    }

    public int currentTime() {
        return currentTime;
    }

    public boolean isRunning() {
        return player.getStatus() == BasicPlayer.PLAYING;
    }

    public void setVolume(int input) {
        float val = (float) input / 100;
        currentVolume = val;
        try {
            player.setGain(val);
        } catch (BasicPlayerException e) {
            if (!e.getMessage().equals("Gain control not supported")) {
                Main.dialogWindow("Error while setting volume.");
            }
        }
    }
}
