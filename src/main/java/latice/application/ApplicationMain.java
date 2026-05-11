package latice.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class ApplicationMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/latice/view/mainGame.fxml")));
            primaryStage.setScene(new Scene(root, 1920, 1080));
        } catch (Exception e) {
            BorderPane errorPane = new BorderPane();
            errorPane.setCenter(new Label("Error loading the application: " + e.getMessage()));
            primaryStage.setScene(new Scene(errorPane, 400, 200));
        }

        primaryStage.setTitle("Latice");
        primaryStage.show();
    }
}
