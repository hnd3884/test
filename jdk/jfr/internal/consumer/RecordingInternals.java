package jdk.jfr.internal.consumer;

import jdk.jfr.consumer.RecordedEvent;
import java.io.IOException;
import jdk.jfr.internal.Type;
import java.util.List;
import jdk.jfr.consumer.RecordedObject;
import jdk.jfr.consumer.RecordingFile;

public abstract class RecordingInternals
{
    public static RecordingInternals INSTANCE;
    
    public abstract boolean isLastEventInChunk(final RecordingFile p0);
    
    public abstract Object getOffsetDataTime(final RecordedObject p0, final String p1);
    
    public abstract List<Type> readTypes(final RecordingFile p0) throws IOException;
    
    public abstract void sort(final List<RecordedEvent> p0);
}
