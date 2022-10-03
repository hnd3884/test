package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Iterator;
import java.util.List;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Locale;
import java.lang.annotation.Annotation;

public abstract class Response implements AutoCloseable
{
    protected Response() {
    }
    
    public abstract int getStatus();
    
    public abstract StatusType getStatusInfo();
    
    public abstract Object getEntity();
    
    public abstract <T> T readEntity(final Class<T> p0);
    
    public abstract <T> T readEntity(final GenericType<T> p0);
    
    public abstract <T> T readEntity(final Class<T> p0, final Annotation[] p1);
    
    public abstract <T> T readEntity(final GenericType<T> p0, final Annotation[] p1);
    
    public abstract boolean hasEntity();
    
    public abstract boolean bufferEntity();
    
    @Override
    public abstract void close();
    
    public abstract MediaType getMediaType();
    
    public abstract Locale getLanguage();
    
    public abstract int getLength();
    
    public abstract Set<String> getAllowedMethods();
    
    public abstract Map<String, NewCookie> getCookies();
    
    public abstract EntityTag getEntityTag();
    
    public abstract Date getDate();
    
    public abstract Date getLastModified();
    
    public abstract URI getLocation();
    
    public abstract Set<Link> getLinks();
    
    public abstract boolean hasLink(final String p0);
    
    public abstract Link getLink(final String p0);
    
    public abstract Link.Builder getLinkBuilder(final String p0);
    
    public abstract MultivaluedMap<String, Object> getMetadata();
    
    public MultivaluedMap<String, Object> getHeaders() {
        return this.getMetadata();
    }
    
    public abstract MultivaluedMap<String, String> getStringHeaders();
    
    public abstract String getHeaderString(final String p0);
    
    public static ResponseBuilder fromResponse(final Response response) {
        final ResponseBuilder b = status(response.getStatus());
        if (response.hasEntity()) {
            b.entity(response.getEntity());
        }
        for (final String headerName : response.getHeaders().keySet()) {
            final List<Object> headerValues = response.getHeaders().get(headerName);
            for (final Object headerValue : headerValues) {
                b.header(headerName, headerValue);
            }
        }
        return b;
    }
    
    public static ResponseBuilder status(final StatusType status) {
        return ResponseBuilder.newInstance().status(status);
    }
    
    public static ResponseBuilder status(final Status status) {
        return status((StatusType)status);
    }
    
    public static ResponseBuilder status(final int status) {
        return ResponseBuilder.newInstance().status(status);
    }
    
    public static ResponseBuilder status(final int status, final String reasonPhrase) {
        return ResponseBuilder.newInstance().status(status, reasonPhrase);
    }
    
    public static ResponseBuilder ok() {
        return status(Status.OK);
    }
    
    public static ResponseBuilder ok(final Object entity) {
        final ResponseBuilder b = ok();
        b.entity(entity);
        return b;
    }
    
    public static ResponseBuilder ok(final Object entity, final MediaType type) {
        return ok().entity(entity).type(type);
    }
    
    public static ResponseBuilder ok(final Object entity, final String type) {
        return ok().entity(entity).type(type);
    }
    
    public static ResponseBuilder ok(final Object entity, final Variant variant) {
        return ok().entity(entity).variant(variant);
    }
    
    public static ResponseBuilder serverError() {
        return status(Status.INTERNAL_SERVER_ERROR);
    }
    
    public static ResponseBuilder created(final URI location) {
        return status(Status.CREATED).location(location);
    }
    
    public static ResponseBuilder accepted() {
        return status(Status.ACCEPTED);
    }
    
    public static ResponseBuilder accepted(final Object entity) {
        return accepted().entity(entity);
    }
    
    public static ResponseBuilder noContent() {
        return status(Status.NO_CONTENT);
    }
    
    public static ResponseBuilder notModified() {
        return status(Status.NOT_MODIFIED);
    }
    
    public static ResponseBuilder notModified(final EntityTag tag) {
        return notModified().tag(tag);
    }
    
    public static ResponseBuilder notModified(final String tag) {
        return notModified().tag(tag);
    }
    
    public static ResponseBuilder seeOther(final URI location) {
        return status(Status.SEE_OTHER).location(location);
    }
    
    public static ResponseBuilder temporaryRedirect(final URI location) {
        return status(Status.TEMPORARY_REDIRECT).location(location);
    }
    
    public static ResponseBuilder notAcceptable(final List<Variant> variants) {
        return status(Status.NOT_ACCEPTABLE).variants(variants);
    }
    
    public abstract static class ResponseBuilder
    {
        protected ResponseBuilder() {
        }
        
        protected static ResponseBuilder newInstance() {
            return RuntimeDelegate.getInstance().createResponseBuilder();
        }
        
        public abstract Response build();
        
        public abstract ResponseBuilder clone();
        
        public abstract ResponseBuilder status(final int p0);
        
        public abstract ResponseBuilder status(final int p0, final String p1);
        
        public ResponseBuilder status(final StatusType status) {
            if (status == null) {
                throw new IllegalArgumentException();
            }
            return this.status(status.getStatusCode(), status.getReasonPhrase());
        }
        
        public ResponseBuilder status(final Status status) {
            return this.status((StatusType)status);
        }
        
        public abstract ResponseBuilder entity(final Object p0);
        
        public abstract ResponseBuilder entity(final Object p0, final Annotation[] p1);
        
