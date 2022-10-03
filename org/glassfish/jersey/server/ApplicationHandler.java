package org.glassfish.jersey.server;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import org.glassfish.jersey.server.internal.JerseyRequestTimeoutHandler;
import java.util.concurrent.CompletableFuture;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import java.security.Principal;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import java.io.OutputStream;
import org.glassfish.jersey.message.internal.NullOutputStream;
import java.util.concurrent.Future;
import java.util.Spliterator;
import java.lang.annotation.Annotation;
import javax.ws.rs.ext.MessageBodyWriter;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.ws.rs.ext.MessageBodyReader;
import org.glassfish.jersey.server.model.Resource;
import java.util.logging.Level;
import java.util.Iterator;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import org.glassfish.jersey.server.internal.monitoring.MonitoringContainerListener;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.process.internal.Stage;
import org.glassfish.jersey.process.internal.ChainableStage;
import java.util.function.Function;
import org.glassfish.jersey.process.internal.Stages;
import org.glassfish.jersey.server.internal.process.RequestProcessingContextReference;
import javax.inject.Provider;
import org.glassfish.jersey.server.internal.process.ReferencesInitializer;
import javax.ws.rs.container.ContainerResponseFilter;
import org.glassfish.jersey.server.model.ModelProcessor;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.server.internal.routing.Routing;
import javax.ws.rs.container.ContainerRequestFilter;
import org.glassfish.jersey.model.internal.RankedProvider;
import org.glassfish.jersey.server.model.ModelValidationException;
import org.glassfish.jersey.server.model.internal.ModelErrors;
import org.glassfish.jersey.server.model.ResourceModelComponent;
import org.glassfish.jersey.server.model.ComponentModelValidator;
import org.glassfish.jersey.server.model.ResourceModel;
import java.util.Set;
import org.glassfish.jersey.server.internal.monitoring.ApplicationEventImpl;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.internal.monitoring.CompositeApplicationEventListener;
import org.glassfish.jersey.model.internal.RankedComparator;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import java.util.Collection;
import java.util.Map;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.message.internal.MessagingBinders;
import java.util.List;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.internal.BootstrapBag;
import java.util.Arrays;
import org.glassfish.jersey.internal.AutoDiscoverableConfigurator;
import javax.ws.rs.RuntimeType;
import org.glassfish.jersey.server.model.internal.ResourceMethodInvokerConfigurator;
import org.glassfish.jersey.internal.ExceptionMapperFactory;
import org.glassfish.jersey.message.internal.MessageBodyFactory;
import org.glassfish.jersey.internal.ContextResolverFactory;
import org.glassfish.jersey.internal.JaxrsProviders;
import org.glassfish.jersey.server.internal.inject.ValueParamProviderConfigurator;
import org.glassfish.jersey.server.internal.inject.ParamExtractorConfigurator;
import org.glassfish.jersey.server.internal.inject.ParamConverterConfigurator;
import org.glassfish.jersey.process.internal.RequestScope;
import org.glassfish.jersey.server.internal.process.RequestProcessingConfigurator;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.inject.CompositeBinder;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.internal.Version;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.model.internal.ManagedObjectsFinalizer;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.SecurityContext;
import java.util.logging.Logger;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

public final class ApplicationHandler implements ContainerLifecycleListener
{
    private static final Logger LOGGER;
    private static final SecurityContext DEFAULT_SECURITY_CONTEXT;
    private Application application;
    private ResourceConfig runtimeConfig;
    private ServerRuntime runtime;
    private Iterable<ContainerLifecycleListener> containerLifecycleListeners;
    private InjectionManager injectionManager;
    private MessageBodyWorkers msgBodyWorkers;
    private ManagedObjectsFinalizer managedObjectsFinalizer;
    
    public ApplicationHandler() {
        this(new Application());
    }
    
    public ApplicationHandler(final Class<? extends Application> jaxrsApplicationClass) {
        this.initialize(new ApplicationConfigurator(jaxrsApplicationClass), Injections.createInjectionManager(), null);
    }
    
    public ApplicationHandler(final Application application) {
        this(application, null, null);
    }
    
    public ApplicationHandler(final Application application, final Binder customBinder) {
        this(application, customBinder, null);
    }
    
