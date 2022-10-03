package jdk.jfr.consumer;

import jdk.jfr.ValueDescriptor;
import java.util.List;
import jdk.jfr.internal.Type;

public final class RecordedThreadGroup extends RecordedObject
{
    static ObjectFactory<RecordedThreadGroup> createFactory(final Type type, final TimeConverter timeConverter) {
        return new ObjectFactory<RecordedThreadGroup>(type) {
            @Override
            RecordedThreadGroup createTyped(final List<ValueDescriptor> list, final long n, final Object[] array) {
                return new RecordedThreadGroup(list, array, timeConverter, null);
            }
        };
    }
    
    private RecordedThreadGroup(final List<ValueDescriptor> list, final Object[] array, final TimeConverter timeConverter) {
        super(list, array, timeConverter);
    }
    
    public String getName() {
        return this.getTyped("name", String.class, null);
    }
    
    public RecordedThreadGroup getParent() {
        return this.getTyped("parent", RecordedThreadGroup.class, null);
    }
}
