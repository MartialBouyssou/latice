package latice.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import latice.application.listeners.LaticeGameListener;
import latice.model.game.Game;
import latice.model.referee.Referee;
import latice.model.referee.rule.AdjacencyRule;
import latice.model.referee.rule.IRule;
import latice.model.referee.rule.MatchingRule;
import latice.view.controller.DragAndDropController;
import latice.view.controller.GameController;

import java.util.List;

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

            GameController gameController = loader.getController();

            Game game = buildGame();
            game.addListener(new LaticeGameListener(gameController));

            primaryStage.setScene(new Scene(root, 1920, 1080));
            primaryStage.setTitle("Latice");
            primaryStage.show();

            gameController.setGame(game);

            game.startGame();

        } catch (Exception e) {
            BorderPane errorPane = new BorderPane();
            errorPane.setCenter(new Label("Error: " + e.getMessage()));
            primaryStage.setScene(new Scene(errorPane, 400, 200));
            primaryStage.show();
        }
    }

    private Game buildGame() {
        List<IRule> rules = List.of(new MatchingRule(), new AdjacencyRule());
        return new Game(new Referee(rules));
    }
}
