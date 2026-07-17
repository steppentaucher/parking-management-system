package controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import model.Betreiber;
import model.Buchung;
import model.Kunde;
import model.Parkplatz;
import model.User;

/**
 * Zentrale Business-Logic des Parkplatz-Management-Systems
 * Verwaltet Parkplätze, Buchungen, User und handelt alle wichtigen Operationen ab
 * Agiert als Vermittler zwischen View und Model (Controller-Pattern)
 */
public class PlattformManager implements IPlattformManager {
    private List<Parkplatz> alleParkplaetze;
    private List<Buchung> alleBuchungen;
    private List<User> alleNutzer;
    private User aktuellerNutzer;

    // Pattern um E-Mail-Adressen zu validieren (Regex)
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public PlattformManager() {
        this.alleParkplaetze = new ArrayList<>();
        this.alleBuchungen = new ArrayList<>();
        this.alleNutzer = new ArrayList<>();
        this.aktuellerNutzer = null;
    }

    /**
     * Löscht eine Buchung, aber nur wenn der aktuell eingeloggte User der Besitzer ist (Security)
     * Die Buchung wird aus der allgemeinen Liste und aus der Kundenliste gelöscht
     */
    public void storniereBuchung(Buchung b) {
        if (this.aktuellerNutzer == null || !this.aktuellerNutzer.equals(b.getKunde())) {
            throw new SecurityException("Abbruch: Nur der Besitzer der Buchung darf diese stornieren.");
        }

        this.alleBuchungen.remove(b);

        Kunde betroffenerKunde = (Kunde) this.aktuellerNutzer;
        betroffenerKunde.getMeineBuchungen().remove(b);

        System.out.println("Buchung erfolgreich storniert.");
    }

    /**
     * Prüft ob ein Parkplatz im gewünschten Zeitraum verfügbar ist
     * Zählt alle Buchungen die den gleichen Parkplatz und Zeitraum betreffen
     * Wenn Anzahl < Kapazität: verfügbar
     */
    public boolean verfuegbarkeitPruefen(Parkplatz p, LocalDateTime von, LocalDateTime bis) {
        int count = 0;

        for (Buchung b : alleBuchungen) {
            if (b.getParkplatz().getId().equals(p.getId())) {
                // Prüfen ob die Zeiten nicht überschneiden (komplexe Logik mit isBefore, isAfter, etc.)
                if (!(bis.isBefore(b.getVon()) || bis.isEqual(b.getVon())
                        || von.isAfter(b.getBis()) || von.isEqual(b.getBis()))) {
                    count++;
                }
            }
        }

        return count < p.getGesamtKapazitaet();
    }

    // Berechnet den Preis für eine potenzielle Buchung (ohne sie zu erstellen)
    public double berechnePreis(Parkplatz p, LocalDateTime von, LocalDateTime bis) {
        Buchung testBuchung = new Buchung("test", p, null, von, bis);
        return testBuchung.berechnePreis();
    }

    // Gibt eine lesbare Zusammensetzung des Preises zurück (Wochentag vs Wochenende)
    public String getPreisAufschluesselung(Parkplatz p, LocalDateTime von, LocalDateTime bis) {
        Buchung testBuchung = new Buchung("test", p, null, von, bis);
        return testBuchung.getPreisAufschluesselung();
    }

    /**
     * Erstellt und speichert eine neue Buchung (Hauptfunktion für Kunden)
     * Validiert: nur Kunden dürfen buchen, Zeiten sind korrekt, Mindestdauer erfüllt, Platz verfügbar
     * Gibt die neue Buchung zurück oder null wenn nicht verfügbar
     */
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

        // Generiere eindeutigen 8-stelligen Code für diese Buchung
        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Kunde kunde = (Kunde) aktuellerNutzer;
        Buchung neueBuchung = new Buchung(code, p, kunde, von, bis);

        alleBuchungen.add(neueBuchung);
        kunde.addBuchung(neueBuchung);

