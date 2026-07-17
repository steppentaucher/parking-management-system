package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Parkplatz-Klasse: speichert alle Daten eines einzelnen Parkplatzes
 * - Basis-Infos: Bezeichnung (Name), Adresse, Kapazität
 * - Preise: normaler Stundensatz und Sondersatz (für Wochenende/Feiertage)
 * - Features: Liste von Extras (Überdachung, EV-Ladestation, etc.)
 * Implementiert Serializable damit Parkplätze gespeichert werden können
 */
public class Parkplatz implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String bezeichnung;
    private String adresse;
    private int gesamtKapazitaet;
    private double stundenSatz;
    private double sonderSatz; // Stundensatz fuer Wochenenden und Feiertage
    private List<String> features;

    // Konstruktor ohne Features (Liste wird leer initialisiert)
    public Parkplatz(String id, String bez, String adr, int kap, double satz, double sonderSatz) {
        this(id, bez, adr, kap, satz, sonderSatz, new ArrayList<>());
    }

    // Vollständiger Konstruktor mit Features
    public Parkplatz(String id, String bez, String adr, int kap, double satz, double sonderSatz, List<String> features) {
        this.id = id;
        this.bezeichnung = bez;
        this.adresse = adr;
        this.gesamtKapazitaet = kap;
        this.stundenSatz = satz;
        this.sonderSatz = sonderSatz;
        this.features = features != null ? new ArrayList<>(features) : new ArrayList<>();
    }

    // === Standard Getter/Setter ===
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getGesamtKapazitaet() {
        return gesamtKapazitaet;
    }

    public void setGesamtKapazitaet(int gesamtKapazitaet) {
        this.gesamtKapazitaet = gesamtKapazitaet;
    }

    public double getStundenSatz() {
        return stundenSatz;
    }

    public void setStundenSatz(double stundenSatz) {
        this.stundenSatz = stundenSatz;
    }

    public double getSonderSatz() {
        return sonderSatz;
    }

    public void setSonderSatz(double sonderSatz) {
        this.sonderSatz = sonderSatz;
    }

    /**
     * Gibt die Features-Liste zurück (initialisiert sie falls null)
     * Das kann vorkommen wenn alte gespeicherte Objekte geladen werden
     */
    public List<String> getFeatures() {
        if (features == null) {
            features = new ArrayList<>();
        }
        return features;
    }

    // Setzt die Features-Liste (kopiert um das Original nicht zu ändern)
    public void setFeatures(List<String> features) {
        this.features = features != null ? new ArrayList<>(features) : new ArrayList<>();
    }

    /**
     * Fügt ein neues Feature hinzu (mit Validierung)
     * Ignoriert null/leere Werte und verhindert Duplikate
     */
    public void addFeature(String feature) {
        if (feature == null || feature.isBlank()) {
            return;
        }

        if (features == null) {
            features = new ArrayList<>();
        }

        String bereinigt = feature.trim();
        if (!features.contains(bereinigt)) {
            features.add(bereinigt);
        }
    }

    /**
     * Prüft ob dieser Parkplatz ein bestimmtes Feature hat
     */
    public boolean hatFeature(String feature) {
        if (feature == null || feature.isBlank()) {
            return false;
        }
        return getFeatures().contains(feature.trim());
    }

    /**
     * Prüft ob dieser Parkplatz alle angeforderten Features hat
     * Wird beim Suchen nach Features benutzt
     */
    public boolean hatAlleFeatures(List<String> gesuchteFeatures) {
        if (gesuchteFeatures == null || gesuchteFeatures.isEmpty()) {
            return true;
        }

        for (String feature : gesuchteFeatures) {
            if (!hatFeature(feature)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gibt alle Features als Komma-getrennten Text zurück
     * Wird in UI angezeigt (z.B. "Überdachung, EV-Ladestation, Kamera")
     * Gibt "-" zurück wenn keine Features vorhanden sind
     */
    public String getFeaturesAlsText() {
        if (getFeatures().isEmpty()) {
            return "-";
        }
        return String.join(", ", getFeatures());
    }
}