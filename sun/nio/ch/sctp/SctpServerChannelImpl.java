package sun.nio.ch.sctp;

import com.sun.nio.sctp.SctpSocketOption;
import java.util.Set;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.io.IOException;
import com.sun.nio.sctp.SctpChannel;
import java.nio.channels.spi.SelectorProvider;
import com.sun.nio.sctp.SctpServerChannel;

public class SctpServerChannelImpl extends SctpServerChannel
{
    private static final String message = "SCTP not supported on this platform";
    
    public SctpServerChannelImpl(final SelectorProvider selectorProvider) {
        super(selectorProvider);
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpChannel accept() throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpServerChannel bind(final SocketAddress socketAddress, final int n) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpServerChannel bindAddress(final InetAddress inetAddress) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public SctpServerChannel unbindAddress(final InetAddress inetAddress) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public Set<SocketAddress> getAllLocalAddresses() throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public <T> T getOption(final SctpSocketOption<T> sctpSocketOption) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public <T> SctpServerChannel setOption(final SctpSocketOption<T> sctpSocketOption, final T t) throws IOException {
        throw new UnsupportedOperationException("SCTP not supported on this platform");
    }
    
    @Override
    public Set<SctpSocketOption<?>> supportedOptions() {
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
