package controller;

import model.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PlattformManager {
	private List<Parkplatz> alleParkplaetze;
	private List<Buchung> alleBuchungen;
	private List<User> alleNutzer;
	private User aktuellerNutzer;

	public PlattformManager() {
		this.alleParkplaetze = new ArrayList<>();
		this.alleBuchungen = new ArrayList<>();
		this.alleNutzer = new ArrayList<>();
		this.aktuellerNutzer = null;
	}

	public double berechnePreis(Parkplatz p, LocalDateTime von, LocalDateTime bis) {
		Buchung testBuchung = new Buchung("test", p, null, von, bis);
		return testBuchung.berechnePreis();
	}

	public boolean verfuegbarkeitPruefen(Parkplatz p, LocalDateTime von, LocalDateTime bis) {
		for (Buchung b : alleBuchungen) {
			if (b.getParkplatz().getId().equals(p.getId())) {
				boolean ueberschneidung = b.getVon().isBefore(bis) && b.getBis().isAfter(von);

				if (ueberschneidung) {
					return false;
				}
			}
		}
		return true;
	}

	public Buchung bucheParkplatz(Parkplatz p, LocalDateTime von, LocalDateTime bis) {
		if (p == null || von == null || bis == null) {
			return null;
		}

		if (!bis.isAfter(von)) {
			return null;
		}

		if (!verfuegbarkeitPruefen(p, von, bis)) {
			return null;
		}

		String buchungsCode = UUID.randomUUID().toString();
		Buchung neueBuchung = new Buchung(buchungsCode, p, null, von, bis);

		alleBuchungen.add(neueBuchung);
		return neueBuchung;
	}

	public User registriereNutzer(String name, String email, String typ) {
		// TODO: Implementierung
		return null;
	}

	public boolean login(String email) {
		if (email == null || email.isBlank()) {
			System.out.println("Fehler: E-Mail ist leer.");
			return false;
		}

		for (User user : alleNutzer) {
			if (user.getEmail().equalsIgnoreCase(email)) {
				aktuellerNutzer = user;
				return true;
			}
		}

		System.out.println("Fehler: Kein Nutzer mit dieser E-Mail gefunden.");
		return false;
	}

	public void addNutzer(User user) {
		if (user != null) {						// Kleine Hilfsmethode zum testen 
			alleNutzer.add(user);
		}
	}

	public List<Buchung> getZukuenftigeBuchungenFuerBetreiber(Betreiber b) {
		// TODO: Implementierung
		return null;
	}

	public void ladeSystemDaten() {
		// Ruft FileIO auf
	}

	public void speichereSystemDaten() {
		// Ruft FileIO auf
	}

	public List<Parkplatz> getAlleParkplaetze() { return alleParkplaetze; }
	public List<Buchung> getAlleBuchungen() { return alleBuchungen; }
	public List<User> getAlleNutzer() { return alleNutzer; }
	public User getAktuellerNutzer() { return aktuellerNutzer; }
}