package org.glassfish.jersey.client;

import javax.ws.rs.core.GenericType;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.message.MessageBodyWorkers;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Collections;
import org.glassfish.jersey.internal.Version;
import javax.ws.rs.ProcessingException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import java.util.Iterator;
import org.glassfish.jersey.process.internal.ChainableStage;
import org.glassfish.jersey.internal.util.collection.Ref;
import javax.inject.Provider;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import java.util.logging.Level;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.collection.Values;
import java.util.function.Function;
import org.glassfish.jersey.process.internal.Stages;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.model.internal.ManagedObjectsFinalizer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.process.internal.RequestScope;
import org.glassfish.jersey.client.spi.Connector;
import org.glassfish.jersey.process.internal.Stage;
import java.util.logging.Logger;

class ClientRuntime implements JerseyClient.ShutdownHook, ClientExecutor
{
    private static final Logger LOG;
    private final Stage<ClientRequest> requestProcessingRoot;
    private final Stage<ClientResponse> responseProcessingRoot;
    private final Connector connector;
    private final ClientConfig config;
    private final RequestScope requestScope;
    private final LazyValue<ExecutorService> asyncRequestExecutor;
    private final LazyValue<ScheduledExecutorService> backgroundScheduler;
    private final Iterable<ClientLifecycleListener> lifecycleListeners;
    private final AtomicBoolean closed;
    private final ManagedObjectsFinalizer managedObjectsFinalizer;
    private final InjectionManager injectionManager;
    
    public ClientRuntime(final ClientConfig config, final Connector connector, final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        this.closed = new AtomicBoolean(false);
        final Provider<Ref<ClientRequest>> clientRequest = (Provider<Ref<ClientRequest>>)(() -> (Ref)injectionManager.getInstance(new GenericType<Ref<ClientRequest>>() {}.getType()));
        final RequestProcessingInitializationStage requestProcessingInitializationStage = new RequestProcessingInitializationStage(clientRequest, bootstrapBag.getMessageBodyWorkers(), injectionManager);
        final Stage.Builder<ClientRequest> requestingChainBuilder = (Stage.Builder<ClientRequest>)Stages.chain((Function)requestProcessingInitializationStage);
        final ChainableStage<ClientRequest> requestFilteringStage = ClientFilteringStages.createRequestFilteringStage(injectionManager);
        this.requestProcessingRoot = (Stage<ClientRequest>)((requestFilteringStage != null) ? requestingChainBuilder.build((Stage)requestFilteringStage) : requestingChainBuilder.build());
        final ChainableStage<ClientResponse> responseFilteringStage = ClientFilteringStages.createResponseFilteringStage(injectionManager);
        this.responseProcessingRoot = (Stage<ClientResponse>)((responseFilteringStage != null) ? responseFilteringStage : Stages.identity());
        this.managedObjectsFinalizer = bootstrapBag.getManagedObjectsFinalizer();
        this.config = config;
        this.connector = connector;
        this.requestScope = bootstrapBag.getRequestScope();
        this.asyncRequestExecutor = (LazyValue<ExecutorService>)Values.lazy(() -> (config.getExecutorService() == null) ? injectionManager.getInstance((Class)ExecutorService.class, new Annotation[] { ClientAsyncExecutorLiteral.INSTANCE }) : config.getExecutorService());
        this.backgroundScheduler = (LazyValue<ScheduledExecutorService>)Values.lazy(() -> (config.getScheduledExecutorService() == null) ? injectionManager.getInstance((Class)ScheduledExecutorService.class, new Annotation[] { ClientBackgroundSchedulerLiteral.INSTANCE }) : config.getScheduledExecutorService());
        this.injectionManager = injectionManager;
        this.lifecycleListeners = Providers.getAllProviders(injectionManager, (Class)ClientLifecycleListener.class);
        for (final ClientLifecycleListener listener : this.lifecycleListeners) {
            try {
                listener.onInit();
            }
            catch (final Throwable t) {
                ClientRuntime.LOG.log(Level.WARNING, LocalizationMessages.ERROR_LISTENER_INIT(listener.getClass().getName()), t);
            }
        }
    }
    
    Runnable createRunnableForAsyncProcessing(final ClientRequest request, final ResponseCallback callback) {
        return () -> this.requestScope.runInScope(() -> {
            try {
                ClientRequest processedRequest2;
                try {
                    final ClientRequest processedRequest = (ClientRequest)Stages.process((Object)request, (Stage)this.requestProcessingRoot);
                    processedRequest2 = this.addUserAgent(processedRequest, this.connector.getName());
                }
                catch (final AbortException aborted) {
                    this.processResponse(aborted.getAbortResponse(), callback);
                    return;
                }
                final AsyncConnectorCallback connectorCallback = new AsyncConnectorCallback() {
                    final /* synthetic */ ResponseCallback val$callback;
                    
                    @Override
                    public void response(final ClientResponse response) {
                        ClientRuntime.this.requestScope.runInScope(() -> {
                            final Object val$callback = this.val$callback;
                            ClientRuntime.this.processResponse(response, callback);
                        });
                    }
                    
                    @Override
                    public void failure(final Throwable failure) {
                        ClientRuntime.this.requestScope.runInScope(() -> {
                            final Object val$callback = this.val$callback;
                            ClientRuntime.this.processFailure(failure, callback);
                        });
                    }
                };
                this.connector.apply(processedRequest2, connectorCallback);
            }
            catch (final Throwable throwable) {
                this.processFailure(throwable, callback);
            }
        });
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return ((ExecutorService)this.asyncRequestExecutor.get()).submit(task);
    }
    
