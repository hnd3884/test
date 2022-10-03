package org.glassfish.jersey.server.model;

import java.util.function.Supplier;
import org.glassfish.jersey.server.model.internal.ResourceMethodInvocationHandlerFactory;
import org.glassfish.jersey.server.model.internal.ResourceMethodDispatcherFactory;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import java.util.concurrent.CancellationException;
import java.util.function.BiConsumer;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.concurrent.CompletionStage;
import org.glassfish.jersey.internal.util.Producer;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.model.ContractProvider;
import java.util.Set;
import java.util.Iterator;
import javax.ws.rs.core.Response;
import java.util.Collections;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.model.internal.RankedComparator;
import org.glassfish.jersey.model.NameBound;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.LinkedList;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.Injections;
import java.util.Collection;
import org.glassfish.jersey.model.internal.ComponentBag;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.container.DynamicFeature;
import java.util.Map;
import java.util.ArrayList;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import org.glassfish.jersey.server.spi.internal.ResourceMethodInvocationHandlerProvider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerRequestFilter;
import org.glassfish.jersey.model.internal.RankedProvider;
import java.util.List;
import java.lang.reflect.Method;
import org.glassfish.jersey.server.spi.internal.ResourceMethodDispatcher;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import javax.ws.rs.container.ResourceInfo;
import org.glassfish.jersey.server.internal.process.Endpoint;

public class ResourceMethodInvoker implements Endpoint, ResourceInfo
{
    private final ResourceMethod method;
    private final Annotation[] methodAnnotations;
    private final Type invocableResponseType;
    private final boolean canUseInvocableResponseType;
    private final ResourceMethodDispatcher dispatcher;
    private final Method resourceMethod;
    private final Class<?> resourceClass;
    private final List<RankedProvider<ContainerRequestFilter>> requestFilters;
    private final List<RankedProvider<ContainerResponseFilter>> responseFilters;
    private final Iterable<ReaderInterceptor> readerInterceptors;
    private final Iterable<WriterInterceptor> writerInterceptors;
    
