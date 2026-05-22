package latice.application.listeners;

import latice.model.game.events.*;
import latice.view.controller.GameController;

public class LaticeGameListener implements GameListener {

    private final GameController rackController;

    public LaticeGameListener(GameController rackController) {
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
        rackController.placeTileOnBoard(
                event.getTile(),
                event.getX(),
                event.getY(),
                event.getPlayer()
        );
    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) { rackController.updateRack(event.getNextPlayer()); }

    @Override
    public void onRackExchanged(RackExchangedEvent event) { rackController.updateRack(event.getPlayer()); }

    @Override
    public void onGameOver(GameOverEvent event) { }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        System.out.println("Invalid move: " + event.getReason());
        rackController.showInvalidMove();
    }
}