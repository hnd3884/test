package org.openjsse.sun.security.ssl;

import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.Timestamp;
import sun.security.provider.certpath.AlgorithmChecker;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.security.AlgorithmConstraints;
import javax.net.ssl.SSLSession;
import javax.net.ssl.ExtendedSSLSession;
import org.openjsse.javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

final class AbstractTrustManagerWrapper extends X509ExtendedTrustManager implements X509TrustManager
{
    private final X509TrustManager tm;
    
    AbstractTrustManagerWrapper(final X509TrustManager tm) {
        this.tm = tm;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.tm.checkClientTrusted(chain, authType);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.tm.checkServerTrusted(chain, authType);
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.tm.getAcceptedIssuers();
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket) throws CertificateException {
        this.tm.checkClientTrusted(chain, authType);
        this.checkAdditionalTrust(chain, authType, socket, true);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket) throws CertificateException {
        this.tm.checkServerTrusted(chain, authType);
        this.checkAdditionalTrust(chain, authType, socket, false);
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) throws CertificateException {
        this.tm.checkClientTrusted(chain, authType);
        this.checkAdditionalTrust(chain, authType, engine, true);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) throws CertificateException {
        this.tm.checkServerTrusted(chain, authType);
        this.checkAdditionalTrust(chain, authType, engine, false);
    }
    
    private void checkAdditionalTrust(final X509Certificate[] chain, final String authType, final Socket socket, final boolean checkClientTrusted) throws CertificateException {
        if (socket != null && socket.isConnected() && socket instanceof SSLSocket) {
            final SSLSocket sslSocket = (SSLSocket)socket;
            final SSLSession session = sslSocket.getHandshakeSession();
            if (session == null) {
                throw new CertificateException("No handshake session");
            }
            final String identityAlg = sslSocket.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (identityAlg != null && identityAlg.length() != 0) {
                X509TrustManagerImpl.checkIdentity(session, chain, identityAlg, checkClientTrusted);
            }
            AlgorithmConstraints constraints;
            if (ProtocolVersion.useTLS12PlusSpec(session.getProtocol())) {
                if (session instanceof ExtendedSSLSession) {
                    final ExtendedSSLSession extSession = (ExtendedSSLSession)session;
                    final String[] peerSupportedSignAlgs = extSession.getLocalSupportedSignatureAlgorithms();
                    constraints = new SSLAlgorithmConstraints(sslSocket, peerSupportedSignAlgs, true);
                }
                else {
                    constraints = new SSLAlgorithmConstraints(sslSocket, true);
                }
            }
            else {
                constraints = new SSLAlgorithmConstraints(sslSocket, true);
            }
            this.checkAlgorithmConstraints(chain, constraints, checkClientTrusted);
        }
    }
    
    private void checkAdditionalTrust(final X509Certificate[] chain, final String authType, final SSLEngine engine, final boolean checkClientTrusted) throws CertificateException {
        if (engine != null) {
            final SSLSession session = engine.getHandshakeSession();
            if (session == null) {
                throw new CertificateException("No handshake session");
            }
            final String identityAlg = engine.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (identityAlg != null && identityAlg.length() != 0) {
                X509TrustManagerImpl.checkIdentity(session, chain, identityAlg, checkClientTrusted);
            }
            AlgorithmConstraints constraints;
            if (ProtocolVersion.useTLS12PlusSpec(session.getProtocol())) {
                if (session instanceof ExtendedSSLSession) {
                    final ExtendedSSLSession extSession = (ExtendedSSLSession)session;
                    final String[] peerSupportedSignAlgs = extSession.getLocalSupportedSignatureAlgorithms();
                    constraints = new SSLAlgorithmConstraints((org.openjsse.javax.net.ssl.SSLEngine)engine, peerSupportedSignAlgs, true);
                }
                else {
                    constraints = new SSLAlgorithmConstraints((org.openjsse.javax.net.ssl.SSLEngine)engine, true);
                }
            }
            else {
                constraints = new SSLAlgorithmConstraints((org.openjsse.javax.net.ssl.SSLEngine)engine, true);
            }
            this.checkAlgorithmConstraints(chain, constraints, checkClientTrusted);
        }
    }
    
    private void checkAlgorithmConstraints(final X509Certificate[] chain, final AlgorithmConstraints constraints, final boolean checkClientTrusted) throws CertificateException {
        try {
            int checkedLength = chain.length - 1;
            final Collection<X509Certificate> trustedCerts = new HashSet<X509Certificate>();
            final X509Certificate[] certs = this.tm.getAcceptedIssuers();
            if (certs != null && certs.length > 0) {
                Collections.addAll(trustedCerts, certs);
            }
            if (trustedCerts.contains(chain[checkedLength])) {
                --checkedLength;
            }
            if (checkedLength >= 0) {
                final AlgorithmChecker checker = new AlgorithmChecker(constraints, (Timestamp)null, checkClientTrusted ? "tls client" : "tls server");
                checker.init(false);
                for (int i = checkedLength; i >= 0; --i) {
                    final X509Certificate cert = chain[i];
                    checker.check(cert, (Collection<String>)Collections.emptySet());
                }
            }
        }
        catch (final CertPathValidatorException cpve) {
            throw new CertificateException("Certificates do not conform to algorithm constraints", cpve);
        }
    }
}
