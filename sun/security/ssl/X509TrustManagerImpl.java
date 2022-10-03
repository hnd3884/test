package sun.security.ssl;

import sun.security.util.HostnameChecker;
import sun.security.util.AnchorCertificates;
import java.util.Iterator;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLSession;
import java.security.AlgorithmConstraints;
import java.util.List;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLEngine;
import java.security.cert.CertificateException;
import java.net.Socket;
import java.util.Collections;
import sun.security.validator.Validator;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509Certificate;
import java.util.Collection;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

final class X509TrustManagerImpl extends X509ExtendedTrustManager implements X509TrustManager
{
    private final String validatorType;
    private final Collection<X509Certificate> trustedCerts;
    private final PKIXBuilderParameters pkixParams;
    private volatile Validator clientValidator;
    private volatile Validator serverValidator;
    
    X509TrustManagerImpl(final String validatorType, Collection<X509Certificate> emptySet) {
        this.validatorType = validatorType;
        this.pkixParams = null;
        if (emptySet == null) {
            emptySet = Collections.emptySet();
        }
        this.trustedCerts = (Collection<X509Certificate>)emptySet;
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,trustmanager")) {
            SSLLogger.fine("adding as trusted certificates", (Object[])emptySet.toArray((Object[])new X509Certificate[0]));
        }
    }
    
    X509TrustManagerImpl(final String validatorType, final PKIXBuilderParameters pkixParams) {
        this.validatorType = validatorType;
        this.pkixParams = pkixParams;
        final Validator validator = this.getValidator("tls server");
        this.trustedCerts = validator.getTrustedCertificates();
        this.serverValidator = validator;
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,trustmanager")) {
            SSLLogger.fine("adding as trusted certificates", (Object[])this.trustedCerts.toArray(new X509Certificate[0]));
        }
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] array, final String s) throws CertificateException {
        this.checkTrusted(array, s, (Socket)null, true);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] array, final String s) throws CertificateException {
        this.checkTrusted(array, s, (Socket)null, false);
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        final X509Certificate[] array = new X509Certificate[this.trustedCerts.size()];
        this.trustedCerts.toArray(array);
        return array;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] array, final String s, final Socket socket) throws CertificateException {
        this.checkTrusted(array, s, socket, true);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] array, final String s, final Socket socket) throws CertificateException {
        this.checkTrusted(array, s, socket, false);
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] array, final String s, final SSLEngine sslEngine) throws CertificateException {
        this.checkTrusted(array, s, sslEngine, true);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] array, final String s, final SSLEngine sslEngine) throws CertificateException {
        this.checkTrusted(array, s, sslEngine, false);
    }
    
    private Validator checkTrustedInit(final X509Certificate[] array, final String s, final boolean b) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("null or zero-length certificate chain");
        }
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("null or zero-length authentication type");
        }
        Validator validator;
        if (b) {
            validator = this.clientValidator;
            if (validator == null) {
                synchronized (this) {
                    validator = this.clientValidator;
                    if (validator == null) {
                        validator = this.getValidator("tls client");
                        this.clientValidator = validator;
                    }
                }
            }
        }
        else {
            validator = this.serverValidator;
            if (validator == null) {
                synchronized (this) {
                    validator = this.serverValidator;
                    if (validator == null) {
                        validator = this.getValidator("tls server");
                        this.serverValidator = validator;
                    }
                }
            }
        }
        return validator;
    }
    
    private void checkTrusted(final X509Certificate[] array, final String s, final Socket socket, final boolean b) throws CertificateException {
        final Validator checkTrustedInit = this.checkTrustedInit(array, s, b);
        X509Certificate[] array2;
        if (socket != null && socket.isConnected() && socket instanceof SSLSocket) {
            final SSLSocket sslSocket = (SSLSocket)socket;
            final SSLSession handshakeSession = sslSocket.getHandshakeSession();
            if (handshakeSession == null) {
                throw new CertificateException("No handshake session");
            }
            final boolean b2 = handshakeSession instanceof ExtendedSSLSession;
            SSLAlgorithmConstraints sslAlgorithmConstraints;
            if (b2 && ProtocolVersion.useTLS12PlusSpec(handshakeSession.getProtocol())) {
                sslAlgorithmConstraints = new SSLAlgorithmConstraints(sslSocket, ((ExtendedSSLSession)handshakeSession).getLocalSupportedSignatureAlgorithms(), false);
            }
            else {
                sslAlgorithmConstraints = new SSLAlgorithmConstraints(sslSocket, false);
            }
            Object o = Collections.emptyList();
            if (!b && b2 && handshakeSession instanceof SSLSessionImpl) {
                o = ((SSLSessionImpl)handshakeSession).getStatusResponses();
            }
            array2 = validate(checkTrustedInit, array, (List<byte[]>)o, sslAlgorithmConstraints, b ? null : s);
            final String endpointIdentificationAlgorithm = sslSocket.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (endpointIdentificationAlgorithm != null && !endpointIdentificationAlgorithm.isEmpty()) {
                checkIdentity(handshakeSession, array2, endpointIdentificationAlgorithm, b);
            }
        }
        else {
            array2 = validate(checkTrustedInit, array, Collections.emptyList(), null, b ? null : s);
        }
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,trustmanager")) {
            SSLLogger.fine("Found trusted certificate", array2[array2.length - 1]);
        }
    }
    
    private void checkTrusted(final X509Certificate[] array, final String s, final SSLEngine sslEngine, final boolean b) throws CertificateException {
        final Validator checkTrustedInit = this.checkTrustedInit(array, s, b);
        X509Certificate[] array2;
        if (sslEngine != null) {
            final SSLSession handshakeSession = sslEngine.getHandshakeSession();
            if (handshakeSession == null) {
                throw new CertificateException("No handshake session");
            }
            final boolean b2 = handshakeSession instanceof ExtendedSSLSession;
            SSLAlgorithmConstraints sslAlgorithmConstraints;
            if (b2 && ProtocolVersion.useTLS12PlusSpec(handshakeSession.getProtocol())) {
                sslAlgorithmConstraints = new SSLAlgorithmConstraints(sslEngine, ((ExtendedSSLSession)handshakeSession).getLocalSupportedSignatureAlgorithms(), false);
            }
            else {
                sslAlgorithmConstraints = new SSLAlgorithmConstraints(sslEngine, false);
            }
            Object o = Collections.emptyList();
            if (!b && b2 && handshakeSession instanceof SSLSessionImpl) {
                o = ((SSLSessionImpl)handshakeSession).getStatusResponses();
            }
            array2 = validate(checkTrustedInit, array, (List<byte[]>)o, sslAlgorithmConstraints, b ? null : s);
            final String endpointIdentificationAlgorithm = sslEngine.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (endpointIdentificationAlgorithm != null && !endpointIdentificationAlgorithm.isEmpty()) {
                checkIdentity(handshakeSession, array2, endpointIdentificationAlgorithm, b);
            }
        }
        else {
            array2 = validate(checkTrustedInit, array, Collections.emptyList(), null, b ? null : s);
        }
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,trustmanager")) {
            SSLLogger.fine("Found trusted certificate", array2[array2.length - 1]);
        }
    }
    
    private Validator getValidator(final String s) {
        Validator validator;
        if (this.pkixParams == null) {
            validator = Validator.getInstance(this.validatorType, s, this.trustedCerts);
        }
        else {
            validator = Validator.getInstance(this.validatorType, s, this.pkixParams);
        }
        return validator;
    }
    
    private static X509Certificate[] validate(final Validator validator, final X509Certificate[] array, final List<byte[]> list, final AlgorithmConstraints algorithmConstraints, final String s) throws CertificateException {
        final Object beginFipsProvider = JsseJce.beginFipsProvider();
        try {
            return validator.validate(array, (Collection)null, (List)list, algorithmConstraints, (Object)s);
        }
        finally {
            JsseJce.endFipsProvider(beginFipsProvider);
        }
    }
    
    private static String getHostNameInSNI(final List<SNIServerName> list) {
        SNIHostName sniHostName = null;
        for (final SNIServerName sniServerName : list) {
            if (sniServerName.getType() != 0) {
                continue;
            }
            if (sniServerName instanceof SNIHostName) {
                sniHostName = (SNIHostName)sniServerName;
                break;
            }
            try {
                sniHostName = new SNIHostName(sniServerName.getEncoded());
            }
            catch (final IllegalArgumentException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,trustmanager")) {
                    SSLLogger.fine("Illegal server name: " + sniServerName, new Object[0]);
                }
            }
            break;
        }
        if (sniHostName != null) {
            return sniHostName.getAsciiName();
        }
        return null;
    }
    
    static List<SNIServerName> getRequestedServerNames(final Socket socket) {
        if (socket != null && socket.isConnected() && socket instanceof SSLSocket) {
            return getRequestedServerNames(((SSLSocket)socket).getHandshakeSession());
        }
        return Collections.emptyList();
    }
    
    static List<SNIServerName> getRequestedServerNames(final SSLEngine sslEngine) {
        if (sslEngine != null) {
            return getRequestedServerNames(sslEngine.getHandshakeSession());
        }
        return Collections.emptyList();
    }
    
    private static List<SNIServerName> getRequestedServerNames(final SSLSession sslSession) {
        if (sslSession != null && sslSession instanceof ExtendedSSLSession) {
            return ((ExtendedSSLSession)sslSession).getRequestedServerNames();
        }
        return Collections.emptyList();
    }
    
    static void checkIdentity(final SSLSession sslSession, final X509Certificate[] array, final String s, final boolean b) throws CertificateException {
        final boolean contains = AnchorCertificates.contains(array[array.length - 1]);
        boolean b2 = false;
        final String peerHost = sslSession.getPeerHost();
        if (!b) {
            final String hostNameInSNI = getHostNameInSNI(getRequestedServerNames(sslSession));
            if (hostNameInSNI != null) {
                try {
                    checkIdentity(hostNameInSNI, array[0], s, contains);
                    b2 = true;
                }
                catch (final CertificateException ex) {
                    if (hostNameInSNI.equalsIgnoreCase(peerHost)) {
                        throw ex;
                    }
                }
            }
        }
        if (!b2) {
            checkIdentity(peerHost, array[0], s, contains);
        }
    }
    
    static void checkIdentity(final String s, final X509Certificate x509Certificate, final String s2) throws CertificateException {
        checkIdentity(s, x509Certificate, s2, false);
    }
    
    private static void checkIdentity(String substring, final X509Certificate x509Certificate, final String s, final boolean b) throws CertificateException {
        if (s != null && !s.isEmpty()) {
            if (substring != null && substring.startsWith("[") && substring.endsWith("]")) {
                substring = substring.substring(1, substring.length() - 1);
            }
            if (s.equalsIgnoreCase("HTTPS")) {
                HostnameChecker.getInstance((byte)1).match(substring, x509Certificate, b);
            }
            else {
                if (!s.equalsIgnoreCase("LDAP") && !s.equalsIgnoreCase("LDAPS")) {
                    throw new CertificateException("Unknown identification algorithm: " + s);
                }
                HostnameChecker.getInstance((byte)2).match(substring, x509Certificate, b);
            }
        }
    }
}
