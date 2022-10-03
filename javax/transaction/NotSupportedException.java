package javax.transaction;

public class NotSupportedException extends Exception
{
    public NotSupportedException() {
    }
    
    public NotSupportedException(final String msg) {
        super(msg);
    }
}
