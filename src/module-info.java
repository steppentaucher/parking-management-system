module BPM {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    
    // Bindet den LGoodDatePicker über den korrekten automatischen Modulnamen ein
    requires lgooddatepicker;
    
    // Erlaubt JavaFX den Zugriff auf eure Klassen
    opens main to javafx.graphics, javafx.fxml;
    opens controller to javafx.graphics, javafx.fxml;
    opens view to javafx.graphics, javafx.fxml;
    opens model to javafx.base, javafx.graphics;
    opens showcase to javafx.graphics, javafx.fxml;
}
