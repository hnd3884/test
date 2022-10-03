package io.opencensus.stats;

import io.opencensus.internal.Utils;
import io.opencensus.common.Function;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Aggregation
{
    private Aggregation() {
    }
    
    public abstract <T> T match(final Function<? super Sum, T> p0, final Function<? super Count, T> p1, final Function<? super Distribution, T> p2, final Function<? super LastValue, T> p3, final Function<? super Aggregation, T> p4);
    
    @Immutable
    public abstract static class Sum extends Aggregation
    {
        private static final Sum INSTANCE;
        
        Sum() {
            super(null);
        }
        
        public static Sum create() {
            return Sum.INSTANCE;
        }
        
        @Override
        public final <T> T match(final Function<? super Sum, T> p0, final Function<? super Count, T> p1, final Function<? super Distribution, T> p2, final Function<? super LastValue, T> p3, final Function<? super Aggregation, T> defaultFunction) {
            return p0.apply(this);
        }
        
        static {
            INSTANCE = new AutoValue_Aggregation_Sum();
        }
    }
    
    @Immutable
    public abstract static class Count extends Aggregation
    {
        private static final Count INSTANCE;
        
        Count() {
            super(null);
        }
        
        public static Count create() {
            return Count.INSTANCE;
        }
        
        @Override
        public final <T> T match(final Function<? super Sum, T> p0, final Function<? super Count, T> p1, final Function<? super Distribution, T> p2, final Function<? super LastValue, T> p3, final Function<? super Aggregation, T> defaultFunction) {
            return p1.apply(this);
        }
        
        static {
            INSTANCE = new AutoValue_Aggregation_Count();
        }
    }
    
    @Deprecated
    @Immutable
    public abstract static class Mean extends Aggregation
    {
        private static final Mean INSTANCE;
        
        Mean() {
            super(null);
        }
        
        public static Mean create() {
            return Mean.INSTANCE;
        }
        
        @Override
        public final <T> T match(final Function<? super Sum, T> p0, final Function<? super Count, T> p1, final Function<? super Distribution, T> p2, final Function<? super LastValue, T> p3, final Function<? super Aggregation, T> defaultFunction) {
            return defaultFunction.apply(this);
        }
        
        static {
            INSTANCE = new AutoValue_Aggregation_Mean();
        }
    }
    
    @Immutable
    public abstract static class Distribution extends Aggregation
    {
        Distribution() {
            super(null);
        }
        
        public static Distribution create(final BucketBoundaries bucketBoundaries) {
            Utils.checkNotNull(bucketBoundaries, "bucketBoundaries");
            return new AutoValue_Aggregation_Distribution(bucketBoundaries);
        }
        
        public abstract BucketBoundaries getBucketBoundaries();
        
        @Override
        public final <T> T match(final Function<? super Sum, T> p0, final Function<? super Count, T> p1, final Function<? super Distribution, T> p2, final Function<? super LastValue, T> p3, final Function<? super Aggregation, T> defaultFunction) {
            return p2.apply(this);
        }
    }
    
    @Immutable
    public abstract static class LastValue extends Aggregation
    {
        private static final LastValue INSTANCE;
        
        LastValue() {
            super(null);
        }
        
        public static LastValue create() {
            return LastValue.INSTANCE;
        }
        
        @Override
        public final <T> T match(final Function<? super Sum, T> p0, final Function<? super Count, T> p1, final Function<? super Distribution, T> p2, final Function<? super LastValue, T> p3, final Function<? super Aggregation, T> defaultFunction) {
            return p3.apply(this);
        }
        
        static {
            INSTANCE = new AutoValue_Aggregation_LastValue();
        }
    }
}
