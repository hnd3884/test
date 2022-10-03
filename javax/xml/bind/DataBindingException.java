package javax.xml.bind;

public class DataBindingException extends RuntimeException
{
    public DataBindingException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public DataBindingException(final Throwable cause) {
        super(cause);
    }
}
