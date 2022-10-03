package javax.ejb;

public class NoSuchObjectLocalException extends EJBException
{
    public NoSuchObjectLocalException() {
    }
    
    public NoSuchObjectLocalException(final String message) {
        super(message);
    }
    
    public NoSuchObjectLocalException(final String message, final Exception ex) {
        super(message, ex);
    }
}
