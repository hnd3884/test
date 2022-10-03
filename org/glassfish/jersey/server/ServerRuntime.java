package org.glassfish.jersey.server;

import org.glassfish.jersey.internal.util.Closure;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import javax.ws.rs.ServiceUnavailableException;
import java.util.LinkedList;
import java.util.HashMap;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.guava.Preconditions;
import java.util.Collection;
import java.util.Date;
import org.glassfish.jersey.internal.util.Producer;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.container.CompletionCallback;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.container.ConnectionCallback;
import javax.ws.rs.container.AsyncResponse;
import java.io.IOException;
import java.io.OutputStream;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import org.glassfish.jersey.message.internal.HeaderValueException;
import javax.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.server.internal.ServerTraceEvent;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import java.util.Iterator;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.server.spi.ResponseErrorMapper;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.internal.process.MappableException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import org.glassfish.jersey.internal.PropertiesDelegate;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.process.internal.RequestContext;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.server.internal.monitoring.RequestEventBuilder;
import org.glassfish.jersey.server.internal.routing.UriRoutingContext;
import java.net.URI;
import javax.ws.rs.NotFoundException;
import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.process.internal.Stages;
import org.glassfish.jersey.internal.util.collection.Refs;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.server.spi.ExternalRequestContext;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.internal.monitoring.RequestEventImpl;
import org.glassfish.jersey.server.internal.monitoring.EmptyRequestEventBuilder;
import java.util.Map;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.server.spi.ExternalRequestScope;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.spi.ExceptionMappers;
import org.glassfish.jersey.process.internal.RequestScope;
import java.util.concurrent.ExecutorService;
import javax.inject.Provider;
import java.util.concurrent.ScheduledExecutorService;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.process.internal.Stage;

public class ServerRuntime
{
    private final Stage<RequestProcessingContext> requestProcessingRoot;
    private final ProcessingProviders processingProviders;
    private final InjectionManager injectionManager;
    private final ScheduledExecutorService backgroundScheduler;
    private final Provider<ExecutorService> managedAsyncExecutor;
    private final RequestScope requestScope;
    private final ExceptionMappers exceptionMappers;
    private final ApplicationEventListener applicationEventListener;
    private final Configuration configuration;
    private final ExternalRequestScope externalRequestScope;
    private final TracingConfig tracingConfig;
    private final TracingLogger.Level tracingThreshold;
    private final boolean processResponseErrors;
    private final boolean disableLocationHeaderRelativeUriResolution;
    private final boolean rfc7231LocationHeaderRelativeUriResolution;
    
    static ServerRuntime createServerRuntime(final InjectionManager injectionManager, final ServerBootstrapBag bootstrapBag, final Stage<RequestProcessingContext> processingRoot, final ApplicationEventListener eventListener, final ProcessingProviders processingProviders) {
        final ScheduledExecutorService scheduledExecutorServiceSupplier = (ScheduledExecutorService)injectionManager.getInstance((Class)ScheduledExecutorService.class, new Annotation[] { BackgroundSchedulerLiteral.INSTANCE });
        final Provider<ExecutorService> asyncExecutorServiceSupplier = (Provider<ExecutorService>)(() -> (ExecutorService)injectionManager.getInstance((Class)ExecutorService.class, new Annotation[] { ManagedAsyncExecutorLiteral.INSTANCE }));
        return new ServerRuntime(processingRoot, processingProviders, injectionManager, scheduledExecutorServiceSupplier, asyncExecutorServiceSupplier, bootstrapBag.getRequestScope(), bootstrapBag.getExceptionMappers(), eventListener, (ExternalRequestScope)injectionManager.getInstance((Class)ExternalRequestScope.class), bootstrapBag.getConfiguration());
    }
    
    private ServerRuntime(final Stage<RequestProcessingContext> requestProcessingRoot, final ProcessingProviders processingProviders, final InjectionManager injectionManager, final ScheduledExecutorService backgroundScheduler, final Provider<ExecutorService> managedAsyncExecutorProvider, final RequestScope requestScope, final ExceptionMappers exceptionMappers, final ApplicationEventListener applicationEventListener, final ExternalRequestScope externalScope, final Configuration configuration) {
        this.requestProcessingRoot = requestProcessingRoot;
        this.processingProviders = processingProviders;
        this.injectionManager = injectionManager;
        this.backgroundScheduler = backgroundScheduler;
        this.managedAsyncExecutor = managedAsyncExecutorProvider;
        this.requestScope = requestScope;
        this.exceptionMappers = exceptionMappers;
        this.applicationEventListener = applicationEventListener;
        this.externalRequestScope = externalScope;
        this.configuration = configuration;
        this.tracingConfig = TracingUtils.getTracingConfig(configuration);
        this.tracingThreshold = TracingUtils.getTracingThreshold(configuration);
        this.processResponseErrors = PropertiesHelper.isProperty(configuration.getProperty("jersey.config.server.exception.processResponseErrors"));
        this.disableLocationHeaderRelativeUriResolution = ServerProperties.getValue(configuration.getProperties(), "jersey.config.server.headers.location.relative.resolution.disabled", Boolean.FALSE, Boolean.class);
        this.rfc7231LocationHeaderRelativeUriResolution = ServerProperties.getValue(configuration.getProperties(), "jersey.config.server.headers.location.relative.resolution.rfc7231", Boolean.FALSE, Boolean.class);
    }
    
