package sun.management.jdp;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardSocketOptions;
import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;
import java.net.Inet6Address;
import java.nio.channels.DatagramChannel;
import java.net.InetAddress;

public final class JdpBroadcaster
{
    private final InetAddress addr;
    private final int port;
    private final DatagramChannel channel;
    
    public JdpBroadcaster(final InetAddress addr, final InetAddress inetAddress, final int port, final int n) throws IOException, JdpException {
        this.addr = addr;
        this.port = port;
        (this.channel = DatagramChannel.open((addr instanceof Inet6Address) ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET)).setOption(StandardSocketOptions.SO_REUSEADDR, true);
        this.channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, n);
        if (inetAddress != null) {
            final NetworkInterface byInetAddress = NetworkInterface.getByInetAddress(inetAddress);
            try {
                this.channel.bind(new InetSocketAddress(inetAddress, 0));
            }
            catch (final UnsupportedAddressTypeException ex) {
                throw new JdpException("Unable to bind to source address");
            }
            this.channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, byInetAddress);
        }
    }
    
    public JdpBroadcaster(final InetAddress inetAddress, final int n, final int n2) throws IOException, JdpException {
        this(inetAddress, null, n, n2);
    }
    
    public void sendPacket(final JdpPacket jdpPacket) throws IOException {
        this.channel.send(ByteBuffer.wrap(jdpPacket.getPacketData()), new InetSocketAddress(this.addr, this.port));
    }
    
    public void shutdown() throws IOException {
        this.channel.close();
    }
}
