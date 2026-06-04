package latice.listeners;

import javafx.application.Platform;
import latice.model.game.events.*;
import latice.model.player.ActionType;
import latice.model.player.Player;
import latice.utils.LangManager;
import latice.view.controller.GameController;


public class LaticeGameListener implements GameListener {

    private final GameController gameController;
    private boolean playTwiceActive = false;

    /**
     * Crée le listener lié au contrôleur de jeu donné.
     * * @param gameController Le contrôleur principal de la vue du jeu, utilisé pour mettre à jour l'UI.
     */
    public LaticeGameListener(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Vérifie si le joueur donné est l'IA en comparant son nom avec celui défini dans les fichiers de langue.
     * * @param player Le joueur à vérifier.
     * @return {@code true} si le joueur est l'IA, {@code false} sinon.
     */
    private boolean isAI(Player player) {
        return player != null && player.getName().equals(LangManager.getInstance().get("menu.username.aiPlayerName"));
    }

    /**
     * Gère l'événement de début de partie.
     * Met à jour le chevalet du premier joueur et ses informations sur l'interface.
     * * @param event L'événement contenant le joueur qui commence la partie.
     */
    @Override
    public void onGameStarted(GameStartedEvent event) {
        gameController.updateRack(event.getPlayer());
        gameController.updatePlayerInfo(event.getPlayer());
    }

    /**
     * Gère l'événement d'achat d'une action supplémentaire par un joueur.
     * Met à jour les informations du joueur (pour refléter la baisse de score) et active le mode "jouer deux fois".
     * * @param event L'événement décrivant l'action achetée et le joueur concerné.
     */
    @Override
    public void onActionBought(ActionBoughtEvent event) {
        gameController.updatePlayerInfo(event.getPlayer());

        if (event.getActionType() == ActionType.PLAY_TWICE) {
            playTwiceActive = true;
            gameController.toggleActionButton();
            gameController.setHasAlreadyPlaced(false);
        }
    }

    /**
     * Gère l'événement de fin de tour.
     * Réinitialise l'état de placement, affiche l'écran de transition (overlay) pour le joueur suivant,
     * et rafraîchit son chevalet et ses informations.
     * * @param event L'événement contenant le joueur suivant, le dernier joueur et le score du dernier tour.
     */
    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        gameController.setHasAlreadyPlaced(false);
        gameController.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                event.getLastTurnScore());
        gameController.updateRack(event.getNextPlayer());
        gameController.updatePlayerInfo(event.getNextPlayer());
    }

    /**
     * Gère l'événement de placement d'une tuile sur le plateau.
     * Met à jour le plateau visuel, le score, et le chevalet du joueur.
     * Détermine si le joueur a droit à un second placement (suite à un achat) ou si le tour doit se terminer.
     * * @param event L'événement contenant la tuile placée, ses coordonnées et les points gagnés.
     */
    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        gameController.placeTileOnBoard(
                event.getTile(),
                event.getX(),
                event.getY(),
                event.getPlayer()
        );
        gameController.updatePlayerInfo(event.getPlayer());
        gameController.updateRack(event.getPlayer());

        if (playTwiceActive) {
            playTwiceActive = false;
            gameController.toggleActionButton();
            gameController.setHasAlreadyPlaced(false); // Laisse le plateau ouvert pour la 2ème tuile

            if (isAI(event.getPlayer())) {
                Platform.runLater(() -> gameController.triggerAITurnIfNeeded());
            }
        } else {
            gameController.setHasAlreadyPlaced(true); // Bloque le plateau après la tuile

            if (isAI(event.getPlayer())) {
                Platform.runLater(() -> gameController.triggerAITurnIfNeeded());
            } else {
                gameController.getGame().endTurn();
            }
        }
    }

    /**
     * Gère l'événement d'un tour passé (le joueur choisit de ne rien faire).
     * Affiche l'overlay de transition vers le joueur suivant.
     * * @param event L'événement contenant le prochain joueur, le dernier joueur et le score (qui est de 0).
     */
    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        gameController.setHasAlreadyPlaced(false);
        gameController.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                event.getLastTurnScore());
        gameController.updateRack(event.getNextPlayer());
        gameController.updatePlayerInfo(event.getNextPlayer());
    }

    /**
     * Gère l'événement d'échange du chevalet.
     * Si le joueur avait activé "jouer deux fois", cela consomme sa deuxième action sans terminer le tour.
     * Sinon, cela consomme son action principale et termine le tour.
     * * @param event L'événement contenant le joueur dont le chevalet a été échangé.
     */
    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        if (playTwiceActive) {
            playTwiceActive = false;
            gameController.toggleActionButton(false);
            gameController.updateRack(event.getPlayer());

            if (isAI(event.getPlayer())) {
                Platform.runLater(() -> gameController.triggerAITurnIfNeeded());
            }
        } else {
            gameController.getGame().completeExchangeAsTurn();
        }
    }

    /**
     * Gère l'événement de fin de partie.
     * Affiche le nom du gagnant, ferme la vue de la partie en cours et réinitialise le plateau.
     * * @param event L'événement contenant le joueur gagnant.
     */
    @Override
    public void onGameOver(GameOverEvent event) {
        gameController.showGameOver(event.getWinner());
        gameController.endGame();
        gameController.resetBoard();
    }

    /**
     * Gère l'événement d'un mouvement invalide (ex: ne respecte pas la couleur/forme ou tuile manquante).
     * Affiche une pop-up d'erreur à l'utilisateur.
     * * @param event L'événement contenant la raison du mouvement invalide.
     */
    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        gameController.showInvalidMove(event.getReason());
    }

    /**
     * Gère l'événement où un joueur essaie d'acheter une action sans avoir assez de points.
     * Affiche une pop-up d'erreur à l'utilisateur.
     * * @param event L'événement contenant la raison de l'échec de la transaction.
     */
    @Override
    public void onCannotAfford(CannotAffordEvent event) {
        gameController.showInvalidMove(event.getReason());
    }
}