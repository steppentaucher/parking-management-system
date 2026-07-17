package view;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
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
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

import controller.IPlattformManager;

public class LoginView extends JPanel {
    private IPlattformManager manager;
    private IMainFrame mainFrame;

    private JTextField txtName;
    private JTextField txtEmail;
    private JComboBox<String> cmbTyp;

    private JButton btnLogin;
    private JButton btnRegistrieren;
    private JButton btnModusWechseln;

    private JLabel lblTitel;
    private JLabel lblName;
    private JLabel lblTyp;

    // Styling wie KundenDashboard
    private static final java.awt.Color PAGE_BG = new java.awt.Color(245, 247, 255);
    private static final java.awt.Color CARD_BG = java.awt.Color.WHITE;
    private static final java.awt.Color PRIMARY = new java.awt.Color(79, 70, 229);
    private static final java.awt.Color PRIMARY_DARK = new java.awt.Color(67, 56, 202);
    private static final java.awt.Color ACCENT = new java.awt.Color(59, 130, 246);
    private static final java.awt.Color TEXT_DARK = new java.awt.Color(30, 41, 59);
    private static final java.awt.Color BORDER = new java.awt.Color(226, 232, 240);

    //Speichert, ob sich die Ansicht aktuell im Registrierungsmodus befindet
    private boolean registrierenModus = false;

    public LoginView(IMainFrame mf, IPlattformManager pm) {//Konstruktor
        this.mainFrame = mf;
        this.manager = pm;

        // BG wie KundenDashboard
        setLayout(new GridBagLayout());
        setBackground(PAGE_BG);

        // Das Content-Panel als abgerundete Karte
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 20, 1),
                new EmptyBorder(22, 22, 22, 22)));

     // GridBagConstraints steuern Position, Abstand und Ausrichtung der Elemente
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

     // Titel der Ansicht, standardmäßig zunächst "Login"
        lblTitel = new JLabel("Login", SwingConstants.CENTER);
        lblTitel.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitel.setForeground(TEXT_DARK);
        // Header als Gradient-Panel
        JPanel header = new GradientPanel();
        header.setLayout(new GridBagLayout());
        header.setBorder(new EmptyBorder(14, 18, 14, 18));
        header.add(lblTitel);
        header.setPreferredSize(new java.awt.Dimension(0, 64));
        gbc.gridwidth = 2;
        card.add(header, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

     // Label und Eingabefeld für Namen
     // nur im Registrierungsmodus sichtbar
        lblName = new JLabel("Name:");
        lblName.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblName.setForeground(TEXT_DARK);
        card.add(lblName, gbc);

        gbc.gridx = 1;
        txtName = new JTextField(20);
        styleTextField(txtName);
        card.add(txtName, gbc);

     // Label und Eingabefeld für E-Mail-Adresse
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblEmail = new JLabel("E-Mail:");
        lblEmail.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblEmail.setForeground(TEXT_DARK);
        card.add(lblEmail, gbc);

        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        styleTextField(txtEmail);
        card.add(txtEmail, gbc);

     // Label und Auswahlbox für den Nutzertyp
     // nur für die Registrierung relevant
        gbc.gridx = 0;
        gbc.gridy++;
        lblTyp = new JLabel("Typ:");
        lblTyp.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTyp.setForeground(TEXT_DARK);
        card.add(lblTyp, gbc);

        gbc.gridx = 1;
        cmbTyp = new JComboBox<>(new String[]{"Kunde", "Betreiber"});
        styleComboBox(cmbTyp);
        card.add(cmbTyp, gbc);

     // Login-Button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        btnLogin = new GradientButton("Login");
        btnLogin.setPreferredSize(new java.awt.Dimension(220, 46));
        card.add(btnLogin, gbc);

     // Registrieren-Button
        btnRegistrieren = new GradientButton("Registrieren");
        btnRegistrieren.setPreferredSize(new java.awt.Dimension(220, 46));
        card.add(btnRegistrieren, gbc);

     // Button zum Umschalten zwischen beiden Modi
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btnModusWechseln = new JButton("Zu Registrierung wechseln");
        btnModusWechseln.setFocusPainted(false);
        btnModusWechseln.setBorderPainted(false);
        btnModusWechseln.setForeground(PRIMARY_DARK);
        btnModusWechseln.setFont(new Font("SansSerif", Font.PLAIN, 13));
        card.add(btnModusWechseln, gbc);

     // Das Card-Panel der eigentlichen View hinzufügen (zentriert)
        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        add(card, outer);

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

    // Hilfsmethoden für den Stil
    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBackground(java.awt.Color.WHITE);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(PRIMARY_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new java.awt.Color(203, 213, 225), 16, 1),
                new EmptyBorder(8, 10, 8, 10)));
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        comboBox.setBackground(java.awt.Color.WHITE);
        comboBox.setForeground(TEXT_DARK);
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new java.awt.Color(203, 213, 225), 16, 1),
                new EmptyBorder(4, 8, 4, 8)));
    }

    // Kleine, wiederverwendbare UI-Klassen 
    private static class RoundedLineBorder extends AbstractBorder {
        private final java.awt.Color color;
        private final int radius;
        private final int thickness;

        public RoundedLineBorder(java.awt.Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(java.awt.Component c) {
            return new Insets(3, 3, 3, 3);
        }

        @Override
        public Insets getBorderInsets(java.awt.Component c, Insets insets) {
            insets.left = 3;
            insets.right = 3;
            insets.top = 3;
            insets.bottom = 3;
            return insets;
        }

        @Override
        public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(
                    x + 0.5,
                    y + 0.5,
                    width - 1.0,
                    height - 1.0,
                    radius,
                    radius));
            g2.dispose();
        }
    }

    private static class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(java.awt.Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 15));
            setMargin(new Insets(10, 18, 10, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            java.awt.Color start = getModel().isPressed() ? PRIMARY_DARK : PRIMARY;
            java.awt.Color end = getModel().isRollover() ? ACCENT : PRIMARY_DARK;

            g2.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(
                    0, 0, new java.awt.Color(99, 102, 241),
                    getWidth(), getHeight(), new java.awt.Color(59, 130, 246));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
        }

        @Override
        public boolean isOpaque() {
            return false;
        }
    }
}