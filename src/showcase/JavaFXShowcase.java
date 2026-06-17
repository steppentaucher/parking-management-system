package showcase;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

/**
 *Interface Showcase
 * 
 * HINWEIS: Diese Klasse muss in einem Projekt mit moduler Struktur (module-info.java)
 * oft ueber einen speziellen Launcher gestartet werden, wenn sie im 'test' Ordner liegt.
 */
public class JavaFXShowcase extends Application {

    private static final Color CYAN = Color.web("#00f2ff");
    private static final Color PURPLE = Color.web("#7000ff");
    private static final Random random = new Random();

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #050505;");
        root.setPrefSize(1000, 700);

        // --- 1. Background Particles (Screensaver Style) ---
        for (int i = 0; i < 40; i++) {
            Circle particle = new Circle(random.nextDouble() * 2 + 1, Color.web("#ffffff", 0.15));
            particle.setTranslateX(random.nextDouble() * 1000 - 500);
            particle.setTranslateY(random.nextDouble() * 700 - 350);
            root.getChildren().add(particle);
            animateParticle(particle);
        }

        // --- 2. The Neural Core (Central Animation) ---
        StackPane coreContainer = new StackPane();
        
        Circle glow1 = createGlowCircle(150, CYAN, 0.2, 3.0);
        Circle glow2 = createGlowCircle(130, PURPLE, 0.15, 4.5);
        Circle glow3 = createGlowCircle(110, CYAN, 0.3, 2.0);
        
        Circle core = new Circle(60);
        core.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, CYAN), new Stop(1, PURPLE)));
        core.setEffect(new Bloom(0.2));
        
        Circle innerPulse = new Circle(40, Color.WHITE);
        innerPulse.setOpacity(0.4);
        innerPulse.setEffect(new GaussianBlur(20));
        
        ScaleTransition st = new ScaleTransition(Duration.seconds(2), innerPulse);
        st.setFromX(0.8); st.setFromY(0.8);
        st.setToX(1.2); st.setToY(1.2);
        st.setAutoReverse(true); st.setCycleCount(Timeline.INDEFINITE);
        st.play();

        coreContainer.getChildren().addAll(glow1, glow2, glow3, core, innerPulse);
        
        RotateTransition rt = new RotateTransition(Duration.seconds(10), coreContainer);
        rt.setByAngle(360);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setCycleCount(Timeline.INDEFINITE);
        rt.play();

        // --- 3. Clean AI UI Overlay ---
        VBox uiOverlay = new VBox(15);
        uiOverlay.setAlignment(Pos.BOTTOM_CENTER);
        uiOverlay.setPadding(new Insets(0, 0, 80, 0));
        uiOverlay.setMouseTransparent(true);

        Label aiStatus = new Label("AI NEURAL CORE ACTIVE");
        aiStatus.setStyle("-fx-font-family: 'Segoe UI Light'; -fx-font-size: 14px; -fx-text-fill: #00f2ff;");
        applyGlow(aiStatus, CYAN);

        Label promptHint = new Label("System Status: Optimal | Awaiting Command...");
        promptHint.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.4);");

        Rectangle footer = new Rectangle(600, 2);
        footer.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT), 
                new Stop(0.5, CYAN), 
                new Stop(1, Color.TRANSPARENT)));
        footer.setOpacity(0.5);

        uiOverlay.getChildren().addAll(aiStatus, footer, promptHint);

        // --- 4. Interactive "Scanner" Line ---
        Rectangle scanner = new Rectangle(1000, 1, Color.web("#00f2ff", 0.3));
        scanner.setTranslateY(-350);
        root.getChildren().add(scanner);
        
        TranslateTransition scannerAnim = new TranslateTransition(Duration.seconds(4), scanner);
        scannerAnim.setFromY(-350);
        scannerAnim.setToY(350);
        scannerAnim.setCycleCount(Timeline.INDEFINITE);
        scannerAnim.setInterpolator(Interpolator.EASE_BOTH);
        scannerAnim.setAutoReverse(true);
        scannerAnim.play();

        root.getChildren().addAll(coreContainer, uiOverlay);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("BPM - Advanced AI Interface");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Circle createGlowCircle(double radius, Color color, double opacity, double duration) {
        Circle c = new Circle(radius);
        c.setFill(Color.TRANSPARENT);
        c.setStroke(color);
        c.setStrokeWidth(2);
        c.setOpacity(opacity);
        c.setEffect(new GaussianBlur(30));
        
        ScaleTransition st = new ScaleTransition(Duration.seconds(duration), c);
        st.setFromX(0.9); st.setFromY(0.9);
        st.setToX(1.1); st.setToY(1.1);
        st.setAutoReverse(true); st.setCycleCount(Timeline.INDEFINITE);
        st.play();
        
        return c;
    }

    private void animateParticle(Circle p) {
        double destX = random.nextDouble() * 1000 - 500;
        double destY = random.nextDouble() * 700 - 350;
        double duration = 10 + random.nextDouble() * 20;

        TranslateTransition tt = new TranslateTransition(Duration.seconds(duration), p);
        tt.setToX(destX);
        tt.setToY(destY);
        tt.setOnFinished(e -> animateParticle(p));
        tt.play();

        FadeTransition ft = new FadeTransition(Duration.seconds(duration / 2), p);
        ft.setFromValue(0.1);
        ft.setToValue(0.4);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.play();
    }

    private void applyGlow(Label label, Color color) {
        DropShadow ds = new DropShadow();
        ds.setColor(color);
        ds.setRadius(10);
        ds.setSpread(0.5);
        label.setEffect(ds);
    }

    /**
     * WORKAROUND LAUNCHER:
     * In modularen Projekten kann JavaFX nicht auf Klassen im 'test' Ordner zugreifen,
     * wenn das Modul sie nicht oeffnet. Dieser Launcher umgeht das Problem.
     */
    public static class Launcher {
        public static void main(String[] args) {
            Application.launch(JavaFXShowcase.class, args);
        }
    }

    public static void main(String[] args) {
        Launcher.main(args);
    }
}
