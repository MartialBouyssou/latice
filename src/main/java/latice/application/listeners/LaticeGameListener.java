package latice.application.listeners;

import latice.model.game.events.*;
import latice.view.controller.GameController;

public class LaticeGameListener implements GameListener {

    private final GameController gameController;

    public LaticeGameListener(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public void onGameStarted(GameStartedEvent event) {
        gameController.updateRack(event.getPlayer());
    }

    @Override
    public void onActionBought(ActionBoughtEvent event) {

    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        gameController.updateRack(event.getNextPlayer());
    }

    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        gameController.updateRack(event.getPlayer());
    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        gameController.updateRack(event.getNextPlayer());
    }

    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        gameController.updateRack(event.getPlayer());
    }

    @Override
    public void onGameOver(GameOverEvent event) { /* unused event atm */ }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        gameController.showInvalidMove();
    }
}
