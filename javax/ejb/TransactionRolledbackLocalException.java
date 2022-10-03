package javax.ejb;

public class TransactionRolledbackLocalException extends EJBException
{
    public TransactionRolledbackLocalException() {
    }
    
    public TransactionRolledbackLocalException(final String message) {
        super(message);
    }
    
    public TransactionRolledbackLocalException(final String message, final Exception ex) {
        super(message, ex);
    }
}