    private ResourceMethodInvoker(final ResourceMethodDispatcher.Provider dispatcherProvider, final ResourceMethodInvocationHandlerProvider invocationHandlerProvider, final ResourceMethod method, final ProcessingProviders processingProviders, InjectionManager injectionManager, final Configuration globalConfig, final ConfiguredValidator validator) {
        this.requestFilters = new ArrayList<RankedProvider<ContainerRequestFilter>>();
        this.responseFilters = new ArrayList<RankedProvider<ContainerResponseFilter>>();
        this.method = method;
        final Invocable invocable = method.getInvocable();
        this.dispatcher = dispatcherProvider.create(invocable, invocationHandlerProvider.create(invocable), validator);
        this.resourceMethod = invocable.getHandlingMethod();
        this.resourceClass = invocable.getHandler().getHandlerClass();
        final ResourceMethodConfig config = new ResourceMethodConfig(globalConfig.getProperties());
        for (final DynamicFeature dynamicFeature : processingProviders.getDynamicFeatures()) {
            dynamicFeature.configure((ResourceInfo)this, (FeatureContext)config);
        }
        final ComponentBag componentBag = config.getComponentBag();
        final List<Object> providers = new ArrayList<Object>(componentBag.getInstances(ComponentBag.excludeMetaProviders(injectionManager)));
        final Set<Class<?>> providerClasses = componentBag.getClasses(ComponentBag.excludeMetaProviders(injectionManager));
        if (!providerClasses.isEmpty()) {
            injectionManager = Injections.createInjectionManager((Object)injectionManager);
            injectionManager.register((Binder)new AbstractBinder() {
                protected void configure() {
                    this.bind((Object)config).to((Class)Configuration.class);
                }
            });
            for (final Class<?> providerClass : providerClasses) {
                providers.add(injectionManager.createAndInitialize((Class)providerClass));
            }
        }
        final List<RankedProvider<ReaderInterceptor>> _readerInterceptors = new LinkedList<RankedProvider<ReaderInterceptor>>();
        final List<RankedProvider<WriterInterceptor>> _writerInterceptors = new LinkedList<RankedProvider<WriterInterceptor>>();
        final List<RankedProvider<ContainerRequestFilter>> _requestFilters = new LinkedList<RankedProvider<ContainerRequestFilter>>();
        final List<RankedProvider<ContainerResponseFilter>> _responseFilters = new LinkedList<RankedProvider<ContainerResponseFilter>>();
        for (final Object provider : providers) {
            final ContractProvider model = componentBag.getModel((Class)provider.getClass());
            final Set<Class<?>> contracts = model.getContracts();
            if (contracts.contains(WriterInterceptor.class)) {
                _writerInterceptors.add((RankedProvider<WriterInterceptor>)new RankedProvider((Object)provider, model.getPriority((Class)WriterInterceptor.class)));
            }
            if (contracts.contains(ReaderInterceptor.class)) {
                _readerInterceptors.add((RankedProvider<ReaderInterceptor>)new RankedProvider((Object)provider, model.getPriority((Class)ReaderInterceptor.class)));
            }
            if (contracts.contains(ContainerRequestFilter.class)) {
                _requestFilters.add((RankedProvider<ContainerRequestFilter>)new RankedProvider((Object)provider, model.getPriority((Class)ContainerRequestFilter.class)));
            }
            if (contracts.contains(ContainerResponseFilter.class)) {
                _responseFilters.add((RankedProvider<ContainerResponseFilter>)new RankedProvider((Object)provider, model.getPriority((Class)ContainerResponseFilter.class)));
            }
        }
        _readerInterceptors.addAll(StreamSupport.stream(processingProviders.getGlobalReaderInterceptors().spliterator(), false).collect((Collector<? super RankedProvider<ReaderInterceptor>, ?, Collection<? extends RankedProvider<ReaderInterceptor>>>)Collectors.toList()));
        _writerInterceptors.addAll(StreamSupport.stream(processingProviders.getGlobalWriterInterceptors().spliterator(), false).collect((Collector<? super RankedProvider<WriterInterceptor>, ?, Collection<? extends RankedProvider<WriterInterceptor>>>)Collectors.toList()));
        if (this.resourceMethod != null) {
            this.addNameBoundFiltersAndInterceptors(processingProviders, _requestFilters, _responseFilters, _readerInterceptors, _writerInterceptors, (NameBound)method);
        }
        this.readerInterceptors = (Iterable<ReaderInterceptor>)Collections.unmodifiableList((List<?>)StreamSupport.stream(Providers.sortRankedProviders(new RankedComparator(), (Iterable)_readerInterceptors).spliterator(), false).collect((Collector<? super Object, ?, List<? extends T>>)Collectors.toList()));
        this.writerInterceptors = (Iterable<WriterInterceptor>)Collections.unmodifiableList((List<?>)StreamSupport.stream(Providers.sortRankedProviders(new RankedComparator(), (Iterable)_writerInterceptors).spliterator(), false).collect((Collector<? super Object, ?, List<? extends T>>)Collectors.toList()));
        this.requestFilters.addAll(_requestFilters);
        this.responseFilters.addAll(_responseFilters);
        this.methodAnnotations = invocable.getHandlingMethod().getDeclaredAnnotations();
        this.invocableResponseType = invocable.getResponseType();
        this.canUseInvocableResponseType = (this.invocableResponseType != null && Void.TYPE != this.invocableResponseType && Void.class != this.invocableResponseType && (!(this.invocableResponseType instanceof Class) || !Response.class.isAssignableFrom((Class<?>)this.invocableResponseType)));
    }
    
