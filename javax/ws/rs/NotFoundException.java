package javax.ws.rs;

import javax.ws.rs.core.Response;

public class NotFoundException extends ClientErrorException
{
    private static final long serialVersionUID = -6820866117511628388L;
    
    public NotFoundException() {
        super(Response.Status.NOT_FOUND);
    }
    
    public NotFoundException(final String message) {
        super(message, Response.Status.NOT_FOUND);
    }
    
    public NotFoundException(final Response response) {
        super(WebApplicationException.validate(response, Response.Status.NOT_FOUND));
    }
    
    public NotFoundException(final String message, final Response response) {
        super(message, WebApplicationException.validate(response, Response.Status.NOT_FOUND));
    }
    
    public NotFoundException(final Throwable cause) {
        super(Response.Status.NOT_FOUND, cause);
    }
    
    public NotFoundException(final String message, final Throwable cause) {
        super(message, Response.Status.NOT_FOUND, cause);
    }
    
    public NotFoundException(final Response response, final Throwable cause) {
        super(WebApplicationException.validate(response, Response.Status.NOT_FOUND), cause);
    }
    
    public NotFoundException(final String message, final Response response, final Throwable cause) {
        super(message, WebApplicationException.validate(response, Response.Status.NOT_FOUND), cause);
    }
}
