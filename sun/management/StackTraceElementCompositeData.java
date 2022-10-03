package sun.management;

import java.lang.reflect.Type;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;

public class StackTraceElementCompositeData extends LazyCompositeData
{
    private final StackTraceElement ste;
    private static final CompositeType stackTraceElementCompositeType;
    private static final String CLASS_NAME = "className";
    private static final String METHOD_NAME = "methodName";
    private static final String FILE_NAME = "fileName";
    private static final String LINE_NUMBER = "lineNumber";
    private static final String NATIVE_METHOD = "nativeMethod";
    private static final String[] stackTraceElementItemNames;
    private static final long serialVersionUID = -2704607706598396827L;
    
    private StackTraceElementCompositeData(final StackTraceElement ste) {
        this.ste = ste;
    }
    
    public StackTraceElement getStackTraceElement() {
        return this.ste;
    }
    
    public static StackTraceElement from(final CompositeData compositeData) {
        validateCompositeData(compositeData);
        return new StackTraceElement(LazyCompositeData.getString(compositeData, "className"), LazyCompositeData.getString(compositeData, "methodName"), LazyCompositeData.getString(compositeData, "fileName"), LazyCompositeData.getInt(compositeData, "lineNumber"));
    }
    
    public static CompositeData toCompositeData(final StackTraceElement stackTraceElement) {
        return new StackTraceElementCompositeData(stackTraceElement).getCompositeData();
    }
    
    @Override
    protected CompositeData getCompositeData() {
        final Object[] array = { this.ste.getClassName(), this.ste.getMethodName(), this.ste.getFileName(), new Integer(this.ste.getLineNumber()), new Boolean(this.ste.isNativeMethod()) };
        try {
            return new CompositeDataSupport(StackTraceElementCompositeData.stackTraceElementCompositeType, StackTraceElementCompositeData.stackTraceElementItemNames, array);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    public static void validateCompositeData(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("Null CompositeData");
        }
        if (!LazyCompositeData.isTypeMatched(StackTraceElementCompositeData.stackTraceElementCompositeType, compositeData.getCompositeType())) {
            throw new IllegalArgumentException("Unexpected composite type for StackTraceElement");
        }
    }
    
    static {
        try {
            stackTraceElementCompositeType = (CompositeType)MappedMXBeanType.toOpenType(StackTraceElement.class);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
        stackTraceElementItemNames = new String[] { "className", "methodName", "fileName", "lineNumber", "nativeMethod" };
    }
}
