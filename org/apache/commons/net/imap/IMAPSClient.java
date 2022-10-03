package org.apache.commons.net.imap;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLHandshakeException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Reader;
import org.apache.commons.net.io.CRLFLineReader;
import java.io.InputStreamReader;
import org.apache.commons.net.util.SSLSocketUtils;
import javax.net.ssl.SSLSocket;
import org.apache.commons.net.util.SSLContextUtils;
import java.io.IOException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;

public class IMAPSClient extends IMAPClient
{
    public static final int DEFAULT_IMAPS_PORT = 993;
    public static final String DEFAULT_PROTOCOL = "TLS";
    private final boolean isImplicit;
    private final String protocol;
    private SSLContext context;
    private String[] suites;
    private String[] protocols;
    private TrustManager trustManager;
    private KeyManager keyManager;
    private HostnameVerifier hostnameVerifier;
    private boolean tlsEndpointChecking;
    
    public IMAPSClient() {
        this("TLS", false);
    }
    
    public IMAPSClient(final boolean implicit) {
        this("TLS", implicit);
    }
    
    public IMAPSClient(final String proto) {
        this(proto, false);
    }
    
    public IMAPSClient(final String proto, final boolean implicit) {
        this(proto, implicit, null);
    }
    
    public IMAPSClient(final String proto, final boolean implicit, final SSLContext ctx) {
        this.context = null;
        this.suites = null;
        this.protocols = null;
        this.trustManager = null;
        this.keyManager = null;
        this.hostnameVerifier = null;
        this.setDefaultPort(993);
        this.protocol = proto;
        this.isImplicit = implicit;
        this.context = ctx;
    }
    
    public IMAPSClient(final boolean implicit, final SSLContext ctx) {
        this("TLS", implicit, ctx);
    }
    
    public IMAPSClient(final SSLContext context) {
        this(false, context);
    }
    
    @Override
    protected void _connectAction_() throws IOException {
        if (this.isImplicit) {
            this.performSSLNegotiation();
        }
        super._connectAction_();
    }
    
    private void initSSLContext() throws IOException {
        if (this.context == null) {
            this.context = SSLContextUtils.createSSLContext(this.protocol, this.getKeyManager(), this.getTrustManager());
        }
    }
    
    private void performSSLNegotiation() throws IOException {
        this.initSSLContext();
        final SSLSocketFactory ssf = this.context.getSocketFactory();
        final String host = (this._hostname_ != null) ? this._hostname_ : this.getRemoteAddress().getHostAddress();
        final int port = this.getRemotePort();
        final SSLSocket socket = (SSLSocket)ssf.createSocket(this._socket_, host, port, true);
        socket.setEnableSessionCreation(true);
        socket.setUseClientMode(true);
        if (this.tlsEndpointChecking) {
            SSLSocketUtils.enableEndpointNameVerification(socket);
        }
        if (this.protocols != null) {
            socket.setEnabledProtocols(this.protocols);
        }
        if (this.suites != null) {
            socket.setEnabledCipherSuites(this.suites);
        }
        socket.startHandshake();
        this._socket_ = socket;
        this._input_ = socket.getInputStream();
        this._output_ = socket.getOutputStream();
        this._reader = new CRLFLineReader(new InputStreamReader(this._input_, "ISO-8859-1"));
        this.__writer = new BufferedWriter(new OutputStreamWriter(this._output_, "ISO-8859-1"));
        if (this.hostnameVerifier != null && !this.hostnameVerifier.verify(host, socket.getSession())) {
            throw new SSLHandshakeException("Hostname doesn't match certificate");
        }
    }
    
    private KeyManager getKeyManager() {
        return this.keyManager;
    }
    
    public void setKeyManager(final KeyManager newKeyManager) {
        this.keyManager = newKeyManager;
    }
    
    public void setEnabledCipherSuites(final String[] cipherSuites) {
        System.arraycopy(cipherSuites, 0, this.suites = new String[cipherSuites.length], 0, cipherSuites.length);
    }
    
    public String[] getEnabledCipherSuites() {
        if (this._socket_ instanceof SSLSocket) {
            return ((SSLSocket)this._socket_).getEnabledCipherSuites();
        }
        return null;
    }
    
    public void setEnabledProtocols(final String[] protocolVersions) {
        System.arraycopy(protocolVersions, 0, this.protocols = new String[protocolVersions.length], 0, protocolVersions.length);
    }
    
    public String[] getEnabledProtocols() {
        if (this._socket_ instanceof SSLSocket) {
            return ((SSLSocket)this._socket_).getEnabledProtocols();
        }
        return null;
    }
    
    public boolean execTLS() throws SSLException, IOException {
        if (this.sendCommand(IMAPCommand.getCommand(IMAPCommand.STARTTLS)) != 0) {
            return false;
        }
        this.performSSLNegotiation();
        return true;
    }
    
    public TrustManager getTrustManager() {
        return this.trustManager;
    }
    
    public void setTrustManager(final TrustManager newTrustManager) {
        this.trustManager = newTrustManager;
    }
    
    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }
    
    public void setHostnameVerifier(final HostnameVerifier newHostnameVerifier) {
        this.hostnameVerifier = newHostnameVerifier;
    }
    
    public boolean isEndpointCheckingEnabled() {
        return this.tlsEndpointChecking;
    }
    
    public void setEndpointCheckingEnabled(final boolean enable) {
        this.tlsEndpointChecking = enable;
    }
}
