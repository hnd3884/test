package org.glassfish.jersey.server.internal;

import org.glassfish.jersey.internal.inject.ClassBinding;
import java.util.Collection;
import org.glassfish.jersey.process.internal.RequestScoped;
import java.lang.reflect.AnnotatedElement;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import javax.inject.Scope;
import java.util.Iterator;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.internal.inject.CustomAnnotationLiteral;
import org.glassfish.jersey.internal.inject.Providers;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.inject.Bindings;
import javax.inject.Singleton;
import org.glassfish.jersey.model.ContractProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Collections;
import java.util.IdentityHashMap;
import org.glassfish.jersey.server.model.ResourceModel;
import java.util.Set;
import org.glassfish.jersey.internal.inject.Binding;
import java.util.function.Consumer;
import java.util.function.Function;
import org.glassfish.jersey.server.ExtendedResourceContext;

public class JerseyResourceContext implements ExtendedResourceContext
{
    private final Function<Class<?>, ?> getOrCreateInstance;
    private final Consumer<Object> injectInstance;
    private final Consumer<Binding> registerBinding;
    private final Set<Class<?>> bindingCache;
    private final Object bindingCacheLock;
    private volatile ResourceModel resourceModel;
    
    public JerseyResourceContext(final Function<Class<?>, ?> getOrCreateInstance, final Consumer<Object> injectInstance, final Consumer<Binding> registerBinding) {
        this.getOrCreateInstance = getOrCreateInstance;
        this.injectInstance = injectInstance;
        this.registerBinding = registerBinding;
        this.bindingCache = Collections.newSetFromMap(new IdentityHashMap<Class<?>, Boolean>());
        this.bindingCacheLock = new Object();
    }
    
    public <T> T getResource(final Class<T> resourceClass) {
        try {
            return (T)this.getOrCreateInstance.apply(resourceClass);
        }
        catch (final Exception ex) {
            Logger.getLogger(JerseyResourceContext.class.getName()).log(Level.WARNING, LocalizationMessages.RESOURCE_LOOKUP_FAILED(resourceClass), ex);
            return null;
        }
    }
    
    public <T> T initResource(final T resource) {
        this.injectInstance.accept(resource);
        return resource;
    }
    
    public <T> void bindResource(final Class<T> resourceClass) {
        if (this.bindingCache.contains(resourceClass)) {
            return;
        }
        synchronized (this.bindingCacheLock) {
            if (this.bindingCache.contains(resourceClass)) {
                return;
            }
            this.unsafeBindResource(resourceClass, null);
        }
    }
    
    public <T> void bindResourceIfSingleton(final T resource) {
        final Class<?> resourceClass = resource.getClass();
        if (this.bindingCache.contains(resourceClass)) {
            return;
        }
        synchronized (this.bindingCacheLock) {
            if (this.bindingCache.contains(resourceClass)) {
                return;
            }
            if (getScope(resourceClass) == Singleton.class) {
                this.registerBinding.accept(Bindings.service((Object)resource).to((Class)resourceClass));
            }
            this.bindingCache.add(resourceClass);
        }
    }
    
    public void unsafeBindResource(final Object resource, final ContractProvider providerModel) {
        final Class<?> resourceClass = resource.getClass();
        Binding binding;
        if (providerModel != null) {
            final Class<? extends Annotation> scope = providerModel.getScope();
            binding = Bindings.service(resource).to((Type)resourceClass);
            for (final Class contract : Providers.getProviderContracts((Class)resourceClass)) {
                binding.addAlias(contract).in(scope.getName()).qualifiedBy((Annotation)CustomAnnotationLiteral.INSTANCE);
            }
        }
        else {
            binding = (Binding)Bindings.serviceAsContract((Class)resourceClass);
        }
        this.registerBinding.accept(binding);
        this.bindingCache.add(resourceClass);
    }
    
    private static Class<? extends Annotation> getScope(final Class<?> resourceClass) {
        final Collection<Class<? extends Annotation>> scopes = ReflectionHelper.getAnnotationTypes((AnnotatedElement)resourceClass, (Class)Scope.class);
        return (Class<? extends Annotation>)(scopes.isEmpty() ? RequestScoped.class : ((Class<? extends Annotation>)scopes.iterator().next()));
    }
    
    public <T> void unsafeBindResource(final Class<T> resourceClass, final ContractProvider providerModel) {
        ClassBinding<T> descriptor;
        if (providerModel != null) {
            final Class<? extends Annotation> scope = providerModel.getScope();
            descriptor = (ClassBinding<T>)Bindings.serviceAsContract((Class)resourceClass).in((Class)scope);
            for (final Class contract : providerModel.getContracts()) {
                descriptor.addAlias(contract).in(scope.getName()).ranked(providerModel.getPriority(contract)).qualifiedBy((Annotation)CustomAnnotationLiteral.INSTANCE);
            }
        }
        else {
            descriptor = (ClassBinding<T>)Bindings.serviceAsContract((Class)resourceClass).in((Class)getScope(resourceClass));
        }
        this.registerBinding.accept((Binding)descriptor);
        this.bindingCache.add(resourceClass);
    }
    
    @Override
    public ResourceModel getResourceModel() {
        return this.resourceModel;
    }
    
    public void setResourceModel(final ResourceModel resourceModel) {
        this.resourceModel = resourceModel;
    }
}
