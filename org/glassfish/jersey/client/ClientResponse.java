package org.glassfish.jersey.client;

import javax.ws.rs.ext.ReaderInterceptor;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.ProcessingException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.ws.rs.core.Link;
import java.util.Set;
import javax.ws.rs.core.NewCookie;
import java.util.Map;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.message.internal.Statuses;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import java.net.URI;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.internal.inject.InjectionManagerSupplier;
import javax.ws.rs.client.ClientResponseContext;
import org.glassfish.jersey.message.internal.InboundMessageContext;

public class ClientResponse extends InboundMessageContext implements ClientResponseContext, InjectionManagerSupplier
{
    private Response.StatusType status;
    private final ClientRequest requestContext;
    private URI resolvedUri;
    
    public ClientResponse(final ClientRequest requestContext, final Response response) {
        this(response.getStatusInfo(), requestContext);
        this.headers(OutboundJaxrsResponse.from(response).getContext().getStringHeaders());
        final Object entity = response.getEntity();
        if (entity != null) {
            final InputStream entityStream = new InputStream() {
                private ByteArrayInputStream byteArrayInputStream = null;
                
                @Override
                public int read() throws IOException {
                    if (this.byteArrayInputStream == null) {
                        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        OutputStream stream = null;
                        try {
                            try {
                                stream = requestContext.getWorkers().writeTo(entity, (Class)entity.getClass(), (Type)null, (Annotation[])null, response.getMediaType(), response.getMetadata(), requestContext.getPropertiesDelegate(), (OutputStream)baos, (Iterable)Collections.emptyList());
                            }
                            finally {
                                if (stream != null) {
                                    stream.close();
                                }
                            }
                        }
                        catch (final IOException ex) {}
                        this.byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
                    }
                    return this.byteArrayInputStream.read();
                }
            };
            this.setEntityStream(entityStream);
        }
    }
    
    public ClientResponse(final Response.StatusType status, final ClientRequest requestContext) {
        this(status, requestContext, requestContext.getUri());
    }
    
    public ClientResponse(final Response.StatusType status, final ClientRequest requestContext, final URI resolvedRequestUri) {
        this.status = status;
        this.resolvedUri = resolvedRequestUri;
        this.requestContext = requestContext;
        this.setWorkers(requestContext.getWorkers());
    }
    
    public int getStatus() {
        return this.status.getStatusCode();
    }
    
    public void setStatus(final int code) {
        this.status = Statuses.from(code);
    }
    
    public void setStatusInfo(final Response.StatusType status) {
        if (status == null) {
            throw new NullPointerException(LocalizationMessages.CLIENT_RESPONSE_STATUS_NULL());
        }
        this.status = status;
    }
    
    public Response.StatusType getStatusInfo() {
        return this.status;
    }
    
    public URI getResolvedRequestUri() {
        return this.resolvedUri;
    }
    
    public void setResolvedRequestUri(final URI uri) {
        if (uri == null) {
            throw new NullPointerException(LocalizationMessages.CLIENT_RESPONSE_RESOLVED_URI_NULL());
        }
        if (!uri.isAbsolute()) {
            throw new IllegalArgumentException(LocalizationMessages.CLIENT_RESPONSE_RESOLVED_URI_NOT_ABSOLUTE());
        }
        this.resolvedUri = uri;
    }
    
    public ClientRequest getRequestContext() {
        return this.requestContext;
    }
    
    public Map<String, NewCookie> getCookies() {
        return super.getResponseCookies();
    }
    
    public Set<Link> getLinks() {
        return (Set)super.getLinks().stream().map(link -> {
            if (link.getUri().isAbsolute()) {
                return link;
            }
            else {
                return Link.fromLink(link).baseUri(this.getResolvedRequestUri()).build(new Object[0]);
            }
        }).collect(Collectors.toSet());
    }
    
    public String toString() {
        return "ClientResponse{method=" + this.requestContext.getMethod() + ", uri=" + this.requestContext.getUri() + ", status=" + this.status.getStatusCode() + ", reason=" + this.status.getReasonPhrase() + "}";
    }
    
    public Object getEntity() throws IllegalStateException {
        return this.getEntityStream();
    }
    
    public <T> T readEntity(final Class<T> entityType) throws ProcessingException, IllegalStateException {
        return (T)this.readEntity((Class)entityType, this.requestContext.getPropertiesDelegate());
    }
    
    public <T> T readEntity(final GenericType<T> entityType) throws ProcessingException, IllegalStateException {
        return (T)this.readEntity(entityType.getRawType(), entityType.getType(), this.requestContext.getPropertiesDelegate());
    }
    
    public <T> T readEntity(final Class<T> entityType, final Annotation[] annotations) throws ProcessingException, IllegalStateException {
        return (T)this.readEntity((Class)entityType, annotations, this.requestContext.getPropertiesDelegate());
    }
    
    public <T> T readEntity(final GenericType<T> entityType, final Annotation[] annotations) throws ProcessingException, IllegalStateException {
        return (T)this.readEntity(entityType.getRawType(), entityType.getType(), annotations, this.requestContext.getPropertiesDelegate());
    }
    
    public InjectionManager getInjectionManager() {
        return this.getRequestContext().getInjectionManager();
    }
    
    protected Iterable<ReaderInterceptor> getReaderInterceptors() {
        return this.requestContext.getReaderInterceptors();
    }
}
