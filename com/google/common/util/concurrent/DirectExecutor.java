package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.Executor;

@ElementTypesAreNonnullByDefault
@GwtCompatible
enum DirectExecutor implements Executor
{
    INSTANCE;
    
    @Override
    public void execute(final Runnable command) {
        command.run();
    }
    
    @Override
    public String toString() {
        return "MoreExecutors.directExecutor()";
    }
}
