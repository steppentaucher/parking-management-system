package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Parkplatz implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String bezeichnung;
    private String adresse;
    private int gesamtKapazitaet;
    private double stundenSatz;
    private double sonderSatz; // Stundensatz fuer Wochenenden und Feiertage
    private List<String> features;

    public Parkplatz(String id, String bez, String adr, int kap, double satz, double sonderSatz) {
        this(id, bez, adr, kap, satz, sonderSatz, new ArrayList<>());
    }

    public Parkplatz(String id, String bez, String adr, int kap, double satz, double sonderSatz, List<String> features) {
        this.id = id;
        this.bezeichnung = bez;
        this.adresse = adr;
        this.gesamtKapazitaet = kap;
        this.stundenSatz = satz;
        this.sonderSatz = sonderSatz;
        this.features = features != null ? new ArrayList<>(features) : new ArrayList<>();
    }

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

    public List<String> getFeatures() {
        if (features == null) {
            features = new ArrayList<>();
        }
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features != null ? new ArrayList<>(features) : new ArrayList<>();
    }

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

    public boolean hatFeature(String feature) {
        if (feature == null || feature.isBlank()) {
            return false;
        }
        return getFeatures().contains(feature.trim());
    }

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

    public String getFeaturesAlsText() {
        if (getFeatures().isEmpty()) {
            return "-";
        }
        return String.join(", ", getFeatures());
    }
}