    public ApplicationHandler(final Application application, final Binder customBinder, final Object parentManager) {
        this.initialize(new ApplicationConfigurator(application), Injections.createInjectionManager(parentManager), customBinder);
    }
    
    private void initialize(final ApplicationConfigurator applicationConfigurator, final InjectionManager injectionManager, final Binder customBinder) {
        ApplicationHandler.LOGGER.config(LocalizationMessages.INIT_MSG(Version.getBuildId()));
        (this.injectionManager = injectionManager).register((Binder)CompositeBinder.wrap(new Binder[] { (Binder)new ServerBinder(), customBinder }));
        this.managedObjectsFinalizer = new ManagedObjectsFinalizer(injectionManager);
        final ServerBootstrapBag bootstrapBag = new ServerBootstrapBag();
        bootstrapBag.setManagedObjectsFinalizer(this.managedObjectsFinalizer);
        final List<BootstrapConfigurator> bootstrapConfigurators = Arrays.asList((BootstrapConfigurator)new RequestProcessingConfigurator(), (BootstrapConfigurator)new RequestScope.RequestScopeConfigurator(), (BootstrapConfigurator)new ParamConverterConfigurator(), (BootstrapConfigurator)new ParamExtractorConfigurator(), (BootstrapConfigurator)new ValueParamProviderConfigurator(), (BootstrapConfigurator)new JerseyResourceContextConfigurator(), (BootstrapConfigurator)new ComponentProviderConfigurator(), (BootstrapConfigurator)new JaxrsProviders.ProvidersConfigurator(), (BootstrapConfigurator)applicationConfigurator, (BootstrapConfigurator)new RuntimeConfigConfigurator(), (BootstrapConfigurator)new ContextResolverFactory.ContextResolversConfigurator(), (BootstrapConfigurator)new MessageBodyFactory.MessageBodyWorkersConfigurator(), (BootstrapConfigurator)new ExceptionMapperFactory.ExceptionMappersConfigurator(), (BootstrapConfigurator)new ResourceMethodInvokerConfigurator(), (BootstrapConfigurator)new ProcessingProvidersConfigurator(), (BootstrapConfigurator)new ContainerProviderConfigurator(RuntimeType.SERVER), (BootstrapConfigurator)new AutoDiscoverableConfigurator(RuntimeType.SERVER));
        bootstrapConfigurators.forEach(configurator -> configurator.init(injectionManager, (BootstrapBag)bootstrapBag));
        this.runtime = (ServerRuntime)Errors.processWithException(() -> this.initialize(injectionManager, bootstrapConfigurators, bootstrapBag));
        this.containerLifecycleListeners = Providers.getAllProviders(injectionManager, (Class)ContainerLifecycleListener.class);
    }
    
