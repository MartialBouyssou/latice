package latice.network.udp;

import latice.network.NetworkConfig;
import latice.network.packet.Packet;
import latice.network.packet.PacketSerializer;
import latice.network.packet.lobby.DiscoveryRequestPacket;
import latice.network.packet.lobby.DiscoveryResponsePacket;
import latice.network.session.DiscoveredHost;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class UdpDiscoveryClient {
    private static final int LISTENING_TIME = 3000;

    public List<DiscoveredHost> discover() throws IOException {
        List<DiscoveredHost> hosts = new ArrayList<>();

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout(LISTENING_TIME);

            DiscoveryRequestPacket requestPacket = new DiscoveryRequestPacket(NetworkConfig.PROTOCOL_VERSION);
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOut);

            requestPacket.writeTo(dataOutputStream);

            byte[] sendData = byteArrayOut.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData,
                    sendData.length,
                    InetAddress.getByName("255.255.255.255"),
                    NetworkConfig.UDP_DISCOVERY_PORT
            );
            socket.send(sendPacket);

            boolean seeking = true;
            while (seeking) {
                try {
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(
                            receiveData, receiveData.length
                    );
                    socket.receive(receivePacket);

                    ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(receivePacket.getData());
                    DataInputStream dataInputStream = new DataInputStream(byteArrayIn);

                    Packet packet = PacketSerializer.read(dataInputStream);

                    if (packet instanceof DiscoveryResponsePacket responsePacket) {
                        InetAddress hostAddr = receivePacket.getAddress();
                        hosts.add(new DiscoveredHost(responsePacket.getLobbyName(), hostAddr, responsePacket.getTcpPort()));
                    }
                } catch (SocketTimeoutException e) {
                    seeking = false;
                }
            }
        }
        return hosts;
    }
}
