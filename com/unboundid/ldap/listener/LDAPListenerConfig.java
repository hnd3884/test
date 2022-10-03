package com.unboundid.ldap.listener;

import com.unboundid.util.Validator;
import javax.net.ServerSocketFactory;
import java.net.InetAddress;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDAPListenerConfig
{
    private boolean useKeepAlive;
    private boolean useLinger;
    private boolean useReuseAddress;
    private boolean useTCPNoDelay;
    private InetAddress listenAddress;
    private int lingerTimeout;
    private int listenPort;
    private int maxConnections;
    private int receiveBufferSize;
    private int sendBufferSize;
    private LDAPListenerExceptionHandler exceptionHandler;
    private LDAPListenerRequestHandler requestHandler;
    private ServerSocketFactory serverSocketFactory;
    
    public LDAPListenerConfig(final int listenPort, final LDAPListenerRequestHandler requestHandler) {
        Validator.ensureTrue(listenPort >= 0 && listenPort <= 65535);
        Validator.ensureNotNull(requestHandler);
        this.listenPort = listenPort;
        this.requestHandler = requestHandler;
        this.useKeepAlive = true;
        this.useLinger = true;
        this.useReuseAddress = true;
        this.useTCPNoDelay = true;
        this.lingerTimeout = 5;
        this.listenAddress = null;
        this.maxConnections = 0;
        this.receiveBufferSize = 0;
        this.sendBufferSize = 0;
        this.exceptionHandler = null;
        this.serverSocketFactory = ServerSocketFactory.getDefault();
    }
    
    public int getListenPort() {
        return this.listenPort;
    }
    
    public void setListenPort(final int listenPort) {
        Validator.ensureTrue(listenPort >= 0 && listenPort <= 65535);
        this.listenPort = listenPort;
    }
    
    public LDAPListenerRequestHandler getRequestHandler() {
        return this.requestHandler;
    }
    
    public void setRequestHandler(final LDAPListenerRequestHandler requestHandler) {
        Validator.ensureNotNull(requestHandler);
        this.requestHandler = requestHandler;
    }
    
    public boolean useKeepAlive() {
        return this.useKeepAlive;
    }
    
    public void setUseKeepAlive(final boolean useKeepAlive) {
        this.useKeepAlive = useKeepAlive;
    }
    
    public boolean useLinger() {
        return this.useLinger;
    }
    
    public void setUseLinger(final boolean useLinger) {
        this.useLinger = useLinger;
    }
    
    public boolean useReuseAddress() {
        return this.useReuseAddress;
    }
    
    public void setUseReuseAddress(final boolean useReuseAddress) {
        this.useReuseAddress = useReuseAddress;
    }
    
    public boolean useTCPNoDelay() {
        return this.useTCPNoDelay;
    }
    
    public void setUseTCPNoDelay(final boolean useTCPNoDelay) {
        this.useTCPNoDelay = useTCPNoDelay;
    }
    
    public InetAddress getListenAddress() {
        return this.listenAddress;
    }
    
    public void setListenAddress(final InetAddress listenAddress) {
        this.listenAddress = listenAddress;
    }
    
    public int getLingerTimeoutSeconds() {
        return this.lingerTimeout;
    }
    
    public void setLingerTimeoutSeconds(final int lingerTimeout) {
        Validator.ensureTrue(lingerTimeout >= 0 && lingerTimeout <= 65535);
        this.lingerTimeout = lingerTimeout;
    }
    
    public int getMaxConnections() {
        return this.maxConnections;
    }
    
    public void setMaxConnections(final int maxConnections) {
        if (maxConnections > 0) {
            this.maxConnections = maxConnections;
        }
        else {
            this.maxConnections = 0;
        }
    }
    
    public int getReceiveBufferSize() {
        return this.receiveBufferSize;
    }
    
    public void setReceiveBufferSize(final int receiveBufferSize) {
        if (receiveBufferSize > 0) {
            this.receiveBufferSize = receiveBufferSize;
        }
        else {
            this.receiveBufferSize = 0;
        }
    }
    
    public int getSendBufferSize() {
        return this.sendBufferSize;
    }
    
    public void setSendBufferSize(final int sendBufferSize) {
        if (sendBufferSize > 0) {
            this.sendBufferSize = sendBufferSize;
        }
        else {
            this.sendBufferSize = 0;
        }
    }
    
    public LDAPListenerExceptionHandler getExceptionHandler() {
        return this.exceptionHandler;
    }
    
    public void setExceptionHandler(final LDAPListenerExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
    
    public ServerSocketFactory getServerSocketFactory() {
        return this.serverSocketFactory;
    }
    
    public void setServerSocketFactory(final ServerSocketFactory serverSocketFactory) {
        if (serverSocketFactory == null) {
            this.serverSocketFactory = ServerSocketFactory.getDefault();
        }
        else {
            this.serverSocketFactory = serverSocketFactory;
        }
    }
    
    public LDAPListenerConfig duplicate() {
        final LDAPListenerConfig copy = new LDAPListenerConfig(this.listenPort, this.requestHandler);
        copy.useKeepAlive = this.useKeepAlive;
        copy.useLinger = this.useLinger;
        copy.useReuseAddress = this.useReuseAddress;
        copy.useTCPNoDelay = this.useTCPNoDelay;
        copy.listenAddress = this.listenAddress;
        copy.lingerTimeout = this.lingerTimeout;
        copy.maxConnections = this.maxConnections;
        copy.receiveBufferSize = this.receiveBufferSize;
        copy.sendBufferSize = this.sendBufferSize;
        copy.exceptionHandler = this.exceptionHandler;
        copy.serverSocketFactory = this.serverSocketFactory;
        return copy;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPListenerConfig(listenAddress=");
        if (this.listenAddress == null) {
            buffer.append("null");
        }
        else {
            buffer.append('\'');
            buffer.append(this.listenAddress.getHostAddress());
            buffer.append('\'');
        }
        buffer.append(", listenPort=");
        buffer.append(this.listenPort);
        buffer.append(", requestHandlerClass='");
        buffer.append(this.requestHandler.getClass().getName());
        buffer.append("', serverSocketFactoryClass='");
        buffer.append(this.serverSocketFactory.getClass().getName());
        buffer.append('\'');
        if (this.exceptionHandler != null) {
            buffer.append(", exceptionHandlerClass='");
            buffer.append(this.exceptionHandler.getClass().getName());
            buffer.append('\'');
        }
        buffer.append(", useKeepAlive=");
        buffer.append(this.useKeepAlive);
        buffer.append(", useTCPNoDelay=");
        buffer.append(this.useTCPNoDelay);
        if (this.useLinger) {
            buffer.append(", useLinger=true, lingerTimeout=");
            buffer.append(this.lingerTimeout);
        }
        else {
            buffer.append(", useLinger=false");
        }
        buffer.append(", maxConnections=");
        buffer.append(this.maxConnections);
        buffer.append(", useReuseAddress=");
        buffer.append(this.useReuseAddress);
        buffer.append(", receiveBufferSize=");
        buffer.append(this.receiveBufferSize);
        buffer.append(", sendBufferSize=");
        buffer.append(this.sendBufferSize);
        buffer.append(')');
    }
}
