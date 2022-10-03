package io.opencensus.trace.export;

import io.opencensus.common.Timestamp;

final class AutoValue_SpanData_TimedEvent<T> extends SpanData.TimedEvent<T>
{
    private final Timestamp timestamp;
    private final T event;
    
    AutoValue_SpanData_TimedEvent(final Timestamp timestamp, final T event) {
        if (timestamp == null) {
            throw new NullPointerException("Null timestamp");
        }
        this.timestamp = timestamp;
        if (event == null) {
            throw new NullPointerException("Null event");
        }
        this.event = event;
    }
    
    @Override
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    
    @Override
    public T getEvent() {
        return this.event;
    }
    
    @Override
    public String toString() {
        return "TimedEvent{timestamp=" + this.timestamp + ", event=" + this.event + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SpanData.TimedEvent) {
            final SpanData.TimedEvent<?> that = (SpanData.TimedEvent<?>)o;
            return this.timestamp.equals(that.getTimestamp()) && this.event.equals(that.getEvent());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.timestamp.hashCode();
        h *= 1000003;
        h ^= this.event.hashCode();
        return h;
    }
}
