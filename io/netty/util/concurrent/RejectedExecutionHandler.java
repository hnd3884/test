package io.netty.util.concurrent;

public interface RejectedExecutionHandler
{
    void rejected(final Runnable p0, final SingleThreadEventExecutor p1);
}
