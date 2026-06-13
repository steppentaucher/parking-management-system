module BPM {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    
    // Erlaubt JavaFX den Zugriff auf eure Klassen
    opens main to javafx.graphics, javafx.fxml;
    opens controller to javafx.graphics, javafx.fxml;
    opens view to javafx.graphics, javafx.fxml;
    opens model to javafx.base, javafx.graphics;
    
    // Erlaubt JavaFX den Zugriff auf die Showcase-Klasse in den Tests
    opens tests to javafx.graphics, javafx.fxml;
}
