package view;

import controller.PlattformManager;
import javax.swing.*;

public class KundenDashboardView extends JPanel {
    private PlattformManager manager;
    private JTable tblParkplaetze;
    private JTextField txtSuchOrt;
    private JTextField txtVon;
    private JTextField txtBis;
    private JButton btnSuchen;
    private JButton btnBuchen;
    private JList listMeineBuchungen;

    public KundenDashboardView(PlattformManager pm) {
        this.manager = pm;
    }

    private void parkplaetzeSuchen() {
        // TODO: Implementierung
    }

    private void buchungAusfuehren() {
        // TODO: Implementierung
    }
}
