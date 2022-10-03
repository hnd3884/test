package sun.nio.ch;

import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.Pipe;
import java.net.ProtocolFamily;
import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class SelectorProviderImpl extends SelectorProvider
{
    @Override
    public DatagramChannel openDatagramChannel() throws IOException {
        return new DatagramChannelImpl(this);
    }
    
    @Override
    public DatagramChannel openDatagramChannel(final ProtocolFamily protocolFamily) throws IOException {
        return new DatagramChannelImpl(this, protocolFamily);
    }
    
    @Override
    public Pipe openPipe() throws IOException {
        return new PipeImpl(this);
    }
    
    @Override
    public abstract AbstractSelector openSelector() throws IOException;
    
    @Override
    public ServerSocketChannel openServerSocketChannel() throws IOException {
        return new ServerSocketChannelImpl(this);
    }
    
    @Override
    public SocketChannel openSocketChannel() throws IOException {
        return new SocketChannelImpl(this);
    }
}
