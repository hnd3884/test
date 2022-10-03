package io.opencensus.metrics.export;

import io.opencensus.common.Timestamp;

final class AutoValue_Point extends Point
{
    private final Value value;
    private final Timestamp timestamp;
    
    AutoValue_Point(final Value value, final Timestamp timestamp) {
        if (value == null) {
            throw new NullPointerException("Null value");
        }
        this.value = value;
        if (timestamp == null) {
            throw new NullPointerException("Null timestamp");
        }
        this.timestamp = timestamp;
    }
    
    @Override
    public Value getValue() {
        return this.value;
    }
    
    @Override
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    
    @Override
    public String toString() {
        return "Point{value=" + this.value + ", timestamp=" + this.timestamp + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Point) {
            final Point that = (Point)o;
            return this.value.equals(that.getValue()) && this.timestamp.equals(that.getTimestamp());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.value.hashCode();
        h *= 1000003;
        h ^= this.timestamp.hashCode();
        return h;
    }
}
