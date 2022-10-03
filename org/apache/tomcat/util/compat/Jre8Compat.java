package org.apache.tomcat.util.compat;

import java.util.Map;
import org.apache.juli.logging.LogFactory;
import javax.net.ssl.SSLEngine;
import java.util.Collections;
import java.security.KeyStore;
import java.net.URI;
import java.lang.reflect.InvocationTargetException;
import javax.net.ssl.SSLParameters;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

class Jre8Compat extends JreCompat
{
    private static final Log log;
    private static final StringManager sm;
    private static final int RUNTIME_MAJOR_VERSION = 8;
    private static final Method setUseCipherSuitesOrderMethod;
    private static final Constructor<?> domainLoadStoreParameterConstructor;
    protected static final Method setApplicationProtocolsMethod;
    protected static final Method getApplicationProtocolMethod;
    
    static boolean isSupported() {
        return Jre8Compat.setUseCipherSuitesOrderMethod != null;
    }
    
    @Override
    public void setUseServerCipherSuitesOrder(final SSLParameters sslParameters, final boolean useCipherSuitesOrder) {
        try {
            Jre8Compat.setUseCipherSuitesOrderMethod.invoke(sslParameters, useCipherSuitesOrder);
        }
        catch (final IllegalArgumentException e) {
            throw new UnsupportedOperationException(e);
        }
        catch (final IllegalAccessException e2) {
            throw new UnsupportedOperationException(e2);
        }
        catch (final InvocationTargetException e3) {
            throw new UnsupportedOperationException(e3);
        }
    }
    
    @Override
    public KeyStore.LoadStoreParameter getDomainLoadStoreParameter(final URI uri) {
        try {
            return (KeyStore.LoadStoreParameter)Jre8Compat.domainLoadStoreParameterConstructor.newInstance(uri, Collections.EMPTY_MAP);
        }
        catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }
    
    @Override
    public int jarFileRuntimeMajorVersion() {
        return 8;
    }
    
    @Override
    public void setApplicationProtocols(final SSLParameters sslParameters, final String[] protocols) {
        if (Jre8Compat.setApplicationProtocolsMethod != null) {
            try {
                Jre8Compat.setApplicationProtocolsMethod.invoke(sslParameters, protocols);
                return;
            }
            catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new UnsupportedOperationException(e);
            }
        }
        super.setApplicationProtocols(sslParameters, protocols);
    }
    
    @Override
    public String getApplicationProtocol(final SSLEngine sslEngine) {
        if (Jre8Compat.getApplicationProtocolMethod != null) {
            try {
                return (String)Jre8Compat.getApplicationProtocolMethod.invoke(sslEngine, new Object[0]);
            }
            catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new UnsupportedOperationException(e);
            }
        }
        return super.getApplicationProtocol(sslEngine);
    }
    
    public static boolean isAlpnSupported() {
        return Jre8Compat.setApplicationProtocolsMethod != null && Jre8Compat.getApplicationProtocolMethod != null;
    }
    
    static {
        log = LogFactory.getLog((Class)Jre8Compat.class);
        sm = StringManager.getManager(Jre8Compat.class);
        Method m1 = null;
        Constructor<?> c2 = null;
        try {
            final Class<?> clazz1 = Class.forName("javax.net.ssl.SSLParameters");
            m1 = clazz1.getMethod("setUseCipherSuitesOrder", Boolean.TYPE);
            final Class<?> clazz2 = Class.forName("java.security.DomainLoadStoreParameter");
            c2 = clazz2.getConstructor(URI.class, Map.class);
        }
        catch (final SecurityException e) {
            Jre8Compat.log.error((Object)Jre8Compat.sm.getString("jre8Compat.unexpected"), (Throwable)e);
        }
        catch (final NoSuchMethodException e2) {
            if (m1 == null) {
                Jre8Compat.log.debug((Object)Jre8Compat.sm.getString("jre8Compat.javaPre8"), (Throwable)e2);
            }
            else {
                Jre8Compat.log.error((Object)Jre8Compat.sm.getString("jre8Compat.unexpected"), (Throwable)e2);
            }
        }
        catch (final ClassNotFoundException e3) {
            Jre8Compat.log.error((Object)Jre8Compat.sm.getString("jre8Compat.unexpected"), (Throwable)e3);
        }
        setUseCipherSuitesOrderMethod = m1;
        domainLoadStoreParameterConstructor = c2;
        Method m2 = null;
        Method m3 = null;
        try {
            m2 = SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
            m3 = SSLEngine.class.getMethod("getApplicationProtocol", (Class<?>[])new Class[0]);
        }
        catch (final ReflectiveOperationException | IllegalArgumentException ex) {}
        setApplicationProtocolsMethod = m2;
        getApplicationProtocolMethod = m3;
    }
}
