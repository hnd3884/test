package org.apache.commons.lang;

import java.util.Iterator;
import java.util.Map;
import java.util.Collection;

public class Validate
{
    public static void isTrue(final boolean expression, final String message, final Object value) {
        if (!expression) {
            throw new IllegalArgumentException(message + value);
        }
    }
    
    public static void isTrue(final boolean expression, final String message, final long value) {
        if (!expression) {
            throw new IllegalArgumentException(message + value);
        }
    }
    
    public static void isTrue(final boolean expression, final String message, final double value) {
        if (!expression) {
            throw new IllegalArgumentException(message + value);
        }
    }
    
    public static void isTrue(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void isTrue(final boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException("The validated expression is false");
        }
    }
    
    public static void notNull(final Object object, final String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notNull(final Object object) {
        if (object == null) {
            throw new IllegalArgumentException("The validated object is null");
        }
    }
    
    public static void notEmpty(final Object[] array, final String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(final Object[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("The validated array is empty");
        }
    }
    
    public static void notEmpty(final Collection collection, final String message) {
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(final Collection collection) {
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException("The validated collection is empty");
        }
    }
    
    public static void notEmpty(final Map map, final String message) {
        if (map == null || map.size() == 0) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(final Map map) {
        if (map == null || map.size() == 0) {
            throw new IllegalArgumentException("The validated map is empty");
        }
    }
    
    public static void notEmpty(final String string, final String message) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void notEmpty(final String string) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("The validated string is empty");
        }
    }
    
    public static void noNullElements(final Object[] array, final String message) {
        notNull(array);
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                throw new IllegalArgumentException(message);
            }
        }
    }
    
    public static void noNullElements(final Object[] array) {
        notNull(array);
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                throw new IllegalArgumentException("The validated array contains null element at index: " + i);
            }
        }
    }
    
    public static void noNullElements(final Collection collection, final String message) {
        notNull(collection);
        int i = 0;
        final Iterator it = collection.iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                throw new IllegalArgumentException(message);
            }
            ++i;
        }
    }
    
    public static void noNullElements(final Collection collection) {
        notNull(collection);
        int i = 0;
        final Iterator it = collection.iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                throw new IllegalArgumentException("The validated collection contains null element at index: " + i);
            }
            ++i;
        }
    }
}
