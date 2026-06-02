package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.ActionType;
import latice.view.controller.GameController;

public class LaticeGameListener implements GameListener {

    private final GameController gameController;
    private final boolean isLanMode;
    private boolean playTwiceActive = false;

    public LaticeGameListener(GameController gameController, boolean isLanMode) {
        this.gameController = gameController;
        this.isLanMode = isLanMode;
    }

    @Override
    public void onGameStarted(GameStartedEvent event) {
        gameController.updateRack(event.getPlayer());
        gameController.updatePlayerInfo(event.getPlayer());
    }

    @Override
    public void onActionBought(ActionBoughtEvent event) {
        gameController.updatePlayerInfo(event.getPlayer());

        if (event.getActionType() == ActionType.PLAY_TWICE) {
            playTwiceActive = true;
            gameController.toggleActionButton();
        }
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        gameController.setHasAlreadyPlaced(false);
        gameController.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                event.getLastTurnScore());
        gameController.updateRack(event.getNextPlayer());
        gameController.updatePlayerInfo(event.getNextPlayer());
    }

    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        gameController.placeTileOnBoard(
                event.getTile(),
                event.getX(),
                event.getY(),
                event.getPlayer()
        );
        gameController.updatePlayerInfo(event.getPlayer());
        gameController.updateRack(event.getPlayer());

        if (isLanMode) return;

        if (playTwiceActive) {
            playTwiceActive = false;
            gameController.toggleActionButton();
        } else {
            gameController.setHasAlreadyPlaced(true);
            gameController.getGame().endTurn();
        }
    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        gameController.setHasAlreadyPlaced(false);
        gameController.showOverlayForPlayer(event.getNextPlayer(), event.getLastPlayer(),
                event.getLastTurnScore());
        gameController.updateRack(event.getNextPlayer());
        gameController.updatePlayerInfo(event.getNextPlayer());
    }

    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        if (playTwiceActive) {
            playTwiceActive = false;
            gameController.toggleActionButton(false);
        } else {
            gameController.getGame().completeExchangeAsTurn();
        }

        gameController.updateRack(event.getPlayer());
    }

    @Override
    public void onGameOver(GameOverEvent event) {
        gameController.showGameOver(event.getWinner());
        gameController.endGame();
        gameController.resetBoard();
    }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        gameController.showInvalidMove(event.getReason());
    }

    @Override
    public void onCannotAfford(CannotAffordEvent event) {
        gameController.showInvalidMove(event.getReason());
    }
}
