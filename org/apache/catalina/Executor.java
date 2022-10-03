package org.apache.catalina;

import java.util.concurrent.TimeUnit;

public interface Executor extends java.util.concurrent.Executor, Lifecycle
{
    String getName();
    
    @Deprecated
    void execute(final Runnable p0, final long p1, final TimeUnit p2);
}
