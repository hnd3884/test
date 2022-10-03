package sun.nio.ch.sctp;

import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.NotificationHandler;
import java.nio.ByteBuffer;
import com.sun.nio.sctp.SctpSocketOption;
import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketAddress;
import com.sun.nio.sctp.Association;
import java.util.Set;
import java.nio.channels.spi.SelectorProvider;
import com.sun.nio.sctp.SctpMultiChannel;

public class SctpMultiChannelImpl extends SctpMultiChannel
{
    private static final String message = "SCTP not supported on this platform";
    
    public SctpMultiChannelImpl(final SelectorProvider selectorProvider) {
        super(selectorProvider);
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public Set<Association> associations() {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpMultiChannel bind(final SocketAddress socketAddress, final int n) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpMultiChannel bindAddress(final InetAddress inetAddress) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpMultiChannel unbindAddress(final InetAddress inetAddress) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public Set<SocketAddress> getAllLocalAddresses() throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public Set<SocketAddress> getRemoteAddresses(final Association association) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpMultiChannel shutdown(final Association association) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public <T> T getOption(final SctpSocketOption<T> sctpSocketOption, final Association association) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public <T> SctpMultiChannel setOption(final SctpSocketOption<T> sctpSocketOption, final T t, final Association association) throws IOException {
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
    public SctpChannel branch(final Association association) throws IOException {
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
