package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.net.Socket;
import javax.net.ssl.SSLParameters;
import java.net.InetAddress;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.util.Collections;
import javax.net.ssl.SNIMatcher;
import java.util.Collection;
import java.security.AlgorithmConstraints;
import javax.net.ssl.SSLServerSocket;

final class SSLServerSocketImpl extends SSLServerSocket
{
    private SSLContextImpl sslContext;
    private byte doClientAuth;
    private boolean useServerMode;
    private boolean enableSessionCreation;
    private CipherSuiteList enabledCipherSuites;
    private ProtocolList enabledProtocols;
    private String identificationProtocol;
    private AlgorithmConstraints algorithmConstraints;
    Collection<SNIMatcher> sniMatchers;
    String[] applicationProtocols;
    private boolean preferLocalCipherSuites;
    
    SSLServerSocketImpl(final int port, final int backlog, final SSLContextImpl context) throws IOException, SSLException {
        super(port, backlog);
        this.doClientAuth = 0;
        this.useServerMode = true;
        this.enableSessionCreation = true;
        this.enabledCipherSuites = null;
        this.enabledProtocols = null;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.applicationProtocols = new String[0];
        this.preferLocalCipherSuites = false;
        this.initServer(context);
    }
    
    SSLServerSocketImpl(final int port, final int backlog, final InetAddress address, final SSLContextImpl context) throws IOException {
        super(port, backlog, address);
        this.doClientAuth = 0;
        this.useServerMode = true;
        this.enableSessionCreation = true;
        this.enabledCipherSuites = null;
        this.enabledProtocols = null;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.applicationProtocols = new String[0];
        this.preferLocalCipherSuites = false;
        this.initServer(context);
    }
    
    SSLServerSocketImpl(final SSLContextImpl context) throws IOException {
        this.doClientAuth = 0;
        this.useServerMode = true;
        this.enableSessionCreation = true;
        this.enabledCipherSuites = null;
        this.enabledProtocols = null;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.applicationProtocols = new String[0];
        this.preferLocalCipherSuites = false;
        this.initServer(context);
    }
    
    private void initServer(final SSLContextImpl context) throws SSLException {
        if (context == null) {
            throw new SSLException("No Authentication context given");
        }
        this.sslContext = context;
        this.enabledCipherSuites = this.sslContext.getDefaultCipherSuiteList(true);
        this.enabledProtocols = this.sslContext.getDefaultProtocolList(true);
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return this.sslContext.getSupportedCipherSuiteList().toStringArray();
    }
    
    @Override
    public synchronized String[] getEnabledCipherSuites() {
        return this.enabledCipherSuites.toStringArray();
    }
    
    @Override
    public synchronized void setEnabledCipherSuites(final String[] suites) {
        this.enabledCipherSuites = new CipherSuiteList(suites);
    }
    
    @Override
    public String[] getSupportedProtocols() {
        return this.sslContext.getSuportedProtocolList().toStringArray();
    }
    
    @Override
    public synchronized void setEnabledProtocols(final String[] protocols) {
        this.enabledProtocols = new ProtocolList(protocols);
    }
    
    @Override
    public synchronized String[] getEnabledProtocols() {
        return this.enabledProtocols.toStringArray();
    }
    
    @Override
    public void setNeedClientAuth(final boolean flag) {
        this.doClientAuth = (byte)(flag ? 2 : 0);
    }
    
    @Override
    public boolean getNeedClientAuth() {
        return this.doClientAuth == 2;
    }
    
    @Override
    public void setWantClientAuth(final boolean flag) {
        this.doClientAuth = (byte)(flag ? 1 : 0);
    }
    
    @Override
    public boolean getWantClientAuth() {
        return this.doClientAuth == 1;
    }
    
    @Override
    public void setUseClientMode(final boolean flag) {
        if (this.useServerMode != !flag && this.sslContext.isDefaultProtocolList(this.enabledProtocols)) {
            this.enabledProtocols = this.sslContext.getDefaultProtocolList(!flag);
        }
        this.useServerMode = !flag;
    }
    
    @Override
    public boolean getUseClientMode() {
        return !this.useServerMode;
    }
    
    @Override
    public void setEnableSessionCreation(final boolean flag) {
        this.enableSessionCreation = flag;
    }
    
    @Override
    public boolean getEnableSessionCreation() {
        return this.enableSessionCreation;
    }
    
    @Override
    public synchronized SSLParameters getSSLParameters() {
        final SSLParameters params = super.getSSLParameters();
        params.setEndpointIdentificationAlgorithm(this.identificationProtocol);
        params.setAlgorithmConstraints(this.algorithmConstraints);
        params.setSNIMatchers(this.sniMatchers);
        params.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
        params.setApplicationProtocols(this.applicationProtocols);
        return params;
    }
    
    @Override
    public synchronized void setSSLParameters(final SSLParameters params) {
        super.setSSLParameters(params);
        this.identificationProtocol = params.getEndpointIdentificationAlgorithm();
        this.algorithmConstraints = params.getAlgorithmConstraints();
        this.preferLocalCipherSuites = params.getUseCipherSuitesOrder();
        final Collection<SNIMatcher> matchers = params.getSNIMatchers();
        if (matchers != null) {
            this.sniMatchers = params.getSNIMatchers();
        }
        this.applicationProtocols = params.getApplicationProtocols();
    }
    
    @Override
    public Socket accept() throws IOException {
        final SSLSocketImpl s = new SSLSocketImpl(this.sslContext, this.useServerMode, this.enabledCipherSuites, this.doClientAuth, this.enableSessionCreation, this.enabledProtocols, this.identificationProtocol, this.algorithmConstraints, this.sniMatchers, this.preferLocalCipherSuites, this.applicationProtocols);
        this.implAccept(s);
        s.doneConnect();
        return s;
    }
    
    @Override
    public String toString() {
        return "[SSL: " + super.toString() + "]";
    }
}