    private ServerRuntime initialize(final InjectionManager injectionManager, final List<BootstrapConfigurator> bootstrapConfigurators, final ServerBootstrapBag bootstrapBag) {
        this.application = bootstrapBag.getApplication();
        this.runtimeConfig = bootstrapBag.getRuntimeConfig();
        injectionManager.register((Binder)new MessagingBinders.MessageBodyProviders(this.application.getProperties(), RuntimeType.SERVER));
        if (this.application instanceof ResourceConfig) {
            ((ResourceConfig)this.application).lock();
        }
        CompositeApplicationEventListener compositeListener = null;
        Errors.mark();
        try {
            if (!(boolean)CommonProperties.getValue((Map)this.runtimeConfig.getProperties(), RuntimeType.SERVER, "jersey.config.disableAutoDiscovery", (Object)Boolean.FALSE, (Class)Boolean.class)) {
                this.runtimeConfig.configureAutoDiscoverableProviders(injectionManager, bootstrapBag.getAutoDiscoverables());
            }
            else {
                this.runtimeConfig.configureForcedAutoDiscoverableProviders(injectionManager);
            }
            this.runtimeConfig.configureMetaProviders(injectionManager, bootstrapBag.getManagedObjectsFinalizer());
            final ResourceBagConfigurator resourceBagConfigurator = new ResourceBagConfigurator();
            resourceBagConfigurator.init(injectionManager, bootstrapBag);
            this.runtimeConfig.lock();
            final ExternalRequestScopeConfigurator externalRequestScopeConfigurator = new ExternalRequestScopeConfigurator();
            externalRequestScopeConfigurator.init(injectionManager, bootstrapBag);
            final ModelProcessorConfigurator modelProcessorConfigurator = new ModelProcessorConfigurator();
            modelProcessorConfigurator.init(injectionManager, bootstrapBag);
            final ResourceModelConfigurator resourceModelConfigurator = new ResourceModelConfigurator();
            resourceModelConfigurator.init(injectionManager, bootstrapBag);
            final ServerExecutorProvidersConfigurator executorProvidersConfigurator = new ServerExecutorProvidersConfigurator();
            executorProvidersConfigurator.init(injectionManager, bootstrapBag);
            injectionManager.completeRegistration();
            bootstrapConfigurators.forEach(configurator -> configurator.postInit(injectionManager, (BootstrapBag)bootstrapBag));
            resourceModelConfigurator.postInit(injectionManager, (BootstrapBag)bootstrapBag);
            final Iterable<ApplicationEventListener> appEventListeners = Providers.getAllProviders(injectionManager, (Class)ApplicationEventListener.class, new RankedComparator());
            if (appEventListeners.iterator().hasNext()) {
                final ResourceBag resourceBag = bootstrapBag.getResourceBag();
                compositeListener = new CompositeApplicationEventListener(appEventListeners);
                compositeListener.onEvent(new ApplicationEventImpl(ApplicationEvent.Type.INITIALIZATION_START, this.runtimeConfig, this.runtimeConfig.getComponentBag().getRegistrations(), resourceBag.classes, resourceBag.instances, null));
            }
            if (!this.disableValidation()) {
                final ComponentModelValidator validator = new ComponentModelValidator(bootstrapBag.getValueParamProviders(), bootstrapBag.getMessageBodyWorkers());
                validator.validate(bootstrapBag.getResourceModel());
            }
            if (Errors.fatalIssuesFound() && !this.ignoreValidationError()) {
                throw new ModelValidationException(LocalizationMessages.RESOURCE_MODEL_VALIDATION_FAILED_AT_INIT(), ModelErrors.getErrorsAsResourceModelIssues(true));
            }
        }
        finally {
            if (this.ignoreValidationError()) {
                Errors.logErrors(true);
                Errors.reset();
            }
            else {
                Errors.unmark();
            }
        }
        this.msgBodyWorkers = bootstrapBag.getMessageBodyWorkers();
        final ProcessingProviders processingProviders = bootstrapBag.getProcessingProviders();
        final ContainerFilteringStage preMatchRequestFilteringStage = new ContainerFilteringStage(processingProviders.getPreMatchFilters(), processingProviders.getGlobalResponseFilters());
        final ChainableStage<RequestProcessingContext> routingStage = Routing.forModel(bootstrapBag.getResourceModel().getRuntimeResourceModel()).resourceContext(bootstrapBag.getResourceContext()).configuration((Configuration)this.runtimeConfig).entityProviders(this.msgBodyWorkers).valueSupplierProviders(bootstrapBag.getValueParamProviders()).modelProcessors(Providers.getAllRankedSortedProviders(injectionManager, (Class)ModelProcessor.class)).createService(serviceType -> Injections.getOrCreate(injectionManager, serviceType)).processingProviders(processingProviders).resourceMethodInvokerBuilder(bootstrapBag.getResourceMethodInvokerBuilder()).buildStage();
        final ContainerFilteringStage resourceFilteringStage = new ContainerFilteringStage(processingProviders.getGlobalRequestFilters(), null);
        final ReferencesInitializer referencesInitializer = new ReferencesInitializer(injectionManager, (Provider<RequestProcessingContextReference>)(() -> (RequestProcessingContextReference)injectionManager.getInstance((Class)RequestProcessingContextReference.class)));
        final Stage<RequestProcessingContext> rootStage = (Stage<RequestProcessingContext>)Stages.chain((Function)referencesInitializer).to((ChainableStage)preMatchRequestFilteringStage).to((ChainableStage)routingStage).to((ChainableStage)resourceFilteringStage).build((Stage)Routing.matchedEndpointExtractor());
        final ServerRuntime serverRuntime = ServerRuntime.createServerRuntime(injectionManager, bootstrapBag, rootStage, compositeListener, processingProviders);
        final ComponentBag componentBag = this.runtimeConfig.getComponentBag();
        final ResourceBag resourceBag2 = bootstrapBag.getResourceBag();
        for (final Object instance : componentBag.getInstances(ComponentBag.excludeMetaProviders(injectionManager))) {
            injectionManager.inject(instance);
        }
        for (final Object instance : resourceBag2.instances) {
            injectionManager.inject(instance);
        }
        logApplicationInitConfiguration(injectionManager, resourceBag2, processingProviders);
        if (compositeListener != null) {
            final ApplicationEvent initFinishedEvent = new ApplicationEventImpl(ApplicationEvent.Type.INITIALIZATION_APP_FINISHED, this.runtimeConfig, componentBag.getRegistrations(), resourceBag2.classes, resourceBag2.instances, bootstrapBag.getResourceModel());
            compositeListener.onEvent(initFinishedEvent);
            final MonitoringContainerListener containerListener = (MonitoringContainerListener)injectionManager.getInstance((Class)MonitoringContainerListener.class);
            containerListener.init(compositeListener, initFinishedEvent);
        }
        return serverRuntime;
    }
    
