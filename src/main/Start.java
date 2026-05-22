package main;
import controller.PlattformManager;
import view.MainFrame;
import javax.swing.SwingUtilities;

public class Start {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlattformManager manager = new PlattformManager();
            // manager.ladeSystemDaten(); // Später aktivieren
            
            MainFrame gui = new MainFrame(manager);
            gui.setVisible(true);
            gui.zeigeLoginView();
        });
    }
}
