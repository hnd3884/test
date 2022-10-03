package io.netty.util.concurrent;

public interface EventExecutorChooserFactory
{
    EventExecutorChooser newChooser(final EventExecutor[] p0);
    
    public interface EventExecutorChooser
    {
        EventExecutor next();
    }
}
