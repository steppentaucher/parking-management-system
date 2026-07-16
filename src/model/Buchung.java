package model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Buchung implements Serializable {
    private static final long serialVersionUID = 1L;
    private String buchungsCode;
    private Parkplatz parkplatz;
    private Kunde kunde;
    private LocalDateTime von;
    private LocalDateTime bis;
    // Preis und Aufschluesselung werden bei der Erstellung der Buchung einmalig
    // berechnet und gespeichert. Spaetere Preisaenderungen am Parkplatz haben
    // dadurch keine Auswirkung mehr auf bestehende Buchungen.
    private double gespeicherterPreis;
    private String gespeicherteAufschluesselung;

    // Feste Liste der Berliner Feiertage fuer 2026 und 2027.
    // An diesen Tagen gilt - wie am Wochenende - der Sondersatz des Parkplatzes.
    private static final List<LocalDate> BERLINER_FEIERTAGE = Arrays.asList(
        // 2026
        LocalDate.of(2026, 1, 1),    // Neujahr
        LocalDate.of(2026, 3, 8),    // Internationaler Frauentag
        LocalDate.of(2026, 4, 3),    // Karfreitag
        LocalDate.of(2026, 4, 6),    // Ostermontag
        LocalDate.of(2026, 5, 1),    // Tag der Arbeit
        LocalDate.of(2026, 5, 14),   // Christi Himmelfahrt
        LocalDate.of(2026, 5, 25),   // Pfingstmontag
        LocalDate.of(2026, 10, 3),   // Tag der Deutschen Einheit
        LocalDate.of(2026, 12, 25),  // 1. Weihnachtsfeiertag
        LocalDate.of(2026, 12, 26),  // 2. Weihnachtsfeiertag
        // 2027
        LocalDate.of(2027, 1, 1),    // Neujahr
        LocalDate.of(2027, 3, 8),    // Internationaler Frauentag
        LocalDate.of(2027, 3, 26),   // Karfreitag
        LocalDate.of(2027, 3, 29),   // Ostermontag
        LocalDate.of(2027, 5, 1),    // Tag der Arbeit
        LocalDate.of(2027, 5, 6),    // Christi Himmelfahrt
        LocalDate.of(2027, 5, 17),   // Pfingstmontag
        LocalDate.of(2027, 10, 3),   // Tag der Deutschen Einheit
        LocalDate.of(2027, 12, 25),  // 1. Weihnachtsfeiertag
        LocalDate.of(2027, 12, 26)   // 2. Weihnachtsfeiertag
    );

    public Buchung(String code, Parkplatz p, Kunde k, LocalDateTime von, LocalDateTime bis) {
        this.buchungsCode = code;
        this.parkplatz = p;
        this.kunde = k;
        this.von = von;
        this.bis = bis;

        // Preis direkt bei der Erstellung festhalten ("einfrieren")
        this.gespeicherterPreis = berechnePreisLive();
        this.gespeicherteAufschluesselung = erstelleAufschluesselungLive();
    }

    // Berechnet den Gesamtpreis der Buchung.
    // Minuten an Wochenenden/Feiertagen werden mit dem Sondersatz berechnet,
    // alle uebrigen Minuten mit dem normalen Stundensatz.
    // Gibt den bei der Buchung festgehaltenen Preis zurueck.
    public double berechnePreis() {
        return gespeicherterPreis;
    }

    // Berechnet den Preis anhand der aktuellen Saetze des Parkplatzes.
    // Wird nur einmal im Konstruktor aufgerufen.
    private double berechnePreisLive() {
        long gesamtMinuten = Duration.between(von, bis).toMinutes();
        if (gesamtMinuten < 15) {
            return 0.0;
        }

        long sonderMinuten = zaehleSonderMinuten();
        long normalMinuten = gesamtMinuten - sonderMinuten;

        double normalPreis = (normalMinuten / 60.0) * parkplatz.getStundenSatz();
        double sonderPreis = (sonderMinuten / 60.0) * parkplatz.getSonderSatz();

        return normalPreis + sonderPreis;
    }

    // Geht den Buchungszeitraum Minute fuer Minute durch und zaehlt,
    // wie viele Minuten auf ein Wochenende oder einen Feiertag fallen.
    
    // Erstellt einen lesbaren Text, der zeigt, wie sich der Preis zusammensetzt.
    // Wird dem Kunden bei der Buchung angezeigt (Wochentag- vs. Wochenend-/Feiertagsanteil).
    // Gibt die bei der Buchung festgehaltene Preisuebersicht zurueck.
    public String getPreisAufschluesselung() {
        return gespeicherteAufschluesselung;
    }

    // Erstellt die Aufschluesselung anhand der aktuellen Saetze des Parkplatzes.
    // Wird nur einmal im Konstruktor aufgerufen.
    private String erstelleAufschluesselungLive() {
        long gesamtMinuten = Duration.between(von, bis).toMinutes();
        if (gesamtMinuten < 15) {
            return "Die Mindestbuchungsdauer von 15 Minuten ist nicht erreicht.";
        }

        long sonderMinuten = zaehleSonderMinuten();
        long normalMinuten = gesamtMinuten - sonderMinuten;

        double normalStunden = normalMinuten / 60.0;
        double sonderStunden = sonderMinuten / 60.0;
        double normalPreis = normalStunden * parkplatz.getStundenSatz();
        double sonderPreis = sonderStunden * parkplatz.getSonderSatz();

        String text = "";
        text += String.format("Wochentag: %.2f h x %.2f € = %.2f €", normalStunden, parkplatz.getStundenSatz(), normalPreis) + "\n";
        text += String.format("Wochenende/Feiertag: %.2f h x %.2f € = %.2f €", sonderStunden, parkplatz.getSonderSatz(), sonderPreis) + "\n";
        text += String.format("Gesamt: %.2f €", normalPreis + sonderPreis);
        return text;
    }
    
    private long zaehleSonderMinuten() {
        long sonderMinuten = 0;
        LocalDateTime aktuelle = von;
        while (aktuelle.isBefore(bis)) {
            if (istWochenendeOderFeiertag(aktuelle.toLocalDate())) {
                sonderMinuten++;
            }
            aktuelle = aktuelle.plusMinutes(1);
        }
        return sonderMinuten;
    }

    // Prueft, ob ein Datum ein Samstag, Sonntag oder Berliner Feiertag ist.
    private boolean istWochenendeOderFeiertag(LocalDate datum) {
        DayOfWeek tag = datum.getDayOfWeek();
        if (tag == DayOfWeek.SATURDAY || tag == DayOfWeek.SUNDAY) {
            return true;
        }
        return BERLINER_FEIERTAGE.contains(datum);
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