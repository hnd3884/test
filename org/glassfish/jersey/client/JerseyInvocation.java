package org.glassfish.jersey.client;

import java.util.Iterator;
import javax.ws.rs.client.SyncInvoker;
import org.glassfish.jersey.internal.inject.Providers;
import javax.ws.rs.client.RxInvokerProvider;
import java.util.concurrent.ExecutorService;
import javax.ws.rs.client.RxInvoker;
import org.glassfish.jersey.spi.ExecutorServiceProvider;
import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Entity;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import java.net.URI;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.BadRequestException;
import java.util.concurrent.CancellationException;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.ResponseProcessingException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.process.internal.RequestScope;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.client.Invocation;

public class JerseyInvocation implements Invocation
{
    private static final Logger LOGGER;
    private final ClientRequest requestContext;
    private final boolean copyRequestContext;
    private static final Map<String, EntityPresence> METHODS;
    
    private JerseyInvocation(final Builder builder) {
        this(builder, false);
    }
    
    private JerseyInvocation(final Builder builder, final boolean copyRequestContext) {
        this.validateHttpMethodAndEntity(builder.requestContext);
        this.requestContext = new ClientRequest(builder.requestContext);
        this.copyRequestContext = copyRequestContext;
    }
    
    private static Map<String, EntityPresence> initializeMap() {
        final Map<String, EntityPresence> map = new HashMap<String, EntityPresence>();
        map.put("DELETE", EntityPresence.MUST_BE_NULL);
        map.put("GET", EntityPresence.MUST_BE_NULL);
        map.put("HEAD", EntityPresence.MUST_BE_NULL);
        map.put("OPTIONS", EntityPresence.MUST_BE_NULL);
        map.put("PATCH", EntityPresence.MUST_BE_PRESENT);
        map.put("POST", EntityPresence.OPTIONAL);
        map.put("PUT", EntityPresence.MUST_BE_PRESENT);
        map.put("TRACE", EntityPresence.MUST_BE_NULL);
        return map;
    }
    
    private void validateHttpMethodAndEntity(final ClientRequest request) {
        boolean suppressExceptions = PropertiesHelper.isProperty(request.getConfiguration().getProperty("jersey.config.client.suppressHttpComplianceValidation"));
        final Object shcvProperty = request.getProperty("jersey.config.client.suppressHttpComplianceValidation");
        if (shcvProperty != null) {
            suppressExceptions = PropertiesHelper.isProperty(shcvProperty);
        }
        final String method = request.getMethod();
        final EntityPresence entityPresence = JerseyInvocation.METHODS.get(method.toUpperCase());
        if (entityPresence == EntityPresence.MUST_BE_NULL && request.hasEntity()) {
            if (!suppressExceptions) {
                throw new IllegalStateException(LocalizationMessages.ERROR_HTTP_METHOD_ENTITY_NOT_NULL(method));
            }
            JerseyInvocation.LOGGER.warning(LocalizationMessages.ERROR_HTTP_METHOD_ENTITY_NOT_NULL(method));
        }
        else if (entityPresence == EntityPresence.MUST_BE_PRESENT && !request.hasEntity()) {
            if (!suppressExceptions) {
                throw new IllegalStateException(LocalizationMessages.ERROR_HTTP_METHOD_ENTITY_NULL(method));
            }
            JerseyInvocation.LOGGER.warning(LocalizationMessages.ERROR_HTTP_METHOD_ENTITY_NULL(method));
        }
    }
    
    private ClientRequest requestForCall(final ClientRequest requestContext) {
        return this.copyRequestContext ? new ClientRequest(requestContext) : requestContext;
    }
    
    public Response invoke() throws ProcessingException, WebApplicationException {
        final ClientRuntime runtime = this.request().getClientRuntime();
        final RequestScope requestScope = runtime.getRequestScope();
        return (Response)requestScope.runInScope(() -> new InboundJaxrsResponse(runtime.invoke(this.requestForCall(this.requestContext)), requestScope));
    }
    
