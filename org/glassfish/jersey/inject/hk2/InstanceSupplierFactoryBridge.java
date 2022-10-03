package org.glassfish.jersey.inject.hk2;

import org.glassfish.jersey.internal.inject.DisposableSupplier;
import java.util.function.Supplier;
import org.glassfish.hk2.api.Factory;

public class InstanceSupplierFactoryBridge<T> implements Factory<T>
{
    private Supplier<T> supplier;
    private boolean disposable;
    
    InstanceSupplierFactoryBridge(final Supplier<T> supplier, final boolean disposable) {
        this.supplier = supplier;
        this.disposable = disposable;
    }
    
    public T provide() {
        return this.supplier.get();
    }
    
    public void dispose(final T instance) {
        if (this.disposable) {
            ((DisposableSupplier)this.supplier).dispose((Object)instance);
        }
    }
}
