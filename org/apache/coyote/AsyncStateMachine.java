package org.apache.coyote;

import org.apache.juli.logging.LogFactory;
import java.security.PrivilegedAction;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import java.security.AccessController;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.net.AbstractEndpoint;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public class AsyncStateMachine
{
    private static final Log log;
    private static final StringManager sm;
    private volatile AsyncState state;
    private volatile long lastAsyncStart;
    private final AtomicLong generation;
    private AsyncContextCallback asyncCtxt;
    private final AbstractProcessor processor;
    
    public AsyncStateMachine(final AbstractProcessor processor) {
        this.state = AsyncState.DISPATCHED;
        this.lastAsyncStart = 0L;
        this.generation = new AtomicLong(0L);
        this.asyncCtxt = null;
        this.processor = processor;
    }
    
    public boolean isAsync() {
        return this.state.isAsync();
    }
    
    public boolean isAsyncDispatching() {
        return this.state.isDispatching();
    }
    
    public boolean isAsyncStarted() {
        return this.state.isStarted();
    }
    
    public boolean isAsyncTimingOut() {
        return this.state == AsyncState.TIMING_OUT;
    }
    
    public boolean isAsyncError() {
        return this.state == AsyncState.ERROR;
    }
    
    public boolean isCompleting() {
        return this.state.isCompleting();
    }
    
    public long getLastAsyncStart() {
        return this.lastAsyncStart;
    }
    
    long getCurrentGeneration() {
        return this.generation.get();
    }
    
    public synchronized void asyncStart(final AsyncContextCallback asyncCtxt) {
        if (this.state == AsyncState.DISPATCHED) {
            this.generation.incrementAndGet();
            this.updateState(AsyncState.STARTING);
            this.asyncCtxt = asyncCtxt;
            this.lastAsyncStart = System.currentTimeMillis();
            return;
        }
        throw new IllegalStateException(AsyncStateMachine.sm.getString("asyncStateMachine.invalidAsyncState", new Object[] { "asyncStart()", this.state }));
    }
    
    public synchronized void asyncOperation() {
        if (this.state == AsyncState.STARTED) {
            this.updateState(AsyncState.READ_WRITE_OP);
            return;
        }
        throw new IllegalStateException(AsyncStateMachine.sm.getString("asyncStateMachine.invalidAsyncState", new Object[] { "asyncOperation()", this.state }));
    }
    
    public synchronized AbstractEndpoint.Handler.SocketState asyncPostProcess() {
        if (this.state == AsyncState.COMPLETE_PENDING) {
            this.clearNonBlockingListeners();
            this.updateState(AsyncState.COMPLETING);
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.DISPATCH_PENDING) {
            this.clearNonBlockingListeners();
            this.updateState(AsyncState.DISPATCHING);
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.STARTING || this.state == AsyncState.READ_WRITE_OP) {
            this.updateState(AsyncState.STARTED);
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        if (this.state == AsyncState.MUST_COMPLETE || this.state == AsyncState.COMPLETING) {
            this.asyncCtxt.fireOnComplete();
            this.updateState(AsyncState.DISPATCHED);
            this.asyncCtxt.decrementInProgressAsyncCount();
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.MUST_DISPATCH) {
            this.updateState(AsyncState.DISPATCHING);
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.DISPATCHING) {
            this.updateState(AsyncState.DISPATCHED);
            this.asyncCtxt.decrementInProgressAsyncCount();
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.STARTED) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        throw new IllegalStateException(AsyncStateMachine.sm.getString("asyncStateMachine.invalidAsyncState", new Object[] { "asyncPostProcess()", this.state }));
    }
    
    public synchronized boolean asyncComplete() {
        if (!ContainerThreadMarker.isContainerThread() && (this.state == AsyncState.STARTING || this.state == AsyncState.READ_WRITE_OP)) {
            this.updateState(AsyncState.COMPLETE_PENDING);
            return false;
        }
        this.clearNonBlockingListeners();
        boolean triggerDispatch = false;
        if (this.state == AsyncState.STARTING || this.state == AsyncState.MUST_ERROR) {
            this.updateState(AsyncState.MUST_COMPLETE);
        }
        else if (this.state == AsyncState.STARTED) {
            this.updateState(AsyncState.COMPLETING);
            triggerDispatch = true;
        }
        else {
            if (this.state != AsyncState.READ_WRITE_OP && this.state != AsyncState.TIMING_OUT && this.state != AsyncState.ERROR) {
                throw new IllegalStateException(AsyncStateMachine.sm.getString("asyncStateMachine.invalidAsyncState", new Object[] { "asyncComplete()", this.state }));
            }
            this.updateState(AsyncState.COMPLETING);
        }
        return triggerDispatch;
    }
    
    public synchronized boolean asyncTimeout() {
        if (this.state == AsyncState.STARTED) {
            this.updateState(AsyncState.TIMING_OUT);
            return true;
        }
        if (this.state == AsyncState.COMPLETING || this.state == AsyncState.DISPATCHING || this.state == AsyncState.DISPATCHED) {
            return false;
        }
        throw new IllegalStateException(AsyncStateMachine.sm.getString("asyncStateMachine.invalidAsyncState", new Object[] { "asyncTimeout()", this.state }));
    }
    
    public synchronized boolean asyncDispatch() {
        if (!ContainerThreadMarker.isContainerThread() && (this.state == AsyncState.STARTING || this.state == AsyncState.READ_WRITE_OP)) {
            this.updateState(AsyncState.DISPATCH_PENDING);
            return false;
        }
        this.clearNonBlockingListeners();
        boolean triggerDispatch = false;
        if (this.state == AsyncState.STARTING || this.state == AsyncState.MUST_ERROR) {
            this.updateState(AsyncState.MUST_DISPATCH);
        }
        else if (this.state == AsyncState.STARTED) {
            this.updateState(AsyncState.DISPATCHING);
            triggerDispatch = true;
        }
        else {
            if (this.state != AsyncState.READ_WRITE_OP && this.state != AsyncState.TIMING_OUT && this.state != AsyncState.ERROR) {
                throw new IllegalStateException(AsyncStateMachine.sm.getString("asyncStateMachine.invalidAsyncState", new Object[] { "asyncDispatch()", this.state }));
            }
            this.updateState(AsyncState.DISPATCHING);
        }
        return triggerDispatch;
    }
    
    public synchronized void asyncDispatched() {
        if (this.state == AsyncState.DISPATCHING || this.state == AsyncState.MUST_DISPATCH) {
            this.updateState(AsyncState.DISPATCHED);
            this.asyncCtxt.decrementInProgressAsyncCount();
            return;
        }
        throw new IllegalStateException(AsyncStateMachine.sm.getString("asyncStateMachine.invalidAsyncState", new Object[] { "asyncDispatched()", this.state }));
    }
    
    public synchronized boolean asyncError() {
        this.clearNonBlockingListeners();
        if (this.state == AsyncState.STARTING) {
            this.updateState(AsyncState.MUST_ERROR);
        }
        else if (this.state == AsyncState.DISPATCHED) {
            this.asyncCtxt.incrementInProgressAsyncCount();
            this.updateState(AsyncState.ERROR);
        }
        else {
            this.updateState(AsyncState.ERROR);
        }
        return !ContainerThreadMarker.isContainerThread();
    }
    
    public synchronized void asyncRun(final Runnable runnable) {
        if (this.state == AsyncState.STARTING || this.state == AsyncState.STARTED || this.state == AsyncState.READ_WRITE_OP) {
            ClassLoader oldCL;
            if (Constants.IS_SECURITY_ENABLED) {
                final PrivilegedAction<ClassLoader> pa = (PrivilegedAction<ClassLoader>)new PrivilegedGetTccl();
                oldCL = AccessController.doPrivileged(pa);
            }
            else {
                oldCL = Thread.currentThread().getContextClassLoader();
            }
            try {
                if (Constants.IS_SECURITY_ENABLED) {
                    final PrivilegedAction<Void> pa2 = (PrivilegedAction<Void>)new PrivilegedSetTccl(this.getClass().getClassLoader());
                    AccessController.doPrivileged(pa2);
                }
                else {
                    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                }
                this.processor.getExecutor().execute(runnable);
            }
            finally {
                if (Constants.IS_SECURITY_ENABLED) {
                    final PrivilegedAction<Void> pa3 = (PrivilegedAction<Void>)new PrivilegedSetTccl(oldCL);
                    AccessController.doPrivileged(pa3);
                }
                else {
                    Thread.currentThread().setContextClassLoader(oldCL);
                }
            }
            return;
        }
        throw new IllegalStateException(AsyncStateMachine.sm.getString("asyncStateMachine.invalidAsyncState", new Object[] { "asyncRun()", this.state }));
    }
    
    synchronized boolean isAvailable() {
        return this.asyncCtxt != null && this.asyncCtxt.isAvailable();
    }
    
    public synchronized void recycle() {
        if (this.lastAsyncStart == 0L) {
            return;
        }
        this.notifyAll();
        this.asyncCtxt = null;
        this.state = AsyncState.DISPATCHED;
        this.lastAsyncStart = 0L;
    }
    
    private void clearNonBlockingListeners() {
        this.processor.getRequest().listener = null;
        this.processor.getRequest().getResponse().listener = null;
    }
    
    private synchronized void updateState(final AsyncState newState) {
        if (AsyncStateMachine.log.isDebugEnabled()) {
            AsyncStateMachine.log.debug((Object)AsyncStateMachine.sm.getString("asyncStateMachine.stateChange", new Object[] { this.state, newState }));
        }
        this.state = newState;
    }
    
    static {
        log = LogFactory.getLog((Class)AsyncStateMachine.class);
        sm = StringManager.getManager((Class)AsyncStateMachine.class);
    }
    
    private enum AsyncState
    {
        DISPATCHED(false, false, false, false), 
        STARTING(true, true, false, false), 
        STARTED(true, true, false, false), 
        MUST_COMPLETE(true, true, true, false), 
        COMPLETE_PENDING(true, true, false, false), 
        COMPLETING(true, false, true, false), 
        TIMING_OUT(true, true, false, false), 
        MUST_DISPATCH(true, true, false, true), 
        DISPATCH_PENDING(true, true, false, false), 
        DISPATCHING(true, false, false, true), 
        READ_WRITE_OP(true, true, false, false), 
        MUST_ERROR(true, true, false, false), 
        ERROR(true, true, false, false);
        
        private final boolean isAsync;
        private final boolean isStarted;
        private final boolean isCompleting;
        private final boolean isDispatching;
        
        private AsyncState(final boolean isAsync, final boolean isStarted, final boolean isCompleting, final boolean isDispatching) {
            this.isAsync = isAsync;
            this.isStarted = isStarted;
            this.isCompleting = isCompleting;
            this.isDispatching = isDispatching;
        }
        
        public boolean isAsync() {
            return this.isAsync;
        }
        
        public boolean isStarted() {
            return this.isStarted;
        }
        
        public boolean isDispatching() {
            return this.isDispatching;
        }
        
        public boolean isCompleting() {
            return this.isCompleting;
        }
    }
}