    public void process(final ContainerRequest request) {
        TracingUtils.initTracingSupport(this.tracingConfig, this.tracingThreshold, request);
        TracingUtils.logStart(request);
        final UriRoutingContext routingContext = request.getUriRoutingContext();
        RequestEventBuilder monitoringEventBuilder = EmptyRequestEventBuilder.INSTANCE;
        RequestEventListener monitoringEventListener = null;
        if (this.applicationEventListener != null) {
            monitoringEventBuilder = new RequestEventImpl.Builder().setContainerRequest(request).setExtendedUriInfo((ExtendedUriInfo)routingContext);
            monitoringEventListener = this.applicationEventListener.onRequest(monitoringEventBuilder.build(RequestEvent.Type.START));
        }
        request.setProcessingProviders(this.processingProviders);
        final RequestProcessingContext context = new RequestProcessingContext(this.injectionManager, request, routingContext, monitoringEventBuilder, monitoringEventListener);
        request.checkState();
        final Responder responder = new Responder(context, this);
        final RequestContext requestScopeInstance = this.requestScope.createContext();
        final AsyncResponderHolder asyncResponderHolder = new AsyncResponderHolder(responder, this.externalRequestScope, requestScopeInstance, (ExternalRequestContext)this.externalRequestScope.open(this.injectionManager));
        context.initAsyncContext((Value<AsyncContext>)asyncResponderHolder);
        this.requestScope.runInScope(requestScopeInstance, (Runnable)new Runnable() {
            @Override
            public void run() {
                try {
                    if (!ServerRuntime.this.disableLocationHeaderRelativeUriResolution) {
                        final URI uriToUse = ServerRuntime.this.rfc7231LocationHeaderRelativeUriResolution ? request.getRequestUri() : request.getBaseUri();
                        OutboundJaxrsResponse.Builder.setBaseUri(uriToUse);
                    }
                    final Ref<Endpoint> endpointRef = (Ref<Endpoint>)Refs.emptyRef();
                    final RequestProcessingContext data = (RequestProcessingContext)Stages.process((Object)context, ServerRuntime.this.requestProcessingRoot, (Ref)endpointRef);
                    final Endpoint endpoint = (Endpoint)endpointRef.get();
                    if (endpoint == null) {
                        throw new NotFoundException();
                    }
                    final ContainerResponse response = (ContainerResponse)endpoint.apply((Object)data);
                    if (!asyncResponderHolder.isAsync()) {
                        responder.process(response);
                    }
                    else {
                        ServerRuntime.this.externalRequestScope.suspend(asyncResponderHolder.externalContext, ServerRuntime.this.injectionManager);
                    }
                }
                catch (final Throwable throwable) {
                    responder.process(throwable);
                }
                finally {
                    asyncResponderHolder.release();
                    OutboundJaxrsResponse.Builder.clearBaseUri();
                }
            }
        });
    }
    
    ScheduledExecutorService getBackgroundScheduler() {
        return this.backgroundScheduler;
    }
    
    private static void ensureAbsolute(final URI location, final MultivaluedMap<String, Object> headers, final ContainerRequest request, final boolean incompatible) {
        if (location == null || location.isAbsolute()) {
            return;
        }
        final URI uri = incompatible ? request.getRequestUri() : request.getBaseUri();
        headers.putSingle((Object)"Location", (Object)uri.resolve(location));
    }
    
    private static class AsyncResponderHolder implements Value<AsyncContext>
    {
        private final Responder responder;
        private final ExternalRequestScope externalScope;
        private final RequestContext requestContext;
        private final ExternalRequestContext<?> externalContext;
        private volatile AsyncResponder asyncResponder;
        
