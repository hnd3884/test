package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.Executor;

public final class ImmediateExecutor implements Executor
{
    public static final ImmediateExecutor INSTANCE;
    
    private ImmediateExecutor() {
    }
    
    @Override
    public void execute(final Runnable command) {
        ObjectUtil.checkNotNull(command, "command").run();
    }
    
    static {
        INSTANCE = new ImmediateExecutor();
    }
}
