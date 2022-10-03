package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.HashSet;
import javax.security.auth.x500.X500Principal;
import java.security.cert.CertificateParsingException;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.crypto.SecretKey;
import java.security.spec.ECParameterSpec;
import javax.net.ssl.X509ExtendedKeyManager;
import java.security.PrivateKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.util.ArrayList;
import java.security.interfaces.ECPublicKey;
import java.security.Key;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.util.Iterator;
import java.util.Set;
import javax.net.ssl.SSLException;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;
import java.security.MessageDigest;
import java.io.IOException;
import java.util.Collection;
import javax.net.ssl.SSLHandshakeException;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import javax.net.ssl.SSLProtocolException;
import java.util.Collections;
import java.security.cert.X509Certificate;
import javax.net.ssl.SNIServerName;
import java.util.List;
import java.math.BigInteger;
import java.security.PublicKey;

final class ClientHandshaker extends Handshaker
{
    private static final int ALTNAME_DNS = 2;
    private static final int ALTNAME_IP = 7;
    private PublicKey serverKey;
    private PublicKey ephemeralServerKey;
    private BigInteger serverDH;
    private DHCrypt dh;
    private ECDHCrypt ecdh;
    private HandshakeMessage.CertificateRequest certRequest;
    private boolean serverKeyExchangeReceived;
    private ProtocolVersion maxProtocolVersion;
    private static final boolean enableSNIExtension;
    private static final boolean allowUnsafeServerCertChange;
    private boolean alpnActive;
    private List<SNIServerName> requestedServerNames;
    private boolean serverNamesAccepted;
    private X509Certificate[] reservedServerCerts;
    
    ClientHandshaker(final SSLSocketImpl socket, final SSLContextImpl context, final ProtocolList enabledProtocols, final ProtocolVersion activeProtocolVersion, final boolean isInitialHandshake, final boolean secureRenegotiation, final byte[] clientVerifyData, final byte[] serverVerifyData) {
        super(socket, context, enabledProtocols, true, true, activeProtocolVersion, isInitialHandshake, secureRenegotiation, clientVerifyData, serverVerifyData);
        this.alpnActive = false;
        this.requestedServerNames = Collections.emptyList();
        this.serverNamesAccepted = false;
        this.reservedServerCerts = null;
    }
    
    ClientHandshaker(final SSLEngineImpl engine, final SSLContextImpl context, final ProtocolList enabledProtocols, final ProtocolVersion activeProtocolVersion, final boolean isInitialHandshake, final boolean secureRenegotiation, final byte[] clientVerifyData, final byte[] serverVerifyData) {
        super(engine, context, enabledProtocols, true, true, activeProtocolVersion, isInitialHandshake, secureRenegotiation, clientVerifyData, serverVerifyData);
        this.alpnActive = false;
        this.requestedServerNames = Collections.emptyList();
        this.serverNamesAccepted = false;
        this.reservedServerCerts = null;
    }
    
