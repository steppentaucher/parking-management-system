package model;

import java.util.ArrayList;
import java.util.List;

public class Kunde extends User {
    private List<Buchung> meineBuchungen;

    public Kunde(String id, String name, String email) {
        super(id, name, email);
        this.meineBuchungen = new ArrayList<>();
    }

    public List<Buchung> getMeineBuchungen() {
        return meineBuchungen;
    }

    public void addBuchung(Buchung b) {
        if (b != null) {
            meineBuchungen.add(b);
        }
    }
    //Todo: Impliementierung
}
