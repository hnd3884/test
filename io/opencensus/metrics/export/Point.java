package io.opencensus.metrics.export;

import io.opencensus.common.Timestamp;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Point
{
    Point() {
    }
    
    public static Point create(final Value value, final Timestamp timestamp) {
        return new AutoValue_Point(value, timestamp);
    }
    
    public abstract Value getValue();
    
    public abstract Timestamp getTimestamp();
}
