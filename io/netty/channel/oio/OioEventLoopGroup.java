package io.netty.channel.oio;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import io.netty.channel.ThreadPerChannelEventLoopGroup;

@Deprecated
public class OioEventLoopGroup extends ThreadPerChannelEventLoopGroup
{
    public OioEventLoopGroup() {
        this(0);
    }
    
    public OioEventLoopGroup(final int maxChannels) {
        this(maxChannels, (ThreadFactory)null);
    }
    
    public OioEventLoopGroup(final int maxChannels, final Executor executor) {
        super(maxChannels, executor, new Object[0]);
    }
    
    public OioEventLoopGroup(final int maxChannels, final ThreadFactory threadFactory) {
        super(maxChannels, threadFactory, new Object[0]);
    }
}
