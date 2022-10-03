package org.openjsse.legacy8ujsse.sun.security.ssl;

import sun.security.util.HostnameChecker;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import java.util.List;
import java.util.Iterator;
import javax.net.ssl.SSLSession;
import java.security.AlgorithmConstraints;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLEngine;
import java.security.cert.CertificateException;
import java.net.Socket;
import java.util.Collections;
import org.openjsse.legacy8ujsse.sun.security.validator.Validator;
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
    private static final Debug debug;
    
    X509TrustManagerImpl(final String validatorType, Collection<X509Certificate> trustedCerts) {
        this.validatorType = validatorType;
        this.pkixParams = null;
        if (trustedCerts == null) {
            trustedCerts = (Collection<X509Certificate>)Collections.emptySet();
        }
        this.trustedCerts = trustedCerts;
        if (X509TrustManagerImpl.debug != null && Debug.isOn("trustmanager")) {
            this.showTrustedCerts();
        }
    }
    
    X509TrustManagerImpl(final String validatorType, final PKIXBuilderParameters params) {
        this.validatorType = validatorType;
        this.pkixParams = params;
        final Validator v = this.getValidator("tls server");
        this.trustedCerts = v.getTrustedCertificates();
        this.serverValidator = v;
        if (X509TrustManagerImpl.debug != null && Debug.isOn("trustmanager")) {
            this.showTrustedCerts();
        }
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.checkTrusted(chain, authType, (Socket)null, true);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.checkTrusted(chain, authType, (Socket)null, false);
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        final X509Certificate[] certsArray = new X509Certificate[this.trustedCerts.size()];
        this.trustedCerts.toArray(certsArray);
        return certsArray;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket) throws CertificateException {
        this.checkTrusted(chain, authType, socket, true);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket) throws CertificateException {
        this.checkTrusted(chain, authType, socket, false);
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) throws CertificateException {
        this.checkTrusted(chain, authType, engine, true);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) throws CertificateException {
        this.checkTrusted(chain, authType, engine, false);
    }
    
    private Validator checkTrustedInit(final X509Certificate[] chain, final String authType, final boolean checkClientTrusted) {
        if (chain == null || chain.length == 0) {
            throw new IllegalArgumentException("null or zero-length certificate chain");
        }
        if (authType == null || authType.length() == 0) {
            throw new IllegalArgumentException("null or zero-length authentication type");
        }
        Validator v = null;
        if (checkClientTrusted) {
            v = this.clientValidator;
            if (v == null) {
                synchronized (this) {
                    v = this.clientValidator;
                    if (v == null) {
                        v = this.getValidator("tls client");
                        this.clientValidator = v;
                    }
                }
            }
        }
        else {
            v = this.serverValidator;
            if (v == null) {
                synchronized (this) {
                    v = this.serverValidator;
                    if (v == null) {
                        v = this.getValidator("tls server");
                        this.serverValidator = v;
                    }
                }
            }
        }
        return v;
    }
    
    private void checkTrusted(final X509Certificate[] chain, final String authType, final Socket socket, final boolean checkClientTrusted) throws CertificateException {
        final Validator v = this.checkTrustedInit(chain, authType, checkClientTrusted);
        X509Certificate[] trustedChain = null;
        if (socket != null && socket.isConnected() && socket instanceof SSLSocket) {
            final SSLSocket sslSocket = (SSLSocket)socket;
            final SSLSession session = sslSocket.getHandshakeSession();
            if (session == null) {
                throw new CertificateException("No handshake session");
            }
            AlgorithmConstraints constraints = null;
            final ProtocolVersion protocolVersion = ProtocolVersion.valueOf(session.getProtocol());
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                if (session instanceof ExtendedSSLSession) {
                    final ExtendedSSLSession extSession = (ExtendedSSLSession)session;
                    final String[] localSupportedSignAlgs = extSession.getLocalSupportedSignatureAlgorithms();
                    constraints = new SSLAlgorithmConstraints(sslSocket, localSupportedSignAlgs, false);
                }
                else {
                    constraints = new SSLAlgorithmConstraints(sslSocket, false);
                }
            }
            else {
                constraints = new SSLAlgorithmConstraints(sslSocket, false);
            }
            trustedChain = validate(v, chain, constraints, checkClientTrusted ? null : authType);
            final String identityAlg = sslSocket.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (identityAlg != null && identityAlg.length() != 0) {
                checkIdentity(session, trustedChain, identityAlg, checkClientTrusted);
            }
        }
        else {
            trustedChain = validate(v, chain, null, checkClientTrusted ? null : authType);
        }
        if (X509TrustManagerImpl.debug != null && Debug.isOn("trustmanager")) {
            System.out.println("Found trusted certificate:");
            System.out.println(trustedChain[trustedChain.length - 1]);
        }
    }
    
    private void checkTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine, final boolean checkClientTrusted) throws CertificateException {
        final Validator v = this.checkTrustedInit(chain, authType, checkClientTrusted);
        X509Certificate[] trustedChain = null;
        if (engine != null) {
            final SSLSession session = engine.getHandshakeSession();
            if (session == null) {
                throw new CertificateException("No handshake session");
            }
            AlgorithmConstraints constraints = null;
            final ProtocolVersion protocolVersion = ProtocolVersion.valueOf(session.getProtocol());
            if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
                if (session instanceof ExtendedSSLSession) {
                    final ExtendedSSLSession extSession = (ExtendedSSLSession)session;
                    final String[] localSupportedSignAlgs = extSession.getLocalSupportedSignatureAlgorithms();
                    constraints = new SSLAlgorithmConstraints(engine, localSupportedSignAlgs, false);
                }
                else {
                    constraints = new SSLAlgorithmConstraints(engine, false);
                }
            }
            else {
                constraints = new SSLAlgorithmConstraints(engine, false);
            }
            trustedChain = validate(v, chain, constraints, checkClientTrusted ? null : authType);
            final String identityAlg = engine.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (identityAlg != null && identityAlg.length() != 0) {
                checkIdentity(session, trustedChain, identityAlg, checkClientTrusted);
            }
        }
        else {
            trustedChain = validate(v, chain, null, checkClientTrusted ? null : authType);
        }
    }
    
    private void showTrustedCerts() {
        for (final X509Certificate cert : this.trustedCerts) {
            System.out.println("adding as trusted cert:");
            System.out.println("  Subject: " + cert.getSubjectX500Principal());
            System.out.println("  Issuer:  " + cert.getIssuerX500Principal());
            System.out.println("  Algorithm: " + cert.getPublicKey().getAlgorithm() + "; Serial number: 0x" + cert.getSerialNumber().toString(16));
            System.out.println("  Valid from " + cert.getNotBefore() + " until " + cert.getNotAfter());
            System.out.println();
        }
    }
    
    private Validator getValidator(final String variant) {
        Validator v;
        if (this.pkixParams == null) {
            v = Validator.getInstance(this.validatorType, variant, this.trustedCerts);
        }
        else {
            v = Validator.getInstance(this.validatorType, variant, this.pkixParams);
        }
        return v;
    }
    
    private static X509Certificate[] validate(final Validator v, final X509Certificate[] chain, final AlgorithmConstraints constraints, final String authType) throws CertificateException {
        final Object o = JsseJce.beginFipsProvider();
        try {
            return v.validate(chain, null, constraints, authType);
        }
        finally {
            JsseJce.endFipsProvider(o);
        }
    }
    
    private static String getHostNameInSNI(final List<SNIServerName> sniNames) {
        SNIHostName hostname = null;
        for (final SNIServerName sniName : sniNames) {
            if (sniName.getType() != 0) {
                continue;
            }
            if (sniName instanceof SNIHostName) {
                hostname = (SNIHostName)sniName;
                break;
            }
            try {
                hostname = new SNIHostName(sniName.getEncoded());
            }
            catch (final IllegalArgumentException iae) {
                if (X509TrustManagerImpl.debug != null && Debug.isOn("trustmanager")) {
                    System.out.println("Illegal server name: " + sniName);
                }
            }
            break;
        }
        if (hostname != null) {
            return hostname.getAsciiName();
        }
        return null;
    }
    
    static List<SNIServerName> getRequestedServerNames(final Socket socket) {
        if (socket != null && socket.isConnected() && socket instanceof SSLSocket) {
            return getRequestedServerNames(((SSLSocket)socket).getHandshakeSession());
        }
        return Collections.emptyList();
    }
    
    static List<SNIServerName> getRequestedServerNames(final SSLEngine engine) {
        if (engine != null) {
            return getRequestedServerNames(engine.getHandshakeSession());
        }
        return Collections.emptyList();
    }
    
    private static List<SNIServerName> getRequestedServerNames(final SSLSession session) {
        if (session != null && session instanceof ExtendedSSLSession) {
            return ((ExtendedSSLSession)session).getRequestedServerNames();
        }
        return Collections.emptyList();
    }
    
    static void checkIdentity(final SSLSession session, final X509Certificate[] trustedChain, final String algorithm, final boolean checkClientTrusted) throws CertificateException {
        boolean identifiable = false;
        final String peerHost = session.getPeerHost();
        if (!checkClientTrusted) {
            final List<SNIServerName> sniNames = getRequestedServerNames(session);
            final String sniHostName = getHostNameInSNI(sniNames);
            if (sniHostName != null) {
                try {
                    checkIdentity(sniHostName, trustedChain[0], algorithm);
                    identifiable = true;
                }
                catch (final CertificateException ce) {
                    if (sniHostName.equalsIgnoreCase(peerHost)) {
                        throw ce;
                    }
                }
            }
        }
        if (!identifiable) {
            checkIdentity(peerHost, trustedChain[0], algorithm);
        }
    }
    
    static void checkIdentity(String hostname, final X509Certificate cert, final String algorithm) throws CertificateException {
        if (algorithm != null && algorithm.length() != 0) {
            if (hostname != null && hostname.startsWith("[") && hostname.endsWith("]")) {
                hostname = hostname.substring(1, hostname.length() - 1);
            }
            if (algorithm.equalsIgnoreCase("HTTPS")) {
                HostnameChecker.getInstance((byte)1).match(hostname, cert);
            }
            else {
                if (!algorithm.equalsIgnoreCase("LDAP") && !algorithm.equalsIgnoreCase("LDAPS")) {
                    throw new CertificateException("Unknown identification algorithm: " + algorithm);
                }
                HostnameChecker.getInstance((byte)2).match(hostname, cert);
            }
        }
    }
    
    static {
        debug = Debug.getInstance("ssl");
    }
}
