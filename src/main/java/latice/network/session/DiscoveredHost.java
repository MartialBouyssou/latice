package latice.network.session;

import java.net.InetAddress;

public class DiscoveredHost {
    private final String hostName;
    private final InetAddress address;
    private final int tcpPort;

    public DiscoveredHost(String hostName, InetAddress address, int tcpPort) {
        this.hostName = hostName;
        this.address = address;
        this.tcpPort = tcpPort;
    }

    public String getHostName() { return hostName; }
    public InetAddress getAddress() { return address; }
    public int getTcpPort() { return tcpPort; }
}
