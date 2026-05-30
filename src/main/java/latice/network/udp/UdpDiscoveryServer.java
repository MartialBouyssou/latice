package latice.network.udp;

import latice.exception.network.NetworkException;
import latice.network.NetworkConfig;
import latice.network.packet.Packet;
import latice.network.packet.PacketSerializer;
import latice.network.packet.lobby.DiscoveryRequestPacket;
import latice.network.packet.lobby.DiscoveryResponsePacket;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpDiscoveryServer implements Runnable {
    private final int port;
    private final String hostName;
    private volatile boolean running = true;

    public UdpDiscoveryServer(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(NetworkConfig.UDP_DISCOVERY_PORT)) {
            while (running) {
                // That's the broadcast (if I'm right AND I HOPEE) !
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(receivePacket.getData());
                DataInputStream dataInputStream = new DataInputStream(byteArrayIn);

                Packet packet = PacketSerializer.read(dataInputStream);

                if (packet instanceof DiscoveryRequestPacket) {
                    InetAddress clientAddr = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();

                    DiscoveryResponsePacket responsePacket = new DiscoveryResponsePacket(
                            hostName, port
                    );

                    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOut);
                    responsePacket.writeTo(dataOutputStream);

                    byte[] responseData = byteArrayOut.toByteArray();

                    DatagramPacket sendPacket = new DatagramPacket(
                            responseData, responseData.length, clientAddr, clientPort
                    );

                    socket.send(sendPacket);
                }
            }

        } catch (IOException e) {
            throw new NetworkException("We have some issues with lan multiplayer discovery");
        }
    }

    public void stop() {
        running = false;
    }
}
