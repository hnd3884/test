package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;
import java.nio.channels.spi.AbstractInterruptibleChannel;

public abstract class SelectableChannel extends AbstractInterruptibleChannel implements Channel
{
    protected SelectableChannel() {
    }
    
    public abstract SelectorProvider provider();
    
    public abstract int validOps();
    
    public abstract boolean isRegistered();
    
    public abstract SelectionKey keyFor(final Selector p0);
    
    public abstract SelectionKey register(final Selector p0, final int p1, final Object p2) throws ClosedChannelException;
    
    public final SelectionKey register(final Selector selector, final int n) throws ClosedChannelException {
        return this.register(selector, n, null);
    }
    
    public abstract SelectableChannel configureBlocking(final boolean p0) throws IOException;
    
    public abstract boolean isBlocking();
    
    public abstract Object blockingLock();
}
