package io.netty.channel;

import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseNotifier;

@Deprecated
public final class ChannelPromiseNotifier extends PromiseNotifier<Void, ChannelFuture> implements ChannelFutureListener
{
    public ChannelPromiseNotifier(final ChannelPromise... promises) {
        super((Promise[])promises);
    }
    
    public ChannelPromiseNotifier(final boolean logNotifyFailure, final ChannelPromise... promises) {
        super(logNotifyFailure, (Promise[])promises);
    }
}
