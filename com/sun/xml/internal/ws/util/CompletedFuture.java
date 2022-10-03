package com.sun.xml.internal.ws.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CompletedFuture<T> implements Future<T>
{
    private final T v;
    private final Throwable re;
    
    public CompletedFuture(final T v, final Throwable re) {
        this.v = v;
        this.re = re;
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return false;
    }
    
    @Override
    public boolean isCancelled() {
        return false;
    }
    
    @Override
    public boolean isDone() {
        return true;
    }
    
    @Override
    public T get() throws ExecutionException {
        if (this.re != null) {
            throw new ExecutionException(this.re);
        }
        return this.v;
    }
    
    @Override
    public T get(final long timeout, final TimeUnit unit) throws ExecutionException {
        return this.get();
    }
}
