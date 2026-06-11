package controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;//Email Format check bei Registrierung

import model.Betreiber;
import model.Buchung;
import model.Kunde;
import model.Parkplatz;
import model.User;

public class PlattformManager {
	private List<Parkplatz> alleParkplaetze;
	private List<Buchung> alleBuchungen;
	private List<User> alleNutzer;
	private User aktuellerNutzer;

	//Email Format, damit bei Registrierung ausschließlich mindestens ein Zeichen + @ + Domaene + Endung (bsp. de) akzeptiert wird
	private static final Pattern EMAIL_PATTERN =
	        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	public PlattformManager() {
		this.alleParkplaetze = new java.util.ArrayList<>();
		this.alleBuchungen = new java.util.ArrayList<>();
		this.alleNutzer = new java.util.ArrayList<>();
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
	    int count = 0;
	    for (Buchung b : alleBuchungen) {
	        if (b.getParkplatz().getId().equals(p.getId())) {

	            if (!(bis.isBefore(b.getVon()) || bis.isEqual(b.getVon()) || von.isAfter(b.getBis()) || von.isEqual(b.getBis()))) {
	                count++;
	            }
	        }
	    }

	    return count < p.getGesamtKapazitaet();

	}


	public double berechnePreis(Parkplatz p, LocalDateTime von, LocalDateTime bis) {
		Buchung testBuchung = new Buchung("test", p, null, von, bis);
		return testBuchung.berechnePreis();
	}




    public Buchung bucheParkplatz(Parkplatz p, LocalDateTime von, LocalDateTime bis) {

    	if (!(aktuellerNutzer instanceof Kunde)) {
    	    throw new IllegalStateException("Nur Kunden können einen Parkplatz buchen.");
    	}

    	if (p == null || von == null || bis == null) {
    	    throw new IllegalArgumentException("Parkplatz und Zeitraum dürfen nicht null sein.");
    	}

    	if (!bis.isAfter(von)) {
    	    throw new IllegalArgumentException("Die Endzeit muss nach der Startzeit liegen.");
    	}

    	Buchung pruefBuchung = new Buchung("test", p, (Kunde) aktuellerNutzer, von, bis);

    	if (!pruefBuchung.istMindestdauerErfuellt()) {
    	    throw new IllegalArgumentException("Die Mindestbuchungsdauer beträgt 15 Minuten.");
    	}

    	if (!verfuegbarkeitPruefen(p, von, bis)) {
    	    return null;
    	}

    	String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    	Kunde kunde = (Kunde) aktuellerNutzer;
    	Buchung neueBuchung = new Buchung(code, p, kunde, von, bis);

    	alleBuchungen.add(neueBuchung);
    	kunde.addBuchung(neueBuchung);

    	return neueBuchung;
    }


    public User registriereNutzer(String name, String email, String typ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Fehler: Name darf nicht leer sein.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Fehler: E-Mail darf nicht leer sein.");
        }

        email = email.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Fehler: Bitte gib eine gültige E-Mail-Adresse ein.");
        }

        if (typ == null || typ.isBlank()) {
            throw new IllegalArgumentException("Fehler: Bitte wähle einen Nutzertyp aus.");
        }

        for (User user : alleNutzer) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("Fehler: Es existiert bereits ein Nutzer mit dieser E-Mail.");
            }
        }

        String id = UUID.randomUUID().toString();
        User neuerNutzer;

        if (typ.equals("Kunde")) {
            neuerNutzer = new Kunde(id, name.trim(), email);
        } else if (typ.equals("Betreiber")) {
            neuerNutzer = new Betreiber(id, name.trim(), email);
        } else {
            throw new IllegalArgumentException("Fehler: Unbekannter Nutzertyp.");
        }

        alleNutzer.add(neuerNutzer);
        return neuerNutzer;
    }

	public boolean login(String email) {
		if (email == null || email.isBlank()) {
		    System.out.println("Fehler: E-Mail ist leer.");
		    return false;
		}

		email = email.trim().toLowerCase();

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