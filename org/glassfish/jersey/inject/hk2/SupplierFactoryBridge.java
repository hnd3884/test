package org.glassfish.jersey.inject.hk2;

import java.lang.annotation.Annotation;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import java.util.function.Supplier;
import java.util.IdentityHashMap;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.inject.DisposableSupplier;
import java.util.Map;
import java.lang.reflect.ParameterizedType;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Factory;

public class SupplierFactoryBridge<T> implements Factory<T>
{
    private ServiceLocator locator;
    private ParameterizedType beanType;
    private String beanName;
    private boolean disposable;
    private Map<Object, DisposableSupplier<T>> disposableSuppliers;
    
    SupplierFactoryBridge(final ServiceLocator locator, final Type beanType, final String beanName, final boolean disposable) {
        this.disposableSuppliers = new IdentityHashMap<Object, DisposableSupplier<T>>();
        this.locator = locator;
        this.beanType = (ParameterizedType)new ParameterizedTypeImpl((Type)Supplier.class, new Type[] { beanType });
        this.beanName = beanName;
        this.disposable = disposable;
    }
    
    public T provide() {
        if (this.beanType != null) {
            final Supplier<T> supplier = (Supplier<T>)this.locator.getService((Type)this.beanType, this.beanName, new Annotation[0]);
            final T instance = supplier.get();
            if (this.disposable) {
                this.disposableSuppliers.put((Object)instance, (org.glassfish.jersey.internal.inject.DisposableSupplier<Object>)supplier);
            }
            return instance;
        }
        return null;
    }
    
    public void dispose(final T instance) {
        if (this.disposable) {
            final DisposableSupplier<T> disposableSupplier = this.disposableSuppliers.get(instance);
            disposableSupplier.dispose((Object)instance);
            this.disposableSuppliers.remove(instance);
        }
    }
}
