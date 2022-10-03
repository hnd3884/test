package io.opencensus.stats;

import io.opencensus.internal.Utils;
import io.opencensus.internal.StringUtils;
import io.opencensus.common.Function;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Measure
{
    static final int NAME_MAX_LENGTH = 255;
    private static final String ERROR_MESSAGE_INVALID_NAME = "Name should be a ASCII string with a length no greater than 255 characters.";
    
    public abstract <T> T match(final Function<? super MeasureDouble, T> p0, final Function<? super MeasureLong, T> p1, final Function<? super Measure, T> p2);
    
    public abstract String getName();
    
    public abstract String getDescription();
    
    public abstract String getUnit();
    
    private Measure() {
    }
    
    @Immutable
    public abstract static class MeasureDouble extends Measure
    {
        MeasureDouble() {
            super(null);
        }
        
        public static MeasureDouble create(final String name, final String description, final String unit) {
            Utils.checkArgument(StringUtils.isPrintableString(name) && name.length() <= 255, (Object)"Name should be a ASCII string with a length no greater than 255 characters.");
            return new AutoValue_Measure_MeasureDouble(name, description, unit);
        }
        
        @Override
        public <T> T match(final Function<? super MeasureDouble, T> p0, final Function<? super MeasureLong, T> p1, final Function<? super Measure, T> defaultFunction) {
            return p0.apply(this);
        }
        
        @Override
        public abstract String getName();
        
        @Override
        public abstract String getDescription();
        
        @Override
        public abstract String getUnit();
    }
    
    @Immutable
    public abstract static class MeasureLong extends Measure
    {
        MeasureLong() {
            super(null);
        }
        
        public static MeasureLong create(final String name, final String description, final String unit) {
            Utils.checkArgument(StringUtils.isPrintableString(name) && name.length() <= 255, (Object)"Name should be a ASCII string with a length no greater than 255 characters.");
            return new AutoValue_Measure_MeasureLong(name, description, unit);
        }
        
        @Override
        public <T> T match(final Function<? super MeasureDouble, T> p0, final Function<? super MeasureLong, T> p1, final Function<? super Measure, T> defaultFunction) {
            return p1.apply(this);
        }
        
        @Override
        public abstract String getName();
        
        @Override
        public abstract String getDescription();
        
        @Override
        public abstract String getUnit();
    }
}
