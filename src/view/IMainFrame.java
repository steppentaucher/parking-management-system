package view;

/**
 * Minimales Interface für das Hauptfenster, damit Views nicht direkt die konkrete
 * MainFrame-Klasse benötigen.
 */
public interface IMainFrame {
    void zeigePassendeAnsichtNachLogin();
    void zeigeLoginView();
}
