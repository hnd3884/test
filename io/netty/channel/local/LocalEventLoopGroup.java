package io.netty.channel.local;

import java.util.concurrent.ThreadFactory;
import io.netty.channel.DefaultEventLoopGroup;

@Deprecated
public class LocalEventLoopGroup extends DefaultEventLoopGroup
{
    public LocalEventLoopGroup() {
    }
    
    public LocalEventLoopGroup(final int nThreads) {
        super(nThreads);
    }
    
    public LocalEventLoopGroup(final ThreadFactory threadFactory) {
        super(0, threadFactory);
    }
    
    public LocalEventLoopGroup(final int nThreads, final ThreadFactory threadFactory) {
        super(nThreads, threadFactory);
    }
}
