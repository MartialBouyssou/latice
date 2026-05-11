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

public class RackController implements GameListener {
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
        rackImageViews = new ArrayList<>();
        rackImageViews.add(rackTile1);
        rackImageViews.add(rackTile2);
        rackImageViews.add(rackTile3);
        rackImageViews.add(rackTile4);
        rackImageViews.add(rackTile5);
    }

    public void setGame(Game game) {
        this.game = game;
        game.addListener(this);
    }

    private void updateRack() {
        if (game == null || game.getCurrentPlayer() == null) {
            return;
        }

        Player currentPlayer = game.getCurrentPlayer();
        List<Tile> tiles = currentPlayer.getRack().getTiles();

        for (int i = 0; i < 5; i++) {
            if (i < tiles.size()) {
                Tile tile = tiles.get(i);
                String imagePath = tile.getTileImgPath(tile);
                ImageView imageView = rackImageViews.get(i);
                imageView.setImage(new Image(imagePath));
                imageView.setVisible(true);
            } else {
                rackImageViews.get(i).setVisible(false);
            }
        }
    }

    @Override
    public void onGameStarted(GameStartedEvent event) {
        updateRack();
    }

    @Override
    public void onGameOver(GameOverEvent event) {

    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        updateRack();
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        updateRack();
    }

    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        updateRack();
    }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Move");
        alert.setHeaderText("Invalid Move");
        // alert.setContentText(event.getReason());
    }

    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        updateRack();
    }
}