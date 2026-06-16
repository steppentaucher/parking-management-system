package model;

import java.util.ArrayList;
import java.util.List;

public class Betreiber extends User {
    
    private List<Parkplatz> meineParkplaetze;

    public Betreiber(String id, String name, String email) {
        super(id, name, email);
        this.meineParkplaetze = new ArrayList<>();
    }

    public void addParkplatz(Parkplatz p) {
        
        if (p.getGesamtKapazitaet() <= 0) {
            throw new IllegalArgumentException("Fehler: Die Gesamtkapazität muss größer als 0 sein.");
        }
        if (p.getStundenSatz() <= 0.0) {
            throw new IllegalArgumentException("Fehler: Der Stundensatz muss größer als 0.0 sein.");
        }

        this.meineParkplaetze.add(p);
    }

    public List<Parkplatz> getMeineParkplaetze() {
        return this.meineParkplaetze;
    }
}