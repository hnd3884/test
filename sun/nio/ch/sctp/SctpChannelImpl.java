package sun.nio.ch.sctp;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.NotificationHandler;
import java.nio.ByteBuffer;
import com.sun.nio.sctp.SctpSocketOption;
import java.util.Set;
import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketAddress;
import com.sun.nio.sctp.Association;
import java.nio.channels.spi.SelectorProvider;
import com.sun.nio.sctp.SctpChannel;

public class SctpChannelImpl extends SctpChannel
{
    private static final String message = "SCTP not supported on this platform";
    
    public SctpChannelImpl(final SelectorProvider selectorProvider) {
        super(selectorProvider);
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public Association association() {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpChannel bind(final SocketAddress socketAddress) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpChannel bindAddress(final InetAddress inetAddress) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpChannel unbindAddress(final InetAddress inetAddress) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public boolean connect(final SocketAddress socketAddress) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public boolean connect(final SocketAddress socketAddress, final int n, final int n2) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public boolean isConnectionPending() {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public boolean finishConnect() throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public Set<SocketAddress> getAllLocalAddresses() throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public Set<SocketAddress> getRemoteAddresses() throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpChannel shutdown() throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public <T> T getOption(final SctpSocketOption<T> sctpSocketOption) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public <T> SctpChannel setOption(final SctpSocketOption<T> sctpSocketOption, final T t) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public Set<SctpSocketOption<?>> supportedOptions() {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public <T> MessageInfo receive(final ByteBuffer byteBuffer, final T t, final NotificationHandler<T> notificationHandler) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public int send(final ByteBuffer byteBuffer, final MessageInfo messageInfo) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    protected void implConfigureBlocking(final boolean b) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    public void implCloseSelectableChannel() throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
}