    @Override
    public Future<?> submit(final Runnable task) {
        return ((ExecutorService)this.asyncRequestExecutor.get()).submit(task);
    }
    
    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        return ((ExecutorService)this.asyncRequestExecutor.get()).submit(task, result);
    }
    
    @Override
    public <T> ScheduledFuture<T> schedule(final Callable<T> callable, final long delay, final TimeUnit unit) {
        return ((ScheduledExecutorService)this.backgroundScheduler.get()).schedule(callable, delay, unit);
    }
    
    @Override
    public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
        return ((ScheduledExecutorService)this.backgroundScheduler.get()).schedule(command, delay, unit);
    }
    
    private void processResponse(final ClientResponse response, final ResponseCallback callback) {
        ClientResponse processedResponse;
        try {
            processedResponse = (ClientResponse)Stages.process((Object)response, (Stage)this.responseProcessingRoot);
        }
        catch (final Throwable throwable) {
            this.processFailure(throwable, callback);
            return;
        }
        callback.completed(processedResponse, this.requestScope);
    }
    
    private void processFailure(final Throwable failure, final ResponseCallback callback) {
        callback.failed((failure instanceof ProcessingException) ? ((ProcessingException)failure) : new ProcessingException(failure));
    }
    
    private Future<?> submit(final ExecutorService executor, final Runnable task) {
        return executor.submit(() -> this.requestScope.runInScope(task));
    }
    
    private ClientRequest addUserAgent(final ClientRequest clientRequest, final String connectorName) {
        final MultivaluedMap<String, Object> headers = (MultivaluedMap<String, Object>)clientRequest.getHeaders();
        if (headers.containsKey((Object)"User-Agent")) {
            if (clientRequest.getHeaderString("User-Agent") == null) {
                headers.remove((Object)"User-Agent");
            }
        }
        else if (!clientRequest.ignoreUserAgent()) {
            if (connectorName != null && !connectorName.isEmpty()) {
                headers.put((Object)"User-Agent", (Object)Collections.singletonList(String.format("Jersey/%s (%s)", Version.getVersion(), connectorName)));
            }
            else {
                headers.put((Object)"User-Agent", (Object)Collections.singletonList(String.format("Jersey/%s", Version.getVersion())));
            }
        }
        return clientRequest;
    }
    
    public ClientResponse invoke(final ClientRequest request) {
        try {
            ClientResponse response;
            try {
                response = this.connector.apply(this.addUserAgent((ClientRequest)Stages.process((Object)request, (Stage)this.requestProcessingRoot), this.connector.getName()));
            }
            catch (final AbortException aborted) {
                response = aborted.getAbortResponse();
            }
            return (ClientResponse)Stages.process((Object)response, (Stage)this.responseProcessingRoot);
        }
        catch (final ProcessingException pe) {
            throw pe;
        }
        catch (final Throwable t) {
            throw new ProcessingException(t.getMessage(), t);
        }
    }
    
    public RequestScope getRequestScope() {
        return this.requestScope;
    }
    
    public ClientConfig getConfig() {
        return this.config;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        }
        finally {
            super.finalize();
        }
    }
    
    @Override
    public void onShutdown() {
        this.close();
    }
    
    private void close() {
        if (this.closed.compareAndSet(false, true)) {
            try {
                for (final ClientLifecycleListener listener : this.lifecycleListeners) {
                    try {
                        listener.onClose();
                    }
                    catch (final Throwable t) {
                        ClientRuntime.LOG.log(Level.WARNING, LocalizationMessages.ERROR_LISTENER_CLOSE(listener.getClass().getName()), t);
                    }
                }
            }
            finally {
                try {
                    this.connector.close();
                }
                finally {
                    this.managedObjectsFinalizer.preDestroy();
                    this.injectionManager.shutdown();
                }
            }
        }
    }
    
    public void preInitialize() {
        this.injectionManager.getInstance((Class)MessageBodyWorkers.class);
    }
    
    public Connector getConnector() {
        return this.connector;
    }
    
    InjectionManager getInjectionManager() {
        return this.injectionManager;
    }
    
    static {
        LOG = Logger.getLogger(ClientRuntime.class.getName());
    }
}
