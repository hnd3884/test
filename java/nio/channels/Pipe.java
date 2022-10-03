package java.nio.channels;

import java.nio.channels.spi.AbstractSelectableChannel;
import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;

public abstract class Pipe
{
    protected Pipe() {
    }
    
    public abstract SourceChannel source();
    
    public abstract SinkChannel sink();
    
    public static Pipe open() throws IOException {
        return SelectorProvider.provider().openPipe();
    }
    
    public abstract static class SourceChannel extends AbstractSelectableChannel implements ReadableByteChannel, ScatteringByteChannel
    {
        protected SourceChannel(final SelectorProvider selectorProvider) {
            super(selectorProvider);
        }
        
        @Override
        public final int validOps() {
            return 1;
        }
    }
    
    public abstract static class SinkChannel extends AbstractSelectableChannel implements WritableByteChannel, GatheringByteChannel
    {
        protected SinkChannel(final SelectorProvider selectorProvider) {
            super(selectorProvider);
        }
        
        @Override
        public final int validOps() {
            return 4;
        }
    }
}
