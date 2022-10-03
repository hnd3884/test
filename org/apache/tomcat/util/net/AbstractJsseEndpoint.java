package org.apache.tomcat.util.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.nio.channels.NetworkChannel;
import org.apache.tomcat.util.net.openssl.OpenSSLImplementation;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.net.ssl.SSLParameters;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.tomcat.util.compat.JreCompat;
import javax.net.ssl.SSLEngine;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import java.util.List;
import java.util.Iterator;

public abstract class AbstractJsseEndpoint<S> extends AbstractEndpoint<S>
{
    private String sslImplementationName;
    private int sniParseLimit;
    private SSLImplementation sslImplementation;
    
    public AbstractJsseEndpoint() {
        this.sslImplementationName = null;
        this.sniParseLimit = 65536;
        this.sslImplementation = null;
    }
    
    public String getSslImplementationName() {
        return this.sslImplementationName;
    }
    
    public void setSslImplementationName(final String s) {
        this.sslImplementationName = s;
    }
    
    public SSLImplementation getSslImplementation() {
        return this.sslImplementation;
    }
    
    public int getSniParseLimit() {
        return this.sniParseLimit;
    }
    
    public void setSniParseLimit(final int sniParseLimit) {
        this.sniParseLimit = sniParseLimit;
    }
    
    protected void initialiseSsl() throws Exception {
        if (this.isSSLEnabled()) {
            this.sslImplementation = SSLImplementation.getInstance(this.getSslImplementationName());
            for (final SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
                this.createSSLContext(sslHostConfig);
            }
            if (this.sslHostConfigs.get(this.getDefaultSSLHostConfigName()) == null) {
                throw new IllegalArgumentException(AbstractJsseEndpoint.sm.getString("endpoint.noSslHostConfig", new Object[] { this.getDefaultSSLHostConfigName(), this.getName() }));
            }
        }
    }
    
