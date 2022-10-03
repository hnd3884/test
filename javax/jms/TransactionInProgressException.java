package javax.jms;

public class TransactionInProgressException extends JMSException
{
    public TransactionInProgressException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public TransactionInProgressException(final String reason) {
        super(reason, (String)null);
    }
}
