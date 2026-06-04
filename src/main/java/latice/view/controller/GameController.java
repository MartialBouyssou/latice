package latice.view.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.Event;

import java.io.IOException;
import java.util.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.util.Duration;
import latice.exception.network.NetworkException;
import latice.exception.network.UnhandledPacketException;
import latice.listeners.LaticeGameListener;
import latice.listeners.NetworkGameListener;
import latice.model.deck.Deck;
import latice.model.game.Game;
import latice.model.game.GameFactory;
import latice.model.player.Player;
import latice.model.player.Rack;
import latice.model.referee.Referee;
import latice.model.tile.Tile;
import latice.model.tile.TileFactory;
import latice.model.ai.AIMove;
import latice.model.ai.AIPlayer;
import latice.network.NetworkConfig;
import latice.network.PacketSender;
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
import latice.view.BoardView;
import latice.view.GameView;
import latice.view.MenuView;
import latice.view.PlayerPanelView;
import latice.view.TurnOverlayView;

public class GameController implements GameView, PacketSender {

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
    @FXML private VBox modeMenuVBox;
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

    @FXML private VBox usernameVsAIMenuVBox;
    @FXML private TextField player1UsernameVsAITf;
    @FXML private Button overlayButton;

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

    private BoardView boardView;
    private PlayerPanelView playerPanelView;
    private TurnOverlayView turnOverlayView;
    private MenuView menuView;

    private Player aiControlledPlayer;
    private AIPlayer aiPlayer;
    private boolean aiActionBoughtThisTurn = false;

