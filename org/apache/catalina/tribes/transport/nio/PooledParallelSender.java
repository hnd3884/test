package org.apache.catalina.tribes.transport.nio;

import java.io.IOException;
import org.apache.catalina.tribes.transport.AbstractSender;
import org.apache.catalina.tribes.transport.DataSender;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.catalina.tribes.transport.PooledSender;

public class PooledParallelSender extends PooledSender implements PooledParallelSenderMBean
{
    protected static final StringManager sm;
    
    @Override
    public void sendMessage(final Member[] destination, final ChannelMessage message) throws ChannelException {
        if (!this.isConnected()) {
            throw new ChannelException(PooledParallelSender.sm.getString("pooledParallelSender.sender.disconnected"));
        }
        final ParallelNioSender sender = (ParallelNioSender)this.getSender();
        if (sender == null) {
            final ChannelException cx = new ChannelException(PooledParallelSender.sm.getString("pooledParallelSender.unable.retrieveSender.timeout", Long.toString(this.getMaxWait())));
            for (final Member member : destination) {
                cx.addFaultyMember(member, new NullPointerException(PooledParallelSender.sm.getString("pooledParallelSender.unable.retrieveSender")));
            }
            throw cx;
        }
        try {
            if (!sender.isConnected()) {
                sender.connect();
            }
            sender.sendMessage(destination, message);
            sender.keepalive();
        }
        catch (final ChannelException x) {
            sender.disconnect();
            throw x;
        }
        finally {
            this.returnSender(sender);
        }
    }
    
    @Override
    public DataSender getNewDataSender() {
        try {
            final ParallelNioSender sender = new ParallelNioSender();
            AbstractSender.transferProperties(this, sender);
            return sender;
        }
        catch (final IOException x) {
            throw new RuntimeException(PooledParallelSender.sm.getString("pooledParallelSender.unable.open"), x);
        }
    }
    
    static {
        sm = StringManager.getManager(PooledParallelSender.class);
    }
}
