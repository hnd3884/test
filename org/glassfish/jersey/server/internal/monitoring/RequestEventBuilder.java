package org.glassfish.jersey.server.internal.monitoring;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ContainerRequest;
import javax.ws.rs.ext.ExceptionMapper;

public interface RequestEventBuilder
{
    RequestEventBuilder setExceptionMapper(final ExceptionMapper<?> p0);
    
    RequestEventBuilder setContainerRequest(final ContainerRequest p0);
    
    RequestEventBuilder setContainerResponse(final ContainerResponse p0);
    
    RequestEventBuilder setSuccess(final boolean p0);
    
    RequestEventBuilder setResponseWritten(final boolean p0);
    
    RequestEventBuilder setException(final Throwable p0, final RequestEvent.ExceptionCause p1);
    
    RequestEventBuilder setExtendedUriInfo(final ExtendedUriInfo p0);
    
    RequestEventBuilder setContainerResponseFilters(final Iterable<ContainerResponseFilter> p0);
    
    RequestEventBuilder setContainerRequestFilters(final Iterable<ContainerRequestFilter> p0);
    
    RequestEventBuilder setResponseSuccessfullyMapped(final boolean p0);
    
    RequestEvent build(final RequestEvent.Type p0);
}
