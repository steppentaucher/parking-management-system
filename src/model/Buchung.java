package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Buchung implements Serializable {
    private static final long serialVersionUID = 1L;
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

    public String getBuchungsCode() { return buchungsCode; }
    public Parkplatz getParkplatz() { return parkplatz; }
    public Kunde getKunde() { return kunde; }
    public LocalDateTime getVon() { return von; }
    public LocalDateTime getBis() { return bis; }
}
