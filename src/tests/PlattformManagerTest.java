package tests;

import controller.PlattformManager;
import model.Buchung;
import model.Betreiber;
import model.Kunde;
import model.Parkplatz;
import model.User;

import java.time.LocalDateTime;

/**
 * Erweiterte, konsolenbasierte Tests für den PlattformManager.
 * Stil: einfache main-Methoden / Ausgaben (kein JUnit) – passend zum vorhandenen Projektstil.
 */
public class PlattformManagerTest {
    private PlattformManager manager;
    private Parkplatz p1;
    private Parkplatz parkplatz1;

    void setUp() {
        manager = new PlattformManager();

        // kleiner Test-Parkplatz zur Verfügbarkeitsprüfung
        p1 = new Parkplatz("1", "TestPark", "Teststraße 1", 2, 2.0, 3.0);
        manager.getAlleParkplaetze().clear();
        manager.getAlleParkplaetze().add(p1);

        // zusätzlicher Beispiel-Parkplatz
        parkplatz1 = new Parkplatz("P1", "Parkhaus Mitte", "Berlin Mitte", 50, 2.5, 3.5);
        manager.getAlleParkplaetze().add(parkplatz1);

        // Nutzer anlegen
        manager.getAlleNutzer().clear();
        manager.addNutzer(new Kunde("k1", "Max Mustermann", "max@mail.de"));
        manager.addNutzer(new Betreiber("b1", "Anna Betreiber", "anna@mail.de"));
    }

    public static void main(String[] args) {
        PlattformManagerTest t = new PlattformManagerTest();

        t.setUp();
        t.testRegistrierungErfolgreichUndDuplikat();

        t.setUp();
        t.testLoginMitGueltigerUndUngueltigerEmail();

        t.setUp();
        t.testVerfuegbarkeit_Faelle();

        t.setUp();
        t.testBuchenUndStornierenWorkflow();

        t.setUp();
        t.testPreiseUndAufschluesselung();
        
        t.setUp();
        t.testBetreiberParkplatzManagement();

        t.setUp();
        t.testSucheMitFeatures();

        System.out.println("\n--- Alle PlattformManager-Tests abgeschlossen ---");
    }

    void testRegistrierungErfolgreichUndDuplikat() {
        System.out.println("testRegistrierungErfolgreichUndDuplikat:");
        try {
            User neu = manager.registriereNutzer("Lisa Tester", "lisa@test.de", "Kunde");
            System.out.println(" - Registrierung erfolgreich: " + (neu != null));

            // Versuch: gleiche E-Mail nochmal -> Exception erwartet
            try {
                manager.registriereNutzer("Lisa2", "lisa@test.de", "Kunde");
                System.out.println(" - FEHLER: Duplicate-Registrierung zugelassen");
            } catch (IllegalArgumentException ex) {
                System.out.println(" - Duplicate-Registrierung korrekt abgelehnt: " + ex.getMessage());
            }
        } catch (Exception e) {
            System.out.println(" - Registrierung fehlgeschlagen: " + e.getMessage());
        }
        System.out.println();
    }

    void testLoginMitGueltigerUndUngueltigerEmail() {
        System.out.println("testLoginMitGueltigerUndUngueltigerEmail:");
        boolean ok = manager.login("max@mail.de");
        System.out.println(" - Login gültig (erwartet true): " + ok);

        manager.logout();
        boolean fail = manager.login("nichtexistent@mail.de");
        System.out.println(" - Login ungültig (erwartet false): " + fail);
        System.out.println();
    }

    void testVerfuegbarkeit_Faelle() {
        System.out.println("testVerfuegbarkeit_Faelle:");

        // Fall 1: leerer Parkplatz (p1 hat Kapazität 2)
        LocalDateTime von = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime bis = LocalDateTime.of(2026, 6, 1, 12, 0);
        System.out.println(" - Leer (erwartet true): " + manager.verfuegbarkeitPruefen(p1, von, bis));

        // Fall 2: zwei Überlappende Buchungen -> Auslastung erreicht
        // Erzeuge zwei Buchungen manuell
        manager.getAlleBuchungen().clear();
        manager.getAlleBuchungen().add(new Buchung("B1", p1, null, von, bis));
        manager.getAlleBuchungen().add(new Buchung("B2", p1, null, von, bis));
        System.out.println(" - Voll belegt (erwartet false): " + manager.verfuegbarkeitPruefen(p1, von, bis));

        // Fall 3: Anschlussbuchung (bis == vorhandenes bis) -> erwartet true
        LocalDateTime von2 = LocalDateTime.of(2026, 6, 1, 12, 0);
        LocalDateTime bis2 = LocalDateTime.of(2026, 6, 1, 13, 0);
        System.out.println(" - Anschluss (erwartet true): " + manager.verfuegbarkeitPruefen(p1, von2, bis2));

        // Fall 4: Überschneidung (11-13) -> erwartet false
        LocalDateTime von3 = LocalDateTime.of(2026, 6, 1, 11, 0);
        LocalDateTime bis3 = LocalDateTime.of(2026, 6, 1, 13, 0);
        System.out.println(" - Überschneidung (erwartet false): " + manager.verfuegbarkeitPruefen(p1, von3, bis3));

        System.out.println();
    }

