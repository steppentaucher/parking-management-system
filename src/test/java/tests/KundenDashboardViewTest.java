package tests;

import controller.PlattformManager;
import model.Parkplatz;
import view.KundenDashboardView;

import javax.swing.*;

public class KundenDashboardViewTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlattformManager manager = new PlattformManager();

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

            JFrame frame = new JFrame("KundenDashboard Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new KundenDashboardView(manager));
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}