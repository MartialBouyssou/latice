package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.ActionType;
import latice.view.controller.GameController;

public class LaticeGameListener implements GameListener {

    private final GameController rackController;
    private boolean playTwiceActive = false;

    public LaticeGameListener(GameController rackController) {
        this.rackController = rackController;
    }

    @Override
    public void onGameStarted(GameStartedEvent event) {
        rackController.updateRack(event.getPlayer());
        rackController.updateScore(event.getPlayer());
    }

    @Override
    public void onActionBought(ActionBoughtEvent event) {
        rackController.updateScore(event.getPlayer());

        if (event.getActionType() == ActionType.PLAY_TWICE) {
            playTwiceActive = true;
        }
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        rackController.hasAlreadyPlaced = false;
        rackController.updateRack(event.getNextPlayer());
        rackController.updateScore(event.getNextPlayer());
    }

    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        rackController.placeTileOnBoard(
                event.getTile(),
                event.getX(),
                event.getY(),
                event.getPlayer()
        );
        rackController.updateScore(event.getPlayer());
        rackController.updateRack(event.getPlayer());

        if (playTwiceActive) {
            playTwiceActive = false;
        } else {
            rackController.hasAlreadyPlaced = true;
        }
        rackController.getGame().endTurn();
    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        rackController.hasAlreadyPlaced = false;
        rackController.updateRack(event.getNextPlayer());
        rackController.updateScore(event.getNextPlayer());
    }

    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        rackController.updateRack(event.getPlayer());
    }

    @Override
    public void onGameOver(GameOverEvent event) {
            rackController.showGameOver(event.getWinner());
    }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        rackController.showInvalidMove(event.getReason());
    }
}