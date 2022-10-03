package javax.ws.rs.client;

import javax.ws.rs.core.Response;
import javax.ws.rs.ProcessingException;

public class ResponseProcessingException extends ProcessingException
{
    private static final long serialVersionUID = -4923161617935731839L;
    private final Response response;
    
    public ResponseProcessingException(final Response response, final Throwable cause) {
        super(cause);
        this.response = response;
    }
    
    public ResponseProcessingException(final Response response, final String message, final Throwable cause) {
        super(message, cause);
        this.response = response;
    }
    
    public ResponseProcessingException(final Response response, final String message) {
        super(message);
        this.response = response;
    }
    
    public Response getResponse() {
        return this.response;
    }
}
