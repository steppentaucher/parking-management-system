package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Kunde-Klasse: ein User, der Parkplätze buchen und eigene Buchungen verwalten kann
 * Speichert die Liste aller Buchungen, die dieser Kunde gemacht hat
 */
public class Kunde extends User {
    private List<Buchung> meineBuchungen;

    public Kunde(String id, String name, String email) {
        super(id, name, email);
        this.meineBuchungen = new ArrayList<>();
    }

    public List<Buchung> getMeineBuchungen() {
        return meineBuchungen;
    }

    // Fügt eine neue Buchung zur Liste hinzu (wird von PlattformManager aufgerufen)
    public void addBuchung(Buchung b) {
        if (b != null) {
            meineBuchungen.add(b);
        }
    }
}
