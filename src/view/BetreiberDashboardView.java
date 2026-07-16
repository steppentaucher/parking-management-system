package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import controller.PlattformManager;
import model.Betreiber;
import model.Parkplatz;

public class BetreiberDashboardView extends JPanel {

    private Betreiber aktuellerBetreiber;
    private PlattformManager manager;
    private CardLayout seitenUmschalter;
    private JPanel seitenContainer;

    private JTable tabelleEigeneParkplaetze;
    private JTable tabelleBelegungsPlan;
    private DefaultTableModel modellParkplaetze;
    private DefaultTableModel modellBelegung;
    private JButton buttonAlleBuchungenAnzeigen;
    private JButton buttonLogout;
    private JLabel formularTitelLabel;
    private ParkplatzFormularKomponente formularKomponente;

    private final Color HINTERGRUND_FARBE = new Color(245, 247, 250);
    private final Color KARTEN_HINTERGRUND = Color.WHITE;
    private final Color AKZENT_FARBE = new Color(37, 99, 235);
    private final Color TEXT_MAIN = new Color(30, 41, 59);
    private final Color RAHMEN_FARBE = new Color(226, 232, 240);

    public BetreiberDashboardView(Betreiber betreiber, PlattformManager pm) {
        this.aktuellerBetreiber = betreiber;
        this.manager = pm;

        this.seitenUmschalter = new CardLayout();
        this.seitenContainer = new JPanel(seitenUmschalter);
        this.seitenContainer.setOpaque(false);

        JPanel uebersichtsSeite = erstelleUebersichtsSeite();
        JPanel formularSeite = erstelleFormularSeite();

        this.seitenContainer.add(uebersichtsSeite, "UEBERSICHT");
        this.seitenContainer.add(formularSeite, "FORMULAR");

        setBackground(HINTERGRUND_FARBE);
        setLayout(new BorderLayout());
        add(seitenContainer, BorderLayout.CENTER);

        aktualisiereDashboard();
    }