    private <T> void addNameBoundProviders(final Collection<RankedProvider<T>> targetCollection, final NameBound nameBound, final MultivaluedMap<Class<? extends Annotation>, RankedProvider<T>> nameBoundProviders, final MultivaluedMap<RankedProvider<T>, Class<? extends Annotation>> nameBoundProvidersInverse) {
        final MultivaluedMap<RankedProvider<T>, Class<? extends Annotation>> foundBindingsMap = (MultivaluedMap<RankedProvider<T>, Class<? extends Annotation>>)new MultivaluedHashMap();
        for (final Class<? extends Annotation> nameBinding : nameBound.getNameBindings()) {
            final Iterable<RankedProvider<T>> providers = (Iterable<RankedProvider<T>>)nameBoundProviders.get((Object)nameBinding);
            if (providers != null) {
                for (final RankedProvider<T> provider : providers) {
                    foundBindingsMap.add((Object)provider, (Object)nameBinding);
                }
            }
        }
        for (final Map.Entry<RankedProvider<T>, List<Class<? extends Annotation>>> entry : foundBindingsMap.entrySet()) {
            final RankedProvider<T> provider2 = entry.getKey();
            final List<Class<? extends Annotation>> foundBindings = entry.getValue();
            final List<Class<? extends Annotation>> providerBindings = (List<Class<? extends Annotation>>)nameBoundProvidersInverse.get((Object)provider2);
            if (foundBindings.size() == providerBindings.size()) {
                targetCollection.add(provider2);
            }
        }
    }
    
    private void addNameBoundFiltersAndInterceptors(final ProcessingProviders processingProviders, final Collection<RankedProvider<ContainerRequestFilter>> targetRequestFilters, final Collection<RankedProvider<ContainerResponseFilter>> targetResponseFilters, final Collection<RankedProvider<ReaderInterceptor>> targetReaderInterceptors, final Collection<RankedProvider<WriterInterceptor>> targetWriterInterceptors, final NameBound nameBound) {
        this.addNameBoundProviders(targetRequestFilters, nameBound, processingProviders.getNameBoundRequestFilters(), processingProviders.getNameBoundRequestFiltersInverse());
        this.addNameBoundProviders(targetResponseFilters, nameBound, processingProviders.getNameBoundResponseFilters(), processingProviders.getNameBoundResponseFiltersInverse());
        this.addNameBoundProviders(targetReaderInterceptors, nameBound, processingProviders.getNameBoundReaderInterceptors(), processingProviders.getNameBoundReaderInterceptorsInverse());
        this.addNameBoundProviders(targetWriterInterceptors, nameBound, processingProviders.getNameBoundWriterInterceptors(), processingProviders.getNameBoundWriterInterceptorsInverse());
    }
    
    public Method getResourceMethod() {
        return this.resourceMethod;
    }
    
    public Class<?> getResourceClass() {
        return this.resourceClass;
    }
    
    public ContainerResponse apply(final RequestProcessingContext processingContext) {
        final ContainerRequest request = processingContext.request();
        final Object resource = processingContext.routingContext().peekMatchedResource();
        if ((this.method.isSuspendDeclared() || this.method.isManagedAsyncDeclared() || this.method.isSse()) && !processingContext.asyncContext().suspend()) {
            throw new ProcessingException(LocalizationMessages.ERROR_SUSPENDING_ASYNC_REQUEST());
        }
        if (this.method.isManagedAsyncDeclared()) {
            processingContext.asyncContext().invokeManaged((Producer<Response>)(() -> {
                final Response response = this.invoke(processingContext, resource);
                if (this.method.isSuspendDeclared()) {
                    return null;
                }
                return response;
            }));
            return null;
        }
        final Response response = this.invoke(processingContext, resource);
        if (this.method.isSse()) {
            return null;
        }
        if (response.hasEntity()) {
            final Object entityFuture = response.getEntity();
            if (entityFuture instanceof CompletionStage) {
                final CompletionStage completionStage = (CompletionStage)entityFuture;
                if (!processingContext.asyncContext().suspend()) {
                    throw new ProcessingException(LocalizationMessages.ERROR_SUSPENDING_ASYNC_REQUEST());
                }
                completionStage.whenComplete(this.whenComplete(processingContext));
                return null;
            }
        }
        return new ContainerResponse(request, response);
    }
    
