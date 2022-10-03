package com.zoho.security.threadlocal;

import java.util.function.Supplier;

public final class ZSecInheritableThreadLocal<T> extends InheritableThreadLocal<T>
{
    @Override
    public void set(final T value) {
        ZSecThreadLocalRegistry.registerThreadLocal(this);
        super.set(value);
    }
    
    @Override
    protected T initialValue() {
        ZSecThreadLocalRegistry.registerThreadLocal(this);
        return super.initialValue();
    }
    
    public static <S> ThreadLocal<S> withInitial(final Supplier<? extends S> supplier) {
        return new ZSecSuppliedThreadLocal<S>(supplier);
    }
}
