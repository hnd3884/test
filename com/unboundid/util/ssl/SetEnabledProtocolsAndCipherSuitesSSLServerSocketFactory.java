package com.unboundid.util.ssl;

import java.net.InetAddress;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;
import javax.net.ssl.SSLServerSocketFactory;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.MOSTLY_THREADSAFE)
final class SetEnabledProtocolsAndCipherSuitesSSLServerSocketFactory extends SSLServerSocketFactory
{
    private final Set<String> cipherSuites;
    private final Set<String> protocols;
    private final SSLServerSocketFactory delegateFactory;
    
    SetEnabledProtocolsAndCipherSuitesSSLServerSocketFactory(final SSLServerSocketFactory delegateFactory, final String defaultProtocol, final Set<String> cipherSuites) {
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
    
    SetEnabledProtocolsAndCipherSuitesSSLServerSocketFactory(final SSLServerSocketFactory delegateFactory, final Set<String> protocols, final Set<String> cipherSuites) {
        this.delegateFactory = delegateFactory;
        this.protocols = protocols;
        this.cipherSuites = cipherSuites;
    }
    
    @Override
    public ServerSocket createServerSocket() throws IOException {
        final ServerSocket serverSocket = this.delegateFactory.createServerSocket();
        SSLUtil.applyEnabledSSLProtocols(serverSocket, this.protocols);
        SSLUtil.applyEnabledSSLCipherSuites(serverSocket, this.cipherSuites);
        return serverSocket;
    }
    
    @Override
    public ServerSocket createServerSocket(final int port) throws IOException {
        final ServerSocket serverSocket = this.delegateFactory.createServerSocket(port);
        SSLUtil.applyEnabledSSLProtocols(serverSocket, this.protocols);
        SSLUtil.applyEnabledSSLCipherSuites(serverSocket, this.cipherSuites);
        return serverSocket;
    }
    
    @Override
    public ServerSocket createServerSocket(final int port, final int backlog) throws IOException {
        final ServerSocket serverSocket = this.delegateFactory.createServerSocket(port, backlog);
        SSLUtil.applyEnabledSSLProtocols(serverSocket, this.protocols);
        SSLUtil.applyEnabledSSLCipherSuites(serverSocket, this.cipherSuites);
        return serverSocket;
    }
    
    @Override
    public ServerSocket createServerSocket(final int port, final int backlog, final InetAddress ifAddress) throws IOException {
        final ServerSocket serverSocket = this.delegateFactory.createServerSocket(port, backlog, ifAddress);
        SSLUtil.applyEnabledSSLProtocols(serverSocket, this.protocols);
        SSLUtil.applyEnabledSSLCipherSuites(serverSocket, this.cipherSuites);
        return serverSocket;
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
