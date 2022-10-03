package jdk.jfr.consumer;

import java.io.IOException;
import jdk.jfr.internal.consumer.RecordingInput;
import jdk.jfr.ValueDescriptor;
import java.util.List;
import jdk.jfr.EventType;

final class EventParser extends Parser
{
    private final Parser[] parsers;
    private final EventType eventType;
    private final TimeConverter timeConverter;
    private final boolean hasDuration;
    private final List<ValueDescriptor> valueDescriptors;
    
    EventParser(final TimeConverter timeConverter, final EventType eventType, final Parser[] parsers) {
        this.timeConverter = timeConverter;
        this.parsers = parsers;
        this.eventType = eventType;
        this.hasDuration = (eventType.getField("duration") != null);
        this.valueDescriptors = eventType.getFields();
    }
    
    public Object parse(final RecordingInput recordingInput) throws IOException {
        final Object[] array = new Object[this.parsers.length];
        for (int i = 0; i < this.parsers.length; ++i) {
            array[i] = this.parsers[i].parse(recordingInput);
        }
        final Long n = (Long)array[0];
        final long convertTimestamp = this.timeConverter.convertTimestamp(n);
        if (this.hasDuration) {
            return new RecordedEvent(this.eventType, this.valueDescriptors, array, convertTimestamp, this.timeConverter.convertTimestamp(n + (long)array[1]), this.timeConverter);
        }
        return new RecordedEvent(this.eventType, this.valueDescriptors, array, convertTimestamp, convertTimestamp, this.timeConverter);
    }
}
