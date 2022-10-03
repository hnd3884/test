package org.glassfish.jersey.server.internal.routing;

import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.model.ModelValidationException;
import org.glassfish.jersey.server.model.internal.ModelErrors;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.server.model.ComponentModelValidator;
import java.util.Iterator;
import org.glassfish.jersey.server.model.ResourceModelComponent;
import org.glassfish.jersey.server.model.ResourceModel;
import org.glassfish.jersey.server.model.Resource;
import java.util.concurrent.ExecutionException;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.internal.guava.CacheLoader;
import java.util.concurrent.TimeUnit;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import org.glassfish.jersey.internal.guava.CacheBuilder;
import java.util.Map;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.internal.guava.LoadingCache;
import java.util.function.Function;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import java.util.Collection;
import org.glassfish.jersey.message.MessageBodyWorkers;
import javax.ws.rs.core.Configuration;
import java.util.logging.Logger;

final class RuntimeLocatorModelBuilder
{
    private static final Logger LOGGER;
    private final Configuration config;
    private final RuntimeModelBuilder runtimeModelBuilder;
    private final MessageBodyWorkers messageBodyWorkers;
    private final Collection<ValueParamProvider> valueSuppliers;
    private final JerseyResourceContext resourceContext;
    private final Iterable<ModelProcessor> modelProcessors;
    private final Function<Class<?>, ?> createServiceFunction;
    private final LoadingCache<LocatorCacheKey, LocatorRouting> cache;
    private final boolean disableValidation;
    private final boolean ignoreValidationErrors;
    private final boolean enableJerseyResourceCaching;
    
    RuntimeLocatorModelBuilder(final Configuration config, final MessageBodyWorkers messageBodyWorkers, final Collection<ValueParamProvider> valueSuppliers, final JerseyResourceContext resourceContext, final RuntimeModelBuilder runtimeModelBuilder, final Iterable<ModelProcessor> modelProcessors, final Function<Class<?>, ?> createServiceFunction) {
        this.config = config;
        this.messageBodyWorkers = messageBodyWorkers;
        this.valueSuppliers = valueSuppliers;
        this.runtimeModelBuilder = runtimeModelBuilder;
        this.resourceContext = resourceContext;
        this.modelProcessors = modelProcessors;
        this.createServiceFunction = createServiceFunction;
        this.disableValidation = ServerProperties.getValue(config.getProperties(), "jersey.config.server.resource.validation.disable", Boolean.FALSE, Boolean.class);
        this.ignoreValidationErrors = ServerProperties.getValue(config.getProperties(), "jersey.config.server.resource.validation.ignoreErrors", Boolean.FALSE, Boolean.class);
        this.enableJerseyResourceCaching = ServerProperties.getValue(config.getProperties(), "jersey.config.server.subresource.cache.jersey.resource.enabled", Boolean.FALSE, Boolean.class);
        final int size = ServerProperties.getValue(config.getProperties(), "jersey.config.server.subresource.cache.size", 64, Integer.class);
        final int age = ServerProperties.getValue(config.getProperties(), "jersey.config.server.subresource.cache.age", -1, Integer.class);
        final CacheBuilder<Object, Object> cacheBuilder = (CacheBuilder<Object, Object>)CacheBuilder.newBuilder();
        if (size > 0) {
            cacheBuilder.maximumSize((long)size);
        }
        else {
            RuntimeLocatorModelBuilder.LOGGER.log(Level.CONFIG, LocalizationMessages.SUBRES_LOC_CACHE_INVALID_SIZE(size, 64));
            cacheBuilder.maximumSize(64L);
        }
        if (age > 0) {
            cacheBuilder.expireAfterAccess((long)age, TimeUnit.SECONDS);
        }
        this.cache = (LoadingCache<LocatorCacheKey, LocatorRouting>)cacheBuilder.build((CacheLoader)new CacheLoader<LocatorCacheKey, LocatorRouting>() {
            public LocatorRouting load(final LocatorCacheKey key) throws Exception {
                return (key.clazz != null) ? RuntimeLocatorModelBuilder.this.createRouting(key.clazz) : RuntimeLocatorModelBuilder.this.buildRouting(key.resource);
            }
        });
    }
    
    Router getRouter(final ResourceMethod resourceMethod) {
        return new SubResourceLocatorRouter(this.createServiceFunction, this.valueSuppliers, resourceMethod, this.resourceContext, this);
    }
    
