package org.glassfish.jersey.message.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import javax.ws.rs.core.CacheControl;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.Link;
import java.net.URI;
import java.util.Set;
import java.util.Date;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.NewCookie;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.Map;
import javax.ws.rs.core.Response;

public class OutboundJaxrsResponse extends Response
{
    private final OutboundMessageContext context;
    private final Response.StatusType status;
    private boolean closed;
    private boolean buffered;
    
    public static OutboundJaxrsResponse from(final Response response) {
        if (response instanceof OutboundJaxrsResponse) {
            return (OutboundJaxrsResponse)response;
        }
        final Response.StatusType status = response.getStatusInfo();
        final OutboundMessageContext context = new OutboundMessageContext();
        context.getHeaders().putAll((Map)response.getMetadata());
        context.setEntity(response.getEntity());
        return new OutboundJaxrsResponse(status, context);
    }
    
    public OutboundJaxrsResponse(final Response.StatusType status, final OutboundMessageContext context) {
        this.closed = false;
        this.buffered = false;
        this.status = status;
        this.context = context;
    }
    
    public OutboundMessageContext getContext() {
        return this.context;
    }
    
    public int getStatus() {
        return this.status.getStatusCode();
    }
    
    public Response.StatusType getStatusInfo() {
        return this.status;
    }
    
    public Object getEntity() {
        if (this.closed) {
            throw new IllegalStateException(LocalizationMessages.RESPONSE_CLOSED());
        }
        return this.context.getEntity();
    }
    
    public <T> T readEntity(final Class<T> type) throws ProcessingException {
        throw new IllegalStateException(LocalizationMessages.NOT_SUPPORTED_ON_OUTBOUND_MESSAGE());
    }
    
    public <T> T readEntity(final GenericType<T> entityType) throws ProcessingException {
        throw new IllegalStateException(LocalizationMessages.NOT_SUPPORTED_ON_OUTBOUND_MESSAGE());
    }
    
    public <T> T readEntity(final Class<T> type, final Annotation[] annotations) throws ProcessingException {
        throw new IllegalStateException(LocalizationMessages.NOT_SUPPORTED_ON_OUTBOUND_MESSAGE());
    }
    
    public <T> T readEntity(final GenericType<T> entityType, final Annotation[] annotations) throws ProcessingException {
        throw new IllegalStateException(LocalizationMessages.NOT_SUPPORTED_ON_OUTBOUND_MESSAGE());
    }
    
    public boolean hasEntity() {
        if (this.closed) {
            throw new IllegalStateException(LocalizationMessages.RESPONSE_CLOSED());
        }
        return this.context.hasEntity();
    }
    
