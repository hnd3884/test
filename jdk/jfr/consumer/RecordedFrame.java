package jdk.jfr.consumer;

import jdk.jfr.ValueDescriptor;
import java.util.List;
import jdk.jfr.internal.Type;

public final class RecordedFrame extends RecordedObject
{
    static ObjectFactory<RecordedFrame> createFactory(final Type type, final TimeConverter timeConverter) {
        return new ObjectFactory<RecordedFrame>(type) {
            @Override
            RecordedFrame createTyped(final List<ValueDescriptor> list, final long n, final Object[] array) {
                return new RecordedFrame(list, array, timeConverter);
            }
        };
    }
    
    RecordedFrame(final List<ValueDescriptor> list, final Object[] array, final TimeConverter timeConverter) {
        super(list, array, timeConverter);
    }
    
    public boolean isJavaFrame() {
        return !this.hasField("javaFrame") || this.getTyped("javaFrame", Boolean.class, Boolean.TRUE);
    }
    
    public int getBytecodeIndex() {
        return this.getTyped("bytecodeIndex", Integer.class, -1);
    }
    
    public int getLineNumber() {
        return this.getTyped("lineNumber", Integer.class, -1);
    }
    
    public String getType() {
        return this.getTyped("type", String.class, null);
    }
    
    public RecordedMethod getMethod() {
        return this.getTyped("method", RecordedMethod.class, null);
    }
}
