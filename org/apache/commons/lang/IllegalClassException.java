package org.apache.commons.lang;

public class IllegalClassException extends IllegalArgumentException
{
    public IllegalClassException(final Class expected, final Class actual) {
        super("Expected: " + safeGetClassName(expected) + ", actual: " + safeGetClassName(actual));
    }
    
    public IllegalClassException(final String message) {
        super(message);
    }
    
    private static final String safeGetClassName(final Class cls) {
        return (cls == null) ? null : cls.getName();
    }
}