    public boolean bufferEntity() throws ProcessingException {
        if (this.closed) {
            throw new IllegalStateException(LocalizationMessages.RESPONSE_CLOSED());
        }
        if (!this.context.hasEntity() || !InputStream.class.isAssignableFrom(this.context.getEntityClass())) {
            return false;
        }
        if (this.buffered) {
            return true;
        }
        final InputStream in = InputStream.class.cast(this.context.getEntity());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        try {
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
        catch (final IOException ex) {
            throw new ProcessingException((Throwable)ex);
        }
        finally {
            try {
                in.close();
            }
            catch (final IOException ex2) {
                throw new ProcessingException((Throwable)ex2);
            }
        }
        this.context.setEntity(new ByteArrayInputStream(out.toByteArray()));
        return this.buffered = true;
    }
    
    public void close() throws ProcessingException {
        this.closed = true;
        this.context.close();
        if (this.buffered) {
            this.context.setEntity(null);
        }
        else if (this.context.hasEntity() && InputStream.class.isAssignableFrom(this.context.getEntityClass())) {
            try {
                InputStream.class.cast(this.context.getEntity()).close();
            }
            catch (final IOException ex) {
                throw new ProcessingException((Throwable)ex);
            }
        }
    }
    
    public MultivaluedMap<String, String> getStringHeaders() {
        return this.context.getStringHeaders();
    }
    
    public String getHeaderString(final String name) {
        return this.context.getHeaderString(name);
    }
    
    public MediaType getMediaType() {
        return this.context.getMediaType();
    }
    
    public Locale getLanguage() {
        return this.context.getLanguage();
    }
    
    public int getLength() {
        return this.context.getLength();
    }
    
    public Map<String, NewCookie> getCookies() {
        return this.context.getResponseCookies();
    }
    
    public EntityTag getEntityTag() {
        return this.context.getEntityTag();
    }
    
    public Date getDate() {
        return this.context.getDate();
    }
    
    public Date getLastModified() {
        return this.context.getLastModified();
    }
    
    public Set<String> getAllowedMethods() {
        return this.context.getAllowedMethods();
    }
    
    public URI getLocation() {
        return this.context.getLocation();
    }
    
    public Set<Link> getLinks() {
        return this.context.getLinks();
    }
    
    public boolean hasLink(final String relation) {
        return this.context.hasLink(relation);
    }
    
    public Link getLink(final String relation) {
        return this.context.getLink(relation);
    }
    
    public Link.Builder getLinkBuilder(final String relation) {
        return this.context.getLinkBuilder(relation);
    }
    
    public MultivaluedMap<String, Object> getMetadata() {
        return this.context.getHeaders();
    }
    
    public String toString() {
        return "OutboundJaxrsResponse{status=" + this.status.getStatusCode() + ", reason=" + this.status.getReasonPhrase() + ", hasEntity=" + this.context.hasEntity() + ", closed=" + this.closed + ", buffered=" + this.buffered + "}";
    }
    
    public static class Builder extends Response.ResponseBuilder
    {
        private Response.StatusType status;
        private final OutboundMessageContext context;
        private static final InheritableThreadLocal<URI> baseUriThreadLocal;
        
        public static void setBaseUri(final URI baseUri) {
            Builder.baseUriThreadLocal.set(baseUri);
        }
        
        private static URI getBaseUri() {
            return Builder.baseUriThreadLocal.get();
        }
        
        public static void clearBaseUri() {
            Builder.baseUriThreadLocal.remove();
        }
        
        public Builder(final OutboundMessageContext context) {
            this.context = context;
        }
        
        public Response build() {
            Response.StatusType st = this.status;
            if (st == null) {
                st = (Response.StatusType)(this.context.hasEntity() ? Response.Status.OK : Response.Status.NO_CONTENT);
            }
            return new OutboundJaxrsResponse(st, new OutboundMessageContext(this.context));
        }
        
        public Response.ResponseBuilder clone() {
            return new Builder(new OutboundMessageContext(this.context)).status(this.status);
        }
        
        public Response.ResponseBuilder status(final Response.StatusType status) {
            if (status == null) {
                throw new IllegalArgumentException("Response status must not be 'null'");
            }
            this.status = status;
            return this;
        }
        
        public Response.ResponseBuilder status(final int status, final String reasonPhrase) {
            if (status < 100 || status > 599) {
                throw new IllegalArgumentException("Response status must not be less than '100' or greater than '599'");
            }
            final Response.Status.Family family = Response.Status.Family.familyOf(status);
            this.status = (Response.StatusType)new Response.StatusType() {
                public int getStatusCode() {
                    return status;
                }
                
                public Response.Status.Family getFamily() {
                    return family;
                }
                
                public String getReasonPhrase() {
                    return reasonPhrase;
                }
            };
            return this;
        }
        
        public Response.ResponseBuilder status(final int code) {
            this.status = Statuses.from(code);
            return this;
        }
        
        public Response.ResponseBuilder entity(final Object entity) {
            this.context.setEntity(entity);
            return this;
        }
        
        public Response.ResponseBuilder entity(final Object entity, final Annotation[] annotations) {
            this.context.setEntity(entity, annotations);
            return this;
        }
        
        public Response.ResponseBuilder type(final MediaType type) {
            this.context.setMediaType(type);
            return this;
        }
        
        public Response.ResponseBuilder type(final String type) {
            return this.type((type == null) ? null : MediaType.valueOf(type));
        }
        
        public Response.ResponseBuilder variant(final Variant variant) {
            if (variant == null) {
                this.type((MediaType)null);
                this.language((String)null);
                this.encoding(null);
                return this;
            }
            this.type(variant.getMediaType());
            this.language(variant.getLanguage());
            this.encoding(variant.getEncoding());
            return this;
        }
        
        public Response.ResponseBuilder variants(final List<Variant> variants) {
            if (variants == null) {
                this.header("Vary", null);
                return this;
            }
            if (variants.isEmpty()) {
                return this;
            }
            final MediaType accept = variants.get(0).getMediaType();
            boolean vAccept = false;
            final Locale acceptLanguage = variants.get(0).getLanguage();
            boolean vAcceptLanguage = false;
            final String acceptEncoding = variants.get(0).getEncoding();
            boolean vAcceptEncoding = false;
            for (final Variant v : variants) {
                vAccept |= (!vAccept && this.vary(v.getMediaType(), accept));
                vAcceptLanguage |= (!vAcceptLanguage && this.vary(v.getLanguage(), acceptLanguage));
                vAcceptEncoding |= (!vAcceptEncoding && this.vary(v.getEncoding(), acceptEncoding));
            }
            final StringBuilder vary = new StringBuilder();
            this.append(vary, vAccept, "Accept");
            this.append(vary, vAcceptLanguage, "Accept-Language");
            this.append(vary, vAcceptEncoding, "Accept-Encoding");
            if (vary.length() > 0) {
                this.header("Vary", vary.toString());
            }
            return this;
        }
        
        private boolean vary(final MediaType v, final MediaType vary) {
            return v != null && !v.equals((Object)vary);
        }
        
        private boolean vary(final Locale v, final Locale vary) {
            return v != null && !v.equals(vary);
        }
        
        private boolean vary(final String v, final String vary) {
            return v != null && !v.equalsIgnoreCase(vary);
        }
        
        private void append(final StringBuilder sb, final boolean v, final String s) {
            if (v) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(s);
            }
        }
        
        public Response.ResponseBuilder language(final String language) {
            this.headerSingle("Content-Language", language);
            return this;
        }
        
        public Response.ResponseBuilder language(final Locale language) {
            this.headerSingle("Content-Language", language);
            return this;
        }
        
        public Response.ResponseBuilder location(final URI location) {
            URI locationUri = location;
            if (location != null && !location.isAbsolute()) {
                final URI baseUri = getBaseUri();
                if (baseUri != null) {
                    locationUri = baseUri.resolve(location);
                }
            }
            this.headerSingle("Location", locationUri);
            return this;
        }
        
        public Response.ResponseBuilder contentLocation(final URI location) {
            this.headerSingle("Content-Location", location);
            return this;
        }
        
        public Response.ResponseBuilder encoding(final String encoding) {
            this.headerSingle("Content-Encoding", encoding);
            return this;
        }
        
        public Response.ResponseBuilder tag(final EntityTag tag) {
            this.headerSingle("ETag", tag);
            return this;
        }
        
        public Response.ResponseBuilder tag(final String tag) {
            return this.tag((tag == null) ? null : new EntityTag(tag));
        }
        
        public Response.ResponseBuilder lastModified(final Date lastModified) {
            this.headerSingle("Last-Modified", lastModified);
            return this;
        }
        
        public Response.ResponseBuilder cacheControl(final CacheControl cacheControl) {
            this.headerSingle("Cache-Control", cacheControl);
            return this;
        }
        
        public Response.ResponseBuilder expires(final Date expires) {
            this.headerSingle("Expires", expires);
            return this;
        }
        
        public Response.ResponseBuilder cookie(final NewCookie... cookies) {
            if (cookies != null) {
                for (final NewCookie cookie : cookies) {
                    this.header("Set-Cookie", cookie);
                }
            }
            else {
                this.header("Set-Cookie", null);
            }
            return this;
        }
        
        public Response.ResponseBuilder header(final String name, final Object value) {
            return this.header(name, value, false);
        }
        
        private Response.ResponseBuilder headerSingle(final String name, final Object value) {
            return this.header(name, value, true);
        }
        
        private Response.ResponseBuilder header(final String name, final Object value, final boolean single) {
            if (value != null) {
                if (single) {
                    this.context.getHeaders().putSingle((Object)name, value);
                }
                else {
                    this.context.getHeaders().add((Object)name, value);
                }
            }
            else {
                this.context.getHeaders().remove((Object)name);
            }
            return this;
        }
        
        public Response.ResponseBuilder variants(final Variant... variants) {
            return this.variants(Arrays.asList(variants));
        }
        
        public Response.ResponseBuilder links(final Link... links) {
            if (links != null) {
                for (final Link link : links) {
                    this.header("Link", link);
                }
            }
            else {
                this.header("Link", null);
            }
            return this;
        }
        
        public Response.ResponseBuilder link(final URI uri, final String rel) {
            this.header("Link", Link.fromUri(uri).rel(rel).build(new Object[0]));
            return this;
        }
        
        public Response.ResponseBuilder link(final String uri, final String rel) {
            this.header("Link", Link.fromUri(uri).rel(rel).build(new Object[0]));
            return this;
        }
        
        public Response.ResponseBuilder allow(final String... methods) {
            if (methods == null || (methods.length == 1 && methods[0] == null)) {
                return this.allow((Set<String>)null);
            }
            return this.allow(new HashSet<String>(Arrays.asList(methods)));
        }
        
        public Response.ResponseBuilder allow(final Set<String> methods) {
            if (methods == null) {
                return this.header("Allow", null, true);
            }
            final StringBuilder allow = new StringBuilder();
            for (final String m : methods) {
                this.append(allow, true, m);
            }
            return this.header("Allow", allow, true);
        }
        
        public Response.ResponseBuilder replaceAll(final MultivaluedMap<String, Object> headers) {
            this.context.replaceHeaders(headers);
            return this;
        }
        
        static {
            baseUriThreadLocal = new InheritableThreadLocal<URI>();
        }
    }
}
