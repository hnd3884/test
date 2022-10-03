package io.netty.handler.ssl;

import javax.net.ssl.SSLContext;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.util.function.BiFunction;
import javax.net.ssl.SSLParameters;
import io.netty.util.internal.EmptyArrays;
import java.util.List;
import javax.net.ssl.SSLEngine;
import java.lang.reflect.Method;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.SuppressJava6Requirement;

@SuppressJava6Requirement(reason = "Usage guarded by java version check")
final class BouncyCastleAlpnSslUtils
{
    private static final InternalLogger logger;
    private static final Class BC_SSL_PARAMETERS;
    private static final Method SET_PARAMETERS;
    private static final Method SET_APPLICATION_PROTOCOLS;
    private static final Method GET_APPLICATION_PROTOCOL;
    private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
    private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
    private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
    private static final Class BC_APPLICATION_PROTOCOL_SELECTOR;
    private static final Method BC_APPLICATION_PROTOCOL_SELECTOR_SELECT;
    
    private BouncyCastleAlpnSslUtils() {
    }
    
    static String getApplicationProtocol(final SSLEngine sslEngine) {
        try {
            return (String)BouncyCastleAlpnSslUtils.GET_APPLICATION_PROTOCOL.invoke(sslEngine, new Object[0]);
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
            final Object bcSslParameters = BouncyCastleAlpnSslUtils.BC_SSL_PARAMETERS.newInstance();
            BouncyCastleAlpnSslUtils.SET_APPLICATION_PROTOCOLS.invoke(bcSslParameters, protocolArray);
            BouncyCastleAlpnSslUtils.SET_PARAMETERS.invoke(engine, bcSslParameters);
        }
        catch (final UnsupportedOperationException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new IllegalStateException(ex2);
        }
        engine.setSSLParameters(parameters);
    }
    
    static String getHandshakeApplicationProtocol(final SSLEngine sslEngine) {
        try {
            return (String)BouncyCastleAlpnSslUtils.GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke(sslEngine, new Object[0]);
        }
        catch (final UnsupportedOperationException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new IllegalStateException(ex2);
        }
    }
    
    static void setHandshakeApplicationProtocolSelector(final SSLEngine engine, final BiFunction<SSLEngine, List<String>, String> selector) {
        try {
            final Object selectorProxyInstance = Proxy.newProxyInstance(BouncyCastleAlpnSslUtils.class.getClassLoader(), new Class[] { BouncyCastleAlpnSslUtils.BC_APPLICATION_PROTOCOL_SELECTOR }, new InvocationHandler() {
                @Override
                public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                    if (method.getName().equals("select")) {
                        try {
                            return selector.apply(args[0], args[1]);
                        }
                        catch (final ClassCastException e) {
                            throw new RuntimeException("BCApplicationProtocolSelector select method parameter of invalid type.", e);
                        }
                    }
                    throw new UnsupportedOperationException(String.format("Method '%s' not supported.", method.getName()));
                }
            });
            BouncyCastleAlpnSslUtils.SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, selectorProxyInstance);
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
            final Object selector = BouncyCastleAlpnSslUtils.GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, new Object[0]);
            return new BiFunction<SSLEngine, List<String>, String>() {
                @Override
                public String apply(final SSLEngine sslEngine, final List<String> strings) {
                    try {
                        return (String)BouncyCastleAlpnSslUtils.BC_APPLICATION_PROTOCOL_SELECTOR_SELECT.invoke(selector, sslEngine, strings);
                    }
                    catch (final Exception e) {
                        throw new RuntimeException("Could not call getHandshakeApplicationProtocolSelector", e);
                    }
                }
            };
        }
        catch (final UnsupportedOperationException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new IllegalStateException(ex2);
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(BouncyCastleAlpnSslUtils.class);
        Class bcSslParameters;
        Class bcApplicationProtocolSelector;
        Method bcApplicationProtocolSelectorSelect;
        Method setParameters;
        Method setApplicationProtocols;
        Method getApplicationProtocol;
        Method getHandshakeApplicationProtocol;
        Method setHandshakeApplicationProtocolSelector;
        Method getHandshakeApplicationProtocolSelector;
        try {
            final Class testBCSslEngine;
            final Class bcSslEngine = testBCSslEngine = Class.forName("org.bouncycastle.jsse.BCSSLEngine");
            bcSslParameters = Class.forName("org.bouncycastle.jsse.BCSSLParameters");
            final Object bcSslParametersInstance = bcSslParameters.newInstance();
            final Class testBCSslParameters = bcSslParameters;
            final Class testBCApplicationProtocolSelector;
            bcApplicationProtocolSelector = (testBCApplicationProtocolSelector = Class.forName("org.bouncycastle.jsse.BCApplicationProtocolSelector"));
            bcApplicationProtocolSelectorSelect = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return testBCApplicationProtocolSelector.getMethod("select", Object.class, List.class);
                }
            });
            final SSLContext context = SslUtils.getSSLContext("BCJSSE");
            final SSLEngine engine = context.createSSLEngine();
            setParameters = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return testBCSslEngine.getMethod("setParameters", testBCSslParameters);
                }
            });
            setParameters.invoke(engine, bcSslParametersInstance);
            setApplicationProtocols = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return testBCSslParameters.getMethod("setApplicationProtocols", String[].class);
                }
            });
            setApplicationProtocols.invoke(bcSslParametersInstance, EmptyArrays.EMPTY_STRINGS);
            getApplicationProtocol = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return testBCSslEngine.getMethod("getApplicationProtocol", (Class[])new Class[0]);
                }
            });
            getApplicationProtocol.invoke(engine, new Object[0]);
            getHandshakeApplicationProtocol = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return testBCSslEngine.getMethod("getHandshakeApplicationProtocol", (Class[])new Class[0]);
                }
            });
            getHandshakeApplicationProtocol.invoke(engine, new Object[0]);
            setHandshakeApplicationProtocolSelector = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return testBCSslEngine.getMethod("setBCHandshakeApplicationProtocolSelector", testBCApplicationProtocolSelector);
                }
            });
            getHandshakeApplicationProtocolSelector = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return testBCSslEngine.getMethod("getBCHandshakeApplicationProtocolSelector", (Class[])new Class[0]);
                }
            });
            getHandshakeApplicationProtocolSelector.invoke(engine, new Object[0]);
        }
        catch (final Throwable t) {
            BouncyCastleAlpnSslUtils.logger.error("Unable to initialize BouncyCastleAlpnSslUtils.", t);
            bcSslParameters = null;
            setParameters = null;
            setApplicationProtocols = null;
            getApplicationProtocol = null;
            getHandshakeApplicationProtocol = null;
            setHandshakeApplicationProtocolSelector = null;
            getHandshakeApplicationProtocolSelector = null;
            bcApplicationProtocolSelectorSelect = null;
            bcApplicationProtocolSelector = null;
        }
        BC_SSL_PARAMETERS = bcSslParameters;
        SET_PARAMETERS = setParameters;
        SET_APPLICATION_PROTOCOLS = setApplicationProtocols;
        GET_APPLICATION_PROTOCOL = getApplicationProtocol;
        GET_HANDSHAKE_APPLICATION_PROTOCOL = getHandshakeApplicationProtocol;
        SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = setHandshakeApplicationProtocolSelector;
        GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = getHandshakeApplicationProtocolSelector;
        BC_APPLICATION_PROTOCOL_SELECTOR_SELECT = bcApplicationProtocolSelectorSelect;
        BC_APPLICATION_PROTOCOL_SELECTOR = bcApplicationProtocolSelector;
    }
}
