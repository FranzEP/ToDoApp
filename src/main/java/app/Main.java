package app;

import ui.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // setzt die optik der app auf die der Systemeinstellaugen
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(); // wenn es nicht funktioniert wird es ignoriert
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}