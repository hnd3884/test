package jdk.jfr.consumer;

import jdk.jfr.internal.Type;
import jdk.jfr.ValueDescriptor;
import java.util.List;

abstract class ObjectFactory<T>
{
    static final String TYPE_PREFIX_VERSION_1 = "com.oracle.jfr.types.";
    static final String TYPE_PREFIX_VERSION_2 = "jdk.types.";
    static final String STACK_FRAME_VERSION_1 = "com.oracle.jfr.types.StackFrame";
    static final String STACK_FRAME_VERSION_2 = "jdk.types.StackFrame";
    private final List<ValueDescriptor> valueDescriptors;
    
    public static ObjectFactory<?> create(final Type type, final TimeConverter timeConverter) {
        final String name = type.getName();
        switch (name) {
            case "java.lang.Thread": {
                return RecordedThread.createFactory(type, timeConverter);
            }
            case "com.oracle.jfr.types.StackFrame":
            case "jdk.types.StackFrame": {
                return RecordedFrame.createFactory(type, timeConverter);
            }
            case "com.oracle.jfr.types.Method":
            case "jdk.types.Method": {
                return RecordedMethod.createFactory(type, timeConverter);
            }
            case "com.oracle.jfr.types.ThreadGroup":
            case "jdk.types.ThreadGroup": {
                return RecordedThreadGroup.createFactory(type, timeConverter);
            }
            case "com.oracle.jfr.types.StackTrace":
            case "jdk.types.StackTrace": {
                return RecordedStackTrace.createFactory(type, timeConverter);
            }
            case "com.oracle.jfr.types.ClassLoader":
            case "jdk.types.ClassLoader": {
                return RecordedClassLoader.createFactory(type, timeConverter);
            }
            case "java.lang.Class": {
                return RecordedClass.createFactory(type, timeConverter);
            }
            default: {
                return null;
            }
        }
    }
    
    ObjectFactory(final Type type) {
        this.valueDescriptors = type.getFields();
    }
    
    T createObject(final long n, final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Object[]) {
            return this.createTyped(this.valueDescriptors, n, (Object[])o);
        }
        throw new InternalError("Object factory must have struct type");
    }
    
    abstract T createTyped(final List<ValueDescriptor> p0, final long p1, final Object[] p2);
}
