package latice.view.controller;

import javafx.application.Platform;
import javafx.event.Event;

import java.io.IOException;
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
import latice.exception.network.ConnectionRefusedException;
import latice.exception.network.UnhandledPacketException;
import latice.listeners.LaticeGameListener;
import latice.model.board.cell.SunCell;
import latice.model.game.Game;
import latice.model.player.Player;
import latice.model.referee.Referee;
import latice.model.referee.rule.*;
import latice.model.tile.Tile;
import latice.model.tile.TileFactory;
import latice.network.NetworkConfig;
import latice.network.packet.Packet;
import latice.network.packet.game.*;
import latice.network.packet.lobby.LobbyJoinPacket;
import latice.network.packet.lobby.LobbyReadyPacket;
import latice.network.packet.lobby.LobbyWelcomePacket;
import latice.network.session.DiscoveredHost;
import latice.network.session.SessionRole;
import latice.network.tcp.TcpClient;
import latice.network.tcp.TcpServer;
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

    private Game game;
    private List<ImageView> rackImageViews;
    private boolean hasAlreadyPlaced = false;
    private Map<String, String> availableLanguages;

    private SessionRole sessionRole;
    private TcpClient tcpClient;
    private TcpServer tcpServer;


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

    public void showThemeMenu() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        themeMenuVBox.setVisible(true);
        themeMenuVBox.setManaged(true);
    }

    public void applyThemeDefault() {
        ThemeManager.getInstance().setCurrentTheme(Theme.DEFAULT);
        showPlayMenu();
    }

    public void applyThemeAnimalCrossing() {
        ThemeManager.getInstance().setCurrentTheme(Theme.ANIMAL_CROSSING);
        showPlayMenu();
    }

    public void applyThemeZelda() {
        ThemeManager.getInstance().setCurrentTheme(Theme.ZELDA);
        showPlayMenu();
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

    private String getPlayerName(String input, String defaultName) {
        if (input == null || input.isBlank()) {
            return defaultName;
        }
        return input;
    }

    private void startAsHost() {
        TcpServer server = new TcpServer(NetworkConfig.TCP_PORT, this::handleIncomingPacket);
        new Thread(server).start();
    }

    public void startAsClient(DiscoveredHost host) {
        sessionRole = SessionRole.CLIENT;

        String username = getPlayerName(player1UsernameTf.getText(), "P1");
        tcpClient = new TcpClient(username, this::handleIncomingPacket);

        try {
            tcpClient.connect(host.getAddress(), host.getTcpPort());
        } catch (IOException e) {
            Logger.getLogger(GameController.class.getName())
                    .severe("Failed: " + e.getMessage());
            showInvalidMove(LangManager.getInstance().get("network.error.connection"));
        }
    }

    private void handleIncomingPacket(Packet packet) {

        if (packet instanceof LobbyJoinPacket lobbyJoinPacket) {
            Platform.runLater(() -> {
                try {
                    handleLobbyJoin(lobbyJoinPacket);
                } catch (IOException e) {
                    throw new ConnectionRefusedException("Client couldn't be welcomed");
                }
            });

        } else if (packet instanceof LobbyWelcomePacket lobbyWelcomePacket) {
            Platform.runLater(() -> handleLobbyWelcome(lobbyWelcomePacket));

        } else if (packet instanceof LobbyReadyPacket lobbyReadyPacket) {
            Platform.runLater(() -> handleLobbyReady(lobbyReadyPacket));

        } else if (packet instanceof GameStartPacket gameStartPacket) {
            Platform.runLater(() -> handleStartGame(gameStartPacket));

        } else if (packet instanceof PlayTilePacket playTilePacket) {
            Platform.runLater(() -> handlePlayTile(playTilePacket));

        } else if (packet instanceof PassTurnPacket) {
            Platform.runLater(this::handlePassTurn);

        } else if (packet instanceof ExchangeRackPacket) {
            Platform.runLater(this::handleExchangeRack);

        } else if (packet instanceof BuyActionPacket) {
            Platform.runLater(this::handleBuyAction);

        } else if (packet instanceof GameOverPacket) {
            Platform.runLater(this::handleGameOver);

        } else {
            throw new UnhandledPacketException(packet.getType().name() + " isn't handled");
        }
    }

    private void handleLobbyJoin(LobbyJoinPacket packet) throws IOException {
        // TODO : store client useername & display it
        LobbyWelcomePacket response = new LobbyWelcomePacket("HOST");
        tcpServer.send(response);
    }

    private void handleLobbyWelcome(LobbyWelcomePacket packet) {
        // TODO : display player name in the lobby
    }

    private void handleLobbyReady(LobbyReadyPacket packet) {
        // TODO : display a message to say that the game will start
    }

    private void handleStartGame(GameStartPacket packet) {
        // TODO : build game with packet data
        // game = buildGameFromPacket(packet);
        // game.addListener(new LaticeGameListener(this));
        // display board
    }

    private void handlePlayTile(PlayTilePacket packet) {
        game.playTile(packet.getTile(), packet.getX(), packet.getY());
    }

    private void handlePassTurn() {
        game.passTurn();
    }

    private void handleExchangeRack() {
        game.exchangeAll();
        game.completeExchangeAsTurn();
    }

    private void handleBuyAction() {
        game.buyAnAction();
    }

    private void handleGameOver() {
        showGameOver(game.getWinner());
        endGame();
        resetBoard();
    }

    private void sendPacket(Packet packet) {
        if (sessionRole == SessionRole.LOCAL) return;

        try {
            if (sessionRole == SessionRole.HOST && tcpServer != null) {
                tcpServer.send(packet);
            } else if (sessionRole == SessionRole.CLIENT && tcpClient != null) {
                tcpClient.send(packet);
            }
        } catch (IOException e) {
            Logger.getLogger(GameController.class.getName())
                    .severe("Failed: " + e.getMessage());
        }
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

    public void buttonSurrender() {
        game.surrender();
    }

    public void toggleActionButton() {
        actionButton.setDisable(!actionButton.isDisable());
    }

    public void toggleActionButton(boolean activate) {
        actionButton.setDisable(activate);
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
                "turnNumber",
                (game.getTurnCount() / game.getPlayers().size() + 1) + "/" + Game.MAX_CYCLES)
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

        } catch (Exception e) {
            // https://docs.oracle.com/javase/8/docs/api/java/util/logging/Logger.html
            // I've done that 'cause SonarQube absolutly wanted us to use Logger, but don't know why
            Logger.getLogger(GameController.class.getName())
                    .warning("Failed to place tile on board UI: " + e.getMessage());
        }
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

        stopNetwork();
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
                        getClass().getResource("/" + ThemeManager.getInstance().getBoardBgPath(bgFile))
                ).toExternalForm();

                imageView.setImage(new Image(path));
                imageView.setVisible(true);
            }
        }
        hasAlreadyPlaced = false;
    }


    private void setupDraggable(ImageView tileView) {
        tileView.setOnDragDetected(event -> {
            Dragboard db = tileView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(tileView.getImage().getUrl());
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
                        sendPacket(new PlayTilePacket(tile, col, row));
                    } else {
                        showInvalidMove(LangManager.getInstance().get("popup.game.invalid.reason"));
                    }
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private Tile extractTileFromUrl(String fullUrl) {
        return TileFactory.getTileFromImagePath(fullUrl);
    }

    private void stopNetwork() {
        if (tcpServer != null) {
            tcpServer.stop();
            tcpServer = null;
        }
        tcpClient = null;
        sessionRole = null;
    }

    public Game getGame() {
        return game;
    }

    public void setHasAlreadyPlaced(boolean value) {
        hasAlreadyPlaced = value;
    }

}

