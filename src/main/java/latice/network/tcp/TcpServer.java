package latice.network.tcp;

import latice.exception.network.ConnectionRefusedException;
import latice.exception.network.NetworkException;
import latice.network.packet.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TcpServer implements Runnable {
    private final int port;
    private final Consumer<Packet> onPacketReceived;
    private final Runnable onDisconnected;
    private TcpClientHandler client;
    private volatile boolean running = true;

    public TcpServer(int port, Consumer<Packet> onPacketReceived, Runnable onDisconnected) {
        this.port = port;
        this.onPacketReceived = onPacketReceived;
        this.onDisconnected = onDisconnected;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                try {
                    TcpClientHandler handler = new TcpClientHandler(clientSocket, onPacketReceived, onDisconnected);
                    client = handler;
                    Thread t = new Thread(handler);
                    t.setDaemon(true);
                    t.start();
                    running = false;
                } catch (IOException e) {
                    throw new ConnectionRefusedException("Failed to create the client handler");
                }
            }
        } catch (IOException e) {
            throw new NetworkException("TcpServer failed");
        }
    }

    public void stop() {
        running = false;
    }

    public void send(Packet packet) throws IOException {
        if (client != null) {
            client.send(packet);
        }
    }
}