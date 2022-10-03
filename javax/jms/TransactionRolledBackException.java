package javax.jms;

public class TransactionRolledBackException extends JMSException
{
    public TransactionRolledBackException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public TransactionRolledBackException(final String reason) {
        super(reason, (String)null);
    }
}
