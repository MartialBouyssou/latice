package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.Player;

import static latice.application.LaticeApplicationConsole.message;

public class ConsoleListener implements GameListener {

    @Override
    public void onGameStarted(GameStartedEvent event) {
        Player player = event.getPlayer();
        message(player.getName() + " is playing first :)");
        message("Here is their hand:" + player.getRack().toString());
    }

    @Override
    public void onActionBought(ActionBoughtEvent event) {
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        message("Turn ended. \nNow " + event.getNextPlayer().getName() + " is playing !!!!");
    }

    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        Player player = event.getPlayer();
        message(player.getName() + " placed tile " + event.getTile()
                + " at position (" + event.getX() + "," + event.getY() + ")");
        message("They now have " + player.getRack().size() + " tiles in their rack.");
    }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        message("Invalid move: " + event.getReason());
    }

    @Override
    public void onGameOver(GameOverEvent event) {
    }

    @Override
    public void onRackExchanged(RackExchangedEvent event) {
    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) {
    }

    @Override
    public void onCannotAfford(CannotAffordEvent event) {
    }
}