    LocatorRouting getRouting(final Class<?> locatorClass) {
        try {
            return (LocatorRouting)this.cache.get((Object)new LocatorCacheKey(locatorClass));
        }
        catch (final ExecutionException ee) {
            RuntimeLocatorModelBuilder.LOGGER.log(Level.FINE, LocalizationMessages.SUBRES_LOC_CACHE_LOAD_FAILED(locatorClass), ee);
            return this.createRouting(locatorClass);
        }
    }
    
    LocatorRouting getRouting(final Resource subresource) {
        if (this.enableJerseyResourceCaching) {
            try {
                return (LocatorRouting)this.cache.get((Object)new LocatorCacheKey(subresource));
            }
            catch (final ExecutionException ee) {
                RuntimeLocatorModelBuilder.LOGGER.log(Level.FINE, LocalizationMessages.SUBRES_LOC_CACHE_LOAD_FAILED(subresource), ee);
                return this.buildRouting(subresource);
            }
        }
        return this.buildRouting(subresource);
    }
    
    boolean isCached(final Class<?> srlClass) {
        return this.cache.getIfPresent((Object)srlClass) != null;
    }
    
    private LocatorRouting createRouting(final Class<?> locatorClass) {
        Resource.Builder builder = Resource.builder(locatorClass, this.disableValidation);
        if (builder == null) {
            builder = Resource.builder().name(locatorClass.getName());
        }
        return this.buildRouting(builder.build());
    }
    
    private LocatorRouting buildRouting(final Resource subResource) {
        final ResourceModel model = new ResourceModel.Builder(true).addResource(subResource).build();
        final ResourceModel enhancedModel = this.enhance(model);
        if (!this.disableValidation) {
            this.validateResource(enhancedModel);
        }
        final Resource enhancedLocator = enhancedModel.getResources().get(0);
        for (final Class<?> handlerClass : enhancedLocator.getHandlerClasses()) {
            this.resourceContext.bindResource(handlerClass);
        }
        return new LocatorRouting(enhancedModel, this.runtimeModelBuilder.buildModel(enhancedModel.getRuntimeResourceModel(), true));
    }
    
    private void validateResource(final ResourceModelComponent component) {
        Errors.process((Runnable)new Runnable() {
            @Override
            public void run() {
                final ComponentModelValidator validator = new ComponentModelValidator(RuntimeLocatorModelBuilder.this.valueSuppliers, RuntimeLocatorModelBuilder.this.messageBodyWorkers);
                validator.validate(component);
                if (Errors.fatalIssuesFound() && !RuntimeLocatorModelBuilder.this.ignoreValidationErrors) {
                    throw new ModelValidationException(LocalizationMessages.ERROR_VALIDATION_SUBRESOURCE(), ModelErrors.getErrorsAsResourceModelIssues());
                }
            }
        });
    }
    
    private ResourceModel enhance(ResourceModel subResourceModel) {
        for (final ModelProcessor modelProcessor : this.modelProcessors) {
            subResourceModel = modelProcessor.processSubResource(subResourceModel, this.config);
            this.validateSubResource(subResourceModel);
        }
        return subResourceModel;
    }
    
    private void validateSubResource(final ResourceModel subResourceModel) {
        if (subResourceModel.getResources().size() != 1) {
            throw new ProcessingException(LocalizationMessages.ERROR_SUB_RESOURCE_LOCATOR_MORE_RESOURCES(subResourceModel.getResources().size()));
        }
    }
    
    static {
        LOGGER = Logger.getLogger(RuntimeLocatorModelBuilder.class.getName());
    }
    
    private static class LocatorCacheKey
    {
        private final Class<?> clazz;
        private final Resource resource;
        
        public LocatorCacheKey(final Class<?> clazz) {
            this(clazz, null);
        }
        
        public LocatorCacheKey(final Resource resource) {
            this(null, resource);
        }
        
        private LocatorCacheKey(final Class<?> clazz, final Resource resource) {
            this.clazz = clazz;
            this.resource = resource;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final LocatorCacheKey that = (LocatorCacheKey)o;
            Label_0062: {
                if (this.clazz != null) {
                    if (this.clazz.equals(that.clazz)) {
                        break Label_0062;
                    }
                }
                else if (that.clazz == null) {
                    break Label_0062;
                }
                return false;
            }
            if (this.resource != null) {
                if (this.resource.equals(that.resource)) {
                    return true;
                }
            }
            else if (that.resource == null) {
                return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = (this.clazz != null) ? this.clazz.hashCode() : 0;
            result = 31 * result + ((this.resource != null) ? this.resource.hashCode() : 0);
            return result;
        }
    }
}
