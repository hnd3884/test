package org.apache.commons.math3.util;

import java.util.Iterator;
import org.apache.commons.math3.exception.MaxCountExceededException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collection;

public class IterationManager
{
    private IntegerSequence.Incrementor iterations;
    private final Collection<IterationListener> listeners;
    
    public IterationManager(final int maxIterations) {
        this.iterations = IntegerSequence.Incrementor.create().withMaximalCount(maxIterations);
        this.listeners = new CopyOnWriteArrayList<IterationListener>();
    }
    
    @Deprecated
    public IterationManager(final int maxIterations, final Incrementor.MaxCountExceededCallback callBack) {
        this(maxIterations, new IntegerSequence.Incrementor.MaxCountExceededCallback() {
            public void trigger(final int maximalCount) throws MaxCountExceededException {
                callBack.trigger(maximalCount);
            }
        });
    }
    
    public IterationManager(final int maxIterations, final IntegerSequence.Incrementor.MaxCountExceededCallback callBack) {
        this.iterations = IntegerSequence.Incrementor.create().withMaximalCount(maxIterations).withCallback(callBack);
        this.listeners = new CopyOnWriteArrayList<IterationListener>();
    }
    
    public void addIterationListener(final IterationListener listener) {
        this.listeners.add(listener);
    }
    
    public void fireInitializationEvent(final IterationEvent e) {
        for (final IterationListener l : this.listeners) {
            l.initializationPerformed(e);
        }
    }
    
    public void fireIterationPerformedEvent(final IterationEvent e) {
        for (final IterationListener l : this.listeners) {
            l.iterationPerformed(e);
        }
    }
    
    public void fireIterationStartedEvent(final IterationEvent e) {
        for (final IterationListener l : this.listeners) {
            l.iterationStarted(e);
        }
    }
    
    public void fireTerminationEvent(final IterationEvent e) {
        for (final IterationListener l : this.listeners) {
            l.terminationPerformed(e);
        }
    }
    
    public int getIterations() {
        return this.iterations.getCount();
    }
    
    public int getMaxIterations() {
        return this.iterations.getMaximalCount();
    }
    
    public void incrementIterationCount() throws MaxCountExceededException {
        this.iterations.increment();
    }
    
    public void removeIterationListener(final IterationListener listener) {
        this.listeners.remove(listener);
    }
    
    public void resetIterationCount() {
        this.iterations = this.iterations.withStart(0);
    }
}