    private BiConsumer whenComplete(final RequestProcessingContext processingContext) {
        return (entity, exception) -> {
            if (exception != null) {
                if (exception instanceof CancellationException) {
                    processingContext.asyncContext().resume((Object)Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
                }
                else {
                    processingContext.asyncContext().resume((Throwable)exception);
                }
            }
            else {
                processingContext.asyncContext().resume(entity);
            }
        };
    }
    
    private Response invoke(final RequestProcessingContext context, final Object resource) {
        context.triggerEvent(RequestEvent.Type.RESOURCE_METHOD_START);
        context.push(response -> {
            if (response == null || response.isMappedFromException()) {
                return response;
            }
            else {
                final Annotation[] entityAnn = response.getEntityAnnotations();
                if (this.methodAnnotations.length > 0) {
                    if (entityAnn.length == 0) {
                        response.setEntityAnnotations(this.methodAnnotations);
                    }
                    else {
                        final Annotation[] mergedAnn = Arrays.copyOf(this.methodAnnotations, this.methodAnnotations.length + entityAnn.length);
                        System.arraycopy(entityAnn, 0, mergedAnn, this.methodAnnotations.length, entityAnn.length);
                        response.setEntityAnnotations(mergedAnn);
                    }
                }
                if (this.canUseInvocableResponseType && response.hasEntity() && !(response.getEntityType() instanceof ParameterizedType)) {
                    response.setEntityType(this.invocableResponseType);
                }
                return response;
            }
        });
        Response jaxrsResponse;
        try {
            jaxrsResponse = this.dispatcher.dispatch(resource, context.request());
        }
        finally {
            context.triggerEvent(RequestEvent.Type.RESOURCE_METHOD_FINISHED);
        }
        if (jaxrsResponse == null) {
            jaxrsResponse = Response.noContent().build();
        }
        return jaxrsResponse;
    }
    
    public Iterable<RankedProvider<ContainerRequestFilter>> getRequestFilters() {
        return this.requestFilters;
    }
    
    public Iterable<RankedProvider<ContainerResponseFilter>> getResponseFilters() {
        return this.responseFilters;
    }
    
    public Iterable<WriterInterceptor> getWriterInterceptors() {
        return this.writerInterceptors;
    }
    
    public Iterable<ReaderInterceptor> getReaderInterceptors() {
        return this.readerInterceptors;
    }
    
    @Override
    public String toString() {
        return this.method.getInvocable().getHandlingMethod().toString();
    }
    
    public static class Builder
    {
        private ResourceMethodDispatcherFactory resourceMethodDispatcherFactory;
        private ResourceMethodInvocationHandlerFactory resourceMethodInvocationHandlerFactory;
        private InjectionManager injectionManager;
        private Configuration configuration;
        private Supplier<ConfiguredValidator> configurationValidator;
        
        public Builder resourceMethodDispatcherFactory(final ResourceMethodDispatcherFactory resourceMethodDispatcherFactory) {
            this.resourceMethodDispatcherFactory = resourceMethodDispatcherFactory;
            return this;
        }
        
        public Builder resourceMethodInvocationHandlerFactory(final ResourceMethodInvocationHandlerFactory resourceMethodInvocationHandlerFactory) {
            this.resourceMethodInvocationHandlerFactory = resourceMethodInvocationHandlerFactory;
            return this;
        }
        
        public Builder injectionManager(final InjectionManager injectionManager) {
            this.injectionManager = injectionManager;
            return this;
        }
        
        public Builder configuration(final Configuration configuration) {
            this.configuration = configuration;
            return this;
        }
        
        public Builder configurationValidator(final Supplier<ConfiguredValidator> configurationValidator) {
            this.configurationValidator = configurationValidator;
            return this;
        }
        
        public ResourceMethodInvoker build(final ResourceMethod method, final ProcessingProviders processingProviders) {
            if (this.resourceMethodDispatcherFactory == null) {
                throw new NullPointerException("ResourceMethodDispatcherFactory is not set.");
            }
            if (this.resourceMethodInvocationHandlerFactory == null) {
                throw new NullPointerException("ResourceMethodInvocationHandlerFactory is not set.");
            }
            if (this.injectionManager == null) {
                throw new NullPointerException("DI injection manager is not set.");
            }
            if (this.configuration == null) {
                throw new NullPointerException("Configuration is not set.");
            }
            if (this.configurationValidator == null) {
                throw new NullPointerException("Configuration validator is not set.");
            }
            return new ResourceMethodInvoker(this.resourceMethodDispatcherFactory, this.resourceMethodInvocationHandlerFactory, method, processingProviders, this.injectionManager, this.configuration, this.configurationValidator.get(), null);
        }
    }
}
