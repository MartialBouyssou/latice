// Fichier : projet actuel/LaticeGameListener.java
package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.ActionType;
import latice.model.player.Player;
import latice.utils.LangManager;
import latice.view.GameView;
import latice.view.controller.GameController;

public class LaticeGameListener implements GameListener {

    private final GameView gameView;
    private final boolean isLanMode;

    /**
     * Creates the listener bound to a game view used to update the UI.
     * @param gameView The view used to reflect game events on the interface.
     * @param isLanMode A boolean to define if you want to execute it in local or not
     */
    public LaticeGameListener(GameView gameView, boolean isLanMode) {
        this.gameView = gameView;
        this.isLanMode = isLanMode;
    }

    /**
     * Checks if the given player is the AI by comparing their name against the locale settings.
     * @param player The player to check.
     * @return {@code true} if the player is the AI, {@code false} otherwise.
     */
    private boolean isAI(Player player) {
        return player != null && player.getName().equals(LangManager.getInstance().get("menu.username.aiPlayerName"));
    }

    /**
     * Handles the game started event by refreshing the starting player's rack and info on the UI.
     * @param event The event holding the player who starts the game.
     */
    @Override
    public void onGameStarted(GameStartedEvent event) {
        gameView.updateRack(event.getPlayer());
        gameView.updatePlayerInfo(event.getPlayer());
    }

    /**
     * Handles the action bought event by updating the player info and disabling the action button
     * until the bought action is used.
     * @param event The event describing the bought action and its type.
     */
    @Override
    public void onActionBought(ActionBoughtEvent event) {
        gameView.updatePlayerInfo(event.getPlayer());
        if (event.getActionType() == ActionType.PLAY_TWICE) {
            gameView.setActionButtonDisabled(true);
        }
    }

    /**
     * Handles the turn ended event by showing the next player overlay and refreshing
     * their rack and info.
     * @param event The event holding the next player, last player and last turn score.
     */
    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        gameView.setHasAlreadyPlaced(false);
        gameView.setActionButtonDisabled(false);
        if (gameView instanceof GameController gc) {
            gc.setAiActionBoughtThisTurn(false);
        }

        if (isLanMode) {
            gameView.setPendingOverlayInfo(event.getLastPlayer(), event.getLastTurnScore());
        } else {
            gameView.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                    event.getLastTurnScore());
            gameView.updateRack(event.getNextPlayer());
            gameView.updatePlayerInfo(event.getNextPlayer());
        }
    }

    /**
     * Handles the tile placed event by drawing the tile on the board and refreshing the UI.
     * @param event The event holding the placed tile, its coordinates and the player.
     */
    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        gameView.placeTileOnBoard(
                event.getTile(),
                event.getX(),
                event.getY(),
                event.getPlayer()
        );
        gameView.updatePlayerInfo(event.getPlayer());
        gameView.updateRack(event.getPlayer());

        if (!isLanMode) {
            boolean hasRemainingAction = gameView.hasRemainingAction();
            gameView.setHasAlreadyPlaced(!hasRemainingAction);
            if (hasRemainingAction) {
                gameView.setActionButtonDisabled(false);
            }

            if (isAI(event.getPlayer()) && gameView instanceof GameController gc) {
                gc.triggerAITurnIfNeeded();
            }
        }
    }

    /**
     * Handles the turn passed event by showing the next player overlay and refreshing their rack and info.
     * @param event The event holding the next player, last player and last turn score.
     */
    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        gameView.setHasAlreadyPlaced(false);
        gameView.setActionButtonDisabled(false);
        if (gameView instanceof GameController gc) {
            gc.setAiActionBoughtThisTurn(false);
        }

        if (isLanMode) {
            gameView.setPendingOverlayInfo(event.getLastPlayer(), event.getLastTurnScore());
        } else {
            gameView.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                    event.getLastTurnScore());
            gameView.updateRack(event.getNextPlayer());
            gameView.updatePlayerInfo(event.getNextPlayer());
        }
    }

    /**
     * Handles the rack exchanged event by refreshing the rack and action state.
     * @param event The event holding the player whose rack was exchanged.
     */
    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        gameView.updateRack(event.getPlayer());
        if (!isLanMode) {
            boolean hasRemainingAction = gameView.hasRemainingAction();
            gameView.setHasAlreadyPlaced(!hasRemainingAction);
            if (hasRemainingAction) {
                gameView.setActionButtonDisabled(false);
            }

            if (isAI(event.getPlayer()) && gameView instanceof GameController gc) {
                gc.triggerAITurnIfNeeded();
            }
        }
    }

    /**
     * Handles the game over event by displaying the winner, ending the game and resetting the board.
     * @param event The event holding the winner of the game.
     */
    @Override
    public void onGameOver(GameOverEvent event) {
        gameView.showGameOver(event.getWinner());
        gameView.endGame();
    }

    /**
     * Handles the invalid move event by showing the rejection reason on the UI.
     * @param event The event holding the reason for the invalid move.
     */
    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        gameView.showInvalidMove(event.getReason());
    }

    /**
     * Handles the cannot afford event by showing the reason on the UI.
     * @param event The event holding the reason the player cannot afford the action.
     */
    @Override
    public void onCannotAfford(CannotAffordEvent event) {
        gameView.showInvalidMove(event.getReason());
    }
}