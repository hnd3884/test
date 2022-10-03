package sun.security.jca;

import java.util.Iterator;
import java.util.List;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

public class GetInstance
{
    private GetInstance() {
    }
    
    public static Provider.Service getService(final String s, final String s2) throws NoSuchAlgorithmException {
        final Provider.Service service = Providers.getProviderList().getService(s, s2);
        if (service == null) {
            throw new NoSuchAlgorithmException(s2 + " " + s + " not available");
        }
        return service;
    }
    
    public static Provider.Service getService(final String s, final String s2, final String s3) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (s3 == null || s3.length() == 0) {
            throw new IllegalArgumentException("missing provider");
        }
        final Provider provider = Providers.getProviderList().getProvider(s3);
        if (provider == null) {
            throw new NoSuchProviderException("no such provider: " + s3);
        }
        final Provider.Service service = provider.getService(s, s2);
        if (service == null) {
            throw new NoSuchAlgorithmException("no such algorithm: " + s2 + " for provider " + s3);
        }
        return service;
    }
    
    public static Provider.Service getService(final String s, final String s2, final Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("missing provider");
        }
        final Provider.Service service = provider.getService(s, s2);
        if (service == null) {
            throw new NoSuchAlgorithmException("no such algorithm: " + s2 + " for provider " + provider.getName());
        }
        return service;
    }
    
    public static List<Provider.Service> getServices(final String s, final String s2) {
        return Providers.getProviderList().getServices(s, s2);
    }
    
    @Deprecated
    public static List<Provider.Service> getServices(final String s, final List<String> list) {
        return Providers.getProviderList().getServices(s, list);
    }
    
    public static List<Provider.Service> getServices(final List<ServiceId> list) {
        return Providers.getProviderList().getServices(list);
    }
    
    public static Instance getInstance(final String s, final Class<?> clazz, final String s2) throws NoSuchAlgorithmException {
        final ProviderList providerList = Providers.getProviderList();
        final Provider.Service service = providerList.getService(s, s2);
        if (service == null) {
            throw new NoSuchAlgorithmException(s2 + " " + s + " not available");
        }
        try {
            return getInstance(service, clazz);
        }
        catch (final NoSuchAlgorithmException ex) {
            NoSuchAlgorithmException ex2 = ex;
            for (final Provider.Service service2 : providerList.getServices(s, s2)) {
                if (service2 == service) {
                    continue;
                }
                try {
                    return getInstance(service2, clazz);
                }
                catch (final NoSuchAlgorithmException ex3) {
                    ex2 = ex3;
                    continue;
                }
                break;
            }
            throw ex2;
        }
    }
    
    public static Instance getInstance(final String s, final Class<?> clazz, final String s2, final Object o) throws NoSuchAlgorithmException {
        final List<Provider.Service> services = getServices(s, s2);
        NoSuchAlgorithmException ex = null;
        for (final Provider.Service service : services) {
            try {
                return getInstance(service, clazz, o);
            }
            catch (final NoSuchAlgorithmException ex2) {
                ex = ex2;
                continue;
            }
            break;
        }
        if (ex != null) {
            throw ex;
        }
        throw new NoSuchAlgorithmException(s2 + " " + s + " not available");
    }
    
    public static Instance getInstance(final String s, final Class<?> clazz, final String s2, final String s3) throws NoSuchAlgorithmException, NoSuchProviderException {
        return getInstance(getService(s, s2, s3), clazz);
    }
    
    public static Instance getInstance(final String s, final Class<?> clazz, final String s2, final Object o, final String s3) throws NoSuchAlgorithmException, NoSuchProviderException {
        return getInstance(getService(s, s2, s3), clazz, o);
    }
    
    public static Instance getInstance(final String s, final Class<?> clazz, final String s2, final Provider provider) throws NoSuchAlgorithmException {
        return getInstance(getService(s, s2, provider), clazz);
    }
    
    public static Instance getInstance(final String s, final Class<?> clazz, final String s2, final Object o, final Provider provider) throws NoSuchAlgorithmException {
        return getInstance(getService(s, s2, provider), clazz, o);
    }
    
    public static Instance getInstance(final Provider.Service service, final Class<?> clazz) throws NoSuchAlgorithmException {
        final Object instance = service.newInstance(null);
        checkSuperClass(service, instance.getClass(), clazz);
        return new Instance(service.getProvider(), instance);
    }
    
    public static Instance getInstance(final Provider.Service service, final Class<?> clazz, final Object o) throws NoSuchAlgorithmException {
        final Object instance = service.newInstance(o);
        checkSuperClass(service, instance.getClass(), clazz);
        return new Instance(service.getProvider(), instance);
    }
    
    public static void checkSuperClass(final Provider.Service service, final Class<?> clazz, final Class<?> clazz2) throws NoSuchAlgorithmException {
        if (clazz2 == null) {
            return;
        }
        if (!clazz2.isAssignableFrom(clazz)) {
            throw new NoSuchAlgorithmException("class configured for " + service.getType() + ": " + service.getClassName() + " not a " + service.getType());
        }
    }
    
    public static final class Instance
    {
        public final Provider provider;
        public final Object impl;
        
        private Instance(final Provider provider, final Object impl) {
            this.provider = provider;
            this.impl = impl;
        }
        
        public Object[] toArray() {
            return new Object[] { this.impl, this.provider };
        }
    }
}
