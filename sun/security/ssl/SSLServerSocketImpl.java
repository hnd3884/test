package sun.security.ssl;

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
        this.sslConfig.isClientMode = false;
    }
    
    SSLServerSocketImpl(final SSLContextImpl sslContext, final int n, final int n2) throws IOException {
        super(n, n2);
        this.sslContext = sslContext;
        this.sslConfig = new SSLConfiguration(sslContext, false);
        this.sslConfig.isClientMode = false;
    }
    
    SSLServerSocketImpl(final SSLContextImpl sslContext, final int n, final int n2, final InetAddress inetAddress) throws IOException {
        super(n, n2, inetAddress);
        this.sslContext = sslContext;
        this.sslConfig = new SSLConfiguration(sslContext, false);
    }
    
    @Override
    public synchronized String[] getEnabledCipherSuites() {
        return CipherSuite.namesOf(this.sslConfig.enabledCipherSuites);
    }
    
    @Override
    public synchronized void setEnabledCipherSuites(final String[] array) {
        this.sslConfig.enabledCipherSuites = CipherSuite.validValuesOf(array);
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
    public synchronized void setEnabledProtocols(final String[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Protocols cannot be null");
        }
        this.sslConfig.enabledProtocols = ProtocolVersion.namesOf(array);
    }
    
    @Override
    public synchronized void setNeedClientAuth(final boolean b) {
        this.sslConfig.clientAuthType = (b ? ClientAuthType.CLIENT_AUTH_REQUIRED : ClientAuthType.CLIENT_AUTH_NONE);
    }
    
    @Override
    public synchronized boolean getNeedClientAuth() {
        return this.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED;
    }
    
    @Override
    public synchronized void setWantClientAuth(final boolean b) {
        this.sslConfig.clientAuthType = (b ? ClientAuthType.CLIENT_AUTH_REQUESTED : ClientAuthType.CLIENT_AUTH_NONE);
    }
    
    @Override
    public synchronized boolean getWantClientAuth() {
        return this.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUESTED;
    }
    
    @Override
    public synchronized void setUseClientMode(final boolean b) {
        if (this.sslConfig.isClientMode != b) {
            if (this.sslContext.isDefaultProtocolVesions(this.sslConfig.enabledProtocols)) {
                this.sslConfig.enabledProtocols = this.sslContext.getDefaultProtocolVersions(!b);
            }
            if (this.sslContext.isDefaultCipherSuiteList(this.sslConfig.enabledCipherSuites)) {
                this.sslConfig.enabledCipherSuites = this.sslContext.getDefaultCipherSuites(!b);
            }
            this.sslConfig.toggleClientMode();
        }
    }
    
    @Override
    public synchronized boolean getUseClientMode() {
        return this.sslConfig.isClientMode;
    }
    
    @Override
    public synchronized void setEnableSessionCreation(final boolean enableSessionCreation) {
        this.sslConfig.enableSessionCreation = enableSessionCreation;
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
    public synchronized void setSSLParameters(final SSLParameters sslParameters) {
        this.sslConfig.setSSLParameters(sslParameters);
    }
    
    @Override
    public Socket accept() throws IOException {
        final SSLSocketImpl sslSocketImpl = new SSLSocketImpl(this.sslContext, this.sslConfig);
        this.implAccept(sslSocketImpl);
        sslSocketImpl.doneConnect();
        return sslSocketImpl;
    }
    
    @Override
    public String toString() {
        return "[SSL: " + super.toString() + "]";
    }
}
