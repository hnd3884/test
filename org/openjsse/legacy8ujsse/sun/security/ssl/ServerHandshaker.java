package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;
import sun.security.util.AlgorithmDecomposer;
import sun.security.util.LegacyAlgorithmConstraints;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import javax.net.ssl.X509ExtendedTrustManager;
import java.math.BigInteger;
import javax.crypto.spec.SecretKeySpec;
import java.security.AccessControlContext;
import java.security.spec.ECParameterSpec;
import javax.net.ssl.X509ExtendedKeyManager;
import java.security.interfaces.ECPublicKey;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import sun.security.util.KeyUtil;
import java.security.KeyPair;
import java.security.Key;
import java.util.Iterator;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collection;
import java.security.Principal;
import javax.net.ssl.SNIServerName;
import java.util.List;
import java.security.GeneralSecurityException;
import java.util.Collections;
import javax.net.ssl.SSLException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLHandshakeException;
import java.security.MessageDigest;
import java.io.IOException;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLProtocolException;
import java.security.AlgorithmConstraints;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

final class ServerHandshaker extends Handshaker
{
    private byte doClientAuth;
    private X509Certificate[] certs;
    private PrivateKey privateKey;
    private Object serviceCreds;
    private boolean needClientVerify;
    private PrivateKey tempPrivateKey;
    private PublicKey tempPublicKey;
    private DHCrypt dh;
    private ECDHCrypt ecdh;
    private ProtocolVersion clientRequestedVersion;
    private EllipticCurvesExtension requestedCurves;
    SignatureAndHashAlgorithm preferableSignatureAlgorithm;
    private static final boolean useSmartEphemeralDHKeys;
    private static final boolean useLegacyEphemeralDHKeys;
    private static final int customizedDHKeySize;
    private static final AlgorithmConstraints legacyAlgorithmConstraints;
    
    ServerHandshaker(final SSLSocketImpl socket, final SSLContextImpl context, final ProtocolList enabledProtocols, final byte clientAuth, final ProtocolVersion activeProtocolVersion, final boolean isInitialHandshake, final boolean secureRenegotiation, final byte[] clientVerifyData, final byte[] serverVerifyData) {
        super(socket, context, enabledProtocols, clientAuth != 0, false, activeProtocolVersion, isInitialHandshake, secureRenegotiation, clientVerifyData, serverVerifyData);
        this.needClientVerify = false;
        this.doClientAuth = clientAuth;
    }
    
    ServerHandshaker(final SSLEngineImpl engine, final SSLContextImpl context, final ProtocolList enabledProtocols, final byte clientAuth, final ProtocolVersion activeProtocolVersion, final boolean isInitialHandshake, final boolean secureRenegotiation, final byte[] clientVerifyData, final byte[] serverVerifyData) {
        super(engine, context, enabledProtocols, clientAuth != 0, false, activeProtocolVersion, isInitialHandshake, secureRenegotiation, clientVerifyData, serverVerifyData);
        this.needClientVerify = false;
        this.doClientAuth = clientAuth;
    }
    
    void setClientAuth(final byte clientAuth) {
        this.doClientAuth = clientAuth;
    }
    
    @Override
    void processMessage(final byte type, final int message_len) throws IOException {
        this.handshakeState.check(type);
        switch (type) {
            case 1: {
                final HandshakeMessage.ClientHello ch = new HandshakeMessage.ClientHello(this.input, message_len);
                this.handshakeState.update(ch, this.resumingSession);
                this.clientHello(ch);
                break;
            }
            case 11: {
                if (this.doClientAuth == 0) {
                    this.fatalSE((byte)10, "client sent unsolicited cert chain");
                }
                final HandshakeMessage.CertificateMsg certificateMsg = new HandshakeMessage.CertificateMsg(this.input);
                this.handshakeState.update(certificateMsg, this.resumingSession);
                this.clientCertificate(certificateMsg);
                break;
            }
            case 16: {
                SecretKey preMasterSecret = null;
                switch (this.keyExchange) {
                    case K_RSA:
                    case K_RSA_EXPORT: {
                        final RSAClientKeyExchange pms = new RSAClientKeyExchange(this.protocolVersion, this.clientRequestedVersion, this.sslContext.getSecureRandom(), this.input, message_len, this.privateKey);
                        this.handshakeState.update(pms, this.resumingSession);
                        preMasterSecret = this.clientKeyExchange(pms);
                        break;
                    }
                    case K_KRB5:
                    case K_KRB5_EXPORT: {
                        final KerberosClientKeyExchange kke = new KerberosClientKeyExchange(this.protocolVersion, this.clientRequestedVersion, this.sslContext.getSecureRandom(), this.input, this.getAccSE(), this.serviceCreds);
                        this.handshakeState.update(kke, this.resumingSession);
                        preMasterSecret = this.clientKeyExchange(kke);
                        break;
                    }
                    case K_DHE_RSA:
                    case K_DHE_DSS:
                    case K_DH_ANON: {
                        final DHClientKeyExchange dhcke = new DHClientKeyExchange(this.input);
                        this.handshakeState.update(dhcke, this.resumingSession);
                        preMasterSecret = this.clientKeyExchange(dhcke);
                        break;
                    }
                    case K_ECDH_RSA:
                    case K_ECDH_ECDSA:
                    case K_ECDHE_RSA:
                    case K_ECDHE_ECDSA:
                    case K_ECDH_ANON: {
                        final ECDHClientKeyExchange ecdhcke = new ECDHClientKeyExchange(this.input);
                        this.handshakeState.update(ecdhcke, this.resumingSession);
                        preMasterSecret = this.clientKeyExchange(ecdhcke);
                        break;
                    }
                    default: {
                        throw new SSLProtocolException("Unrecognized key exchange: " + this.keyExchange);
                    }
                }
                if (this.session.getUseExtendedMasterSecret()) {
                    this.input.digestNow();
                }
                this.calculateKeys(preMasterSecret, this.clientRequestedVersion);
                break;
            }
            case 15: {
                final HandshakeMessage.CertificateVerify cvm = new HandshakeMessage.CertificateVerify(this.input, this.getLocalSupportedSignAlgs(), this.protocolVersion);
                this.handshakeState.update(cvm, this.resumingSession);
                this.clientCertificateVerify(cvm);
                break;
            }
            case 20: {
                final HandshakeMessage.Finished cfm = new HandshakeMessage.Finished(this.protocolVersion, this.input, this.cipherSuite);
                this.handshakeState.update(cfm, this.resumingSession);
                this.clientFinished(cfm);
                break;
            }
            default: {
                throw new SSLProtocolException("Illegal server handshake msg, " + type);
            }
        }
    }
    
