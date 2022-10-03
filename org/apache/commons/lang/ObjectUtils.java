package org.apache.commons.lang;

import java.io.Serializable;

public class ObjectUtils
{
    public static final Null NULL;
    
    public static Object defaultIfNull(final Object object, final Object defaultValue) {
        return (object != null) ? object : defaultValue;
    }
    
    public static boolean equals(final Object object1, final Object object2) {
        return object1 == object2 || (object1 != null && object2 != null && object1.equals(object2));
    }
    
    public static String identityToString(final Object object) {
        if (object == null) {
            return null;
        }
        return appendIdentityToString(null, object).toString();
    }
    
    public static StringBuffer appendIdentityToString(StringBuffer buffer, final Object object) {
        if (object == null) {
            return null;
        }
        if (buffer == null) {
            buffer = new StringBuffer();
        }
        return buffer.append(object.getClass().getName()).append('@').append(Integer.toHexString(System.identityHashCode(object)));
    }
    
    public static String toString(final Object obj) {
        return (obj == null) ? "" : obj.toString();
    }
    
    public static String toString(final Object obj, final String nullStr) {
        return (obj == null) ? nullStr : obj.toString();
    }
    
    static {
        NULL = new Null();
    }
    
    public static class Null implements Serializable
    {
        private static final long serialVersionUID = 7092611880189329093L;
        
        Null() {
        }
        
        private Object readResolve() {
            return ObjectUtils.NULL;
        }
    }
}
