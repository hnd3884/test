package io.netty.channel;

import java.util.Queue;

public interface EventLoopTaskQueueFactory
{
    Queue<Runnable> newTaskQueue(final int p0);
}
