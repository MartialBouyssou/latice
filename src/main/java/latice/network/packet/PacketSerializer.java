package latice.network.packet;

import latice.network.packet.game.*;
import latice.network.packet.lobby.*;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketSerializer {
    private PacketSerializer() {
        /* This utility class should not be instantiated */
    }

    public static Packet read(DataInputStream in) throws IOException {
        int typeOrdinal = in.readInt();
        PacketType type = PacketType.values()[typeOrdinal];

        return switch (type) {
            case DISCOVERY_REQUEST -> DiscoveryRequestPacket.readFrom(in);
            case DISCOVERY_RESPONSE -> DiscoveryResponsePacket.readFrom(in);
            case LOBBY_JOIN -> LobbyJoinPacket.readFrom(in);
            case LOBBY_WELCOME -> LobbyWelcomePacket.readFrom(in);
            case LOBBY_READY -> LobbyReadyPacket.readFrom();
            case TILE_PLACED -> PlayTilePacket.readFrom(in);
            case PASS_TURN -> PassTurnPacket.readFrom();
            case EXCHANGE -> ExchangeRackPacket.readFrom(in);
            case BUY_ACTION -> BuyActionPacket.readFrom(in);
            case GAME_OVER -> GameOverPacket.readFrom(in);
            case GAME_START -> GameStartPacket.readFrom(in);
        };
    }
}
