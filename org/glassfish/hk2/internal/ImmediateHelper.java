package org.glassfish.hk2.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import org.glassfish.hk2.api.Operation;
import org.glassfish.hk2.api.ValidationInformation;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ActiveDescriptor;
import java.util.List;
import javax.inject.Inject;
import java.util.HashSet;
import org.glassfish.hk2.utilities.ImmediateContext;
import org.glassfish.hk2.api.ServiceLocator;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Visibility;
import javax.inject.Singleton;
import org.glassfish.hk2.api.ImmediateController;
import org.glassfish.hk2.api.Validator;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.ValidationService;
import org.glassfish.hk2.api.DynamicConfigurationListener;

@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class ImmediateHelper implements DynamicConfigurationListener, Runnable, ValidationService, ErrorService, Validator, ImmediateController
{
    private static final ThreadFactory THREAD_FACTORY;
    private static final Executor DEFAULT_EXECUTOR;
    private final ServiceLocator locator;
    private final ImmediateContext immediateContext;
    private final HashSet<Long> tidsWithWork;
    private final Object queueLock;
    private boolean threadAvailable;
    private boolean outstandingJob;
    private boolean waitingForWork;
    private boolean firstTime;
    private ImmediateServiceState currentState;
    private Executor currentExecutor;
    private long decayTime;
    
    @Inject
    private ImmediateHelper(final ServiceLocator serviceLocator, final ImmediateContext immediateContext) {
        this.tidsWithWork = new HashSet<Long>();
        this.queueLock = new Object();
        this.firstTime = true;
        this.currentState = ImmediateServiceState.SUSPENDED;
        this.currentExecutor = ImmediateHelper.DEFAULT_EXECUTOR;
        this.decayTime = 20000L;
        this.locator = serviceLocator;
        this.immediateContext = immediateContext;
    }
    
    private boolean hasWork() {
        final long tid = Thread.currentThread().getId();
        final boolean wasFirst = this.firstTime;
        this.firstTime = false;
        final boolean retVal = this.tidsWithWork.contains(tid);
        this.tidsWithWork.remove(tid);
        if (retVal || !wasFirst) {
            return retVal;
        }
        final List<ActiveDescriptor<?>> immediates = this.getImmediateServices();
        return !immediates.isEmpty();
    }
    
    private void doWorkIfWeHaveSome() {
        if (!this.hasWork()) {
            return;
        }
        this.outstandingJob = true;
        if (!this.threadAvailable) {
            this.threadAvailable = true;
            this.currentExecutor.execute(this);
        }
        else if (this.waitingForWork) {
            this.queueLock.notify();
        }
    }
    
    @Override
    public void configurationChanged() {
        synchronized (this.queueLock) {
            if (this.currentState.equals(ImmediateServiceState.SUSPENDED)) {
                return;
            }
            this.doWorkIfWeHaveSome();
        }
    }
    
    @Override
    public Filter getLookupFilter() {
        return this.immediateContext.getValidationFilter();
    }
    
    @Override
    public Validator getValidator() {
        return this;
    }
    
    @Override
    public void onFailure(final ErrorInformation errorInformation) throws MultiException {
        if (!ErrorType.DYNAMIC_CONFIGURATION_FAILURE.equals(errorInformation.getErrorType())) {
            final long tid = Thread.currentThread().getId();
            synchronized (this.queueLock) {
                this.tidsWithWork.remove(tid);
            }
        }
    }
    
    @Override
    public boolean validate(final ValidationInformation info) {
        if (info.getOperation().equals(Operation.BIND) || info.getOperation().equals(Operation.UNBIND)) {
            final long tid = Thread.currentThread().getId();
            synchronized (this.queueLock) {
                this.tidsWithWork.add(tid);
            }
        }
        return true;
    }
    
    @Override
    public void run() {
        while (true) {
            synchronized (this.queueLock) {
                long elapsedTime;
                for (long decayTime = this.decayTime; this.currentState.equals(ImmediateServiceState.RUNNING) && !this.outstandingJob && decayTime > 0L; decayTime -= elapsedTime) {
                    this.waitingForWork = true;
                    final long currentTime = System.currentTimeMillis();
                    try {
                        this.queueLock.wait(decayTime);
                    }
                    catch (final InterruptedException ie) {
                        this.threadAvailable = false;
                        this.waitingForWork = false;
                        return;
                    }
                    elapsedTime = System.currentTimeMillis() - currentTime;
                }
                this.waitingForWork = false;
                if (!this.outstandingJob || this.currentState.equals(ImmediateServiceState.SUSPENDED)) {
                    this.threadAvailable = false;
                    return;
                }
                this.outstandingJob = false;
            }
            this.immediateContext.doWork();
        }
    }
    
    @Override
    public Executor getExecutor() {
        synchronized (this.queueLock) {
            return this.currentExecutor;
        }
    }
    
    @Override
    public void setExecutor(final Executor executor) throws IllegalStateException {
        synchronized (this.queueLock) {
            if (this.currentState.equals(ImmediateServiceState.RUNNING)) {
                throw new IllegalStateException("ImmediateSerivce attempt made to change executor while in RUNNING state");
            }
            this.currentExecutor = ((executor == null) ? ImmediateHelper.DEFAULT_EXECUTOR : executor);
        }
    }
    
    @Override
    public long getThreadInactivityTimeout() {
        synchronized (this.queueLock) {
            return this.decayTime;
        }
    }
    
    @Override
    public void setThreadInactivityTimeout(final long timeInMillis) throws IllegalStateException {
        synchronized (this.queueLock) {
            if (timeInMillis < 0L) {
                throw new IllegalArgumentException();
            }
            this.decayTime = timeInMillis;
        }
    }
    
    @Override
    public ImmediateServiceState getImmediateState() {
        synchronized (this.queueLock) {
            return this.currentState;
        }
    }
    
    @Override
    public void setImmediateState(final ImmediateServiceState state) {
        synchronized (this.queueLock) {
            if (state == null) {
                throw new IllegalArgumentException();
            }
            if (state == this.currentState) {
                return;
            }
            this.currentState = state;
            if (this.currentState.equals(ImmediateServiceState.RUNNING)) {
                this.doWorkIfWeHaveSome();
            }
        }
    }
    
    private List<ActiveDescriptor<?>> getImmediateServices() {
        List<ActiveDescriptor<?>> inScopeAndInThisLocator;
        try {
            inScopeAndInThisLocator = this.locator.getDescriptors(this.immediateContext.getValidationFilter());
        }
        catch (final IllegalStateException ise) {
            inScopeAndInThisLocator = Collections.emptyList();
        }
        return inScopeAndInThisLocator;
    }
    
    static {
        THREAD_FACTORY = new ImmediateThreadFactory();
        DEFAULT_EXECUTOR = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(true), ImmediateHelper.THREAD_FACTORY);
    }
    
    private static class ImmediateThread extends Thread
    {
        private ImmediateThread(final Runnable r) {
            super(r);
            this.setDaemon(true);
            this.setName(this.getClass().getSimpleName() + "-" + System.currentTimeMillis());
        }
    }
    
    private static class ImmediateThreadFactory implements ThreadFactory
    {
        @Override
        public Thread newThread(final Runnable runnable) {
            final Thread activeThread = new ImmediateThread(runnable);
            return activeThread;
        }
    }
}
