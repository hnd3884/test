package javax.ws.rs;

import javax.ws.rs.core.Response;

public class ClientErrorException extends WebApplicationException
{
    private static final long serialVersionUID = -4101970664444907990L;
    
    public ClientErrorException(final Response.Status status) {
        super((Throwable)null, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }
    
    public ClientErrorException(final String message, final Response.Status status) {
        super(message, null, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }
    
    public ClientErrorException(final int status) {
        super((Throwable)null, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }
    
    public ClientErrorException(final String message, final int status) {
        super(message, null, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }
    
    public ClientErrorException(final Response response) {
        super((Throwable)null, WebApplicationException.validate(response, Response.Status.Family.CLIENT_ERROR));
    }
    
    public ClientErrorException(final String message, final Response response) {
        super(message, null, WebApplicationException.validate(response, Response.Status.Family.CLIENT_ERROR));
    }
    
    public ClientErrorException(final Response.Status status, final Throwable cause) {
        super(cause, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }
    
    public ClientErrorException(final String message, final Response.Status status, final Throwable cause) {
        super(message, cause, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }
    
    public ClientErrorException(final int status, final Throwable cause) {
        super(cause, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }
    
    public ClientErrorException(final String message, final int status, final Throwable cause) {
        super(message, cause, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }
    
    public ClientErrorException(final Response response, final Throwable cause) {
        super(cause, WebApplicationException.validate(response, Response.Status.Family.CLIENT_ERROR));
    }
    
    public ClientErrorException(final String message, final Response response, final Throwable cause) {
        super(message, cause, WebApplicationException.validate(response, Response.Status.Family.CLIENT_ERROR));
    }
}
