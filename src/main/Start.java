package main;

import javax.swing.SwingUtilities;

import controller.PlattformManager;
import model.Parkplatz;
import view.MainFrame;

public class Start {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlattformManager manager = new PlattformManager();
            manager.ladeSystemDaten();
            
            // Speichern beim Beenden
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                manager.speichereSystemDaten();
            }));
            
            // manager.ladeSystemDaten(); // Später aktivieren
            
      
            


            manager.getAlleParkplaetze().add(
                    new Parkplatz("p1", "City Parkhaus", "Musterstraße 1, Berlin", 20, 2.50)
            );
            manager.getAlleParkplaetze().add(
                    new Parkplatz("p2", "Zentrum West", "Hauptstraße 12, Berlin", 10, 3.00)
            );
            manager.getAlleParkplaetze().add(	
                    new Parkplatz("p3", "Bahnhof Süd", "Bahnhofplatz 5, Berlin", 15, 2.80)
            );

            MainFrame gui = new MainFrame(manager);
            gui.setVisible(true);
            gui.zeigeLoginView();
        });
    }
}