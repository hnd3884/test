package io.opencensus.internal;

import java.util.ServiceConfigurationError;

public final class Provider
{
    private Provider() {
    }
    
    public static <T> T createInstance(final Class<?> rawClass, final Class<T> superclass) {
        try {
            return (T)rawClass.asSubclass(superclass).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (final Exception e) {
            throw new ServiceConfigurationError("Provider " + rawClass.getName() + " could not be instantiated.", e);
        }
    }
}