    void testBuchenUndStornierenWorkflow() {
        System.out.println("testBuchenUndStornierenWorkflow:");
        manager.getAlleBuchungen().clear();

        // Ohne Login: buchen -> erwartet IllegalStateException
        try {
            manager.bucheParkplatz(parkplatz1, LocalDateTime.of(2026, 6, 2, 10, 0), LocalDateTime.of(2026, 6, 2, 11, 0));
            System.out.println(" - FEHLER: Buchen ohne Login wurde erlaubt");
        } catch (IllegalStateException e) {
            System.out.println(" - Buchen ohne Login korrekt verhindert: " + e.getMessage());
        }

        // Login als Kunde und buchen
        manager.login("max@mail.de");
        Buchung b = manager.bucheParkplatz(parkplatz1, LocalDateTime.of(2026, 6, 2, 10, 0), LocalDateTime.of(2026, 6, 2, 11, 0));
        System.out.println(" - Buchen als Kunde erfolgreich: " + (b != null));

        // Stornieren als derselbe Kunde
        try {
            manager.storniereBuchung(b);
            System.out.println(" - Stornierung erfolgreich");
        } catch (Exception e) {
            System.out.println(" - Stornierung fehlgeschlagen: " + e.getMessage());
        }

        // Stornierung eines fremden Buchungsobjekts -> SecurityException
        manager.logout();
        // Erzeuge Buchung durch Setzen aktueller Nutzer auf Kunde und buchen, dann wechsel Nutzer
        manager.login("max@mail.de");
        Buchung b2 = manager.bucheParkplatz(parkplatz1, LocalDateTime.of(2026, 6, 3, 10, 0), LocalDateTime.of(2026, 6, 3, 11, 0));
        manager.logout();
        manager.login("anna@mail.de"); // Betreiber versucht zu stornieren
        try {
            manager.storniereBuchung(b2);
            System.out.println(" - FEHLER: Betreiber konnte fremde Buchung stornieren");
        } catch (SecurityException se) {
            System.out.println(" - Fremde Stornierung korrekt verhindert: " + se.getMessage());
        }

        System.out.println();
    }

    void testPreiseUndAufschluesselung() {
        System.out.println("testPreiseUndAufschluesselung:");
        LocalDateTime von = LocalDateTime.of(2026, 6, 4, 9, 30);
        LocalDateTime bis = LocalDateTime.of(2026, 6, 4, 11, 0);

        double preis = manager.berechnePreis(parkplatz1, von, bis);
        String aufschl = manager.getPreisAufschluesselung(parkplatz1, von, bis);

        System.out.println(String.format(" - Preis (%.2f €), Aufschlüsselung:\n%s", preis, aufschl));
        System.out.println();
    }

    void testBetreiberParkplatzManagement() {
        System.out.println("testBetreiberParkplatzManagement:");
        // Login als Betreiber
        manager.login("anna@mail.de");

        Parkplatz neu = new Parkplatz("NP1", "NeuPark", "Neue Str.", 10, 1.5, 2.0);
        try {
            manager.parkplatzAnlegen(neu);
            System.out.println(" - Anlegen erfolgreich: " + manager.getAlleParkplaetze().contains(neu));
        } catch (Exception e) {
            System.out.println(" - Anlegen fehlgeschlagen: " + e.getMessage());
        }

        // Bearbeiten
        try {
            manager.parkplatzBearbeiten(neu, "NeuPark2", "Andere Str.", 12, 2.0, 2.5, java.util.Collections.emptyList());
            System.out.println(" - Bearbeiten erfolgreich: " + neu.getBezeichnung().equals("NeuPark2"));
        } catch (Exception e) {
            System.out.println(" - Bearbeiten fehlgeschlagen: " + e.getMessage());
        }

        // Löschen (ohne Buchungen) - sollte funktionieren
        try {
            manager.parkplatzLoeschen(neu);
            System.out.println(" - Löschen erfolgreich: " + !manager.getAlleParkplaetze().contains(neu));
        } catch (Exception e) {
            System.out.println(" - Löschen fehlgeschlagen: " + e.getMessage());
        }

        System.out.println();
    }

    void testSucheMitFeatures() {
        System.out.println("testSucheMitFeatures:");
        // Parkplätze mit Features vorbereiten
        Parkplatz f1 = new Parkplatz("F1", "FPark", "F Str.", 5, 2.0, 0.0);
        f1.setFeatures(java.util.Arrays.asList("E-Laden", "Überdacht"));
        manager.getAlleParkplaetze().add(f1);

        java.util.List<String> filter = java.util.Arrays.asList("E-Laden");
        System.out.println(" - Suche mit Feature 'E-Laden' (erwartet >=1): " + manager.sucheParkplaetze("", filter).size());

        java.util.List<String> filter2 = java.util.Arrays.asList("NichtVorhanden");
        System.out.println(" - Suche mit unbekanntem Feature (erwartet 0): " + manager.sucheParkplaetze("", filter2).size());

        System.out.println();
    }

}