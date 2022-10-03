package sun.nio.ch;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.nio.channels.CompletionHandler;
import java.nio.channels.AsynchronousChannel;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

final class PendingFuture<V, A> implements Future<V>
{
    private static final CancellationException CANCELLED;
    private final AsynchronousChannel channel;
    private final CompletionHandler<V, ? super A> handler;
    private final A attachment;
    private volatile boolean haveResult;
    private volatile V result;
    private volatile Throwable exc;
    private CountDownLatch latch;
    private Future<?> timeoutTask;
    private volatile Object context;
    
    PendingFuture(final AsynchronousChannel channel, final CompletionHandler<V, ? super A> handler, final A attachment, final Object context) {
        this.channel = channel;
        this.handler = handler;
        this.attachment = attachment;
        this.context = context;
    }
    
    PendingFuture(final AsynchronousChannel channel, final CompletionHandler<V, ? super A> handler, final A attachment) {
        this.channel = channel;
        this.handler = handler;
        this.attachment = attachment;
    }
    
    PendingFuture(final AsynchronousChannel asynchronousChannel) {
        this(asynchronousChannel, null, null);
    }
    
    PendingFuture(final AsynchronousChannel asynchronousChannel, final Object o) {
        this(asynchronousChannel, null, null, o);
    }
    
    AsynchronousChannel channel() {
        return this.channel;
    }
    
    CompletionHandler<V, ? super A> handler() {
        return this.handler;
    }
    
    A attachment() {
        return this.attachment;
    }
    
    void setContext(final Object context) {
        this.context = context;
    }
    
    Object getContext() {
        return this.context;
    }
    
    void setTimeoutTask(final Future<?> timeoutTask) {
        synchronized (this) {
            if (this.haveResult) {
                timeoutTask.cancel(false);
            }
            else {
                this.timeoutTask = timeoutTask;
            }
        }
    }
    
    private boolean prepareForWait() {
        synchronized (this) {
            if (this.haveResult) {
                return false;
            }
            if (this.latch == null) {
                this.latch = new CountDownLatch(1);
            }
            return true;
        }
    }
    
    void setResult(final V result) {
        synchronized (this) {
            if (this.haveResult) {
                return;
            }
            this.result = result;
            this.haveResult = true;
            if (this.timeoutTask != null) {
                this.timeoutTask.cancel(false);
            }
            if (this.latch != null) {
                this.latch.countDown();
            }
        }
    }
    
    void setFailure(Throwable exc) {
        if (!(exc instanceof IOException) && !(exc instanceof SecurityException)) {
            exc = new IOException(exc);
        }
        synchronized (this) {
            if (this.haveResult) {
                return;
            }
            this.exc = exc;
            this.haveResult = true;
            if (this.timeoutTask != null) {
                this.timeoutTask.cancel(false);
            }
            if (this.latch != null) {
                this.latch.countDown();
            }
        }
    }
    
    void setResult(final V result, final Throwable failure) {
        if (failure == null) {
            this.setResult(result);
        }
        else {
            this.setFailure(failure);
        }
    }
    
    @Override
    public V get() throws ExecutionException, InterruptedException {
        if (!this.haveResult && this.prepareForWait()) {
            this.latch.await();
        }
        if (this.exc == null) {
            return this.result;
        }
        if (this.exc == PendingFuture.CANCELLED) {
            throw new CancellationException();
        }
        throw new ExecutionException(this.exc);
    }
    
    @Override
    public V get(final long n, final TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        if (!this.haveResult && this.prepareForWait() && !this.latch.await(n, timeUnit)) {
            throw new TimeoutException();
        }
        if (this.exc == null) {
            return this.result;
        }
        if (this.exc == PendingFuture.CANCELLED) {
            throw new CancellationException();
        }
        throw new ExecutionException(this.exc);
    }
    
    Throwable exception() {
        return (this.exc != PendingFuture.CANCELLED) ? this.exc : null;
    }
    
    V value() {
        return this.result;
    }
    
    @Override
    public boolean isCancelled() {
        return this.exc == PendingFuture.CANCELLED;
    }
    
    @Override
    public boolean isDone() {
        return this.haveResult;
    }
    
    @Override
    public boolean cancel(final boolean b) {
        synchronized (this) {
            if (this.haveResult) {
                return false;
            }
            if (this.channel() instanceof Cancellable) {
                ((Cancellable)this.channel()).onCancel(this);
            }
            this.exc = PendingFuture.CANCELLED;
            this.haveResult = true;
            if (this.timeoutTask != null) {
                this.timeoutTask.cancel(false);
            }
        }
        if (b) {
            try {
                this.channel().close();
            }
            catch (final IOException ex) {}
        }
        if (this.latch != null) {
            this.latch.countDown();
        }
        return true;
    }
    
    static {
        CANCELLED = new CancellationException();
    }
}
