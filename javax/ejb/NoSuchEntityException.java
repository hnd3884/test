package javax.ejb;

public class NoSuchEntityException extends EJBException
{
    public NoSuchEntityException() {
    }
    
    public NoSuchEntityException(final Exception ex) {
        super(ex);
    }
    
    public NoSuchEntityException(final String message) {
        super(message);
    }
}
