package io.netty.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

class PromiseTask<V> extends DefaultPromise<V> implements RunnableFuture<V>
{
    private static final Runnable COMPLETED;
    private static final Runnable CANCELLED;
    private static final Runnable FAILED;
    private Object task;
    
    PromiseTask(final EventExecutor executor, final Runnable runnable, final V result) {
        super(executor);
        this.task = ((result == null) ? runnable : new RunnableAdapter(runnable, result));
    }
    
    PromiseTask(final EventExecutor executor, final Runnable runnable) {
        super(executor);
        this.task = runnable;
    }
    
    PromiseTask(final EventExecutor executor, final Callable<V> callable) {
        super(executor);
        this.task = callable;
    }
    
    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }
    
    @Override
    public final boolean equals(final Object obj) {
        return this == obj;
    }
    
    V runTask() throws Throwable {
        final Object task = this.task;
        if (task instanceof Callable) {
            return ((Callable)task).call();
        }
        ((Runnable)task).run();
        return null;
    }
    
    @Override
    public void run() {
        try {
            if (this.setUncancellableInternal()) {
                final V result = this.runTask();
                this.setSuccessInternal(result);
            }
        }
        catch (final Throwable e) {
            this.setFailureInternal(e);
        }
    }
    
    private boolean clearTaskAfterCompletion(final boolean done, final Runnable result) {
        if (done) {
            this.task = result;
        }
        return done;
    }
    
    @Override
    public final Promise<V> setFailure(final Throwable cause) {
        throw new IllegalStateException();
    }
    
    protected final Promise<V> setFailureInternal(final Throwable cause) {
        super.setFailure(cause);
        this.clearTaskAfterCompletion(true, PromiseTask.FAILED);
        return this;
    }
    
    @Override
    public final boolean tryFailure(final Throwable cause) {
        return false;
    }
    
    protected final boolean tryFailureInternal(final Throwable cause) {
        return this.clearTaskAfterCompletion(super.tryFailure(cause), PromiseTask.FAILED);
    }
    
    @Override
    public final Promise<V> setSuccess(final V result) {
        throw new IllegalStateException();
    }
    
    protected final Promise<V> setSuccessInternal(final V result) {
        super.setSuccess(result);
        this.clearTaskAfterCompletion(true, PromiseTask.COMPLETED);
        return this;
    }
    
    @Override
    public final boolean trySuccess(final V result) {
        return false;
    }
    
    protected final boolean trySuccessInternal(final V result) {
        return this.clearTaskAfterCompletion(super.trySuccess(result), PromiseTask.COMPLETED);
    }
    
    @Override
    public final boolean setUncancellable() {
        throw new IllegalStateException();
    }
    
    protected final boolean setUncancellableInternal() {
        return super.setUncancellable();
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return this.clearTaskAfterCompletion(super.cancel(mayInterruptIfRunning), PromiseTask.CANCELLED);
    }
    
    @Override
    protected StringBuilder toStringBuilder() {
        final StringBuilder buf = super.toStringBuilder();
        buf.setCharAt(buf.length() - 1, ',');
        return buf.append(" task: ").append(this.task).append(')');
    }
    
    static {
        COMPLETED = new SentinelRunnable("COMPLETED");
        CANCELLED = new SentinelRunnable("CANCELLED");
        FAILED = new SentinelRunnable("FAILED");
    }
    
    private static final class RunnableAdapter<T> implements Callable<T>
    {
        final Runnable task;
        final T result;
        
        RunnableAdapter(final Runnable task, final T result) {
            this.task = task;
            this.result = result;
        }
        
        @Override
        public T call() {
            this.task.run();
            return this.result;
        }
        
        @Override
        public String toString() {
            return "Callable(task: " + this.task + ", result: " + this.result + ')';
        }
    }
    
    private static class SentinelRunnable implements Runnable
    {
        private final String name;
        
        SentinelRunnable(final String name) {
            this.name = name;
        }
        
        @Override
        public void run() {
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