    private void clientHello(final HandshakeMessage.ClientHello mesg) throws IOException {
        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        if (ServerHandshaker.rejectClientInitiatedRenego && !this.isInitialHandshake && !this.serverHelloRequested) {
            this.fatalSE((byte)40, "Client initiated renegotiation is not allowed");
        }
        final ServerNameExtension clientHelloSNIExt = (ServerNameExtension)mesg.extensions.get(ExtensionType.EXT_SERVER_NAME);
        if (!this.sniMatchers.isEmpty() && clientHelloSNIExt != null && !clientHelloSNIExt.isMatched(this.sniMatchers)) {
            this.fatalSE((byte)112, "Unrecognized server name indication");
        }
        boolean renegotiationIndicated = false;
        final CipherSuiteList cipherSuites = mesg.getCipherSuites();
        if (cipherSuites.contains(CipherSuite.C_SCSV)) {
            renegotiationIndicated = true;
            if (this.isInitialHandshake) {
                this.secureRenegotiation = true;
            }
            else if (this.secureRenegotiation) {
                this.fatalSE((byte)40, "The SCSV is present in a secure renegotiation");
            }
            else {
                this.fatalSE((byte)40, "The SCSV is present in a insecure renegotiation");
            }
        }
        final RenegotiationInfoExtension clientHelloRI = (RenegotiationInfoExtension)mesg.extensions.get(ExtensionType.EXT_RENEGOTIATION_INFO);
        if (clientHelloRI != null) {
            renegotiationIndicated = true;
            if (this.isInitialHandshake) {
                if (!clientHelloRI.isEmpty()) {
                    this.fatalSE((byte)40, "The renegotiation_info field is not empty");
                }
                this.secureRenegotiation = true;
            }
            else {
                if (!this.secureRenegotiation) {
                    this.fatalSE((byte)40, "The renegotiation_info is present in a insecure renegotiation");
                }
                if (!MessageDigest.isEqual(this.clientVerifyData, clientHelloRI.getRenegotiatedConnection())) {
                    this.fatalSE((byte)40, "Incorrect verify data in ClientHello renegotiation_info message");
                }
            }
        }
        else if (!this.isInitialHandshake && this.secureRenegotiation) {
            this.fatalSE((byte)40, "Inconsistent secure renegotiation indication");
        }
        if (!renegotiationIndicated || !this.secureRenegotiation) {
            if (this.isInitialHandshake) {
                if (!ServerHandshaker.allowLegacyHelloMessages) {
                    this.fatalSE((byte)40, "Failed to negotiate the use of secure renegotiation");
                }
                if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                    System.out.println("Warning: No renegotiation indication in ClientHello, allow legacy ClientHello");
                }
            }
            else if (!ServerHandshaker.allowUnsafeRenegotiation) {
                if (this.activeProtocolVersion.v >= ProtocolVersion.TLS10.v) {
                    this.warningSE((byte)100);
                    this.invalidated = true;
                    if (this.input.available() > 0) {
                        this.fatalSE((byte)10, "ClientHello followed by an unexpected  handshake message");
                    }
                    return;
                }
                this.fatalSE((byte)40, "Renegotiation is not allowed");
            }
            else if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                System.out.println("Warning: continue with insecure renegotiation");
            }
        }
        if (ServerHandshaker.useExtendedMasterSecret) {
            final ExtendedMasterSecretExtension extendedMasterSecretExtension = (ExtendedMasterSecretExtension)mesg.extensions.get(ExtensionType.EXT_EXTENDED_MASTER_SECRET);
            if (extendedMasterSecretExtension != null) {
                this.requestedToUseEMS = true;
            }
            else if (mesg.protocolVersion.v >= ProtocolVersion.TLS10.v && !ServerHandshaker.allowLegacyMasterSecret) {
                this.fatalSE((byte)40, "Extended Master Secret extension is required");
            }
        }
        final ALPNExtension clientHelloALPN = (ALPNExtension)mesg.extensions.get(ExtensionType.EXT_ALPN);
        final boolean hasAPCallback = (this.engine != null && this.appProtocolSelectorSSLEngine != null) || (this.conn != null && this.appProtocolSelectorSSLSocket != null);
        if (!hasAPCallback) {
            if (clientHelloALPN != null && this.localApl.length > 0) {
                String negotiatedValue = null;
                final List<String> protocols = clientHelloALPN.getPeerAPs();
                for (final String ap : this.localApl) {
                    if (protocols.contains(ap)) {
                        negotiatedValue = ap;
                        break;
                    }
                }
                if (negotiatedValue == null) {
                    this.fatalSE((byte)120, new SSLHandshakeException("No matching ALPN values"));
                }
                this.applicationProtocol = negotiatedValue;
            }
            else {
                this.applicationProtocol = "";
            }
        }
        this.input.digestNow();
        final HandshakeMessage.ServerHello m1 = new HandshakeMessage.ServerHello();
        this.clientRequestedVersion = mesg.protocolVersion;
        final ProtocolVersion selectedVersion = this.selectProtocolVersion(this.clientRequestedVersion);
        if (selectedVersion == null || selectedVersion.v == ProtocolVersion.SSL20Hello.v) {
            this.fatalSE((byte)40, "Client requested protocol " + this.clientRequestedVersion + " not enabled or not supported");
        }
        this.handshakeHash.protocolDetermined(selectedVersion);
        this.setVersion(selectedVersion);
        m1.protocolVersion = this.protocolVersion;
        this.clnt_random = mesg.clnt_random;
        this.svr_random = new RandomCookie(this.sslContext.getSecureRandom());
        m1.svr_random = this.svr_random;
        this.session = null;
        if (mesg.sessionId.length() != 0) {
            final SSLSessionImpl previous = ((SSLSessionContextImpl)this.sslContext.engineGetServerSessionContext()).get(mesg.sessionId.getId());
            if (previous != null) {
                this.resumingSession = previous.isRejoinable();
                if (this.resumingSession) {
                    final ProtocolVersion oldVersion = previous.getProtocolVersion();
                    if (oldVersion != mesg.protocolVersion) {
                        this.resumingSession = false;
                    }
                }
                if (this.resumingSession && ServerHandshaker.useExtendedMasterSecret) {
                    if (this.requestedToUseEMS && !previous.getUseExtendedMasterSecret()) {
                        this.resumingSession = false;
                    }
                    else if (!this.requestedToUseEMS && previous.getUseExtendedMasterSecret()) {
                        this.fatalSE((byte)40, "Missing Extended Master Secret extension on session resumption");
                    }
                    else if (!this.requestedToUseEMS && !previous.getUseExtendedMasterSecret()) {
                        if (!ServerHandshaker.allowLegacyResumption) {
                            this.fatalSE((byte)40, "Missing Extended Master Secret extension on session resumption");
                        }
                        else {
                            this.resumingSession = false;
                        }
                    }
                }
                if (this.resumingSession) {
                    final List<SNIServerName> oldServerNames = previous.getRequestedServerNames();
                    if (clientHelloSNIExt != null) {
                        if (!clientHelloSNIExt.isIdentical(oldServerNames)) {
                            this.resumingSession = false;
                        }
                    }
                    else if (!oldServerNames.isEmpty()) {
                        this.resumingSession = false;
                    }
                    if (!this.resumingSession && ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                        System.out.println("The requested server name indication is not identical to the previous one");
                    }
                }
                if (this.resumingSession && this.doClientAuth == 2) {
                    try {
                        previous.getPeerPrincipal();
                    }
                    catch (final SSLPeerUnverifiedException e) {
                        this.resumingSession = false;
                    }
                }
                if (this.resumingSession) {
                    final CipherSuite suite = previous.getSuite();
                    if (suite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || suite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
                        final Principal localPrincipal = previous.getLocalPrincipal();
                        Subject subject = null;
                        try {
                            subject = AccessController.doPrivileged((PrivilegedExceptionAction<Subject>)new PrivilegedExceptionAction<Subject>() {
                                @Override
                                public Subject run() throws Exception {
                                    return Krb5Helper.getServerSubject(ServerHandshaker.this.getAccSE());
                                }
                            });
                        }
                        catch (final PrivilegedActionException e2) {
                            subject = null;
                            if (ServerHandshaker.debug != null && Debug.isOn("session")) {
                                System.out.println("Attempt to obtain subject failed!");
                            }
                        }
                        if (subject != null) {
                            if (Krb5Helper.isRelated(subject, localPrincipal)) {
                                if (ServerHandshaker.debug != null && Debug.isOn("session")) {
                                    System.out.println("Subject can provide creds for princ");
                                }
                            }
                            else {
                                this.resumingSession = false;
                                if (ServerHandshaker.debug != null && Debug.isOn("session")) {
                                    System.out.println("Subject cannot provide creds for princ");
                                }
                            }
                        }
                        else {
                            this.resumingSession = false;
                            if (ServerHandshaker.debug != null && Debug.isOn("session")) {
                                System.out.println("Kerberos credentials are not present in the current Subject; check if  javax.security.auth.useSubjectAsCreds system property has been set to false");
                            }
                        }
                    }
                }
                final String identityAlg = this.getEndpointIdentificationAlgorithmSE();
                if (this.resumingSession && identityAlg != null) {
                    final String sessionIdentityAlg = previous.getEndpointIdentificationAlgorithm();
                    if (!identityAlg.equalsIgnoreCase(sessionIdentityAlg)) {
                        if (ServerHandshaker.debug != null && Debug.isOn("session")) {
                            System.out.println("%% can't resume, endpoint id algorithm does not match, requested: " + identityAlg + ", cached: " + sessionIdentityAlg);
                        }
                        this.resumingSession = false;
                    }
                }
                if (this.resumingSession) {
                    final CipherSuite suite2 = previous.getSuite();
                    if (!this.isNegotiable(suite2) || !mesg.getCipherSuites().contains(suite2)) {
                        this.resumingSession = false;
                    }
                    else {
                        this.setCipherSuite(suite2);
                    }
                }
                if (this.resumingSession) {
                    this.session = previous;
                    if (ServerHandshaker.debug != null && (Debug.isOn("handshake") || Debug.isOn("session"))) {
                        System.out.println("%% Resuming " + this.session);
                    }
                }
            }
        }
        if (this.session == null) {
            if (!this.enableNewSession) {
                throw new SSLException("Client did not resume a session");
            }
            this.requestedCurves = (EllipticCurvesExtension)mesg.extensions.get(ExtensionType.EXT_ELLIPTIC_CURVES);
            if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                final SignatureAlgorithmsExtension signAlgs = (SignatureAlgorithmsExtension)mesg.extensions.get(ExtensionType.EXT_SIGNATURE_ALGORITHMS);
                if (signAlgs != null) {
                    final Collection<SignatureAndHashAlgorithm> peerSignAlgs = signAlgs.getSignAlgorithms();
                    if (peerSignAlgs == null || peerSignAlgs.isEmpty()) {
                        throw new SSLHandshakeException("No peer supported signature algorithms");
                    }
                    final Collection<SignatureAndHashAlgorithm> supportedPeerSignAlgs = SignatureAndHashAlgorithm.getSupportedAlgorithms(this.algorithmConstraints, peerSignAlgs);
                    if (supportedPeerSignAlgs.isEmpty()) {
                        throw new SSLHandshakeException("No signature and hash algorithm in common");
                    }
                    this.setPeerSupportedSignAlgs(supportedPeerSignAlgs);
                }
            }
            this.session = new SSLSessionImpl(this.protocolVersion, CipherSuite.C_NULL, this.getLocalSupportedSignAlgs(), this.sslContext.getSecureRandom(), this.getHostAddressSE(), this.getPortSE(), this.requestedToUseEMS && this.protocolVersion.v >= ProtocolVersion.TLS10.v, this.getEndpointIdentificationAlgorithmSE());
            if (this.protocolVersion.v >= ProtocolVersion.TLS12.v && this.peerSupportedSignAlgs != null) {
                this.session.setPeerSupportedSignatureAlgorithms(this.peerSupportedSignAlgs);
            }
            List<SNIServerName> clientHelloSNI = Collections.emptyList();
            if (clientHelloSNIExt != null) {
                clientHelloSNI = clientHelloSNIExt.getServerNames();
            }
            this.session.setRequestedServerNames(clientHelloSNI);
            this.setHandshakeSessionSE(this.session);
            this.chooseCipherSuite(mesg);
            this.session.setSuite(this.cipherSuite);
            this.session.setLocalPrivateKey(this.privateKey);
        }
        else {
            this.setHandshakeSessionSE(this.session);
        }
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
            this.handshakeHash.setFinishedAlg(this.cipherSuite.prfAlg.getPRFHashAlg());
        }
        m1.cipherSuite = this.cipherSuite;
        m1.sessionId = this.session.getSessionId();
        m1.compression_method = this.session.getCompression();
        if (this.secureRenegotiation) {
            final HelloExtension serverHelloRI = new RenegotiationInfoExtension(this.clientVerifyData, this.serverVerifyData);
            m1.extensions.add(serverHelloRI);
        }
        if (!this.sniMatchers.isEmpty() && clientHelloSNIExt != null && !this.resumingSession) {
            final ServerNameExtension serverHelloSNI = new ServerNameExtension();
            m1.extensions.add(serverHelloSNI);
        }
        if (this.session.getUseExtendedMasterSecret()) {
            m1.extensions.add(new ExtendedMasterSecretExtension());
        }
        if (clientHelloALPN != null) {
            final List<String> peerAPs = clientHelloALPN.getPeerAPs();
            if (hasAPCallback) {
                if (this.conn != null) {
                    this.applicationProtocol = this.appProtocolSelectorSSLSocket.apply(this.conn, peerAPs);
                }
                else {
                    this.applicationProtocol = this.appProtocolSelectorSSLEngine.apply(this.engine, peerAPs);
                }
            }
            if (this.applicationProtocol == null || (!this.applicationProtocol.isEmpty() && !peerAPs.contains(this.applicationProtocol))) {
                this.fatalSE((byte)120, new SSLHandshakeException("No matching ALPN values"));
            }
            else if (!this.applicationProtocol.isEmpty()) {
                m1.extensions.add(new ALPNExtension(this.applicationProtocol));
            }
        }
        else {
            this.applicationProtocol = "";
        }
        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
            m1.print(System.out);
            System.out.println("Cipher suite:  " + this.session.getSuite());
        }
        m1.write(this.output);
        this.handshakeState.update(m1, this.resumingSession);
        if (this.resumingSession) {
            this.calculateConnectionKeys(this.session.getMasterSecret());
            this.sendChangeCipherAndFinish(false);
            return;
        }
        if (this.keyExchange != CipherSuite.KeyExchange.K_KRB5) {
            if (this.keyExchange != CipherSuite.KeyExchange.K_KRB5_EXPORT) {
                if (this.keyExchange != CipherSuite.KeyExchange.K_DH_ANON && this.keyExchange != CipherSuite.KeyExchange.K_ECDH_ANON) {
                    if (this.certs == null) {
                        throw new RuntimeException("no certificates");
                    }
                    final HandshakeMessage.CertificateMsg m2 = new HandshakeMessage.CertificateMsg(this.certs);
                    this.session.setLocalCertificates(this.certs);
                    if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                        m2.print(System.out);
                    }
                    m2.write(this.output);
                    this.handshakeState.update(m2, this.resumingSession);
                }
                else if (this.certs != null) {
                    throw new RuntimeException("anonymous keyexchange with certs");
                }
            }
        }
        HandshakeMessage.ServerKeyExchange m3 = null;
        switch (this.keyExchange) {
            case K_RSA:
            case K_KRB5:
            case K_KRB5_EXPORT: {
                m3 = null;
                break;
            }
            case K_RSA_EXPORT: {
                if (JsseJce.getRSAKeyLength(this.certs[0].getPublicKey()) > 512) {
                    try {
                        m3 = new HandshakeMessage.RSA_ServerKeyExchange(this.tempPublicKey, this.privateKey, this.clnt_random, this.svr_random, this.sslContext.getSecureRandom());
                        this.privateKey = this.tempPrivateKey;
                    }
                    catch (final GeneralSecurityException e3) {
                        Handshaker.throwSSLException("Error generating RSA server key exchange", e3);
                        m3 = null;
                    }
                    break;
                }
                m3 = null;
                break;
            }
            case K_DHE_RSA:
            case K_DHE_DSS: {
                try {
                    m3 = new HandshakeMessage.DH_ServerKeyExchange(this.dh, this.privateKey, this.clnt_random.random_bytes, this.svr_random.random_bytes, this.sslContext.getSecureRandom(), this.preferableSignatureAlgorithm, this.protocolVersion);
                }
                catch (final GeneralSecurityException e3) {
                    Handshaker.throwSSLException("Error generating DH server key exchange", e3);
                    m3 = null;
                }
                break;
            }
            case K_DH_ANON: {
                m3 = new HandshakeMessage.DH_ServerKeyExchange(this.dh, this.protocolVersion);
                break;
            }
            case K_ECDHE_RSA:
            case K_ECDHE_ECDSA:
            case K_ECDH_ANON: {
                try {
                    m3 = new HandshakeMessage.ECDH_ServerKeyExchange(this.ecdh, this.privateKey, this.clnt_random.random_bytes, this.svr_random.random_bytes, this.sslContext.getSecureRandom(), this.preferableSignatureAlgorithm, this.protocolVersion);
                }
                catch (final GeneralSecurityException e3) {
                    Handshaker.throwSSLException("Error generating ECDH server key exchange", e3);
                    m3 = null;
                }
                break;
            }
            case K_ECDH_RSA:
            case K_ECDH_ECDSA: {
                m3 = null;
                break;
            }
            default: {
                throw new RuntimeException("internal error: " + this.keyExchange);
            }
        }
        if (m3 != null) {
            if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                m3.print(System.out);
            }
            m3.write(this.output);
            this.handshakeState.update(m3, this.resumingSession);
        }
        if (this.doClientAuth != 0 && this.keyExchange != CipherSuite.KeyExchange.K_DH_ANON && this.keyExchange != CipherSuite.KeyExchange.K_ECDH_ANON && this.keyExchange != CipherSuite.KeyExchange.K_KRB5 && this.keyExchange != CipherSuite.KeyExchange.K_KRB5_EXPORT) {
            Collection<SignatureAndHashAlgorithm> localSignAlgs = null;
            if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                localSignAlgs = this.getLocalSupportedSignAlgs();
                if (localSignAlgs.isEmpty()) {
                    throw new SSLHandshakeException("No supported signature algorithm");
                }
                final Set<String> localHashAlgs = SignatureAndHashAlgorithm.getHashAlgorithmNames(localSignAlgs);
                if (localHashAlgs.isEmpty()) {
                    throw new SSLHandshakeException("No supported signature algorithm");
                }
            }
            final X509Certificate[] caCerts = this.sslContext.getX509TrustManager().getAcceptedIssuers();
            final HandshakeMessage.CertificateRequest m4 = new HandshakeMessage.CertificateRequest(caCerts, this.keyExchange, localSignAlgs, this.protocolVersion);
            if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                m4.print(System.out);
            }
            m4.write(this.output);
            this.handshakeState.update(m4, this.resumingSession);
        }
        final HandshakeMessage.ServerHelloDone m5 = new HandshakeMessage.ServerHelloDone();
        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
            m5.print(System.out);
        }
        m5.write(this.output);
        this.handshakeState.update(m5, this.resumingSession);
        this.output.flush();
    }
    
    private void chooseCipherSuite(final HandshakeMessage.ClientHello mesg) throws IOException {
        CipherSuiteList prefered;
        CipherSuiteList proposed;
        if (this.preferLocalCipherSuites) {
            prefered = this.getActiveCipherSuites();
            proposed = mesg.getCipherSuites();
        }
        else {
            prefered = mesg.getCipherSuites();
            proposed = this.getActiveCipherSuites();
        }
        final List<CipherSuite> legacySuites = new ArrayList<CipherSuite>();
        for (final CipherSuite suite : prefered.collection()) {
            if (!Handshaker.isNegotiable(proposed, suite)) {
                continue;
            }
            if (this.doClientAuth == 2) {
                if (suite.keyExchange == CipherSuite.KeyExchange.K_DH_ANON) {
                    continue;
                }
                if (suite.keyExchange == CipherSuite.KeyExchange.K_ECDH_ANON) {
                    continue;
                }
            }
            if (!ServerHandshaker.legacyAlgorithmConstraints.permits(null, suite.name, null)) {
                legacySuites.add(suite);
            }
            else {
                if (!this.trySetCipherSuite(suite)) {
                    continue;
                }
                if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                    System.out.println("Standard ciphersuite chosen: " + suite);
                }
                return;
            }
        }
        for (final CipherSuite suite : legacySuites) {
            if (this.trySetCipherSuite(suite)) {
                if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                    System.out.println("Legacy ciphersuite chosen: " + suite);
                }
                return;
            }
        }
        this.fatalSE((byte)40, "no cipher suites in common");
    }
    
    boolean trySetCipherSuite(final CipherSuite suite) {
        if (this.resumingSession) {
            return true;
        }
        if (!suite.isNegotiable()) {
            return false;
        }
        if (this.protocolVersion.v >= suite.obsoleted) {
            return false;
        }
        if (this.protocolVersion.v < suite.supported) {
            return false;
        }
        final CipherSuite.KeyExchange keyExchange = suite.keyExchange;
        this.privateKey = null;
        this.certs = null;
        this.dh = null;
        this.tempPrivateKey = null;
        this.tempPublicKey = null;
        Collection<SignatureAndHashAlgorithm> supportedSignAlgs = null;
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
            if (this.peerSupportedSignAlgs != null) {
                supportedSignAlgs = this.peerSupportedSignAlgs;
            }
            else {
                SignatureAndHashAlgorithm algorithm = null;
                switch (keyExchange) {
                    case K_RSA:
                    case K_DHE_RSA:
                    case K_ECDH_RSA:
                    case K_ECDHE_RSA:
                    case K_DH_RSA: {
                        algorithm = SignatureAndHashAlgorithm.valueOf(SignatureAndHashAlgorithm.HashAlgorithm.SHA1.value, SignatureAndHashAlgorithm.SignatureAlgorithm.RSA.value, 0);
                        break;
                    }
                    case K_DHE_DSS:
                    case K_DH_DSS: {
                        algorithm = SignatureAndHashAlgorithm.valueOf(SignatureAndHashAlgorithm.HashAlgorithm.SHA1.value, SignatureAndHashAlgorithm.SignatureAlgorithm.DSA.value, 0);
                        break;
                    }
                    case K_ECDH_ECDSA:
                    case K_ECDHE_ECDSA: {
                        algorithm = SignatureAndHashAlgorithm.valueOf(SignatureAndHashAlgorithm.HashAlgorithm.SHA1.value, SignatureAndHashAlgorithm.SignatureAlgorithm.ECDSA.value, 0);
                        break;
                    }
                }
                if (algorithm == null) {
                    supportedSignAlgs = (Collection<SignatureAndHashAlgorithm>)Collections.emptySet();
                }
                else {
                    supportedSignAlgs = new ArrayList<SignatureAndHashAlgorithm>(1);
                    supportedSignAlgs.add(algorithm);
                    supportedSignAlgs = SignatureAndHashAlgorithm.getSupportedAlgorithms(this.algorithmConstraints, supportedSignAlgs);
                }
                this.session.setPeerSupportedSignatureAlgorithms(supportedSignAlgs);
            }
        }
        switch (keyExchange) {
            case K_RSA: {
                if (!this.setupPrivateKeyAndChain("RSA")) {
                    return false;
                }
                break;
            }
            case K_RSA_EXPORT: {
                if (!this.setupPrivateKeyAndChain("RSA")) {
                    return false;
                }
                try {
                    if (JsseJce.getRSAKeyLength(this.certs[0].getPublicKey()) > 512 && !this.setupEphemeralRSAKeys(suite.exportable)) {
                        return false;
                    }
                    break;
                }
                catch (final RuntimeException e) {
                    return false;
                }
            }
            case K_DHE_RSA: {
                if (!this.setupPrivateKeyAndChain("RSA")) {
                    return false;
                }
                if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    this.preferableSignatureAlgorithm = SignatureAndHashAlgorithm.getPreferableAlgorithm(supportedSignAlgs, this.algorithmConstraints, "RSA", this.privateKey);
                    if (this.preferableSignatureAlgorithm == null) {
                        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                            System.out.println("No signature and hash algorithm for cipher " + suite);
                        }
                        return false;
                    }
                }
                this.setupEphemeralDHKeys(suite.exportable, this.privateKey);
                break;
            }
            case K_ECDHE_RSA: {
                if (!this.setupPrivateKeyAndChain("RSA")) {
                    return false;
                }
                if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    this.preferableSignatureAlgorithm = SignatureAndHashAlgorithm.getPreferableAlgorithm(supportedSignAlgs, this.algorithmConstraints, "RSA", this.privateKey);
                    if (this.preferableSignatureAlgorithm == null) {
                        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                            System.out.println("No signature and hash algorithm for cipher " + suite);
                        }
                        return false;
                    }
                }
                if (!this.setupEphemeralECDHKeys()) {
                    return false;
                }
                break;
            }
            case K_DHE_DSS: {
                if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    this.preferableSignatureAlgorithm = SignatureAndHashAlgorithm.getPreferableAlgorithm(supportedSignAlgs, this.algorithmConstraints, "DSA");
                    if (this.preferableSignatureAlgorithm == null) {
                        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                            System.out.println("No signature and hash algorithm for cipher " + suite);
                        }
                        return false;
                    }
                }
                if (!this.setupPrivateKeyAndChain("DSA")) {
                    return false;
                }
                this.setupEphemeralDHKeys(suite.exportable, this.privateKey);
                break;
            }
            case K_ECDHE_ECDSA: {
                if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    this.preferableSignatureAlgorithm = SignatureAndHashAlgorithm.getPreferableAlgorithm(supportedSignAlgs, this.algorithmConstraints, "ECDSA");
                    if (this.preferableSignatureAlgorithm == null) {
                        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                            System.out.println("No signature and hash algorithm for cipher " + suite);
                        }
                        return false;
                    }
                }
                if (!this.setupPrivateKeyAndChain("EC")) {
                    return false;
                }
                if (!this.setupEphemeralECDHKeys()) {
                    return false;
                }
                break;
            }
            case K_ECDH_RSA: {
                if (!this.setupPrivateKeyAndChain("EC")) {
                    return false;
                }
                this.setupStaticECDHKeys();
                break;
            }
            case K_ECDH_ECDSA: {
                if (!this.setupPrivateKeyAndChain("EC")) {
                    return false;
                }
                this.setupStaticECDHKeys();
                break;
            }
            case K_KRB5:
            case K_KRB5_EXPORT: {
                if (!this.setupKerberosKeys()) {
                    return false;
                }
                break;
            }
            case K_DH_ANON: {
                this.setupEphemeralDHKeys(suite.exportable, null);
                break;
            }
            case K_ECDH_ANON: {
                if (!this.setupEphemeralECDHKeys()) {
                    return false;
                }
                break;
            }
            default: {
                throw new RuntimeException("Unrecognized cipherSuite: " + suite);
            }
        }
        this.setCipherSuite(suite);
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v && this.peerSupportedSignAlgs == null) {
            this.setPeerSupportedSignAlgs(supportedSignAlgs);
        }
        return true;
    }
    
    private boolean setupEphemeralRSAKeys(final boolean export) {
        final KeyPair kp = this.sslContext.getEphemeralKeyManager().getRSAKeyPair(export, this.sslContext.getSecureRandom());
        if (kp == null) {
            return false;
        }
        this.tempPublicKey = kp.getPublic();
        this.tempPrivateKey = kp.getPrivate();
        return true;
    }
    
    private void setupEphemeralDHKeys(final boolean export, final Key key) {
        int keySize = export ? 512 : 1024;
        if (!export) {
            if (ServerHandshaker.useLegacyEphemeralDHKeys) {
                keySize = 768;
            }
            else if (ServerHandshaker.useSmartEphemeralDHKeys) {
                if (key != null) {
                    final int ks = KeyUtil.getKeySize(key);
                    keySize = ((ks <= 1024) ? 1024 : 2048);
                }
            }
            else if (ServerHandshaker.customizedDHKeySize > 0) {
                keySize = ServerHandshaker.customizedDHKeySize;
            }
        }
        this.dh = new DHCrypt(keySize, this.sslContext.getSecureRandom());
    }
    
    private boolean setupEphemeralECDHKeys() {
        final int index = (this.requestedCurves != null) ? this.requestedCurves.getPreferredCurve(this.algorithmConstraints) : EllipticCurvesExtension.getActiveCurves(this.algorithmConstraints);
        if (index < 0) {
            return false;
        }
        this.ecdh = new ECDHCrypt(index, this.sslContext.getSecureRandom());
        return true;
    }
    
    private void setupStaticECDHKeys() {
        this.ecdh = new ECDHCrypt(this.privateKey, this.certs[0].getPublicKey());
    }
    
    private boolean setupPrivateKeyAndChain(final String algorithm) {
        final X509ExtendedKeyManager km = this.sslContext.getX509KeyManager();
        String alias;
        if (this.conn != null) {
            alias = km.chooseServerAlias(algorithm, null, this.conn);
        }
        else {
            alias = km.chooseEngineServerAlias(algorithm, null, this.engine);
        }
        if (alias == null) {
            return false;
        }
        final PrivateKey tempPrivateKey = km.getPrivateKey(alias);
        if (tempPrivateKey == null) {
            return false;
        }
        final X509Certificate[] tempCerts = km.getCertificateChain(alias);
        if (tempCerts == null || tempCerts.length == 0) {
            return false;
        }
        final String keyAlgorithm = algorithm.split("_")[0];
        final PublicKey publicKey = tempCerts[0].getPublicKey();
        if (!tempPrivateKey.getAlgorithm().equals(keyAlgorithm) || !publicKey.getAlgorithm().equals(keyAlgorithm)) {
            return false;
        }
        if (keyAlgorithm.equals("EC")) {
            if (!(publicKey instanceof ECPublicKey)) {
                return false;
            }
            final ECParameterSpec params = ((ECPublicKey)publicKey).getParams();
            final int id = EllipticCurvesExtension.getCurveIndex(params);
            if (id <= 0 || !EllipticCurvesExtension.isSupported(id) || (this.requestedCurves != null && !this.requestedCurves.contains(id))) {
                return false;
            }
        }
        this.privateKey = tempPrivateKey;
        this.certs = tempCerts;
        return true;
    }
    
    private boolean setupKerberosKeys() {
        if (this.serviceCreds != null) {
            return true;
        }
        try {
            final AccessControlContext acc = this.getAccSE();
            this.serviceCreds = AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    return Krb5Helper.getServiceCreds(acc);
                }
            });
            if (this.serviceCreds != null) {
                if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                    System.out.println("Using Kerberos creds");
                }
                final String serverPrincipal = Krb5Helper.getServerPrincipalName(this.serviceCreds);
                if (serverPrincipal != null) {
                    final SecurityManager sm = System.getSecurityManager();
                    try {
                        if (sm != null) {
                            sm.checkPermission(Krb5Helper.getServicePermission(serverPrincipal, "accept"), acc);
                        }
                    }
                    catch (final SecurityException se) {
                        this.serviceCreds = null;
                        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                            System.out.println("Permission to access Kerberos secret key denied");
                        }
                        return false;
                    }
                }
            }
            return this.serviceCreds != null;
        }
        catch (final PrivilegedActionException e) {
            if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
                System.out.println("Attempt to obtain Kerberos key failed: " + e.toString());
            }
            return false;
        }
    }
    
    private SecretKey clientKeyExchange(final KerberosClientKeyExchange mesg) throws IOException {
        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        this.session.setPeerPrincipal(mesg.getPeerPrincipal());
        this.session.setLocalPrincipal(mesg.getLocalPrincipal());
        final byte[] b = mesg.getUnencryptedPreMasterSecret();
        return new SecretKeySpec(b, "TlsPremasterSecret");
    }
    
    private SecretKey clientKeyExchange(final DHClientKeyExchange mesg) throws IOException {
        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        final BigInteger publicKeyValue = mesg.getClientPublicKey();
        this.dh.checkConstraints(this.algorithmConstraints, publicKeyValue);
        return this.dh.getAgreedSecret(publicKeyValue, false);
    }
    
    private SecretKey clientKeyExchange(final ECDHClientKeyExchange mesg) throws IOException {
        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        final byte[] publicPoint = mesg.getEncodedPoint();
        this.ecdh.checkConstraints(this.algorithmConstraints, publicPoint);
        return this.ecdh.getAgreedSecret(publicPoint);
    }
    
    private void clientCertificateVerify(final HandshakeMessage.CertificateVerify mesg) throws IOException {
        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
            final SignatureAndHashAlgorithm signAlg = mesg.getPreferableSignatureAlgorithm();
            if (signAlg == null) {
                throw new SSLHandshakeException("Illegal CertificateVerify message");
            }
            final String hashAlg = SignatureAndHashAlgorithm.getHashAlgorithmName(signAlg);
            if (hashAlg == null || hashAlg.length() == 0) {
                throw new SSLHandshakeException("No supported hash algorithm");
            }
        }
        try {
            final PublicKey publicKey = this.session.getPeerCertificates()[0].getPublicKey();
            final boolean valid = mesg.verify(this.protocolVersion, this.handshakeHash, publicKey, this.session.getMasterSecret());
            if (!valid) {
                this.fatalSE((byte)42, "certificate verify message signature error");
            }
        }
        catch (final GeneralSecurityException e) {
            this.fatalSE((byte)42, "certificate verify format error", e);
        }
        this.needClientVerify = false;
    }
    
    private void clientFinished(final HandshakeMessage.Finished mesg) throws IOException {
        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        if (this.doClientAuth == 2) {
            this.session.getPeerPrincipal();
        }
        if (this.needClientVerify) {
            this.fatalSE((byte)40, "client did not send certificate verify message");
        }
        final boolean verified = mesg.verify(this.handshakeHash, 1, this.session.getMasterSecret());
        if (!verified) {
            this.fatalSE((byte)40, "client 'finished' message doesn't verify");
        }
        if (this.secureRenegotiation) {
            this.clientVerifyData = mesg.getVerifyData();
        }
        if (!this.resumingSession) {
            this.input.digestNow();
            this.sendChangeCipherAndFinish(true);
        }
        else {
            this.handshakeFinished = true;
        }
        this.session.setLastAccessedTime(System.currentTimeMillis());
        if (!this.resumingSession && this.session.isRejoinable()) {
            ((SSLSessionContextImpl)this.sslContext.engineGetServerSessionContext()).put(this.session);
            if (ServerHandshaker.debug != null && Debug.isOn("session")) {
                System.out.println("%% Cached server session: " + this.session);
            }
        }
        else if (!this.resumingSession && ServerHandshaker.debug != null && Debug.isOn("session")) {
            System.out.println("%% Didn't cache non-resumable server session: " + this.session);
        }
    }
    
    private void sendChangeCipherAndFinish(final boolean finishedTag) throws IOException {
        this.output.flush();
        final HandshakeMessage.Finished mesg = new HandshakeMessage.Finished(this.protocolVersion, this.handshakeHash, 2, this.session.getMasterSecret(), this.cipherSuite);
        this.sendChangeCipherSpec(mesg, finishedTag);
        if (this.secureRenegotiation) {
            this.serverVerifyData = mesg.getVerifyData();
        }
    }
    
    @Override
    HandshakeMessage getKickstartMessage() {
        return new HandshakeMessage.HelloRequest();
    }
    
    @Override
    void handshakeAlert(final byte description) throws SSLProtocolException {
        final String message = Alerts.alertDescription(description);
        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
            System.out.println("SSL -- handshake alert:  " + message);
        }
        if (description == 41 && this.doClientAuth == 1) {
            return;
        }
        throw new SSLProtocolException("handshake alert: " + message);
    }
    
    private SecretKey clientKeyExchange(final RSAClientKeyExchange mesg) throws IOException {
        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        return mesg.preMaster;
    }
    
    private void clientCertificate(final HandshakeMessage.CertificateMsg mesg) throws IOException {
        if (ServerHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        final X509Certificate[] peerCerts = mesg.getCertificateChain();
        if (peerCerts.length == 0) {
            if (this.doClientAuth == 1) {
                return;
            }
            this.fatalSE((byte)42, "null cert chain");
        }
        final X509TrustManager tm = this.sslContext.getX509TrustManager();
        try {
            final PublicKey key = peerCerts[0].getPublicKey();
            final String keyAlgorithm = key.getAlgorithm();
            String authType;
            if (keyAlgorithm.equals("RSA")) {
                authType = "RSA";
            }
            else if (keyAlgorithm.equals("DSA")) {
                authType = "DSA";
            }
            else if (keyAlgorithm.equals("EC")) {
                authType = "EC";
            }
            else {
                authType = "UNKNOWN";
            }
            if (!(tm instanceof X509ExtendedTrustManager)) {
                throw new CertificateException("Improper X509TrustManager implementation");
            }
            if (this.conn != null) {
                ((X509ExtendedTrustManager)tm).checkClientTrusted(peerCerts.clone(), authType, this.conn);
            }
            else {
                ((X509ExtendedTrustManager)tm).checkClientTrusted(peerCerts.clone(), authType, this.engine);
            }
        }
        catch (final CertificateException e) {
            this.fatalSE((byte)46, e);
        }
        this.needClientVerify = true;
        this.session.setPeerCertificates(peerCerts);
    }
    
    static {
        legacyAlgorithmConstraints = new LegacyAlgorithmConstraints("jdk.tls.legacyAlgorithms", new SSLAlgorithmDecomposer());
        final String property = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jdk.tls.ephemeralDHKeySize"));
        if (property == null || property.length() == 0) {
            useLegacyEphemeralDHKeys = false;
            useSmartEphemeralDHKeys = false;
            customizedDHKeySize = -1;
        }
        else if ("matched".equals(property)) {
            useLegacyEphemeralDHKeys = false;
            useSmartEphemeralDHKeys = true;
            customizedDHKeySize = -1;
        }
        else if ("legacy".equals(property)) {
            useLegacyEphemeralDHKeys = true;
            useSmartEphemeralDHKeys = false;
            customizedDHKeySize = -1;
        }
        else {
            useLegacyEphemeralDHKeys = false;
            useSmartEphemeralDHKeys = false;
            try {
                customizedDHKeySize = Integer.parseUnsignedInt(property);
                if (ServerHandshaker.customizedDHKeySize < 1024 || ServerHandshaker.customizedDHKeySize > 8192 || (ServerHandshaker.customizedDHKeySize & 0x3F) != 0x0) {
                    throw new IllegalArgumentException("Unsupported customized DH key size: " + ServerHandshaker.customizedDHKeySize + ". The key size must be multiple of 64, and can only range from 1024 to 8192 (inclusive)");
                }
            }
            catch (final NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid system property jdk.tls.ephemeralDHKeySize");
            }
        }
    }
}
