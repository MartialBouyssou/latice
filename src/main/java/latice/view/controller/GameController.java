package latice.view.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
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
import latice.utils.LangManager;

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
    @FXML private Button endTurnButton;
    @FXML private Label lastPlayerLabel;
    @FXML private Label lastPlayerScoreLabel;
    @FXML private Label turnNumberLabel;

    @FXML private BorderPane gameBoardBP;
    @FXML private Button actionButton;

    @FXML private StackPane startMenuStackpane;
    @FXML private VBox rulesMenuVBox;
    @FXML private VBox playMenuVBox;
    @FXML private Label startMenuTitle;
    @FXML private Button startMenuPlayButton;
    @FXML private Button startMenuRuleButton;
    @FXML private VBox usernameMenuVBox;
    @FXML private TextField player1UsernameTf;
    @FXML private TextField player2UsernameTf;

    private Game game;
    private List<ImageView> rackImageViews;
    public boolean hasAlreadyPlaced = false;

    @FXML
    public void initialize() {
        rackImageViews = List.of(rackTile1, rackTile2, rackTile3, rackTile4, rackTile5);
        loadTexts();

        for (Node node : rackBox.getChildren()) {
            if (node instanceof ImageView) {
                setupDraggable((ImageView) node);
            }
        }

        setupBoardDropTarget();
    }

    public void loadTexts() {
        applyTextsRecursively(startMenuStackpane);
        applyTextsRecursively(gameBoardBP);
        applyTextsRecursively(screenOverlay);
    }

    private void applyTextsRecursively(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Labeled labeled) {
                String key = labeled.getText();
                if (key != null && !key.isBlank() && key.contains(".") && !key.contains(" ")) {
                    String value = LangManager.getInstance().get(key);
                    labeled.setText(value);
                }
            }
            if (node instanceof Parent childParent) {
                applyTextsRecursively(childParent);
            }
        }
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

    public void toggleActionButton() {
        actionButton.setDisable(!actionButton.isDisable());
    }

    public void toggleActionButton(boolean activate) {
        actionButton.setDisable(activate);
    }

    public void startGame() {
        playMenuVBox.setVisible(true);
        playMenuVBox.setManaged(true);
        usernameMenuVBox.setVisible(false);
        usernameMenuVBox.setManaged(false);

        startMenuStackpane.setVisible(false);
        startMenuStackpane.setManaged(false);

        game = buildGame();
        game.addListener(new LaticeGameListener(this));
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
        LangManager lang = LangManager.getInstance();

        endTurnButton.setDisable(true);

        overlayPlayerLabel.setText(lang.format("menu.turnIndicator.currentPlayer",
                "name", nextPlayer.getName())
        );
        lastPlayerLabel.setText(lang.format("menu.turnIndicator.lastPlayerScoreHeader",
                "name", lastPlayer.getName())
        );

        lastPlayerScoreLabel.setText(Integer.toString(lastTurnScore));

        turnNumberLabel.setText(lang.format("menu.turnIndicator.turnIndicatorTitle",
                "turnNumber", (game.getTurnCount()/2 + 1) + "/" + Game.MAX_TURN_COUNT)
        );

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
        LangManager lang = LangManager.getInstance();

        currentPlayerLabel.setText(lang.format("menu.game.player.currentPlayer",
                "name", player.getName())
        );
        currentPlayerScoreLabel.setText(lang.format("menu.game.player.score",
                "score", String.valueOf(player.getScore()))
        );
        playerTileRemainingLabel.setText(lang.format("menu.game.player.remaining",
                "count", String.valueOf(remainingTiles))
        );
    }

    public void showInvalidMove(String reason) {
        LangManager lang = LangManager.getInstance();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(lang.get("popup.game.invalid.title"));
        alert.setHeaderText(lang.get("popup.game.invalid.header"));
        alert.setContentText(reason);
        alert.show();
    }

    public void showGameOver(Player player) {
        hideOverlay();
        LangManager lang = LangManager.getInstance();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(lang.get("popup.game.over.title"));
        if (player != null) {
            alert.setHeaderText(lang.format("popup.game.over.winner.header",
                    "name", player.getName())
            );
            alert.setContentText(lang.format("popup.game.over.winner.content",
                    "name", player.getName(),
                    "score", String.valueOf(player.getScore()))
            );
        } else {
            alert.setHeaderText(lang.get("popup.game.over.tie.header"));
            alert.setContentText(lang.get("popup.game.over.tie.content"));
        }
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
                        showInvalidMove(LangManager.getInstance().get("popup.game.invalid.reason"));
                    }
                }
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

                Integer colIndex = GridPane.getColumnIndex(cellView);
                Integer rowIndex = GridPane.getRowIndex(cellView);
                int x = (colIndex == null) ? 0 : colIndex;
                int y = (rowIndex == null) ? 0 : rowIndex;

                Tile tileToPlace = extractTileFromUrl(imageUrl);

                if (tileToPlace != null) {
                    try {
                        game.playTile(tileToPlace, x, y);
                        success = true;
                    } catch (Exception e) { /* No need to display an error */ }
                }
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
