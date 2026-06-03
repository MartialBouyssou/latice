package latice.network.packet.game;

import latice.network.packet.Packet;
import latice.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GameOverPacket implements Packet {
    private final String winnerUsername;
    private final int winnerScore;

    public GameOverPacket(String winnerUsername, int winnerScore) {
        this.winnerUsername = winnerUsername;
        this.winnerScore = winnerScore;
    }

    public String getWinnerUsername() {
        return winnerUsername;
    }

    public int getWinnerScore() {
        return winnerScore;
    }

    @Override
    public PacketType getType() {
        return PacketType.GAME_OVER;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
        out.writeUTF(winnerUsername);
        out.writeInt(winnerScore);
    }

    public static GameOverPacket readFrom(DataInputStream in) throws IOException {
        String winner = in.readUTF();
        int score = in.readInt();

        return new GameOverPacket(winner, score);
    }
}
