package javax.ws.rs;

import javax.ws.rs.core.Response;

public class ForbiddenException extends ClientErrorException
{
    private static final long serialVersionUID = -2740045367479165061L;
    
    public ForbiddenException() {
        super(Response.Status.FORBIDDEN);
    }
    
    public ForbiddenException(final String message) {
        super(message, Response.Status.FORBIDDEN);
    }
    
    public ForbiddenException(final Response response) {
        super(WebApplicationException.validate(response, Response.Status.FORBIDDEN));
    }
    
    public ForbiddenException(final String message, final Response response) {
        super(message, WebApplicationException.validate(response, Response.Status.FORBIDDEN));
    }
    
    public ForbiddenException(final Throwable cause) {
        super(Response.Status.FORBIDDEN, cause);
    }
    
    public ForbiddenException(final String message, final Throwable cause) {
        super(message, Response.Status.FORBIDDEN, cause);
    }
    
    public ForbiddenException(final Response response, final Throwable cause) {
        super(WebApplicationException.validate(response, Response.Status.FORBIDDEN), cause);
    }
    
    public ForbiddenException(final String message, final Response response, final Throwable cause) {
        super(message, WebApplicationException.validate(response, Response.Status.FORBIDDEN), cause);
    }
}