    private JPanel erstelleUebersichtsSeite() {
        JPanel seite = new JPanel(new BorderLayout(20, 20));
        seite.setOpaque(false);
        seite.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel kopfBereich = new JPanel(new BorderLayout());
        kopfBereich.setOpaque(false);

        JLabel titelLabel = new JLabel("Betreiber Dashboard");
        titelLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titelLabel.setForeground(TEXT_MAIN);

        JLabel unterTitelLabel = new JLabel("Verwalten Sie Ihre Parkflächen und analysieren Sie die Belegung.");
        unterTitelLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        unterTitelLabel.setForeground(new Color(100, 116, 139));

        JPanel textGruppe = new JPanel(new GridLayout(2, 1, 4, 4));
        textGruppe.setOpaque(false);
        textGruppe.add(titelLabel);
        textGruppe.add(unterTitelLabel);
        kopfBereich.add(textGruppe, BorderLayout.WEST);

        JButton buttonParkplatzAnlegenOeffnen = new JButton("+ Neuen Parkplatz anlegen");
        buttonParkplatzAnlegenOeffnen.setBackground(AKZENT_FARBE);
        buttonParkplatzAnlegenOeffnen.setForeground(Color.WHITE);
        buttonParkplatzAnlegenOeffnen.setFont(new Font("Arial", Font.BOLD, 14));
        buttonParkplatzAnlegenOeffnen.setFocusPainted(false);
        buttonParkplatzAnlegenOeffnen.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        buttonParkplatzAnlegenOeffnen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonParkplatzAnlegenOeffnen.addActionListener(e -> {
            formularKomponente.zeigeAnlegenModus();
            seitenUmschalter.show(seitenContainer, "FORMULAR");
        });

        buttonLogout = new JButton("Logout");
        buttonLogout.setBackground(AKZENT_FARBE);
        buttonLogout.setForeground(Color.WHITE);
        buttonLogout.setFont(new Font("Arial", Font.BOLD, 14));
        buttonLogout.setFocusPainted(false);
        buttonLogout.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        buttonLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonLogout.addActionListener(e -> logout());

        JPanel buttonPositionierung = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPositionierung.setOpaque(false);
        buttonPositionierung.add(buttonLogout);
        buttonPositionierung.add(buttonParkplatzAnlegenOeffnen);
        kopfBereich.add(buttonPositionierung, BorderLayout.EAST);

        seite.add(kopfBereich, BorderLayout.NORTH);

        JPanel tabellenBereich = new JPanel(new GridLayout(2, 1, 0, 20));
        tabellenBereich.setOpaque(false);

        JPanel karteParkplaetze = erstelleKartenPanel("Meine Parkplätze (Stammdaten)");
        String[] spaltenParkplaetze = { "ID", "Bezeichnung", "Adresse", "Kapazität", "Stundensatz", "Wochenendsatz", "Features" };
        modellParkplaetze = new DefaultTableModel(spaltenParkplaetze, 0) {
            @Override
            public boolean isCellEditable(int zeile, int spalte) {
                return false;
            }
        };
        tabelleEigeneParkplaetze = erstelleModerneTabelle(modellParkplaetze);

        tabelleEigeneParkplaetze.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                filtereBelegungNachAuswahl();
            }
        });
        
        // Doppelklick auf eine Zeile oeffnet den Parkplatz zum Bearbeiten
        tabelleEigeneParkplaetze.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    oeffneBearbeitenFuerAuswahl();
                }
            }
        });

        buttonAlleBuchungenAnzeigen = new JButton("Alle Buchungen anzeigen");
        buttonAlleBuchungenAnzeigen.setFont(new Font("Arial", Font.BOLD, 12));
        buttonAlleBuchungenAnzeigen.setForeground(AKZENT_FARBE);
        buttonAlleBuchungenAnzeigen.setContentAreaFilled(false);
        buttonAlleBuchungenAnzeigen.setBorderPainted(false);
        buttonAlleBuchungenAnzeigen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonAlleBuchungenAnzeigen.addActionListener(e -> {
            tabelleEigeneParkplaetze.clearSelection();
            befuelleBelegungsTabelle(null);
        });

        JPanel kartenKopf = new JPanel(new BorderLayout());
        kartenKopf.setOpaque(false);
        kartenKopf.add(buttonAlleBuchungenAnzeigen, BorderLayout.EAST);
        karteParkplaetze.add(kartenKopf, BorderLayout.NORTH);
        karteParkplaetze.add(new JScrollPane(tabelleEigeneParkplaetze), BorderLayout.CENTER);

        JPanel karteBelegung = erstelleKartenPanel("Aktuelle & Zukünftige Belegung");
        String[] spaltenBelegung = { "Buchungs-ID", "Parkplatz-ID", "Von (Datum/Zeit)", "Bis (Datum/Zeit)", "Umsatz (€)" };
        modellBelegung = new DefaultTableModel(spaltenBelegung, 0) {
            @Override
            public boolean isCellEditable(int zeile, int spalte) {
                return false;
            }
        };
        tabelleBelegungsPlan = erstelleModerneTabelle(modellBelegung);
        karteBelegung.add(new JScrollPane(tabelleBelegungsPlan), BorderLayout.CENTER);

        tabellenBereich.add(karteParkplaetze);
        tabellenBereich.add(karteBelegung);
        seite.add(tabellenBereich, BorderLayout.CENTER);

        return seite;
    }

    private JPanel erstelleFormularSeite() {
        JPanel seite = new JPanel(new BorderLayout(20, 20));
        seite.setOpaque(false);
        seite.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel kopfBereich = new JPanel(new BorderLayout());
        kopfBereich.setOpaque(false);

        formularTitelLabel = new JLabel("Neuen Parkplatz registrieren");
        formularTitelLabel.setFont(new Font("Arial", Font.BOLD, 26));
        formularTitelLabel.setForeground(TEXT_MAIN);
        kopfBereich.add(formularTitelLabel, BorderLayout.WEST);

        JButton buttonZurueck = new JButton("← Zurück zur Übersicht");
        buttonZurueck.setFont(new Font("Arial", Font.BOLD, 14));
        buttonZurueck.setForeground(new Color(100, 116, 139));
        buttonZurueck.setContentAreaFilled(false);
        buttonZurueck.setBorderPainted(false);
        buttonZurueck.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonZurueck.addActionListener(e -> seitenUmschalter.show(seitenContainer, "UEBERSICHT"));
        kopfBereich.add(buttonZurueck, BorderLayout.EAST);

        seite.add(kopfBereich, BorderLayout.NORTH);

        JPanel zentrierungsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        zentrierungsWrapper.setOpaque(false);

        formularKomponente = new ParkplatzFormularKomponente();
        zentrierungsWrapper.add(formularKomponente);

        seite.add(zentrierungsWrapper, BorderLayout.CENTER);

        return seite;
    }

    private void filtereBelegungNachAuswahl() {
        int ausgewaehlteZeile = tabelleEigeneParkplaetze.getSelectedRow();
        if (ausgewaehlteZeile >= 0) {
            String parkplatzId = tabelleEigeneParkplaetze.getValueAt(ausgewaehlteZeile, 0).toString();
            befuelleBelegungsTabelle(parkplatzId);
        }
    }
    
    // Oeffnet das Formular im Bearbeiten-Modus fuer den doppelt angeklickten Parkplatz
    private void oeffneBearbeitenFuerAuswahl() {
        int zeile = tabelleEigeneParkplaetze.getSelectedRow();
        if (zeile < 0) {
            return;
        }

        String parkplatzId = tabelleEigeneParkplaetze.getValueAt(zeile, 0).toString();

        for (Parkplatz p : aktuellerBetreiber.getMeineParkplaetze()) {
            if (p.getId().equals(parkplatzId)) {
                formularKomponente.zeigeBearbeitenModus(p);
                seitenUmschalter.show(seitenContainer, "FORMULAR");
                return;
            }
        }
    }

    private void logout() {
        manager.logout();
        java.awt.Window fenster = SwingUtilities.getWindowAncestor(this);
        if (fenster instanceof MainFrame) {
            ((MainFrame) fenster).zeigeLoginView();
        }
    }

    public void aktualisiereDashboard() {
        modellParkplaetze.setRowCount(0);
        if (aktuellerBetreiber.getMeineParkplaetze() != null) {
            for (Parkplatz p : aktuellerBetreiber.getMeineParkplaetze()) {
                modellParkplaetze.addRow(new Object[] {
                        p.getId(),
                        p.getBezeichnung(),
                        p.getAdresse(),
                        p.getGesamtKapazitaet(),
                        p.getStundenSatz(),
                        p.getSonderSatz(),
                        p.getFeaturesAlsText()
                });
            }
        }
        befuelleBelegungsTabelle(null);
    }

    private void befuelleBelegungsTabelle(String filterParkplatzId) {
        modellBelegung.setRowCount(0);

        if (aktuellerBetreiber.getMeineParkplaetze() != null && !aktuellerBetreiber.getMeineParkplaetze().isEmpty()) {
            java.util.List<String> meineParkplatzIds = new java.util.ArrayList<>();
            for (Parkplatz p : aktuellerBetreiber.getMeineParkplaetze()) {
                meineParkplatzIds.add(p.getId());
            }

            for (model.Buchung buchung : manager.getAlleBuchungen()) {
                if (buchung.getParkplatz() != null && meineParkplatzIds.contains(buchung.getParkplatz().getId())) {
                    if (filterParkplatzId != null && !buchung.getParkplatz().getId().equals(filterParkplatzId)) {
                        continue;
                    }

                    modellBelegung.addRow(new Object[] {
                            buchung.getBuchungsCode(),
                            buchung.getParkplatz().getBezeichnung(),
                            buchung.getVon(),
                            buchung.getBis(),
                            buchung.getPreisAufschluesselung()
                    });
                }
            }
        }
    }

    private JPanel erstelleKartenPanel(String kartenTitel) {
        JPanel karte = new JPanel(new BorderLayout(10, 10));
        karte.setBackground(KARTEN_HINTERGRUND);
        karte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RAHMEN_FARBE, 1, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel titelLabel = new JLabel(kartenTitel);
        titelLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titelLabel.setForeground(TEXT_MAIN);
        karte.add(titelLabel, BorderLayout.WEST);

        return karte;
    }

    private JTable erstelleModerneTabelle(DefaultTableModel modell) {
        JTable tabelle = new JTable(modell) {
            @Override
            public String getToolTipText(java.awt.event.MouseEvent event) {
                int zeile = rowAtPoint(event.getPoint());
                int spalte = columnAtPoint(event.getPoint());
                if (zeile > -1 && spalte > -1) {
                    Object wert = getValueAt(zeile, spalte);
                    return wert != null ? wert.toString() : null;
                }
                return super.getToolTipText(event);
            }
        };

        tabelle.setFont(new Font("Arial", Font.PLAIN, 13));
        tabelle.setRowHeight(35);
        tabelle.setGridColor(RAHMEN_FARBE);
        tabelle.setShowVerticalLines(false);
        tabelle.setSelectionBackground(new Color(239, 246, 255));
        tabelle.setSelectionForeground(AKZENT_FARBE);

        JTableHeader kopfZeile = tabelle.getTableHeader();
        kopfZeile.setFont(new Font("Arial", Font.BOLD, 13));
        kopfZeile.setBackground(HINTERGRUND_FARBE);
        kopfZeile.setForeground(new Color(71, 85, 105));
        kopfZeile.setReorderingAllowed(false);
        kopfZeile.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, RAHMEN_FARBE));

        return tabelle;
    }

    private class ParkplatzFormularKomponente extends JPanel {

        private JTextField eingabeBezeichnung;
        private JTextField eingabeAdresse;
        private JTextField eingabeKapazitaet;
        private JTextField eingabeStundensatz;
        private JTextField eingabeSondersatz;

        private JCheckBox chkELaden;
        private JCheckBox chkUeberdacht;
        private JCheckBox chkBehindertengerecht;
        private JCheckBox chkVideoUeberwacht;

        private JButton buttonSpeichern;
        
        private JButton buttonLoeschen;

        // Der Parkplatz, der gerade bearbeitet wird.
        // null bedeutet: das Formular ist im Anlege-Modus.
        private Parkplatz zuBearbeitenderParkplatz;

        public ParkplatzFormularKomponente() {
            setBackground(KARTEN_HINTERGRUND);
            setLayout(new BorderLayout(15, 15));
            setPreferredSize(new Dimension(460, 620));
            setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

            eingabeBezeichnung = erstelleModernesEingabefeld();
            eingabeAdresse = erstelleModernesEingabefeld();
            eingabeKapazitaet = erstelleModernesEingabefeld();
            eingabeStundensatz = erstelleModernesEingabefeld();
            eingabeSondersatz = erstelleModernesEingabefeld();

            chkELaden = erstelleFeatureCheckbox("E-Laden");
            chkUeberdacht = erstelleFeatureCheckbox("Überdacht");
            chkBehindertengerecht = erstelleFeatureCheckbox("Behindertengerecht");
            chkVideoUeberwacht = erstelleFeatureCheckbox("Videoüberwacht");

            JPanel felderPanel = new JPanel(new GridBagLayout());
            felderPanel.setBackground(KARTEN_HINTERGRUND);
            GridBagConstraints einschränkungen = new GridBagConstraints();
            einschränkungen.fill = GridBagConstraints.HORIZONTAL;
            einschränkungen.weightx = 1.0;

            fuegeFormularZeileHinzu(felderPanel, "Bezeichnung des Parkplatzes", eingabeBezeichnung, einschränkungen, 0);
            fuegeFormularZeileHinzu(felderPanel, "Adresse / Standort", eingabeAdresse, einschränkungen, 1);
            fuegeFormularZeileHinzu(felderPanel, "Maximale Kapazität (Stellplätze)", eingabeKapazitaet, einschränkungen, 2);
            fuegeFormularZeileHinzu(felderPanel, "Standard-Stundensatz (€)", eingabeStundensatz, einschränkungen, 3);
            fuegeFormularZeileHinzu(felderPanel, "Wochenend- / Sondersatz (€)", eingabeSondersatz, einschränkungen, 4);

            JLabel labelFeatures = new JLabel("Features / Ausstattung");
            labelFeatures.setFont(new Font("Arial", Font.BOLD, 12));
            labelFeatures.setForeground(new Color(100, 116, 139));

            einschränkungen.gridy = 10;
            einschränkungen.insets = new Insets(6, 0, 2, 0);
            felderPanel.add(labelFeatures, einschränkungen);

            JPanel featurePanel = new JPanel(new GridBagLayout());
            featurePanel.setOpaque(false);

            GridBagConstraints fgbc = new GridBagConstraints();
            fgbc.anchor = GridBagConstraints.WEST;
            fgbc.insets = new Insets(4, 4, 4, 12);
            fgbc.gridx = 0;
            fgbc.gridy = 0;
            featurePanel.add(chkELaden, fgbc);

            fgbc.gridx = 1;
            featurePanel.add(chkUeberdacht, fgbc);

            fgbc.gridx = 0;
            fgbc.gridy = 1;
            featurePanel.add(chkBehindertengerecht, fgbc);

            fgbc.gridx = 1;
            featurePanel.add(chkVideoUeberwacht, fgbc);

            einschränkungen.gridy = 11;
            einschränkungen.insets = new Insets(0, 0, 10, 0);
            felderPanel.add(featurePanel, einschränkungen);

            add(felderPanel, BorderLayout.CENTER);

            buttonSpeichern = new JButton("Parkfläche speichern");
            buttonSpeichern.setBackground(AKZENT_FARBE);
            buttonSpeichern.setForeground(Color.WHITE);
            buttonSpeichern.setFont(new Font("Arial", Font.BOLD, 14));
            buttonSpeichern.setFocusPainted(false);
            buttonSpeichern.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
            buttonSpeichern.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            buttonSpeichern.addActionListener(e -> verarbeiteSpeicherung());

            // Loeschen-Button (nur im Bearbeiten-Modus sichtbar)
            buttonLoeschen = new JButton("Parkplatz löschen");
            buttonLoeschen.setBackground(new Color(220, 38, 38));
            buttonLoeschen.setForeground(Color.WHITE);
            buttonLoeschen.setFont(new Font("Arial", Font.BOLD, 14));
            buttonLoeschen.setFocusPainted(false);
            buttonLoeschen.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
            buttonLoeschen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            buttonLoeschen.addActionListener(e -> verarbeiteLoeschen());
            buttonLoeschen.setVisible(false);

            JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 8));
            buttonPanel.setOpaque(false);
            buttonPanel.add(buttonSpeichern);
            buttonPanel.add(buttonLoeschen);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        // Setzt das Formular in den Anlege-Modus zurueck (leere Felder)
        public void zeigeAnlegenModus() {
            zuBearbeitenderParkplatz = null;
            formularTitelLabel.setText("Neuen Parkplatz registrieren");
            buttonSpeichern.setText("Parkfläche speichern");
            buttonLoeschen.setVisible(false);

            eingabeBezeichnung.setText("");
            eingabeAdresse.setText("");
            eingabeKapazitaet.setText("");
            eingabeStundensatz.setText("");
            eingabeSondersatz.setText("");
            chkELaden.setSelected(false);
            chkUeberdacht.setSelected(false);
            chkBehindertengerecht.setSelected(false);
            chkVideoUeberwacht.setSelected(false);
        }

        // Befuellt das Formular mit den Werten des gewaehlten Parkplatzes
        public void zeigeBearbeitenModus(Parkplatz p) {
            zuBearbeitenderParkplatz = p;
            formularTitelLabel.setText("Parkplatz bearbeiten");
            buttonSpeichern.setText("Änderungen speichern");
            buttonLoeschen.setVisible(true);

            eingabeBezeichnung.setText(p.getBezeichnung());
            eingabeAdresse.setText(p.getAdresse());
            eingabeKapazitaet.setText(String.valueOf(p.getGesamtKapazitaet()));
            eingabeStundensatz.setText(String.valueOf(p.getStundenSatz()));
            eingabeSondersatz.setText(String.valueOf(p.getSonderSatz()));
            chkELaden.setSelected(p.hatFeature("E-Laden"));
            chkUeberdacht.setSelected(p.hatFeature("Überdacht"));
            chkBehindertengerecht.setSelected(p.hatFeature("Behindertengerecht"));
            chkVideoUeberwacht.setSelected(p.hatFeature("Videoüberwacht"));
        }
        
        private JCheckBox erstelleFeatureCheckbox(String text) {
            JCheckBox checkBox = new JCheckBox(text);
            checkBox.setOpaque(false);
            checkBox.setFont(new Font("Arial", Font.PLAIN, 13));
            checkBox.setForeground(TEXT_MAIN);
            checkBox.setFocusPainted(false);
            return checkBox;
        }

        private void fuegeFormularZeileHinzu(JPanel panel, String beschriftungText, JTextField feld,
                                             GridBagConstraints gbc, int reihe) {
            JLabel beschriftung = new JLabel(beschriftungText);
            beschriftung.setFont(new Font("Arial", Font.BOLD, 12));
            beschriftung.setForeground(new Color(100, 116, 139));

            gbc.gridy = reihe * 2;
            gbc.insets = new Insets(6, 0, 2, 0);
            panel.add(beschriftung, gbc);

            gbc.gridy = (reihe * 2) + 1;
            gbc.insets = new Insets(0, 0, 10, 0);
            panel.add(feld, gbc);
        }

        private JTextField erstelleModernesEingabefeld() {
            JTextField textFeld = new JTextField();
            textFeld.setFont(new Font("Arial", Font.PLAIN, 14));
            textFeld.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(RAHMEN_FARBE, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            return textFeld;
        }

        private List<String> leseAusgewaehlteFeatures() {
            List<String> features = new ArrayList<>();

            if (chkELaden.isSelected()) {
                features.add("E-Laden");
            }
            if (chkUeberdacht.isSelected()) {
                features.add("Überdacht");
            }
            if (chkBehindertengerecht.isSelected()) {
                features.add("Behindertengerecht");
            }
            if (chkVideoUeberwacht.isSelected()) {
                features.add("Videoüberwacht");
            }

            return features;
        }

        private void verarbeiteSpeicherung() {
            try {
                String bezeichnung = eingabeBezeichnung.getText().trim();
                String adresse = eingabeAdresse.getText().trim();
                int kapazitaet = Integer.parseInt(eingabeKapazitaet.getText().trim());
                double stundenSatz = Double.parseDouble(eingabeStundensatz.getText().trim());
                double sonderSatz = Double.parseDouble(eingabeSondersatz.getText().trim());
                List<String> features = leseAusgewaehlteFeatures();

                if (zuBearbeitenderParkplatz == null) {
                    // Anlege-Modus: neuen Parkplatz erzeugen
                    String generierteId = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                    Parkplatz neuerParkplatz = new Parkplatz(
                            generierteId,
                            bezeichnung,
                            adresse,
                            kapazitaet,
                            stundenSatz,
                            sonderSatz,
                            features
                    );

                    manager.parkplatzAnlegen(neuerParkplatz);
                    JOptionPane.showMessageDialog(this, "Parkplatz erfolgreich registriert.", "Erfolg",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Bearbeiten-Modus: bestehenden Parkplatz aktualisieren
                    manager.parkplatzBearbeiten(zuBearbeitenderParkplatz, bezeichnung, adresse,
                            kapazitaet, stundenSatz, sonderSatz, features);
                    JOptionPane.showMessageDialog(this, "Änderungen wurden gespeichert.", "Erfolg",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                zeigeAnlegenModus();
                aktualisiereDashboard();
                seitenUmschalter.show(seitenContainer, "UEBERSICHT");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Bitte geben Sie in den Zahlenfeldern gültige Werte ein (z.B. 50 oder 2.50).",
                        "Eingabefehler",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.WARNING_MESSAGE);
            }
        }

        // Loescht den aktuell bearbeiteten Parkplatz nach einer Sicherheitsabfrage
        private void verarbeiteLoeschen() {
            if (zuBearbeitenderParkplatz == null) {
                return;
            }

            int antwort = JOptionPane.showConfirmDialog(
                    this,
                    "Bist du sicher, dass du den Parkplatz \""
                            + zuBearbeitenderParkplatz.getBezeichnung() + "\" löschen möchtest?",
                    "Parkplatz löschen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (antwort != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                manager.parkplatzLoeschen(zuBearbeitenderParkplatz);

                JOptionPane.showMessageDialog(this, "Parkplatz wurde gelöscht.", "Erfolg",
                        JOptionPane.INFORMATION_MESSAGE);

                zeigeAnlegenModus();
                aktualisiereDashboard();
                seitenUmschalter.show(seitenContainer, "UEBERSICHT");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}