package controller;

import model.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

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
	
    public void storniereBuchung(Buchung b) {
        
        // Akzeptanzkriterium 1: Nur der Besitzer (Kunde) der Buchung kann diese stornieren.
        // Wir prüfen, ob überhaupt jemand eingeloggt ist und ob dieser mit dem Kunden der Buchung übereinstimmt.
        if (this.aktuellerNutzer == null || !this.aktuellerNutzer.equals(b.getKunde())) {
            // Eine SecurityException signalisiert eine fehlende Berechtigung
            throw new SecurityException("Abbruch: Nur der Besitzer der Buchung darf diese stornieren.");
        }

        // Akzeptanzkriterium 2: Das Buchung-Objekt wird aus allen Listen entfernt.
        
        // a) Aus der globalen Liste des PlattformManagers entfernen
        this.alleBuchungen.remove(b);
        
        // b) Aus der persönlichen Liste "meineBuchungen" des Kunden entfernen
        // Da wir oben geprüft haben, dass der aktuelle Nutzer der Besitzer ist, 
        // können wir ihn sicher zu einem "Kunde"-Objekt casten.
        Kunde betroffenerKunde = (Kunde) this.aktuellerNutzer;
        betroffenerKunde.getMeineBuchungen().remove(b);

        // Akzeptanzkriterium 3: Die Verfügbarkeitsprüfung (US4) erkennt den Platz sofort wieder als frei.
        // -> Automatisch erfüllt! Da das Buchungsobjekt vollständig aus 'alleBuchungen' gelöscht wurde,
        // wird die Methode 'verfuegbarkeitPruefen' diesen Zeitraum bei einer erneuten Abfrage als leer/frei betrachten.
        
        System.out.println("Buchung erfolgreich storniert.");
    }
	
	public boolean verfuegbarkeitPruefen(Parkplatz p, LocalDateTime von, LocalDateTime bis) {
		// TODO: Implementierung
		return false;
	}

	public Buchung bucheParkplatz(Parkplatz p, LocalDateTime von, LocalDateTime bis) {
		// TODO: Implementierung
		return null;
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
		if (user != null) {						//Kleine Hilfsmethode zum testen 
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

	public List<Parkplatz> getAlleParkplaetze() {
		return alleParkplaetze;
	}

	public List<Buchung> getAlleBuchungen() {
		return alleBuchungen;
	}

	public List<User> getAlleNutzer() {
		return alleNutzer;
	}

	public User getAktuellerNutzer() {
		return aktuellerNutzer;
	}
}