    @Override
    protected void createSSLContext(final SSLHostConfig sslHostConfig) throws IllegalArgumentException {
        boolean firstCertificate = true;
        for (final SSLHostConfigCertificate certificate : sslHostConfig.getCertificates(true)) {
            final SSLUtil sslUtil = this.sslImplementation.getSSLUtil(certificate);
            if (firstCertificate) {
                firstCertificate = false;
                sslHostConfig.setEnabledProtocols(sslUtil.getEnabledProtocols());
                sslHostConfig.setEnabledCiphers(sslUtil.getEnabledCiphers());
            }
            SSLContext sslContext;
            try {
                sslContext = sslUtil.createSSLContext(this.negotiableProtocols);
            }
            catch (final Exception e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            certificate.setSslContext(sslContext);
        }
    }
    
    protected SSLEngine createSSLEngine(final String sniHostName, final List<Cipher> clientRequestedCiphers, final List<String> clientRequestedApplicationProtocols) {
        final SSLHostConfig sslHostConfig = this.getSSLHostConfig(sniHostName);
        final SSLHostConfigCertificate certificate = this.selectCertificate(sslHostConfig, clientRequestedCiphers);
        final SSLContext sslContext = certificate.getSslContext();
        if (sslContext == null) {
            throw new IllegalStateException(AbstractJsseEndpoint.sm.getString("endpoint.jsse.noSslContext", new Object[] { sniHostName }));
        }
        final SSLEngine engine = sslContext.createSSLEngine();
        engine.setUseClientMode(false);
        engine.setEnabledCipherSuites(sslHostConfig.getEnabledCiphers());
        engine.setEnabledProtocols(sslHostConfig.getEnabledProtocols());
        final SSLParameters sslParameters = engine.getSSLParameters();
        final String honorCipherOrderStr = sslHostConfig.getHonorCipherOrder();
        if (honorCipherOrderStr != null) {
            final boolean honorCipherOrder = Boolean.parseBoolean(honorCipherOrderStr);
            JreCompat.getInstance().setUseServerCipherSuitesOrder(sslParameters, honorCipherOrder);
        }
        if (JreCompat.isAlpnSupported() && clientRequestedApplicationProtocols != null && clientRequestedApplicationProtocols.size() > 0 && this.negotiableProtocols.size() > 0) {
            final List<String> commonProtocols = new ArrayList<String>(this.negotiableProtocols);
            commonProtocols.retainAll(clientRequestedApplicationProtocols);
            if (commonProtocols.size() > 0) {
                final String[] commonProtocolsArray = commonProtocols.toArray(new String[0]);
                JreCompat.getInstance().setApplicationProtocols(sslParameters, commonProtocolsArray);
            }
        }
        switch (sslHostConfig.getCertificateVerification()) {
            case NONE: {
                sslParameters.setNeedClientAuth(false);
                sslParameters.setWantClientAuth(false);
                break;
            }
            case OPTIONAL:
            case OPTIONAL_NO_CA: {
                sslParameters.setWantClientAuth(true);
                break;
            }
            case REQUIRED: {
                sslParameters.setNeedClientAuth(true);
                break;
            }
        }
        engine.setSSLParameters(sslParameters);
        return engine;
    }
    
    private SSLHostConfigCertificate selectCertificate(final SSLHostConfig sslHostConfig, final List<Cipher> clientCiphers) {
        final Set<SSLHostConfigCertificate> certificates = sslHostConfig.getCertificates(true);
        if (certificates.size() == 1) {
            return certificates.iterator().next();
        }
        final LinkedHashSet<Cipher> serverCiphers = sslHostConfig.getCipherList();
        final List<Cipher> candidateCiphers = new ArrayList<Cipher>();
        if (Boolean.parseBoolean(sslHostConfig.getHonorCipherOrder())) {
            candidateCiphers.addAll(serverCiphers);
            candidateCiphers.retainAll(clientCiphers);
        }
        else {
            candidateCiphers.addAll(clientCiphers);
            candidateCiphers.retainAll(serverCiphers);
        }
        for (final Cipher candidate : candidateCiphers) {
            for (final SSLHostConfigCertificate certificate : certificates) {
                if (certificate.getType().isCompatibleWith(candidate.getAu())) {
                    return certificate;
                }
            }
        }
        return certificates.iterator().next();
    }
    
    @Override
    public boolean isAlpnSupported() {
        if (!this.isSSLEnabled()) {
            return false;
        }
        SSLImplementation sslImplementation;
        try {
            sslImplementation = SSLImplementation.getInstance(this.getSslImplementationName());
        }
        catch (final ClassNotFoundException e) {
            return false;
        }
        return sslImplementation.isAlpnSupported();
    }
    
    @Override
    public void init() throws Exception {
        this.testServerCipherSuitesOrderSupport();
        super.init();
    }
    
    private void testServerCipherSuitesOrderSupport() {
        if (!JreCompat.isJre8Available() && !OpenSSLImplementation.class.getName().equals(this.getSslImplementationName())) {
            for (final SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
                if (sslHostConfig.getHonorCipherOrder() != null) {
                    throw new UnsupportedOperationException(AbstractJsseEndpoint.sm.getString("endpoint.jsse.cannotHonorServerCipherOrder"));
                }
            }
        }
    }
    
    @Override
    public void unbind() throws Exception {
        for (final SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
            for (final SSLHostConfigCertificate certificate : sslHostConfig.getCertificates(true)) {
                certificate.setSslContext(null);
            }
        }
    }
    
    protected abstract NetworkChannel getServerSocket();
    
    @Override
    protected final InetSocketAddress getLocalAddress() throws IOException {
        final NetworkChannel serverSock = this.getServerSocket();
        if (serverSock == null) {
            return null;
        }
        final SocketAddress sa = serverSock.getLocalAddress();
        if (sa instanceof InetSocketAddress) {
            return (InetSocketAddress)sa;
        }
        return null;
    }
}
