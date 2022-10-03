package java.util.stream;

import java.util.concurrent.CountedCompleter;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;

abstract class AbstractShortCircuitTask<P_IN, P_OUT, R, K extends AbstractShortCircuitTask<P_IN, P_OUT, R, K>> extends AbstractTask<P_IN, P_OUT, R, K>
{
    protected final AtomicReference<R> sharedResult;
    protected volatile boolean canceled;
    
    protected AbstractShortCircuitTask(final PipelineHelper<P_OUT> pipelineHelper, final Spliterator<P_IN> spliterator) {
        super(pipelineHelper, spliterator);
        this.sharedResult = new AtomicReference<R>(null);
    }
    
    protected AbstractShortCircuitTask(final K k, final Spliterator<P_IN> spliterator) {
        super(k, spliterator);
        this.sharedResult = k.sharedResult;
    }
    
    protected abstract R getEmptyResult();
    
    @Override
    public void compute() {
        Spliterator<P_IN> spliterator = this.spliterator;
        long n = spliterator.estimateSize();
        final long targetSize = this.getTargetSize(n);
        int n2 = 0;
        CountedCompleter<R> countedCompleter = this;
        Object localResult;
        while ((localResult = this.sharedResult.get()) == null) {
            if (((AbstractShortCircuitTask)countedCompleter).taskCanceled()) {
                localResult = ((AbstractShortCircuitTask<P_IN, Object, Object, AbstractTask>)countedCompleter).getEmptyResult();
                break;
            }
            final Spliterator trySplit;
            if (n <= targetSize || (trySplit = spliterator.trySplit()) == null) {
                localResult = ((AbstractTask<P_IN, Object, Object, AbstractTask>)countedCompleter).doLeaf();
                break;
            }
            final AbstractTask<P_IN, P_OUT, R, K> abstractTask = ((AbstractShortCircuitTask)countedCompleter).leftChild = (K)((AbstractTask<P_IN, Object, Object, AbstractTask>)countedCompleter).makeChild(trySplit);
            final AbstractTask<P_IN, P_OUT, R, K> abstractTask2 = ((AbstractShortCircuitTask)countedCompleter).rightChild = (K)((AbstractTask<P_IN, Object, Object, AbstractTask>)countedCompleter).makeChild(spliterator);
            countedCompleter.setPendingCount(1);
            CountedCompleter<R> countedCompleter2;
            if (n2 != 0) {
                n2 = 0;
                spliterator = trySplit;
                countedCompleter = abstractTask;
                countedCompleter2 = abstractTask2;
            }
            else {
                n2 = 1;
                countedCompleter = abstractTask2;
                countedCompleter2 = abstractTask;
            }
            countedCompleter2.fork();
            n = spliterator.estimateSize();
        }
        ((AbstractShortCircuitTask<P_IN, Object, Object, AbstractTask>)countedCompleter).setLocalResult(localResult);
        countedCompleter.tryComplete();
    }
    
    protected void shortCircuit(final R r) {
        if (r != null) {
            this.sharedResult.compareAndSet(null, r);
        }
    }
    
    @Override
    protected void setLocalResult(final R localResult) {
        if (this.isRoot()) {
            if (localResult != null) {
                this.sharedResult.compareAndSet(null, localResult);
            }
        }
        else {
            super.setLocalResult(localResult);
        }
    }
    
    @Override
    public R getRawResult() {
        return this.getLocalResult();
    }
    
    public R getLocalResult() {
        if (this.isRoot()) {
            final R value = this.sharedResult.get();
            return (value == null) ? this.getEmptyResult() : value;
        }
        return super.getLocalResult();
    }
    
    protected void cancel() {
        this.canceled = true;
    }
    
    protected boolean taskCanceled() {
        boolean b = this.canceled;
        if (!b) {
            for (AbstractShortCircuitTask abstractShortCircuitTask = this.getParent(); !b && abstractShortCircuitTask != null; b = abstractShortCircuitTask.canceled, abstractShortCircuitTask = (AbstractShortCircuitTask)abstractShortCircuitTask.getParent()) {}
        }
        return b;
    }
    
    protected void cancelLaterNodes() {
        AbstractShortCircuitTask abstractShortCircuitTask = this.getParent();
        AbstractShortCircuitTask abstractShortCircuitTask2 = this;
        while (abstractShortCircuitTask != null) {
            if (abstractShortCircuitTask.leftChild == abstractShortCircuitTask2) {
                final AbstractShortCircuitTask abstractShortCircuitTask3 = (AbstractShortCircuitTask)abstractShortCircuitTask.rightChild;
                if (!abstractShortCircuitTask3.canceled) {
                    abstractShortCircuitTask3.cancel();
                }
            }
            abstractShortCircuitTask2 = abstractShortCircuitTask;
            abstractShortCircuitTask = (AbstractShortCircuitTask)abstractShortCircuitTask.getParent();
        }
    }
}
