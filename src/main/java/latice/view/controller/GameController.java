package latice.view.controller;

import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.util.Duration;
import java.util.logging.Logger;
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
import latice.model.ai.AIMove;
import latice.model.ai.AIPlayer;
import latice.model.board.cell.SunCell;
import latice.model.game.Game;
import latice.model.player.Player;
import latice.model.referee.Referee;
import latice.model.referee.rule.*;
import latice.model.tile.Tile;
import latice.model.tile.TileFactory;
import latice.utils.theme.ThemeManager;
import latice.utils.theme.Theme;
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
    @FXML private VBox modeMenuVBox;
    @FXML private VBox usernameVsAIMenuVBox;
    @FXML private TextField player1UsernameVsAITf;
    @FXML private Button overlayButton;

    private Game game;
    private List<ImageView> rackImageViews;
    private boolean hasAlreadyPlaced = false;
    private Map<String, String> availableLanguages;
    private Player aiControlledPlayer;
    private AIPlayer aiPlayer;

    /**
     * Initializes the JavaFX controller, setting up views, languages, and drag-and-drop events.
     */
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

    /**
     * Loads available languages from the LangManager and populates the selection ComboBox.
     */
    private void loadLanguageSelector() {
        availableLanguages = LangManager.getInstance().loadAvailableLanguages();
        languageComboBox.getItems().addAll(availableLanguages.values());
        if (!availableLanguages.isEmpty()) {
            String firstCode = availableLanguages.keySet().iterator().next();
            LangManager.getInstance().load(firstCode);
            languageComboBox.setValue(availableLanguages.get(firstCode));
        }
    }

    /**
     * Handles the event triggered when the user selects a new language in the ComboBox.
     */
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

    /**
     * Reloads and applies internationalization texts across all main UI components.
     */
    public void loadTexts() {
        applyTextsRecursively(startMenuStackpane);
        applyTextsRecursively(gameBoardBP);
        applyTextsRecursively(screenOverlay);
    }

    /**
     * Recursively traverses a JavaFX Parent node to find and translate Labeled components.
     * @param parent The root JavaFX node to start the recursive search from.
     */
    private void applyTextsRecursively(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Labeled labeled) {
                String key = (String) labeled.getProperties()
                        .getOrDefault("i18n-key", labeled.getText());
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

    /**
     * Displays the game mode selection menu (local, LAN, vs AI).
     */
    public void showModeMenu() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        modeMenuVBox.setVisible(true);
        modeMenuVBox.setManaged(true);
    }

    /**
     * Displays the menu for choosing player usernames and hides other menus.
     */
    public void showUsernameChoiceMenu() {
        modeMenuVBox.setVisible(false);
        modeMenuVBox.setManaged(false);
        usernameMenuVBox.setVisible(true);
        usernameMenuVBox.setManaged(true);
    }

    /**
     * Displays the username menu for player-vs-AI mode.
     */
    public void showUsernameVsAIMenu() {
        modeMenuVBox.setVisible(false);
        modeMenuVBox.setManaged(false);
        usernameVsAIMenuVBox.setVisible(true);
        usernameVsAIMenuVBox.setManaged(true);
    }

    /**
     * Displays the main play menu and hides the rules, theme and other menus.
     */
    public void showPlayMenu() {
        themeMenuVBox.setVisible(false);
        themeMenuVBox.setManaged(false);
        rulesMenuVBox.setVisible(false);
        rulesMenuVBox.setManaged(false);
        modeMenuVBox.setVisible(false);
        modeMenuVBox.setManaged(false);
        usernameVsAIMenuVBox.setVisible(false);
        usernameVsAIMenuVBox.setManaged(false);
        playMenuVBox.setVisible(true);
        playMenuVBox.setManaged(true);
    }

    /**
     * Displays the rules menu and hides the main play menu.
     */
    public void showRules() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        rulesMenuVBox.setVisible(true);
        rulesMenuVBox.setManaged(true);
    }

    /**
     * Toggles the interactive state of the action button.
     */
    public void toggleActionButton() {
        actionButton.setDisable(!actionButton.isDisable());
    }

    /**
     * Sets the active state of the action button explicitly.
     * @param activate A boolean indicating if the button should be disabled (true) or enabled (false).
     */
    public void toggleActionButton(boolean activate) {
        actionButton.setDisable(activate);
    }

    /**
     * Hides the starting menus, initializes the game components, and brings up the game board.
     */
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

    /**
     * Hides the starting menus, initializes a player-vs-AI game, and brings up the game board.
     */
    public void startGameVsAI() {
        playMenuVBox.setVisible(true);
        playMenuVBox.setManaged(true);
        usernameVsAIMenuVBox.setVisible(false);
        usernameVsAIMenuVBox.setManaged(false);
        startMenuStackpane.setVisible(false);
        startMenuStackpane.setManaged(false);

        String humanName = getPlayerName(player1UsernameVsAITf.getText(), "P1");
        String aiName = LangManager.getInstance().get("menu.username.aiPlayerName");

        List<IRule> rules = List.of(
                new MatchingRule(),
                new AdjacencyRule(),
                new PositionRule(),
                new FirstTurnRule()
        );

        game = new Game(Referee.create(rules), humanName, aiName);
        game.addListener(new LaticeGameListener(this));
        aiPlayer = new AIPlayer(game.getReferee());
        game.startGame();

        for (Player p : game.getPlayers()) {
            if (p.getName().equals(aiName)) {
                aiControlledPlayer = p;
                break;
            }
        }

        gameBoardBP.setVisible(true);
        gameBoardBP.setManaged(true);

        triggerAITurnIfNeeded();
    }

    /**
     * Executes the AI's turn, including the capability to buy additional actions if the AI gains enough points.
     */
    public void playAITurn() {
        if (aiControlledPlayer == null || aiPlayer == null) {
            return;
        }

        int turnsRemaining = Game.MAX_CYCLES - (game.getTurnCount() / game.getPlayers().size());

        if (hasAlreadyPlaced) {
            if (aiControlledPlayer.canAfford(Referee.EXTRA_ACTION_COST) && !aiControlledPlayer.getRack().isEmpty()) {
                AIMove extraMove = aiPlayer.findBestMove(game.getBoard(), aiControlledPlayer.getRack(), aiControlledPlayer.getScore() - Referee.EXTRA_ACTION_COST, turnsRemaining);
                if (extraMove != null) {
                    game.buyAnAction();
                    game.playTile(extraMove.getTile(), extraMove.getX(), extraMove.getY());
                    return;
                }
            }
            game.endTurn();
            return;
        }

        AIMove move = aiPlayer.findBestMove(game.getBoard(), aiControlledPlayer.getRack(), aiControlledPlayer.getScore(), turnsRemaining);
        if (move != null) {
            game.playTile(move.getTile(), move.getX(), move.getY());
        } else {
            if (aiControlledPlayer.getDeck().isEmpty()) {
                game.passTurn();
            } else {
                game.exchangeAll();
            }
        }
    }

    /**
     * Triggers the AI turn if the current player is the AI-controlled player.
     */
    public void triggerAITurnIfNeeded() {
        if (aiControlledPlayer != null
                && aiPlayer != null
                && game != null
                && game.getCurrentPlayer() != null
                && game.getCurrentPlayer().equals(aiControlledPlayer)) {
            playAITurn();
        }
    }

    /**
     * Constructs the Game object for a local two-player game, injecting rules and player names.
     * @return The newly instantiated Game object.
     */
    private Game buildGame() {
        List<IRule> rules = List.of(
                new MatchingRule(),
                new AdjacencyRule(),
                new PositionRule(),
                new FirstTurnRule()
        );
        String name1 = getPlayerName(player1UsernameTf.getText(), "P1");
        String name2 = getPlayerName(player2UsernameTf.getText(), "P2");
        if (name1.equals(name2)) {
            name2 = name2 + "_2";
        }
        return new Game(Referee.create(rules), name1, name2);
    }

    /**
     * Validates and returns a player's name, falling back to a default if empty.
     * @param input The user input to validate.
     * @param defaultName The default name to apply if the input is empty.
     * @return A valid player name.
     */
    private String getPlayerName(String input, String defaultName) {
        if (input == null || input.isBlank()) {
            return defaultName;
        }
        return input;
    }

    /**
     * Displays the transition overlay between player turns with score summaries.
     * @param nextPlayer The player who will play next.
     * @param lastPlayer The player whose turn just ended.
     * @param lastTurnScore The score acquired during the last turn.
     */
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
                "turnNumber",
                (game.getTurnCount() / game.getPlayers().size() + 1) + "/" + Game.MAX_CYCLES)
        );

        String aiName = lang.get("menu.username.aiPlayerName");
        if (nextPlayer.getName().equals(aiName)) {
            overlayButton.setVisible(false);
            overlayButton.setManaged(false);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> hideOverlay());
            pause.play();
        } else {
            overlayButton.setVisible(true);
            overlayButton.setManaged(true);
        }

        screenOverlay.setVisible(true);
        screenOverlay.setManaged(true);
    }

    /**
     * Hides the transition overlay and re-enables the end turn button.
     */
    public void hideOverlay() {
        screenOverlay.setVisible(false);
        screenOverlay.setManaged(false);
        endTurnButton.setDisable(false);
        triggerAITurnIfNeeded();
    }

    /**
     * Updates the UI images representing the current player's rack of tiles.
     * @param player The player whose rack is being updated.
     */
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

    /**
     * Refreshes the score, name, and remaining tile count displayed for the current player.
     * @param player The current player.
     */
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

    /**
     * Displays an error alert popup to the user detailing why their move was invalid.
     * @param reason The specific rule violation or error message.
     */
    public void showInvalidMove(String reason) {
        LangManager lang = LangManager.getInstance();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(lang.get("popup.game.invalid.title"));
        alert.setHeaderText(lang.get("popup.game.invalid.header"));
        alert.setContentText(reason);
        alert.show();
    }

    /**
     * Displays the end-of-game alert showing the winner and their score, or a tie message.
     * @param player The winning player, or null if the game resulted in a tie.
     */
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

    /**
     * Visually places a tile onto the board grid and updates the player's rack view.
     * @param tile The tile object being placed.
     * @param targetX The X coordinate on the grid.
     * @param targetY The Y coordinate on the grid.
     * @param player The player performing the placement.
     */
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
        } catch (Exception e) {
            Logger.getLogger(GameController.class.getName())
                    .warning("Failed to place tile on board UI: " + e.getMessage());
        }
    }

    /**
     * Configures a JavaFX ImageView to support drag operations using its image URL.
     * @param tileView The ImageView representing the draggable tile.
     */
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

    /**
     * Configures the main board grid to accept drag-and-drop actions and trigger game logic.
     */
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

    /**
     * Triggers the logic to exchange all tiles in the current player's rack.
     */
    public void buttonExchangeAll() {
        game.exchangeAll();
    }

    /**
     * Triggers the logic to buy an extra action using sun points.
     */
    public void buttonBuyAnAction() {
        game.buyAnAction();
    }

    /**
     * Passes the current player's turn to the next player, or ends the turn if an action was played.
     */
    public void buttonEndTurn() {
        if (hasAlreadyPlaced) {
            game.endTurn();
        } else {
            game.passTurn();
        }
    }

    /**
     * Resolves the corresponding Tile model object from the provided image file path.
     * @param fullUrl The absolute file path of the image being parsed.
     * @return The corresponding Tile instance.
     */
    private Tile extractTileFromUrl(String fullUrl) {
        return TileFactory.getTileFromImagePath(fullUrl);
    }

    /**
     * Terminates the active game session and returns the user to the starting menus.
     */
    public void endGame() {
        playMenuVBox.setVisible(true);
        playMenuVBox.setManaged(true);
        usernameMenuVBox.setVisible(false);
        usernameMenuVBox.setManaged(false);

        aiControlledPlayer = null;
        aiPlayer = null;

        gameBoardBP.setVisible(false);
        gameBoardBP.setManaged(false);

        startMenuStackpane.setVisible(true);
        startMenuStackpane.setManaged(true);
    }

    /**
     * Resets the graphical state of the board to its empty, initial configuration.
     */
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

    /**
     * Gets the active Game instance managed by this controller.
     * @return The active Game object.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Displays the menu allowing the user to select the visual theme of the game.
     */
    public void showThemeMenu() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        themeMenuVBox.setVisible(true);
        themeMenuVBox.setManaged(true);
    }

    /**
     * Applies the default Latice visual theme and returns to the play menu.
     */
    public void applyThemeDefault() {
        ThemeManager.getInstance().setCurrentTheme(Theme.DEFAULT);
        showPlayMenu();
    }

    /**
     * Applies the Animal Crossing visual theme and returns to the play menu.
     */
    public void applyThemeAnimalCrossing() {
        ThemeManager.getInstance().setCurrentTheme(Theme.ANIMAL_CROSSING);
        showPlayMenu();
    }

    /**
     * Applies the Legend of Zelda visual theme and returns to the play menu.
     */
    public void applyThemeZelda() {
        ThemeManager.getInstance().setCurrentTheme(Theme.ZELDA);
        showPlayMenu();
    }

    /**
     * Executes the surrender logic for the currently active player.
     */
    public void buttonSurrender() {
        game.surrender();
    }

    /**
     * Updates the status of whether a tile has already been placed in the current turn.
     * @param value Boolean true if a tile has been placed, false otherwise.
     */
    public void setHasAlreadyPlaced(boolean value) {
        hasAlreadyPlaced = value;
    }
}