package org.openjsse.sun.security.ssl;

import java.net.Socket;
import javax.net.ssl.SSLParameters;
import java.net.InetAddress;
import java.io.IOException;
import javax.net.ssl.SSLServerSocket;

final class SSLServerSocketImpl extends SSLServerSocket
{
    private final SSLContextImpl sslContext;
    private final SSLConfiguration sslConfig;
    
    SSLServerSocketImpl(final SSLContextImpl sslContext) throws IOException {
        this.sslContext = sslContext;
        this.sslConfig = new SSLConfiguration(sslContext, false);
    }
    
    SSLServerSocketImpl(final SSLContextImpl sslContext, final int port, final int backlog) throws IOException {
        super(port, backlog);
        this.sslContext = sslContext;
        this.sslConfig = new SSLConfiguration(sslContext, false);
    }
    
    SSLServerSocketImpl(final SSLContextImpl sslContext, final int port, final int backlog, final InetAddress address) throws IOException {
        super(port, backlog, address);
        this.sslContext = sslContext;
        this.sslConfig = new SSLConfiguration(sslContext, false);
    }
    
    @Override
    public synchronized String[] getEnabledCipherSuites() {
        return CipherSuite.namesOf(this.sslConfig.enabledCipherSuites);
    }
    
    @Override
    public synchronized void setEnabledCipherSuites(final String[] suites) {
        this.sslConfig.enabledCipherSuites = CipherSuite.validValuesOf(suites);
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return CipherSuite.namesOf(this.sslContext.getSupportedCipherSuites());
    }
    
    @Override
    public String[] getSupportedProtocols() {
        return ProtocolVersion.toStringArray(this.sslContext.getSupportedProtocolVersions());
    }
    
    @Override
    public synchronized String[] getEnabledProtocols() {
        return ProtocolVersion.toStringArray(this.sslConfig.enabledProtocols);
    }
    
    @Override
    public synchronized void setEnabledProtocols(final String[] protocols) {
        if (protocols == null) {
            throw new IllegalArgumentException("Protocols cannot be null");
        }
        this.sslConfig.enabledProtocols = ProtocolVersion.namesOf(protocols);
    }
    
    @Override
    public synchronized void setNeedClientAuth(final boolean need) {
        this.sslConfig.clientAuthType = (need ? ClientAuthType.CLIENT_AUTH_REQUIRED : ClientAuthType.CLIENT_AUTH_NONE);
    }
    
    @Override
    public synchronized boolean getNeedClientAuth() {
        return this.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED;
    }
    
    @Override
    public synchronized void setWantClientAuth(final boolean want) {
        this.sslConfig.clientAuthType = (want ? ClientAuthType.CLIENT_AUTH_REQUESTED : ClientAuthType.CLIENT_AUTH_NONE);
    }
    
    @Override
    public synchronized boolean getWantClientAuth() {
        return this.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUESTED;
    }
    
    @Override
    public synchronized void setUseClientMode(final boolean useClientMode) {
        if (this.sslConfig.isClientMode != useClientMode) {
            if (this.sslContext.isDefaultProtocolVesions(this.sslConfig.enabledProtocols)) {
                this.sslConfig.enabledProtocols = this.sslContext.getDefaultProtocolVersions(!useClientMode);
            }
            if (this.sslContext.isDefaultCipherSuiteList(this.sslConfig.enabledCipherSuites)) {
                this.sslConfig.enabledCipherSuites = this.sslContext.getDefaultCipherSuites(!useClientMode);
            }
            this.sslConfig.toggleClientMode();
        }
    }
    
    @Override
    public synchronized boolean getUseClientMode() {
        return this.sslConfig.isClientMode;
    }
    
    @Override
    public synchronized void setEnableSessionCreation(final boolean flag) {
        this.sslConfig.enableSessionCreation = flag;
    }
    
    @Override
    public synchronized boolean getEnableSessionCreation() {
        return this.sslConfig.enableSessionCreation;
    }
    
    @Override
    public synchronized SSLParameters getSSLParameters() {
        return this.sslConfig.getSSLParameters();
    }
    
    @Override
    public synchronized void setSSLParameters(final SSLParameters params) {
        this.sslConfig.setSSLParameters(params);
    }
    
    @Override
    public Socket accept() throws IOException {
        final SSLSocketImpl s = new SSLSocketImpl(this.sslContext, this.sslConfig);
        this.implAccept(s);
        s.doneConnect();
        return s;
    }
    
    @Override
    public String toString() {
        return "[SSL: " + super.toString() + "]";
    }
}
