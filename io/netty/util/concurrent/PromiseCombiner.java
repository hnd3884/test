package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;

public final class PromiseCombiner
{
    private int expectedCount;
    private int doneCount;
    private Promise<Void> aggregatePromise;
    private Throwable cause;
    private final GenericFutureListener<Future<?>> listener;
    private final EventExecutor executor;
    
    @Deprecated
    public PromiseCombiner() {
        this(ImmediateEventExecutor.INSTANCE);
    }
    
    public PromiseCombiner(final EventExecutor executor) {
        this.listener = new GenericFutureListener<Future<?>>() {
            @Override
            public void operationComplete(final Future<?> future) {
                if (PromiseCombiner.this.executor.inEventLoop()) {
                    this.operationComplete0(future);
                }
                else {
                    PromiseCombiner.this.executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            PromiseCombiner$1.this.operationComplete0(future);
                        }
                    });
                }
            }
            
            private void operationComplete0(final Future<?> future) {
                assert PromiseCombiner.this.executor.inEventLoop();
                ++PromiseCombiner.this.doneCount;
                if (!future.isSuccess() && PromiseCombiner.this.cause == null) {
                    PromiseCombiner.this.cause = future.cause();
                }
                if (PromiseCombiner.this.doneCount == PromiseCombiner.this.expectedCount && PromiseCombiner.this.aggregatePromise != null) {
                    PromiseCombiner.this.tryPromise();
                }
            }
        };
        this.executor = ObjectUtil.checkNotNull(executor, "executor");
    }
    
    @Deprecated
    public void add(final Promise promise) {
        this.add((Future)promise);
    }
    
    public void add(final Future future) {
        this.checkAddAllowed();
        this.checkInEventLoop();
        ++this.expectedCount;
        future.addListener(this.listener);
    }
    
    @Deprecated
    public void addAll(final Promise... promises) {
        this.addAll((Future[])promises);
    }
    
    public void addAll(final Future... futures) {
        for (final Future future : futures) {
            this.add(future);
        }
    }
    
    public void finish(final Promise<Void> aggregatePromise) {
        ObjectUtil.checkNotNull(aggregatePromise, "aggregatePromise");
        this.checkInEventLoop();
        if (this.aggregatePromise != null) {
            throw new IllegalStateException("Already finished");
        }
        this.aggregatePromise = aggregatePromise;
        if (this.doneCount == this.expectedCount) {
            this.tryPromise();
        }
    }
    
    private void checkInEventLoop() {
        if (!this.executor.inEventLoop()) {
            throw new IllegalStateException("Must be called from EventExecutor thread");
        }
    }
    
    private boolean tryPromise() {
        return (this.cause == null) ? this.aggregatePromise.trySuccess(null) : this.aggregatePromise.tryFailure(this.cause);
    }
    
    private void checkAddAllowed() {
        if (this.aggregatePromise != null) {
            throw new IllegalStateException("Adding promises is not allowed after finished adding");
        }
    }
}
