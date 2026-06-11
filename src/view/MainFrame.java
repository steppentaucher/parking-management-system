package view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.PlattformManager;
import model.Betreiber;
import model.Kunde;
import model.User;

// Diese Klasse ist das Hauptfenster der Anwendung.
// Sie zeigt je nach Situation die LoginView, KundenView oder BetreiberView an.
public class MainFrame extends JFrame {

    // Zentrale Verwaltungsinstanz der Anwendung
    private PlattformManager manager;

    // Enthält das aktuell angezeigte Panel im Fenster
    private JPanel aktuellesPanel;

    // Konstruktor des Hauptfensters
    public MainFrame(PlattformManager manager) {
        this.manager = manager;

        // Fenstertitel setzen
        setTitle("Parkplatzmanagement System");

        // Anwendung beim Schließen vollständig beenden
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Standardgröße des Fensters
        setSize(1000, 700);

        // Fenster mittig auf dem Bildschirm platzieren
        setLocationRelativeTo(null);

        // Layout für das Hauptfenster festlegen
        setLayout(new BorderLayout());
    }

    // Zeigt die Login-Ansicht an
    public void zeigeLoginView() {
        setzePanel(new LoginView(this, manager));
    }

    // Zeigt die Kundenansicht an
    public void zeigeKundenView() {
        setzePanel(new KundenDashboardView(manager));
    }

    // Zeigt die Betreiberansicht an
    public void zeigeBetreiberView() {
        setzePanel(new BetreiberDashboardView(manager));
    }

    /*
     * Prüft nach dem Login, welcher Nutzertyp aktuell angemeldet ist.
     * Je nach Typ wird automatisch die passende Ansicht geladen.
     * Falls kein passender Nutzer erkannt wird, wird eine Fehlermeldung angezeigt
     * und vorsichtshalber wieder die LoginView geöffnet.
     */
    public void zeigePassendeAnsichtNachLogin() {
        // Aktuell eingeloggten Nutzer abrufen
        User user = manager.getAktuellerNutzer();

        // Je nach Nutzertyp passende Ansicht öffnen
        if (user instanceof Kunde) {
            zeigeKundenView();
        } else if (user instanceof Betreiber) {
            zeigeBetreiberView();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Für den aktuellen Nutzer konnte keine passende Ansicht geladen werden."
            );

            zeigeLoginView();
        }
    }

    /*
     * Ersetzt das aktuell sichtbare Panel durch ein neues.
     * So kann zwischen verschiedenen Views umgeschaltet werden,
     * ohne ein neues Fenster öffnen zu müssen.
     */
    private void setzePanel(JPanel neuesPanel) {
        // Altes Panel entfernen, falls bereits eines angezeigt wird
        if (aktuellesPanel != null) {
            remove(aktuellesPanel);
        }

        // Neues Panel speichern und im Zentrum des Fensters anzeigen
        aktuellesPanel = neuesPanel;
        add(aktuellesPanel, BorderLayout.CENTER);

        // Oberfläche aktualisieren, damit die Änderung sichtbar wird
        revalidate();
        repaint();
    }
}