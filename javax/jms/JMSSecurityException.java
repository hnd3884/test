package javax.jms;

public class JMSSecurityException extends JMSException
{
    public JMSSecurityException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public JMSSecurityException(final String reason) {
        super(reason, (String)null);
    }
}
