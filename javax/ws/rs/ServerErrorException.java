package javax.ws.rs;

import javax.ws.rs.core.Response;

public class ServerErrorException extends WebApplicationException
{
    private static final long serialVersionUID = 4730895276505569556L;
    
    public ServerErrorException(final Response.Status status) {
        super((Throwable)null, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }
    
    public ServerErrorException(final String message, final Response.Status status) {
        super(message, null, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }
    
    public ServerErrorException(final int status) {
        super((Throwable)null, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }
    
    public ServerErrorException(final String message, final int status) {
        super(message, null, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }
    
    public ServerErrorException(final Response response) {
        super((Throwable)null, WebApplicationException.validate(response, Response.Status.Family.SERVER_ERROR));
    }
    
    public ServerErrorException(final String message, final Response response) {
        super(message, null, WebApplicationException.validate(response, Response.Status.Family.SERVER_ERROR));
    }
    
    public ServerErrorException(final Response.Status status, final Throwable cause) {
        super(cause, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }
    
    public ServerErrorException(final String message, final Response.Status status, final Throwable cause) {
        super(message, cause, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }
    
    public ServerErrorException(final int status, final Throwable cause) {
        super(cause, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }
    
    public ServerErrorException(final String message, final int status, final Throwable cause) {
        super(message, cause, WebApplicationException.validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }
    
    public ServerErrorException(final Response response, final Throwable cause) {
        super(cause, WebApplicationException.validate(response, Response.Status.Family.SERVER_ERROR));
    }
    
    public ServerErrorException(final String message, final Response response, final Throwable cause) {
        super(message, cause, WebApplicationException.validate(response, Response.Status.Family.SERVER_ERROR));
    }
}