    @Override
    void processMessage(final byte type, final int messageLen) throws IOException {
        final List<Byte> ignoredOptStates = this.handshakeState.check(type);
        Label_1086: {
            switch (type) {
                case 0: {
                    final HandshakeMessage.HelloRequest helloRequest = new HandshakeMessage.HelloRequest(this.input);
                    this.handshakeState.update(helloRequest, this.resumingSession);
                    this.serverHelloRequest(helloRequest);
                    break;
                }
                case 2: {
                    final HandshakeMessage.ServerHello serverHello = new HandshakeMessage.ServerHello(this.input, messageLen);
                    this.serverHello(serverHello);
                    this.handshakeState.update(serverHello, this.resumingSession);
                    break;
                }
                case 11: {
                    if (this.keyExchange == CipherSuite.KeyExchange.K_DH_ANON || this.keyExchange == CipherSuite.KeyExchange.K_ECDH_ANON || this.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
                        this.fatalSE((byte)10, "unexpected server cert chain");
                    }
                    final HandshakeMessage.CertificateMsg certificateMsg = new HandshakeMessage.CertificateMsg(this.input);
                    this.handshakeState.update(certificateMsg, this.resumingSession);
                    this.serverCertificate(certificateMsg);
                    this.serverKey = this.session.getPeerCertificates()[0].getPublicKey();
                    break;
                }
                case 12: {
                    this.serverKeyExchangeReceived = true;
                    switch (this.keyExchange) {
                        case K_RSA_EXPORT: {
                            if (this.serverKey == null) {
                                throw new SSLProtocolException("Server did not send certificate message");
                            }
                            if (!(this.serverKey instanceof RSAPublicKey)) {
                                throw new SSLProtocolException("Protocol violation: the certificate type must be appropriate for the selected cipher suite's key exchange algorithm");
                            }
                            if (JsseJce.getRSAKeyLength(this.serverKey) <= 512) {
                                throw new SSLProtocolException("Protocol violation: server sent a server key exchange message for key exchange " + this.keyExchange + " when the public key in the server certificate is less than or equal to 512 bits in length");
                            }
                            try {
                                final HandshakeMessage.RSA_ServerKeyExchange rsaSrvKeyExchange = new HandshakeMessage.RSA_ServerKeyExchange(this.input);
                                this.handshakeState.update(rsaSrvKeyExchange, this.resumingSession);
                                this.serverKeyExchange(rsaSrvKeyExchange);
                            }
                            catch (final GeneralSecurityException e) {
                                Handshaker.throwSSLException("Server key", e);
                            }
                            break Label_1086;
                        }
                        case K_DH_ANON: {
                            try {
                                final HandshakeMessage.DH_ServerKeyExchange dhSrvKeyExchange = new HandshakeMessage.DH_ServerKeyExchange(this.input, this.protocolVersion);
                                this.handshakeState.update(dhSrvKeyExchange, this.resumingSession);
                                this.serverKeyExchange(dhSrvKeyExchange);
                            }
                            catch (final GeneralSecurityException e) {
                                Handshaker.throwSSLException("Server key", e);
                            }
                            break Label_1086;
                        }
                        case K_DHE_DSS:
                        case K_DHE_RSA: {
                            try {
                                final HandshakeMessage.DH_ServerKeyExchange dhSrvKeyExchange = new HandshakeMessage.DH_ServerKeyExchange(this.input, this.serverKey, this.clnt_random.random_bytes, this.svr_random.random_bytes, messageLen, this.getLocalSupportedSignAlgs(), this.protocolVersion);
                                this.handshakeState.update(dhSrvKeyExchange, this.resumingSession);
                                this.serverKeyExchange(dhSrvKeyExchange);
                            }
                            catch (final GeneralSecurityException e) {
                                Handshaker.throwSSLException("Server key", e);
                            }
                            break Label_1086;
                        }
                        case K_ECDHE_ECDSA:
                        case K_ECDHE_RSA:
                        case K_ECDH_ANON: {
                            try {
                                final HandshakeMessage.ECDH_ServerKeyExchange ecdhSrvKeyExchange = new HandshakeMessage.ECDH_ServerKeyExchange(this.input, this.serverKey, this.clnt_random.random_bytes, this.svr_random.random_bytes, this.getLocalSupportedSignAlgs(), this.protocolVersion);
                                this.handshakeState.update(ecdhSrvKeyExchange, this.resumingSession);
                                this.serverKeyExchange(ecdhSrvKeyExchange);
                            }
                            catch (final GeneralSecurityException e) {
                                Handshaker.throwSSLException("Server key", e);
                            }
                            break Label_1086;
                        }
                        case K_RSA:
                        case K_DH_RSA:
                        case K_DH_DSS:
                        case K_ECDH_ECDSA:
                        case K_ECDH_RSA: {
                            throw new SSLProtocolException("Protocol violation: server sent a server key exchange message for key exchange " + this.keyExchange);
                        }
                        case K_KRB5:
                        case K_KRB5_EXPORT: {
                            throw new SSLProtocolException("unexpected receipt of server key exchange algorithm");
                        }
                        default: {
                            throw new SSLProtocolException("unsupported key exchange algorithm = " + this.keyExchange);
                        }
                    }
                    break;
                }
                case 13: {
                    if (this.keyExchange == CipherSuite.KeyExchange.K_DH_ANON || this.keyExchange == CipherSuite.KeyExchange.K_ECDH_ANON) {
                        throw new SSLHandshakeException("Client authentication requested for anonymous cipher suite.");
                    }
                    if (this.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
                        throw new SSLHandshakeException("Client certificate requested for kerberos cipher suite.");
                    }
                    this.certRequest = new HandshakeMessage.CertificateRequest(this.input, this.protocolVersion);
                    if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
                        this.certRequest.print(System.out);
                    }
                    this.handshakeState.update(this.certRequest, this.resumingSession);
                    if (this.protocolVersion.v < ProtocolVersion.TLS12.v) {
                        break;
                    }
                    final Collection<SignatureAndHashAlgorithm> peerSignAlgs = this.certRequest.getSignAlgorithms();
                    if (peerSignAlgs == null || peerSignAlgs.isEmpty()) {
                        throw new SSLHandshakeException("No peer supported signature algorithms");
                    }
                    final Collection<SignatureAndHashAlgorithm> supportedPeerSignAlgs = SignatureAndHashAlgorithm.getSupportedAlgorithms(this.algorithmConstraints, peerSignAlgs);
                    if (supportedPeerSignAlgs.isEmpty()) {
                        throw new SSLHandshakeException("No supported signature and hash algorithm in common");
                    }
                    this.setPeerSupportedSignAlgs(supportedPeerSignAlgs);
                    this.session.setPeerSupportedSignatureAlgorithms(supportedPeerSignAlgs);
                    break;
                }
                case 14: {
                    final HandshakeMessage.ServerHelloDone serverHelloDone = new HandshakeMessage.ServerHelloDone(this.input);
                    this.handshakeState.update(serverHelloDone, this.resumingSession);
                    this.serverHelloDone(serverHelloDone);
                    break;
                }
                case 20: {
                    final HandshakeMessage.Finished serverFinished = new HandshakeMessage.Finished(this.protocolVersion, this.input, this.cipherSuite);
                    this.handshakeState.update(serverFinished, this.resumingSession);
                    this.serverFinished(serverFinished);
                    break;
                }
                default: {
                    throw new SSLProtocolException("Illegal client handshake msg, " + type);
                }
            }
        }
    }
    
    private void serverHelloRequest(final HandshakeMessage.HelloRequest mesg) throws IOException {
        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        if (!this.clientHelloDelivered) {
            if (!this.secureRenegotiation && !ClientHandshaker.allowUnsafeRenegotiation) {
                if (this.activeProtocolVersion.v >= ProtocolVersion.TLS10.v) {
                    this.warningSE((byte)100);
                    this.invalidated = true;
                }
                else {
                    this.fatalSE((byte)40, "Renegotiation is not allowed");
                }
            }
            else {
                if (!this.secureRenegotiation && ClientHandshaker.debug != null && Debug.isOn("handshake")) {
                    System.out.println("Warning: continue with insecure renegotiation");
                }
                this.kickstart();
            }
        }
    }
    
    private void serverHello(final HandshakeMessage.ServerHello mesg) throws IOException {
        this.serverKeyExchangeReceived = false;
        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        final ProtocolVersion mesgVersion = mesg.protocolVersion;
        if (!this.isNegotiable(mesgVersion)) {
            throw new SSLHandshakeException("Server chose " + mesgVersion + ", but that protocol version is not enabled or not supported by the client.");
        }
        this.handshakeHash.protocolDetermined(mesgVersion);
        this.setVersion(mesgVersion);
        final RenegotiationInfoExtension serverHelloRI = (RenegotiationInfoExtension)mesg.extensions.get(ExtensionType.EXT_RENEGOTIATION_INFO);
        if (serverHelloRI != null) {
            if (this.isInitialHandshake) {
                if (!serverHelloRI.isEmpty()) {
                    this.fatalSE((byte)40, "The renegotiation_info field is not empty");
                }
                this.secureRenegotiation = true;
            }
            else {
                if (!this.secureRenegotiation) {
                    this.fatalSE((byte)40, "Unexpected renegotiation indication extension");
                }
                final byte[] verifyData = new byte[this.clientVerifyData.length + this.serverVerifyData.length];
                System.arraycopy(this.clientVerifyData, 0, verifyData, 0, this.clientVerifyData.length);
                System.arraycopy(this.serverVerifyData, 0, verifyData, this.clientVerifyData.length, this.serverVerifyData.length);
                if (!MessageDigest.isEqual(verifyData, serverHelloRI.getRenegotiatedConnection())) {
                    this.fatalSE((byte)40, "Incorrect verify data in ServerHello renegotiation_info message");
                }
            }
        }
        else if (this.isInitialHandshake) {
            if (!ClientHandshaker.allowLegacyHelloMessages) {
                this.fatalSE((byte)40, "Failed to negotiate the use of secure renegotiation");
            }
            this.secureRenegotiation = false;
            if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
                System.out.println("Warning: No renegotiation indication extension in ServerHello");
            }
        }
        else if (this.secureRenegotiation) {
            this.fatalSE((byte)40, "No renegotiation indication extension");
        }
        this.svr_random = mesg.svr_random;
        if (!this.isNegotiable(mesg.cipherSuite)) {
            this.fatalSE((byte)47, "Server selected improper ciphersuite " + mesg.cipherSuite);
        }
        this.setCipherSuite(mesg.cipherSuite);
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
            this.handshakeHash.setFinishedAlg(this.cipherSuite.prfAlg.getPRFHashAlg());
        }
        if (mesg.compression_method != 0) {
            this.fatalSE((byte)47, "compression type not supported, " + mesg.compression_method);
        }
        if (this.session != null) {
            if (this.session.getSessionId().equals(mesg.sessionId)) {
                final CipherSuite sessionSuite = this.session.getSuite();
                if (this.cipherSuite != sessionSuite) {
                    throw new SSLProtocolException("Server returned wrong cipher suite for session");
                }
                final ProtocolVersion sessionVersion = this.session.getProtocolVersion();
                if (this.protocolVersion != sessionVersion) {
                    throw new SSLProtocolException("Server resumed session with wrong protocol version");
                }
                if (sessionSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || sessionSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
                    final Principal localPrincipal = this.session.getLocalPrincipal();
                    Subject subject = null;
                    try {
                        subject = AccessController.doPrivileged((PrivilegedExceptionAction<Subject>)new PrivilegedExceptionAction<Subject>() {
                            @Override
                            public Subject run() throws Exception {
                                return Krb5Helper.getClientSubject(ClientHandshaker.this.getAccSE());
                            }
                        });
                    }
                    catch (final PrivilegedActionException e) {
                        subject = null;
                        if (ClientHandshaker.debug != null && Debug.isOn("session")) {
                            System.out.println("Attempt to obtain subject failed!");
                        }
                    }
                    if (subject == null) {
                        if (ClientHandshaker.debug != null && Debug.isOn("session")) {
                            System.out.println("Kerberos credentials are not present in the current Subject; check if  javax.security.auth.useSubjectAsCreds system property has been set to false");
                        }
                        throw new SSLProtocolException("Server resumed session with no subject");
                    }
                    final Set<Principal> principals = subject.getPrincipals(Principal.class);
                    if (!principals.contains(localPrincipal)) {
                        throw new SSLProtocolException("Server resumed session with wrong subject identity");
                    }
                    if (ClientHandshaker.debug != null && Debug.isOn("session")) {
                        System.out.println("Subject identity is same");
                    }
                }
                this.resumingSession = true;
                this.calculateConnectionKeys(this.session.getMasterSecret());
                if (ClientHandshaker.debug != null && Debug.isOn("session")) {
                    System.out.println("%% Server resumed " + this.session);
                }
            }
            else {
                if (this.isInitialHandshake) {
                    this.session.invalidate();
                }
                this.session = null;
                if (!this.enableNewSession) {
                    throw new SSLException("New session creation is disabled");
                }
            }
        }
        final ExtendedMasterSecretExtension extendedMasterSecretExt = (ExtendedMasterSecretExtension)mesg.extensions.get(ExtensionType.EXT_EXTENDED_MASTER_SECRET);
        if (extendedMasterSecretExt != null) {
            if (!ClientHandshaker.useExtendedMasterSecret || mesgVersion.v < ProtocolVersion.TLS10.v || !this.requestedToUseEMS) {
                this.fatalSE((byte)110, "Server sent the extended_master_secret extension improperly");
            }
            if (this.resumingSession && this.session != null && !this.session.getUseExtendedMasterSecret()) {
                this.fatalSE((byte)110, "Server sent an unexpected extended_master_secret extension on session resumption");
            }
        }
        else {
            if (ClientHandshaker.useExtendedMasterSecret && !ClientHandshaker.allowLegacyMasterSecret) {
                this.fatalSE((byte)40, "Extended Master Secret extension is required");
            }
            if (this.resumingSession && this.session != null) {
                if (this.session.getUseExtendedMasterSecret()) {
                    this.fatalSE((byte)40, "Missing Extended Master Secret extension on session resumption");
                }
                else if (ClientHandshaker.useExtendedMasterSecret && !ClientHandshaker.allowLegacyResumption) {
                    this.fatalSE((byte)40, "Extended Master Secret extension is required");
                }
            }
        }
        final ALPNExtension serverHelloALPN = (ALPNExtension)mesg.extensions.get(ExtensionType.EXT_ALPN);
        if (serverHelloALPN != null) {
            if (!this.alpnActive) {
                this.fatalSE((byte)110, "Server sent " + ExtensionType.EXT_ALPN + " extension when not requested by client");
            }
            final List<String> protocols = serverHelloALPN.getPeerAPs();
            final String p;
            if (protocols.size() == 1 && !(p = protocols.get(0)).isEmpty()) {
                int i;
                for (i = 0; i < this.localApl.length && !this.localApl[i].equals(p); ++i) {}
                if (i == this.localApl.length) {
                    this.fatalSE((byte)40, "Server has selected an application protocol name which was not offered by the client: " + p);
                }
                this.applicationProtocol = p;
            }
            else {
                this.fatalSE((byte)40, "Incorrect data in ServerHello " + ExtensionType.EXT_ALPN + " message");
            }
        }
        else {
            this.applicationProtocol = "";
        }
        if (this.resumingSession && this.session != null) {
            this.setHandshakeSessionSE(this.session);
            if (this.isInitialHandshake) {
                this.session.setAsSessionResumption(true);
            }
            return;
        }
        for (final HelloExtension ext : mesg.extensions.list()) {
            final ExtensionType type = ext.type;
            if (type == ExtensionType.EXT_SERVER_NAME) {
                this.serverNamesAccepted = true;
            }
            else {
                if (type == ExtensionType.EXT_ELLIPTIC_CURVES || type == ExtensionType.EXT_EC_POINT_FORMATS || type == ExtensionType.EXT_SERVER_NAME || type == ExtensionType.EXT_ALPN || type == ExtensionType.EXT_RENEGOTIATION_INFO || type == ExtensionType.EXT_EXTENDED_MASTER_SECRET) {
                    continue;
                }
                this.fatalSE((byte)110, "Server sent an unsupported extension: " + type);
            }
        }
        (this.session = new SSLSessionImpl(this.protocolVersion, this.cipherSuite, this.getLocalSupportedSignAlgs(), mesg.sessionId, this.getHostSE(), this.getPortSE(), extendedMasterSecretExt != null, this.getEndpointIdentificationAlgorithmSE())).setRequestedServerNames(this.requestedServerNames);
        this.setHandshakeSessionSE(this.session);
        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
            System.out.println("** " + this.cipherSuite);
        }
    }
    
    private void serverKeyExchange(final HandshakeMessage.RSA_ServerKeyExchange mesg) throws IOException, GeneralSecurityException {
        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        if (!mesg.verify(this.serverKey, this.clnt_random, this.svr_random)) {
            this.fatalSE((byte)40, "server key exchange invalid");
        }
        this.ephemeralServerKey = mesg.getPublicKey();
        if (!this.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), this.ephemeralServerKey)) {
            throw new SSLHandshakeException("RSA ServerKeyExchange does not comply to algorithm constraints");
        }
    }
    
    private void serverKeyExchange(final HandshakeMessage.DH_ServerKeyExchange mesg) throws IOException {
        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        this.dh = new DHCrypt(mesg.getModulus(), mesg.getBase(), this.sslContext.getSecureRandom());
        this.serverDH = mesg.getServerPublicKey();
        this.dh.checkConstraints(this.algorithmConstraints, this.serverDH);
    }
    
    private void serverKeyExchange(final HandshakeMessage.ECDH_ServerKeyExchange mesg) throws IOException {
        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        final ECPublicKey key = mesg.getPublicKey();
        this.ecdh = new ECDHCrypt(key.getParams(), this.sslContext.getSecureRandom());
        this.ephemeralServerKey = key;
        if (!this.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), this.ephemeralServerKey)) {
            throw new SSLHandshakeException("ECDH ServerKeyExchange does not comply to algorithm constraints");
        }
    }
    
    private void serverHelloDone(final HandshakeMessage.ServerHelloDone mesg) throws IOException {
        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        this.input.digestNow();
        PrivateKey signingKey = null;
        if (this.certRequest != null) {
            final X509ExtendedKeyManager km = this.sslContext.getX509KeyManager();
            final ArrayList<String> keytypesTmp = new ArrayList<String>(4);
            for (int i = 0; i < this.certRequest.types.length; ++i) {
                String typeName = null;
                switch (this.certRequest.types[i]) {
                    case 1: {
                        typeName = "RSA";
                        break;
                    }
                    case 2: {
                        typeName = "DSA";
                        break;
                    }
                    case 64: {
                        typeName = (JsseJce.isEcAvailable() ? "EC" : null);
                        break;
                    }
                    default: {
                        typeName = null;
                        break;
                    }
                }
                if (typeName != null && !keytypesTmp.contains(typeName)) {
                    keytypesTmp.add(typeName);
                }
            }
            String alias = null;
            final int keytypesTmpSize = keytypesTmp.size();
            if (keytypesTmpSize != 0) {
                final String[] keytypes = keytypesTmp.toArray(new String[keytypesTmpSize]);
                if (this.conn != null) {
                    alias = km.chooseClientAlias(keytypes, this.certRequest.getAuthorities(), this.conn);
                }
                else {
                    alias = km.chooseEngineClientAlias(keytypes, this.certRequest.getAuthorities(), this.engine);
                }
            }
            HandshakeMessage.CertificateMsg m1 = null;
            if (alias != null) {
                final X509Certificate[] certs = km.getCertificateChain(alias);
                if (certs != null && certs.length != 0) {
                    final PublicKey publicKey = certs[0].getPublicKey();
                    if (publicKey != null) {
                        m1 = new HandshakeMessage.CertificateMsg(certs);
                        signingKey = km.getPrivateKey(alias);
                        this.session.setLocalPrivateKey(signingKey);
                        this.session.setLocalCertificates(certs);
                    }
                }
            }
            if (m1 == null) {
                if (this.protocolVersion.v >= ProtocolVersion.TLS10.v) {
                    m1 = new HandshakeMessage.CertificateMsg(new X509Certificate[0]);
                }
                else {
                    this.warningSE((byte)41);
                }
                if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
                    System.out.println("Warning: no suitable certificate found - continuing without client authentication");
                }
            }
            if (m1 != null) {
                if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
                    m1.print(System.out);
                }
                m1.write(this.output);
                this.handshakeState.update(m1, this.resumingSession);
            }
        }
        HandshakeMessage m2 = null;
        switch (this.keyExchange) {
            case K_RSA_EXPORT:
            case K_RSA: {
                if (this.serverKey == null) {
                    throw new SSLProtocolException("Server did not send certificate message");
                }
                if (!(this.serverKey instanceof RSAPublicKey)) {
                    throw new SSLProtocolException("Server certificate does not include an RSA key");
                }
                PublicKey key;
                if (this.keyExchange == CipherSuite.KeyExchange.K_RSA) {
                    key = this.serverKey;
                }
                else if (JsseJce.getRSAKeyLength(this.serverKey) <= 512) {
                    key = this.serverKey;
                }
                else {
                    if (this.ephemeralServerKey == null) {
                        throw new SSLProtocolException("Server did not send a RSA_EXPORT Server Key Exchange message");
                    }
                    key = this.ephemeralServerKey;
                }
                m2 = new RSAClientKeyExchange(this.protocolVersion, this.maxProtocolVersion, this.sslContext.getSecureRandom(), key);
                break;
            }
            case K_DH_RSA:
            case K_DH_DSS: {
                m2 = new DHClientKeyExchange();
                break;
            }
            case K_DH_ANON:
            case K_DHE_DSS:
            case K_DHE_RSA: {
                if (this.dh == null) {
                    throw new SSLProtocolException("Server did not send a DH Server Key Exchange message");
                }
                m2 = new DHClientKeyExchange(this.dh.getPublicKey());
                break;
            }
            case K_ECDHE_ECDSA:
            case K_ECDHE_RSA:
            case K_ECDH_ANON: {
                if (this.ecdh == null) {
                    throw new SSLProtocolException("Server did not send a ECDH Server Key Exchange message");
                }
                m2 = new ECDHClientKeyExchange(this.ecdh.getPublicKey());
                break;
            }
            case K_ECDH_ECDSA:
            case K_ECDH_RSA: {
                if (this.serverKey == null) {
                    throw new SSLProtocolException("Server did not send certificate message");
                }
                if (!(this.serverKey instanceof ECPublicKey)) {
                    throw new SSLProtocolException("Server certificate does not include an EC key");
                }
                final ECParameterSpec params = ((ECPublicKey)this.serverKey).getParams();
                this.ecdh = new ECDHCrypt(params, this.sslContext.getSecureRandom());
                m2 = new ECDHClientKeyExchange(this.ecdh.getPublicKey());
                break;
            }
            case K_KRB5:
            case K_KRB5_EXPORT: {
                String sniHostname = null;
                for (final SNIServerName serverName : this.requestedServerNames) {
                    if (serverName instanceof SNIHostName) {
                        sniHostname = ((SNIHostName)serverName).getAsciiName();
                        break;
                    }
                }
                KerberosClientKeyExchange kerberosMsg = null;
                if (sniHostname != null) {
                    try {
                        kerberosMsg = new KerberosClientKeyExchange(sniHostname, this.getAccSE(), this.protocolVersion, this.sslContext.getSecureRandom());
                    }
                    catch (final IOException e) {
                        if (this.serverNamesAccepted) {
                            throw e;
                        }
                        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
                            System.out.println("Warning, cannot use Server Name Indication: " + e.getMessage());
                        }
                    }
                }
                if (kerberosMsg == null) {
                    final String hostname = this.getHostSE();
                    if (hostname == null) {
                        throw new IOException("Hostname is required to use Kerberos cipher suites");
                    }
                    kerberosMsg = new KerberosClientKeyExchange(hostname, this.getAccSE(), this.protocolVersion, this.sslContext.getSecureRandom());
                }
                this.session.setPeerPrincipal(kerberosMsg.getPeerPrincipal());
                this.session.setLocalPrincipal(kerberosMsg.getLocalPrincipal());
                m2 = kerberosMsg;
                break;
            }
            default: {
                throw new RuntimeException("Unsupported key exchange: " + this.keyExchange);
            }
        }
        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
            m2.print(System.out);
        }
        m2.write(this.output);
        this.handshakeState.update(m2, this.resumingSession);
        this.output.doHashes();
        this.output.flush();
        SecretKey preMasterSecret = null;
        switch (this.keyExchange) {
            case K_RSA_EXPORT:
            case K_RSA: {
                preMasterSecret = ((RSAClientKeyExchange)m2).preMaster;
                break;
            }
            case K_KRB5:
            case K_KRB5_EXPORT: {
                final byte[] secretBytes = ((KerberosClientKeyExchange)m2).getUnencryptedPreMasterSecret();
                preMasterSecret = new SecretKeySpec(secretBytes, "TlsPremasterSecret");
                break;
            }
            case K_DH_ANON:
            case K_DHE_DSS:
            case K_DHE_RSA: {
                preMasterSecret = this.dh.getAgreedSecret(this.serverDH, true);
                break;
            }
            case K_ECDHE_ECDSA:
            case K_ECDHE_RSA:
            case K_ECDH_ANON: {
                preMasterSecret = this.ecdh.getAgreedSecret(this.ephemeralServerKey);
                break;
            }
            case K_ECDH_ECDSA:
            case K_ECDH_RSA: {
                preMasterSecret = this.ecdh.getAgreedSecret(this.serverKey);
                break;
            }
            default: {
                throw new IOException("Internal error: unknown key exchange " + this.keyExchange);
            }
        }
        this.calculateKeys(preMasterSecret, null);
        if (signingKey != null) {
            HandshakeMessage.CertificateVerify m3;
            try {
                SignatureAndHashAlgorithm preferableSignatureAlgorithm = null;
                if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                    preferableSignatureAlgorithm = SignatureAndHashAlgorithm.getPreferableAlgorithm(this.getPeerSupportedSignAlgs(), this.algorithmConstraints, signingKey.getAlgorithm(), signingKey);
                    if (preferableSignatureAlgorithm == null) {
                        throw new SSLHandshakeException("No supported signature algorithm");
                    }
                    final String hashAlg = SignatureAndHashAlgorithm.getHashAlgorithmName(preferableSignatureAlgorithm);
                    if (hashAlg == null || hashAlg.length() == 0) {
                        throw new SSLHandshakeException("No supported hash algorithm");
                    }
                }
                m3 = new HandshakeMessage.CertificateVerify(this.protocolVersion, this.handshakeHash, signingKey, this.session.getMasterSecret(), this.sslContext.getSecureRandom(), preferableSignatureAlgorithm);
            }
            catch (final GeneralSecurityException e2) {
                this.fatalSE((byte)40, "Error signing certificate verify", e2);
                m3 = null;
            }
            if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
                m3.print(System.out);
            }
            m3.write(this.output);
            this.handshakeState.update(m3, this.resumingSession);
            this.output.doHashes();
        }
        this.sendChangeCipherAndFinish(false);
    }
    
    private void serverFinished(final HandshakeMessage.Finished mesg) throws IOException {
        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        final boolean verified = mesg.verify(this.handshakeHash, 2, this.session.getMasterSecret());
        if (!verified) {
            this.fatalSE((byte)47, "server 'finished' message doesn't verify");
        }
        if (this.secureRenegotiation) {
            this.serverVerifyData = mesg.getVerifyData();
        }
        if (!this.isInitialHandshake) {
            this.session.setAsSessionResumption(false);
        }
        if (this.resumingSession) {
            this.input.digestNow();
            this.sendChangeCipherAndFinish(true);
        }
        else {
            this.handshakeFinished = true;
        }
        this.session.setLastAccessedTime(System.currentTimeMillis());
        if (!this.resumingSession) {
            if (this.session.isRejoinable()) {
                ((SSLSessionContextImpl)this.sslContext.engineGetClientSessionContext()).put(this.session);
                if (ClientHandshaker.debug != null && Debug.isOn("session")) {
                    System.out.println("%% Cached client session: " + this.session);
                }
            }
            else if (ClientHandshaker.debug != null && Debug.isOn("session")) {
                System.out.println("%% Didn't cache non-resumable client session: " + this.session);
            }
        }
    }
    
    private void sendChangeCipherAndFinish(final boolean finishedTag) throws IOException {
        final HandshakeMessage.Finished mesg = new HandshakeMessage.Finished(this.protocolVersion, this.handshakeHash, 1, this.session.getMasterSecret(), this.cipherSuite);
        this.sendChangeCipherSpec(mesg, finishedTag);
        if (this.secureRenegotiation) {
            this.clientVerifyData = mesg.getVerifyData();
        }
    }
    
    @Override
    HandshakeMessage getKickstartMessage() throws SSLException {
        SessionId sessionId = new SessionId(new byte[0]);
        CipherSuiteList cipherSuites = this.getActiveCipherSuites();
        this.maxProtocolVersion = this.protocolVersion;
        this.session = ((SSLSessionContextImpl)this.sslContext.engineGetClientSessionContext()).get(this.getHostSE(), this.getPortSE());
        if (ClientHandshaker.debug != null && Debug.isOn("session")) {
            if (this.session != null) {
                System.out.println("%% Client cached " + this.session + (this.session.isRejoinable() ? "" : " (not rejoinable)"));
            }
            else {
                System.out.println("%% No cached client session");
            }
        }
        if (this.session != null) {
            if (!ClientHandshaker.allowUnsafeServerCertChange && this.session.isSessionResumption()) {
                try {
                    this.reservedServerCerts = (X509Certificate[])this.session.getPeerCertificates();
                }
                catch (final SSLPeerUnverifiedException ex) {}
            }
            if (!this.session.isRejoinable()) {
                this.session = null;
            }
        }
        if (this.session != null) {
            final CipherSuite sessionSuite = this.session.getSuite();
            final ProtocolVersion sessionVersion = this.session.getProtocolVersion();
            if (!this.isNegotiable(sessionSuite)) {
                if (ClientHandshaker.debug != null && Debug.isOn("session")) {
                    System.out.println("%% can't resume, unavailable cipher");
                }
                this.session = null;
            }
            if (this.session != null && !this.isNegotiable(sessionVersion)) {
                if (ClientHandshaker.debug != null && Debug.isOn("session")) {
                    System.out.println("%% can't resume, protocol disabled");
                }
                this.session = null;
            }
            if (this.session != null && ClientHandshaker.useExtendedMasterSecret) {
                final boolean isTLS10Plus = sessionVersion.v >= ProtocolVersion.TLS10.v;
                if (isTLS10Plus && !this.session.getUseExtendedMasterSecret() && !ClientHandshaker.allowLegacyResumption) {
                    this.session = null;
                }
                if (this.session != null && !ClientHandshaker.allowUnsafeServerCertChange) {
                    final String identityAlg = this.getEndpointIdentificationAlgorithmSE();
                    if (identityAlg == null || identityAlg.length() == 0) {
                        if (isTLS10Plus) {
                            if (!this.session.getUseExtendedMasterSecret()) {
                                this.session = null;
                            }
                        }
                        else {
                            this.session = null;
                        }
                    }
                }
            }
            final String identityAlg2 = this.getEndpointIdentificationAlgorithmSE();
            if (this.session != null && identityAlg2 != null) {
                final String sessionIdentityAlg = this.session.getEndpointIdentificationAlgorithm();
                if (!identityAlg2.equalsIgnoreCase(sessionIdentityAlg)) {
                    if (ClientHandshaker.debug != null && Debug.isOn("session")) {
                        System.out.println("%% can't resume, endpoint id algorithm does not match, requested: " + identityAlg2 + ", cached: " + sessionIdentityAlg);
                    }
                    this.session = null;
                }
            }
            if (this.session != null) {
                if (ClientHandshaker.debug != null && (Debug.isOn("handshake") || Debug.isOn("session"))) {
                    System.out.println("%% Try resuming " + this.session + " from port " + this.getLocalPortSE());
                }
                sessionId = this.session.getSessionId();
                this.setVersion(this.maxProtocolVersion = sessionVersion);
            }
            if (!this.enableNewSession) {
                if (this.session == null) {
                    throw new SSLHandshakeException("Can't reuse existing SSL client session");
                }
                final Collection<CipherSuite> cipherList = new ArrayList<CipherSuite>(2);
                cipherList.add(sessionSuite);
                if (!this.secureRenegotiation && cipherSuites.contains(CipherSuite.C_SCSV)) {
                    cipherList.add(CipherSuite.C_SCSV);
                }
                cipherSuites = new CipherSuiteList(cipherList);
            }
        }
        if (this.session == null && !this.enableNewSession) {
            throw new SSLHandshakeException("No existing session to resume");
        }
        if (this.secureRenegotiation && cipherSuites.contains(CipherSuite.C_SCSV)) {
            final Collection<CipherSuite> cipherList2 = new ArrayList<CipherSuite>(cipherSuites.size() - 1);
            for (final CipherSuite suite : cipherSuites.collection()) {
                if (suite != CipherSuite.C_SCSV) {
                    cipherList2.add(suite);
                }
            }
            cipherSuites = new CipherSuiteList(cipherList2);
        }
        boolean negotiable = false;
        for (final CipherSuite suite : cipherSuites.collection()) {
            if (this.isNegotiable(suite)) {
                negotiable = true;
                break;
            }
        }
        if (!negotiable) {
            throw new SSLHandshakeException("No negotiable cipher suite");
        }
        final HandshakeMessage.ClientHello clientHelloMessage = new HandshakeMessage.ClientHello(this.sslContext.getSecureRandom(), this.maxProtocolVersion, sessionId, cipherSuites);
        if (cipherSuites.containsEC()) {
            final EllipticCurvesExtension ece = EllipticCurvesExtension.createExtension(this.algorithmConstraints);
            if (ece != null) {
                clientHelloMessage.extensions.add(ece);
                clientHelloMessage.extensions.add(EllipticPointFormatsExtension.DEFAULT);
            }
        }
        if (this.maxProtocolVersion.v >= ProtocolVersion.TLS12.v) {
            final Collection<SignatureAndHashAlgorithm> localSignAlgs = this.getLocalSupportedSignAlgs();
            if (localSignAlgs.isEmpty()) {
                throw new SSLHandshakeException("No supported signature algorithm");
            }
            clientHelloMessage.addSignatureAlgorithmsExtension(localSignAlgs);
        }
        if (ClientHandshaker.useExtendedMasterSecret && this.maxProtocolVersion.v >= ProtocolVersion.TLS10.v && (this.session == null || this.session.getUseExtendedMasterSecret())) {
            clientHelloMessage.addExtendedMasterSecretExtension();
            this.requestedToUseEMS = true;
        }
        if (ClientHandshaker.enableSNIExtension) {
            if (this.session != null) {
                this.requestedServerNames = this.session.getRequestedServerNames();
            }
            else {
                this.requestedServerNames = this.serverNames;
            }
            if (!this.requestedServerNames.isEmpty()) {
                clientHelloMessage.addSNIExtension(this.requestedServerNames);
            }
        }
        if (this.localApl != null && this.localApl.length > 0) {
            clientHelloMessage.addALPNExtension(this.localApl);
            this.alpnActive = true;
        }
        this.clnt_random = clientHelloMessage.clnt_random;
        if (this.secureRenegotiation || !cipherSuites.contains(CipherSuite.C_SCSV)) {
            clientHelloMessage.addRenegotiationInfoExtension(this.clientVerifyData);
        }
        return clientHelloMessage;
    }
    
    @Override
    void handshakeAlert(final byte description) throws SSLProtocolException {
        final String message = Alerts.alertDescription(description);
        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
            System.out.println("SSL - handshake alert: " + message);
        }
        throw new SSLProtocolException("handshake alert:  " + message);
    }
    
    private void serverCertificate(final HandshakeMessage.CertificateMsg mesg) throws IOException {
        if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
            mesg.print(System.out);
        }
        final X509Certificate[] peerCerts = mesg.getCertificateChain();
        if (peerCerts.length == 0) {
            this.fatalSE((byte)42, "empty certificate chain");
        }
        if (this.reservedServerCerts != null && !this.session.getUseExtendedMasterSecret()) {
            final String identityAlg = this.getEndpointIdentificationAlgorithmSE();
            if ((identityAlg == null || identityAlg.length() == 0) && !isIdentityEquivalent(peerCerts[0], this.reservedServerCerts[0])) {
                this.fatalSE((byte)42, "server certificate change is restricted during renegotiation");
            }
        }
        final X509TrustManager tm = this.sslContext.getX509TrustManager();
        try {
            String keyExchangeString;
            if (this.keyExchange == CipherSuite.KeyExchange.K_RSA_EXPORT && !this.serverKeyExchangeReceived) {
                keyExchangeString = CipherSuite.KeyExchange.K_RSA.name;
            }
            else {
                keyExchangeString = this.keyExchange.name;
            }
            if (!(tm instanceof X509ExtendedTrustManager)) {
                throw new CertificateException("Improper X509TrustManager implementation");
            }
            if (this.conn != null) {
                ((X509ExtendedTrustManager)tm).checkServerTrusted(peerCerts.clone(), keyExchangeString, this.conn);
            }
            else {
                ((X509ExtendedTrustManager)tm).checkServerTrusted(peerCerts.clone(), keyExchangeString, this.engine);
            }
        }
        catch (final CertificateException e) {
            this.fatalSE((byte)46, e);
        }
        this.session.setPeerCertificates(peerCerts);
    }
    
    private static boolean isIdentityEquivalent(final X509Certificate thisCert, final X509Certificate prevCert) {
        if (thisCert.equals(prevCert)) {
            return true;
        }
        Collection<List<?>> thisSubjectAltNames = null;
        try {
            thisSubjectAltNames = thisCert.getSubjectAlternativeNames();
        }
        catch (final CertificateParsingException cpe) {
            if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
                System.out.println("Attempt to obtain subjectAltNames extension failed!");
            }
        }
        Collection<List<?>> prevSubjectAltNames = null;
        try {
            prevSubjectAltNames = prevCert.getSubjectAlternativeNames();
        }
        catch (final CertificateParsingException cpe2) {
            if (ClientHandshaker.debug != null && Debug.isOn("handshake")) {
                System.out.println("Attempt to obtain subjectAltNames extension failed!");
            }
        }
        if (thisSubjectAltNames != null && prevSubjectAltNames != null) {
            final Collection<String> thisSubAltIPAddrs = getSubjectAltNames(thisSubjectAltNames, 7);
            final Collection<String> prevSubAltIPAddrs = getSubjectAltNames(prevSubjectAltNames, 7);
            if (thisSubAltIPAddrs != null && prevSubAltIPAddrs != null && isEquivalent(thisSubAltIPAddrs, prevSubAltIPAddrs)) {
                return true;
            }
            final Collection<String> thisSubAltDnsNames = getSubjectAltNames(thisSubjectAltNames, 2);
            final Collection<String> prevSubAltDnsNames = getSubjectAltNames(prevSubjectAltNames, 2);
            if (thisSubAltDnsNames != null && prevSubAltDnsNames != null && isEquivalent(thisSubAltDnsNames, prevSubAltDnsNames)) {
                return true;
            }
        }
        final X500Principal thisSubject = thisCert.getSubjectX500Principal();
        final X500Principal prevSubject = prevCert.getSubjectX500Principal();
        final X500Principal thisIssuer = thisCert.getIssuerX500Principal();
        final X500Principal prevIssuer = prevCert.getIssuerX500Principal();
        return !thisSubject.getName().isEmpty() && !prevSubject.getName().isEmpty() && thisSubject.equals(prevSubject) && thisIssuer.equals(prevIssuer);
    }
    
    private static Collection<String> getSubjectAltNames(final Collection<List<?>> subjectAltNames, final int type) {
        HashSet<String> subAltDnsNames = null;
        for (final List<?> subjectAltName : subjectAltNames) {
            final int subjectAltNameType = (int)subjectAltName.get(0);
            if (subjectAltNameType == type) {
                final String subAltDnsName = (String)subjectAltName.get(1);
                if (subAltDnsName == null || subAltDnsName.isEmpty()) {
                    continue;
                }
                if (subAltDnsNames == null) {
                    subAltDnsNames = new HashSet<String>(subjectAltNames.size());
                }
                subAltDnsNames.add(subAltDnsName);
            }
        }
        return subAltDnsNames;
    }
    
    private static boolean isEquivalent(final Collection<String> thisSubAltNames, final Collection<String> prevSubAltNames) {
        for (final String thisSubAltName : thisSubAltNames) {
            for (final String prevSubAltName : prevSubAltNames) {
                if (thisSubAltName.equalsIgnoreCase(prevSubAltName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        enableSNIExtension = Debug.getBooleanProperty("jsse.enableSNIExtension", true);
        allowUnsafeServerCertChange = Debug.getBooleanProperty("jdk.tls.allowUnsafeServerCertChange", false);
    }
}
