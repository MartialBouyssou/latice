package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.ActionType;
import latice.view.controller.GameController;

public class LaticeGameListener implements GameListener {

    private final GameController gameController;
    private boolean playTwiceActive = false;

    public LaticeGameListener(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public void onGameStarted(GameStartedEvent event) {
        gameController.updateRack(event.getPlayer());
        gameController.updateScore(event.getPlayer());
    }

    @Override
    public void onActionBought(ActionBoughtEvent event) {
        gameController.updateScore(event.getPlayer());

        if (event.getActionType() == ActionType.PLAY_TWICE) {
            playTwiceActive = true;
        }
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        gameController.hasAlreadyPlaced = false;
        gameController.updateRack(event.getNextPlayer());
        gameController.updateScore(event.getNextPlayer());
    }

    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        gameController.placeTileOnBoard(
                event.getTile(),
                event.getX(),
                event.getY(),
                event.getPlayer()
        );
        gameController.updateScore(event.getPlayer());
        gameController.updateRack(event.getPlayer());

        if (playTwiceActive) {
            playTwiceActive = false;
        } else {
            gameController.hasAlreadyPlaced = true;
            gameController.getGame().endTurn();
        }
    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        gameController.hasAlreadyPlaced = false;
        gameController.updateRack(event.getNextPlayer());
        gameController.updateScore(event.getNextPlayer());
    }

    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        gameController.updateRack(event.getPlayer());
    }

    @Override
    public void onGameOver(GameOverEvent event) {
            gameController.showGameOver(event.getWinner());
    }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        gameController.showInvalidMove(event.getReason());
    }

    public void onCannotAfford(CannotAffordEvent event) { gameController.showInvalidMove(event.getReason());}
}