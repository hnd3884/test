package javax.print.attribute;

public class UnmodifiableSetException extends RuntimeException
{
    public UnmodifiableSetException() {
    }
    
    public UnmodifiableSetException(final String s) {
        super(s);
    }
}
