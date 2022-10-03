package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import com.sun.xml.internal.ws.model.RuntimeModelerException;

class Util
{
    static String nullSafe(final String value) {
        return (value == null) ? "" : value;
    }
    
    static <T> T nullSafe(final T value, final T defaultValue) {
        return (value == null) ? defaultValue : value;
    }
    
    static <T extends Enum> T nullSafe(final Enum value, final T defaultValue) {
        return (T)((value == null) ? defaultValue : Enum.valueOf(defaultValue.getClass(), value.toString()));
    }
    
    public static Class<?> findClass(final String className) {
        try {
            return Class.forName(className);
        }
        catch (final ClassNotFoundException e) {
            throw new RuntimeModelerException("runtime.modeler.external.metadata.generic", new Object[] { e });
        }
    }
}
