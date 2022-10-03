package jdk.internal.util;

public final class StaticProperty
{
    private static final String JDK_SERIAL_FILTER;
    
    private StaticProperty() {
    }
    
    public static String jdkSerialFilter() {
        return StaticProperty.JDK_SERIAL_FILTER;
    }
    
    static {
        JDK_SERIAL_FILTER = System.getProperty("jdk.serialFilter");
    }
}
