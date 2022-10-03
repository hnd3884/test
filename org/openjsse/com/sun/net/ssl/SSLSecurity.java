package org.openjsse.com.sun.net.ssl;

import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import sun.security.jca.ProviderList;
import sun.security.jca.Providers;
import java.security.Provider;

final class SSLSecurity
{
    private SSLSecurity() {
    }
    
    private static Provider.Service getService(final String type, final String alg) {
        final ProviderList list = Providers.getProviderList();
        for (final Provider p : list.providers()) {
            final Provider.Service s = p.getService(type, alg);
            if (s != null) {
                return s;
            }
        }
        return null;
    }
    
    private static Object[] getImpl1(final String algName, final String engineType, final Provider.Service service) throws NoSuchAlgorithmException {
        final Provider provider = service.getProvider();
        final String className = service.getClassName();
        Class<?> implClass;
        try {
            final ClassLoader cl = provider.getClass().getClassLoader();
            if (cl == null) {
                implClass = Class.forName(className);
            }
            else {
                implClass = cl.loadClass(className);
            }
        }
        catch (final ClassNotFoundException e) {
            throw new NoSuchAlgorithmException("Class " + className + " configured for " + engineType + " not found: " + e.getMessage());
        }
        catch (final SecurityException e2) {
            throw new NoSuchAlgorithmException("Class " + className + " configured for " + engineType + " cannot be accessed: " + e2.getMessage());
        }
        try {
            Object obj = null;
            final Class<?> typeClassJavax;
            if ((typeClassJavax = Class.forName("javax.net.ssl." + engineType + "Spi")) != null && checkSuperclass(implClass, typeClassJavax)) {
                if (engineType.equals("SSLContext")) {
                    obj = new SSLContextSpiWrapper(algName, provider);
                }
                else if (engineType.equals("TrustManagerFactory")) {
                    obj = new TrustManagerFactorySpiWrapper(algName, provider);
                }
                else {
                    if (!engineType.equals("KeyManagerFactory")) {
                        throw new IllegalStateException("Class " + implClass.getName() + " unknown engineType wrapper:" + engineType);
                    }
                    obj = new KeyManagerFactorySpiWrapper(algName, provider);
                }
            }
            else {
                final Class<?> typeClassCom;
                if ((typeClassCom = Class.forName("org.openjsse.com.sun.net.ssl." + engineType + "Spi")) != null && checkSuperclass(implClass, typeClassCom)) {
                    obj = service.newInstance(null);
                }
            }
            if (obj != null) {
                return new Object[] { obj, provider };
            }
            throw new NoSuchAlgorithmException("Couldn't locate correct object or wrapper: " + engineType + " " + algName);
        }
        catch (final ClassNotFoundException e) {
            final IllegalStateException exc = new IllegalStateException("Engine Class Not Found for " + engineType);
            exc.initCause(e);
            throw exc;
        }
    }
    
    static Object[] getImpl(final String algName, final String engineType, final String provName) throws NoSuchAlgorithmException, NoSuchProviderException {
        Provider.Service service;
        if (provName != null) {
            final ProviderList list = Providers.getProviderList();
            final Provider prov = list.getProvider(provName);
            if (prov == null) {
                throw new NoSuchProviderException("No such provider: " + provName);
            }
            service = prov.getService(engineType, algName);
        }
        else {
            service = getService(engineType, algName);
        }
        if (service == null) {
            throw new NoSuchAlgorithmException("Algorithm " + algName + " not available");
        }
        return getImpl1(algName, engineType, service);
    }
    
    static Object[] getImpl(final String algName, final String engineType, final Provider prov) throws NoSuchAlgorithmException {
        final Provider.Service service = prov.getService(engineType, algName);
        if (service == null) {
            throw new NoSuchAlgorithmException("No such algorithm: " + algName);
        }
        return getImpl1(algName, engineType, service);
    }
    
    private static boolean checkSuperclass(Class<?> subclass, final Class<?> superclass) {
        if (subclass == null || superclass == null) {
            return false;
        }
        while (!subclass.equals(superclass)) {
            subclass = subclass.getSuperclass();
            if (subclass == null) {
                return false;
            }
        }
        return true;
    }
    
    static Object[] truncateArray(final Object[] oldArray, final Object[] newArray) {
        for (int i = 0; i < newArray.length; ++i) {
            newArray[i] = oldArray[i];
        }
        return newArray;
    }
}
