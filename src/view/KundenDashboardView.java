package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.PlattformManager;
import model.Buchung;
import model.Parkplatz;

public class KundenDashboardView extends JPanel {
	private static final long serialVersionUID = 1L;
    private PlattformManager manager;
    private JTable tblParkplaetze;
    private JTextField txtSuchOrt;
    private JTextField txtVonDatum;
    private JTextField txtVonUhrzeit;
    private JTextField txtBisDatum;
    private JTextField txtBisUhrzeit;
    private JButton btnSuchen;
    private JButton btnBuchen;
    private JList<String> listMeineBuchungen;
    private JLabel lblPreis;

    public KundenDashboardView(PlattformManager pm) {
        this.manager = pm;

        setLayout(new BorderLayout(10, 10));

        JPanel oben = new JPanel(new GridLayout(2, 6, 5, 5));
        txtSuchOrt = new JTextField();

        txtVonDatum = new JTextField();
        txtVonUhrzeit = new JTextField();
        txtBisDatum = new JTextField();
        txtBisUhrzeit = new JTextField();

        btnSuchen = new JButton("Suchen");
        btnBuchen = new JButton("Buchen");
        lblPreis = new JLabel("Preis: -");

        oben.add(new JLabel("Ort:"));
        oben.add(new JLabel("Von Datum"));
        oben.add(new JLabel("Von Uhrzeit"));
        oben.add(new JLabel("Bis Datum"));
        oben.add(new JLabel("Bis Uhrzeit"));
        oben.add(new JLabel(""));

        oben.add(txtSuchOrt);
        oben.add(txtVonDatum);
        oben.add(txtVonUhrzeit);
        oben.add(txtBisDatum);
        oben.add(txtBisUhrzeit);
        oben.add(btnSuchen);

        add(oben, BorderLayout.NORTH);

        tblParkplaetze = new JTable();
        JScrollPane scrollParkplaetze = new JScrollPane(tblParkplaetze);
        add(scrollParkplaetze, BorderLayout.CENTER);

        JPanel unten = new JPanel();
        unten.setLayout(new BoxLayout(unten, BoxLayout.Y_AXIS));

        JPanel aktionenPanel = new JPanel(new BorderLayout());
        aktionenPanel.add(lblPreis, BorderLayout.WEST);
        aktionenPanel.add(btnBuchen, BorderLayout.EAST);

        listMeineBuchungen = new JList<>(new DefaultListModel<>());
        JScrollPane scrollBuchungen = new JScrollPane(listMeineBuchungen);
        scrollBuchungen.setPreferredSize(new Dimension(0, 120));

        unten.add(aktionenPanel);
        unten.add(Box.createVerticalStrut(8));
        unten.add(new JLabel("Meine Buchungen:"));
        unten.add(scrollBuchungen);

        add(unten, BorderLayout.SOUTH);

        btnSuchen.addActionListener(e -> parkplaetzeSuchen());
        btnBuchen.addActionListener(e -> buchungAusfuehren());

        tblParkplaetze.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preisAktualisieren();
            }
        });
        aktualisiereMeineBuchungenListe();
    }

    private void parkplaetzeSuchen() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Bezeichnung", "Adresse", "Kapazität", "Stundensatz"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String suchOrt = txtSuchOrt.getText().trim().toLowerCase();

        for (Parkplatz p : manager.getAlleParkplaetze()) {
            if (suchOrt.isEmpty() || p.getAdresse().toLowerCase().contains(suchOrt)) {
                model.addRow(new Object[]{
                        p.getId(),
                        p.getBezeichnung(),
                        p.getAdresse(),
                        p.getGesamtKapazitaet(),
                        p.getStundenSatz()
                });
            }
        }

        tblParkplaetze.setModel(model);
    }

    private void preisAktualisieren() {
        try {
            int zeile = tblParkplaetze.getSelectedRow();

            if (zeile == -1) {
                lblPreis.setText("Preis: -");
                return;
            }

            String parkplatzId = tblParkplaetze.getValueAt(zeile, 0).toString();
            Parkplatz ausgewaehlterParkplatz = findeParkplatzNachId(parkplatzId);

            if (ausgewaehlterParkplatz == null) {
                lblPreis.setText("Preis: -");
                return;
            }

            LocalDateTime von = leseVonZeitpunkt();
            LocalDateTime bis = leseBisZeitpunkt();

            double preis = manager.berechnePreis(ausgewaehlterParkplatz, von, bis);
            lblPreis.setText(String.format("Preis: %.2f €", preis));

        } catch (DateTimeParseException e) {
            lblPreis.setText("Preis: Bitte Datum/Uhrzeit eingeben");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fehler bei der Preisberechnung.");
        }
    }

    private Parkplatz findeParkplatzNachId(String id) {
        for (Parkplatz p : manager.getAlleParkplaetze()) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    private LocalDateTime leseVonZeitpunkt() {
        DateTimeFormatter datumFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter uhrzeitFormat = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate datum = LocalDate.parse(txtVonDatum.getText().trim(), datumFormat);
        LocalTime uhrzeit = LocalTime.parse(txtVonUhrzeit.getText().trim(), uhrzeitFormat);

        return LocalDateTime.of(datum, uhrzeit);
    }

    private LocalDateTime leseBisZeitpunkt() {
        DateTimeFormatter datumFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter uhrzeitFormat = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate datum = LocalDate.parse(txtBisDatum.getText().trim(), datumFormat);
        LocalTime uhrzeit = LocalTime.parse(txtBisUhrzeit.getText().trim(), uhrzeitFormat);

        return LocalDateTime.of(datum, uhrzeit);
    }

    private void buchungAusfuehren() {
        try {
            int zeile = tblParkplaetze.getSelectedRow();

            if (zeile == -1) {
                JOptionPane.showMessageDialog(this, "Bitte wähle zuerst einen Parkplatz aus.");
                return;
            }

            String parkplatzId = tblParkplaetze.getValueAt(zeile, 0).toString();
            Parkplatz ausgewaehlterParkplatz = findeParkplatzNachId(parkplatzId);

            if (ausgewaehlterParkplatz == null) {
                JOptionPane.showMessageDialog(this, "Der ausgewählte Parkplatz wurde nicht gefunden.");
                return;
            }

            LocalDateTime von = leseVonZeitpunkt();
            LocalDateTime bis = leseBisZeitpunkt();

            if (!bis.isAfter(von)) {
                JOptionPane.showMessageDialog(this, "Die Endzeit muss nach der Startzeit liegen.");
                return;
            }

            Buchung neueBuchung = manager.bucheParkplatz(ausgewaehlterParkplatz, von, bis);

            if (neueBuchung == null) {
                JOptionPane.showMessageDialog(this, "Buchung konnte nicht durchgeführt werden.");
                return;
            }

            aktualisiereMeineBuchungenListe();

            JOptionPane.showMessageDialog(this, "Buchung erfolgreich gespeichert.");
            preisAktualisieren();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Bitte gib das Datum im Format dd.MM.yyyy und die Uhrzeit im Format HH:mm ein."
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fehler beim Buchen.");
        }
    }
    private void aktualisiereMeineBuchungenListe() {
        DefaultListModel<String> model = (DefaultListModel<String>) listMeineBuchungen.getModel();
        model.clear();

        if (manager.getAktuellerNutzer() instanceof model.Kunde) {
            model.Kunde kunde = (model.Kunde) manager.getAktuellerNutzer();

            for (Object obj : kunde.getMeineBuchungen()) {
                Buchung b = (Buchung) obj;
                model.addElement(
                        b.getBuchungsCode()
                        + " | " + b.getParkplatz().getBezeichnung()
                        + " | " + b.getVon().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                        + " bis " + b.getBis().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                );
            }
        }
    }
}