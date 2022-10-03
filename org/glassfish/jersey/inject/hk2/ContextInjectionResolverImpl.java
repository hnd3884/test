package org.glassfish.jersey.inject.hk2;

import java.util.Objects;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import org.glassfish.jersey.internal.inject.ForeignRequestScopeBridge;
import org.glassfish.hk2.utilities.InjecteeImpl;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import java.lang.reflect.Type;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.jersey.process.internal.RequestScoped;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.Set;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.jersey.internal.util.collection.Cache;
import javax.inject.Inject;
import org.glassfish.hk2.api.ServiceLocator;
import javax.inject.Singleton;
import org.glassfish.jersey.internal.inject.ContextInjectionResolver;
import javax.ws.rs.core.Context;
import org.glassfish.hk2.api.InjectionResolver;

@Singleton
public class ContextInjectionResolverImpl implements InjectionResolver<Context>, ContextInjectionResolver
{
    @Inject
    private ServiceLocator serviceLocator;
    private final Cache<CacheKey, ActiveDescriptor<?>> descriptorCache;
    private final Cache<CacheKey, Injectee> foreignRequestScopedInjecteeCache;
    private LazyValue<Set<Class<?>>> foreignRequestScopedComponents;
    
    public ContextInjectionResolverImpl() {
        this.descriptorCache = (Cache<CacheKey, ActiveDescriptor<?>>)new Cache(cacheKey -> this.serviceLocator.getInjecteeDescriptor(cacheKey.injectee));
        this.foreignRequestScopedInjecteeCache = (Cache<CacheKey, Injectee>)new Cache((Function)new Function<CacheKey, Injectee>() {
            @Override
            public Injectee apply(final CacheKey cacheKey) {
                final Injectee injectee = cacheKey.getInjectee();
                if (injectee.getParent() != null && Field.class.isAssignableFrom(injectee.getParent().getClass())) {
                    final Field f = (Field)injectee.getParent();
                    if (((Set)ContextInjectionResolverImpl.this.foreignRequestScopedComponents.get()).contains(f.getDeclaringClass())) {
                        final Class<?> clazz = f.getType();
                        if (ContextInjectionResolverImpl.this.serviceLocator.getServiceHandle((Class)clazz, new Annotation[0]).getActiveDescriptor().getScopeAnnotation() == RequestScoped.class) {
                            final AbstractActiveDescriptor<Object> descriptor = (AbstractActiveDescriptor<Object>)BuilderHelper.activeLink((Class)clazz).to((Type)clazz).in((Class)RequestScoped.class).build();
                            return (Injectee)new DescriptorOverridingInjectee(injectee, (ActiveDescriptor)descriptor);
                        }
                    }
                }
                return injectee;
            }
        });
        this.foreignRequestScopedComponents = (LazyValue<Set<Class<?>>>)Values.lazy(this::getForeignRequestScopedComponents);
    }
    
    public Object resolve(final Injectee injectee, final ServiceHandle<?> root) {
        final Type requiredType = injectee.getRequiredType();
        final boolean isHk2Factory = ReflectionHelper.isSubClassOf(requiredType, (Type)Factory.class);
        Injectee newInjectee;
        if (isHk2Factory) {
            newInjectee = this.getFactoryInjectee(injectee, ReflectionHelper.getTypeArgument(requiredType, 0));
        }
        else {
            newInjectee = (Injectee)this.foreignRequestScopedInjecteeCache.apply((Object)new CacheKey(injectee));
        }
        final ActiveDescriptor<?> ad = (ActiveDescriptor<?>)this.descriptorCache.apply((Object)new CacheKey(newInjectee));
        if (ad == null) {
            return null;
        }
        final ServiceHandle handle = this.serviceLocator.getServiceHandle((ActiveDescriptor)ad, newInjectee);
        if (isHk2Factory) {
            return this.asFactory(handle);
        }
        return handle.getService();
    }
    
    public Object resolve(final org.glassfish.jersey.internal.inject.Injectee injectee) {
        final InjecteeImpl hk2injectee = toInjecteeImpl(injectee);
        return this.resolve((Injectee)hk2injectee, null);
    }
    
    private static InjecteeImpl toInjecteeImpl(final org.glassfish.jersey.internal.inject.Injectee injectee) {
        final InjecteeImpl hk2injectee = new InjecteeImpl() {
            public Class<?> getInjecteeClass() {
                return injectee.getInjecteeClass();
            }
        };
        hk2injectee.setRequiredType(injectee.getRequiredType());
        hk2injectee.setRequiredQualifiers(injectee.getRequiredQualifiers());
        hk2injectee.setParent(injectee.getParent());
        if (injectee.getInjecteeDescriptor() != null) {
            hk2injectee.setInjecteeDescriptor((ActiveDescriptor)injectee.getInjecteeDescriptor().get());
        }
        return hk2injectee;
    }
    
    private Factory asFactory(final ServiceHandle handle) {
        return (Factory)new Factory() {
            public Object provide() {
                return handle.getService();
            }
            
            public void dispose(final Object instance) {
            }
        };
    }
    
    private Injectee getFactoryInjectee(final Injectee injectee, final Type requiredType) {
        return (Injectee)new RequiredTypeOverridingInjectee(injectee, requiredType);
    }
    
    public boolean isConstructorParameterIndicator() {
        return true;
    }
    
    public boolean isMethodParameterIndicator() {
        return false;
    }
    
    public Class<Context> getAnnotation() {
        return Context.class;
    }
    
    private Set<Class<?>> getForeignRequestScopedComponents() {
        final List<ForeignRequestScopeBridge> scopeBridges = this.serviceLocator.getAllServices((Class)ForeignRequestScopeBridge.class, new Annotation[0]);
        final Set<Class<?>> result = new HashSet<Class<?>>();
        for (final ForeignRequestScopeBridge bridge : scopeBridges) {
            final Set<Class<?>> requestScopedComponents = bridge.getRequestScopedComponents();
            if (requestScopedComponents != null) {
                result.addAll(requestScopedComponents);
            }
        }
        return result;
    }
    
    public static final class Binder extends AbstractBinder
    {
        protected void configure() {
            this.bind((Class)ContextInjectionResolverImpl.class).to((TypeLiteral)new TypeLiteral<InjectionResolver<Context>>() {}).to((TypeLiteral)new TypeLiteral<org.glassfish.jersey.internal.inject.InjectionResolver<Context>>() {}).to((Class)ContextInjectionResolver.class).in((Class)Singleton.class);
        }
    }
    
    private static class RequiredTypeOverridingInjectee extends InjecteeImpl
    {
        private RequiredTypeOverridingInjectee(final Injectee injectee, final Type requiredType) {
            super(injectee);
            this.setRequiredType(requiredType);
        }
    }
    
    private static class DescriptorOverridingInjectee extends InjecteeImpl
    {
        private DescriptorOverridingInjectee(final Injectee injectee, final ActiveDescriptor descriptor) {
            super(injectee);
            this.setInjecteeDescriptor(descriptor);
        }
    }
    
    private static class CacheKey
    {
        private final Injectee injectee;
        private final int hash;
        
        private CacheKey(final Injectee injectee) {
            this.injectee = injectee;
            this.hash = Objects.hash(injectee.getInjecteeClass(), injectee.getInjecteeDescriptor(), injectee.getParent(), injectee.getRequiredQualifiers(), injectee.getRequiredType(), injectee.getPosition());
        }
        
        private Injectee getInjectee() {
            return this.injectee;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CacheKey)) {
                return false;
            }
            final CacheKey cacheKey = (CacheKey)o;
            return this.hash == cacheKey.hash;
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
    }
}
