package latice.network.tcp;

import latice.exception.network.ClientDisconnectedException;
import latice.network.packet.Packet;
import latice.network.packet.PacketSerializer;
import latice.network.packet.game.*;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public class TcpClientHandler implements Runnable {
    private final Socket socket;
    private final Consumer<Packet> onPacketReceived;
    private final DataOutputStream outputStream;

    public TcpClientHandler(Socket socket, Consumer<Packet> onPacketReceived) throws IOException {
        this.socket = socket;
        this.onPacketReceived = onPacketReceived;
        this.outputStream = new DataOutputStream(socket.getOutputStream());
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
            throw new ClientDisconnectedException("Client was disconnected during TCP handling");
        }
    }

    public void send(Packet packet) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(arrayOutputStream);
        packet.writeTo(dataOut);

        byte[] data = arrayOutputStream.toByteArray();
        outputStream.write(data);
        outputStream.flush();
    }
}
