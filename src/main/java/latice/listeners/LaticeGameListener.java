package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.ActionType;
import latice.model.player.Player;
import latice.utils.LangManager;
import latice.view.controller.GameController;

/**
 * Listener qui gère les événements du jeu Latice et met à jour l'interface graphique
 * en fonction des actions réalisées par les joueurs (ou par l'intelligence artificielle).
 */
public class LaticeGameListener implements GameListener {

    private final GameController gameController;
    private boolean playTwiceActive = false;

    /**
     * Crée un nouveau listener lié au contrôleur de jeu fourni.
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
     * Met à jour l'affichage du chevalet du premier joueur et ses informations de score/tuiles.
     * * @param event L'événement contenant le joueur qui commence la partie.
     */
    @Override
    public void onGameStarted(GameStartedEvent event) {
        gameController.updateRack(event.getPlayer());
        gameController.updatePlayerInfo(event.getPlayer());
    }

    /**
     * Gère l'événement d'achat d'une action supplémentaire par un joueur.
     * Déduit les points et débloque le plateau pour une deuxième tuile.
     * * @param event L'événement décrivant l'action achetée.
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
     * Prépare le plateau pour le joueur suivant et affiche l'écran de transition.
     * * @param event L'événement de fin de tour contenant les infos des joueurs.
     */
    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        gameController.setHasAlreadyPlaced(false);
        gameController.setAiActionBoughtThisTurn(false);
        gameController.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                event.getLastTurnScore());
        gameController.updateRack(event.getNextPlayer());
        gameController.updatePlayerInfo(event.getNextPlayer());
    }

    /**
     * Gère l'événement de placement d'une tuile sur le plateau.
     * Met à jour visuellement le plateau et termine le tour si l'action n'a pas été prolongée.
     * * @param event L'événement contenant la tuile, les coordonnées et les points gagnés.
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
            gameController.setHasAlreadyPlaced(false);

            if (isAI(event.getPlayer())) {
                gameController.triggerAITurnIfNeeded();
            }
        } else {
            gameController.setHasAlreadyPlaced(true);

            if (isAI(event.getPlayer())) {
                gameController.triggerAITurnIfNeeded();
            } else {
                gameController.getGame().endTurn();
            }
        }
    }

    /**
     * Gère l'événement indiquant qu'un joueur passe son tour.
     * Affiche l'overlay pour le joueur suivant.
     * * @param event L'événement contenant les infos du passage de tour.
     */
    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        gameController.setHasAlreadyPlaced(false);
        gameController.setAiActionBoughtThisTurn(false);
        gameController.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                event.getLastTurnScore());
        gameController.updateRack(event.getNextPlayer());
        gameController.updatePlayerInfo(event.getNextPlayer());
    }

    /**
     * Gère l'échange de tuiles depuis le deck.
     * Termine le tour, ou consomme la deuxième action si elle avait été achetée.
     * * @param event L'événement contenant le joueur concerné.
     */
    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        if (playTwiceActive) {
            playTwiceActive = false;
            gameController.toggleActionButton(false);
            gameController.updateRack(event.getPlayer());

            if (isAI(event.getPlayer())) {
                gameController.triggerAITurnIfNeeded();
            }
        } else {
            gameController.setAiActionBoughtThisTurn(false);
            gameController.getGame().completeExchangeAsTurn();
        }
    }

    /**
     * Gère l'événement de fin de jeu et l'affichage du gagnant.
     * * @param event L'événement de victoire contenant le vainqueur.
     */
    @Override
    public void onGameOver(GameOverEvent event) {
        gameController.showGameOver(event.getWinner());
        gameController.endGame();
        gameController.resetBoard();
    }

    /**
     * Intercepte les coups invalides et affiche une pop-up d'erreur.
     * * @param event L'événement expliquant l'invalidité du mouvement.
     */
    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        gameController.showInvalidMove(event.getReason());
    }

    /**
     * Intercepte l'impossibilité d'acheter une action et affiche une pop-up d'erreur.
     * * @param event L'événement contenant la raison de l'impossibilité.
     */
    @Override
    public void onCannotAfford(CannotAffordEvent event) {
        gameController.showInvalidMove(event.getReason());
    }
}