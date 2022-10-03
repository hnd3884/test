package com.zoho.security.threadlocal;

import java.util.function.Supplier;

final class ZSecSuppliedThreadLocal<T> extends ThreadLocal<T>
{
    private final Supplier<? extends T> supplier;
    
    ZSecSuppliedThreadLocal(final Supplier<? extends T> supplier) {
        if (supplier == null) {
            throw new NullPointerException("Supplier specified to determine the threadlocal initial value is null");
        }
        this.supplier = supplier;
    }
    
    @Override
    protected T initialValue() {
        ZSecThreadLocalRegistry.registerThreadLocal(this);
        return (T)this.supplier.get();
    }
    
    @Override
    public void set(final T value) {
        ZSecThreadLocalRegistry.registerThreadLocal(this);
        super.set(value);
    }
}
