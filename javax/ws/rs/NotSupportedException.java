package javax.ws.rs;

import javax.ws.rs.core.Response;

public class NotSupportedException extends ClientErrorException
{
    private static final long serialVersionUID = -8286622745725405656L;
    
    public NotSupportedException() {
        super(Response.Status.UNSUPPORTED_MEDIA_TYPE);
    }
    
    public NotSupportedException(final String message) {
        super(message, Response.Status.UNSUPPORTED_MEDIA_TYPE);
    }
    
    public NotSupportedException(final Response response) {
        super(WebApplicationException.validate(response, Response.Status.UNSUPPORTED_MEDIA_TYPE));
    }
    
    public NotSupportedException(final String message, final Response response) {
        super(message, WebApplicationException.validate(response, Response.Status.UNSUPPORTED_MEDIA_TYPE));
    }
    
    public NotSupportedException(final Throwable cause) {
        super(Response.Status.UNSUPPORTED_MEDIA_TYPE, cause);
    }
    
    public NotSupportedException(final String message, final Throwable cause) {
        super(message, Response.Status.UNSUPPORTED_MEDIA_TYPE, cause);
    }
    
    public NotSupportedException(final Response response, final Throwable cause) {
        super(WebApplicationException.validate(response, Response.Status.UNSUPPORTED_MEDIA_TYPE), cause);
    }
    
    public NotSupportedException(final String message, final Response response, final Throwable cause) {
        super(message, WebApplicationException.validate(response, Response.Status.UNSUPPORTED_MEDIA_TYPE), cause);
    }
}
