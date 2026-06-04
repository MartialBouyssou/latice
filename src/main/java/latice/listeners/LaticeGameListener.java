package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.ActionType;
import latice.model.player.Player;
import latice.utils.LangManager;
import latice.view.controller.GameController;

/**
 * Listener qui gère les événements du jeu Latice et met à jour l'interface graphique
 * en fonction des actions réalisées par les joueurs ou l'IA.
 */
public class LaticeGameListener implements GameListener {

    private final GameController gameController;
    private boolean playTwiceActive = false;

    /**
     * Crée le listener lié au contrôleur de jeu donné.
     * @param gameController Le contrôleur principal de la vue du jeu, utilisé pour mettre à jour l'UI.
     */
    public LaticeGameListener(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Vérifie si le joueur donné est l'IA en comparant son nom avec celui défini dans les fichiers de langue.
     * @param player Le joueur à vérifier.
     * @return true si le joueur est l'IA, false sinon.
     */
    private boolean isAI(Player player) {
        return player != null && player.getName().equals(LangManager.getInstance().get("menu.username.aiPlayerName"));
    }

    @Override
    public void onGameStarted(GameStartedEvent event) {
        gameController.updateRack(event.getPlayer());
        gameController.updatePlayerInfo(event.getPlayer());
    }

    @Override
    public void onActionBought(ActionBoughtEvent event) {
        gameController.updatePlayerInfo(event.getPlayer());

        if (event.getActionType() == ActionType.PLAY_TWICE) {
            playTwiceActive = true;
            gameController.toggleActionButton();
            gameController.setHasAlreadyPlaced(false);
        }
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        gameController.setHasAlreadyPlaced(false);
        gameController.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                event.getLastTurnScore());
        gameController.updateRack(event.getNextPlayer());
        gameController.updatePlayerInfo(event.getNextPlayer());
    }

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
                gameController.triggerAITurnIfNeeded();
            }
        } else {
            gameController.setHasAlreadyPlaced(true); // Bloque le plateau après la tuile

            if (isAI(event.getPlayer())) {
                gameController.triggerAITurnIfNeeded();
            } else {
                gameController.getGame().endTurn();
            }
        }
    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        gameController.setHasAlreadyPlaced(false);
        gameController.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                event.getLastTurnScore());
        gameController.updateRack(event.getNextPlayer());
        gameController.updatePlayerInfo(event.getNextPlayer());
    }

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
            gameController.getGame().completeExchangeAsTurn();
        }
    }

    @Override
    public void onGameOver(GameOverEvent event) {
        gameController.showGameOver(event.getWinner());
        gameController.endGame();
        gameController.resetBoard();
    }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        gameController.showInvalidMove(event.getReason());
    }

    @Override
    public void onCannotAfford(CannotAffordEvent event) {
        gameController.showInvalidMove(event.getReason());
    }
}