        private AsyncResponderHolder(final Responder responder, final ExternalRequestScope externalRequestScope, final RequestContext requestContext, final ExternalRequestContext<?> externalContext) {
            this.responder = responder;
            this.externalScope = externalRequestScope;
            this.requestContext = requestContext;
            this.externalContext = externalContext;
        }
        
        public AsyncContext get() {
            final AsyncResponder ar = new AsyncResponder(this.responder, this.requestContext, this.externalScope, this.externalContext);
            return this.asyncResponder = ar;
        }
        
        public boolean isAsync() {
            final AsyncResponder ar = this.asyncResponder;
            return ar != null && !ar.isRunning();
        }
        
        public void release() {
            if (this.asyncResponder == null) {
                this.requestContext.release();
            }
        }
    }
    
    private static class Responder
    {
        private static final Logger LOGGER;
        private final RequestProcessingContext processingContext;
        private final ServerRuntime runtime;
        private final CompletionCallbackRunner completionCallbackRunner;
        private final ConnectionCallbackRunner connectionCallbackRunner;
        private final TracingLogger tracingLogger;
        
        public Responder(final RequestProcessingContext processingContext, final ServerRuntime runtime) {
            this.completionCallbackRunner = new CompletionCallbackRunner();
            this.connectionCallbackRunner = new ConnectionCallbackRunner();
            this.processingContext = processingContext;
            this.runtime = runtime;
            this.tracingLogger = TracingLogger.getInstance((PropertiesDelegate)processingContext.request());
        }
        
        public void process(ContainerResponse response) {
            this.processingContext.monitoringEventBuilder().setContainerResponse(response);
            response = this.processResponse(response);
            this.release(response);
        }
        
        private ContainerResponse processResponse(ContainerResponse response) {
            final Stage<ContainerResponse> respondingRoot = this.processingContext.createRespondingRoot();
            if (respondingRoot != null) {
                response = (ContainerResponse)Stages.process((Object)response, (Stage)respondingRoot);
            }
            this.writeResponse(response);
            this.completionCallbackRunner.onComplete(null);
            return response;
        }
        
        public void process(final Throwable throwable) {
            final ContainerRequest request = this.processingContext.request();
            this.processingContext.monitoringEventBuilder().setException(throwable, RequestEvent.ExceptionCause.ORIGINAL);
            this.processingContext.triggerEvent(RequestEvent.Type.ON_EXCEPTION);
            ContainerResponse response = null;
            try {
                final Response exceptionResponse = this.mapException(throwable);
                try {
                    try {
                        response = this.convertResponse(exceptionResponse);
                        if (!this.runtime.disableLocationHeaderRelativeUriResolution) {
                            ensureAbsolute(response.getLocation(), response.getHeaders(), request, this.runtime.rfc7231LocationHeaderRelativeUriResolution);
                        }
                        this.processingContext.monitoringEventBuilder().setContainerResponse(response).setResponseSuccessfullyMapped(true);
                    }
                    finally {
                        this.processingContext.triggerEvent(RequestEvent.Type.EXCEPTION_MAPPING_FINISHED);
                    }
                    this.processResponse(response);
                }
                catch (final Throwable respError) {
                    Responder.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_PROCESSING_RESPONSE_FROM_ALREADY_MAPPED_EXCEPTION());
                    this.processingContext.monitoringEventBuilder().setException(respError, RequestEvent.ExceptionCause.MAPPED_RESPONSE);
                    this.processingContext.triggerEvent(RequestEvent.Type.ON_EXCEPTION);
                    throw respError;
                }
            }
            catch (final Throwable responseError) {
                if (throwable != responseError && (!(throwable instanceof MappableException) || throwable.getCause() != responseError)) {
                    Responder.LOGGER.log(Level.FINE, LocalizationMessages.ERROR_EXCEPTION_MAPPING_ORIGINAL_EXCEPTION(), throwable);
                }
                if (!this.processResponseError(responseError)) {
                    Responder.LOGGER.log(Level.FINE, LocalizationMessages.ERROR_EXCEPTION_MAPPING_THROWN_TO_CONTAINER(), responseError);
                    try {
                        request.getResponseWriter().failure(responseError);
                    }
                    finally {
                        this.completionCallbackRunner.onComplete(responseError);
                    }
                }
            }
            finally {
                this.release(response);
            }
        }
        
