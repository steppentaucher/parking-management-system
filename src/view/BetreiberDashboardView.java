package view;

import controller.PlattformManager;
import javax.swing.*;

public class BetreiberDashboardView extends JPanel {
    private PlattformManager manager;
    private JTable tblEigeneParkplaetze;
    private JTable tblBelegungsPlan;
    private JTextField txtName;
    private JTextField txtKapazitaet;
    private JTextField txtPreis;
    private JButton btnParkplatzAnlegen;

    public BetreiberDashboardView(PlattformManager pm) {
        this.manager = pm;
    }

    private void neuerParkplatz() {
        // TODO: Implementierung
    }

    private void aktualisiereDashboard() {
        // TODO: Implementierung
    }
}
