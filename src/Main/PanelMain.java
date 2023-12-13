package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class PanelMain extends JPanel implements Runnable {
    private Thread thread;
    private final SongPlayer player;
    private final JLabel title = new JLabel(), creator = new JLabel(), album = new JLabel(), year = new JLabel(), genre = new JLabel();
    private final JLabel timeLabel = new JLabel();
    private final Panel fileLabelPanel = new Panel();
    private final JSlider timeSlider = new JSlider(0, 0), volumeSlider;
    public BufferedImage defaultCoverImage;
    private int currentVolume = 0;
    private String filePath = "";

    public PanelMain() {
        this.setPreferredSize(new Dimension(600, 500));
        this.setFocusable(true);
        player = new SongPlayer();
        volumeSlider = new JSlider(JSlider.VERTICAL, 0, 100, 50);
        setComponents();
        getRes();
        Config.load(this);
        startThread();
    }

    public void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    private void getRes() {
        try {
            defaultCoverImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("musicCoverDefault.png")));
        } catch (IOException e) {
            Main.dialogWindow("Cannot load resource.");
        }
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
            if (timer >= 500000000) {
                timer = 0;
                timeSlider.setValue(player.getCurrentSongTime());
                if (player.isReady()) {
                    timeLabel.setText(timeDisplayConversion(timeSlider.getValue()) + "   " + timeDisplayConversion(player.getSongLength()));
                }
            }

            if (getVolume() != currentVolume && player.isReady()) {
                player.setVolume(getVolume());
                currentVolume = getVolume();
                Config.save(currentVolume, filePath);
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (player.getCurrentSong() != null && player.getCurrentSong().art() != null) {
            g.drawImage(player.getCurrentSong().art(), 175, 250, 250, 250, this);
        } else {
            g.drawImage(defaultCoverImage, 175, 250, 250, 250, this);
        }
    }

    public void setComponents() {
        repaint();
        JButton fileButton = new JButton();
        fileButton.setText("Select folder");
        fileButton.addActionListener(e -> selectFileButton());
        this.add(fileButton);

        JButton playButton = new JButton();
        playButton.setText("play");
        playButton.addActionListener(e -> player.play());
        this.add(playButton);

        JButton pauseButton = new JButton();
        pauseButton.setText("Pause");
        pauseButton.addActionListener(e -> player.pauseOrPlay());
        this.add(pauseButton);

        JButton stopButton = new JButton();
        stopButton.setText("Stop");
        stopButton.addActionListener(e -> player.stop());
        this.add(stopButton);

        JButton switchButton = new JButton();
        switchButton.setText("Switch next");
        switchButton.addActionListener(e -> switchButtonFunction());
        this.add(switchButton);

        JButton switchBackButton = new JButton();
        switchBackButton.setText("Switch back");
        switchBackButton.addActionListener(e -> switchBackButtonFunction());
        this.add(switchBackButton);

        this.add(timeSlider);
        this.add(timeLabel);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setMinorTickSpacing(10);
        volumeSlider.setMajorTickSpacing(50);
        volumeSlider.setPreferredSize(new Dimension(50, 100));
        this.add(volumeSlider);

        fileLabelPanel.setLayout(new BoxLayout(fileLabelPanel, BoxLayout.PAGE_AXIS));
        this.add(fileLabelPanel);

        Panel labelPanel = new Panel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
        labelPanel.add(title);
        labelPanel.add(creator);
        labelPanel.add(album);
        labelPanel.add(year);
        labelPanel.add(genre);
        this.add(labelPanel);

        setSongLabels();
    }

    public void setSongLabels() {
        if (player.getCurrentSong() != null) {
            timeSlider.setMaximum(player.getSongLength());
            Song song = player.getCurrentSong();
            if (song.title() != null && !song.title().isEmpty()) {
                title.setText("Title: " + song.title());
            } else {
                title.setText("");
            }
            if (song.creator() != null && !song.creator().isEmpty()) {
                creator.setText("Creator: " + song.creator());
            } else {
                creator.setText("");
            }
            if (song.album() != null && !song.album().isEmpty()) {
                album.setText("Album: " + song.album());
            } else {
                album.setText("");
            }
            if (song.year() != null && !song.year().isEmpty()) {
                year.setText("Year: " + song.year());
            } else {
                year.setText("");
            }
            if (song.genre() != null && !song.genre().isEmpty()) {
                genre.setText("Genre: " + song.genre());
            } else {
                genre.setText("");
            }
        }
    }

    public void setFileNames() {
        fileLabelPanel.removeAll();
        String[] labels;
        if (player.getFileManager().getSongNames() != null) {
            labels = player.getFileManager().getSongNames().toArray(new String[0]);
        } else {
            labels = new String[0];
        }
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(i + 1 + ". " + labels[i]);
            fileLabelPanel.add(label);
        }
    }

    public void switchButtonFunction() {
        player.switchSongNext();
        setSongLabels();
        repaint();
        timeSlider.setValue(0);
    }

    public void switchBackButtonFunction() {
        player.switchSongBack();
        setSongLabels();
        repaint();
        timeSlider.setValue(0);
    }

    public void selectFileButton() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            filePath = fileChooser.getSelectedFile().getAbsolutePath();
            player.getSongs(filePath);
            repaint();
            setSongLabels();
            setFileNames();
            Config.save(currentVolume, filePath);
        }
    }

    private String timeDisplayConversion(int input) {
        int minutes = (int) Math.floor((double) input / 60);
        int seconds = input % 60;

        String output = "";

        if (minutes < 10) {
            output += 0;
        }
        output += minutes + ":";
        if (seconds < 10) {
            output += 0;
        }
        output += seconds;

        return output;
    }

    public int getVolume() {
        return volumeSlider.getValue();
    }

    public void setCurrentVolume(int val) {
        volumeSlider.setValue(val);
    }

    public void setFolder(String path) {
        player.getSongs(path);
        filePath = path;
        repaint();
        setSongLabels();
        setFileNames();
    }
}
