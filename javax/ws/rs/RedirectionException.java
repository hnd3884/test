package javax.ws.rs;

import java.net.URI;
import javax.ws.rs.core.Response;

public class RedirectionException extends WebApplicationException
{
    private static final long serialVersionUID = -2584325408291098012L;
    
    public RedirectionException(final Response.Status status, final URI location) {
        super((Throwable)null, WebApplicationException.validate(Response.status(status).location(location).build(), Response.Status.Family.REDIRECTION));
    }
    
    public RedirectionException(final String message, final Response.Status status, final URI location) {
        super(message, null, WebApplicationException.validate(Response.status(status).location(location).build(), Response.Status.Family.REDIRECTION));
    }
    
    public RedirectionException(final int status, final URI location) {
        super((Throwable)null, WebApplicationException.validate(Response.status(status).location(location).build(), Response.Status.Family.REDIRECTION));
    }
    
    public RedirectionException(final String message, final int status, final URI location) {
        super(message, null, WebApplicationException.validate(Response.status(status).location(location).build(), Response.Status.Family.REDIRECTION));
    }
    
    public RedirectionException(final Response response) {
        super((Throwable)null, WebApplicationException.validate(response, Response.Status.Family.REDIRECTION));
    }
    
    public RedirectionException(final String message, final Response response) {
        super(message, null, WebApplicationException.validate(response, Response.Status.Family.REDIRECTION));
    }
    
    public URI getLocation() {
        return this.getResponse().getLocation();
    }
}
