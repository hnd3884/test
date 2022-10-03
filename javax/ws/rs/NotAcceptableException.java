package javax.ws.rs;

import javax.ws.rs.core.Response;

public class NotAcceptableException extends ClientErrorException
{
    private static final long serialVersionUID = -1476163816796529078L;
    
    public NotAcceptableException() {
        super(Response.Status.NOT_ACCEPTABLE);
    }
    
    public NotAcceptableException(final String message) {
        super(message, Response.Status.NOT_ACCEPTABLE);
    }
    
    public NotAcceptableException(final Response response) {
        super(WebApplicationException.validate(response, Response.Status.NOT_ACCEPTABLE));
    }
    
    public NotAcceptableException(final String message, final Response response) {
        super(message, WebApplicationException.validate(response, Response.Status.NOT_ACCEPTABLE));
    }
    
    public NotAcceptableException(final Throwable cause) {
        super(Response.Status.NOT_ACCEPTABLE, cause);
    }
    
    public NotAcceptableException(final String message, final Throwable cause) {
        super(message, Response.Status.NOT_ACCEPTABLE, cause);
    }
    
    public NotAcceptableException(final Response response, final Throwable cause) {
        super(WebApplicationException.validate(response, Response.Status.NOT_ACCEPTABLE), cause);
    }
    
    public NotAcceptableException(final String message, final Response response, final Throwable cause) {
        super(message, WebApplicationException.validate(response, Response.Status.NOT_ACCEPTABLE), cause);
    }
}
