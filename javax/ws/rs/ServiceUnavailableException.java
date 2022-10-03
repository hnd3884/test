package javax.ws.rs;

import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Date;
import javax.ws.rs.core.Response;

public class ServiceUnavailableException extends ServerErrorException
{
    private static final long serialVersionUID = 3821068205617492633L;
    
    public ServiceUnavailableException() {
        super(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
    }
    
    public ServiceUnavailableException(final String message) {
        super(message, Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
    }
    
    public ServiceUnavailableException(final Long retryAfter) {
        super(Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Retry-After", retryAfter).build());
    }
    
    public ServiceUnavailableException(final String message, final Long retryAfter) {
        super(message, Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Retry-After", retryAfter).build());
    }
    
    public ServiceUnavailableException(final Date retryAfter) {
        super(Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Retry-After", retryAfter).build());
    }
    
    public ServiceUnavailableException(final String message, final Date retryAfter) {
        super(message, Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Retry-After", retryAfter).build());
    }
    
    public ServiceUnavailableException(final Response response) {
        super(WebApplicationException.validate(response, Response.Status.SERVICE_UNAVAILABLE));
    }
    
    public ServiceUnavailableException(final String message, final Response response) {
        super(message, WebApplicationException.validate(response, Response.Status.SERVICE_UNAVAILABLE));
    }
    
    public ServiceUnavailableException(final Date retryAfter, final Throwable cause) {
        super(Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Retry-After", retryAfter).build(), cause);
    }
    
    public ServiceUnavailableException(final String message, final Date retryAfter, final Throwable cause) {
        super(message, Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Retry-After", retryAfter).build(), cause);
    }
    
    public ServiceUnavailableException(final Long retryAfter, final Throwable cause) {
        super(Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Retry-After", retryAfter).build(), cause);
    }
    
    public ServiceUnavailableException(final String message, final Long retryAfter, final Throwable cause) {
        super(message, Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Retry-After", retryAfter).build(), cause);
    }
    
    public ServiceUnavailableException(final Response response, final Throwable cause) {
        super(WebApplicationException.validate(response, Response.Status.SERVICE_UNAVAILABLE), cause);
    }
    
    public ServiceUnavailableException(final String message, final Response response, final Throwable cause) {
        super(message, WebApplicationException.validate(response, Response.Status.SERVICE_UNAVAILABLE), cause);
    }
    
    public boolean hasRetryAfter() {
        return this.getResponse().getHeaders().containsKey("Retry-After");
    }
    
    public Date getRetryTime(final Date requestTime) {
        final String value = this.getResponse().getHeaderString("Retry-After");
        if (value == null) {
            return null;
        }
        try {
            final Long interval = Long.parseLong(value);
            return new Date(requestTime.getTime() + interval * 1000L);
        }
        catch (final NumberFormatException ex) {
            final RuntimeDelegate.HeaderDelegate<Date> dateDelegate = RuntimeDelegate.getInstance().createHeaderDelegate(Date.class);
            return dateDelegate.fromString(value);
        }
    }
}
