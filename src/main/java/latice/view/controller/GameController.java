package latice.view.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import latice.model.board.Board;
import latice.model.game.Game;
import latice.model.player.Player;
import latice.model.tile.Tile;
import latice.model.tile.TileFactory;

import java.util.List;

public class GameController {

    @FXML private HBox rackBox;
    @FXML private GridPane board;
    @FXML private Label currentPlayerLabel;
    @FXML private ImageView rackTile1;
    @FXML private ImageView rackTile2;
    @FXML private ImageView rackTile3;
    @FXML private ImageView rackTile4;
    @FXML private ImageView rackTile5;

    private Game game;
    private Board gameBoard;
    private List<ImageView> rackImageViews;

    @FXML
    public void initialize() {
        rackImageViews = List.of(rackTile1, rackTile2, rackTile3, rackTile4, rackTile5);

        gameBoard = Board.create();

        for (Node node : rackBox.getChildren()) {
            if (node instanceof ImageView) {
                setupDraggable((ImageView) node);
            }
        }

        for (Node node : board.getChildren()) {
            if (node instanceof ImageView) {
                setupDropTarget((ImageView) node);
            }
        }
    }

    public void updateRack(Player player) {
        List<Tile> tiles = player.getRack().getTiles();
        currentPlayerLabel.setText("Tour de " + player.getName() + " :");

        for (int i = 0; i < 5; i++) {
            if (i < tiles.size()) {
                Tile tile = tiles.get(i);
                String imagePath = tile.getTileImgPath();
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

    private void setupDraggable(ImageView tileView) {
        tileView.setOnDragDetected(event -> {
            Dragboard db = tileView.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            String imageUrl = tileView.getImage().getUrl();
            content.putString(imageUrl);

            db.setContent(content);

            db.setDragView(tileView.getImage());

            event.consume();
        });

        tileView.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                rackBox.getChildren().remove(tileView);
            }
            event.consume();
        });
    }

    private void setupDropTarget(ImageView cellView) {
        cellView.setOnDragOver(event -> {
            if (event.getGestureSource() != cellView && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        cellView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String imageUrl = db.getString();

                Integer colIndex = GridPane.getColumnIndex(cellView);
                Integer rowIndex = GridPane.getRowIndex(cellView);
                int x = (colIndex == null) ? 0 : colIndex;
                int y = (rowIndex == null) ? 0 : rowIndex;

                Tile tileToPlace = extractTileFromUrl(imageUrl);

                if (tileToPlace != null) {
                    gameBoard.placeTile(tileToPlace, x, y);
                    cellView.setImage(new Image(imageUrl));
                    success = true;
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private Tile extractTileFromUrl(String fullUrl) {
        String filename = fullUrl.substring(fullUrl.lastIndexOf('/') + 1);
        String mapKey = "latice/images/" + filename;

        return TileFactory.getTileFromImagePath(mapKey);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}