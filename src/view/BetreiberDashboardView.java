package view;

import controller.PlattformManager;
import model.Parkplatz;

import javax.swing.*;
import java.awt.*;

public class BetreiberDashboardView extends JPanel {
	private PlattformManager manager;
	private JTable tblEigeneParkplaetze; // Platzhalter fuer spaetere Story
	private JTable tblBelegungsPlan;     // Platzhalter fuer spaetere Story
	private JTextField txtName;
	private JTextField txtAdresse;
	private JTextField txtKapazitaet;
	private JTextField txtPreis;
	private JTextField txtSonderSatz;
	private JButton btnParkplatzAnlegen;

	public BetreiberDashboardView(PlattformManager pm) {
		this.manager = pm;

		// Layout fuer das ganze Panel
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		// Ueberschrift
		JLabel lblTitel = new JLabel("Parkplatz anlegen");
		lblTitel.setFont(new Font("SansSerif", Font.BOLD, 20));
		add(lblTitel, BorderLayout.NORTH);

		// Eingabefelder erzeugen
		txtName = new JTextField();
		txtAdresse = new JTextField();
		txtKapazitaet = new JTextField();
		txtPreis = new JTextField();
		txtSonderSatz = new JTextField();

		// Formular: pro Zeile eine Beschriftung und ein Eingabefeld
		JPanel formular = new JPanel(new GridLayout(5, 2, 8, 8));
		formular.add(new JLabel("Bezeichnung:"));
		formular.add(txtName);
		formular.add(new JLabel("Adresse:"));
		formular.add(txtAdresse);
		formular.add(new JLabel("Kapazität:"));
		formular.add(txtKapazitaet);
		formular.add(new JLabel("Stundensatz (€):"));
		formular.add(txtPreis);
		formular.add(new JLabel("Sondersatz Wochenende/Feiertag (€):"));
		formular.add(txtSonderSatz);

		add(formular, BorderLayout.CENTER);

		// Button unten rechts; ruft beim Klick neuerParkplatz() auf
		btnParkplatzAnlegen = new JButton("Parkplatz anlegen");
		btnParkplatzAnlegen.addActionListener(e -> neuerParkplatz());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(btnParkplatzAnlegen);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void neuerParkplatz() {
		try {
			// Eingaben aus den Textfeldern lesen
			String bezeichnung = txtName.getText().trim();
			String adresse = txtAdresse.getText().trim();
			int kapazitaet = Integer.parseInt(txtKapazitaet.getText().trim());
			double stundenSatz = Double.parseDouble(txtPreis.getText().trim());
			double sonderSatz = Double.parseDouble(txtSonderSatz.getText().trim());

			// Eindeutige ID erzeugen (gleiches Muster wie bei Buchungen)
			String id = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

			// Parkplatz erstellen und ueber den Manager in beide Listen anlegen
			Parkplatz p = new Parkplatz(id, bezeichnung, adresse, kapazitaet, stundenSatz, sonderSatz);
			manager.parkplatzAnlegen(p);

			JOptionPane.showMessageDialog(this, "Parkplatz wurde angelegt.");

			// Nach erfolgreichem Anlegen die Felder leeren,
			// damit man direkt den naechsten Parkplatz eingeben kann.
			txtName.setText("");
			txtAdresse.setText("");
			txtKapazitaet.setText("");
			txtPreis.setText("");
			txtSonderSatz.setText("");

		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Bitte gültige Zahlen für Kapazität, Stundensatz und Sondersatz eingeben.");
		} catch (Exception e) {
			// Faengt z. B. die Validierungs-Meldungen aus addParkplatz und
			// die Duplikat-Meldung aus parkplatzAnlegen ab
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void aktualisiereDashboard() {
		// TODO: Implementierung
	}
}