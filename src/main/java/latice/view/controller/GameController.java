package latice.view.controller;

import javafx.application.Platform;
import javafx.event.Event;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
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
import latice.exception.network.NetworkException;
import latice.exception.network.UnhandledPacketException;
import latice.listeners.LaticeGameListener;
import latice.listeners.NetworkGameListener;
import latice.model.board.cell.SunCell;
import latice.model.deck.Deck;
import latice.model.game.Game;
import latice.model.player.Player;
import latice.model.player.Rack;
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
import latice.network.udp.UdpDiscoveryClient;
import latice.network.udp.UdpDiscoveryServer;
import latice.utils.theme.ThemeManager;
import latice.utils.theme.Theme;
import latice.utils.LangManager;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

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
    @FXML private VBox lanMenuVBox;
    @FXML private VBox searchLanMenuVBox;
    @FXML private VBox lanLobbyMenuVBox;
    @FXML private Button lanPlayButton;
    @FXML private Label lanHostNameLabel;
    @FXML private Label lanClientNameLabel;
    @FXML private StackPane lanScreenOverlay;
    @FXML private Label turnOfLabel;

    private Game game;
    private NetworkGameListener networkGameListener;
    private List<ImageView> rackImageViews;
    private boolean hasAlreadyPlaced = false;
    private Map<String, String> availableLanguages;

    private SessionRole sessionRole;
    private UdpDiscoveryServer udpDiscoveryServer;
    private TcpClient tcpClient;
    private TcpServer tcpServer;
    private String hostUsername = "Host";
    private String clientUsername = "Client";
    private Player pendingLastPlayer;
    private int pendingLastTurnScore;

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
        stopNetwork();
        searchLanMenuVBox.setVisible(false);
        searchLanMenuVBox.setManaged(false);
        lanMenuVBox.setVisible(false);
        lanMenuVBox.setManaged(false);
        lanLobbyMenuVBox.setVisible(false);
        lanLobbyMenuVBox.setManaged(false);
        themeMenuVBox.setVisible(false);
        themeMenuVBox.setManaged(false);
        rulesMenuVBox.setVisible(false);
        rulesMenuVBox.setManaged(false);
        usernameMenuVBox.setVisible(false);
        usernameMenuVBox.setManaged(false);
        playMenuVBox.setVisible(true);
        playMenuVBox.setManaged(true);
    }

    public void showLanMenu() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        lanMenuVBox.setVisible(true);
        lanMenuVBox.setManaged(true);
    }

    public void showLanSearchMenu() {
        LangManager lang = LangManager.getInstance();
        lanMenuVBox.setVisible(false);
        lanMenuVBox.setManaged(false);
        searchLanMenuVBox.setVisible(true);
        searchLanMenuVBox.setManaged(true);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(lang.get("popup.lanUsername.title"));
        dialog.setHeaderText(lang.get("popup.lanUsername.header"));
        dialog.setContentText(lang.get("popup.lanUsername.context"));
        Optional<String> result = dialog.showAndWait();
        clientUsername = result.filter(s -> !s.isBlank()).orElse(lang.get("default.clientName"));

        searchLanMenuVBox.getChildren().removeIf(node ->
                node.getUserData() != null && node.getUserData().equals("hostButton"));

        UdpDiscoveryClient discoveryClient = new UdpDiscoveryClient();
        try {
            List<DiscoveredHost> hosts = discoveryClient.discover();
            if (hosts.isEmpty()) {
                Label noHostLabel = new Label(lang.get("popup.lanSearch.noHost"));
                noHostLabel.setUserData("hostButton");
                searchLanMenuVBox.getChildren().add(
                        searchLanMenuVBox.getChildren().size() - 1, noHostLabel);
            } else {
                for (DiscoveredHost host : hosts) {
                    Button hostButton = new Button(
                            lang.format("menu.lanSearch.host", "host", host.getHostName()));
                    hostButton.setUserData("hostButton");
                    hostButton.setOnAction(e -> startAsClient(host));
                    searchLanMenuVBox.getChildren().add(
                            searchLanMenuVBox.getChildren().size() - 1, hostButton);
                }
            }
        } catch (IOException e) {
            throw new NetworkException("Client failed to fetch hosts...");
        }
    }

    public void showLanLobbyMenu() {
        LangManager lang = LangManager.getInstance();
        lanPlayButton.setDisable(true);
        lanMenuVBox.setVisible(false);
        lanMenuVBox.setManaged(false);
        lanLobbyMenuVBox.setVisible(true);
        lanLobbyMenuVBox.setManaged(true);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(lang.get("popup.lanUsername.title"));
        dialog.setHeaderText(lang.get("popup.lanUsername.header"));
        dialog.setContentText(lang.get("popup.lanUsername.context"));
        Optional<String> result = dialog.showAndWait();
        hostUsername = result.filter(s -> !s.isBlank()).orElse(lang.get("default.hostName"));

        lanHostNameLabel.setText(lang.format("menu.lanLobby.hostUsername", "host", hostUsername));
        startAsHost();
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
        sessionRole = SessionRole.LOCAL;
        usernameMenuVBox.setVisible(false);
        usernameMenuVBox.setManaged(false);
        startMenuStackpane.setVisible(false);
        startMenuStackpane.setManaged(false);
        gameBoardBP.setVisible(true);
        gameBoardBP.setManaged(true);

        game = buildLocalGame();
        game.addListener(new LaticeGameListener(this, false));
        game.startGame();
    }

    private Game buildLocalGame() {
        LangManager lang = LangManager.getInstance();
        String name1 = getPlayerName(player1UsernameTf.getText(), lang.get("default.firstPlayerName"));
        String name2 = getPlayerName(player2UsernameTf.getText(), lang.get("default.secondPlayerName"));
        if (name1.equals(name2)) name2 = name2 + "_2";
        return new Game(buildReferee(), name1, name2);
    }

    private void startAsHost() {
        sessionRole = SessionRole.HOST;
        tcpServer = new TcpServer(NetworkConfig.TCP_PORT, this::handleIncomingPacket);
        Thread tcpThread = new Thread(tcpServer);
        tcpThread.setDaemon(true);
        tcpThread.start();
        udpDiscoveryServer = new UdpDiscoveryServer(hostUsername, NetworkConfig.TCP_PORT);
        Thread udpThread = new Thread(udpDiscoveryServer);
        udpThread.setDaemon(true);
        udpThread.start();
    }

    public void startAsClient(DiscoveredHost host) {
        sessionRole = SessionRole.CLIENT;
        tcpClient = new TcpClient(clientUsername, this::handleIncomingPacket);
        try {
            tcpClient.connect(host.getAddress(), host.getTcpPort());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Connection failed: {0}", e.getMessage());
            showInvalidMove(LangManager.getInstance().get("network.error.connection"));
        }
    }

    private void startLanGame(GameStartPacket packet) {
        Player starter = Player.create(packet.getStarterUserName(),
                new Rack(packet.getStarterRack()),
                Deck.createNoShuffle(packet.getStarterDeck()));
        Player second = Player.create(packet.getSecondUserName(),
                new Rack(packet.getSecondRack()),
                Deck.createNoShuffle(packet.getSecondDeck()));

        game = new Game(buildReferee(), starter, second);

        networkGameListener = new NetworkGameListener(this);
        game.addListener(networkGameListener);
        game.addListener(new LaticeGameListener(this, true));

        game.startFromExistingPlayers();

        lanLobbyMenuVBox.setVisible(false);
        lanLobbyMenuVBox.setManaged(false);
        startMenuStackpane.setVisible(false);
        startMenuStackpane.setManaged(false);
        gameBoardBP.setVisible(true);
        gameBoardBP.setManaged(true);

        showLanOverlay(game.getCurrentPlayer().getName());
    }

    private void handleIncomingPacket(Packet packet) {
        if (packet instanceof LobbyJoinPacket p) {
            Platform.runLater(() -> handleLobbyJoin(p));
        } else if (packet instanceof LobbyWelcomePacket p) {
            Platform.runLater(() -> handleLobbyWelcome(p));
        } else if (packet instanceof LobbyReadyPacket) {
            Platform.runLater(this::handleLobbyReady);
        } else if (packet instanceof GameStartPacket p) {
            Platform.runLater(() -> handleStartGame(p));
        } else if (packet instanceof PlayTilePacket p) {
            Platform.runLater(() -> handlePlayTile(p));
        } else if (packet instanceof PassTurnPacket) {
            Platform.runLater(this::handlePassTurn);
        } else if (packet instanceof ExchangeRackPacket p) {
            Platform.runLater(() -> handleExchangeRack(p));
        } else if (packet instanceof BuyActionPacket) {
            Platform.runLater(this::handleBuyAction);
        } else if (packet instanceof GameOverPacket p) {
            Platform.runLater(() -> handleGameOver(p));
        } else {
            throw new UnhandledPacketException(packet.getType().name() + " isn't handled");
        }
    }

    private void handleLobbyJoin(LobbyJoinPacket packet) {
        LangManager lang = LangManager.getInstance();
        clientUsername = packet.getClientName();

        lanHostNameLabel.setText(lang.format("menu.lanLobby.hostUsername", "host", hostUsername));
        lanClientNameLabel.setText(lang.format("menu.lanLobby.clientUsernameNotReady",
                "client", clientUsername));

        sendPacket(new LobbyWelcomePacket(hostUsername));
    }

    private void handleLobbyWelcome(LobbyWelcomePacket packet) {
        LangManager lang = LangManager.getInstance();
        hostUsername = packet.getHostName();

        searchLanMenuVBox.setVisible(false);
        searchLanMenuVBox.setManaged(false);
        lanLobbyMenuVBox.setVisible(true);
        lanLobbyMenuVBox.setManaged(true);
        lanPlayButton.setText(lang.get("menu.lanLobby.ready"));
        lanPlayButton.setOnAction(e -> buttonLanReady());

        lanHostNameLabel.setText(lang.format("menu.lanLobby.hostUsername", "host", hostUsername));
        lanClientNameLabel.setText(lang.format("menu.lanLobby.clientUsernameNotReady",
                "client", clientUsername));
    }

    private void handleLobbyReady() {
        LangManager lang = LangManager.getInstance();
        lanClientNameLabel.setText(lang.format("menu.lanLobby.clientUsernameReady",
                "client", clientUsername));
        lanPlayButton.setDisable(false);
        lanPlayButton.setOnAction(e -> startLanGameAsHost());
    }

    private void startLanGameAsHost() {
        lanPlayButton.setDisable(true);
        game = buildLanGameAsHost();
        networkGameListener = new NetworkGameListener(this);
        game.addListener(networkGameListener);
        game.addListener(new LaticeGameListener(this, true));

        executeLocalAction(() -> {
            game.startGame();
            sendPacket(buildGameStartPacket());
            showLanOverlay(game.getCurrentPlayer().getName());
        });

        lanLobbyMenuVBox.setVisible(false);
        lanLobbyMenuVBox.setManaged(false);
        startMenuStackpane.setVisible(false);
        startMenuStackpane.setManaged(false);
        gameBoardBP.setVisible(true);
        gameBoardBP.setManaged(true);
    }

    private void handleStartGame(GameStartPacket packet) {
        startLanGame(packet);
    }

    private void handlePlayTile(PlayTilePacket packet) {
        networkGameListener.setLocalAction(false);
        boolean wasPlayTwice = game.isPlayTwiceActive();
        List<Tile> rackBefore = new ArrayList<>(game.getCurrentPlayer().getRack().getTiles());
        game.playTile(packet.getTile(), packet.getX(), packet.getY());
        boolean moveFailed = game.getCurrentPlayer().getRack().getTiles().containsAll(rackBefore)
                && rackBefore.containsAll(game.getCurrentPlayer().getRack().getTiles());
        if (!moveFailed && !wasPlayTwice) {
            game.endTurn();
            showLanOverlay(game.getCurrentPlayer().getName());
        }
        networkGameListener.setLocalAction(false);
    }

    private void handlePassTurn() {
        networkGameListener.setLocalAction(false);
        game.passTurn();
        networkGameListener.setLocalAction(false);
        showLanOverlay(game.getCurrentPlayer().getName());
    }

    private void handleExchangeRack(ExchangeRackPacket packet) {
        networkGameListener.setLocalAction(false);
        game.getCurrentPlayer().getRack().setTiles(packet.getNewRack());
        game.getCurrentPlayer().getDeck().setDeck(packet.getRemainingDeck());
        game.completeExchangeAsTurn();
        networkGameListener.setLocalAction(false);
        showLanOverlay(game.getCurrentPlayer().getName());
    }

    private void handleBuyAction() {
        networkGameListener.setLocalAction(false);
        game.buyAnAction();
        networkGameListener.setLocalAction(false);
    }

    private void handleGameOver(GameOverPacket packet) {
        Player winner = game.getPlayers().stream()
                .filter(p -> p.getName().equals(packet.getWinnerUsername()))
                .findFirst()
                .orElse(null);
        showGameOver(winner);
        endGame();
        resetBoard();
    }

    private void executeLocalAction(Runnable action) {
        networkGameListener.setLocalAction(true);
        action.run();
        if (networkGameListener != null) {
            networkGameListener.setLocalAction(false);
        }
    }

    public void buttonExchangeAll() {
        if (sessionRole == SessionRole.LOCAL) {
            game.exchangeAll();
        } else {
            executeLocalAction(() -> {
                game.exchangeAll();
                showLanOverlay(game.getCurrentPlayer().getName());
            });
        }
    }

    public void buttonBuyAnAction() {
        if (sessionRole == SessionRole.LOCAL) {
            game.buyAnAction();
        } else {
            executeLocalAction(game::buyAnAction);
        }
    }

    public void buttonEndTurn() {
        if (sessionRole == SessionRole.LOCAL) {
            game.passTurn();
        } else {
            executeLocalAction(() -> {
                game.passTurn();
                showLanOverlay(game.getCurrentPlayer().getName());
            });
        }
    }

    public void buttonSurrender() {
        if (sessionRole == SessionRole.LOCAL) {
            game.surrender();
        } else {
            executeLocalAction(game::surrender);
        }
    }

    private void buttonLanReady() {
        LangManager lang = LangManager.getInstance();
        lanPlayButton.setDisable(true);
        sendPacket(new LobbyReadyPacket());
        lanClientNameLabel.setText(lang.format("menu.lanLobby.clientUsernameReady",
                "client", clientUsername));
    }

    public void quitApp() {
        stopNetwork();
        System.exit(0);
    }

    private Game buildLanGameAsHost() {
        return new Game(buildReferee(), hostUsername, clientUsername);
    }

    private GameStartPacket buildGameStartPacket() {
        Player starter = game.getCurrentPlayer();
        Player second = game.getNextPlayer();
        return new GameStartPacket(
                starter.getName(), second.getName(),
                starter.getRack().getTiles(), starter.getDeck().getTiles(),
                second.getRack().getTiles(), second.getDeck().getTiles()
        );
    }

    public void sendPacket(Packet packet) {
        if (sessionRole == SessionRole.LOCAL) return;
        try {
            if (sessionRole == SessionRole.HOST && tcpServer != null) {
                tcpServer.send(packet);
            } else if (sessionRole == SessionRole.CLIENT && tcpClient != null) {
                tcpClient.send(packet);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to send packet: {0}", e.getMessage());
        }
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
                "name", nextPlayer.getName()));
        lastPlayerLabel.setText(lang.format("menu.turnIndicator.lastPlayerScoreHeader",
                "name", lastPlayer.getName()));
        lastPlayerScoreLabel.setText(Integer.toString(lastTurnScore));
        turnNumberLabel.setText(lang.format("menu.turnIndicator.turnIndicatorTitle",
                "turnNumber",
                (game.getTurnCount() / game.getPlayers().size() + 1) + "/" + Game.MAX_CYCLES));
        screenOverlay.setVisible(true);
        screenOverlay.setManaged(true);
    }

    public void hideOverlay() {
        screenOverlay.setVisible(false);
        screenOverlay.setManaged(false);
        endTurnButton.setDisable(false);
    }

    public void showOverlay() {
        screenOverlay.setVisible(true);
        screenOverlay.setManaged(true);
        endTurnButton.setDisable(true);
    }

    public void showLanOverlay(String currentPlayerName) {
        String localUsername = sessionRole == SessionRole.HOST ? hostUsername : clientUsername;
        boolean isMyTurn = currentPlayerName.equals(localUsername);

        if (isMyTurn) {
            lanScreenOverlay.setVisible(false);
            lanScreenOverlay.setManaged(false);
            updateRack(game.getCurrentPlayer());
            updatePlayerInfo(game.getCurrentPlayer());
            if (game.getTurnCount() != 0 && pendingLastPlayer != null) {
                showOverlayForPlayer(game.getCurrentPlayer(), pendingLastPlayer, pendingLastTurnScore);
            }
            pendingLastPlayer = null;
            pendingLastTurnScore = 0;
        } else {
            if (game.getTurnCount() != 0) hideOverlay();
            turnOfLabel.setText(LangManager.getInstance().format("menu.lanOverlay.turnOf",
                    "currentPlayer", currentPlayerName));
            lanScreenOverlay.setVisible(true);
            lanScreenOverlay.setManaged(true);
        }
    }

    public void updateRack(Player player) {
        List<Tile> tiles = player.getRack().getTiles();
        for (int i = 0; i < rackImageViews.size(); i++) {
            if (i < tiles.size()) {
                rackImageViews.get(i).setImage(new Image(tiles.get(i).getTileImgPath()));
                rackImageViews.get(i).setVisible(true);
            } else {
                rackImageViews.get(i).setVisible(false);
            }
        }
    }

    public void updatePlayerInfo(Player player) {
        int remainingTiles = player.getDeck().size() + player.getRack().getTiles().size();
        LangManager lang = LangManager.getInstance();
        currentPlayerLabel.setText(lang.format("menu.game.player.currentPlayer",
                "name", player.getName()));
        currentPlayerScoreLabel.setText(lang.format("menu.game.player.score",
                "score", String.valueOf(player.getScore())));
        playerTileRemainingLabel.setText(lang.format("menu.game.player.remaining",
                "count", String.valueOf(remainingTiles)));
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
            alert.setHeaderText(lang.format("popup.game.over.winner.header", "name", player.getName()));
            alert.setContentText(lang.format("popup.game.over.winner.content",
                    "name", player.getName(), "score", String.valueOf(player.getScore())));
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
            LOGGER.log(Level.WARNING, "Failed to place tile on board UI: {0}", e.getMessage());
        }
    }

    public void endGame() {
        lanScreenOverlay.setVisible(false);
        lanScreenOverlay.setManaged(false);
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
            if (db.hasString() && game != null) {
                double cellWidth = board.getWidth() / 9.0;
                double cellHeight = board.getHeight() / 9.0;
                int col = (int) (event.getX() / cellWidth);
                int row = (int) (event.getY() / cellHeight);
                Tile tile = extractTileFromUrl(db.getString());

                if (tile != null) {
                    if (!hasAlreadyPlaced) {
                        if (sessionRole == SessionRole.LOCAL) {
                            game.playTile(tile, col, row);
                        } else {
                            executeLocalAction(() -> {
                                boolean wasPlayTwice = game.isPlayTwiceActive();
                                List<Tile> rackBefore = new ArrayList<>(game.getCurrentPlayer().getRack().getTiles());
                                game.playTile(tile, col, row);
                                boolean moveFailed = game.getCurrentPlayer().getRack().getTiles().containsAll(rackBefore)
                                        && rackBefore.containsAll(game.getCurrentPlayer().getRack().getTiles());
                                if (moveFailed) return;
                                if (wasPlayTwice) {
                                    actionButton.setDisable(false);
                                } else {
                                    game.endTurn();
                                    showLanOverlay(game.getCurrentPlayer().getName());
                                }
                            });
                        }
                    } else {
                        showInvalidMove(LangManager.getInstance().get("popup.game.invalid.reason"));
                    }
                }
            }
            event.setDropCompleted(false);
            event.consume();
        });
    }

    private Tile extractTileFromUrl(String fullUrl) {
        return TileFactory.getTileFromImagePath(fullUrl);
    }

    private Referee buildReferee() {
        return Referee.create(List.of(
                new MatchingRule(),
                new AdjacencyRule(),
                new PositionRule(),
                new FirstTurnRule()
        ));
    }

    private String getPlayerName(String input, String defaultName) {
        return (input == null || input.isBlank()) ? defaultName : input;
    }

    private void stopNetwork() {
        if (udpDiscoveryServer != null) {
            udpDiscoveryServer.stop();
            udpDiscoveryServer = null;
        }
        if (tcpServer != null) {
            tcpServer.stop();
            tcpServer = null;
        }
        tcpClient = null;
        sessionRole = null;
        networkGameListener = null;
    }

    public Game getGame() { return game; }
    public void setHasAlreadyPlaced(boolean value) { hasAlreadyPlaced = value; }
    public void setPendingOverlayInfo(Player lastPlayer, int lastTurnScore) {
        this.pendingLastPlayer = lastPlayer;
        this.pendingLastTurnScore = lastTurnScore;
    }
}
