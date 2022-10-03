package jdk.jfr.consumer;

import jdk.jfr.ValueDescriptor;
import java.util.List;
import jdk.jfr.internal.Type;

public final class RecordedMethod extends RecordedObject
{
    static ObjectFactory<RecordedMethod> createFactory(final Type type, final TimeConverter timeConverter) {
        return new ObjectFactory<RecordedMethod>(type) {
            @Override
            RecordedMethod createTyped(final List<ValueDescriptor> list, final long n, final Object[] array) {
                return new RecordedMethod(list, array, timeConverter, null);
            }
        };
    }
    
    private RecordedMethod(final List<ValueDescriptor> list, final Object[] array, final TimeConverter timeConverter) {
        super(list, array, timeConverter);
    }
    
    public RecordedClass getType() {
        return this.getTyped("type", RecordedClass.class, null);
    }
    
    public String getName() {
        return this.getTyped("name", String.class, null);
    }
    
    public String getDescriptor() {
        return this.getTyped("descriptor", String.class, null);
    }
    
    public int getModifiers() {
        return this.getTyped("modifiers", Integer.class, 0);
    }
    
    public boolean isHidden() {
        return this.getTyped("hidden", Boolean.class, Boolean.FALSE);
    }
}
