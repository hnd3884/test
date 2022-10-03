package javax.xml.crypto.dsig;

import java.util.Hashtable;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import java.util.Enumeration;
import java.security.Provider;
import java.security.NoSuchAlgorithmException;
import javax.xml.crypto.NoSuchMechanismException;
import java.security.Security;
import java.util.Map;

final class XMLDSigSecurity
{
    private XMLDSigSecurity() {
    }
    
    private static ProviderProperty getEngineClassName(final String s, final Map.Entry entry, final String s2, final String s3, final boolean b) throws NoSuchAlgorithmException {
        final Provider[] providers = Security.getProviders();
        int n = 0;
        ProviderProperty engineClassName = null;
        for (int n2 = 0; n2 < providers.length && n == 0; ++n2) {
            try {
                engineClassName = getEngineClassName(s, entry, s2, s3, providers[n2], b);
                n = 1;
            }
            catch (final Exception ex) {}
        }
        if (n != 0) {
            return engineClassName;
        }
        if (b) {
            throw new NoSuchMechanismException("Mechanism type " + s + " not available");
        }
        throw new NoSuchAlgorithmException("Algorithm type " + s + " not available");
    }
    
    private static ProviderProperty getEngineClassName(final String s, final Map.Entry entry, final String s2, String string, final Provider provider, final boolean b) throws NoSuchAlgorithmException {
        String className = getProviderProperty(string, entry, provider);
        if (className == null) {
            final String standardName = getStandardName(s, s2, provider);
            if (standardName != null) {
                string = s2 + "." + standardName;
            }
            if (standardName == null || (className = getProviderProperty(string, entry, provider)) == null) {
                if (b) {
                    throw new NoSuchMechanismException("no such mechanism type: " + s + " for provider " + provider.getName());
                }
                throw new NoSuchAlgorithmException("no such algorithm: " + s + " for provider " + provider.getName());
            }
        }
        final ProviderProperty providerProperty = new ProviderProperty();
        providerProperty.className = className;
        providerProperty.provider = provider;
        return providerProperty;
    }
    
    private static boolean checkSuperclass(final Class clazz, final Class clazz2) {
        return clazz2.isAssignableFrom(clazz);
    }
    
    static Object[] getImpl(final String s, final String s2, final Provider provider) throws NoSuchAlgorithmException {
        return getImpl(s, null, s2, provider);
    }
    
    static Object[] getImpl(final String s, final Map.Entry entry, final String s2, final Provider provider) throws NoSuchAlgorithmException {
        Class clazz = null;
        boolean b = true;
        if (s2.equals("XMLSignatureFactory")) {
            clazz = XMLSignatureFactory.class;
        }
        else if (s2.equals("KeyInfoFactory")) {
            clazz = KeyInfoFactory.class;
        }
        else if (s2.equals("TransformService")) {
            clazz = TransformService.class;
            b = false;
        }
        final String string = s2 + "." + s;
        if (provider == null) {
            return doGetImpl(s2, clazz, getEngineClassName(s, entry, s2, string, b), b);
        }
        return doGetImpl(s2, clazz, getEngineClassName(s, entry, s2, string, provider, b), b);
    }
    
    private static Object[] doGetImpl(final String s, final Class clazz, final ProviderProperty providerProperty, final boolean b) throws NoSuchAlgorithmException {
        final String className = providerProperty.className;
        final String name = providerProperty.provider.getName();
        try {
            final ClassLoader classLoader = providerProperty.provider.getClass().getClassLoader();
            Class<?> clazz2;
            if (classLoader != null) {
                clazz2 = classLoader.loadClass(className);
            }
            else {
                clazz2 = Class.forName(className);
            }
            if (checkSuperclass(clazz2, clazz)) {
                return new Object[] { clazz2.newInstance(), providerProperty.provider };
            }
            if (b) {
                throw new NoSuchMechanismException("class configured for " + s + ": " + className + " not a " + s);
            }
            throw new NoSuchAlgorithmException("class configured for " + s + ": " + className + " not a " + s);
        }
        catch (final ClassNotFoundException ex) {
            if (b) {
                throw new NoSuchMechanismException("class configured for " + s + "(provider: " + name + ")" + "cannot be found.\n", ex);
            }
            throw (NoSuchAlgorithmException)new NoSuchAlgorithmException("class configured for " + s + "(provider: " + name + ")" + "cannot be found.\n").initCause(ex);
        }
        catch (final InstantiationException ex2) {
            if (b) {
                throw new NoSuchMechanismException("class " + className + " configured for " + s + "(provider: " + name + ") cannot be " + "instantiated. ", ex2);
            }
            throw (NoSuchAlgorithmException)new NoSuchAlgorithmException("class " + className + " configured for " + s + "(provider: " + name + ") cannot be " + "instantiated. ").initCause(ex2);
        }
        catch (final IllegalAccessException ex3) {
            if (b) {
                throw new NoSuchMechanismException("class " + className + " configured for " + s + "(provider: " + name + ") cannot be accessed.\n", ex3);
            }
            throw (NoSuchAlgorithmException)new NoSuchAlgorithmException("class " + className + " configured for " + s + "(provider: " + name + ") cannot be accessed.\n").initCause(ex3);
        }
    }
    
    private static String getProviderProperty(final String s, final Map.Entry entry, final Provider provider) {
        String s2 = provider.getProperty(s);
        if (s2 == null) {
            final Enumeration<Object> keys = ((Hashtable<Object, V>)provider).keys();
            while (keys.hasMoreElements()) {
                final String s3 = keys.nextElement();
                if (s.equalsIgnoreCase(s3)) {
                    s2 = provider.getProperty(s3);
                    break;
                }
            }
        }
        if (s2 != null && entry != null && !provider.entrySet().contains(entry) && (!entry.getValue().equals("DOM") || provider.get(entry.getKey()) != null)) {
            s2 = null;
        }
        return s2;
    }
    
    private static String getStandardName(final String s, final String s2, final Provider provider) {
        return getProviderProperty("Alg.Alias." + s2 + "." + s, null, provider);
    }
    
    private static class ProviderProperty
    {
        String className;
        Provider provider;
    }
}
