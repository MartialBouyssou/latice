package latice.listeners;

import latice.model.game.events.*;
import latice.model.player.ActionType;
import latice.network.packet.game.*;
import latice.view.controller.GameController;

public class NetworkGameListener implements GameListener {
    private final GameController gameController;
    private boolean isLocalAction = false;

    public NetworkGameListener(GameController gameController) {
        this.gameController = gameController;
    }

    public void setLocalAction(boolean value) {
        this.isLocalAction = value;
    }

    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        if (isLocalAction) {
            gameController.sendPacket(new PlayTilePacket(event.getTile(), event.getX(), event.getY()));
        }
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        if (isLocalAction) {
            gameController.sendPacket(new PassTurnPacket());
        }
    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        if (isLocalAction) {
            gameController.sendPacket(new PassTurnPacket());
        }
    }

    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        if (isLocalAction) {
            gameController.sendPacket(new ExchangeRackPacket(event.getLastRack().getTiles()));
        }
    }

    @Override
    public void onActionBought(ActionBoughtEvent event) {
        if (isLocalAction) {
            gameController.sendPacket(new BuyActionPacket(event.getActionType()));
        }
    }

    @Override
    public void onGameOver(GameOverEvent event) {
        if (isLocalAction && event.getWinner() != null) {
            gameController.sendPacket(new GameOverPacket(
                    event.getWinner().getName(),
                    event.getWinner().getScore()
            ));
        }
    }

    @Override
    public void onGameStarted(GameStartedEvent event) {
        /* Not used yet */
    }

    @Override
    public void onInvalidMove(InvalidMoveEvent event) {
        /* Not used yet */
    }

    @Override
    public void onCannotAfford(CannotAffordEvent event) {
        /* Not used yet */
    }
}
