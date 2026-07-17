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

public class PlattformManager implements IPlattformManager {
    private List<Parkplatz> alleParkplaetze;
    private List<Buchung> alleBuchungen;
    private List<User> alleNutzer;
    private User aktuellerNutzer;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public PlattformManager() {
        this.alleParkplaetze = new ArrayList<>();
        this.alleBuchungen = new ArrayList<>();
        this.alleNutzer = new ArrayList<>();
        this.aktuellerNutzer = null;
    }

    public void storniereBuchung(Buchung b) {
        if (this.aktuellerNutzer == null || !this.aktuellerNutzer.equals(b.getKunde())) {
            throw new SecurityException("Abbruch: Nur der Besitzer der Buchung darf diese stornieren.");
        }

        this.alleBuchungen.remove(b);

        Kunde betroffenerKunde = (Kunde) this.aktuellerNutzer;
        betroffenerKunde.getMeineBuchungen().remove(b);

        System.out.println("Buchung erfolgreich storniert.");
    }

    public boolean verfuegbarkeitPruefen(Parkplatz p, LocalDateTime von, LocalDateTime bis) {
        int count = 0;

        for (Buchung b : alleBuchungen) {
            if (b.getParkplatz().getId().equals(p.getId())) {
                if (!(bis.isBefore(b.getVon()) || bis.isEqual(b.getVon())
                        || von.isAfter(b.getBis()) || von.isEqual(b.getBis()))) {
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

    public String getPreisAufschluesselung(Parkplatz p, LocalDateTime von, LocalDateTime bis) {
        Buchung testBuchung = new Buchung("test", p, null, von, bis);
        return testBuchung.getPreisAufschluesselung();
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

    public void logout() {
        this.aktuellerNutzer = null;
    }

    public void addNutzer(User user) {
        if (user != null) {
            alleNutzer.add(user);
        }
    }

    public List<Buchung> getZukuenftigeBuchungenFuerBetreiber(Betreiber b) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public void ladeSystemDaten() {
        this.alleNutzer = FileIO.ladeUser();
        this.alleParkplaetze = FileIO.ladeParkplaetze();
        this.alleBuchungen = FileIO.ladeBuchungen();
        this.aktuellerNutzer = FileIO.ladeSystemDaten();

        for (Buchung b : alleBuchungen) {
            for (Parkplatz p : alleParkplaetze) {
                if (b.getParkplatz().getId().equals(p.getId())) {
                    try {
                        java.lang.reflect.Field f = Buchung.class.getDeclaredField("parkplatz");
                        f.setAccessible(true);
                        f.set(b, p);
                    } catch (Exception e) {
                    }
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