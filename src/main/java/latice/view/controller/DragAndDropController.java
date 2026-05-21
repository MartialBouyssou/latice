package latice.view.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import latice.model.board.Board;
import latice.model.tile.Tile;
import latice.model.tile.TileFactory;

public class DragAndDropController {
    @FXML private HBox rackBox;
    @FXML private GridPane board;

    private Board gameBoard;

    public void initialize() {
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
                    try {
                        gameBoard.placeTile(tileToPlace, x, y);

                        cellView.setImage(new Image(imageUrl));

                        success = true;

                    } catch (Exception e) {
                        System.out.println("Placement impossible : " + e.getClass().getSimpleName());
                    }
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private Tile extractTileFromUrl(String fullUrl) {
        try {
            String filename = fullUrl.substring(fullUrl.lastIndexOf('/') + 1);
            String mapKey = "latice/images/" + filename;

            return TileFactory.getTileFromImagePath(mapKey);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction de la tuile : " + fullUrl);
            return null;
        }
    }
}