    private boolean ignoreValidationError() {
        return ServerProperties.getValue(this.runtimeConfig.getProperties(), "jersey.config.server.resource.validation.ignoreErrors", Boolean.FALSE, Boolean.class);
    }
    
    private boolean disableValidation() {
        return ServerProperties.getValue(this.runtimeConfig.getProperties(), "jersey.config.server.resource.validation.disable", Boolean.FALSE, Boolean.class);
    }
    
    private static void logApplicationInitConfiguration(final InjectionManager injectionManager, final ResourceBag resourceBag, final ProcessingProviders processingProviders) {
        if (!ApplicationHandler.LOGGER.isLoggable(Level.CONFIG)) {
            return;
        }
        final StringBuilder sb = new StringBuilder(LocalizationMessages.LOGGING_APPLICATION_INITIALIZED()).append('\n');
        final List<Resource> rootResourceClasses = resourceBag.getRootResources();
        if (!rootResourceClasses.isEmpty()) {
            sb.append(LocalizationMessages.LOGGING_ROOT_RESOURCE_CLASSES()).append(":");
            for (final Resource r : rootResourceClasses) {
                for (final Class clazz : r.getHandlerClasses()) {
                    sb.append('\n').append("  ").append(clazz.getName());
                }
            }
        }
        sb.append('\n');
        Set<MessageBodyReader> messageBodyReaders;
        Set<MessageBodyWriter> messageBodyWriters;
        if (ApplicationHandler.LOGGER.isLoggable(Level.FINE)) {
            final Spliterator<MessageBodyReader> mbrSpliterator = Providers.getAllProviders(injectionManager, (Class)MessageBodyReader.class).spliterator();
            messageBodyReaders = StreamSupport.stream(mbrSpliterator, false).collect((Collector<? super MessageBodyReader, ?, Set<MessageBodyReader>>)Collectors.toSet());
            final Spliterator<MessageBodyWriter> mbwSpliterator = Providers.getAllProviders(injectionManager, (Class)MessageBodyWriter.class).spliterator();
            messageBodyWriters = StreamSupport.stream(mbwSpliterator, false).collect((Collector<? super MessageBodyWriter, ?, Set<MessageBodyWriter>>)Collectors.toSet());
        }
        else {
            messageBodyReaders = Providers.getCustomProviders(injectionManager, (Class)MessageBodyReader.class);
            messageBodyWriters = Providers.getCustomProviders(injectionManager, (Class)MessageBodyWriter.class);
        }
        printProviders(LocalizationMessages.LOGGING_PRE_MATCH_FILTERS(), processingProviders.getPreMatchFilters(), sb);
        printProviders(LocalizationMessages.LOGGING_GLOBAL_REQUEST_FILTERS(), processingProviders.getGlobalRequestFilters(), sb);
        printProviders(LocalizationMessages.LOGGING_GLOBAL_RESPONSE_FILTERS(), processingProviders.getGlobalResponseFilters(), sb);
        printProviders(LocalizationMessages.LOGGING_GLOBAL_READER_INTERCEPTORS(), processingProviders.getGlobalReaderInterceptors(), sb);
        printProviders(LocalizationMessages.LOGGING_GLOBAL_WRITER_INTERCEPTORS(), processingProviders.getGlobalWriterInterceptors(), sb);
        printNameBoundProviders(LocalizationMessages.LOGGING_NAME_BOUND_REQUEST_FILTERS(), (Map<Class<? extends Annotation>, List<org.glassfish.jersey.model.internal.RankedProvider<Object>>>)processingProviders.getNameBoundRequestFilters(), sb);
        printNameBoundProviders(LocalizationMessages.LOGGING_NAME_BOUND_RESPONSE_FILTERS(), (Map<Class<? extends Annotation>, List<org.glassfish.jersey.model.internal.RankedProvider<Object>>>)processingProviders.getNameBoundResponseFilters(), sb);
        printNameBoundProviders(LocalizationMessages.LOGGING_NAME_BOUND_READER_INTERCEPTORS(), (Map<Class<? extends Annotation>, List<org.glassfish.jersey.model.internal.RankedProvider<Object>>>)processingProviders.getNameBoundReaderInterceptors(), sb);
        printNameBoundProviders(LocalizationMessages.LOGGING_NAME_BOUND_WRITER_INTERCEPTORS(), (Map<Class<? extends Annotation>, List<org.glassfish.jersey.model.internal.RankedProvider<Object>>>)processingProviders.getNameBoundWriterInterceptors(), sb);
        printProviders(LocalizationMessages.LOGGING_DYNAMIC_FEATURES(), processingProviders.getDynamicFeatures(), sb);
        printProviders(LocalizationMessages.LOGGING_MESSAGE_BODY_READERS(), (Iterable<Object>)messageBodyReaders.stream().map((Function<? super Object, ?>)new WorkersToStringTransform<Object>()).collect((Collector<? super Object, ?, Iterable<T>>)Collectors.toList()), sb);
        printProviders(LocalizationMessages.LOGGING_MESSAGE_BODY_WRITERS(), (Iterable<Object>)messageBodyWriters.stream().map((Function<? super Object, ?>)new WorkersToStringTransform<Object>()).collect((Collector<? super Object, ?, Iterable<T>>)Collectors.toList()), sb);
        ApplicationHandler.LOGGER.log(Level.CONFIG, sb.toString());
    }
    
