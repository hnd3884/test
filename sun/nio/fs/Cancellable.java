package sun.nio.fs;

import java.util.concurrent.ExecutionException;
import sun.misc.Unsafe;

abstract class Cancellable implements Runnable
{
    private static final Unsafe unsafe;
    private final long pollingAddress;
    private final Object lock;
    private boolean completed;
    private Throwable exception;
    
    protected Cancellable() {
        this.lock = new Object();
        this.pollingAddress = Cancellable.unsafe.allocateMemory(4L);
        Cancellable.unsafe.putIntVolatile(null, this.pollingAddress, 0);
    }
    
    protected long addressToPollForCancel() {
        return this.pollingAddress;
    }
    
    protected int cancelValue() {
        return Integer.MAX_VALUE;
    }
    
    final void cancel() {
        synchronized (this.lock) {
            if (!this.completed) {
                Cancellable.unsafe.putIntVolatile(null, this.pollingAddress, this.cancelValue());
            }
        }
    }
    
    private Throwable exception() {
        synchronized (this.lock) {
            return this.exception;
        }
    }
    
    @Override
    public final void run() {
        try {
            this.implRun();
        }
        catch (final Throwable exception) {
            synchronized (this.lock) {
                this.exception = exception;
            }
            synchronized (this.lock) {
                this.completed = true;
                Cancellable.unsafe.freeMemory(this.pollingAddress);
            }
        }
        finally {
            synchronized (this.lock) {
                this.completed = true;
                Cancellable.unsafe.freeMemory(this.pollingAddress);
            }
        }
    }
    
    abstract void implRun() throws Throwable;
    
    static void runInterruptibly(final Cancellable cancellable) throws ExecutionException {
        final Thread thread = new Thread(cancellable);
        thread.start();
        boolean b = false;
        while (thread.isAlive()) {
            try {
                thread.join();
            }
            catch (final InterruptedException ex) {
                b = true;
                cancellable.cancel();
            }
        }
        if (b) {
            Thread.currentThread().interrupt();
        }
        final Throwable exception = cancellable.exception();
        if (exception != null) {
            throw new ExecutionException(exception);
        }
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
}
