package org.apache.commons.math3.ode.events;

import java.util.Arrays;

public class EventFilter implements EventHandler
{
    private static final int HISTORY_SIZE = 100;
    private final EventHandler rawHandler;
    private final FilterType filter;
    private final Transformer[] transformers;
    private final double[] updates;
    private boolean forward;
    private double extremeT;
    
    public EventFilter(final EventHandler rawHandler, final FilterType filter) {
        this.rawHandler = rawHandler;
        this.filter = filter;
        this.transformers = new Transformer[100];
        this.updates = new double[100];
    }
    
    public void init(final double t0, final double[] y0, final double t) {
        this.rawHandler.init(t0, y0, t);
        this.forward = (t >= t0);
        this.extremeT = (this.forward ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
        Arrays.fill(this.transformers, Transformer.UNINITIALIZED);
        Arrays.fill(this.updates, this.extremeT);
    }
    
    public double g(final double t, final double[] y) {
        final double rawG = this.rawHandler.g(t, y);
        if (this.forward) {
            final int last = this.transformers.length - 1;
            if (this.extremeT < t) {
                final Transformer previous = this.transformers[last];
                final Transformer next = this.filter.selectTransformer(previous, rawG, this.forward);
                if (next != previous) {
                    System.arraycopy(this.updates, 1, this.updates, 0, last);
                    System.arraycopy(this.transformers, 1, this.transformers, 0, last);
                    this.updates[last] = this.extremeT;
                    this.transformers[last] = next;
                }
                this.extremeT = t;
                return next.transformed(rawG);
            }
            for (int i = last; i > 0; --i) {
                if (this.updates[i] <= t) {
                    return this.transformers[i].transformed(rawG);
                }
            }
            return this.transformers[0].transformed(rawG);
        }
        else {
            if (t < this.extremeT) {
                final Transformer previous2 = this.transformers[0];
                final Transformer next2 = this.filter.selectTransformer(previous2, rawG, this.forward);
                if (next2 != previous2) {
                    System.arraycopy(this.updates, 0, this.updates, 1, this.updates.length - 1);
                    System.arraycopy(this.transformers, 0, this.transformers, 1, this.transformers.length - 1);
                    this.updates[0] = this.extremeT;
                    this.transformers[0] = next2;
                }
                this.extremeT = t;
                return next2.transformed(rawG);
            }
            for (int j = 0; j < this.updates.length - 1; ++j) {
                if (t <= this.updates[j]) {
                    return this.transformers[j].transformed(rawG);
                }
            }
            return this.transformers[this.updates.length - 1].transformed(rawG);
        }
    }
    
    public Action eventOccurred(final double t, final double[] y, final boolean increasing) {
        return this.rawHandler.eventOccurred(t, y, this.filter.getTriggeredIncreasing());
    }
    
    public void resetState(final double t, final double[] y) {
        this.rawHandler.resetState(t, y);
    }
}
