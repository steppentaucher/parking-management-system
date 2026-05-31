package tests;

import controller.PlattformManager;
import model.Betreiber;
import model.Kunde;
import model.Parkplatz;
import model.Buchung;
import view.MainFrame;

import java.time.LocalDateTime;

public class PlattformManagerTest {
    private PlattformManager manager;
    private Parkplatz p1;

    public static void main(String[] args) {
        PlattformManagerTest test = new PlattformManagerTest();

        System.out.println("--- PlattformManager Verfügbarkeitsprüfung ---");
        
        // TC-01
        test.setUp();
        System.out.println("Fall 1: Parkplatz ist frei -> " + test.check(
            LocalDateTime.of(2026, 6, 1, 10, 0), 
            LocalDateTime.of(2026, 6, 1, 12, 0)
        ));

        // TC-03
        test.setUp();
        test.addBooking(10, 12);
        System.out.println("Fall 2: Parkplatz voll belegt -> " + test.check(
            LocalDateTime.of(2026, 6, 1, 10, 0), 
            LocalDateTime.of(2026, 6, 1, 12, 0)
        ));

        // TC-04
        test.setUp();
        test.addBooking(10, 12);
        System.out.println("Fall 3: Überschneidung (11-13 Uhr) -> " + test.check(
            LocalDateTime.of(2026, 6, 1, 11, 0), 
            LocalDateTime.of(2026, 6, 1, 13, 0)
        ));

        // TC-05
        test.setUp();
        test.addBooking(10, 12);
        System.out.println("Fall 4: Anschluss (ab 12 Uhr) -> " + test.check(
            LocalDateTime.of(2026, 6, 1, 12, 0), 
            LocalDateTime.of(2026, 6, 1, 14, 0)
        ));
    }

    private void setUp() {
        manager = new PlattformManager();
        p1 = new Parkplatz("1", "Test", "Str", 1, 10.0);
        manager.getAlleParkplaetze().add(p1);
    }

    private void addBooking(int startH, int endH) {
        LocalDateTime von = LocalDateTime.of(2026, 6, 1, startH, 0);
        LocalDateTime bis = LocalDateTime.of(2026, 6, 1, endH, 0);
        manager.getAlleBuchungen().add(new Buchung("B", p1, null, von, bis));

        test.setUp();
        test.testLoginMitGueltigerEmail();

        test.setUp();
        test.testLoginMitUngueltigerEmail();

        test.setUp();
     
    }

    void setUp() {
        manager = new PlattformManager();
        manager.addNutzer(new Kunde("k1", "Max Mustermann", "max@mail.de"));
        manager.addNutzer(new Betreiber("b1", "Anna Betreiber", "anna@mail.de"));
        // TODO: Hier später weitere Testdaten (Parkplatz, Buchung) initialisieren
    }

    void testLoginMitGueltigerEmail() {
        boolean ergebnis = manager.login("max@mail.de");

        System.out.println("testLoginMitGueltigerEmail:");
        System.out.println("Erwartet: true");
        System.out.println("Tatsächlich: " + ergebnis);

        if (manager.getAktuellerNutzer() != null) {
            System.out.println("Aktueller Nutzer: " + manager.getAktuellerNutzer().getEmail());
        } else {
            System.out.println("Aktueller Nutzer: null");
        }

        System.out.println();
    }

    void testLoginMitUngueltigerEmail() {
        boolean ergebnis = manager.login("falsch@mail.de");

        System.out.println("testLoginMitUngueltigerEmail:");
        System.out.println("Erwartet: false");
        System.out.println("Tatsächlich: " + ergebnis);

        if (manager.getAktuellerNutzer() != null) {
            System.out.println("Aktueller Nutzer: " + manager.getAktuellerNutzer().getEmail());
        } else {
            System.out.println("Aktueller Nutzer: null");
        }

        System.out.println();
    }


    void testRegistrierungErfolgreich() {
        // TODO: Teste ob US1 funktioniert
    }

    void testVerfuegbarkeitPruefen_Frei() {
        // TODO: Teste ob ein freier Zeitraum als 'true' erkannt wird (US4)
    }

    void testVerfuegbarkeitPruefen_Belegt() {
        // TODO: Teste ob ein belegter Zeitraum als 'false' erkannt wird (US4)
    }

    void testBucheParkplatz_Erfolgreich() {
        // TODO: Teste ob eine Buchung korrekt angelegt wird (US5)
    }

    private boolean check(LocalDateTime von, LocalDateTime bis) {
        return manager.verfuegbarkeitPruefen(p1, von, bis);
    }
}