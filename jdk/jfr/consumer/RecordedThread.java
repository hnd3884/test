package jdk.jfr.consumer;

import jdk.jfr.ValueDescriptor;
import java.util.List;
import jdk.jfr.internal.Type;

public final class RecordedThread extends RecordedObject
{
    private final long uniqueId;
    
    static ObjectFactory<RecordedThread> createFactory(final Type type, final TimeConverter timeConverter) {
        return new ObjectFactory<RecordedThread>(type) {
            @Override
            RecordedThread createTyped(final List<ValueDescriptor> list, final long n, final Object[] array) {
                return new RecordedThread(list, n, array, timeConverter, null);
            }
        };
    }
    
    private RecordedThread(final List<ValueDescriptor> list, final long uniqueId, final Object[] array, final TimeConverter timeConverter) {
        super(list, array, timeConverter);
        this.uniqueId = uniqueId;
    }
    
    public String getOSName() {
        return this.getTyped("osName", String.class, null);
    }
    
    public long getOSThreadId() {
        return this.getTyped("osThreadId", Long.class, -1L);
    }
    
    public RecordedThreadGroup getThreadGroup() {
        return this.getTyped("group", RecordedThreadGroup.class, null);
    }
    
    public String getJavaName() {
        return this.getTyped("javaName", String.class, null);
    }
    
    public long getJavaThreadId() {
        return this.getTyped("javaThreadId", Long.class, -1L);
    }
    
    public long getId() {
        return this.uniqueId;
    }
}
