package controller;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistenz-Layer für das Parkplatz-Management-System
 * Speichert und lädt alle Daten (User, Parkplätze, Buchungen) von der Festplatte
 * Nutzt Serialisierung (ObjectOutputStream) um Objekte als Binärdaten zu speichern
 */
public class FileIO {
    private static final String DIR = "speicher";
    private static final String USER_FILE = DIR + "/users.txt";
    private static final String PARK_FILE = DIR + "/parkplaetze.txt";
    private static final String BUCH_FILE = DIR + "/buchungen.txt";
    private static final String SYS_FILE = DIR + "/system_data.txt";

    // Erstellt den "speicher"-Ordner falls dieser noch nicht existiert
    private static void checkDir() {
        File folder = new File(DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // Speichert User-Liste als Binärdatei
    @SuppressWarnings("unchecked")
    public static void speichereUser(List<User> liste) {
        checkDir();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            out.writeObject(liste);
        } catch (IOException e) {
            System.err.println("Fehler users: " + e.toString());
        }
    }

    // Lädt User-Liste von Festplatte, gibt leere Liste zurück wenn File nicht existiert
    @SuppressWarnings("unchecked")
    public static List<User> ladeUser() {
        File f = new File(USER_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (List<User>) in.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Speichert Parkplatz-Liste als Binärdatei
    @SuppressWarnings("unchecked")
    public static void speichereParkplaetze(List<Parkplatz> liste) {
        checkDir();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(PARK_FILE))) {
            out.writeObject(liste);
        } catch (IOException e) {
            System.err.println("Fehler parkplaetze: " + e.toString());
        }
    }

    // Lädt Parkplatz-Liste von Festplatte
    @SuppressWarnings("unchecked")
    public static List<Parkplatz> ladeParkplaetze() {
        File f = new File(PARK_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Parkplatz>) in.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Speichert Buchungs-Liste als Binärdatei
    @SuppressWarnings("unchecked")
    public static void speichereBuchungen(List<Buchung> liste) {
        checkDir();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(BUCH_FILE))) {
            out.writeObject(liste);
        } catch (IOException e) {
            System.err.println("Fehler buchungen: " + e.toString());
        }
    }

    // Lädt Buchungs-Liste von Festplatte
    @SuppressWarnings("unchecked")
    public static List<Buchung> ladeBuchungen() {
        File f = new File(BUCH_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Buchung>) in.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Speichert den aktuell eingeloggten User (damit er beim Neustarten noch logged ist)
    public static void speichereSystemDaten(User aktuellerNutzer) {
        checkDir();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SYS_FILE))) {
            out.writeObject(aktuellerNutzer);
        } catch (IOException e) {
            System.err.println("Fehler system_data: " + e.toString());
        }
    }

    // Lädt den aktuell eingeloggten User von Festplatte (falls vorhanden, sonst null)
    public static User ladeSystemDaten() {
        File f = new File(SYS_FILE);
        if (!f.exists()) return null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (User) in.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
