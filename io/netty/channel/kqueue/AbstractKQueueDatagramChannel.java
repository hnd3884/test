package io.netty.channel.kqueue;

import java.io.IOException;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelMetadata;

abstract class AbstractKQueueDatagramChannel extends AbstractKQueueChannel
{
    private static final ChannelMetadata METADATA;
    
    AbstractKQueueDatagramChannel(final Channel parent, final BsdSocket fd, final boolean active) {
        super(parent, fd, active);
    }
    
    @Override
    public ChannelMetadata metadata() {
        return AbstractKQueueDatagramChannel.METADATA;
    }
    
    protected abstract boolean doWriteMessage(final Object p0) throws Exception;
    
    @Override
    protected void doWrite(final ChannelOutboundBuffer in) throws Exception {
        int maxMessagesPerWrite = this.maxMessagesPerWrite();
        while (maxMessagesPerWrite > 0) {
            final Object msg = in.current();
            if (msg == null) {
                break;
            }
            try {
                boolean done = false;
                for (int i = this.config().getWriteSpinCount(); i > 0; --i) {
                    if (this.doWriteMessage(msg)) {
                        done = true;
                        break;
                    }
                }
                if (!done) {
                    break;
                }
                in.remove();
                --maxMessagesPerWrite;
            }
            catch (final IOException e) {
                --maxMessagesPerWrite;
                in.remove(e);
            }
        }
        this.writeFilter(!in.isEmpty());
    }
    
    static {
        METADATA = new ChannelMetadata(true);
    }
}
