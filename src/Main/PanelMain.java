package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class PanelMain extends JPanel implements Runnable {
    private Thread thread;
    private final SongPlayer songPlayer;
    private final JLabel
            metadataTitleLabel = new JLabel(),
            metadataCreatorLabel = new JLabel(),
            metadataAlbumLabel = new JLabel(),
            metadataYearLabel = new JLabel(),
            metadataGenreLabel = new JLabel(),
            timeLabel = new JLabel(),
            imageLabel = new JLabel();

    private final JPanel fileLabelPanel = new JPanel();
    private final JSlider timeSlider = new JSlider(0, 0), volumeSlider;
    private final JButton pauseButton = new JButton("Pause");
    public BufferedImage defaultCoverImage;
    private int currentVolume = 0;
    private String currentFilePath = "";

    public PanelMain() {
        this.setPreferredSize(new Dimension(600, 500));
        this.setFocusable(true);
        songPlayer = new SongPlayer();
        volumeSlider = new JSlider(JSlider.VERTICAL, 0, 100, 50);
        getResource();
        setComponents();
        Config.load(this);
        startThread();
    }

    public void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    private void getResource() {
        try {
            defaultCoverImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("musicCoverDefault.png")));
        } catch (IOException e) {
            Main.openDialogWindow("Cannot load resource.");
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
                timeSlider.setValue(songPlayer.getCurrentSongTime());
                if (songPlayer.isReady()) {
                    timeLabel.setText(timeDisplayConversion(timeSlider.getValue()) + "   " + timeDisplayConversion(songPlayer.getSongLength()));
                }
            }
        }
    }

    public void setComponents() {
        this.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        JPanel fileControlPanel = new JPanel();
        JPanel leftPanel = new JPanel();
        JPanel imagePanel = new JPanel();
        JPanel labelPanel = new JPanel();

        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        fileControlPanel.setLayout(new BoxLayout(fileControlPanel, BoxLayout.X_AXIS));
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
        fileLabelPanel.setLayout(new BoxLayout(fileLabelPanel, BoxLayout.PAGE_AXIS));

        labelPanel.setPreferredSize(new Dimension(170, 100));

        ImageIcon icon = new ImageIcon(defaultCoverImage);
        Image scaledImage = icon.getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT);
        imageLabel.setIcon(new ImageIcon(scaledImage));

        timeSlider.addChangeListener(e -> {
            if (!timeSlider.getValueIsAdjusting()) {
                if (timeSlider.getValue() != songPlayer.getCurrentSongTime()) {
                    songPlayer.setTime(timeSlider.getValue());
                }
            }
        });

        imageLabel.setBorder(BorderFactory.createLineBorder(this.getBackground(), 10));
        fileLabelPanel.setBorder(BorderFactory.createLineBorder(this.getBackground(), 5));
        labelPanel.setBorder(BorderFactory.createLineBorder(this.getBackground(), 5));
        controlPanel.setBorder(BorderFactory.createLineBorder(this.getBackground(), 5));
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 5));

        JButton fileButton = new JButton("Select folder");
        fileButton.addActionListener(e -> selectFileButton());

        JButton switchButton = new JButton(">>>");
        switchButton.addActionListener(e -> switchNextButtonFunction());

        JButton switchBackButton = new JButton("<<<");
        switchBackButton.addActionListener(e -> switchBackButtonFunction());

        JButton playButton = new JButton("Play");
        playButton.addActionListener(e -> songPlayer.play());

        pauseButton.addActionListener(e -> pauseButtonFunction());

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> songPlayer.stop());

        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setMinorTickSpacing(10);
        volumeSlider.setMajorTickSpacing(50);
        volumeSlider.setPreferredSize(new Dimension(50, 100));
        volumeSlider.addChangeListener(e -> {
            if (!volumeSlider.getValueIsAdjusting()) {
                if (volumeSlider.getValue() != currentVolume && songPlayer.isReady()) {
                    songPlayer.setVolume(getVolume());
                    currentVolume = getVolume();
                    Config.save(currentVolume, currentFilePath);
                }
            }
        });

        labelPanel.add(metadataTitleLabel);
        labelPanel.add(metadataCreatorLabel);
        labelPanel.add(metadataAlbumLabel);
        labelPanel.add(metadataYearLabel);
        labelPanel.add(metadataGenreLabel);

        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(stopButton);

        fileControlPanel.add(fileButton);
        fileControlPanel.add(switchBackButton);
        fileControlPanel.add(switchButton);

        controlPanel.add(timeSlider);
        controlPanel.add(timeLabel);
        controlPanel.add(volumeSlider);
        imagePanel.add(imageLabel);
        leftPanel.add(fileLabelPanel);
        leftPanel.add(labelPanel);

        this.add(leftPanel, BorderLayout.LINE_START);
        this.add(fileControlPanel, BorderLayout.PAGE_START);
        this.add(controlPanel, BorderLayout.PAGE_END);
        this.add(imagePanel, BorderLayout.LINE_END);

        setCurrentSongLabels();
    }

    public void setCurrentSongLabels() {
        if (songPlayer.getCurrentSong() != null) {
            pauseButton.setText("Pause");
            timeSlider.setMaximum(songPlayer.getSongLength());
            Song song = songPlayer.getCurrentSong();
            if (song.title() != null && !song.title().isEmpty()) {
                metadataTitleLabel.setText("Title: " + song.title());
            } else {
                metadataTitleLabel.setText("");
            }
            if (song.creator() != null && !song.creator().isEmpty()) {
                metadataCreatorLabel.setText("Creator: " + song.creator());
            } else {
                metadataCreatorLabel.setText("");
            }
            if (song.album() != null && !song.album().isEmpty()) {
                metadataAlbumLabel.setText("Album: " + song.album());
            } else {
                metadataAlbumLabel.setText("");
            }
            if (song.year() != null && !song.year().isEmpty()) {
                metadataYearLabel.setText("Year: " + song.year());
            } else {
                metadataYearLabel.setText("");
            }
            if (song.genre() != null && !song.genre().isEmpty()) {
                metadataGenreLabel.setText("Genre: " + song.genre());
            } else {
                metadataGenreLabel.setText("");
            }

            if (song.albumImage() != null) {
                ImageIcon icon = new ImageIcon(song.albumImage());
                Image scaledImage = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                ImageIcon icon = new ImageIcon(defaultCoverImage);
                Image scaledImage = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            }
        }
    }

    public void setCurrentFileNames() {
        fileLabelPanel.removeAll();
        String[] labels;
        if (songPlayer.getFileManager().getSongNames() != null) {
            labels = songPlayer.getFileManager().getSongNames().toArray(new String[0]);
        } else {
            labels = new String[0];
        }

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(labelPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        fileLabelPanel.add(scrollPane);
        fileLabelPanel.setPreferredSize(new Dimension(170, 400));

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(i + 1 + ". " + labels[i]);
            labelPanel.add(label);
        }
    }

    public void switchNextButtonFunction() {
        songPlayer.switchSongNext();
        setCurrentSongLabels();
        timeSlider.setValue(0);
    }

    public void switchBackButtonFunction() {
        songPlayer.switchSongBack();
        setCurrentSongLabels();
        timeSlider.setValue(0);
    }

    public void selectFileButton() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            songPlayer.getSongs(currentFilePath);
            setCurrentSongLabels();
            setCurrentFileNames();
            Config.save(currentVolume, currentFilePath);
        }
    }

    public void pauseButtonFunction() {
        if (songPlayer.isReady()) {
            if (songPlayer.isPlaying()) {
                pauseButton.setText("Resume");
            } else {
                pauseButton.setText("Pause");
            }
            songPlayer.pauseOrPlay();
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
        songPlayer.setVolume(val);
    }

    public void setFolder(String path) {
        songPlayer.getSongs(path);
        currentFilePath = path;
        setCurrentSongLabels();
        setCurrentFileNames();
    }
}
