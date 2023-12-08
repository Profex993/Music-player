package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class PanelMain extends JPanel implements Runnable {
    private Thread thread;
    private final SongPlayer player = new SongPlayer();
    private final JLabel title = new JLabel(), creator = new JLabel(), album = new JLabel(), year = new JLabel(), genre = new JLabel();
    private final Panel fileLabelPanel = new Panel();
    private final JSlider timeSlider = new JSlider(0, 0);
    public BufferedImage defaultCoverImage;

    public PanelMain() {
        this.setPreferredSize(new Dimension(600, 500));
        this.setFocusable(true);
        setComponents();
        getRes();
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
            throw new RuntimeException(e);
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
                timeSlider.setValue(player.getCurrentSOngTime());
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
            timeSlider.setMaximum(player.getSongTime());
            System.out.println(timeSlider.getValue() + "time");
            Song song = player.getCurrentSong();
            if (song.title() != null && !song.title().equals("")) {
                title.setText("Title: " + song.title());
            } else {
                title.setText("");
            }
            if (song.creator() != null && !song.creator().equals("")) {
                creator.setText("Creator: " + song.creator());
            } else {
                creator.setText("");
            }
            if (song.album() != null && !song.album().equals("")) {
                album.setText("Album: " + song.album());
            } else {
                album.setText("");
            }
            if (song.year() != null && !song.year().equals("")) {
                year.setText("Year: " + song.year());
            } else {
                year.setText("");
            }
            if (song.genre() != null && !song.genre().equals("")) {
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
    }

    public void switchBackButtonFunction() {
        player.switchSongBack();
        setSongLabels();
        repaint();
    }

    public void selectFileButton() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            player.getSongs(fileChooser.getSelectedFile().getAbsolutePath());
            repaint();
            setSongLabels();
            setFileNames();
        }
    }
}
