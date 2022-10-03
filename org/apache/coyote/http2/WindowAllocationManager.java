package org.apache.coyote.http2;

import org.apache.juli.logging.LogFactory;
import org.apache.coyote.Response;
import org.apache.coyote.ActionCode;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

class WindowAllocationManager
{
    private static final Log log;
    private static final StringManager sm;
    private static final int NONE = 0;
    private static final int STREAM = 1;
    private static final int CONNECTION = 2;
    private final Stream stream;
    private int waitingFor;
    
    WindowAllocationManager(final Stream stream) {
        this.waitingFor = 0;
        this.stream = stream;
    }
    
    void waitForStream(final long timeout) throws InterruptedException {
        if (WindowAllocationManager.log.isDebugEnabled()) {
            WindowAllocationManager.log.debug((Object)WindowAllocationManager.sm.getString("windowAllocationManager.waitFor.stream", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString(), Long.toString(timeout) }));
        }
        this.waitFor(1, timeout);
    }
    
    void waitForConnection(final long timeout) throws InterruptedException {
        if (WindowAllocationManager.log.isDebugEnabled()) {
            WindowAllocationManager.log.debug((Object)WindowAllocationManager.sm.getString("windowAllocationManager.waitFor.connection", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString(), Integer.toString(this.stream.getConnectionAllocationRequested()), Long.toString(timeout) }));
        }
        this.waitFor(2, timeout);
    }
    
    void waitForStreamNonBlocking() {
        if (WindowAllocationManager.log.isDebugEnabled()) {
            WindowAllocationManager.log.debug((Object)WindowAllocationManager.sm.getString("windowAllocationManager.waitForNonBlocking.stream", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString() }));
        }
        this.waitForNonBlocking(1);
    }
    
    void waitForConnectionNonBlocking() {
        if (WindowAllocationManager.log.isDebugEnabled()) {
            WindowAllocationManager.log.debug((Object)WindowAllocationManager.sm.getString("windowAllocationManager.waitForNonBlocking.connection", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString() }));
        }
        this.waitForNonBlocking(2);
    }
    
    void notifyStream() {
        this.notify(1);
    }
    
    void notifyConnection() {
        this.notify(2);
    }
    
    void notifyAny() {
        this.notify(3);
    }
    
    boolean isWaitingForStream() {
        return this.isWaitingFor(1);
    }
    
    boolean isWaitingForConnection() {
        return this.isWaitingFor(2);
    }
    
    private boolean isWaitingFor(final int waitTarget) {
        synchronized (this.stream) {
            return (this.waitingFor & waitTarget) > 0;
        }
    }
    
    private void waitFor(final int waitTarget, final long timeout) throws InterruptedException {
        synchronized (this.stream) {
            if (this.waitingFor != 0) {
                throw new IllegalStateException(WindowAllocationManager.sm.getString("windowAllocationManager.waitFor.ise", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString() }));
            }
            this.waitingFor = waitTarget;
            if (timeout < 0L) {
                this.stream.wait();
            }
            else {
                this.stream.wait(timeout);
            }
        }
    }
    
    private void waitForNonBlocking(final int waitTarget) {
        synchronized (this.stream) {
            if (this.waitingFor == 0) {
                this.waitingFor = waitTarget;
            }
            else if (this.waitingFor != waitTarget) {
                throw new IllegalStateException(WindowAllocationManager.sm.getString("windowAllocationManager.waitFor.ise", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString() }));
            }
        }
    }
    
    private void notify(final int notifyTarget) {
        synchronized (this.stream) {
            if (WindowAllocationManager.log.isDebugEnabled()) {
                WindowAllocationManager.log.debug((Object)WindowAllocationManager.sm.getString("windowAllocationManager.notify", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString(), Integer.toString(this.waitingFor), Integer.toString(notifyTarget) }));
            }
            if ((notifyTarget & this.waitingFor) > 0) {
                this.waitingFor = 0;
                final Response response = this.stream.getCoyoteResponse();
                if (response != null) {
                    if (response.getWriteListener() == null) {
                        if (WindowAllocationManager.log.isDebugEnabled()) {
                            WindowAllocationManager.log.debug((Object)WindowAllocationManager.sm.getString("windowAllocationManager.notified", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString() }));
                        }
                        this.stream.notify();
                    }
                    else {
                        if (WindowAllocationManager.log.isDebugEnabled()) {
                            WindowAllocationManager.log.debug((Object)WindowAllocationManager.sm.getString("windowAllocationManager.dispatched", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString() }));
                        }
                        response.action(ActionCode.DISPATCH_WRITE, null);
                        response.action(ActionCode.DISPATCH_EXECUTE, null);
                    }
                }
            }
        }
    }
    
    static {
        log = LogFactory.getLog((Class)WindowAllocationManager.class);
        sm = StringManager.getManager((Class)WindowAllocationManager.class);
    }
}
