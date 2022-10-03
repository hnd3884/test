package javax.ws.rs;

import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.ws.rs.core.Response;

public class NotAllowedException extends ClientErrorException
{
    private static final long serialVersionUID = -586776054369626119L;
    
    public NotAllowedException(final String allowed, final String... moreAllowed) {
        super(validateAllow(createNotAllowedResponse(allowed, moreAllowed)));
    }
    
    public NotAllowedException(final String message, final String allowed, final String... moreAllowed) {
        super(message, validateAllow(createNotAllowedResponse(allowed, moreAllowed)));
    }
    
    private static Response createNotAllowedResponse(final String allowed, final String... moreAllowed) {
        if (allowed == null) {
            throw new NullPointerException("No allowed method specified.");
        }
        Set<String> methods;
        if (moreAllowed != null && moreAllowed.length > 0) {
            methods = new HashSet<String>(moreAllowed.length + 1);
            methods.add(allowed);
            Collections.addAll(methods, moreAllowed);
        }
        else {
            methods = Collections.singleton(allowed);
        }
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).allow(methods).build();
    }
    
    public NotAllowedException(final Response response) {
        super(WebApplicationException.validate(response, Response.Status.METHOD_NOT_ALLOWED));
    }
    
    public NotAllowedException(final String message, final Response response) {
        super(message, WebApplicationException.validate(response, Response.Status.METHOD_NOT_ALLOWED));
    }
    
    public NotAllowedException(final Throwable cause, final String... allowedMethods) {
        super(validateAllow(Response.status(Response.Status.METHOD_NOT_ALLOWED).allow(allowedMethods).build()), cause);
    }
    
    public NotAllowedException(final String message, final Throwable cause, final String... allowedMethods) {
        super(message, validateAllow(Response.status(Response.Status.METHOD_NOT_ALLOWED).allow(allowedMethods).build()), cause);
    }
    
    public NotAllowedException(final Response response, final Throwable cause) {
        super(validateAllow(WebApplicationException.validate(response, Response.Status.METHOD_NOT_ALLOWED)), cause);
    }
    
    public NotAllowedException(final String message, final Response response, final Throwable cause) {
        super(message, validateAllow(WebApplicationException.validate(response, Response.Status.METHOD_NOT_ALLOWED)), cause);
    }
    
    private static Response validateAllow(final Response response) {
        if (!response.getHeaders().containsKey("Allow")) {
            throw new IllegalArgumentException("Response does not contain required 'Allow' HTTP header.");
        }
        return response;
    }
}
