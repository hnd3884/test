package org.glassfish.jersey.server.internal.monitoring;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.monitoring.RequestEvent;

public class RequestEventImpl implements RequestEvent
{
    private final Type type;
    private final ContainerRequest containerRequest;
    private final ContainerResponse containerResponse;
    private final Throwable throwable;
    private final ExtendedUriInfo extendedUriInfo;
    private final Iterable<ContainerResponseFilter> containerResponseFilters;
    private final Iterable<ContainerRequestFilter> containerRequestFilters;
    private final ExceptionMapper<?> exceptionMapper;
    private final boolean success;
    private final boolean responseSuccessfullyMapped;
    private final ExceptionCause exceptionCause;
    private final boolean responseWritten;
    
    private RequestEventImpl(final Type type, final ContainerRequest containerRequest, final ContainerResponse containerResponse, final Throwable throwable, final ExtendedUriInfo extendedUriInfo, final Iterable<ContainerResponseFilter> containerResponseFilters, final Iterable<ContainerRequestFilter> containerRequestFilters, final ExceptionMapper<?> exceptionMapper, final boolean success, final boolean responseSuccessfullyMapped, final ExceptionCause exceptionCause, final boolean responseWritten) {
        this.type = type;
        this.containerRequest = containerRequest;
        this.containerResponse = containerResponse;
        this.throwable = throwable;
        this.extendedUriInfo = extendedUriInfo;
        this.containerResponseFilters = containerResponseFilters;
        this.containerRequestFilters = containerRequestFilters;
        this.exceptionMapper = exceptionMapper;
        this.success = success;
        this.responseSuccessfullyMapped = responseSuccessfullyMapped;
        this.exceptionCause = exceptionCause;
        this.responseWritten = responseWritten;
    }
    
    @Override
    public ContainerRequest getContainerRequest() {
        return this.containerRequest;
    }
    
    @Override
    public ContainerResponse getContainerResponse() {
        return this.containerResponse;
    }
    
    @Override
    public Throwable getException() {
        return this.throwable;
    }
    
    @Override
    public Type getType() {
        return this.type;
    }
    
    @Override
    public ExtendedUriInfo getUriInfo() {
        return this.extendedUriInfo;
    }
    
    @Override
    public ExceptionMapper<?> getExceptionMapper() {
        return this.exceptionMapper;
    }
    
    @Override
    public Iterable<ContainerRequestFilter> getContainerRequestFilters() {
        return this.containerRequestFilters;
    }
    
    @Override
    public Iterable<ContainerResponseFilter> getContainerResponseFilters() {
        return this.containerResponseFilters;
    }
    
    @Override
    public boolean isSuccess() {
        return this.success;
    }
    
    @Override
    public boolean isResponseSuccessfullyMapped() {
        return this.responseSuccessfullyMapped;
    }
    
    @Override
    public ExceptionCause getExceptionCause() {
        return this.exceptionCause;
    }
    
    @Override
    public boolean isResponseWritten() {
        return this.responseWritten;
    }
    
    public static class Builder implements RequestEventBuilder
    {
        private ContainerRequest containerRequest;
        private ContainerResponse containerResponse;
        private Throwable throwable;
        private ExtendedUriInfo extendedUriInfo;
        private Iterable<ContainerResponseFilter> containerResponseFilters;
        private Iterable<ContainerRequestFilter> containerRequestFilters;
        private ExceptionMapper<?> exceptionMapper;
        private boolean success;
        private boolean responseWritten;
        private boolean responseSuccessfullyMapped;
        private ExceptionCause exceptionCause;
        
        @Override
        public Builder setExceptionMapper(final ExceptionMapper<?> exceptionMapper) {
            this.exceptionMapper = exceptionMapper;
            return this;
        }
        
        @Override
        public Builder setContainerRequest(final ContainerRequest containerRequest) {
            this.containerRequest = containerRequest;
            return this;
        }
        
        @Override
        public Builder setContainerResponse(final ContainerResponse containerResponse) {
            this.containerResponse = containerResponse;
            return this;
        }
        
        @Override
        public Builder setResponseWritten(final boolean responseWritten) {
            this.responseWritten = responseWritten;
            return this;
        }
        
        @Override
        public Builder setSuccess(final boolean success) {
            this.success = success;
            return this;
        }
        
        @Override
        public Builder setException(final Throwable throwable, final ExceptionCause exceptionCause) {
            this.throwable = throwable;
            this.exceptionCause = exceptionCause;
            return this;
        }
        
        @Override
        public Builder setExtendedUriInfo(final ExtendedUriInfo extendedUriInfo) {
            this.extendedUriInfo = extendedUriInfo;
            return this;
        }
        
        @Override
        public Builder setContainerResponseFilters(final Iterable<ContainerResponseFilter> containerResponseFilters) {
            this.containerResponseFilters = containerResponseFilters;
            return this;
        }
        
        @Override
        public Builder setContainerRequestFilters(final Iterable<ContainerRequestFilter> containerRequestFilters) {
            this.containerRequestFilters = containerRequestFilters;
            return this;
        }
        
        @Override
        public Builder setResponseSuccessfullyMapped(final boolean responseSuccessfullyMapped) {
            this.responseSuccessfullyMapped = responseSuccessfullyMapped;
            return this;
        }
        
        @Override
        public RequestEventImpl build(final Type type) {
            return new RequestEventImpl(type, this.containerRequest, this.containerResponse, this.throwable, this.extendedUriInfo, this.containerResponseFilters, this.containerRequestFilters, this.exceptionMapper, this.success, this.responseSuccessfullyMapped, this.exceptionCause, this.responseWritten, null);
        }
    }
}
