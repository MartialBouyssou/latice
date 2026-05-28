package latice.view.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import latice.listeners.LaticeGameListener;
import latice.model.board.cell.SunCell;
import latice.model.game.Game;
import latice.model.game.events.TurnPassedEvent;
import latice.model.player.Player;
import latice.model.referee.Referee;
import latice.model.referee.rule.*;
import latice.model.tile.Tile;
import latice.model.tile.TileFactory;

import java.util.List;
import java.util.Objects;

public class GameController {

    @FXML private HBox rackBox;
    @FXML private GridPane board;
    @FXML private Label currentPlayerLabel;
    @FXML private Label currentPlayerScoreLabel;
    @FXML private Label playerTileRemainingLabel;
    @FXML private ImageView rackTile1;
    @FXML private ImageView rackTile2;
    @FXML private ImageView rackTile3;
    @FXML private ImageView rackTile4;
    @FXML private ImageView rackTile5;
    @FXML private StackPane screenOverlay;
    @FXML private Label overlayPlayerLabel;
    @FXML private Button overlayButton;
    @FXML private Button endTurnButton;
    @FXML private Label lastPlayerLabel;
    @FXML private Label lastPlayerScoreLabel;
    @FXML private Label turnNumberLabel;

    @FXML private BorderPane gameBoardBP;

    @FXML private StackPane startMenuStackpane;
    @FXML private VBox rulesMenuVBox;
    @FXML private VBox playMenuVBox;
    @FXML private VBox usernameMenuVBox;
    @FXML private TextField player1UsernameTf;
    @FXML private TextField player2UsernameTf;

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

    public void showUsernameChoiceMenu() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        usernameMenuVBox.setVisible(true);
        usernameMenuVBox.setManaged(true);
    }

    public void showPlayMenu() {
        rulesMenuVBox.setVisible(false);
        rulesMenuVBox.setManaged(false);
        playMenuVBox.setVisible(true);
        playMenuVBox.setManaged(true);
    }

    public void showRules() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        rulesMenuVBox.setVisible(true);
        rulesMenuVBox.setManaged(true);
    }

    public void startGame() {
        playMenuVBox.setVisible(true);
        playMenuVBox.setManaged(true);
        usernameMenuVBox.setVisible(false);
        usernameMenuVBox.setManaged(false);

        startMenuStackpane.setVisible(false);
        startMenuStackpane.setManaged(false);

        Game game = buildGame();
        game.addListener(new LaticeGameListener(this));
        this.game = game;
        game.startGame();

        gameBoardBP.setVisible(true);
        gameBoardBP.setManaged(true);
    }

    private Game buildGame() {
        List<IRule> rules = List.of(new MatchingRule(), new AdjacencyRule(),
                new PositionRule(), new FirstTurnRule());

        if (player1UsernameTf.getText().equals(player2UsernameTf.getText())) {
            return new Game(Referee.create(rules));
        }
        return new Game(Referee.create(rules),
                player1UsernameTf.getText(), player2UsernameTf.getText());
    }

    public void showOverlayForPlayer(Player nextPlayer, Player lastPlayer, int lastTurnScore) {
        endTurnButton.setDisable(true);
        overlayPlayerLabel.setText("It's " + nextPlayer.getName() + "'s turn!");
        lastPlayerLabel.setText(lastPlayer.getName() + "'s score :");
        lastPlayerScoreLabel.setText(Integer.toString(lastTurnScore));
        turnNumberLabel.setText("Turn " + (game.getTurnCount()/2 + 1) + "/" + Game.MAX_TURN_COUNT);
        screenOverlay.setVisible(true);
        screenOverlay.setManaged(true);
    }

    public void hideOverlay() {
        screenOverlay.setVisible(false);
        screenOverlay.setManaged(false);
        endTurnButton.setDisable(false);
    }

    public void updateRack(Player player) {
        List<Tile> tiles = player.getRack().getTiles();

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

    public void updatePlayerInfo(Player player) {
        int remainingTiles = player.getDeck().size() + player.getRack().getTiles().size();

        currentPlayerLabel.setText(player.getName() + "'s turn:");
        currentPlayerScoreLabel.setText("Score: " + player.getScore());
        playerTileRemainingLabel.setText("Remaining Tiles: " + remainingTiles);
    }

    public void showInvalidMove(String reason) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Move");
        alert.setHeaderText("You can't do that..");
        alert.setContentText(reason);
        alert.show();
    }

    public void showGameOver(Player player) {
        hideOverlay();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game ended");
        if (player != null) {
            alert.setHeaderText("Winner: " + player.getName());
            alert.setContentText("Player" + player.getName() + " won with a score of "
                    + player.getScore() + " points !");
        } else {
            alert.setHeaderText("Tie!");
            alert.setContentText("It's a tie ! Both players have the same count of tile placed !");
        }
        alert.show();
    }

    public void buttonEndTurn(){
        game.passTurn();
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

    public void buttonExchangeAll(){
        game.exchangeAll();
    }

    public void buttonBuyAnAction(){
        game.buyAnAction();
    }

    public void buttonEndTurn(){
        game.passTurn();
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

    private void setupPassButton() {
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

    public void endGame() {
        playMenuVBox.setVisible(true);
        playMenuVBox.setManaged(true);
        usernameMenuVBox.setVisible(false);
        usernameMenuVBox.setManaged(false);

        gameBoardBP.setVisible(false);
        gameBoardBP.setManaged(false);
        startMenuStackpane.setVisible(true);
        startMenuStackpane.setManaged(true);
    }

    public void resetBoard() {
        for (Node node : board.getChildren()) {
            if (node instanceof ImageView imageView) {
                Integer col = GridPane.getColumnIndex(node);
                Integer row = GridPane.getRowIndex(node);
                int x = (col == null) ? 0 : col;
                int y = (row == null) ? 0 : row;

                String imagePath;
                if ((x == 4 && y == 4)) {
                    imagePath = Objects.requireNonNull(getClass()
                            .getResource("/latice/images/bg_moon.png")).toExternalForm();
                } else if (game.getBoard().getCell(x, y) instanceof SunCell) {
                    imagePath = Objects.requireNonNull(getClass()
                            .getResource("/latice/images/bg_sun.png")).toExternalForm();
                } else {
                    imagePath = Objects.requireNonNull(getClass()
                            .getResource("/latice/images/bg_sea.png")).toExternalForm();
                }

                imageView.setImage(new Image(imagePath));
                imageView.setVisible(true);
            }
        }
        hasAlreadyPlaced = false;
    }

    public Game getGame() {
        return game;
    }
}
