package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.ActionType;
import latice.view.controller.GameController;

public class LaticeGameListener implements GameListener {

    private final GameController gameController;
    private boolean playTwiceActive = false;

    /**
     * Creates the listener bound to the given game controller used to update the UI.
     * @param gameController The controller used to reflect game events on the interface.
     */
    public LaticeGameListener(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Handles the game started event by refreshing the starting player's rack and info on the UI.
     * @param event The event holding the player who starts the game.
     */
    @Override
    public void onGameStarted(GameStartedEvent event) {
        gameController.updateRack(event.getPlayer());
        gameController.updatePlayerInfo(event.getPlayer());
    }

    /**
     * Handles the action bought event by updating the player info and enabling the play-twice mode when relevant.
     * @param event The event describing the bought action and its type.
     */
    @Override
    public void onActionBought(ActionBoughtEvent event) {
        gameController.updatePlayerInfo(event.getPlayer());

        if (event.getActionType() == ActionType.PLAY_TWICE) {
            playTwiceActive = true;
            gameController.toggleActionButton();
        }
    }

    /**
     * Handles the turn ended event by showing the next player overlay and refreshing their rack and info.
     * @param event The event holding the next player, last player and last turn score.
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
     * Handles the tile placed event by drawing the tile on the board, refreshing the UI and ending the turn unless play-twice is active.
     * @param event The event holding the placed tile, its coordinates and the player.
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
        } else {
            gameController.setHasAlreadyPlaced(true);
            gameController.getGame().endTurn();
        }
    }

    /**
     * Handles the turn passed event by showing the next player overlay and refreshing their rack and info.
     * @param event The event holding the next player, last player and last turn score.
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
     * Handles the rack exchanged event by either cancelling play-twice or completing the exchange as a turn, then refreshing the rack.
     * @param event The event holding the player whose rack was exchanged.
     */
    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        if (playTwiceActive) {
            playTwiceActive = false;
            gameController.toggleActionButton(false);
        } else {
            gameController.getGame().completeExchangeAsTurn();
        }

        gameController.updateRack(event.getPlayer());
    }

    /**
     * Handles the game over event by displaying the winner, ending the game and resetting the board.
     * @param event The event holding the winner of the game.
     */
    @Override
    public void onGameOver(GameOverEvent event) {
        gameController.showGameOver(event.getWinner());
        gameController.endGame();
        gameController.resetBoard();
    }

    /**
     * Handles the invalid move event by showing the rejection reason on the UI.
     * @param event The event holding the reason for the invalid move.
     */
    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        gameController.showInvalidMove(event.getReason());
    }

    /**
     * Handles the cannot afford event by showing the reason on the UI.
     * @param event The event holding the reason the player cannot afford the action.
     */
    @Override
    public void onCannotAfford(CannotAffordEvent event) {
        gameController.showInvalidMove(event.getReason());
    }
}
