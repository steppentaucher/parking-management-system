package view;

import controller.PlattformManager;
import model.Betreiber;
import model.Parkplatz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class BetreiberDashboardView extends JPanel {

	private Betreiber aktuellerBetreiber;
	private PlattformManager manager;
	private CardLayout seitenUmschalter;
	private JPanel seitenContainer;

	// Komponenten für die Übersichtsseite
	private JTable tabelleEigeneParkplaetze;
	private JTable tabelleBelegungsPlan;
	private DefaultTableModel modellParkplaetze;
	private DefaultTableModel modellBelegung;
	private JButton buttonAlleBuchungenAnzeigen;
	private JButton buttonLogout;

	// Farbpalette für ein modernes, flaches Design
	private final Color HINTERGRUND_FARBE = new Color(245, 247, 250);
	private final Color KARTEN_HINTERGRUND = Color.WHITE;
	private final Color AKZENT_FARBE = new Color(37, 99, 235); // Modernes Blau
	private final Color TEXT_MAIN = new Color(30, 41, 59);
	private final Color RAHMEN_FARBE = new Color(226, 232, 240);

	public BetreiberDashboardView(Betreiber betreiber, PlattformManager pm) {
		this.aktuellerBetreiber = betreiber;
		this.manager = pm;

		// Das CardLayout ermöglicht es uns, zwischen Übersicht und Formular hin- und
		// herzuwechseln
		this.seitenUmschalter = new CardLayout();
		this.seitenContainer = new JPanel(seitenUmschalter);
		this.seitenContainer.setOpaque(false);

		// Die beiden Hauptansichten (Karten) erstellen
		JPanel uebersichtsSeite = erstelleUebersichtsSeite();
		JPanel formularSeite = erstelleFormularSeite();

		// Die Seiten mit einem eindeutigen Namen im Container registrieren
		this.seitenContainer.add(uebersichtsSeite, "UEBERSICHT");
		this.seitenContainer.add(formularSeite, "FORMULAR");

		// Grundsetup für dieses Hauptpanel
		setBackground(HINTERGRUND_FARBE);
		setLayout(new BorderLayout());
		add(seitenContainer, BorderLayout.CENTER);

		// Daten beim Start das erste Mal aus dem Manager laden
		aktualisiereDashboard();
	}

	private JPanel erstelleUebersichtsSeite() {
		JPanel seite = new JPanel(new BorderLayout(20, 20));
		seite.setOpaque(false);
		seite.setBorder(new EmptyBorder(25, 25, 25, 25));

		// 1. Kopfbereich (Header)
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

		// Button zum Wechseln auf die Formular-Erstellungsseite
		JButton buttonParkplatzAnlegenOeffnen = new JButton("+ Neuen Parkplatz anlegen");
		buttonParkplatzAnlegenOeffnen.setBackground(AKZENT_FARBE);
		buttonParkplatzAnlegenOeffnen.setForeground(Color.WHITE);
		buttonParkplatzAnlegenOeffnen.setFont(new Font("Arial", Font.BOLD, 14));
		buttonParkplatzAnlegenOeffnen.setFocusPainted(false);
		buttonParkplatzAnlegenOeffnen.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
		buttonParkplatzAnlegenOeffnen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonParkplatzAnlegenOeffnen.addActionListener(e -> seitenUmschalter.show(seitenContainer, "FORMULAR"));

		// Logout-Button
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

		// 2. Tabellen-Bereich (Zwei Karten untereinander)
		JPanel tabellenBereich = new JPanel(new GridLayout(2, 1, 0, 20));
		tabellenBereich.setOpaque(false);

		// --- Karte 1: Parkplatz-Stammdaten ---
		JPanel karteParkplaetze = erstelleKartenPanel("Meine Parkplätze (Stammdaten)");
		String[] spaltenParkplaetze = { "ID", "Bezeichnung", "Adresse", "Kapazität", "Stundensatz", "Wochenendsatz" };
		modellParkplaetze = new DefaultTableModel(spaltenParkplaetze, 0) {
			@Override
			public boolean isCellEditable(int zeile, int spalte) {
				return false;
			}
		};
		tabelleEigeneParkplaetze = erstelleModerneTabelle(modellParkplaetze);

		// Wenn ein Parkplatz angeklickt wird, filtere die Buchungstabelle darunter
		tabelleEigeneParkplaetze.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting())
				filtereBelegungNachAuswahl();
		});

		buttonAlleBuchungenAnzeigen = new JButton("Alle Buchungen anzeigen");
		buttonAlleBuchungenAnzeigen.setFont(new Font("Arial", Font.BOLD, 12));
		buttonAlleBuchungenAnzeigen.setForeground(AKZENT_FARBE);
		buttonAlleBuchungenAnzeigen.setContentAreaFilled(false);
		buttonAlleBuchungenAnzeigen.setBorderPainted(false);
		buttonAlleBuchungenAnzeigen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonAlleBuchungenAnzeigen.addActionListener(e -> {
			tabelleEigeneParkplaetze.clearSelection();
			befuelleBelegungsTabelle(null); // Filter zurücksetzen
		});

		JPanel kartenKopf = new JPanel(new BorderLayout());
		kartenKopf.setOpaque(false);
		kartenKopf.add(buttonAlleBuchungenAnzeigen, BorderLayout.EAST);
		karteParkplaetze.add(kartenKopf, BorderLayout.NORTH);
		karteParkplaetze.add(new JScrollPane(tabelleEigeneParkplaetze), BorderLayout.CENTER);

		// --- Karte 2: Belegungsplan ---
		JPanel karteBelegung = erstelleKartenPanel("Aktuelle & Zukünftige Belegung");
		String[] spaltenBelegung = { "Buchungs-ID", "Parkplatz-ID", "Von (Datum/Zeit)", "Bis (Datum/Zeit)",
				"Umsatz (€)" };
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

	// --- ANSICHT 2: DIE EINGABEMASKE (FORMULAR) ---
	private JPanel erstelleFormularSeite() {
		JPanel seite = new JPanel(new BorderLayout(20, 20));
		seite.setOpaque(false);
		seite.setBorder(new EmptyBorder(25, 25, 25, 25));

		// Formular-Kopfbereich
		JPanel kopfBereich = new JPanel(new BorderLayout());
		kopfBereich.setOpaque(false);

		JLabel titelLabel = new JLabel("Neuen Parkplatz registrieren");
		titelLabel.setFont(new Font("Arial", Font.BOLD, 26));
		titelLabel.setForeground(TEXT_MAIN);
		kopfBereich.add(titelLabel, BorderLayout.WEST);

		// Zurück-Button oben rechts
		JButton buttonZurueck = new JButton("← Zurück zur Übersicht");
		buttonZurueck.setFont(new Font("Arial", Font.BOLD, 14));
		buttonZurueck.setForeground(new Color(100, 116, 139));
		buttonZurueck.setContentAreaFilled(false);
		buttonZurueck.setBorderPainted(false);
		buttonZurueck.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonZurueck.addActionListener(e -> seitenUmschalter.show(seitenContainer, "UEBERSICHT"));
		kopfBereich.add(buttonZurueck, BorderLayout.EAST);

		seite.add(kopfBereich, BorderLayout.NORTH);

		// Hier nutzen wir echte Objektorientierung: Das Formular ist ein eigenständiges
		// Objekt
		JPanel zentrierungsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
		zentrierungsWrapper.setOpaque(false);

		ParkplatzFormularKomponente formularKartenKomponente = new ParkplatzFormularKomponente();
		zentrierungsWrapper.add(formularKartenKomponente);

		seite.add(zentrierungsWrapper, BorderLayout.CENTER);

		return seite;
	}

	// --- LOGIK & DATENVERARBEITUNG ---

	private void filtereBelegungNachAuswahl() {
		int ausgewaehlteZeile = tabelleEigeneParkplaetze.getSelectedRow();
		if (ausgewaehlteZeile >= 0) {
			// Die ID steht in der ersten Spalte (Index 0)
			String parkplatzId = tabelleEigeneParkplaetze.getValueAt(ausgewaehlteZeile, 0).toString();
			befuelleBelegungsTabelle(parkplatzId);
		}
	}
	
	// Meldet den aktuellen Betreiber ab und wechselt zurück zur Login-Ansicht.
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
				modellParkplaetze.addRow(new Object[] { p.getId(), p.getBezeichnung(), p.getAdresse(),
						p.getGesamtKapazitaet(), p.getStundenSatz(), p.getSonderSatz() });
			}
		}
		befuelleBelegungsTabelle(null);
	}

	private void befuelleBelegungsTabelle(String filterParkplatzId) {
        // Zuerst leeren wir die Tabelle komplett, um keine alten Daten anzuzeigen
        modellBelegung.setRowCount(0);
        
        // Wir prüfen, ob der Betreiber überhaupt Parkplätze hat
        if (aktuellerBetreiber.getMeineParkplaetze() != null && !aktuellerBetreiber.getMeineParkplaetze().isEmpty()) {
            
            // Sammle alle Parkplatz-IDs des Betreibers
            java.util.List<String> meineParkplatzIds = new java.util.ArrayList<>();
            for (Parkplatz p : aktuellerBetreiber.getMeineParkplaetze()) {
                meineParkplatzIds.add(p.getId());
            }
            
            // Iteriere durch alle Buchungen der Plattform
            for (model.Buchung buchung : manager.getAlleBuchungen()) {
                // Prüfe, ob diese Buchung zu einem Parkplatz des Betreibers gehört
                if (buchung.getParkplatz() != null && meineParkplatzIds.contains(buchung.getParkplatz().getId())) {
                    
                    // Falls ein Filter aktiv ist, prüfe, ob die Buchung zu diesem Parkplatz passt
                    if (filterParkplatzId != null && !buchung.getParkplatz().getId().equals(filterParkplatzId)) {
                        continue;
                    }
                    
                    // Füge die Buchungsdaten als neue Zeile in die Tabelle ein
                    modellBelegung.addRow(new Object[]{
                            buchung.getBuchungsCode(),           // Eindeutige ID der Buchung
                            buchung.getParkplatz().getBezeichnung(),  // Name des Parkplatzes für bessere Lesbarkeit
                            buchung.getVon(),                    // Startzeitpunkt
                            buchung.getBis(),                    // Endzeitpunkt
                            buchung.getPreisAufschluesselung()   // Berechneter Umsatz / Preis
                    });
                }
            }
        }
    }

	// --- DESIGN-HILFSMETHODEN ---

	private JPanel erstelleKartenPanel(String kartenTitel) {
		JPanel karte = new JPanel(new BorderLayout(10, 10));
		karte.setBackground(KARTEN_HINTERGRUND);
		karte.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(RAHMEN_FARBE, 1, true),
				BorderFactory.createEmptyBorder(18, 18, 18, 18)));

		JLabel titelLabel = new JLabel(kartenTitel);
		titelLabel.setFont(new Font("Arial", Font.BOLD, 16));
		titelLabel.setForeground(TEXT_MAIN);
		karte.add(titelLabel, BorderLayout.WEST);

		return karte;
	}

	private JTable erstelleModerneTabelle(DefaultTableModel modell) {
		// Option A: Überschreiben für automatische Text-Hinweise (Tooltips) bei langen
		// Texten
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
		private JButton buttonSpeichern;

		public ParkplatzFormularKomponente() {
			// Setup der Formular-Karte
			setBackground(KARTEN_HINTERGRUND);
			setLayout(new BorderLayout(15, 15));
			setPreferredSize(new Dimension(460, 480));
			setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

			// Eingabefelder erzeugen
			eingabeBezeichnung = erstelleModernesEingabefeld();
			eingabeAdresse = erstelleModernesEingabefeld();
			eingabeKapazitaet = erstelleModernesEingabefeld();
			eingabeStundensatz = erstelleModernesEingabefeld();
			eingabeSondersatz = erstelleModernesEingabefeld();

			// Gitter-Layout für die Felder definieren
			JPanel felderPanel = new JPanel(new GridBagLayout());
			felderPanel.setBackground(KARTEN_HINTERGRUND);
			GridBagConstraints einschränkungen = new GridBagConstraints();
			einschränkungen.fill = GridBagConstraints.HORIZONTAL;
			einschränkungen.weightx = 1.0;

			fuegeFormularZeileHinzu(felderPanel, "Bezeichnung des Parkplatzes", eingabeBezeichnung, einschränkungen, 0);
			fuegeFormularZeileHinzu(felderPanel, "Adresse / Standort", eingabeAdresse, einschränkungen, 1);
			fuegeFormularZeileHinzu(felderPanel, "Maximale Kapazität (Stellplätze)", eingabeKapazitaet, einschränkungen,
					2);
			fuegeFormularZeileHinzu(felderPanel, "Standard-Stundensatz (€)", eingabeStundensatz, einschränkungen, 3);
			fuegeFormularZeileHinzu(felderPanel, "Wochenend- / Sondersatz (€)", eingabeSondersatz, einschränkungen, 4);

			add(felderPanel, BorderLayout.CENTER);

			// Speichern-Button initialisieren
			buttonSpeichern = new JButton("Parkfläche speichern");
			buttonSpeichern.setBackground(AKZENT_FARBE);
			buttonSpeichern.setForeground(Color.WHITE);
			buttonSpeichern.setFont(new Font("Arial", Font.BOLD, 14));
			buttonSpeichern.setFocusPainted(false);
			buttonSpeichern.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
			buttonSpeichern.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			// Logik beim Klick auf Speichern
			buttonSpeichern.addActionListener(e -> verarbeiteSpeicherung());
			add(buttonSpeichern, BorderLayout.SOUTH);
		}

		private void fuegeFormularZeileHinzu(JPanel panel, String beschriftungText, JTextField feld,
				GridBagConstraints gbc, int reihe) {
			JLabel beschriftung = new JLabel(beschriftungText);
			beschriftung.setFont(new Font("Arial", Font.BOLD, 12));
			beschriftung.setForeground(new Color(100, 116, 139));

			// Label platzieren
			gbc.gridy = reihe * 2;
			gbc.insets = new Insets(6, 0, 2, 0);
			panel.add(beschriftung, gbc);

			// Zugehöriges Textfeld direkt darunter platzieren
			gbc.gridy = (reihe * 2) + 1;
			gbc.insets = new Insets(0, 0, 10, 0);
			panel.add(feld, gbc);
		}

		private JTextField erstelleModernesEingabefeld() {
			JTextField textFeld = new JTextField();
			textFeld.setFont(new Font("Arial", Font.PLAIN, 14));
			textFeld.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(RAHMEN_FARBE, 1),
					BorderFactory.createEmptyBorder(6, 10, 6, 10)));
			return textFeld;
		}

		private void verarbeiteSpeicherung() {
			try {
				String bezeichnung = eingabeBezeichnung.getText().trim();
				String adresse = eingabeAdresse.getText().trim();
				int kapazitaet = Integer.parseInt(eingabeKapazitaet.getText().trim());
				double stundenSatz = Double.parseDouble(eingabeStundensatz.getText().trim());
				double sonderSatz = Double.parseDouble(eingabeSondersatz.getText().trim());

				// Eindeutige ID generieren (Die ersten 8 Stellen einer UUID)
				String generierteId = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

				// Neues Modellobjekt erzeugen
				Parkplatz neuerParkplatz = new Parkplatz(generierteId, bezeichnung, adresse, kapazitaet, stundenSatz,
						sonderSatz);

				// An den Controller übergeben
				manager.parkplatzAnlegen(neuerParkplatz);

				JOptionPane.showMessageDialog(this, "Parkplatz erfolgreich registriert.", "Erfolg",
						JOptionPane.INFORMATION_MESSAGE);

				// Felder säubern
				eingabeBezeichnung.setText("");
				eingabeAdresse.setText("");
				eingabeKapazitaet.setText("");
				eingabeStundensatz.setText("");
				eingabeSondersatz.setText("");

				// Dashboard-Tabellen aktualisieren und Ansicht zurückwechseln
				aktualisiereDashboard();
				seitenUmschalter.show(seitenContainer, "UEBERSICHT");

			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this,
						"Bitte geben Sie in den Zahlenfeldern gültige Werte ein (z.B. 50 oder 2.50).", "Eingabefehler",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
}