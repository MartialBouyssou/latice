package latice.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import latice.utils.LangManager;

public class ApplicationMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/latice/view/mainGame.fxml"));

            Parent root = loader.load();

            primaryStage.setScene(new Scene(root, 1920, 1080));
            primaryStage.setTitle(LangManager.getInstance().get("app.title"));
            primaryStage.show();

        } catch (Exception e) {
            BorderPane errorPane = new BorderPane();
            errorPane.setCenter(new Label("Error: " + e.getMessage()));
            primaryStage.setScene(new Scene(errorPane, 400, 200));
            primaryStage.show();
        }
    }
}
