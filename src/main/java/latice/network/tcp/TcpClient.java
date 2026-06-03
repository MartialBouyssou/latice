package latice.network.tcp;

import latice.exception.network.ClientDisconnectedException;
import latice.network.packet.Packet;
import latice.network.packet.PacketSerializer;
import latice.network.packet.lobby.LobbyJoinPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.function.Consumer;

public class TcpClient implements Runnable {
    private Socket socket;
    private DataOutputStream outputStream;
    private final String playerUsername;
    private final Consumer<Packet> onPacketReceived;
    private final Runnable onDisconnected;

    public TcpClient(String playerUsername, Consumer<Packet> packetConsumer, Runnable onDisconnected) {
        this.playerUsername = playerUsername;
        this.onPacketReceived = packetConsumer;
        this.onDisconnected = onDisconnected;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            while (true) {
                Packet packet = PacketSerializer.read(in);
                onPacketReceived.accept(packet);
            }
        } catch (IOException e) {
            onDisconnected.run();
        }
    }

    public void connect(InetAddress address, int port) throws IOException {
        socket = new Socket(address, port);
        outputStream = new DataOutputStream(socket.getOutputStream());
        send(new LobbyJoinPacket(playerUsername));
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public synchronized void send(Packet packet) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(arrayOutputStream);
        packet.writeTo(dataOut);

        byte[] data = arrayOutputStream.toByteArray();
        outputStream.write(data);
        outputStream.flush();
    }
}
