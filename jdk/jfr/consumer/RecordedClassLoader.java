package jdk.jfr.consumer;

import jdk.jfr.ValueDescriptor;
import java.util.List;
import jdk.jfr.internal.Type;

public final class RecordedClassLoader extends RecordedObject
{
    private final long uniqueId;
    
    static ObjectFactory<RecordedClassLoader> createFactory(final Type type, final TimeConverter timeConverter) {
        return new ObjectFactory<RecordedClassLoader>(type) {
            @Override
            RecordedClassLoader createTyped(final List<ValueDescriptor> list, final long n, final Object[] array) {
                return new RecordedClassLoader(list, n, array, timeConverter, null);
            }
        };
    }
    
    private RecordedClassLoader(final List<ValueDescriptor> list, final long uniqueId, final Object[] array, final TimeConverter timeConverter) {
        super(list, array, timeConverter);
        this.uniqueId = uniqueId;
    }
    
    public RecordedClass getType() {
        return this.getTyped("type", RecordedClass.class, null);
    }
    
    public String getName() {
        return this.getTyped("name", String.class, null);
    }
    
    public long getId() {
        return this.uniqueId;
    }
}
