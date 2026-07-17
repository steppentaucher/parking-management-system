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
            
        
            MainFrame gui = new MainFrame(manager);
            gui.setVisible(true);
            gui.zeigeLoginView();
        });
       
    }
}