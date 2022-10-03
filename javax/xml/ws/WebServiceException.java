package javax.xml.ws;

public class WebServiceException extends RuntimeException
{
    public WebServiceException() {
    }
    
    public WebServiceException(final String message) {
        super(message);
    }
    
    public WebServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public WebServiceException(final Throwable cause) {
        super(cause);
    }
}