        public abstract ResponseBuilder allow(final String... p0);
        
        public abstract ResponseBuilder allow(final Set<String> p0);
        
        public abstract ResponseBuilder cacheControl(final CacheControl p0);
        
        public abstract ResponseBuilder encoding(final String p0);
        
        public abstract ResponseBuilder header(final String p0, final Object p1);
        
        public abstract ResponseBuilder replaceAll(final MultivaluedMap<String, Object> p0);
        
        public abstract ResponseBuilder language(final String p0);
        
        public abstract ResponseBuilder language(final Locale p0);
        
        public abstract ResponseBuilder type(final MediaType p0);
        
        public abstract ResponseBuilder type(final String p0);
        
        public abstract ResponseBuilder variant(final Variant p0);
        
        public abstract ResponseBuilder contentLocation(final URI p0);
        
        public abstract ResponseBuilder cookie(final NewCookie... p0);
        
        public abstract ResponseBuilder expires(final Date p0);
        
        public abstract ResponseBuilder lastModified(final Date p0);
        
        public abstract ResponseBuilder location(final URI p0);
        
        public abstract ResponseBuilder tag(final EntityTag p0);
        
        public abstract ResponseBuilder tag(final String p0);
        
        public abstract ResponseBuilder variants(final Variant... p0);
        
        public abstract ResponseBuilder variants(final List<Variant> p0);
        
        public abstract ResponseBuilder links(final Link... p0);
        
        public abstract ResponseBuilder link(final URI p0, final String p1);
        
        public abstract ResponseBuilder link(final String p0, final String p1);
    }
    
    public interface StatusType
    {
        int getStatusCode();
        
        Status.Family getFamily();
        
        String getReasonPhrase();
        
        default Status toEnum() {
            return Status.fromStatusCode(this.getStatusCode());
        }
    }
    
    public enum Status implements StatusType
    {
        OK(200, "OK"), 
        CREATED(201, "Created"), 
        ACCEPTED(202, "Accepted"), 
        NO_CONTENT(204, "No Content"), 
        RESET_CONTENT(205, "Reset Content"), 
        PARTIAL_CONTENT(206, "Partial Content"), 
        MOVED_PERMANENTLY(301, "Moved Permanently"), 
        FOUND(302, "Found"), 
        SEE_OTHER(303, "See Other"), 
        NOT_MODIFIED(304, "Not Modified"), 
        USE_PROXY(305, "Use Proxy"), 
        TEMPORARY_REDIRECT(307, "Temporary Redirect"), 
        BAD_REQUEST(400, "Bad Request"), 
        UNAUTHORIZED(401, "Unauthorized"), 
        PAYMENT_REQUIRED(402, "Payment Required"), 
        FORBIDDEN(403, "Forbidden"), 
        NOT_FOUND(404, "Not Found"), 
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"), 
        NOT_ACCEPTABLE(406, "Not Acceptable"), 
        PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"), 
        REQUEST_TIMEOUT(408, "Request Timeout"), 
        CONFLICT(409, "Conflict"), 
        GONE(410, "Gone"), 
        LENGTH_REQUIRED(411, "Length Required"), 
        PRECONDITION_FAILED(412, "Precondition Failed"), 
        REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"), 
        REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"), 
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"), 
        REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"), 
        EXPECTATION_FAILED(417, "Expectation Failed"), 
        PRECONDITION_REQUIRED(428, "Precondition Required"), 
        TOO_MANY_REQUESTS(429, "Too Many Requests"), 
        REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"), 
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"), 
        NOT_IMPLEMENTED(501, "Not Implemented"), 
        BAD_GATEWAY(502, "Bad Gateway"), 
        SERVICE_UNAVAILABLE(503, "Service Unavailable"), 
        GATEWAY_TIMEOUT(504, "Gateway Timeout"), 
        HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"), 
        NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");
        
        private final int code;
        private final String reason;
        private final Family family;
        
        private Status(final int statusCode, final String reasonPhrase) {
            this.code = statusCode;
            this.reason = reasonPhrase;
            this.family = Family.familyOf(statusCode);
        }
        
        @Override
        public Family getFamily() {
            return this.family;
        }
        
        @Override
        public int getStatusCode() {
            return this.code;
        }
        
        @Override
        public String getReasonPhrase() {
            return this.toString();
        }
        
        @Override
        public String toString() {
            return this.reason;
        }
        
        public static Status fromStatusCode(final int statusCode) {
            for (final Status s : values()) {
                if (s.code == statusCode) {
                    return s;
                }
            }
            return null;
        }
        
        public enum Family
        {
            INFORMATIONAL, 
            SUCCESSFUL, 
            REDIRECTION, 
            CLIENT_ERROR, 
            SERVER_ERROR, 
            OTHER;
            
            public static Family familyOf(final int statusCode) {
                switch (statusCode / 100) {
                    case 1: {
                        return Family.INFORMATIONAL;
                    }
                    case 2: {
                        return Family.SUCCESSFUL;
                    }
                    case 3: {
                        return Family.REDIRECTION;
                    }
                    case 4: {
                        return Family.CLIENT_ERROR;
                    }
                    case 5: {
                        return Family.SERVER_ERROR;
                    }
                    default: {
                        return Family.OTHER;
                    }
                }
            }
        }
    }
}
