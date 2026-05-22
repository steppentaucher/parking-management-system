package tests;

import controller.PlattformManager;
import model.Betreiber;
import model.Kunde;
import model.Parkplatz;
import view.MainFrame;

import java.time.LocalDateTime;

public class PlattformManagerTest {
    private PlattformManager manager;

    public static void main(String[] args) {
        PlattformManagerTest test = new PlattformManagerTest();

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

    void testStornierung_Freigabe() {
        // TODO: Teste ob Stornierung den Platz wieder freigibt (US10)
    }
}