    private static <T> void printNameBoundProviders(final String title, final Map<Class<? extends Annotation>, List<RankedProvider<T>>> providers, final StringBuilder sb) {
        if (!providers.isEmpty()) {
            sb.append(title).append(":").append('\n');
            for (final Map.Entry<Class<? extends Annotation>, List<RankedProvider<T>>> entry : providers.entrySet()) {
                for (final RankedProvider rankedProvider : entry.getValue()) {
                    sb.append("   ").append(LocalizationMessages.LOGGING_PROVIDER_BOUND(rankedProvider, entry.getKey())).append('\n');
                }
            }
        }
    }
    
    private static <T> void printProviders(final String title, final Iterable<T> providers, final StringBuilder sb) {
        final Iterator<T> iterator = providers.iterator();
        boolean first = true;
        while (iterator.hasNext()) {
            if (first) {
                sb.append(title).append(":").append('\n');
                first = false;
            }
            final T provider = iterator.next();
            sb.append("   ").append(provider).append('\n');
        }
    }
    
    public Future<ContainerResponse> apply(final ContainerRequest requestContext) {
        return this.apply(requestContext, (OutputStream)new NullOutputStream());
    }
    
    public Future<ContainerResponse> apply(final ContainerRequest request, final OutputStream outputStream) {
        final FutureResponseWriter responseFuture = new FutureResponseWriter(request.getMethod(), outputStream, this.runtime.getBackgroundScheduler());
        if (request.getSecurityContext() == null) {
            request.setSecurityContext(ApplicationHandler.DEFAULT_SECURITY_CONTEXT);
        }
        request.setWriter(responseFuture);
        this.handle(request);
        return responseFuture;
    }
    
    public void handle(final ContainerRequest request) {
        request.setWorkers(this.msgBodyWorkers);
        this.runtime.process(request);
    }
    
    public InjectionManager getInjectionManager() {
        return this.injectionManager;
    }
    
