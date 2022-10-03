package io.opencensus.trace.export;

import java.util.List;

final class AutoValue_SpanData_TimedEvents<T> extends SpanData.TimedEvents<T>
{
    private final List<SpanData.TimedEvent<T>> events;
    private final int droppedEventsCount;
    
    AutoValue_SpanData_TimedEvents(final List<SpanData.TimedEvent<T>> events, final int droppedEventsCount) {
        if (events == null) {
            throw new NullPointerException("Null events");
        }
        this.events = events;
        this.droppedEventsCount = droppedEventsCount;
    }
    
    @Override
    public List<SpanData.TimedEvent<T>> getEvents() {
        return this.events;
    }
    
    @Override
    public int getDroppedEventsCount() {
        return this.droppedEventsCount;
    }
    
    @Override
    public String toString() {
        return "TimedEvents{events=" + this.events + ", droppedEventsCount=" + this.droppedEventsCount + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SpanData.TimedEvents) {
            final SpanData.TimedEvents<?> that = (SpanData.TimedEvents<?>)o;
            return this.events.equals(that.getEvents()) && this.droppedEventsCount == that.getDroppedEventsCount();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.events.hashCode();
        h *= 1000003;
        h ^= this.droppedEventsCount;
        return h;
    }
}
