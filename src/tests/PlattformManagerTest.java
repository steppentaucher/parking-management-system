package tests;

import controller.PlattformManager;
import model.Parkplatz;
import java.time.LocalDateTime;

public class PlattformManagerTest {
    private PlattformManager manager;

    
    void setUp() {
        manager = new PlattformManager();
        // TODO: Hier Testdaten (User, Parkplatz) initialisieren
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
