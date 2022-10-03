package sun.security.ssl;

import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.Timestamp;
import sun.security.provider.certpath.AlgorithmChecker;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.net.ssl.SSLSession;
import java.security.AlgorithmConstraints;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SSLSocket;
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
    public void checkClientTrusted(final X509Certificate[] array, final String s) throws CertificateException {
        this.tm.checkClientTrusted(array, s);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] array, final String s) throws CertificateException {
        this.tm.checkServerTrusted(array, s);
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.tm.getAcceptedIssuers();
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] array, final String s, final Socket socket) throws CertificateException {
        this.tm.checkClientTrusted(array, s);
        this.checkAdditionalTrust(array, s, socket, true);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] array, final String s, final Socket socket) throws CertificateException {
        this.tm.checkServerTrusted(array, s);
        this.checkAdditionalTrust(array, s, socket, false);
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] array, final String s, final SSLEngine sslEngine) throws CertificateException {
        this.tm.checkClientTrusted(array, s);
        this.checkAdditionalTrust(array, s, sslEngine, true);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] array, final String s, final SSLEngine sslEngine) throws CertificateException {
        this.tm.checkServerTrusted(array, s);
        this.checkAdditionalTrust(array, s, sslEngine, false);
    }
    
    private void checkAdditionalTrust(final X509Certificate[] array, final String s, final Socket socket, final boolean b) throws CertificateException {
        if (socket != null && socket.isConnected() && socket instanceof SSLSocket) {
            final SSLSocket sslSocket = (SSLSocket)socket;
            final SSLSession handshakeSession = sslSocket.getHandshakeSession();
            if (handshakeSession == null) {
                throw new CertificateException("No handshake session");
            }
            final String endpointIdentificationAlgorithm = sslSocket.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (endpointIdentificationAlgorithm != null && !endpointIdentificationAlgorithm.isEmpty()) {
                X509TrustManagerImpl.checkIdentity(handshakeSession, array, endpointIdentificationAlgorithm, b);
            }
            SSLAlgorithmConstraints sslAlgorithmConstraints;
            if (ProtocolVersion.useTLS12PlusSpec(handshakeSession.getProtocol())) {
                if (handshakeSession instanceof ExtendedSSLSession) {
                    sslAlgorithmConstraints = new SSLAlgorithmConstraints(sslSocket, ((ExtendedSSLSession)handshakeSession).getLocalSupportedSignatureAlgorithms(), true);
                }
                else {
                    sslAlgorithmConstraints = new SSLAlgorithmConstraints(sslSocket, true);
                }
            }
            else {
                sslAlgorithmConstraints = new SSLAlgorithmConstraints(sslSocket, true);
            }
            this.checkAlgorithmConstraints(array, sslAlgorithmConstraints, b);
        }
    }
    
    private void checkAdditionalTrust(final X509Certificate[] array, final String s, final SSLEngine sslEngine, final boolean b) throws CertificateException {
        if (sslEngine != null) {
            final SSLSession handshakeSession = sslEngine.getHandshakeSession();
            if (handshakeSession == null) {
                throw new CertificateException("No handshake session");
            }
            final String endpointIdentificationAlgorithm = sslEngine.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (endpointIdentificationAlgorithm != null && !endpointIdentificationAlgorithm.isEmpty()) {
                X509TrustManagerImpl.checkIdentity(handshakeSession, array, endpointIdentificationAlgorithm, b);
            }
            SSLAlgorithmConstraints sslAlgorithmConstraints;
            if (ProtocolVersion.useTLS12PlusSpec(handshakeSession.getProtocol())) {
                if (handshakeSession instanceof ExtendedSSLSession) {
                    sslAlgorithmConstraints = new SSLAlgorithmConstraints(sslEngine, ((ExtendedSSLSession)handshakeSession).getLocalSupportedSignatureAlgorithms(), true);
                }
                else {
                    sslAlgorithmConstraints = new SSLAlgorithmConstraints(sslEngine, true);
                }
            }
            else {
                sslAlgorithmConstraints = new SSLAlgorithmConstraints(sslEngine, true);
            }
            this.checkAlgorithmConstraints(array, sslAlgorithmConstraints, b);
        }
    }
    
    private void checkAlgorithmConstraints(final X509Certificate[] array, final AlgorithmConstraints algorithmConstraints, final boolean b) throws CertificateException {
        try {
            int n = array.length - 1;
            final HashSet set = new HashSet();
            final X509Certificate[] acceptedIssuers = this.tm.getAcceptedIssuers();
            if (acceptedIssuers != null && acceptedIssuers.length > 0) {
                Collections.addAll(set, acceptedIssuers);
            }
            if (set.contains(array[n])) {
                --n;
            }
            if (n >= 0) {
                final AlgorithmChecker algorithmChecker = new AlgorithmChecker(algorithmConstraints, (Timestamp)null, b ? "tls client" : "tls server");
                algorithmChecker.init(false);
                for (int i = n; i >= 0; --i) {
                    algorithmChecker.check(array[i], (Collection<String>)Collections.emptySet());
                }
            }
        }
        catch (final CertPathValidatorException ex) {
            throw new CertificateException("Certificates do not conform to algorithm constraints", ex);
        }
    }
}
