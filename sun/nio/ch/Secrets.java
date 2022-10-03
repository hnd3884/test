package sun.nio.ch;

import java.nio.channels.ServerSocketChannel;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.io.FileDescriptor;
import java.nio.channels.spi.SelectorProvider;

public final class Secrets
{
    private Secrets() {
    }
    
    private static SelectorProvider provider() {
        final SelectorProvider provider = SelectorProvider.provider();
        if (!(provider instanceof SelectorProviderImpl)) {
            throw new UnsupportedOperationException();
        }
        return provider;
    }
    
    public static SocketChannel newSocketChannel(final FileDescriptor fileDescriptor) {
        try {
            return new SocketChannelImpl(provider(), fileDescriptor, false);
        }
        catch (final IOException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    public static ServerSocketChannel newServerSocketChannel(final FileDescriptor fileDescriptor) {
        try {
            return new ServerSocketChannelImpl(provider(), fileDescriptor, false);
        }
        catch (final IOException ex) {
            throw new AssertionError((Object)ex);
        }
    }
}
