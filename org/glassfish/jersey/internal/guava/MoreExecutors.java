package org.glassfish.jersey.internal.guava;

import java.util.concurrent.Executor;

public final class MoreExecutors
{
    private MoreExecutors() {
    }
    
    public static Executor directExecutor() {
        return DirectExecutor.INSTANCE;
    }
    
    private enum DirectExecutor implements Executor
    {
        INSTANCE;
        
        @Override
        public void execute(final Runnable command) {
            command.run();
        }
    }
}
