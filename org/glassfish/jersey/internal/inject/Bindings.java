package org.glassfish.jersey.internal.inject;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.lang.reflect.Type;
import javax.ws.rs.core.GenericType;
import java.util.Collection;

public final class Bindings
{
    private Bindings() {
        throw new AssertionError((Object)"Utility class instantiation forbidden.");
    }
    
    public static Collection<Binding> getBindings(final InjectionManager injectionManager, final Binder binder) {
        if (binder instanceof AbstractBinder) {
            ((AbstractBinder)binder).setInjectionManager(injectionManager);
        }
        return binder.getBindings();
    }
    
    public static <T> ClassBinding<T> service(final Class<T> serviceType) {
        return new ClassBinding<T>(serviceType);
    }
    
    public static <T> ClassBinding<T> serviceAsContract(final Class<T> serviceType) {
        return (ClassBinding)new ClassBinding((Class<Object>)serviceType).to((Class<? super Object>)serviceType);
    }
    
    public static <T> ClassBinding<T> service(final GenericType<T> serviceType) {
        return (ClassBinding)new ClassBinding(serviceType.getRawType()).asType((Class)serviceType.getType());
    }
    
    public static <T> ClassBinding<T> serviceAsContract(final GenericType<T> serviceType) {
        return (ClassBinding)new ClassBinding(serviceType.getRawType()).asType((Class)serviceType.getType()).to(serviceType.getType());
    }
    
    public static <T> ClassBinding<T> serviceAsContract(final Type serviceType) {
        return (ClassBinding)new ClassBinding((Class<Object>)ReflectionHelper.getRawClass(serviceType)).asType((Class)serviceType).to(serviceType);
    }
    
    public static <T> InstanceBinding<T> service(final T service) {
        return new InstanceBinding<T>(service);
    }
    
    public static <T> InstanceBinding<T> serviceAsContract(final T service) {
        return new InstanceBinding<T>(service, service.getClass());
    }
    
    public static <T> SupplierClassBinding<T> supplier(final Class<? extends Supplier<T>> supplierType, final Class<? extends Annotation> supplierScope) {
        return new SupplierClassBinding<T>(supplierType, supplierScope);
    }
    
    public static <T> SupplierClassBinding<T> supplier(final Class<? extends Supplier<T>> supplierType) {
        return new SupplierClassBinding<T>(supplierType, null);
    }
    
    public static <T> SupplierInstanceBinding<T> supplier(final Supplier<T> supplier) {
        return new SupplierInstanceBinding<T>(supplier);
    }
    
    public static <T extends InjectionResolver> InjectionResolverBinding<T> injectionResolver(final T resolver) {
        return new InjectionResolverBinding<T>(resolver);
    }
}
