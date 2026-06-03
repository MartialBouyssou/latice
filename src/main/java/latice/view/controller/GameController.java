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
     *
     * @param parent The root JavaFX node to start the recursive search from.
     */
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

    /**
     * Displays the menu for choosing player usernames and hides other menus.
     */
    public void showUsernameChoiceMenu() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        usernameMenuVBox.setVisible(true);
        usernameMenuVBox.setManaged(true);
    }

    /**
     * Displays the main play menu and hides the rules and theme menus.
     */
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

    /**
     * Displays the lan menu and hides the main play menu.
     */
    public void showLanMenu() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        lanMenuVBox.setVisible(true);
        lanMenuVBox.setManaged(true);
    }

    /**
     * Displays the lan search menu and hides the main lan menu while
     * starting UDP requests
     */
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

    /**
     * Displays the lan lobby menu and hides others menu.
     */
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

        lanHostNameLabel.setText(lang.format("menu.lanLobby.hostUsername",
                "host", hostUsername));
        lanClientNameLabel.setText(lang.get("menu.lanLobby.clientNotHere"));
        startAsHost();
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
     * Displays the menu allowing the user to select the visual theme of the game.
     */
    public void showThemeMenu() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        themeMenuVBox.setVisible(true);
        themeMenuVBox.setManaged(true);
    }

    /**
     * Apply the default theme to the game.
     */
    public void applyThemeDefault() {
        ThemeManager.getInstance().setCurrentTheme(Theme.DEFAULT);
        applyTheme();
    }

    /**
     * Apply the animal crossing theme to the game.
     */
    public void applyThemeAnimalCrossing() {
        ThemeManager.getInstance().setCurrentTheme(Theme.ANIMAL_CROSSING);
        applyTheme();
    }

    /**
     * Apply the zelda crossing theme to the game.
     */
    public void applyThemeZelda() {
        ThemeManager.getInstance().setCurrentTheme(Theme.ZELDA);
        applyTheme();
    }

    /**
     * Apply the theme and show play menu.
     */
    private void applyTheme() {
        showPlayMenu();
    }

    /**
     * Hides the starting menus, initializes the game components, and brings up the game board.
     */
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
        resetBoard();
    }

    /**
     * Constructs the Game object, injecting rules and player names.
     *
     * @return The newly instantiated Game object.
     */
    private Game buildLocalGame() {
        LangManager lang = LangManager.getInstance();
        String name1 = getPlayerName(player1UsernameTf.getText(), lang.get("default.firstPlayerName"));
        String name2 = getPlayerName(player2UsernameTf.getText(), lang.get("default.secondPlayerName"));
        if (name1.equals(name2)) name2 = name2 + "_2";
        return new Game(buildReferee(), name1, name2);
    }

    /**
     * Start the host and launch UDP and TCP server
     */
    private void startAsHost() {
        sessionRole = SessionRole.HOST;
        tcpServer = new TcpServer(NetworkConfig.TCP_PORT, this::handleIncomingPacket,
                () -> Platform.runLater(this::handleClientDisconnected));
        Thread tcpThread = new Thread(tcpServer);
        tcpThread.setDaemon(true);
        tcpThread.start();
        udpDiscoveryServer = new UdpDiscoveryServer(hostUsername, NetworkConfig.TCP_PORT);
        Thread udpThread = new Thread(udpDiscoveryServer);
        udpThread.setDaemon(true);
        udpThread.start();
    }

    /**
     * Start the client and launch TCP server
     */
    public void startAsClient(DiscoveredHost host) {
        sessionRole = SessionRole.CLIENT;
        tcpClient = new TcpClient(clientUsername, this::handleIncomingPacket,
                () -> Platform.runLater(this::handleHostDisconnected));
        try {
            tcpClient.connect(host.getAddress(), host.getTcpPort());
        } catch (IOException e) {
            showInvalidMove(LangManager.getInstance().get("network.error.connection"));
        }
    }

    /**
     * Start the game with received Game Packet to have the same games objects
     * as the host
     */
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

    // TODO: Docstring
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

    // TODO: Docstring
    private void handleClientDisconnected() {
        if (game != null && gameBoardBP.isVisible()) {
            showGameOver(null);
            endGame();
            resetBoard();
        } else {
            stopNetwork();
            showLanLobbyMenu();
        }
    }

    // TODO: Docstring
    private void handleHostDisconnected() {
        if (game != null && gameBoardBP.isVisible()) {
            showGameOver(null);
            endGame();
            resetBoard();
        } else {
            showPlayMenu();
        }
    }

    // TODO: Docstring
    private void handleLobbyJoin(LobbyJoinPacket packet) {
        LangManager lang = LangManager.getInstance();
        clientUsername = packet.getClientName();

        lanHostNameLabel.setText(lang.format("menu.lanLobby.hostUsername", "host", hostUsername));
        lanClientNameLabel.setText(lang.format("menu.lanLobby.clientUsernameNotReady",
                "client", clientUsername));

        sendPacket(new LobbyWelcomePacket(hostUsername));
    }

    // TODO: Docstring
    private void handleLobbyWelcome(LobbyWelcomePacket packet) {
        LangManager lang = LangManager.getInstance();
        hostUsername = packet.getHostName();

        searchLanMenuVBox.setVisible(false);
        searchLanMenuVBox.setManaged(false);
        lanLobbyMenuVBox.setVisible(true);
        lanLobbyMenuVBox.setManaged(true);
        lanPlayButton.setText(lang.get("menu.lanLobby.ready"));
        lanPlayButton.setOnAction(e -> buttonLanReady());
        lanPlayButton.setDisable(false);

        lanHostNameLabel.setText(lang.format("menu.lanLobby.hostUsername", "host", hostUsername));
        lanClientNameLabel.setText(lang.format("menu.lanLobby.clientUsernameNotReady",
                "client", clientUsername));
    }

    // TODO: Docstring
    private void handleLobbyReady() {
        LangManager lang = LangManager.getInstance();
        lanClientNameLabel.setText(lang.format("menu.lanLobby.clientUsernameReady",
                "client", clientUsername));
        lanPlayButton.setDisable(false);
        lanPlayButton.setOnAction(e -> startLanGameAsHost());
    }

    // TODO: Docstring
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

    // TODO: Docstring
    private void handleStartGame(GameStartPacket packet) {
        startLanGame(packet);
    }

    // TODO: Docstring
    private void handlePlayTile(PlayTilePacket packet) {
        if (networkGameListener != null) {
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
    }

    // TODO: Docstring
    private void handlePassTurn() {
        if (networkGameListener != null) {
            networkGameListener.setLocalAction(false);
            game.passTurn();
            networkGameListener.setLocalAction(false);
            showLanOverlay(game.getCurrentPlayer().getName());
        }
    }

    // TODO: Docstring
    private void handleExchangeRack(ExchangeRackPacket packet) {
        if (networkGameListener != null) {
            networkGameListener.setLocalAction(false);
            game.getCurrentPlayer().getRack().setTiles(packet.getNewRack());
            game.getCurrentPlayer().getDeck().setDeck(packet.getRemainingDeck());
            game.completeExchangeAsTurn();
            networkGameListener.setLocalAction(false);
            showLanOverlay(game.getCurrentPlayer().getName());
        }
    }

    // TODO: Docstring
    private void handleBuyAction() {
        if (networkGameListener != null) {
            networkGameListener.setLocalAction(false);
            game.buyAnAction();
            networkGameListener.setLocalAction(false);
        }
    }

    // TODO: Docstring
    private void handleGameOver(GameOverPacket packet) {
        if (game != null) {
            Player winner = game.getPlayers().stream()
                    .filter(p -> p.getName().equals(packet.getWinnerUsername()))
                    .findFirst()
                    .orElse(null);
            showGameOver(winner);
            endGame();
            resetBoard();
        }
    }

    // TODO: Docstring
    private void executeLocalAction(Runnable action) {
        networkGameListener.setLocalAction(true);
        action.run();
        if (networkGameListener != null) {
            networkGameListener.setLocalAction(false);
        }
    }

    /**
     * Triggers the logic to exchange all tiles in the current player's rack.
     */
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

    /**
     * Triggers the logic to buy an extra action using sun points.
     */
    public void buttonBuyAnAction() {
        if (sessionRole == SessionRole.LOCAL) {
            game.buyAnAction();
        } else {
            executeLocalAction(game::buyAnAction);
        }
    }

    /**
     * Passes the current player's turn to the next player.
     */
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

    /**
     * Executes the surrender logic for the currently active player.
     */
    public void buttonSurrender() {
        if (sessionRole == SessionRole.LOCAL) {
            game.surrender();
        } else {
            executeLocalAction(game::surrender);
        }
    }

    // TODO: Docstring
    private void buttonLanReady() {
        LangManager lang = LangManager.getInstance();
        lanPlayButton.setDisable(true);
        sendPacket(new LobbyReadyPacket());
        lanClientNameLabel.setText(lang.format("menu.lanLobby.clientUsernameReady",
                "client", clientUsername));
    }

    // TODO: Docstring
    public void quitApp() {
        stopNetwork();
        System.exit(0);
    }

    // TODO: Docstring
    private Game buildLanGameAsHost() {
        return new Game(buildReferee(), hostUsername, clientUsername);
    }

    // TODO: Docstring
    private GameStartPacket buildGameStartPacket() {
        Player starter = game.getCurrentPlayer();
        Player second = game.getNextPlayer();
        return new GameStartPacket(
                starter.getName(), second.getName(),
                starter.getRack().getTiles(), starter.getDeck().getTiles(),
                second.getRack().getTiles(), second.getDeck().getTiles()
        );
    }

    // TODO: Docstring
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

    /**
     * Toggles the interactive state of the action button.
     */
    public void toggleActionButton() {
        actionButton.setDisable(!actionButton.isDisable());
    }

    /**
     * Sets the active state of the action button explicitly.
     *
     * @param activate A boolean indicating if the button should be disabled (true) or enabled (false).
     */
    public void toggleActionButton(boolean activate) {
        actionButton.setDisable(activate);
    }

    /**
     * Displays the transition overlay between player turns with score summaries.
     *
     * @param nextPlayer The player whose turn is starting.
     * @param lastPlayer The player whose turn just ended.
     * @param lastTurnScore The score acquired by the last player during their turn.
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
    }

    // TODO: docstring
    public void showOverlay() {
        screenOverlay.setVisible(true);
        screenOverlay.setManaged(true);
        endTurnButton.setDisable(true);
    }

    // TODO: docstring
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

    /**
     * Updates the UI images representing the current player's rack of tiles.
     *
     * @param player The player whose rack should be displayed.
     */
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

    /**
     * Refreshes the score, name, and remaining tile count displayed for the current player.
     *
     * @param player The current active player.
     */
    public void updatePlayerInfo(Player player) {
        int remainingTiles = player.getDeck().size() + player.getRack().getTiles().size();
        LangManager lang = LangManager.getInstance();

        currentPlayerLabel.setText(lang.format("menu.game.player.currentPlayer",
                "name", player.getName()));
        currentPlayerScoreLabel.setText(lang.format("menu.game.player.score",
                "score", String.valueOf(player.getScore()))
        );
        playerTileRemainingLabel.setText(lang.format("menu.game.player.remaining",
                "count", String.valueOf(remainingTiles))
        );
    }

    /**
     * Displays an error alert popup to the user detailing why their move was invalid.
     *
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
     *
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
     *
     * @param tile The tile object being placed.
     * @param targetX The X coordinate (column) on the grid.
     * @param targetY The Y coordinate (row) on the grid.
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

        } catch (Exception e) { /* Nothing here */ }
    }

    /**
     * Terminates the active game session and returns the user to the starting menus.
     */
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
        if (game != null) resetBoard();
        game = null;
        stopNetwork();
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
                        getClass().getResource("/" + ThemeManager.getInstance().getBoardBgPath(bgFile))
                ).toExternalForm();

                imageView.setImage(new Image(path));
                imageView.setVisible(true);
            }
        }
        hasAlreadyPlaced = false;
    }

    /**
     * Configures a JavaFX ImageView to support drag operations using its image URL.
     *
     * @param tileView The ImageView representing the draggable tile.
     */
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

    /**
     * Resolves the corresponding Tile model object from the provided image file path.
     *
     * @param fullUrl The absolute file path of the image being parsed.
     *
     * @return The corresponding Tile instance.
     */
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

    /**
     * Validates and returns a player's name, falling back to a default if empty.
     *
     * @param input The raw input string from the username text field.
     * @param defaultName The default string to use if the input is blank or null.
     *
     * @return The finalized player name.
     */
    private String getPlayerName(String input, String defaultName) {
        return (input == null || input.isBlank()) ? defaultName : input;
    }

    /**
     * Stop the TCP and UDP server and reset all variables
     */
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

    /**
     * Gets the active Game instance managed by this controller.
     *
     * @return The active Game object.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Updates the status of whether a tile has already been placed in the current turn.
     *
     * @param value Boolean true if a tile has been placed, false otherwise.
     */
    public void setHasAlreadyPlaced(boolean value) {
        hasAlreadyPlaced = value;
    }

    /**
     * Set lastPlayer and lastTurnScore to info overlay
     *
     * @param lastPlayer the last Player who has played
     * @param lastTurnScore the last Player score
     */
    public void setPendingOverlayInfo(Player lastPlayer, int lastTurnScore) {
        this.pendingLastPlayer = lastPlayer;
        this.pendingLastTurnScore = lastTurnScore;
    }
}
