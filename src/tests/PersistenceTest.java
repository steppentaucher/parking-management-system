package tests;

import controller.PlattformManager;
import model.*;
import java.time.LocalDateTime;
import java.util.List;

public class PersistenceTest {
    public static void main(String[] args) {
        System.out.println("--- Starte Persistenz-Test ---");

        // 1. Initialisierung und Daten erstellen
        PlattformManager managerSave = new PlattformManager();
        
        Kunde k1 = new Kunde("U1", "Max Mustermann", "max@test.de");
        Betreiber b1 = new Betreiber("U2", "Park-Profi GmbH", "info@park-profi.de");
        managerSave.getAlleNutzer().add(k1);
        managerSave.getAlleNutzer().add(b1);
        
        Parkplatz p1 = new Parkplatz("P1", "Zentrum P1", "Hauptstraße 1", 50, 2.50, 3.50);
        managerSave.getAlleParkplaetze().add(p1);
        b1.addParkplatz(p1); // Verknüpfung Betreiber -> Parkplatz
        
        LocalDateTime von = LocalDateTime.of(2026, 6, 10, 10, 0);
        LocalDateTime bis = LocalDateTime.of(2026, 6, 10, 12, 0);
        Buchung bu1 = new Buchung("B-001", p1, k1, von, bis);
        managerSave.getAlleBuchungen().add(bu1);
        k1.addBuchung(bu1); // Verknüpfung Kunde -> Buchung

        // 2. Speichern
        System.out.println("Speichere Daten...");
        managerSave.speichereSystemDaten();

        // 3. Neu laden in einem frischen Manager
        System.out.println("Lade Daten in neuen Manager...");
        PlattformManager managerLoad = new PlattformManager();
        managerLoad.ladeSystemDaten();

        // 4. Validierung
        validate(managerLoad);
        
        System.out.println("--- Test abgeschlossen ---");
    }

    private static void validate(PlattformManager ml) {
        List<User> nutzer = ml.getAlleNutzer();
        List<Parkplatz> parkplaetze = ml.getAlleParkplaetze();
        List<Buchung> buchungen = ml.getAlleBuchungen();

        System.out.println("Validierung:");
        System.out.println("Nutzer geladen: " + nutzer.size() + " (Erwartet: 2)");
        System.out.println("Parkplätze geladen: " + parkplaetze.size() + " (Erwartet: 1)");
        System.out.println("Buchungen geladen: " + buchungen.size() + " (Erwartet: 1)");

        if (!buchungen.isEmpty()) {
            Buchung b = buchungen.get(0);
            System.out.println("Check Buchung Referenzen:");
            System.out.println(" - Parkplatz-ID: " + b.getParkplatz().getId() + " (Erwartet: P1)");
            System.out.println(" - Kunden-Name: " + b.getKunde().getName() + " (Erwartet: Max Mustermann)");
            
            // Teste ob Referenz-Gleichheit besteht (wichtig bei Serialisierung)
            boolean refOk = (b.getParkplatz() == parkplaetze.get(0));
            System.out.println(" - Referenz-Integrität (Parkplatz): " + (refOk ? "OK" : "FEHLER"));
        }
        
        if (nutzer.size() >= 2 && nutzer.get(1) instanceof Betreiber) {
            Betreiber b = (Betreiber) nutzer.get(1);
            System.out.println("Check Betreiber -> Parkplatz Verknüpfung: " + 
                (b.getMeineParkplaetze().size() == 1 ? "OK" : "FEHLER"));
        }
    }
}
