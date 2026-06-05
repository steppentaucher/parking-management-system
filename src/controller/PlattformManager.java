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
		this.alleParkplaetze = new java.util.ArrayList<>();
		this.alleBuchungen = new java.util.ArrayList<>();
		this.alleNutzer = new java.util.ArrayList<>();
    	this.aktuellerNutzer = null;
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
		if (user != null) {						// Kleine Hilfsmethode zum testen 
			alleNutzer.add(user);
		}
	}

	public List<Buchung> getZukuenftigeBuchungenFuerBetreiber(Betreiber b) {
		// TODO: Implementierung
		return null;
	}

	public void ladeSystemDaten() {
		this.alleNutzer = FileIO.ladeUser();
		this.alleParkplaetze = FileIO.ladeParkplaetze();
		this.alleBuchungen = FileIO.ladeBuchungen();
		this.aktuellerNutzer = FileIO.ladeSystemDaten();
		
		// Referenzen wiederherstellen (damit b.getParkplatz() das gleiche Objekt ist wie in alleParkplaetze)
		for (Buchung b : alleBuchungen) {
			for (Parkplatz p : alleParkplaetze) {
				if (b.getParkplatz().getId().equals(p.getId())) {
					// Ersetze die deserialisierte Kopie durch das Original-Objekt aus der Liste
					try {
						java.lang.reflect.Field f = Buchung.class.getDeclaredField("parkplatz");
						f.setAccessible(true);
						f.set(b, p);
					} catch (Exception e) {}
					break;
				}
			}
		}
	}

	public void speichereSystemDaten() {
		FileIO.speichereUser(this.alleNutzer);
		FileIO.speichereParkplaetze(this.alleParkplaetze);
		FileIO.speichereBuchungen(this.alleBuchungen);
		FileIO.speichereSystemDaten(this.aktuellerNutzer);
	}

	public List<Parkplatz> getAlleParkplaetze() { return alleParkplaetze; }
	public List<Buchung> getAlleBuchungen() { return alleBuchungen; }
	public List<User> getAlleNutzer() { return alleNutzer; }
	public User getAktuellerNutzer() { return aktuellerNutzer; }
}