    public <T> T invoke(final Class<T> responseType) throws ProcessingException, WebApplicationException {
        if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
        }
        final ClientRuntime runtime = this.request().getClientRuntime();
        final RequestScope requestScope = runtime.getRequestScope();
        return (T)requestScope.runInScope(() -> {
            try {
                return this.translate(runtime.invoke(this.requestForCall(this.requestContext)), requestScope, (Class<Object>)responseType);
            }
            catch (final ProcessingException ex) {
                if (ex.getCause() instanceof WebApplicationException) {
                    throw (WebApplicationException)ex.getCause();
                }
                throw ex;
            }
        });
    }
    
    public <T> T invoke(final GenericType<T> responseType) throws ProcessingException, WebApplicationException {
        if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
        }
        final ClientRuntime runtime = this.request().getClientRuntime();
        final RequestScope requestScope = runtime.getRequestScope();
        return (T)requestScope.runInScope(() -> {
            try {
                return this.translate(runtime.invoke(this.requestForCall(this.requestContext)), requestScope, (javax.ws.rs.core.GenericType<Object>)responseType);
            }
            catch (final ProcessingException ex) {
                if (ex.getCause() instanceof WebApplicationException) {
                    throw (WebApplicationException)ex.getCause();
                }
                throw ex;
            }
        });
    }
    
    public Future<Response> submit() {
        final CompletableFuture<Response> responseFuture = new CompletableFuture<Response>();
        final ClientRuntime runtime = this.request().getClientRuntime();
        runtime.submit(runtime.createRunnableForAsyncProcessing(this.requestForCall(this.requestContext), new ResponseCallback() {
            @Override
            public void completed(final ClientResponse response, final RequestScope scope) {
                if (!responseFuture.isCancelled()) {
                    responseFuture.complete(new InboundJaxrsResponse(response, scope));
                }
                else {
                    response.close();
                }
            }
            
            @Override
            public void failed(final ProcessingException error) {
                if (!responseFuture.isCancelled()) {
                    responseFuture.completeExceptionally((Throwable)error);
                }
            }
        }));
        return responseFuture;
    }
    
    public <T> Future<T> submit(final Class<T> responseType) {
        if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
        }
        final CompletableFuture<T> responseFuture = new CompletableFuture<T>();
        final ClientRuntime runtime = this.request().getClientRuntime();
        runtime.submit(runtime.createRunnableForAsyncProcessing(this.requestForCall(this.requestContext), new ResponseCallback() {
            @Override
            public void completed(final ClientResponse response, final RequestScope scope) {
                if (responseFuture.isCancelled()) {
                    response.close();
                    return;
                }
                try {
                    responseFuture.complete(JerseyInvocation.this.translate(response, scope, (Class<Object>)responseType));
                }
                catch (final ProcessingException ex) {
                    this.failed(ex);
                }
            }
            
            @Override
            public void failed(final ProcessingException error) {
                if (responseFuture.isCancelled()) {
                    return;
                }
                if (error.getCause() instanceof WebApplicationException) {
                    responseFuture.completeExceptionally(error.getCause());
                }
                else {
                    responseFuture.completeExceptionally((Throwable)error);
                }
            }
        }));
        return responseFuture;
    }
    
    private <T> T translate(final ClientResponse response, final RequestScope scope, final Class<T> responseType) throws ProcessingException {
        if (responseType == Response.class) {
            return responseType.cast(new InboundJaxrsResponse(response, scope));
        }
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            try {
                return response.readEntity(responseType);
            }
            catch (final ProcessingException ex) {
                if (ex.getClass() == ProcessingException.class) {
                    throw new ResponseProcessingException((Response)new InboundJaxrsResponse(response, scope), ex.getCause());
                }
                throw new ResponseProcessingException((Response)new InboundJaxrsResponse(response, scope), (Throwable)ex);
            }
            catch (final WebApplicationException ex2) {
                throw new ResponseProcessingException((Response)new InboundJaxrsResponse(response, scope), (Throwable)ex2);
            }
            catch (final Exception ex3) {
                throw new ResponseProcessingException((Response)new InboundJaxrsResponse(response, scope), LocalizationMessages.UNEXPECTED_ERROR_RESPONSE_PROCESSING(), (Throwable)ex3);
            }
        }
        throw this.convertToException(new InboundJaxrsResponse(response, scope));
    }
    
    public <T> Future<T> submit(final GenericType<T> responseType) {
        if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
        }
        final CompletableFuture<T> responseFuture = new CompletableFuture<T>();
        final ClientRuntime runtime = this.request().getClientRuntime();
        runtime.submit(runtime.createRunnableForAsyncProcessing(this.requestForCall(this.requestContext), new ResponseCallback() {
            @Override
            public void completed(final ClientResponse response, final RequestScope scope) {
                if (responseFuture.isCancelled()) {
                    response.close();
                    return;
                }
                try {
                    responseFuture.complete(JerseyInvocation.this.translate(response, scope, (javax.ws.rs.core.GenericType<Object>)responseType));
                }
                catch (final ProcessingException ex) {
                    this.failed(ex);
                }
            }
            
            @Override
            public void failed(final ProcessingException error) {
                if (responseFuture.isCancelled()) {
                    return;
                }
                if (error.getCause() instanceof WebApplicationException) {
                    responseFuture.completeExceptionally(error.getCause());
                }
                else {
                    responseFuture.completeExceptionally((Throwable)error);
                }
            }
        }));
        return responseFuture;
    }
    
    private <T> T translate(final ClientResponse response, final RequestScope scope, final GenericType<T> responseType) throws ProcessingException {
        if (responseType.getRawType() == Response.class) {
            return (T)new InboundJaxrsResponse(response, scope);
        }
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            try {
                return response.readEntity(responseType);
            }
            catch (final ProcessingException ex) {
                throw new ResponseProcessingException((Response)new InboundJaxrsResponse(response, scope), (Throwable)((ex.getCause() != null) ? ex.getCause() : ex));
            }
            catch (final WebApplicationException ex2) {
                throw new ResponseProcessingException((Response)new InboundJaxrsResponse(response, scope), (Throwable)ex2);
            }
            catch (final Exception ex3) {
                throw new ResponseProcessingException((Response)new InboundJaxrsResponse(response, scope), LocalizationMessages.UNEXPECTED_ERROR_RESPONSE_PROCESSING(), (Throwable)ex3);
            }
        }
        throw this.convertToException(new InboundJaxrsResponse(response, scope));
    }
    
    public <T> Future<T> submit(final InvocationCallback<T> callback) {
        return this.submit(null, callback);
    }
    
    public <T> Future<T> submit(final GenericType<T> responseType, final InvocationCallback<T> callback) {
        final CompletableFuture<T> responseFuture = new CompletableFuture<T>();
        try {
            final ReflectionHelper.DeclaringClassInterfacePair pair = ReflectionHelper.getClass((Class)callback.getClass(), (Class)InvocationCallback.class);
            Type callbackParamType;
            Class<T> callbackParamClass;
            if (responseType == null) {
                final Type[] typeArguments = ReflectionHelper.getParameterizedTypeArguments(pair);
                if (typeArguments == null || typeArguments.length == 0) {
                    callbackParamType = Object.class;
                }
                else {
                    callbackParamType = typeArguments[0];
                }
                callbackParamClass = ReflectionHelper.erasure(callbackParamType);
            }
            else {
                callbackParamType = responseType.getType();
                callbackParamClass = ReflectionHelper.erasure((Type)responseType.getRawType());
            }
            final ResponseCallback responseCallback = new ResponseCallback() {
                @Override
                public void completed(final ClientResponse response, final RequestScope scope) {
                    if (responseFuture.isCancelled()) {
                        response.close();
                        this.failed(new ProcessingException((Throwable)new CancellationException(LocalizationMessages.ERROR_REQUEST_CANCELLED())));
                        return;
                    }
                    if (callbackParamClass == Response.class) {
                        final T result = callbackParamClass.cast(new InboundJaxrsResponse(response, scope));
                        responseFuture.complete(result);
                        callback.completed((Object)result);
                    }
                    else if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                        final T result = response.readEntity((javax.ws.rs.core.GenericType<T>)new GenericType(callbackParamType));
                        responseFuture.complete(result);
                        callback.completed((Object)result);
                    }
                    else {
                        this.failed(JerseyInvocation.this.convertToException(new InboundJaxrsResponse(response, scope)));
                    }
                }
                
                @Override
                public void failed(final ProcessingException error) {
                    try {
                        if (error.getCause() instanceof WebApplicationException) {
                            responseFuture.completeExceptionally(error.getCause());
                        }
                        else if (!responseFuture.isCancelled()) {
                            responseFuture.completeExceptionally((Throwable)error);
                        }
                    }
                    finally {
                        callback.failed((error.getCause() instanceof CancellationException) ? error.getCause() : error);
                    }
                }
            };
            final ClientRuntime runtime = this.request().getClientRuntime();
            runtime.submit(runtime.createRunnableForAsyncProcessing(this.requestForCall(this.requestContext), responseCallback));
        }
        catch (final Throwable error) {
            ProcessingException ce;
            if (error instanceof ProcessingException) {
                ce = (ProcessingException)error;
                responseFuture.completeExceptionally((Throwable)ce);
            }
            else if (error instanceof WebApplicationException) {
                ce = new ProcessingException(error);
                responseFuture.completeExceptionally(error);
            }
            else {
                ce = new ProcessingException(error);
                responseFuture.completeExceptionally((Throwable)ce);
            }
            callback.failed((Throwable)ce);
        }
        return responseFuture;
    }
    
    public JerseyInvocation property(final String name, final Object value) {
        this.requestContext.setProperty(name, value);
        return this;
    }
    
    private ProcessingException convertToException(final Response response) {
        try {
            response.bufferEntity();
            final int statusCode = response.getStatus();
            final Response.Status status = Response.Status.fromStatusCode(statusCode);
            WebApplicationException webAppException = null;
            if (status == null) {
                final Response.Status.Family statusFamily = response.getStatusInfo().getFamily();
                webAppException = this.createExceptionForFamily(response, statusFamily);
            }
            else {
                switch (status) {
                    case BAD_REQUEST: {
                        webAppException = (WebApplicationException)new BadRequestException(response);
                        break;
                    }
                    case UNAUTHORIZED: {
                        webAppException = (WebApplicationException)new NotAuthorizedException(response);
                        break;
                    }
                    case FORBIDDEN: {
                        webAppException = (WebApplicationException)new ForbiddenException(response);
                        break;
                    }
                    case NOT_FOUND: {
                        webAppException = (WebApplicationException)new NotFoundException(response);
                        break;
                    }
                    case METHOD_NOT_ALLOWED: {
                        webAppException = (WebApplicationException)new NotAllowedException(response);
                        break;
                    }
                    case NOT_ACCEPTABLE: {
                        webAppException = (WebApplicationException)new NotAcceptableException(response);
                        break;
                    }
                    case UNSUPPORTED_MEDIA_TYPE: {
                        webAppException = (WebApplicationException)new NotSupportedException(response);
                        break;
                    }
                    case INTERNAL_SERVER_ERROR: {
                        webAppException = (WebApplicationException)new InternalServerErrorException(response);
                        break;
                    }
                    case SERVICE_UNAVAILABLE: {
                        webAppException = (WebApplicationException)new ServiceUnavailableException(response);
                        break;
                    }
                    default: {
                        final Response.Status.Family statusFamily = response.getStatusInfo().getFamily();
                        webAppException = this.createExceptionForFamily(response, statusFamily);
                        break;
                    }
                }
            }
            return (ProcessingException)new ResponseProcessingException(response, (Throwable)webAppException);
        }
        catch (final Throwable t) {
            return (ProcessingException)new ResponseProcessingException(response, LocalizationMessages.RESPONSE_TO_EXCEPTION_CONVERSION_FAILED(), t);
        }
    }
    
    private WebApplicationException createExceptionForFamily(final Response response, final Response.Status.Family statusFamily) {
        WebApplicationException webAppException = null;
        switch (statusFamily) {
            case REDIRECTION: {
                webAppException = (WebApplicationException)new RedirectionException(response);
                break;
            }
            case CLIENT_ERROR: {
                webAppException = (WebApplicationException)new ClientErrorException(response);
                break;
            }
            case SERVER_ERROR: {
                webAppException = (WebApplicationException)new ServerErrorException(response);
                break;
            }
            default: {
                webAppException = new WebApplicationException(response);
                break;
            }
        }
        return webAppException;
    }
    
    ClientRequest request() {
        return this.requestContext;
    }
    
    static {
        LOGGER = Logger.getLogger(JerseyInvocation.class.getName());
        METHODS = initializeMap();
    }
    
    private enum EntityPresence
    {
        MUST_BE_NULL, 
        MUST_BE_PRESENT, 
        OPTIONAL;
    }
    
    public static class Builder implements Invocation.Builder
    {
        private final ClientRequest requestContext;
        
        protected Builder(final URI uri, final ClientConfig configuration) {
            this.requestContext = new ClientRequest(uri, configuration, (PropertiesDelegate)new MapPropertiesDelegate());
        }
        
        ClientRequest request() {
            return this.requestContext;
        }
        
        private void storeEntity(final Entity<?> entity) {
            if (entity != null) {
                this.requestContext.variant(entity.getVariant());
                this.requestContext.setEntity(entity.getEntity());
                this.requestContext.setEntityAnnotations(entity.getAnnotations());
            }
        }
        
        public JerseyInvocation build(final String method) {
            this.requestContext.setMethod(method);
            return new JerseyInvocation(this, true, null);
        }
        
        public JerseyInvocation build(final String method, final Entity<?> entity) {
            this.requestContext.setMethod(method);
            this.storeEntity(entity);
            return new JerseyInvocation(this, true, null);
        }
        
        public JerseyInvocation buildGet() {
            this.requestContext.setMethod("GET");
            return new JerseyInvocation(this, true, null);
        }
        
        public JerseyInvocation buildDelete() {
            this.requestContext.setMethod("DELETE");
            return new JerseyInvocation(this, true, null);
        }
        
        public JerseyInvocation buildPost(final Entity<?> entity) {
            this.requestContext.setMethod("POST");
            this.storeEntity(entity);
            return new JerseyInvocation(this, true, null);
        }
        
        public JerseyInvocation buildPut(final Entity<?> entity) {
            this.requestContext.setMethod("PUT");
            this.storeEntity(entity);
            return new JerseyInvocation(this, true, null);
        }
        
        public javax.ws.rs.client.AsyncInvoker async() {
            return (javax.ws.rs.client.AsyncInvoker)new AsyncInvoker(this);
        }
        
        public Builder accept(final String... mediaTypes) {
            this.requestContext.accept(mediaTypes);
            return this;
        }
        
        public Builder accept(final MediaType... mediaTypes) {
            this.requestContext.accept(mediaTypes);
            return this;
        }
        
        public Invocation.Builder acceptEncoding(final String... encodings) {
            this.requestContext.getHeaders().addAll((Object)"Accept-Encoding", (Object[])encodings);
            return (Invocation.Builder)this;
        }
        
        public Builder acceptLanguage(final Locale... locales) {
            this.requestContext.acceptLanguage(locales);
            return this;
        }
        
        public Builder acceptLanguage(final String... locales) {
            this.requestContext.acceptLanguage(locales);
            return this;
        }
        
        public Builder cookie(final Cookie cookie) {
            this.requestContext.cookie(cookie);
            return this;
        }
        
        public Builder cookie(final String name, final String value) {
            this.requestContext.cookie(new Cookie(name, value));
            return this;
        }
        
        public Builder cacheControl(final CacheControl cacheControl) {
            this.requestContext.cacheControl(cacheControl);
            return this;
        }
        
        public Builder header(final String name, final Object value) {
            final MultivaluedMap<String, Object> headers = (MultivaluedMap<String, Object>)this.requestContext.getHeaders();
            if (value == null) {
                headers.remove((Object)name);
            }
            else {
                headers.add((Object)name, value);
            }
            if ("User-Agent".equalsIgnoreCase(name)) {
                this.requestContext.ignoreUserAgent(value == null);
            }
            return this;
        }
        
        public Builder headers(final MultivaluedMap<String, Object> headers) {
            this.requestContext.replaceHeaders((MultivaluedMap)headers);
            return this;
        }
        
        public Response get() throws ProcessingException {
            return this.method("GET");
        }
        
        public <T> T get(final Class<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("GET", responseType);
        }
        
        public <T> T get(final GenericType<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("GET", responseType);
        }
        
        public Response put(final Entity<?> entity) throws ProcessingException {
            return this.method("PUT", entity);
        }
        
        public <T> T put(final Entity<?> entity, final Class<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("PUT", entity, responseType);
        }
        
        public <T> T put(final Entity<?> entity, final GenericType<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("PUT", entity, responseType);
        }
        
        public Response post(final Entity<?> entity) throws ProcessingException {
            return this.method("POST", entity);
        }
        
        public <T> T post(final Entity<?> entity, final Class<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("POST", entity, responseType);
        }
        
        public <T> T post(final Entity<?> entity, final GenericType<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("POST", entity, responseType);
        }
        
        public Response delete() throws ProcessingException {
            return this.method("DELETE");
        }
        
        public <T> T delete(final Class<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("DELETE", responseType);
        }
        
        public <T> T delete(final GenericType<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("DELETE", responseType);
        }
        
        public Response head() throws ProcessingException {
            return this.method("HEAD");
        }
        
        public Response options() throws ProcessingException {
            return this.method("OPTIONS");
        }
        
        public <T> T options(final Class<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("OPTIONS", responseType);
        }
        
        public <T> T options(final GenericType<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("OPTIONS", responseType);
        }
        
        public Response trace() throws ProcessingException {
            return this.method("TRACE");
        }
        
        public <T> T trace(final Class<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("TRACE", responseType);
        }
        
        public <T> T trace(final GenericType<T> responseType) throws ProcessingException, WebApplicationException {
            return this.method("TRACE", responseType);
        }
        
        public Response method(final String name) throws ProcessingException {
            this.requestContext.setMethod(name);
            return new JerseyInvocation(this, null).invoke();
        }
        
        public <T> T method(final String name, final Class<T> responseType) throws ProcessingException, WebApplicationException {
            if (responseType == null) {
                throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
            }
            this.requestContext.setMethod(name);
            return new JerseyInvocation(this, null).invoke(responseType);
        }
        
        public <T> T method(final String name, final GenericType<T> responseType) throws ProcessingException, WebApplicationException {
            if (responseType == null) {
                throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
            }
            this.requestContext.setMethod(name);
            return new JerseyInvocation(this, null).invoke(responseType);
        }
        
        public Response method(final String name, final Entity<?> entity) throws ProcessingException {
            this.requestContext.setMethod(name);
            this.storeEntity(entity);
            return new JerseyInvocation(this, null).invoke();
        }
        
        public <T> T method(final String name, final Entity<?> entity, final Class<T> responseType) throws ProcessingException, WebApplicationException {
            if (responseType == null) {
                throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
            }
            this.requestContext.setMethod(name);
            this.storeEntity(entity);
            return new JerseyInvocation(this, null).invoke(responseType);
        }
        
        public <T> T method(final String name, final Entity<?> entity, final GenericType<T> responseType) throws ProcessingException, WebApplicationException {
            if (responseType == null) {
                throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
            }
            this.requestContext.setMethod(name);
            this.storeEntity(entity);
            return new JerseyInvocation(this, null).invoke(responseType);
        }
        
        public Builder property(final String name, final Object value) {
            this.requestContext.setProperty(name, value);
            return this;
        }
        
        public CompletionStageRxInvoker rx() {
            final ExecutorServiceProvider instance = (ExecutorServiceProvider)this.requestContext.getInjectionManager().getInstance((Class)ExecutorServiceProvider.class);
            return (CompletionStageRxInvoker)new JerseyCompletionStageRxInvoker((Invocation.Builder)this, instance.getExecutorService());
        }
        
        public <T extends RxInvoker> T rx(final Class<T> clazz) {
            final ExecutorServiceProvider instance = (ExecutorServiceProvider)this.requestContext.getInjectionManager().getInstance((Class)ExecutorServiceProvider.class);
            return this.createRxInvoker(clazz, instance.getExecutorService());
        }
        
        private <T extends RxInvoker> T rx(final Class<T> clazz, final ExecutorService executorService) {
            if (executorService == null) {
                throw new IllegalArgumentException(LocalizationMessages.NULL_INPUT_PARAMETER("executorService"));
            }
            return (T)this.createRxInvoker(clazz, executorService);
        }
        
        private <T extends RxInvoker> T createRxInvoker(final Class<? extends RxInvoker> clazz, final ExecutorService executorService) {
            if (clazz == null) {
                throw new IllegalArgumentException(LocalizationMessages.NULL_INPUT_PARAMETER("clazz"));
            }
            final Iterable<RxInvokerProvider> allProviders = Providers.getAllProviders(this.requestContext.getInjectionManager(), (Class)RxInvokerProvider.class);
            for (final RxInvokerProvider invokerProvider : allProviders) {
                if (invokerProvider.isProviderFor((Class)clazz)) {
                    final RxInvoker rxInvoker = invokerProvider.getRxInvoker((SyncInvoker)this, executorService);
                    if (rxInvoker == null) {
                        throw new IllegalStateException(LocalizationMessages.CLIENT_RX_PROVIDER_NULL());
                    }
                    return (T)rxInvoker;
                }
            }
            throw new IllegalStateException(LocalizationMessages.CLIENT_RX_PROVIDER_NOT_REGISTERED(clazz.getSimpleName()));
        }
    }
    
    private static class AsyncInvoker implements javax.ws.rs.client.AsyncInvoker
    {
        private final Builder builder;
        
        private AsyncInvoker(final Builder request) {
            this.builder = request;
            this.builder.requestContext.setAsynchronous(true);
        }
        
        public Future<Response> get() {
            return this.method("GET");
        }
        
        public <T> Future<T> get(final Class<T> responseType) {
            return this.method("GET", responseType);
        }
        
        public <T> Future<T> get(final GenericType<T> responseType) {
            return this.method("GET", responseType);
        }
        
        public <T> Future<T> get(final InvocationCallback<T> callback) {
            return this.method("GET", callback);
        }
        
        public Future<Response> put(final Entity<?> entity) {
            return this.method("PUT", entity);
        }
        
        public <T> Future<T> put(final Entity<?> entity, final Class<T> responseType) {
            return this.method("PUT", entity, responseType);
        }
        
        public <T> Future<T> put(final Entity<?> entity, final GenericType<T> responseType) {
            return this.method("PUT", entity, responseType);
        }
        
        public <T> Future<T> put(final Entity<?> entity, final InvocationCallback<T> callback) {
            return this.method("PUT", entity, callback);
        }
        
        public Future<Response> post(final Entity<?> entity) {
            return this.method("POST", entity);
        }
        
        public <T> Future<T> post(final Entity<?> entity, final Class<T> responseType) {
            return this.method("POST", entity, responseType);
        }
        
        public <T> Future<T> post(final Entity<?> entity, final GenericType<T> responseType) {
            return this.method("POST", entity, responseType);
        }
        
        public <T> Future<T> post(final Entity<?> entity, final InvocationCallback<T> callback) {
            return this.method("POST", entity, callback);
        }
        
        public Future<Response> delete() {
            return this.method("DELETE");
        }
        
        public <T> Future<T> delete(final Class<T> responseType) {
            return this.method("DELETE", responseType);
        }
        
        public <T> Future<T> delete(final GenericType<T> responseType) {
            return this.method("DELETE", responseType);
        }
        
        public <T> Future<T> delete(final InvocationCallback<T> callback) {
            return this.method("DELETE", callback);
        }
        
        public Future<Response> head() {
            return this.method("HEAD");
        }
        
        public Future<Response> head(final InvocationCallback<Response> callback) {
            return this.method("HEAD", callback);
        }
        
        public Future<Response> options() {
            return this.method("OPTIONS");
        }
        
        public <T> Future<T> options(final Class<T> responseType) {
            return this.method("OPTIONS", responseType);
        }
        
        public <T> Future<T> options(final GenericType<T> responseType) {
            return this.method("OPTIONS", responseType);
        }
        
        public <T> Future<T> options(final InvocationCallback<T> callback) {
            return this.method("OPTIONS", callback);
        }
        
        public Future<Response> trace() {
            return this.method("TRACE");
        }
        
        public <T> Future<T> trace(final Class<T> responseType) {
            return this.method("TRACE", responseType);
        }
        
        public <T> Future<T> trace(final GenericType<T> responseType) {
            return this.method("TRACE", responseType);
        }
        
        public <T> Future<T> trace(final InvocationCallback<T> callback) {
            return this.method("TRACE", callback);
        }
        
        public Future<Response> method(final String name) {
            this.builder.requestContext.setMethod(name);
            return new JerseyInvocation(this.builder, null).submit();
        }
        
        public <T> Future<T> method(final String name, final Class<T> responseType) {
            if (responseType == null) {
                throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
            }
            this.builder.requestContext.setMethod(name);
            return new JerseyInvocation(this.builder, null).submit(responseType);
        }
        
        public <T> Future<T> method(final String name, final GenericType<T> responseType) {
            if (responseType == null) {
                throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
            }
            this.builder.requestContext.setMethod(name);
            return new JerseyInvocation(this.builder, null).submit(responseType);
        }
        
        public <T> Future<T> method(final String name, final InvocationCallback<T> callback) {
            this.builder.requestContext.setMethod(name);
            return new JerseyInvocation(this.builder, null).submit(callback);
        }
        
        public Future<Response> method(final String name, final Entity<?> entity) {
            this.builder.requestContext.setMethod(name);
            this.builder.storeEntity(entity);
            return new JerseyInvocation(this.builder, null).submit();
        }
        
        public <T> Future<T> method(final String name, final Entity<?> entity, final Class<T> responseType) {
            if (responseType == null) {
                throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
            }
            this.builder.requestContext.setMethod(name);
            this.builder.storeEntity(entity);
            return new JerseyInvocation(this.builder, null).submit(responseType);
        }
        
        public <T> Future<T> method(final String name, final Entity<?> entity, final GenericType<T> responseType) {
            if (responseType == null) {
                throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
            }
            this.builder.requestContext.setMethod(name);
            this.builder.storeEntity(entity);
            return new JerseyInvocation(this.builder, null).submit(responseType);
        }
        
        public <T> Future<T> method(final String name, final Entity<?> entity, final InvocationCallback<T> callback) {
            this.builder.requestContext.setMethod(name);
            this.builder.storeEntity(entity);
            return new JerseyInvocation(this.builder, null).submit(callback);
        }
    }
}
