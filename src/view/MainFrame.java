package view;

import controller.PlattformManager;
import javax.swing.*;

public class MainFrame extends JFrame {
    private PlattformManager manager;
    private JPanel aktuellesPanel;

    public MainFrame(PlattformManager manager) {
        this.manager = manager;
        setTitle("BPM - Parkplatz Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void zeigeLoginView() {
        // TODO: Implementierung
    }

    public void zeigeKundenView() {
        // TODO: Implementierung
    }

    public void zeigeBetreiberView() {
        // TODO: Implementierung
    }
}