        private boolean processResponseError(final Throwable responseError) {
            boolean processed = false;
            if (this.runtime.processResponseErrors) {
                final Iterable<ResponseErrorMapper> mappers = Providers.getAllProviders(this.runtime.injectionManager, (Class)ResponseErrorMapper.class);
                try {
                    Response processedError = null;
                    for (final ResponseErrorMapper mapper : mappers) {
                        processedError = mapper.toResponse(responseError);
                        if (processedError != null) {
                            break;
                        }
                    }
                    if (processedError != null) {
                        this.processResponse(new ContainerResponse(this.processingContext.request(), processedError));
                        processed = true;
                    }
                }
                catch (final Throwable throwable) {
                    Responder.LOGGER.log(Level.FINE, LocalizationMessages.ERROR_EXCEPTION_MAPPING_PROCESSED_RESPONSE_ERROR(), throwable);
                }
            }
            return processed;
        }
        
        private ContainerResponse convertResponse(final Response exceptionResponse) {
            final ContainerResponse containerResponse = new ContainerResponse(this.processingContext.request(), exceptionResponse);
            containerResponse.setMappedFromException(true);
            return containerResponse;
        }
        
        private Response mapException(final Throwable originalThrowable) throws Throwable {
            Responder.LOGGER.log(Level.FINER, LocalizationMessages.EXCEPTION_MAPPING_START(), originalThrowable);
            Throwable throwable = originalThrowable;
            boolean inMappable = false;
            boolean mappingNotFound = false;
            do {
                if (throwable instanceof MappableException) {
                    inMappable = true;
                }
                else if (inMappable || throwable instanceof WebApplicationException) {
                    if (this.runtime.processResponseErrors && throwable instanceof InternalServerErrorException && throwable.getCause() instanceof MessageBodyProviderNotFoundException) {
                        throw throwable;
                    }
                    Response waeResponse = null;
                    if (throwable instanceof WebApplicationException) {
                        final WebApplicationException webApplicationException = (WebApplicationException)throwable;
                        this.processingContext.routingContext().setMappedThrowable(throwable);
                        waeResponse = webApplicationException.getResponse();
                        if (waeResponse.hasEntity()) {
                            Responder.LOGGER.log(Level.FINE, LocalizationMessages.EXCEPTION_MAPPING_WAE_ENTITY(waeResponse.getStatus()), throwable);
                            return waeResponse;
                        }
                    }
                    final long timestamp = this.tracingLogger.timestamp((TracingLogger.Event)ServerTraceEvent.EXCEPTION_MAPPING);
                    final ExceptionMapper mapper = this.runtime.exceptionMappers.findMapping(throwable);
                    if (mapper != null) {
                        this.processingContext.monitoringEventBuilder().setExceptionMapper((ExceptionMapper<?>)mapper);
                        this.processingContext.triggerEvent(RequestEvent.Type.EXCEPTION_MAPPER_FOUND);
                        try {
                            final Response mappedResponse = mapper.toResponse(throwable);
                            if (this.tracingLogger.isLogEnabled((TracingLogger.Event)ServerTraceEvent.EXCEPTION_MAPPING)) {
                                this.tracingLogger.logDuration((TracingLogger.Event)ServerTraceEvent.EXCEPTION_MAPPING, timestamp, new Object[] { mapper, throwable, throwable.getLocalizedMessage(), (mappedResponse != null) ? mappedResponse.getStatusInfo() : "-no-response-" });
                            }
                            this.processingContext.routingContext().setMappedThrowable(throwable);
                            if (mappedResponse != null) {
                                if (Responder.LOGGER.isLoggable(Level.FINER)) {
                                    final String message = String.format("Exception '%s' has been mapped by '%s' to response '%s' (%s:%s).", throwable.getLocalizedMessage(), mapper.getClass().getName(), mappedResponse.getStatusInfo().getReasonPhrase(), mappedResponse.getStatusInfo().getStatusCode(), mappedResponse.getStatusInfo().getFamily());
                                    Responder.LOGGER.log(Level.FINER, message);
                                }
                                return mappedResponse;
                            }
                            return Response.noContent().build();
                        }
                        catch (final Throwable mapperThrowable) {
                            Responder.LOGGER.log(Level.SEVERE, LocalizationMessages.EXCEPTION_MAPPER_THROWS_EXCEPTION(mapper.getClass()), mapperThrowable);
                            Responder.LOGGER.log(Level.SEVERE, LocalizationMessages.EXCEPTION_MAPPER_FAILED_FOR_EXCEPTION(), throwable);
                            return Response.serverError().build();
                        }
                    }
                    if (waeResponse != null) {
                        Responder.LOGGER.log(Level.FINE, LocalizationMessages.EXCEPTION_MAPPING_WAE_NO_ENTITY(waeResponse.getStatus()), throwable);
                        return waeResponse;
                    }
                    mappingNotFound = true;
                }
                if (throwable instanceof HeaderValueException && ((HeaderValueException)throwable).getContext() == HeaderValueException.Context.INBOUND) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                if (!inMappable || mappingNotFound) {
                    throw throwable;
                }
                throwable = throwable.getCause();
            } while (throwable != null);
            throw originalThrowable;
        }
        
