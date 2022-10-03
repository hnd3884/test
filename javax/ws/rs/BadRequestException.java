package javax.ws.rs;

import javax.ws.rs.core.Response;

public class BadRequestException extends ClientErrorException
{
    private static final long serialVersionUID = 7264647684649480265L;
    
    public BadRequestException() {
        super(Response.Status.BAD_REQUEST);
    }
    
    public BadRequestException(final String message) {
        super(message, Response.Status.BAD_REQUEST);
    }
    
    public BadRequestException(final Response response) {
        super(WebApplicationException.validate(response, Response.Status.BAD_REQUEST));
    }
    
    public BadRequestException(final String message, final Response response) {
        super(message, WebApplicationException.validate(response, Response.Status.BAD_REQUEST));
    }
    
    public BadRequestException(final Throwable cause) {
        super(Response.Status.BAD_REQUEST, cause);
    }
    
    public BadRequestException(final String message, final Throwable cause) {
        super(message, Response.Status.BAD_REQUEST, cause);
    }
    
    public BadRequestException(final Response response, final Throwable cause) {
        super(WebApplicationException.validate(response, Response.Status.BAD_REQUEST), cause);
    }
    
    public BadRequestException(final String message, final Response response, final Throwable cause) {
        super(message, WebApplicationException.validate(response, Response.Status.BAD_REQUEST), cause);
    }
}
