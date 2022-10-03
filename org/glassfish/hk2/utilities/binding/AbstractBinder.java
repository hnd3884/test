package org.glassfish.hk2.utilities.binding;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.glassfish.hk2.api.TwoPhaseResource;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.api.Factory;
import java.lang.reflect.Type;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.utilities.Binder;

public abstract class AbstractBinder implements Binder, DynamicConfiguration
{
    private transient DynamicConfiguration configuration;
    private transient AbstractBindingBuilder<?> currentBuilder;
    private transient HK2Loader defaultLoader;
    
    public <T> ServiceBindingBuilder<T> bind(final Class<T> serviceType) {
        return (ServiceBindingBuilder<T>)this.resetBuilder((AbstractBindingBuilder<Object>)AbstractBindingBuilder.create((Class<T>)serviceType, false));
    }
    
    public <T> ServiceBindingBuilder<T> bindAsContract(final Class<T> serviceType) {
        return (ServiceBindingBuilder<T>)this.resetBuilder((AbstractBindingBuilder<Object>)AbstractBindingBuilder.create((Class<T>)serviceType, true));
    }
    
    public <T> ServiceBindingBuilder<T> bindAsContract(final TypeLiteral<T> serviceType) {
        return (ServiceBindingBuilder<T>)this.resetBuilder((AbstractBindingBuilder<Object>)AbstractBindingBuilder.create((TypeLiteral<T>)serviceType, true));
    }
    
    public <T> ServiceBindingBuilder<T> bindAsContract(final Type serviceType) {
        return (ServiceBindingBuilder<T>)this.resetBuilder(AbstractBindingBuilder.create(serviceType, true));
    }
    
    public <T> ScopedBindingBuilder<T> bind(final T service) {
        return (ScopedBindingBuilder<T>)this.resetBuilder((AbstractBindingBuilder<Object>)AbstractBindingBuilder.create((T)service));
    }
    
    public <T> ServiceBindingBuilder<T> bindFactory(final Class<? extends Factory<T>> factoryType, final Class<? extends Annotation> factoryScope) {
        return (ServiceBindingBuilder<T>)this.resetBuilder(AbstractBindingBuilder.createFactoryBinder((Class<? extends Factory<Object>>)factoryType, factoryScope));
    }
    
    public <T> ServiceBindingBuilder<T> bindFactory(final Class<? extends Factory<T>> factoryType) {
        return (ServiceBindingBuilder<T>)this.resetBuilder(AbstractBindingBuilder.createFactoryBinder((Class<? extends Factory<Object>>)factoryType, (Class<? extends Annotation>)null));
    }
    
    public <T> ServiceBindingBuilder<T> bindFactory(final Factory<T> factory) {
        return (ServiceBindingBuilder<T>)this.resetBuilder((AbstractBindingBuilder<Object>)AbstractBindingBuilder.createFactoryBinder((Factory<T>)factory));
    }
    
    @Override
    public void bind(final DynamicConfiguration configuration) {
        if (this.configuration != null) {
            throw new IllegalArgumentException("Recursive configuration call detected.");
        }
        if (configuration == null) {
            throw new NullPointerException("configuration");
        }
        this.configuration = configuration;
        try {
            this.configure();
        }
        finally {
            this.complete();
        }
    }
    
    private <T> AbstractBindingBuilder<T> resetBuilder(final AbstractBindingBuilder<T> newBuilder) {
        if (this.currentBuilder != null) {
            this.currentBuilder.complete(this.configuration(), this.getDefaultBinderLoader());
        }
        return (AbstractBindingBuilder<T>)(this.currentBuilder = newBuilder);
    }
    
    private void complete() {
        try {
            this.resetBuilder((AbstractBindingBuilder<Object>)null);
        }
        finally {
            this.configuration = null;
        }
    }
    
    protected abstract void configure();
    
    private DynamicConfiguration configuration() {
        if (this.configuration == null) {
            throw new IllegalArgumentException("Dynamic configuration accessed from outside of an active binder configuration scope.");
        }
        return this.configuration;
    }
    
    @Override
    public <T> ActiveDescriptor<T> bind(final Descriptor descriptor) {
        return this.bind(descriptor, true);
    }
    
    @Override
    public <T> ActiveDescriptor<T> bind(final Descriptor descriptor, final boolean requiresDeepCopy) {
        this.setLoader(descriptor);
        return this.configuration().bind(descriptor, requiresDeepCopy);
    }
    
    @Override
    public FactoryDescriptors bind(final FactoryDescriptors factoryDescriptors) {
        return this.bind(factoryDescriptors, true);
    }
    
    @Override
    public FactoryDescriptors bind(final FactoryDescriptors factoryDescriptors, final boolean requiresDeepCopy) {
        this.setLoader(factoryDescriptors.getFactoryAsAService());
        this.setLoader(factoryDescriptors.getFactoryAsAFactory());
        return this.configuration().bind(factoryDescriptors, requiresDeepCopy);
    }
    
    @Override
    public <T> ActiveDescriptor<T> addActiveDescriptor(final ActiveDescriptor<T> activeDescriptor) throws IllegalArgumentException {
        return this.addActiveDescriptor(activeDescriptor, true);
    }
    
    @Override
    public <T> ActiveDescriptor<T> addActiveDescriptor(final ActiveDescriptor<T> activeDescriptor, final boolean requiresDeepCopy) throws IllegalArgumentException {
        return this.configuration().addActiveDescriptor(activeDescriptor, requiresDeepCopy);
    }
    
    @Override
    public <T> ActiveDescriptor<T> addActiveDescriptor(final Class<T> rawClass) throws MultiException, IllegalArgumentException {
        return this.configuration().addActiveDescriptor(rawClass);
    }
    
    @Override
    public <T> FactoryDescriptors addActiveFactoryDescriptor(final Class<? extends Factory<T>> rawFactoryClass) throws MultiException, IllegalArgumentException {
        return this.configuration().addActiveFactoryDescriptor((Class<? extends Factory<Object>>)rawFactoryClass);
    }
    
    @Override
    public void addUnbindFilter(final Filter unbindFilter) throws IllegalArgumentException {
        this.configuration().addUnbindFilter(unbindFilter);
    }
    
    @Override
    public void addIdempotentFilter(final Filter... unbindFilter) throws IllegalArgumentException {
        this.configuration().addIdempotentFilter(unbindFilter);
    }
    
    @Override
    public void registerTwoPhaseResources(final TwoPhaseResource... resources) {
        this.configuration().registerTwoPhaseResources(resources);
    }
    
    @Override
    public void commit() throws MultiException {
        this.configuration().commit();
    }
    
    public final void install(final Binder... binders) {
        for (final Binder binder : binders) {
            binder.bind(this);
        }
    }
    
    private void setLoader(final Descriptor descriptor) {
        if (descriptor.getLoader() == null && descriptor instanceof DescriptorImpl) {
            ((DescriptorImpl)descriptor).setLoader(this.getDefaultBinderLoader());
        }
    }
    
    private HK2Loader getDefaultBinderLoader() {
        if (this.defaultLoader == null) {
            final ClassLoader binderClassLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    final ClassLoader loader = this.getClass().getClassLoader();
                    if (loader == null) {
                        return ClassLoader.getSystemClassLoader();
                    }
                    return loader;
                }
            });
            this.defaultLoader = new HK2Loader() {
                @Override
                public Class<?> loadClass(final String className) throws MultiException {
                    try {
                        return binderClassLoader.loadClass(className);
                    }
                    catch (final ClassNotFoundException e) {
                        throw new MultiException(e);
                    }
                }
            };
        }
        return this.defaultLoader;
    }
}
