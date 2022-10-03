package jdk.jfr.consumer;

import java.util.Arrays;
import java.util.Collections;
import jdk.jfr.ValueDescriptor;
import java.util.List;
import jdk.jfr.internal.Type;

public final class RecordedStackTrace extends RecordedObject
{
    static ObjectFactory<RecordedStackTrace> createFactory(final Type type, final TimeConverter timeConverter) {
        return new ObjectFactory<RecordedStackTrace>(type) {
            @Override
            RecordedStackTrace createTyped(final List<ValueDescriptor> list, final long n, final Object[] array) {
                return new RecordedStackTrace(list, array, timeConverter, null);
            }
        };
    }
    
    private RecordedStackTrace(final List<ValueDescriptor> list, final Object[] array, final TimeConverter timeConverter) {
        super(list, array, timeConverter);
    }
    
    public List<RecordedFrame> getFrames() {
        final Object[] array = this.getTyped("frames", (Class<RecordedFrame[]>)Object[].class, (RecordedFrame[])null);
        if (array == null) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList((RecordedFrame[])array);
    }
    
    public boolean isTruncated() {
        return this.getTyped("truncated", Boolean.class, true);
    }
}
