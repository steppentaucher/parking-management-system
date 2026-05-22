package model;

import java.util.ArrayList;
import java.util.List;

public class Betreiber extends User {
    private List<Parkplatz> meineParkplaetze;

    public Betreiber(String id, String name, String email) {
        super(id, name, email);
        this.meineParkplaetze = new ArrayList<>();
    }

    public List<Parkplatz> getMeineParkplaetze() {
        return meineParkplaetze;
    }

    public void addParkplatz(Parkplatz p) {
        if (p != null) {
            meineParkplaetze.add(p);
        }
    }
    //Todo: Impliementierung
}
