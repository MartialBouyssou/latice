package latice.application.listeners;

import latice.model.game.events.*;
import latice.view.controller.RackController;

public class LaticeGameListener implements GameListener {

    private final RackController rackController;

    public LaticeGameListener(RackController rackController) {
        this.rackController = rackController;
    }

    @Override
    public void onGameStarted(GameStartedEvent event) {
        rackController.updateRack(event.getPlayer());
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        rackController.updateRack(event.getNextPlayer());
    }

    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        rackController.updateRack(event.getPlayer());
    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        rackController.updateRack(event.getNextPlayer());
    }

    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        rackController.updateRack(event.getPlayer());
    }

    @Override
    public void onGameOver(GameOverEvent event) { }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        rackController.showInvalidMove();
    }
}
