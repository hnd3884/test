package jdk.internal.dynalink;

public class NoSuchDynamicMethodException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public NoSuchDynamicMethodException(final String message) {
        super(message);
    }
}
