package org.glassfish.jersey.server.internal.monitoring;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collection;

class AggregatedValueObject
{
    private final long max;
    private final long min;
    private final double mean;
    private final long count;
    
    private AggregatedValueObject(final long max, final long min, final double mean, final long count) {
        this.max = max;
        this.min = min;
        this.mean = mean;
        this.count = count;
    }
    
    public static AggregatedValueObject createFromValues(final Collection<Long> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("The values collection must not be empty");
        }
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        long sum = 0L;
        for (final Long value : values) {
            max = Math.max(max, value);
            min = Math.min(min, value);
            sum += value;
        }
        return new AggregatedValueObject(max, min, sum / (double)values.size(), values.size());
    }
    
    public static AggregatedValueObject createFromMultiValues(final Collection<? extends Collection<Long>> values) {
        final Collection<Long> mergedCollection = new LinkedList<Long>();
        for (final Collection<Long> collection : values) {
            mergedCollection.addAll(collection);
        }
        return createFromValues(mergedCollection);
    }
    
    public long getMax() {
        return this.max;
    }
    
    public long getMin() {
        return this.min;
    }
    
    public double getMean() {
        return this.mean;
    }
    
    public long getCount() {
        return this.count;
    }
}
