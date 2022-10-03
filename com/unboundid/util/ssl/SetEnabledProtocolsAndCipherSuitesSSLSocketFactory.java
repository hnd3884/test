package com.unboundid.util.ssl;

import java.net.InetAddress;
import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;
import javax.net.ssl.SSLSocketFactory;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.MOSTLY_THREADSAFE)
final class SetEnabledProtocolsAndCipherSuitesSSLSocketFactory extends SSLSocketFactory
{
    private final Set<String> cipherSuites;
    private final Set<String> protocols;
    private final SSLSocketFactory delegateFactory;
    
    SetEnabledProtocolsAndCipherSuitesSSLSocketFactory(final SSLSocketFactory delegateFactory, final String defaultProtocol, final Set<String> cipherSuites) {
        this.delegateFactory = delegateFactory;
        this.cipherSuites = cipherSuites;
        if (defaultProtocol.equalsIgnoreCase("TLSv1.2")) {
            this.protocols = new HashSet<String>(Arrays.asList("TLSv1.2", "TLSv1.1", "TLSv1"));
        }
        else if (defaultProtocol.equalsIgnoreCase("TLSv1.1")) {
            this.protocols = new HashSet<String>(Arrays.asList("TLSv1.1", "TLSv1"));
        }
        else if (defaultProtocol.equalsIgnoreCase("TLSv1")) {
            this.protocols = new HashSet<String>(Collections.singletonList("TLSv1"));
        }
        else {
            this.protocols = Collections.emptySet();
        }
    }
    
    SetEnabledProtocolsAndCipherSuitesSSLSocketFactory(final SSLSocketFactory delegateFactory, final Set<String> protocols, final Set<String> cipherSuites) {
        this.delegateFactory = delegateFactory;
        this.protocols = protocols;
        this.cipherSuites = cipherSuites;
    }
    
    @Override
    public Socket createSocket() throws IOException {
        return new SetEnabledProtocolsAndCipherSuitesSocket(this.delegateFactory.createSocket(), this.protocols, this.cipherSuites);
    }
    
    @Override
    public Socket createSocket(final String host, final int port) throws IOException {
        final Socket createdSocket = this.delegateFactory.createSocket(host, port);
        SSLUtil.applyEnabledSSLProtocols(createdSocket, this.protocols);
        SSLUtil.applyEnabledSSLCipherSuites(createdSocket, this.cipherSuites);
        return createdSocket;
    }
    
    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localHost, final int localPort) throws IOException {
        final Socket createdSocket = this.delegateFactory.createSocket(host, port, localHost, localPort);
        SSLUtil.applyEnabledSSLProtocols(createdSocket, this.protocols);
        SSLUtil.applyEnabledSSLCipherSuites(createdSocket, this.cipherSuites);
        return createdSocket;
    }
    
    @Override
    public Socket createSocket(final InetAddress host, final int port) throws IOException {
        final Socket createdSocket = this.delegateFactory.createSocket(host, port);
        SSLUtil.applyEnabledSSLProtocols(createdSocket, this.protocols);
        SSLUtil.applyEnabledSSLCipherSuites(createdSocket, this.cipherSuites);
        return createdSocket;
    }
    
    @Override
    public Socket createSocket(final InetAddress host, final int port, final InetAddress localHost, final int localPort) throws IOException {
        final Socket createdSocket = this.delegateFactory.createSocket(host, port, localHost, localPort);
        SSLUtil.applyEnabledSSLProtocols(createdSocket, this.protocols);
        SSLUtil.applyEnabledSSLCipherSuites(createdSocket, this.cipherSuites);
        return createdSocket;
    }
    
    @Override
    public Socket createSocket(final Socket s, final String host, final int port, final boolean autoClose) throws IOException {
        final Socket createdSocket = this.delegateFactory.createSocket(s, host, port, autoClose);
        SSLUtil.applyEnabledSSLProtocols(createdSocket, this.protocols);
        SSLUtil.applyEnabledSSLCipherSuites(createdSocket, this.cipherSuites);
        return createdSocket;
    }
    
    @Override
    public String[] getDefaultCipherSuites() {
        return this.delegateFactory.getDefaultCipherSuites();
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return this.delegateFactory.getSupportedCipherSuites();
    }
}
