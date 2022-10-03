package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executor;

public final class ThreadPerTaskExecutor implements Executor
{
    private final ThreadFactory threadFactory;
    
    public ThreadPerTaskExecutor(final ThreadFactory threadFactory) {
        this.threadFactory = ObjectUtil.checkNotNull(threadFactory, "threadFactory");
    }
    
    @Override
    public void execute(final Runnable command) {
        this.threadFactory.newThread(command).start();
    }
}
