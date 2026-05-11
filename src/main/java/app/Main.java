package app; // Achte darauf, dass hier dein richtiges Package steht (z.B. app)

import ui.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // --- NEU: Modernes Design aktivieren ---
        try {
            // Setzt das Aussehen der App auf das Standard-Design deines Betriebssystems
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(); // Falls es nicht klappt, ignorieren wir es einfach
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