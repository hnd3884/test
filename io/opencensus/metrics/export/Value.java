package io.opencensus.metrics.export;

import io.opencensus.common.Function;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Value
{
    Value() {
    }
    
    public static Value doubleValue(final double value) {
        return ValueDouble.create(value);
    }
    
    public static Value longValue(final long value) {
        return ValueLong.create(value);
    }
    
    public static Value distributionValue(final Distribution value) {
        return ValueDistribution.create(value);
    }
    
    public static Value summaryValue(final Summary value) {
        return ValueSummary.create(value);
    }
    
    public abstract <T> T match(final Function<? super Double, T> p0, final Function<? super Long, T> p1, final Function<? super Distribution, T> p2, final Function<? super Summary, T> p3, final Function<? super Value, T> p4);
    
    @Immutable
    abstract static class ValueDouble extends Value
    {
        @Override
        public final <T> T match(final Function<? super Double, T> doubleFunction, final Function<? super Long, T> longFunction, final Function<? super Distribution, T> distributionFunction, final Function<? super Summary, T> summaryFunction, final Function<? super Value, T> defaultFunction) {
            return doubleFunction.apply(this.getValue());
        }
        
        static ValueDouble create(final double value) {
            return new AutoValue_Value_ValueDouble(value);
        }
        
        abstract double getValue();
    }
    
    @Immutable
    abstract static class ValueLong extends Value
    {
        @Override
        public final <T> T match(final Function<? super Double, T> doubleFunction, final Function<? super Long, T> longFunction, final Function<? super Distribution, T> distributionFunction, final Function<? super Summary, T> summaryFunction, final Function<? super Value, T> defaultFunction) {
            return longFunction.apply(this.getValue());
        }
        
        static ValueLong create(final long value) {
            return new AutoValue_Value_ValueLong(value);
        }
        
        abstract long getValue();
    }
    
    @Immutable
    abstract static class ValueDistribution extends Value
    {
        @Override
        public final <T> T match(final Function<? super Double, T> doubleFunction, final Function<? super Long, T> longFunction, final Function<? super Distribution, T> distributionFunction, final Function<? super Summary, T> summaryFunction, final Function<? super Value, T> defaultFunction) {
            return distributionFunction.apply(this.getValue());
        }
        
        static ValueDistribution create(final Distribution value) {
            return new AutoValue_Value_ValueDistribution(value);
        }
        
        abstract Distribution getValue();
    }
    
    @Immutable
    abstract static class ValueSummary extends Value
    {
        @Override
        public final <T> T match(final Function<? super Double, T> doubleFunction, final Function<? super Long, T> longFunction, final Function<? super Distribution, T> distributionFunction, final Function<? super Summary, T> summaryFunction, final Function<? super Value, T> defaultFunction) {
            return summaryFunction.apply(this.getValue());
        }
        
        static ValueSummary create(final Summary value) {
            return new AutoValue_Value_ValueSummary(value);
        }
        
        abstract Summary getValue();
    }
}
