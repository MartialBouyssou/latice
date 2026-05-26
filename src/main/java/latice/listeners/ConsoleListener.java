package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.Player;

import static latice.application.LaticeApplicationConsole.message;

public class ConsoleListener implements GameListener {

    @Override
    public void onGameStarted(GameStartedEvent event) {
        Player player = event.getPlayer();
        String firstPlayer = player.getName();
        message(firstPlayer + " is playing first :)");
        message("Here is he's hand:" + player.getRack().toString());
    }

    @Override
    public void onActionBought(ActionBoughtEvent event) {

    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        String nextPlayerName = event.getNextPlayer().getName();
        message("Turn ended. \nNow " + nextPlayerName + " is playing !!!!");
    }

    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        Player player = event.getPlayer();

        message(player.getName() + " placed tile " + event.getTile() +
                " at position (" + event.getX() + "," + event.getY() + ")");
        message("He have now " + player.getRack().size() +
                " tiles in his rack. \nHe need to draw");

        event.getPlayer().drawTile();

        message("He have now " + player.getRack().size() +
                " tiles in his rack. He drew a tile");

        message("-------------------------------------");
    }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        message("Invalid move: " + event.getReason());
    }

    @Override
    public void onGameOver(GameOverEvent event) { /* not yet needed */ }
    @Override
    public void onRackExchanged(RackExchangedEvent event) { /* not yet needed */ }
    @Override
    public void onTurnPassed(TurnPassedEvent event) { /* not yet needed */ }

    @Override
    public void onCannotAfford(CannotAffordEvent event) { /* not yet needed */ }

}