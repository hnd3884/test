package javax.ws.rs;

import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import javax.ws.rs.core.Response;
import java.util.List;

public class NotAuthorizedException extends ClientErrorException
{
    private static final long serialVersionUID = -3156040750581929702L;
    private transient List<Object> challenges;
    
    public NotAuthorizedException(final Object challenge, final Object... moreChallenges) {
        super(createUnauthorizedResponse(challenge, moreChallenges));
        this.challenges = cacheChallenges(challenge, moreChallenges);
    }
    
    public NotAuthorizedException(final String message, final Object challenge, final Object... moreChallenges) {
        super(message, createUnauthorizedResponse(challenge, moreChallenges));
        this.challenges = cacheChallenges(challenge, moreChallenges);
    }
    
    public NotAuthorizedException(final Response response) {
        super(WebApplicationException.validate(response, Response.Status.UNAUTHORIZED));
    }
    
    public NotAuthorizedException(final String message, final Response response) {
        super(message, WebApplicationException.validate(response, Response.Status.UNAUTHORIZED));
    }
    
    public NotAuthorizedException(final Throwable cause, final Object challenge, final Object... moreChallenges) {
        super(createUnauthorizedResponse(challenge, moreChallenges), cause);
        this.challenges = cacheChallenges(challenge, moreChallenges);
    }
    
    public NotAuthorizedException(final String message, final Throwable cause, final Object challenge, final Object... moreChallenges) {
        super(message, createUnauthorizedResponse(challenge, moreChallenges), cause);
        this.challenges = cacheChallenges(challenge, moreChallenges);
    }
    
    public NotAuthorizedException(final Response response, final Throwable cause) {
        super(WebApplicationException.validate(response, Response.Status.UNAUTHORIZED), cause);
    }
    
    public NotAuthorizedException(final String message, final Response response, final Throwable cause) {
        super(message, WebApplicationException.validate(response, Response.Status.UNAUTHORIZED), cause);
    }
    
    public List<Object> getChallenges() {
        if (this.challenges == null) {
            this.challenges = this.getResponse().getHeaders().get("WWW-Authenticate");
        }
        return this.challenges;
    }
    
    private static Response createUnauthorizedResponse(final Object challenge, final Object[] otherChallenges) {
        if (challenge == null) {
            throw new NullPointerException("Primary challenge parameter must not be null.");
        }
        final Response.ResponseBuilder builder = Response.status(Response.Status.UNAUTHORIZED).header("WWW-Authenticate", challenge);
        if (otherChallenges != null) {
            for (final Object oc : otherChallenges) {
                builder.header("WWW-Authenticate", oc);
            }
        }
        return builder.build();
    }
    
    private static List<Object> cacheChallenges(final Object challenge, final Object[] moreChallenges) {
        final List<Object> temp = new ArrayList<Object>(1 + ((moreChallenges == null) ? 0 : moreChallenges.length));
        temp.add(challenge);
        if (moreChallenges != null) {
            temp.addAll(Arrays.asList(moreChallenges));
        }
        return Collections.unmodifiableList((List<?>)temp);
    }
}
