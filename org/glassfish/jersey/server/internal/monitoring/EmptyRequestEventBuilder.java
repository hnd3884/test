package org.glassfish.jersey.server.internal.monitoring;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ContainerRequest;
import javax.ws.rs.ext.ExceptionMapper;

public class EmptyRequestEventBuilder implements RequestEventBuilder
{
    public static final EmptyRequestEventBuilder INSTANCE;
    
    @Override
    public RequestEventBuilder setExceptionMapper(final ExceptionMapper<?> exceptionMapper) {
        return this;
    }
    
    @Override
    public RequestEventBuilder setContainerRequest(final ContainerRequest containerRequest) {
        return this;
    }
    
    @Override
    public RequestEventBuilder setContainerResponse(final ContainerResponse containerResponse) {
        return this;
    }
    
    @Override
    public RequestEventBuilder setSuccess(final boolean success) {
        return this;
    }
    
    @Override
    public RequestEventBuilder setResponseWritten(final boolean responseWritten) {
        return this;
    }
    
    @Override
    public RequestEventBuilder setException(final Throwable throwable, final RequestEvent.ExceptionCause exceptionCause) {
        return this;
    }
    
    @Override
    public RequestEventBuilder setExtendedUriInfo(final ExtendedUriInfo extendedUriInfo) {
        return this;
    }
    
    @Override
    public RequestEventBuilder setContainerResponseFilters(final Iterable<ContainerResponseFilter> containerResponseFilters) {
        return this;
    }
    
    @Override
    public RequestEventBuilder setContainerRequestFilters(final Iterable<ContainerRequestFilter> containerRequestFilters) {
        return this;
    }
    
    @Override
    public RequestEventBuilder setResponseSuccessfullyMapped(final boolean responseSuccessfullyMapped) {
        return this;
    }
    
    @Override
    public RequestEvent build(final RequestEvent.Type eventType) {
        return null;
    }
    
    static {
        INSTANCE = new EmptyRequestEventBuilder();
    }
}
