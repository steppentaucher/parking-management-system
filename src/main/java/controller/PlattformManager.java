package controller;

import model.*;
import java.time.LocalDateTime;
import java.util.List;

public class PlattformManager {
    private List<Parkplatz> alleParkplaetze;
    private List<Buchung> alleBuchungen;
    private List<User> alleNutzer;
    private User aktuellerNutzer;

    public PlattformManager() {
        // Initialisierung
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
        // TODO: Implementierung
        return false;
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
