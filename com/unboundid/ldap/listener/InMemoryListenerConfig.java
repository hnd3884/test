package com.unboundid.ldap.listener;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import javax.net.ssl.TrustManager;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import javax.net.ssl.SSLServerSocketFactory;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import javax.net.ssl.SSLSocketFactory;
import javax.net.SocketFactory;
import javax.net.ServerSocketFactory;
import java.net.InetAddress;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class InMemoryListenerConfig
{
    private final InetAddress listenAddress;
    private final int listenPort;
    private final ServerSocketFactory serverSocketFactory;
    private final SocketFactory clientSocketFactory;
    private final SSLSocketFactory startTLSSocketFactory;
    private final String listenerName;
    
    public InMemoryListenerConfig(final String listenerName, final InetAddress listenAddress, final int listenPort, final ServerSocketFactory serverSocketFactory, final SocketFactory clientSocketFactory, final SSLSocketFactory startTLSSocketFactory) throws LDAPException {
        if (listenerName == null || listenerName.isEmpty()) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_LISTENER_CFG_NO_NAME.get());
        }
        if (listenPort < 0 || listenPort > 65535) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_LISTENER_CFG_INVALID_PORT.get(listenPort));
        }
        this.listenerName = listenerName;
        this.listenAddress = listenAddress;
        this.listenPort = listenPort;
        this.serverSocketFactory = serverSocketFactory;
        this.clientSocketFactory = clientSocketFactory;
        this.startTLSSocketFactory = startTLSSocketFactory;
    }
    
    public static InMemoryListenerConfig createLDAPConfig(final String listenerName) throws LDAPException {
        return new InMemoryListenerConfig(listenerName, null, 0, null, null, null);
    }
    
    public static InMemoryListenerConfig createLDAPConfig(final String listenerName, final int listenPort) throws LDAPException {
        return new InMemoryListenerConfig(listenerName, null, listenPort, null, null, null);
    }
    
    public static InMemoryListenerConfig createLDAPConfig(final String listenerName, final InetAddress listenAddress, final int listenPort, final SSLSocketFactory startTLSSocketFactory) throws LDAPException {
        return new InMemoryListenerConfig(listenerName, listenAddress, listenPort, null, null, startTLSSocketFactory);
    }
    
    public static InMemoryListenerConfig createLDAPSConfig(final String listenerName, final SSLServerSocketFactory serverSocketFactory) throws LDAPException {
        return createLDAPSConfig(listenerName, null, 0, serverSocketFactory, null);
    }
    
    public static InMemoryListenerConfig createLDAPSConfig(final String listenerName, final int listenPort, final SSLServerSocketFactory serverSocketFactory) throws LDAPException {
        return createLDAPSConfig(listenerName, null, listenPort, serverSocketFactory, null);
    }
    
    public static InMemoryListenerConfig createLDAPSConfig(final String listenerName, final InetAddress listenAddress, final int listenPort, final SSLServerSocketFactory serverSocketFactory, final SSLSocketFactory clientSocketFactory) throws LDAPException {
        if (serverSocketFactory == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_LISTENER_CFG_NO_SSL_SERVER_SOCKET_FACTORY.get());
        }
        if (clientSocketFactory == null) {
            try {
                final SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
                final SSLSocketFactory clientFactory = sslUtil.createSSLSocketFactory();
                return new InMemoryListenerConfig(listenerName, listenAddress, listenPort, serverSocketFactory, clientFactory, null);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_LISTENER_CFG_COULD_NOT_CREATE_SSL_SOCKET_FACTORY.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
        final SSLSocketFactory clientFactory = clientSocketFactory;
        return new InMemoryListenerConfig(listenerName, listenAddress, listenPort, serverSocketFactory, clientFactory, null);
    }
    
    public String getListenerName() {
        return this.listenerName;
    }
    
    public InetAddress getListenAddress() {
        return this.listenAddress;
    }
    
    public int getListenPort() {
        return this.listenPort;
    }
    
    public ServerSocketFactory getServerSocketFactory() {
        return this.serverSocketFactory;
    }
    
    public SocketFactory getClientSocketFactory() {
        return this.clientSocketFactory;
    }
    
    public SSLSocketFactory getStartTLSSocketFactory() {
        return this.startTLSSocketFactory;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("InMemoryListenerConfig(name='");
        buffer.append(this.listenerName);
        buffer.append('\'');
        if (this.listenAddress != null) {
            buffer.append(", listenAddress='");
            buffer.append(this.listenAddress.getHostAddress());
            buffer.append('\'');
        }
        buffer.append(", listenPort=");
        buffer.append(this.listenPort);
        if (this.serverSocketFactory != null) {
            buffer.append(", serverSocketFactoryClass='");
            buffer.append(this.serverSocketFactory.getClass().getName());
            buffer.append('\'');
        }
        if (this.clientSocketFactory != null) {
            buffer.append(", clientSocketFactoryClass='");
            buffer.append(this.clientSocketFactory.getClass().getName());
            buffer.append('\'');
        }
        if (this.startTLSSocketFactory != null) {
            buffer.append(", startTLSSocketFactoryClass='");
            buffer.append(this.startTLSSocketFactory.getClass().getName());
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