    public ResourceConfig getConfiguration() {
        return this.runtimeConfig;
    }
    
    @Override
    public void onStartup(final Container container) {
        for (final ContainerLifecycleListener listener : this.containerLifecycleListeners) {
            listener.onStartup(container);
        }
    }
    
    @Override
    public void onReload(final Container container) {
        for (final ContainerLifecycleListener listener : this.containerLifecycleListeners) {
            listener.onReload(container);
        }
    }
    
    @Override
    public void onShutdown(final Container container) {
        try {
            for (final ContainerLifecycleListener listener : this.containerLifecycleListeners) {
                listener.onShutdown(container);
            }
        }
        finally {
            try {
                this.injectionManager.preDestroy((Object)ResourceConfig.unwrapApplication(this.application));
            }
            finally {
                this.managedObjectsFinalizer.preDestroy();
                this.injectionManager.shutdown();
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ApplicationHandler.class.getName());
        DEFAULT_SECURITY_CONTEXT = (SecurityContext)new SecurityContext() {
            public boolean isUserInRole(final String role) {
                return false;
            }
            
            public boolean isSecure() {
                return false;
            }
            
            public Principal getUserPrincipal() {
                return null;
            }
            
            public String getAuthenticationScheme() {
                return null;
            }
        };
    }
    
    private class RuntimeConfigConfigurator implements BootstrapConfigurator
    {
        public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
            final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
            serverBag.setApplicationHandler(ApplicationHandler.this);
            serverBag.setConfiguration((Configuration)ResourceConfig.createRuntimeConfig(serverBag.getApplication()));
            final InstanceBinding<ApplicationHandler> handlerBinding = (InstanceBinding<ApplicationHandler>)Bindings.service((Object)ApplicationHandler.this).to((Class)ApplicationHandler.class);
            final InstanceBinding<ResourceConfig> configBinding = (InstanceBinding<ResourceConfig>)((InstanceBinding)Bindings.service((Object)serverBag.getRuntimeConfig()).to((Class)Configuration.class)).to((Class)ServerConfig.class);
            injectionManager.register((Binding)handlerBinding);
            injectionManager.register((Binding)configBinding);
        }
    }
    
    private static class WorkersToStringTransform<T> implements Function<T, String>
    {
        @Override
        public String apply(final T t) {
            if (t != null) {
                return t.getClass().getName();
            }
            return null;
        }
    }
    
    private static class FutureResponseWriter extends CompletableFuture<ContainerResponse> implements ContainerResponseWriter
    {
        private ContainerResponse response;
        private final String requestMethodName;
        private final OutputStream outputStream;
        private final JerseyRequestTimeoutHandler requestTimeoutHandler;
        
        private FutureResponseWriter(final String requestMethodName, final OutputStream outputStream, final ScheduledExecutorService backgroundScheduler) {
            this.response = null;
            this.requestMethodName = requestMethodName;
            this.outputStream = outputStream;
            this.requestTimeoutHandler = new JerseyRequestTimeoutHandler(this, backgroundScheduler);
        }
        
        @Override
        public OutputStream writeResponseStatusAndHeaders(final long contentLength, final ContainerResponse response) {
            this.response = response;
            if (contentLength >= 0L) {
                response.getHeaders().putSingle((Object)"Content-Length", (Object)Long.toString(contentLength));
            }
            return this.outputStream;
        }
        
        @Override
        public boolean suspend(final long time, final TimeUnit unit, final TimeoutHandler handler) {
            return this.requestTimeoutHandler.suspend(time, unit, handler);
        }
        
        @Override
        public void setSuspendTimeout(final long time, final TimeUnit unit) {
            this.requestTimeoutHandler.setSuspendTimeout(time, unit);
        }
        
        @Override
        public void commit() {
            final ContainerResponse current = this.response;
            if (current != null) {
                if ("HEAD".equals(this.requestMethodName) && current.hasEntity()) {
                    current.setEntity(null);
                }
                this.requestTimeoutHandler.close();
                super.complete(current);
            }
        }
        
        @Override
        public void failure(final Throwable error) {
            this.requestTimeoutHandler.close();
            super.completeExceptionally(error);
        }
        
        @Override
        public boolean enableResponseBuffering() {
            return true;
        }
    }
}
