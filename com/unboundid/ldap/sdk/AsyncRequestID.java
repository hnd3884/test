package com.unboundid.ldap.sdk;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ArrayBlockingQueue;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.util.concurrent.Future;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AsyncRequestID implements Serializable, Future<LDAPResult>
{
    private static final long serialVersionUID = 8244005138437962030L;
    private final ArrayBlockingQueue<LDAPResult> resultQueue;
    private final AtomicBoolean cancelRequested;
    private final AtomicReference<LDAPResult> result;
    private final int messageID;
    private final LDAPConnection connection;
    private volatile AsyncTimeoutTimerTask timerTask;
    
    AsyncRequestID(final int messageID, final LDAPConnection connection) {
        this.messageID = messageID;
        this.connection = connection;
        this.resultQueue = new ArrayBlockingQueue<LDAPResult>(1);
        this.cancelRequested = new AtomicBoolean(false);
        this.result = new AtomicReference<LDAPResult>();
        this.timerTask = null;
    }
    
    public int getMessageID() {
        return this.messageID;
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        if (this.isDone()) {
            return false;
        }
        try {
            this.cancelRequested.set(true);
            this.result.compareAndSet(null, new LDAPResult(this.messageID, ResultCode.USER_CANCELED, LDAPMessages.INFO_ASYNC_REQUEST_USER_CANCELED.get(), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS));
            this.connection.abandon(this);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        return true;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelRequested.get();
    }
    
    @Override
    public boolean isDone() {
        if (this.cancelRequested.get()) {
            return true;
        }
        if (this.result.get() != null) {
            return true;
        }
        final LDAPResult newResult = this.resultQueue.poll();
        if (newResult != null) {
            this.result.set(newResult);
            return true;
        }
        return false;
    }
    
    @Override
    public LDAPResult get() throws InterruptedException {
        final long maxWaitTime = this.connection.getConnectionOptions().getResponseTimeoutMillis();
        try {
            return this.get(maxWaitTime, TimeUnit.MILLISECONDS);
        }
        catch (final TimeoutException te) {
            Debug.debugException(te);
            return new LDAPResult(this.messageID, ResultCode.TIMEOUT, te.getMessage(), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
        }
    }
    
    @Override
    public LDAPResult get(final long timeout, final TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        final LDAPResult newResult = this.resultQueue.poll();
        if (newResult != null) {
            this.result.set(newResult);
            return newResult;
        }
        final LDAPResult previousResult = this.result.get();
        if (previousResult != null) {
            return previousResult;
        }
        final LDAPResult resultAfterWaiting = this.resultQueue.poll(timeout, timeUnit);
        if (resultAfterWaiting == null) {
            final long timeoutMillis = timeUnit.toMillis(timeout);
            throw new TimeoutException(LDAPMessages.WARN_ASYNC_REQUEST_GET_TIMEOUT.get(timeoutMillis));
        }
        this.result.set(resultAfterWaiting);
        return resultAfterWaiting;
    }
    
    void setTimerTask(final AsyncTimeoutTimerTask timerTask) {
        this.timerTask = timerTask;
    }
    
    void setResult(final LDAPResult result) {
        this.resultQueue.offer(result);
        final AsyncTimeoutTimerTask t = this.timerTask;
        if (t != null) {
            t.cancel();
            this.connection.getTimer().purge();
            this.timerTask = null;
        }
    }
    
    @Override
    public int hashCode() {
        return this.messageID;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (o == this || (o instanceof AsyncRequestID && ((AsyncRequestID)o).messageID == this.messageID));
    }
    
    @Override
    public String toString() {
        return "AsyncRequestID(messageID=" + this.messageID + ')';
    }
}
