package com.sun.net.ssl;

import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import sun.security.jca.Providers;
import java.security.Provider;

final class SSLSecurity
{
    private SSLSecurity() {
    }
    
    private static Provider.Service getService(final String s, final String s2) {
        final Iterator<Provider> iterator = Providers.getProviderList().providers().iterator();
        while (iterator.hasNext()) {
            final Provider.Service service = iterator.next().getService(s, s2);
            if (service != null) {
                return service;
            }
        }
        return null;
    }
    
    private static Object[] getImpl1(final String s, final String s2, final Provider.Service service) throws NoSuchAlgorithmException {
        final Provider provider = service.getProvider();
        final String className = service.getClassName();
        Class<?> clazz;
        try {
            final ClassLoader classLoader = provider.getClass().getClassLoader();
            if (classLoader == null) {
                clazz = Class.forName(className);
            }
            else {
                clazz = classLoader.loadClass(className);
            }
        }
        catch (final ClassNotFoundException ex) {
            throw new NoSuchAlgorithmException("Class " + className + " configured for " + s2 + " not found: " + ex.getMessage());
        }
        catch (final SecurityException ex2) {
            throw new NoSuchAlgorithmException("Class " + className + " configured for " + s2 + " cannot be accessed: " + ex2.getMessage());
        }
        try {
            Object instance = null;
            final Class<?> forName;
            if ((forName = Class.forName("javax.net.ssl." + s2 + "Spi")) != null && checkSuperclass(clazz, forName)) {
                if (s2.equals("SSLContext")) {
                    instance = new SSLContextSpiWrapper(s, provider);
                }
                else if (s2.equals("TrustManagerFactory")) {
                    instance = new TrustManagerFactorySpiWrapper(s, provider);
                }
                else {
                    if (!s2.equals("KeyManagerFactory")) {
                        throw new IllegalStateException("Class " + clazz.getName() + " unknown engineType wrapper:" + s2);
                    }
                    instance = new KeyManagerFactorySpiWrapper(s, provider);
                }
            }
            else {
                final Class<?> forName2;
                if ((forName2 = Class.forName("com.sun.net.ssl." + s2 + "Spi")) != null && checkSuperclass(clazz, forName2)) {
                    instance = service.newInstance(null);
                }
            }
            if (instance != null) {
                return new Object[] { instance, provider };
            }
            throw new NoSuchAlgorithmException("Couldn't locate correct object or wrapper: " + s2 + " " + s);
        }
        catch (final ClassNotFoundException ex3) {
            final IllegalStateException ex4 = new IllegalStateException("Engine Class Not Found for " + s2);
            ex4.initCause(ex3);
            throw ex4;
        }
    }
    
    static Object[] getImpl(final String s, final String s2, final String s3) throws NoSuchAlgorithmException, NoSuchProviderException {
        Provider.Service service;
        if (s3 != null) {
            final Provider provider = Providers.getProviderList().getProvider(s3);
            if (provider == null) {
                throw new NoSuchProviderException("No such provider: " + s3);
            }
            service = provider.getService(s2, s);
        }
        else {
            service = getService(s2, s);
        }
        if (service == null) {
            throw new NoSuchAlgorithmException("Algorithm " + s + " not available");
        }
        return getImpl1(s, s2, service);
    }
    
    static Object[] getImpl(final String s, final String s2, final Provider provider) throws NoSuchAlgorithmException {
        final Provider.Service service = provider.getService(s2, s);
        if (service == null) {
            throw new NoSuchAlgorithmException("No such algorithm: " + s);
        }
        return getImpl1(s, s2, service);
    }
    
    private static boolean checkSuperclass(Class<?> superclass, final Class<?> clazz) {
        if (superclass == null || clazz == null) {
            return false;
        }
        while (!superclass.equals(clazz)) {
            superclass = superclass.getSuperclass();
            if (superclass == null) {
                return false;
            }
        }
        return true;
    }
    
    static Object[] truncateArray(final Object[] array, final Object[] array2) {
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = array[i];
        }
        return array2;
    }
}
