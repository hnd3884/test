package javax.ejb;

public class AccessLocalException extends EJBException
{
    public AccessLocalException() {
    }
    
    public AccessLocalException(final String message) {
        super(message);
    }
    
    public AccessLocalException(final String message, final Exception ex) {
        super(message, ex);
    }
}