        private ContainerResponse writeResponse(final ContainerResponse response) {
            final ContainerRequest request = this.processingContext.request();
            final ContainerResponseWriter writer = request.getResponseWriter();
            if (!this.runtime.disableLocationHeaderRelativeUriResolution) {
                ensureAbsolute(response.getLocation(), response.getHeaders(), response.getRequestContext(), this.runtime.rfc7231LocationHeaderRelativeUriResolution);
            }
            if (!response.hasEntity()) {
                this.tracingLogger.log((TracingLogger.Event)ServerTraceEvent.FINISHED, new Object[] { response.getStatusInfo() });
                this.tracingLogger.flush((MultivaluedMap)response.getHeaders());
                writer.writeResponseStatusAndHeaders(0L, response);
                this.setWrittenResponse(response);
                return response;
            }
            final Object entity = response.getEntity();
            boolean skipFinally = false;
            final boolean isHead = request.getMethod().equals("HEAD");
            try {
                response.setStreamProvider((OutboundMessageContext.StreamProvider)new OutboundMessageContext.StreamProvider() {
                    public OutputStream getOutputStream(final int contentLength) throws IOException {
                        if (!Responder.this.runtime.disableLocationHeaderRelativeUriResolution) {
                            ensureAbsolute(response.getLocation(), response.getHeaders(), response.getRequestContext(), Responder.this.runtime.rfc7231LocationHeaderRelativeUriResolution);
                        }
                        final OutputStream outputStream = writer.writeResponseStatusAndHeaders(contentLength, response);
                        return isHead ? null : outputStream;
                    }
                });
                if ((writer.enableResponseBuffering() || isHead) && !response.isChunked()) {
                    response.enableBuffering(this.runtime.configuration);
                }
                try {
                    response.setEntityStream(request.getWorkers().writeTo(entity, (Class)entity.getClass(), response.getEntityType(), response.getEntityAnnotations(), response.getMediaType(), (MultivaluedMap)response.getHeaders(), request.getPropertiesDelegate(), response.getEntityStream(), (Iterable)request.getWriterInterceptors()));
                }
                catch (final MappableException mpe) {
                    if (mpe.getCause() instanceof IOException) {
                        this.connectionCallbackRunner.onDisconnect((AsyncResponse)this.processingContext.asyncContext());
                    }
                    throw mpe;
                }
                this.tracingLogger.log((TracingLogger.Event)ServerTraceEvent.FINISHED, new Object[] { response.getStatusInfo() });
                this.tracingLogger.flush((MultivaluedMap)response.getHeaders());
                this.setWrittenResponse(response);
            }
            catch (final Throwable ex) {
                if (response.isCommitted()) {
                    Responder.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_WRITING_RESPONSE_ENTITY(), ex);
                }
                else {
                    skipFinally = true;
                    if (ex instanceof RuntimeException) {
                        throw (RuntimeException)ex;
                    }
                    throw new MappableException(ex);
                }
            }
            finally {
                if (!skipFinally) {
                    boolean close = !response.isChunked();
                    if (response.isChunked()) {
                        try {
                            response.commitStream();
                        }
                        catch (final Exception e) {
                            Responder.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_COMMITTING_OUTPUT_STREAM(), e);
                            close = true;
                        }
                        final ChunkedOutput chunked = (ChunkedOutput)entity;
                        try {
                            chunked.setContext(this.runtime.requestScope, this.runtime.requestScope.referenceCurrent(), request, response, (ConnectionCallback)this.connectionCallbackRunner);
                        }
                        catch (final IOException ex2) {
                            Responder.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_WRITING_RESPONSE_ENTITY_CHUNK(), ex2);
                            close = true;
                        }
                        if (!chunked.isClosed() && !writer.suspend(0L, TimeUnit.SECONDS, null)) {
                            Responder.LOGGER.fine(LocalizationMessages.ERROR_SUSPENDING_CHUNKED_OUTPUT_RESPONSE());
                        }
                    }
                    if (close) {
                        try {
                            response.close();
                        }
                        catch (final Exception e) {
                            Responder.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_CLOSING_COMMIT_OUTPUT_STREAM(), e);
                        }
                    }
                }
            }
            return response;
        }
        
        private void setWrittenResponse(final ContainerResponse response) {
            this.processingContext.monitoringEventBuilder().setContainerResponse(response).setSuccess(response.getStatus() < Response.Status.BAD_REQUEST.getStatusCode()).setResponseWritten(true);
        }
        
        private void release(final ContainerResponse responseContext) {
            try {
                this.processingContext.closeableService().close();
                if (responseContext != null && !responseContext.isChunked()) {
                    responseContext.close();
                }
            }
            catch (final Throwable throwable) {
                Responder.LOGGER.log(Level.WARNING, LocalizationMessages.RELEASING_REQUEST_PROCESSING_RESOURCES_FAILED(), throwable);
            }
            finally {
                this.runtime.externalRequestScope.close();
                this.processingContext.triggerEvent(RequestEvent.Type.FINISHED);
            }
        }
        
        static {
            LOGGER = Logger.getLogger(Responder.class.getName());
        }
    }
    
    private static class AsyncResponder implements AsyncContext, ContainerResponseWriter.TimeoutHandler, CompletionCallback
    {
        private static final Logger LOGGER;
        private static final javax.ws.rs.container.TimeoutHandler DEFAULT_TIMEOUT_HANDLER;
        private final Object stateLock;
        private State state;
        private boolean cancelled;
        private final Responder responder;
        private final RequestContext requestContext;
        private final ExternalRequestContext<?> foreignScopeInstance;
        private final ExternalRequestScope requestScopeListener;
        private volatile javax.ws.rs.container.TimeoutHandler timeoutHandler;
        private final List<AbstractCallbackRunner<?>> callbackRunners;
        
        public AsyncResponder(final Responder responder, final RequestContext requestContext, final ExternalRequestScope requestScopeListener, final ExternalRequestContext<?> foreignScopeInstance) {
            this.stateLock = new Object();
            this.state = State.RUNNING;
            this.cancelled = false;
            this.timeoutHandler = AsyncResponder.DEFAULT_TIMEOUT_HANDLER;
            this.responder = responder;
            this.requestContext = requestContext;
            this.foreignScopeInstance = foreignScopeInstance;
            this.requestScopeListener = requestScopeListener;
            this.callbackRunners = Collections.unmodifiableList((List<? extends AbstractCallbackRunner<?>>)Arrays.asList(responder.completionCallbackRunner, responder.connectionCallbackRunner));
            responder.completionCallbackRunner.register(this);
        }
        
        @Override
        public void onTimeout(final ContainerResponseWriter responseWriter) {
            final javax.ws.rs.container.TimeoutHandler handler = this.timeoutHandler;
            try {
                synchronized (this.stateLock) {
                    if (this.state == State.SUSPENDED) {
                        handler.handleTimeout((AsyncResponse)this);
                    }
                }
            }
            catch (final Throwable throwable) {
                this.resume(throwable);
            }
        }
        
        public void onComplete(final Throwable throwable) {
            synchronized (this.stateLock) {
                this.state = State.COMPLETED;
            }
        }
        
        @Override
        public void invokeManaged(final Producer<Response> producer) {
            ((ExecutorService)this.responder.runtime.managedAsyncExecutor.get()).submit(new Runnable() {
                @Override
                public void run() {
                    AsyncResponder.this.responder.runtime.requestScope.runInScope(AsyncResponder.this.requestContext, (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AsyncResponder.this.requestScopeListener.resume(AsyncResponder.this.foreignScopeInstance, AsyncResponder.this.responder.runtime.injectionManager);
                                final Response response = (Response)producer.call();
                                if (response != null) {
                                    AsyncResponder.this.resume(response);
                                }
                            }
                            catch (final Throwable t) {
                                AsyncResponder.this.resume(t);
                            }
                        }
                    });
                }
            });
        }
        
        @Override
        public boolean suspend() {
            synchronized (this.stateLock) {
                if (this.state == State.RUNNING && this.responder.processingContext.request().getResponseWriter().suspend(0L, TimeUnit.SECONDS, this)) {
                    this.state = State.SUSPENDED;
                    return true;
                }
            }
            return false;
        }
        
        public boolean resume(final Object response) {
            return this.resume(new Runnable() {
                @Override
                public void run() {
                    try {
                        AsyncResponder.this.requestScopeListener.resume(AsyncResponder.this.foreignScopeInstance, AsyncResponder.this.responder.runtime.injectionManager);
                        final Response jaxrsResponse = (Response)((response instanceof Response) ? response : Response.ok(response).build());
                        if (!AsyncResponder.this.responder.runtime.disableLocationHeaderRelativeUriResolution) {
                            ensureAbsolute(jaxrsResponse.getLocation(), (MultivaluedMap<String, Object>)jaxrsResponse.getHeaders(), AsyncResponder.this.responder.processingContext.request(), AsyncResponder.this.responder.runtime.rfc7231LocationHeaderRelativeUriResolution);
                        }
                        AsyncResponder.this.responder.process(new ContainerResponse(AsyncResponder.this.responder.processingContext.request(), jaxrsResponse));
                    }
                    catch (final Throwable t) {
                        AsyncResponder.this.responder.process(t);
                    }
                }
            });
        }
        
        public boolean resume(final Throwable error) {
            return this.resume(new Runnable() {
                @Override
                public void run() {
                    try {
                        AsyncResponder.this.requestScopeListener.resume(AsyncResponder.this.foreignScopeInstance, AsyncResponder.this.responder.runtime.injectionManager);
                        AsyncResponder.this.responder.process((Throwable)new MappableException(error));
                    }
                    catch (final Throwable t) {}
                }
            });
        }
        
        private boolean resume(final Runnable handler) {
            synchronized (this.stateLock) {
                if (this.state != State.SUSPENDED) {
                    return false;
                }
                this.state = State.RESUMED;
            }
            try {
                this.responder.runtime.requestScope.runInScope(this.requestContext, handler);
            }
            finally {
                this.requestContext.release();
            }
            return true;
        }
        
        public boolean cancel() {
            return this.cancel((Value<Response>)new Value<Response>() {
                public Response get() {
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
                }
            });
        }
        
        public boolean cancel(final int retryAfter) {
            return this.cancel((Value<Response>)new Value<Response>() {
                public Response get() {
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Retry-After", (Object)retryAfter).build();
                }
            });
        }
        
        public boolean cancel(final Date retryAfter) {
            return this.cancel((Value<Response>)new Value<Response>() {
                public Response get() {
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).header("Retry-After", (Object)retryAfter).build();
                }
            });
        }
        
        private boolean cancel(final Value<Response> responseValue) {
            synchronized (this.stateLock) {
                if (this.cancelled) {
                    return true;
                }
                if (this.state != State.SUSPENDED) {
                    return false;
                }
                this.state = State.RESUMED;
                this.cancelled = true;
            }
            this.responder.runtime.requestScope.runInScope(this.requestContext, (Runnable)new Runnable() {
                @Override
                public void run() {
                    try {
                        AsyncResponder.this.requestScopeListener.resume(AsyncResponder.this.foreignScopeInstance, AsyncResponder.this.responder.runtime.injectionManager);
                        final Response response = (Response)responseValue.get();
                        AsyncResponder.this.responder.process(new ContainerResponse(AsyncResponder.this.responder.processingContext.request(), response));
                    }
                    catch (final Throwable t) {
                        AsyncResponder.this.responder.process(t);
                    }
                }
            });
            return true;
        }
        
        public boolean isRunning() {
            synchronized (this.stateLock) {
                return this.state == State.RUNNING;
            }
        }
        
        public boolean isSuspended() {
            synchronized (this.stateLock) {
                return this.state == State.SUSPENDED;
            }
        }
        
        public boolean isCancelled() {
            synchronized (this.stateLock) {
                return this.cancelled;
            }
        }
        
        public boolean isDone() {
            synchronized (this.stateLock) {
                return this.state == State.COMPLETED;
            }
        }
        
        public boolean setTimeout(final long time, final TimeUnit unit) {
            try {
                this.responder.processingContext.request().getResponseWriter().setSuspendTimeout(time, unit);
                return true;
            }
            catch (final IllegalStateException ex) {
                AsyncResponder.LOGGER.log(Level.FINER, "Unable to set timeout on the AsyncResponse.", ex);
                return false;
            }
        }
        
        public void setTimeoutHandler(final javax.ws.rs.container.TimeoutHandler handler) {
            this.timeoutHandler = handler;
        }
        
        public Collection<Class<?>> register(final Class<?> callback) {
            Preconditions.checkNotNull((Object)callback, (Object)LocalizationMessages.PARAM_NULL("callback"));
            return this.register(Injections.getOrCreate(this.responder.runtime.injectionManager, (Class)callback));
        }
        
        public Map<Class<?>, Collection<Class<?>>> register(final Class<?> callback, final Class<?>... callbacks) {
            Preconditions.checkNotNull((Object)callback, (Object)LocalizationMessages.PARAM_NULL("callback"));
            Preconditions.checkNotNull((Object)callbacks, (Object)LocalizationMessages.CALLBACK_ARRAY_NULL());
            for (final Class<?> additionalCallback : callbacks) {
                Preconditions.checkNotNull((Object)additionalCallback, (Object)LocalizationMessages.CALLBACK_ARRAY_ELEMENT_NULL());
            }
            final Map<Class<?>, Collection<Class<?>>> results = new HashMap<Class<?>, Collection<Class<?>>>();
            results.put(callback, this.register(callback));
            for (final Class<?> c : callbacks) {
                results.put(c, this.register(c));
            }
            return results;
        }
        
        public Collection<Class<?>> register(final Object callback) {
            Preconditions.checkNotNull(callback, (Object)LocalizationMessages.PARAM_NULL("callback"));
            final Collection<Class<?>> result = new LinkedList<Class<?>>();
            for (final AbstractCallbackRunner<?> runner : this.callbackRunners) {
                if (runner.supports(callback.getClass()) && runner.register(callback)) {
                    result.add(runner.getCallbackContract());
                }
            }
            return result;
        }
        
        public Map<Class<?>, Collection<Class<?>>> register(final Object callback, final Object... callbacks) {
            Preconditions.checkNotNull(callback, (Object)LocalizationMessages.PARAM_NULL("callback"));
            Preconditions.checkNotNull((Object)callbacks, (Object)LocalizationMessages.CALLBACK_ARRAY_NULL());
            for (final Object additionalCallback : callbacks) {
                Preconditions.checkNotNull(additionalCallback, (Object)LocalizationMessages.CALLBACK_ARRAY_ELEMENT_NULL());
            }
            final Map<Class<?>, Collection<Class<?>>> results = new HashMap<Class<?>, Collection<Class<?>>>();
            results.put(callback.getClass(), this.register(callback));
            for (final Object c : callbacks) {
                results.put(c.getClass(), this.register(c));
            }
            return results;
        }
        
        static {
            LOGGER = Logger.getLogger(AsyncResponder.class.getName());
            DEFAULT_TIMEOUT_HANDLER = (javax.ws.rs.container.TimeoutHandler)new javax.ws.rs.container.TimeoutHandler() {
                public void handleTimeout(final AsyncResponse asyncResponse) {
                    throw new ServiceUnavailableException();
                }
            };
        }
    }
    
    abstract static class AbstractCallbackRunner<T>
    {
        private final Queue<T> callbacks;
        private final Logger logger;
        
        protected AbstractCallbackRunner(final Logger logger) {
            this.callbacks = new ConcurrentLinkedQueue<T>();
            this.logger = logger;
        }
        
        public final boolean supports(final Class<?> callbackClass) {
            return this.getCallbackContract().isAssignableFrom(callbackClass);
        }
        
        public abstract Class<?> getCallbackContract();
        
        public boolean register(final Object callback) {
            return this.callbacks.offer((T)callback);
        }
        
        protected final void executeCallbacks(final Closure<T> invoker) {
            for (final T callback : this.callbacks) {
                try {
                    invoker.invoke((Object)callback);
                }
                catch (final Throwable t) {
                    this.logger.log(Level.WARNING, LocalizationMessages.ERROR_ASYNC_CALLBACK_FAILED(callback.getClass().getName()), t);
                }
            }
        }
    }
    
    private static class CompletionCallbackRunner extends AbstractCallbackRunner<CompletionCallback> implements CompletionCallback
    {
        private static final Logger LOGGER;
        
        private CompletionCallbackRunner() {
            super(CompletionCallbackRunner.LOGGER);
        }
        
        @Override
        public Class<?> getCallbackContract() {
            return CompletionCallback.class;
        }
        
        public void onComplete(final Throwable throwable) {
            this.executeCallbacks((org.glassfish.jersey.internal.util.Closure<CompletionCallback>)new Closure<CompletionCallback>() {
                public void invoke(final CompletionCallback callback) {
                    callback.onComplete(throwable);
                }
            });
        }
        
        static {
            LOGGER = Logger.getLogger(CompletionCallbackRunner.class.getName());
        }
    }
    
    private static class ConnectionCallbackRunner extends AbstractCallbackRunner<ConnectionCallback> implements ConnectionCallback
    {
        private static final Logger LOGGER;
        
        private ConnectionCallbackRunner() {
            super(ConnectionCallbackRunner.LOGGER);
        }
        
        @Override
        public Class<?> getCallbackContract() {
            return ConnectionCallback.class;
        }
        
        public void onDisconnect(final AsyncResponse disconnected) {
            this.executeCallbacks((org.glassfish.jersey.internal.util.Closure<ConnectionCallback>)new Closure<ConnectionCallback>() {
                public void invoke(final ConnectionCallback callback) {
                    callback.onDisconnect(disconnected);
                }
            });
        }
        
        static {
            LOGGER = Logger.getLogger(ConnectionCallbackRunner.class.getName());
        }
    }
}
