package org.glassfish.hk2.osgiresourcelocator;

public abstract class ServiceLoader
{
    private static ServiceLoader _me;
    
    ServiceLoader() {
    }
    
    public static synchronized void initialize(final ServiceLoader singleton) {
        if (singleton == null) {
            throw new NullPointerException("Did you intend to call reset()?");
        }
        if (ServiceLoader._me != null) {
            throw new IllegalStateException("Already initialzed with [" + ServiceLoader._me + "]");
        }
        ServiceLoader._me = singleton;
    }
    
    public static synchronized void reset() {
        if (ServiceLoader._me == null) {
            throw new IllegalStateException("Not yet initialized");
        }
        ServiceLoader._me = null;
    }
    
    public static <T> Iterable<? extends T> lookupProviderInstances(final Class<T> serviceClass) {
        return lookupProviderInstances(serviceClass, null);
    }
    
    public static <T> Iterable<? extends T> lookupProviderInstances(final Class<T> serviceClass, final ProviderFactory<T> factory) {
        if (ServiceLoader._me == null) {
            return null;
        }
        return (Iterable<? extends T>)ServiceLoader._me.lookupProviderInstances1((Class<Object>)serviceClass, (ProviderFactory<Object>)factory);
    }
    
    public static <T> Iterable<Class> lookupProviderClasses(final Class<T> serviceClass) {
        return ServiceLoader._me.lookupProviderClasses1((Class<Object>)serviceClass);
    }
    
    abstract <T> Iterable<? extends T> lookupProviderInstances1(final Class<T> p0, final ProviderFactory<T> p1);
    
    abstract <T> Iterable<Class> lookupProviderClasses1(final Class<T> p0);
    
    public interface ProviderFactory<T>
    {
        T make(final Class p0, final Class<T> p1) throws Exception;
    }
}
