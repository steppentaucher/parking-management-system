package tests;

import controller.PlattformManager;
import model.Kunde;
import model.Parkplatz;
import view.MainFrame;

import javax.swing.*;

public class KundenDashboardViewTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlattformManager manager = new PlattformManager();

            Kunde testKunde = new Kunde(
                    "K1",
                    "Max Mustermann",
                    "max@test.de"
            );

            manager.addNutzer(testKunde);

            manager.getAlleParkplaetze().add(new Parkplatz(
                    "P1",
                    "Parkhaus Mitte",
                    "Berlin Mitte",
                    50,
                    2.5
            ));

            manager.getAlleParkplaetze().add(new Parkplatz(
                    "P2",
                    "Garage Alex",
                    "Berlin Alexanderplatz",
                    30,
                    3.0
            ));

            manager.getAlleParkplaetze().add(new Parkplatz(
                    "P3",
                    "City Parking",
                    "Berlin Charlottenburg",
                    80,
                    1.8
            ));

            MainFrame frame = new MainFrame(manager);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.zeigeLoginView();
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            JOptionPane.showMessageDialog(
                    frame,
                    "Testnutzer angelegt:\nE-Mail: max@test.de"
            );
        });
    }
}