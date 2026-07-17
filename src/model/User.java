package model;

import java.io.Serializable;

/**
 * Abstrakte Basis-Klasse für alle Nutzer (Kunde oder Betreiber)
 * Speichert gemeinsame Daten: eindeutige ID, Name und E-Mail
 * Implementiert Serializable damit User-Objekte gespeichert werden können
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
