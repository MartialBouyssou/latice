package latice.view.controller;

import javafx.event.Event;
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
import latice.model.player.Player;
import latice.model.referee.Referee;
import latice.model.referee.rule.*;
import latice.model.tile.Tile;
import latice.model.tile.TileFactory;
import latice.model.theme.ThemeManager;
import latice.model.theme.ThemeManager.Theme;
import latice.utils.LangManager;

import java.util.List;
import java.util.Map;
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
    @FXML private ComboBox<String> languageComboBox;
    @FXML private VBox rulesMenuVBox;
    @FXML private VBox playMenuVBox;
    @FXML private VBox usernameMenuVBox;
    @FXML private TextField player1UsernameTf;
    @FXML private TextField player2UsernameTf;
    @FXML private VBox themeMenuVBox;

    private Game game;
    private List<ImageView> rackImageViews;
    private boolean hasAlreadyPlaced = false;
    private Map<String, String> availableLanguages;

    @FXML
    public void initialize() {
        rackImageViews = List.of(rackTile1, rackTile2, rackTile3, rackTile4, rackTile5);

        loadLanguageSelector();
        loadTexts();

        for (Node node : rackBox.getChildren()) {
            if (node instanceof ImageView imageNode) {
                setupDraggable(imageNode);
            }
        }

        setupBoardDropTarget();
    }

    private void loadLanguageSelector() {
        availableLanguages = LangManager.getInstance().loadAvailableLanguages();

        languageComboBox.getItems().addAll(availableLanguages.values());

        if (!availableLanguages.isEmpty()) {
            String firstCode = availableLanguages.keySet().iterator().next();
            LangManager.getInstance().load(firstCode);
            languageComboBox.setValue(availableLanguages.get(firstCode));
        }
    }

    public void onLanguageChanged() {
        String selectedLabel = languageComboBox.getValue();
        if (selectedLabel == null) return;

        availableLanguages.entrySet().stream()
                .filter(e -> e.getValue().equals(selectedLabel))
                .findFirst()
                .ifPresent(e -> {
                    LangManager.getInstance().load(e.getKey());
                    loadTexts();
                });
    }

    public void loadTexts() {
        applyTextsRecursively(startMenuStackpane);
        applyTextsRecursively(gameBoardBP);
        applyTextsRecursively(screenOverlay);
    }

    private void applyTextsRecursively(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Labeled labeled) {
                String key = (String) labeled.getProperties()
                        .getOrDefault("i18n-key", labeled.getText());
                // i18n is the used name for internationalization => Multi language support :)

                if (key != null && !key.isBlank() && key.contains(".") && !key.contains(" ")) {
                    labeled.getProperties().put("i18n-key", key);
                    labeled.setText(LangManager.getInstance().get(key));
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
        themeMenuVBox.setVisible(false);
        themeMenuVBox.setManaged(false);
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

                if (x == targetX && y == targetY && node instanceof ImageView imageNode) {
                    imageNode.setImage(new Image(tile.getTileImgPath()));
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

        tileView.setOnDragDone(Event::consume);
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

    private Tile extractTileFromUrl(String fullUrl) {
        return TileFactory.getTileFromImagePath(fullUrl);
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

                String bgFile;
                if (x == 4 && y == 4) {
                    bgFile = "bg_moon.png";
                } else if (game.getBoard().getCell(x, y) instanceof SunCell) {
                    bgFile = "bg_sun.png";
                } else {
                    bgFile = "bg_sea.png";
                }

                String path = Objects.requireNonNull(
                        getClass().getResource("/" +
                                ThemeManager.getInstance().getBoardBgPath(bgFile))
                ).toExternalForm();

                imageView.setImage(new Image(path));
                imageView.setVisible(true);
            }
        }
        hasAlreadyPlaced = false;
    }

    public Game getGame() {
        return game;
    }

    public void showThemeMenu() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        themeMenuVBox.setVisible(true);
        themeMenuVBox.setManaged(true);
    }

    public void applyThemeDefault() {
        ThemeManager.getInstance().setCurrentTheme(ThemeManager.Theme.DEFAULT);
        showPlayMenu();
    }

    public void applyThemeAnimalCrossing() {
        ThemeManager.getInstance().setCurrentTheme(ThemeManager.Theme.ANIMAL_CROSSING);
        showPlayMenu();
    }

    public void applyThemeZelda() {
        ThemeManager.getInstance().setCurrentTheme(Theme.ZELDA);
        showPlayMenu();
    }

    public void buttonSurrender() {
        game.surrender();
    }

    public void setHasAlreadyPlaced(boolean value) {
        hasAlreadyPlaced = value;
    }
}

