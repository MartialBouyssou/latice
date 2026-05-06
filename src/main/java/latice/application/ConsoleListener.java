package latice.application;

import latice.model.game.events.*;

import static latice.application.LaticeApplicationConsole.message;

public class ConsoleListener implements GameListener {

    @Override
    public void onGameStarted(GameStartedEvent event) {
        String firstPlayer = event.getPlayer().getName();
        message(firstPlayer + " is playing first :)");
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        String nextPlayerName = event.getNextPlayer().getName();
        message("Turn ended. \nNow " + nextPlayerName + " is playing !!!!");
    }

    @Override
    public void onTilePlaced(TilePlacedEvent event) { /* not yet needed */ }
    @Override
    public void onGameOver(GameOverEvent event) { /* not yet needed */ }
    @Override
    public void onInvalidMove(InvalidMoveEvent event) { /* not yet needed */ }
    @Override
    public void onRackExchanged(RackExchangedEvent event) { /* not yet needed */ }
    @Override
    public void onTurnPassed(TurnPassedEvent event) { /* not yet needed */ }
}