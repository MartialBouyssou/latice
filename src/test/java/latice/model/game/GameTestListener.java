package latice.model.game;

import latice.model.game.events.*;

public class GameTestListener implements GameListener {
    public int turnPassedCount;
    public int turnEndedCount;

    @Override
    public void onTilePlaced(TilePlacedEvent event) {
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        turnEndedCount++;
    }

    @Override
    public void onGameOver(GameOverEvent event) {
    }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
    }

    @Override
    public void onRackExchanged(RackExchangedEvent event) {
    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        turnPassedCount++;
    }

    @Override
    public void onGameStarted(GameStartedEvent event) {
    }

    @Override
    public void onActionBought(ActionBoughtEvent event) {
    }

    @Override
    public void onCannotAfford(CannotAffordEvent event) {
    }
}