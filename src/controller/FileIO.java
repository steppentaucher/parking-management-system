package controller;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileIO {
    private static final String DIR = "speicher";
    private static final String USER_FILE = DIR + "/users.txt";
    private static final String PARK_FILE = DIR + "/parkplaetze.txt";
    private static final String BUCH_FILE = DIR + "/buchungen.txt";
    private static final String SYS_FILE = DIR + "/system_data.txt";

    private static void checkDir() {
        File folder = new File(DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    @SuppressWarnings("unchecked")
    public static void speichereUser(List<User> liste) {
        checkDir();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            out.writeObject(liste);
        } catch (IOException e) {
            System.err.println("Fehler users: " + e.toString());
        }
    }

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

    @SuppressWarnings("unchecked")
    public static void speichereParkplaetze(List<Parkplatz> liste) {
        checkDir();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(PARK_FILE))) {
            out.writeObject(liste);
        } catch (IOException e) {
            System.err.println("Fehler parkplaetze: " + e.toString());
        }
    }

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

    @SuppressWarnings("unchecked")
    public static void speichereBuchungen(List<Buchung> liste) {
        checkDir();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(BUCH_FILE))) {
            out.writeObject(liste);
        } catch (IOException e) {
            System.err.println("Fehler buchungen: " + e.toString());
        }
    }

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

    public static void speichereSystemDaten(User aktuellerNutzer) {
        checkDir();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SYS_FILE))) {
            out.writeObject(aktuellerNutzer);
        } catch (IOException e) {
            System.err.println("Fehler system_data: " + e.toString());
        }
    }

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
