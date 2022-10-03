package jdk.jfr.consumer;

import jdk.jfr.ValueDescriptor;
import java.util.List;
import jdk.jfr.internal.Type;

public final class RecordedClass extends RecordedObject
{
    private final long uniqueId;
    
    static ObjectFactory<RecordedClass> createFactory(final Type type, final TimeConverter timeConverter) {
        return new ObjectFactory<RecordedClass>(type) {
            @Override
            RecordedClass createTyped(final List<ValueDescriptor> list, final long n, final Object[] array) {
                return new RecordedClass(list, n, array, timeConverter, null);
            }
        };
    }
    
    private RecordedClass(final List<ValueDescriptor> list, final long uniqueId, final Object[] array, final TimeConverter timeConverter) {
        super(list, array, timeConverter);
        this.uniqueId = uniqueId;
    }
    
    public int getModifiers() {
        return this.getTyped("modifiers", Integer.class, -1);
    }
    
    public RecordedClassLoader getClassLoader() {
        return this.getTyped("classLoader", RecordedClassLoader.class, null);
    }
    
    public String getName() {
        return this.getTyped("name", String.class, null).replace("/", ".");
    }
    
    public long getId() {
        return this.uniqueId;
    }
}
