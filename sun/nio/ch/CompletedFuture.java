package sun.nio.ch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.util.concurrent.Future;

final class CompletedFuture<V> implements Future<V>
{
    private final V result;
    private final Throwable exc;
    
    private CompletedFuture(final V result, final Throwable exc) {
        this.result = result;
        this.exc = exc;
    }
    
    static <V> CompletedFuture<V> withResult(final V v) {
        return new CompletedFuture<V>(v, null);
    }
    
    static <V> CompletedFuture<V> withFailure(Throwable t) {
        if (!(t instanceof IOException) && !(t instanceof SecurityException)) {
            t = new IOException(t);
        }
        return new CompletedFuture<V>(null, t);
    }
    
    static <V> CompletedFuture<V> withResult(final V v, final Throwable t) {
        if (t == null) {
            return withResult(v);
        }
        return withFailure(t);
    }
    
    @Override
    public V get() throws ExecutionException {
        if (this.exc != null) {
            throw new ExecutionException(this.exc);
        }
        return this.result;
    }
    
    @Override
    public V get(final long n, final TimeUnit timeUnit) throws ExecutionException {
        if (timeUnit == null) {
            throw new NullPointerException();
        }
        if (this.exc != null) {
            throw new ExecutionException(this.exc);
        }
        return this.result;
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
    public boolean cancel(final boolean b) {
        return false;
    }
}
