package javax.xml.ws;

public class ProtocolException extends WebServiceException
{
    public ProtocolException() {
    }
    
    public ProtocolException(final String message) {
        super(message);
    }
    
    public ProtocolException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ProtocolException(final Throwable cause) {
        super(cause);
    }
}
