package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Buchung {
    private String buchungsCode;
    private Parkplatz parkplatz;
    private Kunde kunde;
    private LocalDateTime von;
    private LocalDateTime bis;

    public Buchung(String code, Parkplatz p, Kunde k, LocalDateTime von, LocalDateTime bis) {
        this.buchungsCode = code;
        this.parkplatz = p;
        this.kunde = k;
        this.von = von;
        this.bis = bis;
    }

    public double berechnePreis() {
        long minuten = Duration.between(von, bis).toMinutes();

        if (minuten < 15) {
            return 0.0;
        }

        double stunden = minuten / 60.0;
        return stunden * parkplatz.getStundenSatz();
    }

    public boolean istMindestdauerErfuellt() {
        long minuten = Duration.between(von, bis).toMinutes();
        return minuten >= 15;
    }

    public String getBuchungsCode() { return buchungsCode; }
    public Parkplatz getParkplatz() { return parkplatz; }
    public Kunde getKunde() { return kunde; }
    public LocalDateTime getVon() { return von; }
    public LocalDateTime getBis() { return bis; }
}