    /**
     * Initializes the JavaFX controller, setting up views, languages, and drag-and-drop events.
     */
    @FXML
    public void initialize() {
        rackImageViews = List.of(rackTile1, rackTile2, rackTile3, rackTile4, rackTile5);
        createViewHelpers();

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
     * Creates lightweight view helpers around the JavaFX nodes injected by FXML.
     */
    private void createViewHelpers() {
        boardView = new BoardView(board);
        playerPanelView = new PlayerPanelView(rackImageViews, currentPlayerLabel,
                currentPlayerScoreLabel, playerTileRemainingLabel, actionButton);
        turnOverlayView = new TurnOverlayView(screenOverlay, overlayPlayerLabel, endTurnButton,
                lastPlayerLabel, lastPlayerScoreLabel, turnNumberLabel, lanScreenOverlay, turnOfLabel, overlayButton);
        menuView = new MenuView(startMenuStackpane, gameBoardBP, playMenuVBox, modeMenuVBox, usernameMenuVBox,
                rulesMenuVBox, themeMenuVBox, lanMenuVBox, searchLanMenuVBox, lanLobbyMenuVBox, usernameVsAIMenuVBox);
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

        if (selectedLabel != null) {
            availableLanguages.entrySet().stream()
                    .filter(e -> e.getValue().equals(selectedLabel))
                    .findFirst()
                    .ifPresent(e -> {
                        LangManager.getInstance().load(e.getKey());
                        loadTexts();
                    });
        }
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
        if (parent != null) {
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
    }

    /**
     * Displays the menu for choosing the game mode (Local, LAN, IA).
     */
    public void showModeMenu() {
        stopNetwork();
        menuView.showModeMenu();
    }

    /**
     * Displays the menu for choosing player usernames and hides other menus.
     */
    public void showUsernameChoiceMenu() {
        menuView.showUsernameChoiceMenu();
    }

    /**
     * Displays the menu for choosing a human username against AI.
     */
    public void showUsernameVsAIMenu() {
        menuView.showUsernameVsAIMenu();
    }

    /**
     * Displays the main play menu and hides the rules and theme menus.
     */
    public void showPlayMenu() {
        stopNetwork();
        menuView.showPlayMenu();
    }

    /**
     * Displays the lan menu and hides the main play menu.
     */
    public void showLanMenu() {
        menuView.showLanMenu();
    }

    /**
     * Displays the lan search menu and hides the main lan menu while
     * starting UDP requests
     */
    public void showLanSearchMenu() {
        LangManager lang = LangManager.getInstance();
        menuView.showLanSearchMenu();

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
        menuView.showLanLobbyMenu();

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
        menuView.showRulesMenu();
    }

    /**
     * Displays the menu allowing the user to select the visual theme of the game.
     */
    public void showThemeMenu() {
        menuView.showThemeMenu();
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
        menuView.showGameBoard();

        game = buildLocalGame();
        game.addListener(new LaticeGameListener(this, false));
        game.startGame();
        resetBoard();
    }

    /**
     * Starts a local game versus the Artificial Intelligence.
     */
    public void startGameVsAI() {
        sessionRole = SessionRole.LOCAL;
        menuView.showGameBoard();

        LangManager lang = LangManager.getInstance();
        String humanName = getPlayerName(player1UsernameVsAITf.getText(), lang.get("default.firstPlayerName"));
        String aiName = lang.get("menu.username.aiPlayerName");

        game = GameFactory.createGame(humanName, aiName);
        game.addListener(new LaticeGameListener(this, false));
        aiPlayer = new AIPlayer(game.getReferee());
        game.startGame();

        for (Player p : game.getPlayers()) {
            if (p.getName().equals(aiName)) {
                aiControlledPlayer = p;
                break;
            }
        }

        resetBoard();
        triggerAITurnIfNeeded();
    }

    /**
     * Manages and executes the AI player's moves synchronously.
     */
    public void playAITurn() {
        if (aiControlledPlayer == null || aiPlayer == null) return;

        int turnsRemaining = Game.MAX_CYCLES - (game.getTurnCount() / game.getPlayers().size());

        if (hasAlreadyPlaced) {
            game.endTurn();
            return;
        }

        if (!aiActionBoughtThisTurn && aiControlledPlayer.canAfford(Referee.EXTRA_ACTION_COST) && aiControlledPlayer.getRack().getTiles().size() >= 2) {
            if (aiPlayer.isDoublePlayWorthIt(game.getBoard(), aiControlledPlayer.getRack(), aiControlledPlayer.getScore(), turnsRemaining)) {
                aiActionBoughtThisTurn = true;
                game.buyAnAction();
            }
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
     * Checks if the active player is the AI, and triggers its turn asynchronously.
     */
    public void triggerAITurnIfNeeded() {
        if (aiControlledPlayer != null
                && aiPlayer != null
                && game != null
                && game.getCurrentPlayer() != null
                && game.getCurrentPlayer().equals(aiControlledPlayer)) {
            Platform.runLater(() -> playAITurn());
        }
    }

    /**
     * Constructs the Game object, injecting rules and player names.
     *
     * @return The newly instantiated Game object.
     */
    private Game buildLocalGame() {
        LangManager lang = LangManager.getInstance();
        String name1 = getPlayerName(player1UsernameTf.getText(),
                lang.get("default.firstPlayerName"));
        String name2 = getPlayerName(player2UsernameTf.getText(),
                lang.get("default.secondPlayerName"));
        if (name1.equals(name2)) name2 = name2 + "_2";
        return GameFactory.createGame(name1, name2);
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

        game = GameFactory.createGame(starter, second);

        networkGameListener = new NetworkGameListener(this);
        game.addListener(networkGameListener);
        game.addListener(new LaticeGameListener(this, true));

        game.startFromExistingPlayers();

        menuView.showGameBoard();

        showLanOverlay(game.getCurrentPlayer().getName());
    }

    /**
     * Routes an incoming network packet to the appropriate handler on the JavaFX thread.
     *
     * @param packet The packet received from the remote player.
     */
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

    /**
     * Handles the remote client disconnection while hosting a LAN game.
     */
    private void handleClientDisconnected() {
        if (game != null && gameBoardBP.isVisible()) {
            showGameOver(null);
            endGame();
        } else {
            stopNetwork();
            showLanLobbyMenu();
        }
    }

    /**
     * Handles the host disconnection while connected as a LAN client.
     */
    private void handleHostDisconnected() {
        if (game != null && gameBoardBP.isVisible()) {
            showGameOver(null);
            endGame();
        } else {
            showPlayMenu();
        }
    }

    /**
     * Handles a client joining the hosted LAN lobby.
     *
     * @param packet The lobby join packet containing the client name.
     */
    private void handleLobbyJoin(LobbyJoinPacket packet) {
        LangManager lang = LangManager.getInstance();
        clientUsername = packet.getClientName();

        lanHostNameLabel.setText(lang.format("menu.lanLobby.hostUsername", "host", hostUsername));
        lanClientNameLabel.setText(lang.format("menu.lanLobby.clientUsernameNotReady",
                "client", clientUsername));

        sendPacket(new LobbyWelcomePacket(hostUsername));
    }

    /**
     * Handles the host welcome packet received by a LAN client.
     *
     * @param packet The welcome packet containing the host name.
     */
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

    /**
     * Marks the LAN client as ready and enables the host start button.
     */
    private void handleLobbyReady() {
        LangManager lang = LangManager.getInstance();
        lanClientNameLabel.setText(lang.format("menu.lanLobby.clientUsernameReady",
                "client", clientUsername));
        lanPlayButton.setDisable(false);
        lanPlayButton.setOnAction(e -> startLanGameAsHost());
    }

    /**
     * Starts the LAN game from the host side and sends the initial game state to the client.
     */
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

        menuView.showGameBoard();
    }

    /**
     * Starts a LAN game from a game start packet.
     *
     * @param packet The packet containing both players' initial state.
     */
    private void handleStartGame(GameStartPacket packet) {
        startLanGame(packet);
    }

    /**
     * Applies a tile placement received from the remote player.
     *
     * @param packet The packet containing the tile and target coordinates.
     */
    private void handlePlayTile(PlayTilePacket packet) {
        if (networkGameListener != null) {
            networkGameListener.setLocalAction(false);
            Player playerBeforeMove = game.getCurrentPlayer();
            int tilesPlayedBeforeMove = playerBeforeMove.getTilesPlayed();
            game.playTile(packet.getTile(), packet.getX(), packet.getY());
            boolean moveSucceeded = playerBeforeMove.getTilesPlayed() > tilesPlayedBeforeMove;
            if (moveSucceeded && game != null) {
                showLanOverlay(game.getCurrentPlayer().getName());
            }
            networkGameListener.setLocalAction(false);
        }
    }

    /**
     * Applies a pass turn action received from the remote player.
     */
    private void handlePassTurn() {
        if (networkGameListener != null) {
            networkGameListener.setLocalAction(false);
            game.passTurn();
            networkGameListener.setLocalAction(false);
            showLanOverlay(game.getCurrentPlayer().getName());
        }
    }

    /**
     * Applies a rack exchange received from the remote player.
     *
     * @param packet The packet containing the synchronized rack and deck state.
     */
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

    /**
     * Applies an action purchase received from the remote player.
     */
    private void handleBuyAction() {
        if (networkGameListener != null) {
            networkGameListener.setLocalAction(false);
            game.buyAnAction();
            networkGameListener.setLocalAction(false);
        }
    }

    /**
     * Handles a remote game over notification.
     *
     * @param packet The packet containing the remote end game informations.
     */
    private void handleGameOver(GameOverPacket packet) {
        if (game != null) {
            if (packet.isDraw()) {
                showGameOver(null);
                endGame();
                return;
            }

            Player winner = game.getPlayers().stream()
                    .filter(p -> p.getName().equals(packet.getWinnerUsername()))
                    .findFirst()
                    .orElse(null);
            showGameOver(winner);
            endGame();
        }
    }

    /**
     * Runs a local game action while allowing the network listener to relay its events.
     *
     * @param action The local action to execute.
     */
    private void executeLocalAction(Runnable action) {
        if (networkGameListener == null) {
            action.run();
            return;
        }

        networkGameListener.setLocalAction(true);
        try {
            action.run();
        } finally {
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
            if (hasAlreadyPlaced) {
                game.endTurn();
            } else {
                game.passTurn();
            }
        } else {
            executeLocalAction(() -> {
                if (hasAlreadyPlaced) {
                    game.endTurn();
                } else {
                    game.passTurn();
                }
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

    /**
     * Sends the client ready state to the host in a LAN lobby.
     */
    private void buttonLanReady() {
        LangManager lang = LangManager.getInstance();
        lanPlayButton.setDisable(true);
        sendPacket(new LobbyReadyPacket());
        lanClientNameLabel.setText(lang.format("menu.lanLobby.clientUsernameReady",
                "client", clientUsername));
    }

    /**
     * Stops active network resources and exits the application.
     */
    public void quitApp() {
        stopNetwork();
        System.exit(0);
    }

    /**
     * Builds a LAN game for the host using the selected player names.
     *
     * @return A new LAN game instance.
     */
    private Game buildLanGameAsHost() {
        return GameFactory.createGame(hostUsername, clientUsername);
    }

    /**
     * Builds the packet used to synchronize the initial LAN game state.
     *
     * @return The game start packet to send to the client.
     */
    private GameStartPacket buildGameStartPacket() {
        Player starter = game.getCurrentPlayer();
        Player second = game.getNextPlayer();
        return new GameStartPacket(
                starter.getName(), second.getName(),
                starter.getRack().getTiles(), starter.getDeck().getTiles(),
                second.getRack().getTiles(), second.getDeck().getTiles()
        );
    }

    /**
     * Sends a packet through the active LAN connection.
     *
     * @param packet The packet to send.
     */
    @Override
    public void sendPacket(Packet packet) {
        if (sessionRole != SessionRole.LOCAL) {
            try {
                if (sessionRole == SessionRole.HOST && tcpServer != null) {
                    tcpServer.send(packet);
                } else if (sessionRole == SessionRole.CLIENT && tcpClient != null) {
                    tcpClient.send(packet);
                }
            } catch (IOException e) { /* Nothing here logger are not useful */ }
        }
    }

    /**
     * Toggles the interactive state of the action button.
     */
    public void toggleActionButton() {
        setActionButtonDisabled(!actionButton.isDisable());
    }

    /**
     * Sets the active state of the action button explicitly.
     *
     * @param activate A boolean indicating if the button is disabled (true) or enabled (false).
     */
    public void toggleActionButton(boolean activate) {
        setActionButtonDisabled(activate);
    }

    /**
     * Displays the transition overlay between player turns with score summaries.
     *
     * @param nextPlayer The player whose turn is starting.
     * @param lastPlayer The player whose turn just ended.
     * @param lastTurnScore The score acquired by the last player during their turn.
     */
    @Override
    public void showOverlayForPlayer(Player nextPlayer, Player lastPlayer, int lastTurnScore) {
        turnOverlayView.showTurnOverlay(game, nextPlayer, lastPlayer, lastTurnScore);

        String aiName = LangManager.getInstance().get("menu.username.aiPlayerName");
        if (nextPlayer.getName().equals(aiName)) {
            turnOverlayView.setReadyButtonVisible(false);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> hideOverlay());
            pause.play();
        } else {
            turnOverlayView.setReadyButtonVisible(true);
        }
    }

    /**
     * Hides the transition overlay and re-enables the end turn button.
     */
    public void hideOverlay() {
        turnOverlayView.hideTurnOverlay();
        triggerAITurnIfNeeded();
    }

    /**
     * Shows the generic turn transition overlay.
     */
    public void showOverlay() {
        turnOverlayView.showTurnOverlay();
    }

    /**
     * Updates the LAN overlay depending on whether the current turn belongs to the local player.
     *
     * @param currentPlayerName The name of the player whose turn is active.
     */
    public void showLanOverlay(String currentPlayerName) {
        String localUsername = sessionRole == SessionRole.HOST ?
                hostUsername : clientUsername;
        boolean isMyTurn = currentPlayerName.equals(localUsername);

        if (isMyTurn) {
            turnOverlayView.hideLanWaitingOverlay();
            updateRack(game.getCurrentPlayer());
            updatePlayerInfo(game.getCurrentPlayer());
            if (game.getTurnCount() != 0 && pendingLastPlayer != null) {
                showOverlayForPlayer(game.getCurrentPlayer(), pendingLastPlayer,
                        pendingLastTurnScore);
            }
            pendingLastPlayer = null;
            pendingLastTurnScore = 0;
        } else {
            if (game.getTurnCount() != 0) {
                hideOverlay();
            }
            turnOverlayView.showLanWaitingOverlay(currentPlayerName);
        }
    }

    /**
     * Updates the UI images representing the current player's rack of tiles.
     *
     * @param player The player whose rack should be displayed.
     */
    @Override
    public void updateRack(Player player) {
        playerPanelView.updateRack(player);
    }

    /**
     * Refreshes the score, name, and remaining tile count displayed for the current player.
     *
     * @param player The current active player.
     */
    @Override
    public void updatePlayerInfo(Player player) {
        playerPanelView.updatePlayerInfo(player);
    }

    /**
     * Displays an error alert popup to the user detailing why their move was invalid.
     *
     * @param reason The specific rule violation or error message.
     */
    @Override
    public void showInvalidMove(String reason) {
        turnOverlayView.showInvalidMove(reason);
    }

    /**
     * Displays the end-of-game alert showing the winner and their score, or a tie message.
     *
     * @param player The winning player, or null if the game resulted in a tie.
     */
    @Override
    public void showGameOver(Player player) {
        turnOverlayView.showGameOver(player);
    }

    /**
     * Visually places a tile onto the board grid and updates the player's rack view.
     *
     * @param tile The tile object being placed.
     * @param targetX The X coordinate (column) on the grid.
     * @param targetY The Y coordinate (row) on the grid.
     * @param player The player performing the placement.
     */
    @Override
    public void placeTileOnBoard(Tile tile, int targetX, int targetY, Player player) {
        boardView.placeTile(tile, targetX, targetY);
    }

    /**
     * Terminates the active game session and returns the user to the starting menus.
     */
    @Override
    public void endGame() {
        if (turnOverlayView != null) {
            turnOverlayView.hideTurnOverlay();
            turnOverlayView.hideLanWaitingOverlay();
        }
        if (game != null && game.getBoard() != null) {
            resetBoard();
        }

        aiControlledPlayer = null;
        aiPlayer = null;

        game = null;
        stopNetwork();
        menuView.showStartMenu();
        menuView.showPlayMenu();
    }

    /**
     * Resets the graphical state of the board to its empty, initial configuration.
     */
    public void resetBoard() {
        if (game != null && game.getBoard() != null) {
            boardView.reset(game.getBoard());
        }
        hasAlreadyPlaced = false;
        aiActionBoughtThisTurn = false;
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

            Tile tile = extractTileFromUrl(db.getString());

            if (tile != null) {
                boardView.highlightCells(game.getValidPositionsFor(tile));
            }

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
                        boardView.clearHighlights();
                        if (sessionRole == SessionRole.LOCAL) {
                            game.playTile(tile, col, row);
                        } else {
                            executeLocalAction(() -> {
                                Player playerBeforeMove = game.getCurrentPlayer();
                                int tilesPlayedBeforeMove = playerBeforeMove.getTilesPlayed();
                                game.playTile(tile, col, row);
                                boolean moveSucceeded =
                                        playerBeforeMove.getTilesPlayed() > tilesPlayedBeforeMove;
                                if (moveSucceeded && game != null) {
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
     * Sets the action button disabled state through the player panel view.
     *
     * @param disabled True when the button must be disabled.
     */
    @Override
    public void setActionButtonDisabled(boolean disabled) {
        playerPanelView.setActionButtonDisabled(disabled);
    }

    /**
     * Indicates if the active game still allows one more action this turn.
     *
     * @return True when an action remains.
     */
    @Override
    public boolean hasRemainingAction() {
        return game != null && game.hasRemainingAction();
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
    @Override
    public void setHasAlreadyPlaced(boolean value) {
        hasAlreadyPlaced = value;
    }

    //TODO: docstring
    /**
     * @param value
     */
    public void setAiActionBoughtThisTurn(boolean value) {
        this.aiActionBoughtThisTurn = value;
    }

    /**
     * Set lastPlayer and lastTurnScore to info overlay
     *
     * @param lastPlayer the last Player who has played
     * @param lastTurnScore the last Player score
     */
    @Override
    public void setPendingOverlayInfo(Player lastPlayer, int lastTurnScore) {
        this.pendingLastPlayer = lastPlayer;
        this.pendingLastTurnScore = lastTurnScore;
    }
}
