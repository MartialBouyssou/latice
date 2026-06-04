package latice.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import latice.utils.LangManager;

import java.util.Objects;

public class ApplicationMain extends Application {

    /**
     * Application entry point that launches the JavaFX runtime.
     * @param args The command line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes and displays the main game window by loading its FXML view.
     * If loading fails, shows an error pane with the failure message instead.
     * @param primaryStage The primary stage provided by the JavaFX runtime.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/latice/view/mainGame.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root, 1920, 1080);
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/latice/style/game.css")).toExternalForm()
            );
            primaryStage.setScene(scene);
            primaryStage.setTitle(LangManager.getInstance().get("app.title"));
            primaryStage.setOnCloseRequest(event -> System.exit(0));
            primaryStage.show();

        } catch (Exception e) {
            BorderPane errorPane = new BorderPane();
            errorPane.setCenter(new Label("Error: " + e.getMessage()));
            primaryStage.setScene(new Scene(errorPane, 400, 200));
            primaryStage.show();
        }
    }
}
