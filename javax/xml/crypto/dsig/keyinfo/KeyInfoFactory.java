package javax.xml.crypto.dsig.keyinfo;

import java.security.AccessController;
import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedAction;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.URIDereferencer;
import java.math.BigInteger;
import java.security.KeyException;
import java.security.PublicKey;
import java.util.List;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.lang.reflect.InvocationTargetException;
import javax.xml.crypto.NoSuchMechanismException;
import java.lang.reflect.Method;
import java.security.Provider;

public abstract class KeyInfoFactory
{
    private String mechanismType;
    private Provider provider;
    private static Class cl;
    private static final Class[] getImplParams;
    private static Method getImplMethod;
    
    protected KeyInfoFactory() {
    }
    
    public static KeyInfoFactory getInstance(final String s) {
        if (s == null) {
            throw new NullPointerException("mechanismType cannot be null");
        }
        return findInstance(s, null);
    }
    
    private static KeyInfoFactory findInstance(final String mechanismType, final Provider provider) {
        if (KeyInfoFactory.getImplMethod == null) {
            throw new NoSuchMechanismException("Cannot find " + mechanismType + " mechanism type");
        }
        Object[] array;
        try {
            array = (Object[])KeyInfoFactory.getImplMethod.invoke(null, mechanismType, "KeyInfoFactory", provider);
        }
        catch (final IllegalAccessException ex) {
            throw new NoSuchMechanismException("Cannot find " + mechanismType + " mechanism type", ex);
        }
        catch (final InvocationTargetException ex2) {
            throw new NoSuchMechanismException("Cannot find " + mechanismType + " mechanism type", ex2);
        }
        final KeyInfoFactory keyInfoFactory = (KeyInfoFactory)array[0];
        keyInfoFactory.mechanismType = mechanismType;
        keyInfoFactory.provider = (Provider)array[1];
        return keyInfoFactory;
    }
    
    public static KeyInfoFactory getInstance(final String s, final Provider provider) {
        if (s == null) {
            throw new NullPointerException("mechanismType cannot be null");
        }
        if (provider == null) {
            throw new NullPointerException("provider cannot be null");
        }
        return findInstance(s, provider);
    }
    
    public static KeyInfoFactory getInstance(final String s, final String s2) throws NoSuchProviderException {
        if (s == null) {
            throw new NullPointerException("mechanismType cannot be null");
        }
        if (s2 == null) {
            throw new NullPointerException("provider cannot be null");
        }
        final Provider provider = Security.getProvider(s2);
        if (provider == null) {
            throw new NoSuchProviderException("cannot find provider named " + s2);
        }
        return findInstance(s, provider);
    }
    
    public static KeyInfoFactory getInstance() {
        return getInstance("DOM");
    }
    
    public final String getMechanismType() {
        return this.mechanismType;
    }
    
    public final Provider getProvider() {
        return this.provider;
    }
    
    public abstract KeyInfo newKeyInfo(final List p0);
    
    public abstract KeyInfo newKeyInfo(final List p0, final String p1);
    
    public abstract KeyName newKeyName(final String p0);
    
    public abstract KeyValue newKeyValue(final PublicKey p0) throws KeyException;
    
    public abstract PGPData newPGPData(final byte[] p0);
    
    public abstract PGPData newPGPData(final byte[] p0, final byte[] p1, final List p2);
    
    public abstract PGPData newPGPData(final byte[] p0, final List p1);
    
    public abstract RetrievalMethod newRetrievalMethod(final String p0);
    
    public abstract RetrievalMethod newRetrievalMethod(final String p0, final String p1, final List p2);
    
    public abstract X509Data newX509Data(final List p0);
    
    public abstract X509IssuerSerial newX509IssuerSerial(final String p0, final BigInteger p1);
    
    public abstract boolean isFeatureSupported(final String p0);
    
    public abstract URIDereferencer getURIDereferencer();
    
    public abstract KeyInfo unmarshalKeyInfo(final XMLStructure p0) throws MarshalException;
    
    static {
        getImplParams = new Class[] { String.class, String.class, Provider.class };
        try {
            KeyInfoFactory.cl = Class.forName("javax.xml.crypto.dsig.XMLDSigSecurity");
        }
        catch (final ClassNotFoundException ex) {}
        KeyInfoFactory.getImplMethod = AccessController.doPrivileged((PrivilegedAction<Method>)new PrivilegedAction() {
            public Object run() {
                AccessibleObject declaredMethod = null;
                try {
                    declaredMethod = KeyInfoFactory.cl.getDeclaredMethod("getImpl", (Class[])KeyInfoFactory.getImplParams);
                    if (declaredMethod != null) {
                        declaredMethod.setAccessible(true);
                    }
                }
                catch (final NoSuchMethodException ex) {}
                return declaredMethod;
            }
        });
    }
}
