package org.glassfish.jersey.client;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

public interface ClientExecutor
{
     <T> Future<T> submit(final Callable<T> p0);
    
    Future<?> submit(final Runnable p0);
    
     <T> Future<T> submit(final Runnable p0, final T p1);
    
     <T> ScheduledFuture<T> schedule(final Callable<T> p0, final long p1, final TimeUnit p2);
    
    ScheduledFuture<?> schedule(final Runnable p0, final long p1, final TimeUnit p2);
}
