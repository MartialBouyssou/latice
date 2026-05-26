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
import latice.model.game.Game;
import latice.model.game.events.TurnPassedEvent;
import latice.model.player.Player;
import latice.model.tile.Tile;
import latice.model.tile.TileFactory;

import java.util.List;

public class GameController {

    @FXML private HBox rackBox;
    @FXML private GridPane board;
    @FXML private Label currentPlayerLabel;
    @FXML private Label currentPlayerScoreLabel;
    @FXML private ImageView rackTile1;
    @FXML private ImageView rackTile2;
    @FXML private ImageView rackTile3;
    @FXML private ImageView rackTile4;
    @FXML private ImageView rackTile5;

    private Game game;
    private List<ImageView> rackImageViews;
    public boolean hasAlreadyPlaced = false;

    @FXML
    public void initialize() {
        rackImageViews = List.of(rackTile1, rackTile2, rackTile3, rackTile4, rackTile5);

        for (Node node : rackBox.getChildren()) {
            if (node instanceof ImageView) {
                setupDraggable((ImageView) node);
            }
        }

        setupBoardDropTarget();
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

    public void updateScore(Player player) {
        currentPlayerScoreLabel.setText("Score : " + player.getScore());
    }

    public void showInvalidMove(String reason) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Move");
        alert.setHeaderText("You can't do that..");
        alert.setContentText(reason);
        alert.show();
    }

    public void showGameOver(Player player) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game ended");
        alert.setHeaderText("Winner :" + player.getName());
        alert.setContentText("Player" + player.getName() + " won with a score of "
                + player.getScore() + " points !");
        alert.show();
    }

    public void placeTileOnBoard(Tile tile, int targetX, int targetY, Player player) {
        try {
            for (Node node : board.getChildren()) {
                Integer colIndex = GridPane.getColumnIndex(node);
                Integer rowIndex = GridPane.getRowIndex(node);
                int x = (colIndex == null) ? 0 : colIndex;
                int y = (rowIndex == null) ? 0 : rowIndex;

                if (x == targetX && y == targetY && node instanceof ImageView) {
                    ((ImageView) node).setImage(new Image(tile.getTileImgPath()));
                    break;
                }
            }

            updateRack(player);

        } catch (Exception e) { /* No need to debug this catch */ }
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
            event.consume();
        });
    }

    private void setupBoardDropTarget() {
        board.setOnDragOver(event -> {
            if (event.getGestureSource() != board && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        board.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString() && game != null) {
                double cellWidth  = board.getWidth()  / 9.0;
                double cellHeight = board.getHeight() / 9.0;

                int col = (int) (event.getX() / cellWidth);
                int row = (int) (event.getY() / cellHeight);

                Tile tile = extractTileFromUrl(db.getString());

                if (tile != null) {
                    if (!hasAlreadyPlaced) {
                        game.playTile(tile, col, row);
                    } else {
                        showInvalidMove("You already placed a tile this turn ! " +
                                "Buy an action or end your turn !");
                    }
                } else {
                    System.err.println("Tuile non reconnue depuis l'URL : " + db.getString());
                }
            } else {
                System.err.println("Dragboard vide ou Game = null");
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void buttonExchangeRack(){};

    public void buttonBuyAnAction(){};

    public void buttonEndTurn(){};

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


            if (db.hasString() && game != null) {
                String imageUrl = db.getString();
                System.out.println("URL captée depuis la souris : " + imageUrl);

                Integer colIndex = GridPane.getColumnIndex(cellView);
                Integer rowIndex = GridPane.getRowIndex(cellView);
                int x = (colIndex == null) ? 0 : colIndex;
                int y = (rowIndex == null) ? 0 : rowIndex;
                System.out.println("Coordonnées ciblées : X=" + x + ", Y=" + y);

                Tile tileToPlace = extractTileFromUrl(imageUrl);

                if (tileToPlace != null) {
                    System.out.println("Tuile reconnue ! Chemin de l'image : " + tileToPlace.getTileImgPath());
                    try {
                        game.playTile(tileToPlace, x, y);
                        success = true;
                        System.out.println("Ordre playTile() envoyé au modèle avec succès.");
                    } catch (Exception e) {
                        System.err.println("Crash à l'intérieur du moteur de jeu (playTile) :");
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("La tuile n'a pas été reconnue ! extractTileFromUrl a renvoyé null.");
                }
            } else {
                System.err.println("Dragboard vide ou objet Game = null");
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void setupPassButton() {
        // TODO: [TURN PASS] this is the button that allow player to skip or end turn when pressed
        // add the button action
        if (!hasAlreadyPlaced) {
            game.sendEvent(new TurnPassedEvent(game.getCurrentPlayer(),
                    game.getNextPlayer(), game.getPlayersTurnScore()));
        } else {
            game.endTurn();
        }
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