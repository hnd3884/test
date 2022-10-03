package org.glassfish.jersey.server;

import java.io.IOException;
import javax.ws.rs.core.Configuration;
import java.io.OutputStream;
import javax.ws.rs.core.GenericEntity;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import javax.ws.rs.core.Link;
import java.net.URI;
import javax.ws.rs.core.EntityTag;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.Date;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.util.Map;
import org.glassfish.jersey.message.internal.Statuses;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.container.ContainerResponseContext;

public class ContainerResponse implements ContainerResponseContext
{
    private Response.StatusType status;
    private final ContainerRequest requestContext;
    private final OutboundMessageContext messageContext;
    private boolean mappedFromException;
    private boolean closed;
    
    public ContainerResponse(final ContainerRequest requestContext, final Response response) {
        this(requestContext, OutboundJaxrsResponse.from(response));
    }
    
    ContainerResponse(final ContainerRequest requestContext, final OutboundJaxrsResponse response) {
        (this.requestContext = requestContext).inResponseProcessing();
        this.status = response.getStatusInfo();
        this.messageContext = response.getContext();
        final String varyValue = requestContext.getVaryValue();
        if (varyValue != null && !this.messageContext.getHeaders().containsKey((Object)"Vary")) {
            this.messageContext.getHeaders().add((Object)"Vary", (Object)varyValue);
        }
    }
    
    public boolean isMappedFromException() {
        return this.mappedFromException;
    }
    
    public void setMappedFromException(final boolean mappedFromException) {
        this.mappedFromException = mappedFromException;
    }
    
    public int getStatus() {
        return this.status.getStatusCode();
    }
    
    public void setStatus(final int code) {
        this.status = Statuses.from(code);
    }
    
    public void setStatusInfo(final Response.StatusType status) {
        if (status == null) {
            throw new NullPointerException("Response status must not be 'null'");
        }
        this.status = status;
    }
    
    public Response.StatusType getStatusInfo() {
        return this.status;
    }
    
    public ContainerRequest getRequestContext() {
        return this.requestContext;
    }
    
    public Map<String, NewCookie> getCookies() {
        return this.messageContext.getResponseCookies();
    }
    
    public OutboundMessageContext getWrappedMessageContext() {
        return this.messageContext;
    }
    
    public String getHeaderString(final String name) {
        return this.messageContext.getHeaderString(name);
    }
    
    public MultivaluedMap<String, Object> getHeaders() {
        return (MultivaluedMap<String, Object>)this.messageContext.getHeaders();
    }
    
    public MultivaluedMap<String, String> getStringHeaders() {
        return (MultivaluedMap<String, String>)this.messageContext.getStringHeaders();
    }
    
    public Date getDate() {
        return this.messageContext.getDate();
    }
    
    public Locale getLanguage() {
        return this.messageContext.getLanguage();
    }
    
    public MediaType getMediaType() {
        return this.messageContext.getMediaType();
    }
    
    public Set<String> getAllowedMethods() {
        return this.messageContext.getAllowedMethods();
    }
    
    public int getLength() {
        return this.messageContext.getLength();
    }
    
    public EntityTag getEntityTag() {
        return this.messageContext.getEntityTag();
    }
    
    public Date getLastModified() {
        return this.messageContext.getLastModified();
    }
    
    public URI getLocation() {
        return this.messageContext.getLocation();
    }
    
    public Set<Link> getLinks() {
        return this.messageContext.getLinks();
    }
    
    public boolean hasLink(final String relation) {
        return this.messageContext.hasLink(relation);
    }
    
    public Link getLink(final String relation) {
        return this.messageContext.getLink(relation);
    }
    
    public Link.Builder getLinkBuilder(final String relation) {
        return this.messageContext.getLinkBuilder(relation);
    }
    
    public boolean hasEntity() {
        return this.messageContext.hasEntity();
    }
    
    public Object getEntity() {
        return this.messageContext.getEntity();
    }
    
    public void setEntity(final Object entity) {
        this.messageContext.setEntity(entity);
    }
    
    public void setEntity(final Object entity, final Annotation[] annotations) {
        this.messageContext.setEntity(entity, annotations);
    }
    
    public void setEntity(final Object entity, final Type type, final Annotation[] annotations) {
        this.messageContext.setEntity(entity, type, annotations);
    }
    
    public void setEntity(final Object entity, final Annotation[] annotations, final MediaType mediaType) {
        this.messageContext.setEntity(entity, annotations, mediaType);
    }
    
    public void setMediaType(final MediaType mediaType) {
        this.messageContext.setMediaType(mediaType);
    }
    
    public Class<?> getEntityClass() {
        return this.messageContext.getEntityClass();
    }
    
    public Type getEntityType() {
        return this.messageContext.getEntityType();
    }
    
    public void setEntityType(final Type type) {
        Type t = type;
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            if (parameterizedType.getRawType().equals(GenericEntity.class)) {
                t = parameterizedType.getActualTypeArguments()[0];
            }
        }
        this.messageContext.setEntityType(t);
    }
    
    public Annotation[] getEntityAnnotations() {
        return this.messageContext.getEntityAnnotations();
    }
    
    public void setEntityAnnotations(final Annotation[] annotations) {
        this.messageContext.setEntityAnnotations(annotations);
    }
    
    public OutputStream getEntityStream() {
        return this.messageContext.getEntityStream();
    }
    
    public void setEntityStream(final OutputStream outputStream) {
        this.messageContext.setEntityStream(outputStream);
    }
    
    public void setStreamProvider(final OutboundMessageContext.StreamProvider streamProvider) {
        this.messageContext.setStreamProvider(streamProvider);
    }
    
    public void enableBuffering(final Configuration configuration) {
        this.messageContext.enableBuffering(configuration);
    }
    
    public void commitStream() throws IOException {
        this.messageContext.commitStream();
    }
    
    public boolean isCommitted() {
        return this.messageContext.isCommitted();
    }
    
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.messageContext.close();
            this.requestContext.getResponseWriter().commit();
        }
    }
    
    public boolean isChunked() {
        return this.hasEntity() && ChunkedOutput.class.isAssignableFrom(this.getEntity().getClass());
    }
}
