package com.sun.naming.internal;

import java.lang.ref.WeakReference;

class NamedWeakReference<T> extends WeakReference<T>
{
    private final String name;
    
    NamedWeakReference(final T t, final String name) {
        super(t);
        this.name = name;
    }
    
    String getName() {
        return this.name;
    }
}
