package latice.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import latice.model.game.Game;
import latice.model.game.events.*;
import latice.model.player.Player;
import latice.model.tile.Tile;

import java.util.ArrayList;
import java.util.List;

import static latice.application.LaticeApplicationConsole.message;

public class RackController {
    @FXML private HBox rackBox;
    @FXML private ImageView rackTile1;
    @FXML private ImageView rackTile2;
    @FXML private ImageView rackTile3;
    @FXML private ImageView rackTile4;
    @FXML private ImageView rackTile5;

    private Game game;
    private List<ImageView> rackImageViews;

    @FXML
    public void initialize() {
        rackImageViews = List.of(rackTile1, rackTile2, rackTile3, rackTile4, rackTile5);
    }

    public void updateRack(Player player) {
        List<Tile> tiles = player.getRack().getTiles();

        for (int i = 0; i < 5; i++) {
            if (i < tiles.size()) {
                Tile tile = tiles.get(i);
                String imagePath = tile.getTileImgPath();
                message(imagePath);
                ImageView imageView = rackImageViews.get(i);
                imageView.setImage(new Image(imagePath));
                imageView.setVisible(true);
            } else {
                rackImageViews.get(i).setVisible(false);
            }
        }
    }

    public void showInvalidMove() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Move");
        alert.setHeaderText("Invalid Move");
        alert.show();
    }
}