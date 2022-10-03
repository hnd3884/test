package io.opencensus.stats;

import io.opencensus.common.Function;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Measurement
{
    public abstract <T> T match(final Function<? super MeasurementDouble, T> p0, final Function<? super MeasurementLong, T> p1, final Function<? super Measurement, T> p2);
    
    public abstract Measure getMeasure();
    
    private Measurement() {
    }
    
    @Immutable
    public abstract static class MeasurementDouble extends Measurement
    {
        MeasurementDouble() {
            super(null);
        }
        
        public static MeasurementDouble create(final Measure.MeasureDouble measure, final double value) {
            return new AutoValue_Measurement_MeasurementDouble(measure, value);
        }
        
        @Override
        public abstract Measure.MeasureDouble getMeasure();
        
        public abstract double getValue();
        
        @Override
        public <T> T match(final Function<? super MeasurementDouble, T> p0, final Function<? super MeasurementLong, T> p1, final Function<? super Measurement, T> defaultFunction) {
            return p0.apply(this);
        }
    }
    
    @Immutable
    public abstract static class MeasurementLong extends Measurement
    {
        MeasurementLong() {
            super(null);
        }
        
        public static MeasurementLong create(final Measure.MeasureLong measure, final long value) {
            return new AutoValue_Measurement_MeasurementLong(measure, value);
        }
        
        @Override
        public abstract Measure.MeasureLong getMeasure();
        
        public abstract long getValue();
        
        @Override
        public <T> T match(final Function<? super MeasurementDouble, T> p0, final Function<? super MeasurementLong, T> p1, final Function<? super Measurement, T> defaultFunction) {
            return p1.apply(this);
        }
    }
}
