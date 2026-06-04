package latice.view.controller;

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
    private boolean aiActionBoughtThisTurn = false;
    private Map<String, String> availableLanguages;
    private Player aiControlledPlayer;
    private AIPlayer aiPlayer;

    /**
     * Méthode d'initialisation appelée automatiquement par JavaFX lors du chargement du FXML.
     * Prépare les textes, les langues et les événements de glisser-déposer (drag-and-drop).
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
     * Charge les langues disponibles depuis le gestionnaire et initialise la liste déroulante (ComboBox).
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
     * Appelée lorsque l'utilisateur sélectionne une nouvelle langue dans la liste déroulante.
     * Met à jour toute l'interface avec la nouvelle langue choisie.
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
     * Parcours l'interface pour appliquer les textes traduits sur tous les nœuds marqués.
     */
    public void loadTexts() {
        applyTextsRecursively(startMenuStackpane);
        applyTextsRecursively(gameBoardBP);
        applyTextsRecursively(screenOverlay);
    }

    /**
     * Applique récursivement les traductions sur un composant parent et ses enfants.
     * * @param parent Le nœud parent à parcourir.
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
     * Affiche le menu de sélection du mode de jeu (Local, IA, etc.) et masque les autres menus.
     */
    public void showModeMenu() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        modeMenuVBox.setVisible(true);
        modeMenuVBox.setManaged(true);
    }

    /**
     * Affiche le menu de choix de pseudo pour le mode multijoueur local.
     */
    public void showUsernameChoiceMenu() {
        modeMenuVBox.setVisible(false);
        modeMenuVBox.setManaged(false);
        usernameMenuVBox.setVisible(true);
        usernameMenuVBox.setManaged(true);
    }

    /**
     * Affiche le menu de choix de pseudo pour le mode Joueur contre IA.
     */
    public void showUsernameVsAIMenu() {
        modeMenuVBox.setVisible(false);
        modeMenuVBox.setManaged(false);
        usernameVsAIMenuVBox.setVisible(true);
        usernameVsAIMenuVBox.setManaged(true);
    }

    /**
     * Affiche le menu de démarrage principal du jeu.
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
     * Affiche le panneau des règles du jeu.
     */
    public void showRules() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        rulesMenuVBox.setVisible(true);
        rulesMenuVBox.setManaged(true);
    }

    /**
     * Active ou désactive le bouton d'achat d'action (inverse son état actuel).
     */
    public void toggleActionButton() {
        actionButton.setDisable(!actionButton.isDisable());
    }

    /**
     * Définit explicitement l'état activé/désactivé du bouton d'achat d'action.
     * * @param activate {@code true} pour désactiver le bouton, {@code false} pour l'activer.
     */
    public void toggleActionButton(boolean activate) {
        actionButton.setDisable(activate);
    }

    /**
     * Démarre une partie locale en mode Humain contre Humain.
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
     * Démarre une partie en mode Humain contre Intelligence Artificielle.
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
     * Gère et exécute de manière synchrone les décisions et actions du joueur IA.
     * L'IA détermine si elle achète une action supplémentaire et place ses tuiles.
     */
    public void playAITurn() {
        if (aiControlledPlayer == null || aiPlayer == null) {
            return;
        }

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
     * Vérifie si le joueur courant est l'IA, et si oui, déclenche sa réflexion.
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
     * Construit et retourne une nouvelle instance de jeu configurée pour le mode local.
     * * @return La partie initialisée avec les règles et les noms des joueurs.
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
     * Récupère le nom saisi ou attribue un nom par défaut si le champ est vide.
     * * @param input Le nom entré par l'utilisateur.
     * @param defaultName Le nom par défaut à utiliser en cas de champ vide.
     * @return Le pseudo validé.
     */
    private String getPlayerName(String input, String defaultName) {
        if (input == null || input.isBlank()) {
            return defaultName;
        }
        return input;
    }

    /**
     * Affiche l'écran de transition entre deux tours (overlay) masquant le chevalet de l'adversaire.
     * Gère automatiquement la transition si c'est au tour de l'IA de jouer.
     * * @param nextPlayer Le joueur qui doit jouer le tour suivant.
     * @param lastPlayer Le joueur qui vient de terminer son tour.
     * @param lastTurnScore Le score remporté lors du tour précédent.
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
     * Cache l'écran de transition et donne la main au joueur actif ou à l'IA.
     */
    public void hideOverlay() {
        screenOverlay.setVisible(false);
        screenOverlay.setManaged(false);
        endTurnButton.setDisable(false);
        triggerAITurnIfNeeded();
    }

    /**
     * Met à jour l'affichage graphique des tuiles présentes sur le chevalet du joueur.
     * * @param player Le joueur dont le chevalet doit être dessiné.
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
     * Rafraîchit les étiquettes de score, de nom et de tuiles restantes pour le joueur actif.
     * * @param player Le joueur dont les informations doivent être affichées.
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
     * Affiche une pop-up d'erreur informant l'utilisateur que son coup n'est pas permis.
     * * @param reason La raison précise du refus (ex: "Pas assez de points", "Coup illégal").
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
     * Affiche l'écran de fin de partie avec le score du vainqueur.
     * * @param player Le gagnant de la partie, ou {@code null} en cas d'égalité.
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
     * Insère visuellement l'image d'une tuile posée sur la case correspondante du plateau de jeu.
     * * @param tile La tuile à afficher.
     * @param targetX La coordonnée X sur la grille.
     * @param targetY La coordonnée Y sur la grille.
     * @param player Le joueur qui a placé la tuile.
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
     * Configure une image de tuile du chevalet pour la rendre déplaçable avec la souris.
     * * @param tileView Le composant ImageView de la tuile.
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
     * Configure la grille du plateau pour qu'elle accepte le relâchement d'une tuile (drop).
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
     * Déclenche la procédure d'échange de tuiles via le clic sur le bouton correspondant.
     */
    public void buttonExchangeAll() {
        game.exchangeAll();
    }

    /**
     * Déclenche l'achat d'une action supplémentaire via le clic sur le bouton correspondant.
     */
    public void buttonBuyAnAction() {
        game.buyAnAction();
    }

    /**
     * Déclenche la fin ou le passage de tour via le clic sur le bouton "Fin du tour".
     */
    public void buttonEndTurn() {
        if (hasAlreadyPlaced) {
            game.endTurn();
        } else {
            game.passTurn();
        }
    }

    /**
     * Convertit le chemin de l'image de la tuile en son équivalent objet {@link Tile}.
     * * @param fullUrl L'URL complète de l'image.
     * @return L'objet Tile associé.
     */
    private Tile extractTileFromUrl(String fullUrl) {
        return TileFactory.getTileFromImagePath(fullUrl);
    }

    /**
     * Quitte la partie en cours, réinitialise l'état et renvoie au menu principal.
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
     * Réinitialise les images du plateau pour correspondre à un plateau vide en fonction du thème.
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
        aiActionBoughtThisTurn = false;
    }

    /**
     * Retourne l'instance du jeu actuellement active.
     * * @return L'instance {@link Game}.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Affiche le menu de choix du thème visuel du jeu.
     */
    public void showThemeMenu() {
        playMenuVBox.setVisible(false);
        playMenuVBox.setManaged(false);
        themeMenuVBox.setVisible(true);
        themeMenuVBox.setManaged(true);
    }

    /**
     * Applique le thème "Classique" et retourne à l'écran de sélection.
     */
    public void applyThemeDefault() {
        ThemeManager.getInstance().setCurrentTheme(Theme.DEFAULT);
        showPlayMenu();
    }

    /**
     * Applique le thème "Animal Crossing" et retourne à l'écran de sélection.
     */
    public void applyThemeAnimalCrossing() {
        ThemeManager.getInstance().setCurrentTheme(Theme.ANIMAL_CROSSING);
        showPlayMenu();
    }

    /**
     * Applique le thème "Zelda" et retourne à l'écran de sélection.
     */
    public void applyThemeZelda() {
        ThemeManager.getInstance().setCurrentTheme(Theme.ZELDA);
        showPlayMenu();
    }

    /**
     * Déclenche l'abandon de la partie pour le joueur actif.
     */
    public void buttonSurrender() {
        game.surrender();
    }

    /**
     * Met à jour le flag indiquant si le joueur actif a posé sa tuile principale ce tour-ci.
     * * @param value {@code true} si une tuile a été posée, {@code false} sinon.
     */
    public void setHasAlreadyPlaced(boolean value) {
        hasAlreadyPlaced = value;
    }

    /**
     * Met à jour le flag indiquant si l'IA a déjà acheté une action pendant ce tour.
     * * @param value {@code true} si l'action a été achetée, {@code false} sinon.
     */
    public void setAiActionBoughtThisTurn(boolean value) {
        this.aiActionBoughtThisTurn = value;
    }
}