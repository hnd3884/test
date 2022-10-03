package org.openjsse.sun.security.ssl;

import org.openjsse.sun.security.util.HostnameChecker;
import sun.security.util.AnchorCertificates;
import java.util.Iterator;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import java.util.List;
import javax.net.ssl.SSLSession;
import java.security.AlgorithmConstraints;
import org.openjsse.javax.net.ssl.ExtendedSSLSession;
import org.openjsse.javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLEngine;
import java.security.cert.CertificateException;
import java.net.Socket;
import java.util.Collections;
import org.openjsse.sun.security.validator.Validator;
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
    
    X509TrustManagerImpl(final String validatorType, Collection<X509Certificate> trustedCerts) {
        this.validatorType = validatorType;
        this.pkixParams = null;
        if (trustedCerts == null) {
            trustedCerts = (Collection<X509Certificate>)Collections.emptySet();
        }
        this.trustedCerts = trustedCerts;
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,trustmanager")) {
            SSLLogger.fine("adding as trusted certificates", (Object[])trustedCerts.toArray(new X509Certificate[0]));
        }
    }
    
    X509TrustManagerImpl(final String validatorType, final PKIXBuilderParameters params) {
        this.validatorType = validatorType;
        this.pkixParams = params;
        final Validator v = this.getValidator("tls server");
        this.trustedCerts = v.getTrustedCertificates();
        this.serverValidator = v;
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,trustmanager")) {
            SSLLogger.fine("adding as trusted certificates", (Object[])this.trustedCerts.toArray(new X509Certificate[0]));
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
            final boolean isExtSession = session instanceof ExtendedSSLSession;
            AlgorithmConstraints constraints;
            if (isExtSession && ProtocolVersion.useTLS12PlusSpec(session.getProtocol())) {
                final ExtendedSSLSession extSession = (ExtendedSSLSession)session;
                final String[] localSupportedSignAlgs = extSession.getLocalSupportedSignatureAlgorithms();
                constraints = new SSLAlgorithmConstraints(sslSocket, localSupportedSignAlgs, false);
            }
            else {
                constraints = new SSLAlgorithmConstraints(sslSocket, false);
            }
            List<byte[]> responseList = Collections.emptyList();
            if (!checkClientTrusted && isExtSession) {
                responseList = ((ExtendedSSLSession)session).getStatusResponses();
            }
            trustedChain = validate(v, chain, responseList, constraints, checkClientTrusted ? null : authType);
            final String identityAlg = sslSocket.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (identityAlg != null && identityAlg.length() != 0) {
                checkIdentity(session, trustedChain, identityAlg, checkClientTrusted);
            }
        }
        else {
            trustedChain = validate(v, chain, Collections.emptyList(), null, checkClientTrusted ? null : authType);
        }
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,trustmanager")) {
            SSLLogger.fine("Found trusted certificate", trustedChain[trustedChain.length - 1]);
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
            final boolean isExtSession = session instanceof ExtendedSSLSession;
            AlgorithmConstraints constraints;
            if (isExtSession && ProtocolVersion.useTLS12PlusSpec(session.getProtocol())) {
                final ExtendedSSLSession extSession = (ExtendedSSLSession)session;
                final String[] localSupportedSignAlgs = extSession.getLocalSupportedSignatureAlgorithms();
                constraints = new SSLAlgorithmConstraints((org.openjsse.javax.net.ssl.SSLEngine)engine, localSupportedSignAlgs, false);
            }
            else {
                constraints = new SSLAlgorithmConstraints((org.openjsse.javax.net.ssl.SSLEngine)engine, false);
            }
            List<byte[]> responseList = Collections.emptyList();
            if (!checkClientTrusted && isExtSession) {
                responseList = ((ExtendedSSLSession)session).getStatusResponses();
            }
            trustedChain = validate(v, chain, responseList, constraints, checkClientTrusted ? null : authType);
            final String identityAlg = engine.getSSLParameters().getEndpointIdentificationAlgorithm();
            if (identityAlg != null && identityAlg.length() != 0) {
                checkIdentity(session, trustedChain, identityAlg, checkClientTrusted);
            }
        }
        else {
            trustedChain = validate(v, chain, Collections.emptyList(), null, checkClientTrusted ? null : authType);
        }
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,trustmanager")) {
            SSLLogger.fine("Found trusted certificate", trustedChain[trustedChain.length - 1]);
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
    
    private static X509Certificate[] validate(final Validator v, final X509Certificate[] chain, final List<byte[]> responseList, final AlgorithmConstraints constraints, final String authType) throws CertificateException {
        final Object o = JsseJce.beginFipsProvider();
        try {
            return v.validate(chain, null, responseList, constraints, authType);
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
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,trustmanager")) {
                    SSLLogger.fine("Illegal server name: " + sniName, new Object[0]);
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
        final boolean chainsToPublicCA = AnchorCertificates.contains(trustedChain[trustedChain.length - 1]);
        boolean identifiable = false;
        final String peerHost = session.getPeerHost();
        if (!checkClientTrusted) {
            final List<SNIServerName> sniNames = getRequestedServerNames(session);
            final String sniHostName = getHostNameInSNI(sniNames);
            if (sniHostName != null) {
                try {
                    checkIdentity(sniHostName, trustedChain[0], algorithm, chainsToPublicCA);
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
            checkIdentity(peerHost, trustedChain[0], algorithm, chainsToPublicCA);
        }
    }
    
    static void checkIdentity(final String hostname, final X509Certificate cert, final String algorithm) throws CertificateException {
        checkIdentity(hostname, cert, algorithm, false);
    }
    
    private static void checkIdentity(String hostname, final X509Certificate cert, final String algorithm, final boolean chainsToPublicCA) throws CertificateException {
        if (algorithm != null && algorithm.length() != 0) {
            if (hostname != null && hostname.startsWith("[") && hostname.endsWith("]")) {
                hostname = hostname.substring(1, hostname.length() - 1);
            }
            if (algorithm.equalsIgnoreCase("HTTPS")) {
                HostnameChecker.getInstance((byte)1).match(hostname, cert, chainsToPublicCA);
            }
            else {
                if (!algorithm.equalsIgnoreCase("LDAP") && !algorithm.equalsIgnoreCase("LDAPS")) {
                    throw new CertificateException("Unknown identification algorithm: " + algorithm);
                }
                HostnameChecker.getInstance((byte)2).match(hostname, cert, chainsToPublicCA);
            }
        }
    }
}
