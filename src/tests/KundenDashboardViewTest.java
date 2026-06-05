package tests;

import controller.PlattformManager;
import model.Buchung;
import model.Kunde;
import model.Parkplatz;
import view.KundenDashboardView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class KundenDashboardViewTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlattformManager manager = new PlattformManager();

            manager.getAlleParkplaetze().add(new Parkplatz(
                    "P1",
                    "Parkhaus Mitte",
                    "Berlin Mitte",
                    50,
                    2.5
            ));

            manager.getAlleParkplaetze().add(new Parkplatz(
                    "P2",
                    "Garage Alex",
                    "Berlin Alexanderplatz",
                    30,
                    3.0
            ));

            manager.getAlleParkplaetze().add(new Parkplatz(
                    "P3",
                    "City Parking",
                    "Berlin Charlottenburg",
                    80,
                    1.8
            ));

            Kunde testKunde = new Kunde(
                    "K1",
                    "Max Mustermann",
                    "max@test.de"
            );

            manager.addNutzer(testKunde);

            String email = JOptionPane.showInputDialog(
                    null,
                    "Bitte E-Mail zum Login eingeben:",
                    "Login",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (email == null || email.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Login abgebrochen."
                );
                return;
            }

            boolean erfolgreich = manager.login(email.trim());

            if (!erfolgreich) {
                JOptionPane.showMessageDialog(
                        null,
                        "Login fehlgeschlagen."
                );
                return;
            }

            JFrame dashboardFrame = new JFrame("KundenDashboard Test");
            dashboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dashboardFrame.setContentPane(new KundenDashboardView(manager));
            dashboardFrame.setSize(900, 600);
            dashboardFrame.setLocationRelativeTo(null);

            dashboardFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    zeigeGebuchteParkplaetze(manager);
                }
            });

            dashboardFrame.setVisible(true);
        });
    }

    private static void zeigeGebuchteParkplaetze(PlattformManager manager) {
        JFrame frame = new JFrame("Kundenansicht Test - Gebuchte Parkplätze");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 500);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(245, 247, 255));

        JLabel titel = new JLabel("Gebuchte Parkplätze des Kunden");
        titel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titel.setForeground(new Color(30, 41, 59));

        String kundenName = manager.getAktuellerNutzer() != null
                ? manager.getAktuellerNutzer().getName()
                : "Unbekannt";

        JLabel untertitel = new JLabel("Aktueller Kunde: " + kundenName);
        untertitel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        untertitel.setForeground(new Color(100, 116, 139));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.add(titel);
        header.add(Box.createVerticalStrut(4));
        header.add(untertitel);

        String[] spalten = {
                "Buchungscode", "Parkplatz", "Adresse", "Von", "Bis"
        };

        DefaultTableModel model = new DefaultTableModel(spalten, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (Buchung buchung : manager.getAlleBuchungen()) {
            if (buchung.getKunde() != null
                    && manager.getAktuellerNutzer() != null
                    && buchung.getKunde().getId().equals(manager.getAktuellerNutzer().getId())) {

                Parkplatz p = buchung.getParkplatz();

                model.addRow(new Object[]{
                        buchung.getBuchungsCode(),
                        p != null ? p.getBezeichnung() : "-",
                        p != null ? p.getAdresse() : "-",
                        		buchung.getVon().format(formatter),
                        		buchung.getBis().format(formatter)
                });
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 226, 236)));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        if (model.getRowCount() == 0) {
            JLabel leer = new JLabel("Noch keine gebuchten Parkplätze vorhanden.");
            leer.setHorizontalAlignment(SwingConstants.CENTER);
            leer.setFont(new Font("SansSerif", Font.PLAIN, 15));
            leer.setForeground(new Color(100, 116, 139));
            center.add(leer, BorderLayout.CENTER);
        } else {
            center.add(scrollPane, BorderLayout.CENTER);
        }

        JButton btnBeenden = new JButton("Test beenden");
        btnBeenden.addActionListener(e -> frame.dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(btnBeenden);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }
}