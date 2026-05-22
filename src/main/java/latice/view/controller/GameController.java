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

    public void showInvalidMove() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Move");
        alert.setHeaderText("Invalid Move");
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
                    System.out.println("chargement de l'image : " + tile.getTileImgPath());
                    ((ImageView) node).setImage(new Image(tile.getTileImgPath()));
                    break;
                }
            }

            updateRack(player);

        } catch (Exception e) {
            System.err.println("ERREUR lors de l'affichage de la tuile sur l'interface :");
        }
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
                    game.playTile(tile, col, row);
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