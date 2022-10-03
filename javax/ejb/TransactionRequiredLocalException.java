package javax.ejb;

public class TransactionRequiredLocalException extends EJBException
{
    public TransactionRequiredLocalException() {
    }
    
    public TransactionRequiredLocalException(final String message) {
        super(message);
    }
}
