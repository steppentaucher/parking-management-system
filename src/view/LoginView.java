package view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import controller.PlattformManager;

public class LoginView extends JPanel {
    private PlattformManager manager;
    private MainFrame mainFrame;

    private JTextField txtName;
    private JTextField txtEmail;
    private JComboBox<String> cmbTyp;

    private JButton btnLogin;
    private JButton btnRegistrieren;
    private JButton btnModusWechseln;

    private JLabel lblTitel;
    private JLabel lblName;
    private JLabel lblTyp;

    //Speichert, ob sich die Ansicht aktuell im Registrierungsmodus befindet
    private boolean registrierenModus = false;

    public LoginView(MainFrame mf, PlattformManager pm) {//Konstruktor
        this.mainFrame = mf;
        this.manager = pm;

        // gesamte LoginView verwendet ein GridBagLayout
        // damit das innere Panel mittig angeordnet werden kann
        setLayout(new GridBagLayout());

     // Das Content-Panel enthält die eigentlichen UI-Elemente der Maske
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

     // GridBagConstraints steuern Position, Abstand und Ausrichtung der Elemente
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

     // Titel der Ansicht, standardmäßig zunächst "Login"
        lblTitel = new JLabel("Login", SwingConstants.CENTER);
        lblTitel.setFont(new Font("Arial", Font.BOLD, 22));
        content.add(lblTitel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

     // Label und Eingabefeld für Namen
     // nur im Registrierungsmodus sichtbar
        lblName = new JLabel("Name:");
        content.add(lblName, gbc);

        gbc.gridx = 1;
        txtName = new JTextField(20);
        content.add(txtName, gbc);

     // Label und Eingabefeld für E-Mail-Adresse
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblEmail = new JLabel("E-Mail:");
        content.add(lblEmail, gbc);

        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        content.add(txtEmail, gbc);

     // Label und Auswahlbox für den Nutzertyp
     // nur für die Registrierung relevant
        gbc.gridx = 0;
        gbc.gridy++;
        lblTyp = new JLabel("Typ:");
        content.add(lblTyp, gbc);

        gbc.gridx = 1;
        cmbTyp = new JComboBox<>(new String[]{"Kunde", "Betreiber"});
        content.add(cmbTyp, gbc);

     // Login-Button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        btnLogin = new JButton("Login");
        content.add(btnLogin, gbc);

     // Registrieren-Button
        btnRegistrieren = new JButton("Registrieren");
        content.add(btnRegistrieren, gbc);

     // Button zum Umschalten zwischen beiden Modi
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btnModusWechseln = new JButton("Zu Registrierung wechseln");
        content.add(btnModusWechseln, gbc);

     // Das Content-Panel wird der eigentlichen View hinzugefügt
        add(content);

       // Event-Handler für Benutzeraktionen (login, registrierung, wechseln)
        btnLogin.addActionListener(e -> handleLogin());
        btnRegistrieren.addActionListener(e -> handleRegistrierung());
        btnModusWechseln.addActionListener(e -> modusWechseln());

        aktualisiereModus();
    }

    // Wechselt zwischen Login-Modus und Registrierungsmodus
    // Nach dem Umschalten wird die Oberfläche aktualisiert

    private void modusWechseln() {
        registrierenModus = !registrierenModus;
        aktualisiereModus();
    }


     // Passt die Oberfläche an den aktuellen Modus an
     // Im Registrierungsmodus werden zusätzliche Felder eingeblendet
     // im Login-Modus werden nur die für den Login nötigen Felder angezeigt

    private void aktualisiereModus() {
    	 // Überschrift abhängig vom aktuellen Modus setzen
        lblTitel.setText(registrierenModus ? "Registrieren" : "Login");

        // Namensfeld und Nutzertyp nur bei Registrierung anzeigen
        lblName.setVisible(registrierenModus);
        txtName.setVisible(registrierenModus);
        lblTyp.setVisible(registrierenModus);
        cmbTyp.setVisible(registrierenModus);
        btnRegistrieren.setVisible(registrierenModus);

        // Login-Button nur im Login-Modus anzeigen
        btnLogin.setVisible(!registrierenModus);

        // Beschriftung des Umschalt-Buttons anpassen je nach modus
        btnModusWechseln.setText(
                registrierenModus
                        ? "Zu Login wechseln"
                        : "Zu Registrierung wechseln"
        );

     // Oberfläche nach Änderungen neu berechnen und neu zeichnen
        revalidate();
        repaint();
    }

    //Führt den Login-Vorgang aus
    // Die eingegebene E-Mail wird eingelesen, geprüft und an den PlattformManager übergeben
    // Bei Erfolg wird zur passenden Benutzeransicht gewechselt
    private void handleLogin() {
    	// E-Mail aus dem Textfeld lesen, Leerzeichen entfernen und in Kleinbuchstaben umwandeln
    	String email = txtEmail.getText().trim().toLowerCase();

    	// Prüfung, ob überhaupt eine E-Mail eingegeben wurde.
        if (email.isBlank()) {
            JOptionPane.showMessageDialog(this, "Bitte gib eine E-Mail-Adresse ein.");
            return;
        }

        // Login beim PlattformManager durchführen
        boolean erfolgreich = manager.login(email);

     // Je nach Ergebnis Rückmeldung geben und bei Erfolg zur passenden Ansicht wechseln
        if (erfolgreich) {
            JOptionPane.showMessageDialog(this, "Login erfolgreich.");
            mainFrame.zeigePassendeAnsichtNachLogin();
        } else {
            JOptionPane.showMessageDialog(this, "Login fehlgeschlagen. Nutzer nicht gefunden.");
        }
    }

    // Führt die Registrierung eines neuen Nutzers aus
    // Dabei werden Name, E-Mail und Nutzertyp ausgelesen und validiert
    // Fehler werden über Dialogfenster angezeigt
    private void handleRegistrierung() {
    	 // Eingaben aus den Formularfeldern auslesen
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String typ = (String) cmbTyp.getSelectedItem();

     // Prüfen, ob alle Pflichtfelder ausgefüllt sind
        if (name.isBlank() || email.isBlank() || typ == null || typ.isBlank()) {
            JOptionPane.showMessageDialog(this, "Bitte fülle alle Felder aus.");
            return;
        }

        try {
        	 // Registrierung über den PlattformManager durchführen
            manager.registriereNutzer(name, email, typ);

            // Erfolgsrückmeldung anzeigen
                JOptionPane.showMessageDialog(
                        this,
                        "Registrierung erfolgreich. Du kannst dich jetzt einloggen.");

             // Eingabefelder nach erfolgreicher Registrierung zurücksetzen
                txtName.setText("");
                txtEmail.setText("");
                cmbTyp.setSelectedIndex(0);

             // Nach erfolgreicher Registrierung zurück in den Login-Modus wechseln
                registrierenModus = false;
                aktualisiereModus();

             // Fachliche Eingabefehler, z. B. ungültiges E-Mail-Format oder doppelte E-Mail
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Ungültige Eingabe",
                    JOptionPane.WARNING_MESSAGE
            );
         // Unerwartete Fehler, die nicht durch fachliche Validierung abgedeckt sind
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Registrierung fehlgeschlagen.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}