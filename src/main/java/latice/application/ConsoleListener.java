package latice.application;

import latice.model.game.events.*;

public class ConsoleListener implements GameListener {

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        String nextPlayerName = event.getNextPlayer().getName();
        System.out.println("Turn ended. \nNow " + nextPlayerName + " is playing !!!!");
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
    @Override
    public void onGameStarted(GameStartedEvent event) { /* not yet needed */ }
}