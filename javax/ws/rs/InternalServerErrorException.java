package javax.ws.rs;

import javax.ws.rs.core.Response;

public class InternalServerErrorException extends ServerErrorException
{
    private static final long serialVersionUID = -6515710697540553309L;
    
    public InternalServerErrorException() {
        super(Response.Status.INTERNAL_SERVER_ERROR);
    }
    
    public InternalServerErrorException(final String message) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR);
    }
    
    public InternalServerErrorException(final Response response) {
        super(WebApplicationException.validate(response, Response.Status.INTERNAL_SERVER_ERROR));
    }
    
    public InternalServerErrorException(final String message, final Response response) {
        super(message, WebApplicationException.validate(response, Response.Status.INTERNAL_SERVER_ERROR));
    }
    
    public InternalServerErrorException(final Throwable cause) {
        super(Response.Status.INTERNAL_SERVER_ERROR, cause);
    }
    
    public InternalServerErrorException(final String message, final Throwable cause) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR, cause);
    }
    
    public InternalServerErrorException(final Response response, final Throwable cause) {
        super(WebApplicationException.validate(response, Response.Status.INTERNAL_SERVER_ERROR), cause);
    }
    
    public InternalServerErrorException(final String message, final Response response, final Throwable cause) {
        super(message, WebApplicationException.validate(response, Response.Status.INTERNAL_SERVER_ERROR), cause);
    }
}
