package model;

import java.io.Serializable;

public class Parkplatz implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String bezeichnung;
    private String adresse;
    private int gesamtKapazitaet;
    private double stundenSatz;
    private double sonderSatz; // Stundensatz fuer Wochenenden und Feiertage

    public Parkplatz(String id, String bez, String adr, int kap, double satz, double sonderSatz) {
        this.id = id;
        this.bezeichnung = bez;
        this.adresse = adr;
        this.gesamtKapazitaet = kap;
        this.stundenSatz = satz;
        this.sonderSatz = sonderSatz;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBezeichnung() { return bezeichnung; }
    public void setBezeichnung(String bezeichnung) { this.bezeichnung = bezeichnung; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public int getGesamtKapazitaet() { return gesamtKapazitaet; }
    public void setGesamtKapazitaet(int gesamtKapazitaet) { this.gesamtKapazitaet = gesamtKapazitaet; }

    public double getStundenSatz() { return stundenSatz; }
    public void setStundenSatz(double stundenSatz) { this.stundenSatz = stundenSatz; }

    // Sondersatz fuer Wochenende/Feiertag
    public double getSonderSatz() { return sonderSatz; }
    public void setSonderSatz(double sonderSatz) { this.sonderSatz = sonderSatz; }
}