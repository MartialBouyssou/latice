package latice.network;

import latice.network.packet.Packet;

public interface PacketSender {

    /**
     * Sends the given packet
     *
     * @param packet The packet to send
     */
    void sendPacket(Packet packet);
}
