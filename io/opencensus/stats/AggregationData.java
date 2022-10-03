package io.opencensus.stats;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import io.opencensus.internal.Utils;
import java.util.Collection;
import io.opencensus.metrics.data.Exemplar;
import java.util.List;
import io.opencensus.common.Function;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class AggregationData
{
    private AggregationData() {
    }
    
    public abstract <T> T match(final Function<? super SumDataDouble, T> p0, final Function<? super SumDataLong, T> p1, final Function<? super CountData, T> p2, final Function<? super DistributionData, T> p3, final Function<? super LastValueDataDouble, T> p4, final Function<? super LastValueDataLong, T> p5, final Function<? super AggregationData, T> p6);
    
    @Immutable
    public abstract static class SumDataDouble extends AggregationData
    {
        SumDataDouble() {
            super(null);
        }
        
        public static SumDataDouble create(final double sum) {
            return new AutoValue_AggregationData_SumDataDouble(sum);
        }
        
        public abstract double getSum();
        
        @Override
        public final <T> T match(final Function<? super SumDataDouble, T> p0, final Function<? super SumDataLong, T> p1, final Function<? super CountData, T> p2, final Function<? super DistributionData, T> p3, final Function<? super LastValueDataDouble, T> p4, final Function<? super LastValueDataLong, T> p5, final Function<? super AggregationData, T> defaultFunction) {
            return p0.apply(this);
        }
    }
    
    @Immutable
    public abstract static class SumDataLong extends AggregationData
    {
        SumDataLong() {
            super(null);
        }
        
        public static SumDataLong create(final long sum) {
            return new AutoValue_AggregationData_SumDataLong(sum);
        }
        
        public abstract long getSum();
        
        @Override
        public final <T> T match(final Function<? super SumDataDouble, T> p0, final Function<? super SumDataLong, T> p1, final Function<? super CountData, T> p2, final Function<? super DistributionData, T> p3, final Function<? super LastValueDataDouble, T> p4, final Function<? super LastValueDataLong, T> p5, final Function<? super AggregationData, T> defaultFunction) {
            return p1.apply(this);
        }
    }
    
    @Immutable
    public abstract static class CountData extends AggregationData
    {
        CountData() {
            super(null);
        }
        
        public static CountData create(final long count) {
            return new AutoValue_AggregationData_CountData(count);
        }
        
        public abstract long getCount();
        
        @Override
        public final <T> T match(final Function<? super SumDataDouble, T> p0, final Function<? super SumDataLong, T> p1, final Function<? super CountData, T> p2, final Function<? super DistributionData, T> p3, final Function<? super LastValueDataDouble, T> p4, final Function<? super LastValueDataLong, T> p5, final Function<? super AggregationData, T> defaultFunction) {
            return p2.apply(this);
        }
    }
    
    @Deprecated
    @Immutable
    public abstract static class MeanData extends AggregationData
    {
        MeanData() {
            super(null);
        }
        
        public static MeanData create(final double mean, final long count) {
            return new AutoValue_AggregationData_MeanData(mean, count);
        }
        
        public abstract double getMean();
        
        public abstract long getCount();
        
        @Override
        public final <T> T match(final Function<? super SumDataDouble, T> p0, final Function<? super SumDataLong, T> p1, final Function<? super CountData, T> p2, final Function<? super DistributionData, T> p3, final Function<? super LastValueDataDouble, T> p4, final Function<? super LastValueDataLong, T> p5, final Function<? super AggregationData, T> defaultFunction) {
            return defaultFunction.apply(this);
        }
    }
    
    @Immutable
    public abstract static class DistributionData extends AggregationData
    {
        DistributionData() {
            super(null);
        }
        
        @Deprecated
        public static DistributionData create(final double mean, final long count, final double min, final double max, final double sumOfSquaredDeviations, final List<Long> bucketCounts, final List<Exemplar> exemplars) {
            return create(mean, count, sumOfSquaredDeviations, bucketCounts, exemplars);
        }
        
        public static DistributionData create(final double mean, final long count, final double sumOfSquaredDeviations, final List<Long> bucketCounts, final List<Exemplar> exemplars) {
            final List<Long> bucketCountsCopy = Collections.unmodifiableList((List<? extends Long>)new ArrayList<Long>(Utils.checkNotNull(bucketCounts, "bucketCounts")));
            for (final Long bucketCount : bucketCountsCopy) {
                Utils.checkNotNull(bucketCount, "bucketCount");
            }
            Utils.checkNotNull(exemplars, "exemplars");
            for (final Exemplar exemplar : exemplars) {
                Utils.checkNotNull(exemplar, "exemplar");
            }
            return new AutoValue_AggregationData_DistributionData(mean, count, sumOfSquaredDeviations, bucketCountsCopy, Collections.unmodifiableList((List<? extends Exemplar>)new ArrayList<Exemplar>(exemplars)));
        }
        
        @Deprecated
        public static DistributionData create(final double mean, final long count, final double min, final double max, final double sumOfSquaredDeviations, final List<Long> bucketCounts) {
            return create(mean, count, sumOfSquaredDeviations, bucketCounts, Collections.emptyList());
        }
        
        public static DistributionData create(final double mean, final long count, final double sumOfSquaredDeviations, final List<Long> bucketCounts) {
            return create(mean, count, sumOfSquaredDeviations, bucketCounts, Collections.emptyList());
        }
        
        public abstract double getMean();
        
        public abstract long getCount();
        
        @Deprecated
        public double getMin() {
            return 0.0;
        }
        
        @Deprecated
        public double getMax() {
            return 0.0;
        }
        
        public abstract double getSumOfSquaredDeviations();
        
        public abstract List<Long> getBucketCounts();
        
        public abstract List<Exemplar> getExemplars();
        
        @Override
        public final <T> T match(final Function<? super SumDataDouble, T> p0, final Function<? super SumDataLong, T> p1, final Function<? super CountData, T> p2, final Function<? super DistributionData, T> p3, final Function<? super LastValueDataDouble, T> p4, final Function<? super LastValueDataLong, T> p5, final Function<? super AggregationData, T> defaultFunction) {
            return p3.apply(this);
        }
    }
    
    @Immutable
    public abstract static class LastValueDataDouble extends AggregationData
    {
        LastValueDataDouble() {
            super(null);
        }
        
        public static LastValueDataDouble create(final double lastValue) {
            return new AutoValue_AggregationData_LastValueDataDouble(lastValue);
        }
        
        public abstract double getLastValue();
        
        @Override
        public final <T> T match(final Function<? super SumDataDouble, T> p0, final Function<? super SumDataLong, T> p1, final Function<? super CountData, T> p2, final Function<? super DistributionData, T> p3, final Function<? super LastValueDataDouble, T> p4, final Function<? super LastValueDataLong, T> p5, final Function<? super AggregationData, T> defaultFunction) {
            return p4.apply(this);
        }
    }
    
    @Immutable
    public abstract static class LastValueDataLong extends AggregationData
    {
        LastValueDataLong() {
            super(null);
        }
        
        public static LastValueDataLong create(final long lastValue) {
            return new AutoValue_AggregationData_LastValueDataLong(lastValue);
        }
        
        public abstract long getLastValue();
        
        @Override
        public final <T> T match(final Function<? super SumDataDouble, T> p0, final Function<? super SumDataLong, T> p1, final Function<? super CountData, T> p2, final Function<? super DistributionData, T> p3, final Function<? super LastValueDataDouble, T> p4, final Function<? super LastValueDataLong, T> p5, final Function<? super AggregationData, T> defaultFunction) {
            return p5.apply(this);
        }
    }
}
