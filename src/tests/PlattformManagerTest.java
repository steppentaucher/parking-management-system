package tests;

import controller.PlattformManager;
import model.Parkplatz;
import model.Buchung;
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
    }

    private boolean check(LocalDateTime von, LocalDateTime bis) {
        return manager.verfuegbarkeitPruefen(p1, von, bis);
    }
}
