package jdk.jfr.consumer;

import java.time.Duration;
import java.time.Instant;
import jdk.jfr.ValueDescriptor;
import java.util.List;
import jdk.jfr.EventType;

public final class RecordedEvent extends RecordedObject
{
    private final EventType eventType;
    private final long startTime;
    final long endTime;
    
    RecordedEvent(final EventType eventType, final List<ValueDescriptor> list, final Object[] array, final long startTime, final long endTime, final TimeConverter timeConverter) {
        super(list, array, timeConverter);
        this.eventType = eventType;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public RecordedStackTrace getStackTrace() {
        return this.getTyped("stackTrace", RecordedStackTrace.class, null);
    }
    
    public RecordedThread getThread() {
        return this.getTyped("eventThread", RecordedThread.class, null);
    }
    
    public EventType getEventType() {
        return this.eventType;
    }
    
    public Instant getStartTime() {
        return Instant.ofEpochSecond(0L, this.startTime);
    }
    
    public Instant getEndTime() {
        return Instant.ofEpochSecond(0L, this.endTime);
    }
    
    public Duration getDuration() {
        return Duration.ofNanos(this.endTime - this.startTime);
    }
    
    @Override
    public List<ValueDescriptor> getFields() {
        return this.getEventType().getFields();
    }
}
