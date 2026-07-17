package controller;

import java.time.LocalDateTime;
import java.util.List;

import model.Buchung;
import model.Parkplatz;
import model.User;

/**
 * Minimales Interface für PlattformManager, ermöglicht lose Kopplung in Views.
 * Enthält nur Methoden, die aktuell von Views benutzt werden.
 */
public interface IPlattformManager {
    boolean login(String email);

    User registriereNutzer(String name, String email, String typ);

    void logout();

    User getAktuellerNutzer();

    List<Parkplatz> getAlleParkplaetze();

    double berechnePreis(Parkplatz p, LocalDateTime von, LocalDateTime bis);
}
