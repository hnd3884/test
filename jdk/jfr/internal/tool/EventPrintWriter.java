package jdk.jfr.internal.tool;

import jdk.jfr.Timestamp;
import jdk.jfr.Timespan;
import jdk.jfr.consumer.RecordedObject;
import java.io.IOException;
import java.io.FileNotFoundException;
import jdk.jfr.internal.consumer.RecordingInternals;
import jdk.jfr.consumer.RecordingFile;
import java.util.ArrayList;
import java.nio.file.Path;
import jdk.jfr.consumer.RecordedEvent;
import java.util.List;
import java.util.HashMap;
import java.io.PrintWriter;
import jdk.jfr.ValueDescriptor;
import java.util.Map;
import jdk.jfr.EventType;
import java.util.function.Predicate;

abstract class EventPrintWriter extends StructuredWriter
{
    protected static final String STACK_TRACE_FIELD = "stackTrace";
    protected static final String EVENT_THREAD_FIELD = "eventThread";
    private Predicate<EventType> eventFilter;
    private int stackDepth;
    private Map<ValueDescriptor, ValueType> typeOfValues;
    
    EventPrintWriter(final PrintWriter printWriter) {
        super(printWriter);
        this.eventFilter = (p0 -> true);
        this.typeOfValues = new HashMap<ValueDescriptor, ValueType>();
    }
    
    protected abstract void print(final List<RecordedEvent> p0);
    
    void print(final Path path) throws FileNotFoundException, IOException {
        final ArrayList list = new ArrayList(500000);
        this.printBegin();
        try (final RecordingFile recordingFile = new RecordingFile(path)) {
            while (recordingFile.hasMoreEvents()) {
                final RecordedEvent event = recordingFile.readEvent();
                if (this.acceptEvent(event)) {
                    list.add(event);
                }
                if (RecordingInternals.INSTANCE.isLastEventInChunk(recordingFile)) {
                    RecordingInternals.INSTANCE.sort(list);
                    this.print(list);
                    list.clear();
                }
            }
        }
        this.printEnd();
        this.flush(true);
    }
    
    protected void printEnd() {
    }
    
    protected void printBegin() {
    }
    
    public final void setEventFilter(final Predicate<EventType> eventFilter) {
        this.eventFilter = eventFilter;
    }
    
    protected final boolean acceptEvent(final RecordedEvent recordedEvent) {
        return this.eventFilter.test(recordedEvent.getEventType());
    }
    
    protected final int getStackDepth() {
        return this.stackDepth;
    }
    
    protected final boolean isLateField(final String s) {
        return s.equals("eventThread") || s.equals("stackTrace");
    }
    
    public void setStackDepth(final int stackDepth) {
        this.stackDepth = stackDepth;
    }
    
    protected Object getValue(final RecordedObject recordedObject, final ValueDescriptor valueDescriptor) {
        ValueType determineValueType = this.typeOfValues.get(valueDescriptor);
        if (determineValueType == null) {
            determineValueType = this.determineValueType(valueDescriptor);
            this.typeOfValues.put(valueDescriptor, determineValueType);
        }
        switch (determineValueType) {
            case TIMESPAN: {
                return recordedObject.getDuration(valueDescriptor.getName());
            }
            case TIMESTAMP: {
                return RecordingInternals.INSTANCE.getOffsetDataTime(recordedObject, valueDescriptor.getName());
            }
            default: {
                return recordedObject.getValue(valueDescriptor.getName());
            }
        }
    }
    
    private ValueType determineValueType(final ValueDescriptor valueDescriptor) {
        if (valueDescriptor.getAnnotation(Timespan.class) != null) {
            return ValueType.TIMESPAN;
        }
        if (valueDescriptor.getAnnotation(Timestamp.class) != null) {
            return ValueType.TIMESTAMP;
        }
        return ValueType.OTHER;
    }
    
    enum ValueType
    {
        TIMESPAN, 
        TIMESTAMP, 
        OTHER;
    }
}
