package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.Player;

import static latice.application.LaticeApplicationConsole.message;

public class ConsoleListener implements GameListener {

    /**
     * Handles the game started event by announcing the first player and printing their hand.
     * @param event The event holding the player who starts the game.
     */
    @Override
    public void onGameStarted(GameStartedEvent event) {
        Player player = event.getPlayer();
        message(player.getName() + " is playing first :)");
        message("Here is their hand:" + player.getRack().toString());
    }

    /**
     * Handles the action bought event. No console output is produced for this event.
     * @param event The event describing the bought action.
     */
    @Override
    public void onActionBought(ActionBoughtEvent event) {
    }

    /**
     * Handles the turn ended event by announcing which player plays next.
     * @param event The event holding the next player to play.
     */
    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        message("Turn ended. \nNow " + event.getNextPlayer().getName() + " is playing !!!!");
    }

    /**
     * Handles the tile placed event by printing the placed tile, its position and the player's rack size.
     * @param event The event holding the placed tile, its coordinates and the player.
     */
    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        Player player = event.getPlayer();
        message(player.getName() + " placed tile " + event.getTile()
                + " at position (" + event.getX() + "," + event.getY() + ")");
        message("They now have " + player.getRack().size() + " tiles in their rack.");
    }

    /**
     * Handles the invalid move event by printing the reason why the move was rejected.
     * @param event The event holding the reason for the invalid move.
     */
    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        message("Invalid move: " + event.getReason());
    }

    /**
     * Handles the game over event. No console output is produced for this event.
     * @param event The event describing the end of the game.
     */
    @Override
    public void onGameOver(GameOverEvent event) {
    }

    /**
     * Handles the rack exchanged event. No console output is produced for this event.
     * @param event The event describing the rack exchange.
     */
    @Override
    public void onRackExchanged(RackExchangedEvent event) {
    }

    /**
     * Handles the turn passed event. No console output is produced for this event.
     * @param event The event describing the passed turn.
     */
    @Override
    public void onTurnPassed(TurnPassedEvent event) {
    }

    /**
     * Handles the cannot afford event. No console output is produced for this event.
     * @param event The event describing the action the player cannot afford.
     */
    @Override
    public void onCannotAfford(CannotAffordEvent event) {
    }
}