        return neueBuchung;
    }

    /**
     * Betreiber können neue Parkplätze anlegen
     * Prüft dass kein Duplikat mit gleichen Eigenschaften existiert
     */
    public void parkplatzAnlegen(Parkplatz p) {
        if (!(aktuellerNutzer instanceof Betreiber)) {
            throw new IllegalStateException("Nur Betreiber dürfen Parkplätze anlegen.");
        }

        for (Parkplatz vorhanden : alleParkplaetze) {
            if (vorhanden.getBezeichnung().equals(p.getBezeichnung())
                    && vorhanden.getAdresse().equals(p.getAdresse())
                    && vorhanden.getGesamtKapazitaet() == p.getGesamtKapazitaet()
                    && vorhanden.getStundenSatz() == p.getStundenSatz()
                    && vorhanden.getSonderSatz() == p.getSonderSatz()
                    && vorhanden.getFeaturesAlsText().equals(p.getFeaturesAlsText())) {
                throw new IllegalArgumentException("Fehler: Ein Parkplatz mit genau diesen Eigenschaften existiert bereits.");
            }
        }

        Betreiber betreiber = (Betreiber) aktuellerNutzer;
        betreiber.addParkplatz(p);
        alleParkplaetze.add(p);
    }
    
    /**
     * Betreiber können nur Parkplätze löschen die keine Buchungen haben
     * Verhindert dass gebuchte Parkplätze gelöscht werden
     */
    public void parkplatzLoeschen(Parkplatz p) {
        // Nur ein eingeloggter Betreiber darf Parkplaetze loeschen.
        if (!(aktuellerNutzer instanceof Betreiber)) {
            throw new IllegalStateException("Nur Betreiber dürfen Parkplätze löschen.");
        }

        // Ein Parkplatz mit vorhandenen Buchungen darf nicht geloescht werden.
        for (Buchung b : alleBuchungen) {
            if (b.getParkplatz() != null && b.getParkplatz().getId().equals(p.getId())) {
                throw new IllegalStateException("Fehler: Dieser Parkplatz kann nicht gelöscht werden, da dieser bereits gebucht wurde!");
            }
        }

        // Aus beiden Listen entfernen (gleiches Prinzip wie beim Anlegen, nur umgekehrt)
        Betreiber betreiber = (Betreiber) aktuellerNutzer;
        betreiber.getMeineParkplaetze().remove(p);
        alleParkplaetze.remove(p);
    }

    /**
     * Betreiber können ihre Parkplätze bearbeiten (Name, Adresse, Kapazität, Preise, Features)
     * Bestehende Buchungen behalten ihren alten Preis (dieser wird nicht nachträglich geändert)
     */
    public void parkplatzBearbeiten(Parkplatz p, String bezeichnung, String adresse,
            int kapazitaet, double stundenSatz, double sonderSatz, List<String> features) {
        // Nur ein eingeloggter Betreiber darf Parkplaetze bearbeiten.
        if (!(aktuellerNutzer instanceof Betreiber)) {
            throw new IllegalStateException("Nur Betreiber dürfen Parkplätze bearbeiten.");
        }

        // Gleiche Validierung wie beim Anlegen
        if (kapazitaet <= 0) {
            throw new IllegalArgumentException("Fehler: Die Gesamtkapazität muss größer als 0 sein.");
        }
        if (stundenSatz <= 0.0) {
            throw new IllegalArgumentException("Fehler: Der Stundensatz muss größer als 0.0 sein.");
        }

        // Neue Werte in das bestehende Parkplatz-Objekt uebernehmen.
        // Bestehende Buchungen behalten ihren Preis, da dieser beim Buchen
        // fest in der Buchung gespeichert wird.
        p.setBezeichnung(bezeichnung);
        p.setAdresse(adresse);
        p.setGesamtKapazitaet(kapazitaet);
        p.setStundenSatz(stundenSatz);
        p.setSonderSatz(sonderSatz);
        p.setFeatures(features);
    }

    /**
     * Sucht Parkplätze nach Ort und/oder Features
     * Gibt alle Parkplätze zurück die passen (Kombination von Adresse/Bezeichnung + Features)
     */
    public List<Parkplatz> sucheParkplaetze(String ort, List<String> featureFilter) {
        List<Parkplatz> ergebnis = new ArrayList<>();
        String suchOrt = ort == null ? "" : ort.trim().toLowerCase();

        for (Parkplatz p : alleParkplaetze) {
            boolean ortPasst = suchOrt.isEmpty()
                    || p.getAdresse().toLowerCase().contains(suchOrt)
                    || p.getBezeichnung().toLowerCase().contains(suchOrt);

            boolean featuresPassen = p.hatAlleFeatures(featureFilter);

            if (ortPasst && featuresPassen) {
                ergebnis.add(p);
            }
        }

        return ergebnis;
    }

    /**
     * Neuer Nutzer registriert sich (Kunde oder Betreiber)
     * Validiert: Name nicht leer, E-Mail gültig, E-Mail noch nicht vergeben
     * Generiert eindeutige User-ID (UUID)
     */
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

    /**
     * User versucht sich mit E-Mail einzuloggen
     * Findet User mit dieser E-Mail und speichert ihn als aktuellen User
     * Gibt true zurück wenn erfolgreich, false wenn User nicht gefunden
     */
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

    // Setzt aktuellen User auf null (logout)
    public void logout() {
        this.aktuellerNutzer = null;
    }

    // Fügt einen neuen User zur Liste hinzu (wird von FileIO beim Laden benutzt)
    public void addNutzer(User user) {
        if (user != null) {
            alleNutzer.add(user);
        }
    }

    // (Unvollständig: sollte zukünftige Buchungen für einen Betreiber anzeigen)
    public List<Buchung> getZukuenftigeBuchungenFuerBetreiber(Betreiber b) {
        return null;
    }

    /**
     * Lädt alle Daten von Festplatte (wird beim App-Start aufgerufen)
     * Lädt User, Parkplätze, Buchungen und den aktuellen User
     * Verbindet Buchungen mit ihren Parkplatz-Objekten
     */
    @SuppressWarnings("unchecked")
    public void ladeSystemDaten() {
        this.alleNutzer = FileIO.ladeUser();
        this.alleParkplaetze = FileIO.ladeParkplaetze();
        this.alleBuchungen = FileIO.ladeBuchungen();
        this.aktuellerNutzer = FileIO.ladeSystemDaten();

        // Verbinde Buchungen mit den echten Parkplatz-Objekten (wird von FileIO geladen)
        for (Buchung b : alleBuchungen) {
            for (Parkplatz p : alleParkplaetze) {
                if (b.getParkplatz().getId().equals(p.getId())) {
                    try {
                    	b.setParkplatz(p);
                    } catch (Exception e) {
                    	e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    /**
     * Speichert alle Daten auf Festplatte (wird beim App-Beenden aufgerufen)
     * Speichert User, Parkplätze, Buchungen und den aktuellen User
     */
    public void speichereSystemDaten() {
        FileIO.speichereUser(this.alleNutzer);
        FileIO.speichereParkplaetze(this.alleParkplaetze);
        FileIO.speichereBuchungen(this.alleBuchungen);
        FileIO.speichereSystemDaten(this.aktuellerNutzer);
    }

    // Getter-Methoden für alle Listen und aktuellen User
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