package Players;

import Main.Main;
import Main.Song;
import Main.SongPlayer;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class Mp3Player implements Runnable {
    private final SongPlayer songPlayer;
    private final BasicPlayer player = new BasicPlayer();
    private Thread thread;
    private int currentTime = 0;
    private float currentVolume = 0.5f;

    public Mp3Player(SongPlayer songPlayer) {
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
                if ((player.getStatus() == BasicPlayer.PLAYING) || player.getStatus() == BasicPlayer.STOPPED
                        && (songPlayer.isAutoplayEnabled() || songPlayer.isLoopEnabled()) &&
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

                    play();
                }
            }
        }
    }

    public void setSong(Song s) {
        try {
            player.open(s.file());
        } catch (Exception e) {
            Main.openDialogWindow("Player error: mp3 set song.");
            throw new RuntimeException(e);
        }
    }

    public void play() {
        try {
            player.play();
            player.setGain(currentVolume);
            currentTime = 0;
        } catch (BasicPlayerException e) {
            Main.openDialogWindow("Player error: mp3 play.");
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            currentTime = 0;
            player.stop();
        } catch (BasicPlayerException e) {
            Main.openDialogWindow("Player error: mp3 stop.");
            throw new RuntimeException(e);
        }
    }

    public void pause() {
        try {
            player.pause();
        } catch (BasicPlayerException e) {
            Main.openDialogWindow("Player error: mp3 pause.");
            throw new RuntimeException(e);
        }
    }

    public void resume() {
        try {
            player.resume();
        } catch (BasicPlayerException e) {
            Main.openDialogWindow("Player error: mp3 resume.");
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
        float val = 0;
        if (input != 0) {
            val = (float) input / 100;
        }
        currentVolume = val;
        try {
            player.setGain(val);
        } catch (BasicPlayerException e) {
            if (!e.getMessage().equals("Gain control not supported")) {
                Main.openDialogWindow("Error while setting volume.");
            }
        }
    }

    public void setTime(int newTime) {
        try {
            long skipTime = newTime * 16000L;
            currentTime = newTime;
            player.seek(skipTime);
            player.setGain(currentVolume);
        } catch (BasicPlayerException e) {
            Main.openDialogWindow("Error while setting time.");
        }
    }
}
