package Main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        startWindow();
    }

    public static void startWindow() {

        JFrame window = new JFrame();
        window.setTitle("Music Player");

        PanelMain panel = new PanelMain();
        window.setIconImage(panel.defaultCoverImage);
        window.add(panel);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setVisible(true);
        window.pack();
    }

    public static void dialogWindow(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}