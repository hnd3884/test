package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.Collection;

public class UniformTimeValuesSnapshot extends AbstractTimeSnapshot
{
    private final long[] values;
    
    public UniformTimeValuesSnapshot(final Collection<Long> values, final long timeInterval, final TimeUnit timeIntervalUnit) {
        super(timeInterval, timeIntervalUnit);
        final Object[] copy = values.toArray();
        this.values = new long[copy.length];
        for (int i = 0; i < copy.length; ++i) {
            this.values[i] = (long)copy[i];
        }
        Arrays.sort(this.values);
    }
    
    public double getValue(final double quantile) {
        if (quantile < 0.0 || quantile > 1.0 || Double.isNaN(quantile)) {
            throw new IllegalArgumentException(quantile + " is not in [0..1] range");
        }
        if (this.values.length == 0) {
            return 0.0;
        }
        final double pos = quantile * (this.values.length + 1);
        final int index = (int)pos;
        if (index < 1) {
            return (double)this.values[0];
        }
        if (index >= this.values.length) {
            return (double)this.values[this.values.length - 1];
        }
        final double lower = (double)this.values[index - 1];
        final double upper = (double)this.values[index];
        return lower + (pos - Math.floor(pos)) * (upper - lower);
    }
    
    @Override
    public long size() {
        return this.values.length;
    }
    
    public long[] getValues() {
        return Arrays.copyOf(this.values, this.values.length);
    }
    
    @Override
    public long getMax() {
        if (this.values.length == 0) {
            return 0L;
        }
        return this.values[this.values.length - 1];
    }
    
    @Override
    public long getMin() {
        if (this.values.length == 0) {
            return 0L;
        }
        return this.values[0];
    }
    
    @Override
    public double getMean() {
        if (this.values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (final long value : this.values) {
            sum += value;
        }
        return sum / this.values.length;
    }
}
