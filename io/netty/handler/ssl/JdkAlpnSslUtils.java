package io.netty.handler.ssl;

import io.netty.util.internal.PlatformDependent;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.function.BiFunction;
import javax.net.ssl.SSLParameters;
import io.netty.util.internal.EmptyArrays;
import java.util.List;
import javax.net.ssl.SSLEngine;
import java.lang.reflect.Method;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.SuppressJava6Requirement;

@SuppressJava6Requirement(reason = "Usage guarded by java version check")
final class JdkAlpnSslUtils
{
    private static final InternalLogger logger;
    private static final Method SET_APPLICATION_PROTOCOLS;
    private static final Method GET_APPLICATION_PROTOCOL;
    private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
    private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
    private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
    
    private JdkAlpnSslUtils() {
    }
    
    static boolean supportsAlpn() {
        return JdkAlpnSslUtils.GET_APPLICATION_PROTOCOL != null;
    }
    
    static String getApplicationProtocol(final SSLEngine sslEngine) {
        try {
            return (String)JdkAlpnSslUtils.GET_APPLICATION_PROTOCOL.invoke(sslEngine, new Object[0]);
        }
        catch (final UnsupportedOperationException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new IllegalStateException(ex2);
        }
    }
    
    static String getHandshakeApplicationProtocol(final SSLEngine sslEngine) {
        try {
            return (String)JdkAlpnSslUtils.GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke(sslEngine, new Object[0]);
        }
        catch (final UnsupportedOperationException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new IllegalStateException(ex2);
        }
    }
    
    static void setApplicationProtocols(final SSLEngine engine, final List<String> supportedProtocols) {
        final SSLParameters parameters = engine.getSSLParameters();
        final String[] protocolArray = supportedProtocols.toArray(EmptyArrays.EMPTY_STRINGS);
        try {
            JdkAlpnSslUtils.SET_APPLICATION_PROTOCOLS.invoke(parameters, protocolArray);
        }
        catch (final UnsupportedOperationException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new IllegalStateException(ex2);
        }
        engine.setSSLParameters(parameters);
    }
    
    static void setHandshakeApplicationProtocolSelector(final SSLEngine engine, final BiFunction<SSLEngine, List<String>, String> selector) {
        try {
            JdkAlpnSslUtils.SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, selector);
        }
        catch (final UnsupportedOperationException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new IllegalStateException(ex2);
        }
    }
    
    static BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector(final SSLEngine engine) {
        try {
            return (BiFunction)JdkAlpnSslUtils.GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, new Object[0]);
        }
        catch (final UnsupportedOperationException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new IllegalStateException(ex2);
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(JdkAlpnSslUtils.class);
        Method getHandshakeApplicationProtocol;
        Method getApplicationProtocol;
        Method setApplicationProtocols;
        Method setHandshakeApplicationProtocolSelector;
        Method getHandshakeApplicationProtocolSelector;
        try {
            final SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            final SSLEngine engine = context.createSSLEngine();
            getHandshakeApplicationProtocol = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return SSLEngine.class.getMethod("getHandshakeApplicationProtocol", (Class<?>[])new Class[0]);
                }
            });
            getHandshakeApplicationProtocol.invoke(engine, new Object[0]);
            getApplicationProtocol = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return SSLEngine.class.getMethod("getApplicationProtocol", (Class<?>[])new Class[0]);
                }
            });
            getApplicationProtocol.invoke(engine, new Object[0]);
            setApplicationProtocols = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
                }
            });
            setApplicationProtocols.invoke(engine.getSSLParameters(), EmptyArrays.EMPTY_STRINGS);
            setHandshakeApplicationProtocolSelector = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return SSLEngine.class.getMethod("setHandshakeApplicationProtocolSelector", BiFunction.class);
                }
            });
            setHandshakeApplicationProtocolSelector.invoke(engine, new BiFunction<SSLEngine, List<String>, String>() {
                @Override
                public String apply(final SSLEngine sslEngine, final List<String> strings) {
                    return null;
                }
            });
            getHandshakeApplicationProtocolSelector = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return SSLEngine.class.getMethod("getHandshakeApplicationProtocolSelector", (Class<?>[])new Class[0]);
                }
            });
            getHandshakeApplicationProtocolSelector.invoke(engine, new Object[0]);
        }
        catch (final Throwable t) {
            final int version = PlatformDependent.javaVersion();
            if (version >= 9) {
                JdkAlpnSslUtils.logger.error("Unable to initialize JdkAlpnSslUtils, but the detected java version was: {}", (Object)version, t);
            }
            getHandshakeApplicationProtocol = null;
            getApplicationProtocol = null;
            setApplicationProtocols = null;
            setHandshakeApplicationProtocolSelector = null;
            getHandshakeApplicationProtocolSelector = null;
        }
        GET_HANDSHAKE_APPLICATION_PROTOCOL = getHandshakeApplicationProtocol;
        GET_APPLICATION_PROTOCOL = getApplicationProtocol;
        SET_APPLICATION_PROTOCOLS = setApplicationProtocols;
        SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = setHandshakeApplicationProtocolSelector;
        GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = getHandshakeApplicationProtocolSelector;
    }
}
