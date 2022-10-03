package org.glassfish.jersey.client;

import javax.ws.rs.core.Link;
import java.net.URI;
import java.util.Set;
import java.util.Date;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.NewCookie;
import java.util.Map;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.annotation.Annotation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.internal.util.Producer;
import org.glassfish.jersey.process.internal.RequestContext;
import org.glassfish.jersey.process.internal.RequestScope;
import javax.ws.rs.core.Response;

class InboundJaxrsResponse extends Response
{
    private final ClientResponse context;
    private final RequestScope scope;
    private final RequestContext requestContext;
    
    public InboundJaxrsResponse(final ClientResponse context, final RequestScope scope) {
        this.context = context;
        this.scope = scope;
        if (this.scope != null) {
            this.requestContext = scope.referenceCurrent();
        }
        else {
            this.requestContext = null;
        }
    }
    
    public int getStatus() {
        return this.context.getStatus();
    }
    
    public Response.StatusType getStatusInfo() {
        return this.context.getStatusInfo();
    }
    
    public Object getEntity() throws IllegalStateException {
        return this.context.getEntity();
    }
    
    public <T> T readEntity(final Class<T> entityType) throws ProcessingException, IllegalStateException {
        return this.runInScopeIfPossible((org.glassfish.jersey.internal.util.Producer<T>)new Producer<T>() {
            public T call() {
                return InboundJaxrsResponse.this.context.readEntity(entityType);
            }
        });
    }
    
    public <T> T readEntity(final GenericType<T> entityType) throws ProcessingException, IllegalStateException {
        return this.runInScopeIfPossible((org.glassfish.jersey.internal.util.Producer<T>)new Producer<T>() {
            public T call() {
                return InboundJaxrsResponse.this.context.readEntity(entityType);
            }
        });
    }
    
    public <T> T readEntity(final Class<T> entityType, final Annotation[] annotations) throws ProcessingException, IllegalStateException {
        return this.runInScopeIfPossible((org.glassfish.jersey.internal.util.Producer<T>)new Producer<T>() {
            public T call() {
                return InboundJaxrsResponse.this.context.readEntity(entityType, annotations);
            }
        });
    }
    
    public <T> T readEntity(final GenericType<T> entityType, final Annotation[] annotations) throws ProcessingException, IllegalStateException {
        return this.runInScopeIfPossible((org.glassfish.jersey.internal.util.Producer<T>)new Producer<T>() {
            public T call() {
                return InboundJaxrsResponse.this.context.readEntity(entityType, annotations);
            }
        });
    }
    
    public boolean hasEntity() {
        return this.context.hasEntity();
    }
    
    public boolean bufferEntity() throws ProcessingException {
        return this.context.bufferEntity();
    }
    
    public void close() throws ProcessingException {
        try {
            this.context.close();
        }
        finally {
            if (this.requestContext != null) {
                this.requestContext.release();
            }
        }
    }
    
    public String getHeaderString(final String name) {
        return this.context.getHeaderString(name);
    }
    
    public MultivaluedMap<String, String> getStringHeaders() {
        return (MultivaluedMap<String, String>)this.context.getHeaders();
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
        final MultivaluedMap<String, ?> headers = (MultivaluedMap<String, ?>)this.context.getHeaders();
        return (MultivaluedMap<String, Object>)headers;
    }
    
    public String toString() {
        return "InboundJaxrsResponse{context=" + this.context + "}";
    }
    
    private <T> T runInScopeIfPossible(final Producer<T> producer) {
        if (this.scope != null && this.requestContext != null) {
            return (T)this.scope.runInScope(this.requestContext, (Producer)producer);
        }
        return (T)producer.call();
    }
}
