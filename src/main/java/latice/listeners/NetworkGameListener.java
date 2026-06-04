package latice.listeners;

import latice.model.game.events.*;
import latice.network.packet.game.*;
import latice.network.PacketSender;

public class NetworkGameListener implements GameListener {
    private final PacketSender packetSender;
    private boolean isLocalAction = false;

    public NetworkGameListener(PacketSender packetSender) {
        this.packetSender = packetSender;
    }

    public void setLocalAction(boolean value) {
        this.isLocalAction = value;
    }

    @Override
    public void onTilePlaced(TilePlacedEvent event) {
        if (isLocalAction) {
            packetSender.sendPacket(new PlayTilePacket(event.getTile(), event.getX(), event.getY()));
        }
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
    }

    @Override
    public void onTurnPassed(TurnPassedEvent event) {
        if (isLocalAction) {
            packetSender.sendPacket(new PassTurnPacket());
        }
    }

    @Override
    public void onRackExchanged(RackExchangedEvent event) {
        if (isLocalAction) {
            packetSender.sendPacket(new ExchangeRackPacket(
                    event.getLastRack().getTiles(),
                    event.getPlayer().getDeck().getTiles()
            ));
        }
    }

    @Override
    public void onActionBought(ActionBoughtEvent event) {
        if (isLocalAction) {
            packetSender.sendPacket(new BuyActionPacket(event.getActionType()));
        }
    }

    @Override
    public void onGameOver(GameOverEvent event) {
        if (!isLocalAction) {
            return;
        }

        if (event.getWinner() == null) {
            packetSender.sendPacket(GameOverPacket.draw());
            return;
        }

        packetSender.sendPacket(new GameOverPacket(
                event.getWinner().getName(),
                event.getWinner().getScore()
        ));
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
