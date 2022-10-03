package org.glassfish.jersey.server;

import org.glassfish.jersey.model.ContractProvider;
import java.util.function.BiPredicate;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.glassfish.jersey.internal.inject.ProviderBinder;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.spi.ComponentProvider;
import java.util.Map;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import org.glassfish.jersey.internal.inject.Providers;
import javax.ws.rs.RuntimeType;
import java.util.Set;
import org.glassfish.jersey.server.model.Resource;
import java.util.HashSet;
import java.util.Iterator;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.ResourceModel;
import java.util.Collection;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.BootstrapConfigurator;

public class ResourceModelConfigurator implements BootstrapConfigurator
{
    private static final Logger LOGGER;
    
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        final Collection<ModelProcessor> modelProcessors = serverBag.getModelProcessors();
        final ResourceConfig runtimeConfig = serverBag.getRuntimeConfig();
        final ResourceBag resourceBag = serverBag.getResourceBag();
        final ComponentBag componentBag = runtimeConfig.getComponentBag();
        this.bindProvidersAndResources(injectionManager, serverBag, componentBag, resourceBag.classes, resourceBag.instances, runtimeConfig);
        ResourceModel resourceModel = new ResourceModel.Builder(resourceBag.getRootResources(), false).build();
        resourceModel = this.processResourceModel(modelProcessors, resourceModel, runtimeConfig);
        this.bindEnhancingResourceClasses(injectionManager, serverBag, resourceModel, resourceBag, runtimeConfig);
        serverBag.setResourceModel(resourceModel);
        serverBag.getResourceContext().setResourceModel(resourceModel);
    }
    
    private ResourceModel processResourceModel(final Collection<ModelProcessor> modelProcessors, ResourceModel resourceModel, final ResourceConfig runtimeConfig) {
        for (final ModelProcessor modelProcessor : modelProcessors) {
            resourceModel = modelProcessor.processResourceModel(resourceModel, (Configuration)runtimeConfig);
        }
        return resourceModel;
    }
    
    private void bindEnhancingResourceClasses(final InjectionManager injectionManager, final ServerBootstrapBag bootstrapBag, final ResourceModel resourceModel, final ResourceBag resourceBag, final ResourceConfig runtimeConfig) {
        final Set<Class<?>> newClasses = new HashSet<Class<?>>();
        final Set<Object> newInstances = new HashSet<Object>();
        for (final Resource res : resourceModel.getRootResources()) {
            newClasses.addAll(res.getHandlerClasses());
            newInstances.addAll(res.getHandlerInstances());
        }
        newClasses.removeAll(resourceBag.classes);
        newInstances.removeAll(resourceBag.instances);
        final ComponentBag emptyComponentBag = ComponentBag.newInstance(input -> false);
        this.bindProvidersAndResources(injectionManager, bootstrapBag, emptyComponentBag, newClasses, newInstances, runtimeConfig);
    }
    
    private void bindProvidersAndResources(final InjectionManager injectionManager, final ServerBootstrapBag bootstrapBag, final ComponentBag componentBag, final Collection<Class<?>> resourceClasses, final Collection<Object> resourceInstances, final ResourceConfig runtimeConfig) {
        final Collection<ComponentProvider> componentProviders = (Collection<ComponentProvider>)bootstrapBag.getComponentProviders().get();
        final JerseyResourceContext resourceContext = bootstrapBag.getResourceContext();
        final Set<Class<?>> registeredClasses = runtimeConfig.getRegisteredClasses();
        Class<?> componentClass = null;
        final Predicate<Class<?>> correctlyConfigured = componentClass -> Providers.checkProviderRuntime(componentClass, componentBag.getModel(componentClass), RuntimeType.SERVER, !registeredClasses.contains(componentClass), resourceClasses.contains(componentClass));
        ContractProvider model = null;
        final BiPredicate<Class<?>, ContractProvider> correctlyConfiguredResource = (resourceClass, model) -> Providers.checkProviderRuntime(resourceClass, model, RuntimeType.SERVER, !registeredClasses.contains(resourceClass), true);
        final Set<Class<?>> componentClasses = (Set<Class<?>>)componentBag.getClasses(ComponentBag.excludeMetaProviders(injectionManager)).stream().filter(correctlyConfigured).collect(Collectors.toSet());
        final Set<Class<?>> classes = Collections.newSetFromMap(new IdentityHashMap<Class<?>, Boolean>());
        classes.addAll(componentClasses);
        classes.addAll(resourceClasses);
        final Iterator<Class<?>> iterator = classes.iterator();
        while (iterator.hasNext()) {
            componentClass = iterator.next();
            model = componentBag.getModel((Class)componentClass);
            if (this.bindWithComponentProvider(componentClass, model, componentProviders)) {
                continue;
            }
            if (resourceClasses.contains(componentClass)) {
                if (!Resource.isAcceptable(componentClass)) {
                    ResourceModelConfigurator.LOGGER.warning(LocalizationMessages.NON_INSTANTIABLE_COMPONENT(componentClass));
                }
                else {
                    if (model != null && !correctlyConfiguredResource.test(componentClass, model)) {
                        model = null;
                    }
                    resourceContext.unsafeBindResource(componentClass, model);
                }
            }
            else {
                ProviderBinder.bindProvider((Class)componentClass, model, injectionManager);
            }
        }
        final Set<Object> instances = (Set<Object>)componentBag.getInstances(ComponentBag.excludeMetaProviders(injectionManager)).stream().filter(instance -> correctlyConfigured.test(instance.getClass())).collect(Collectors.toSet());
        instances.addAll(resourceInstances);
        for (final Object component : instances) {
            ContractProvider model2 = componentBag.getModel((Class)component.getClass());
            if (resourceInstances.contains(component)) {
                if (model2 != null && !correctlyConfiguredResource.test(component.getClass(), model2)) {
                    model2 = null;
                }
                resourceContext.unsafeBindResource(component, model2);
            }
            else {
                ProviderBinder.bindProvider(component, model2, injectionManager);
            }
        }
    }
    
    private boolean bindWithComponentProvider(final Class<?> component, final ContractProvider providerModel, final Iterable<ComponentProvider> componentProviders) {
        final Set<Class<?>> contracts = (providerModel == null) ? Collections.emptySet() : providerModel.getContracts();
        for (final ComponentProvider provider : componentProviders) {
            if (provider.bind(component, contracts)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(ResourceModelConfigurator.class.getName());
    }
}
