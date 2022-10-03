package org.glassfish.jersey.server.internal;

import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class JerseyRequestTimeoutHandler
{
    private static final Logger LOGGER;
    private ScheduledFuture<?> timeoutTask;
    private ContainerResponseWriter.TimeoutHandler timeoutHandler;
    private boolean suspended;
    private final Object runtimeLock;
    private final ContainerResponseWriter containerResponseWriter;
    private final ScheduledExecutorService executor;
    
    public JerseyRequestTimeoutHandler(final ContainerResponseWriter containerResponseWriter, final ScheduledExecutorService timeoutTaskExecutor) {
        this.timeoutTask = null;
        this.timeoutHandler = null;
        this.suspended = false;
        this.runtimeLock = new Object();
        this.containerResponseWriter = containerResponseWriter;
        this.executor = timeoutTaskExecutor;
    }
    
    public boolean suspend(final long timeOut, final TimeUnit unit, final ContainerResponseWriter.TimeoutHandler handler) {
        synchronized (this.runtimeLock) {
            if (this.suspended) {
                return false;
            }
            this.suspended = true;
            this.timeoutHandler = handler;
            this.containerResponseWriter.setSuspendTimeout(timeOut, unit);
            return true;
        }
    }
    
    public void setSuspendTimeout(final long timeOut, final TimeUnit unit) throws IllegalStateException {
        synchronized (this.runtimeLock) {
            if (!this.suspended) {
                throw new IllegalStateException(LocalizationMessages.SUSPEND_NOT_SUSPENDED());
            }
            this.close(true);
            if (timeOut <= 0L) {
                return;
            }
            try {
                this.timeoutTask = this.executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            synchronized (JerseyRequestTimeoutHandler.this.runtimeLock) {
                                JerseyRequestTimeoutHandler.this.timeoutHandler.onTimeout(JerseyRequestTimeoutHandler.this.containerResponseWriter);
                            }
                        }
                        catch (final Throwable throwable) {
                            JerseyRequestTimeoutHandler.LOGGER.log(Level.WARNING, LocalizationMessages.SUSPEND_HANDLER_EXECUTION_FAILED(), throwable);
                        }
                    }
                }, timeOut, unit);
            }
            catch (final IllegalStateException ex) {
                JerseyRequestTimeoutHandler.LOGGER.log(Level.WARNING, LocalizationMessages.SUSPEND_SCHEDULING_ERROR(), ex);
            }
        }
    }
    
    public void close() {
        this.close(false);
    }
    
    private synchronized void close(final boolean interruptIfRunning) {
        if (this.timeoutTask != null) {
            this.timeoutTask.cancel(interruptIfRunning);
            this.timeoutTask = null;
        }
    }
    
    static {
        LOGGER = Logger.getLogger(JerseyRequestTimeoutHandler.class.getName());
    }
}
