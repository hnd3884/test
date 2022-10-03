package org.glassfish.jersey.internal.inject;

import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.stream.Stream;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.Arrays;
import java.lang.annotation.Annotation;
import java.util.function.Supplier;
import java.lang.reflect.Type;
import javax.ws.rs.core.GenericType;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBinder implements Binder
{
    private List<Binding> internalBindings;
    private List<AbstractBinder> installed;
    private InjectionManager injectionManager;
    private boolean configured;
    
    public AbstractBinder() {
        this.internalBindings = new ArrayList<Binding>();
        this.installed = new ArrayList<AbstractBinder>();
        this.configured = false;
    }
    
    protected abstract void configure();
    
    void setInjectionManager(final InjectionManager injectionManager) {
        this.injectionManager = injectionManager;
    }
    
    protected final <T> Provider<T> createManagedInstanceProvider(final Class<T> clazz) {
        return (Provider<T>)(() -> {
            if (this.injectionManager == null) {
                throw new IllegalStateException(LocalizationMessages.INJECTION_MANAGER_NOT_PROVIDED());
            }
            return this.injectionManager.getInstance((Class<Object>)clazz);
        });
    }
    
    public <T> ClassBinding<T> bind(final Class<T> serviceType) {
        final ClassBinding<T> binding = Bindings.service(serviceType);
        this.internalBindings.add(binding);
        return binding;
    }
    
    public Binding bind(final Binding binding) {
        this.internalBindings.add(binding);
        return binding;
    }
    
    public <T> ClassBinding<T> bindAsContract(final Class<T> serviceType) {
        final ClassBinding<T> binding = Bindings.serviceAsContract(serviceType);
        this.internalBindings.add(binding);
        return binding;
    }
    
    public <T> ClassBinding<T> bindAsContract(final GenericType<T> serviceType) {
        final ClassBinding<T> binding = Bindings.service(serviceType);
        this.internalBindings.add(binding);
        return binding;
    }
    
    public ClassBinding<Object> bindAsContract(final Type serviceType) {
        final ClassBinding<Object> binding = Bindings.serviceAsContract(serviceType);
        this.internalBindings.add(binding);
        return binding;
    }
    
    public <T> InstanceBinding<T> bind(final T service) {
        final InstanceBinding<T> binding = Bindings.service(service);
        this.internalBindings.add(binding);
        return binding;
    }
    
    public <T> SupplierClassBinding<T> bindFactory(final Class<? extends Supplier<T>> supplierType, final Class<? extends Annotation> supplierScope) {
        final SupplierClassBinding<T> binding = Bindings.supplier(supplierType, supplierScope);
        this.internalBindings.add(binding);
        return binding;
    }
    
    public <T> SupplierClassBinding<T> bindFactory(final Class<? extends Supplier<T>> supplierType) {
        final SupplierClassBinding<T> binding = Bindings.supplier(supplierType);
        this.internalBindings.add(binding);
        return binding;
    }
    
    public <T> SupplierInstanceBinding<T> bindFactory(final Supplier<T> factory) {
        final SupplierInstanceBinding<T> binding = Bindings.supplier(factory);
        this.internalBindings.add(binding);
        return binding;
    }
    
    public <T extends InjectionResolver> InjectionResolverBinding<T> bind(final T resolver) {
        final InjectionResolverBinding<T> binding = Bindings.injectionResolver(resolver);
        this.internalBindings.add(binding);
        return binding;
    }
    
    public final void install(final AbstractBinder... binders) {
        Arrays.stream(binders).filter(Objects::nonNull).forEach(this.installed::add);
    }
    
    @Override
    public Collection<Binding> getBindings() {
        this.invokeConfigure();
        final List<Binding> bindings = this.installed.stream().flatMap(binder -> Bindings.getBindings(this.injectionManager, binder).stream()).collect((Collector<? super Object, ?, List<Binding>>)Collectors.toList());
        bindings.addAll(this.internalBindings);
        return bindings;
    }
    
    private void invokeConfigure() {
        if (!this.configured) {
            this.configure();
            this.configured = true;
        }
    }
}
