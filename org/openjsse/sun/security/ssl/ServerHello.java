package org.openjsse.sun.security.ssl;

import java.security.AlgorithmConstraints;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Iterator;
import java.util.List;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.util.Set;
import java.util.LinkedList;
import java.util.Map;
import java.util.Collection;
import java.util.Arrays;
import javax.net.ssl.SSLException;
import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLHandshakeException;
import javax.crypto.SecretKey;

final class ServerHello
{
    static final SSLConsumer handshakeConsumer;
    static final HandshakeProducer t12HandshakeProducer;
    static final HandshakeProducer t13HandshakeProducer;
    static final HandshakeProducer hrrHandshakeProducer;
    static final HandshakeProducer hrrReproducer;
    private static final HandshakeConsumer t12HandshakeConsumer;
    private static final HandshakeConsumer t13HandshakeConsumer;
    private static final HandshakeConsumer d12HandshakeConsumer;
    private static final HandshakeConsumer d13HandshakeConsumer;
    private static final HandshakeConsumer t13HrrHandshakeConsumer;
    private static final HandshakeConsumer d13HrrHandshakeConsumer;
    
    private static void setUpPskKD(final HandshakeContext hc, final SecretKey psk) throws SSLHandshakeException {
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
            SSLLogger.fine("Using PSK to derive early secret", new Object[0]);
        }
        try {
            final CipherSuite.HashAlg hashAlg = hc.negotiatedCipherSuite.hashAlg;
            final HKDF hkdf = new HKDF(hashAlg.name);
            final byte[] zeros = new byte[hashAlg.hashLength];
            final SecretKey earlySecret = hkdf.extract(zeros, psk, "TlsEarlySecret");
            hc.handshakeKeyDerivation = new SSLSecretDerivation(hc, earlySecret);
        }
        catch (final GeneralSecurityException gse) {
            throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(gse);
        }
    }
    
    static {
        handshakeConsumer = new ServerHelloConsumer();
        t12HandshakeProducer = new T12ServerHelloProducer();
        t13HandshakeProducer = new T13ServerHelloProducer();
        hrrHandshakeProducer = new T13HelloRetryRequestProducer();
        hrrReproducer = new T13HelloRetryRequestReproducer();
        t12HandshakeConsumer = new T12ServerHelloConsumer();
        t13HandshakeConsumer = new T13ServerHelloConsumer();
        d12HandshakeConsumer = new T12ServerHelloConsumer();
        d13HandshakeConsumer = new T13ServerHelloConsumer();
        t13HrrHandshakeConsumer = new T13HelloRetryRequestConsumer();
        d13HrrHandshakeConsumer = new T13HelloRetryRequestConsumer();
    }
    
    static final class ServerHelloMessage extends SSLHandshake.HandshakeMessage
    {
        final ProtocolVersion serverVersion;
        final RandomCookie serverRandom;
        final SessionId sessionId;
        final CipherSuite cipherSuite;
        final byte compressionMethod;
        final SSLExtensions extensions;
        final ClientHello.ClientHelloMessage clientHello;
        final ByteBuffer handshakeRecord;
        
        ServerHelloMessage(final HandshakeContext context, final ProtocolVersion serverVersion, final SessionId sessionId, final CipherSuite cipherSuite, final RandomCookie serverRandom, final ClientHello.ClientHelloMessage clientHello) {
            super(context);
            this.serverVersion = serverVersion;
            this.serverRandom = serverRandom;
            this.sessionId = sessionId;
            this.cipherSuite = cipherSuite;
            this.compressionMethod = 0;
            this.extensions = new SSLExtensions(this);
            this.clientHello = clientHello;
            this.handshakeRecord = null;
        }
        
        ServerHelloMessage(final HandshakeContext context, final ByteBuffer m) throws IOException {
            super(context);
            this.handshakeRecord = m.duplicate();
            final byte major = m.get();
            final byte minor = m.get();
            this.serverVersion = ProtocolVersion.valueOf(major, minor);
            if (this.serverVersion == null) {
                throw context.conContext.fatal(Alert.PROTOCOL_VERSION, "Unsupported protocol version: " + ProtocolVersion.nameOf(major, minor));
            }
            this.serverRandom = new RandomCookie(m);
            this.sessionId = new SessionId(Record.getBytes8(m));
            try {
                this.sessionId.checkLength(this.serverVersion.id);
            }
            catch (final SSLProtocolException ex) {
                throw this.handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, ex);
            }
            final int cipherSuiteId = Record.getInt16(m);
            this.cipherSuite = CipherSuite.valueOf(cipherSuiteId);
            if (this.cipherSuite == null || !context.isNegotiable(this.cipherSuite)) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Server selected improper ciphersuite " + CipherSuite.nameOf(cipherSuiteId));
            }
            this.compressionMethod = m.get();
            if (this.compressionMethod != 0) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "compression type not supported, " + this.compressionMethod);
            }
            SSLExtension[] supportedExtensions;
            if (this.serverRandom.isHelloRetryRequest()) {
                supportedExtensions = context.sslConfig.getEnabledExtensions(SSLHandshake.HELLO_RETRY_REQUEST);
            }
            else {
                supportedExtensions = context.sslConfig.getEnabledExtensions(SSLHandshake.SERVER_HELLO);
            }
            if (m.hasRemaining()) {
                this.extensions = new SSLExtensions(this, m, supportedExtensions);
            }
            else {
                this.extensions = new SSLExtensions(this);
            }
            this.clientHello = null;
        }
        
        public SSLHandshake handshakeType() {
            return this.serverRandom.isHelloRetryRequest() ? SSLHandshake.HELLO_RETRY_REQUEST : SSLHandshake.SERVER_HELLO;
        }
        
        public int messageLength() {
            return 38 + this.sessionId.length() + this.extensions.length();
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            hos.putInt8(this.serverVersion.major);
            hos.putInt8(this.serverVersion.minor);
            hos.write(this.serverRandom.randomBytes);
            hos.putBytes8(this.sessionId.getId());
            hos.putInt8(this.cipherSuite.id >> 8 & 0xFF);
            hos.putInt8(this.cipherSuite.id & 0xFF);
            hos.putInt8(this.compressionMethod);
            this.extensions.send(hos);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"{0}\": '{'\n  \"server version\"      : \"{1}\",\n  \"random\"              : \"{2}\",\n  \"session id\"          : \"{3}\",\n  \"cipher suite\"        : \"{4}\",\n  \"compression methods\" : \"{5}\",\n  \"extensions\"          : [\n{6}\n  ]\n'}'", Locale.ENGLISH);
            final Object[] messageFields = { this.serverRandom.isHelloRetryRequest() ? "HelloRetryRequest" : "ServerHello", this.serverVersion.name, Utilities.toHexString(this.serverRandom.randomBytes), this.sessionId.toString(), this.cipherSuite.name + "(" + Utilities.byte16HexString(this.cipherSuite.id) + ")", Utilities.toHexString(this.compressionMethod), Utilities.indent(this.extensions.toString(), "    ") };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class T12ServerHelloProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ClientHello.ClientHelloMessage clientHello = (ClientHello.ClientHelloMessage)message;
            if (!shc.isResumption || shc.resumingSession == null) {
                if (!shc.sslConfig.enableSessionCreation) {
                    throw new SSLException("Not resumption, and no new session is allowed");
                }
                if (shc.localSupportedSignAlgs == null) {
                    shc.localSupportedSignAlgs = SignatureScheme.getSupportedAlgorithms(shc.sslConfig, shc.algorithmConstraints, shc.activeProtocols);
                }
                final SSLSessionImpl session = new SSLSessionImpl(shc, CipherSuite.C_NULL);
                session.setMaximumPacketSize(shc.sslConfig.maximumPacketSize);
                shc.handshakeSession = session;
                final SSLExtension[] enabledExtensions = shc.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO, shc.negotiatedProtocol);
                clientHello.extensions.consumeOnTrade(shc, enabledExtensions);
                final KeyExchangeProperties credentials = chooseCipherSuite(shc, clientHello);
                if (credentials == null) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "no cipher suites in common");
                }
                shc.negotiatedCipherSuite = credentials.cipherSuite;
                shc.handshakeKeyExchange = credentials.keyExchange;
                shc.handshakeSession.setSuite(credentials.cipherSuite);
                shc.handshakePossessions.addAll(Arrays.asList(credentials.possessions));
                shc.handshakeHash.determine(shc.negotiatedProtocol, shc.negotiatedCipherSuite);
                shc.stapleParams = StatusResponseManager.processStapling(shc);
                shc.staplingActive = (shc.stapleParams != null);
                final SSLKeyExchange ke = credentials.keyExchange;
                if (ke != null) {
                    for (final Map.Entry<Byte, HandshakeProducer> me : ke.getHandshakeProducers(shc)) {
                        shc.handshakeProducers.put(me.getKey(), me.getValue());
                    }
                }
                if (ke != null && shc.sslConfig.clientAuthType != ClientAuthType.CLIENT_AUTH_NONE && !shc.negotiatedCipherSuite.isAnonymous()) {
                    for (final SSLHandshake hs : ke.getRelatedHandshakers(shc)) {
                        if (hs == SSLHandshake.CERTIFICATE) {
                            shc.handshakeProducers.put(SSLHandshake.CERTIFICATE_REQUEST.id, SSLHandshake.CERTIFICATE_REQUEST);
                            break;
                        }
                    }
                }
                shc.handshakeProducers.put(SSLHandshake.SERVER_HELLO_DONE.id, SSLHandshake.SERVER_HELLO_DONE);
            }
            else {
                shc.handshakeSession = shc.resumingSession;
                shc.negotiatedProtocol = shc.resumingSession.getProtocolVersion();
                shc.negotiatedCipherSuite = shc.resumingSession.getSuite();
                shc.handshakeHash.determine(shc.negotiatedProtocol, shc.negotiatedCipherSuite);
            }
            final ServerHelloMessage shm = new ServerHelloMessage(shc, shc.negotiatedProtocol, shc.handshakeSession.getSessionId(), shc.negotiatedCipherSuite, new RandomCookie(shc), clientHello);
            shc.serverHelloRandom = shm.serverRandom;
            final SSLExtension[] serverHelloExtensions = shc.sslConfig.getEnabledExtensions(SSLHandshake.SERVER_HELLO, shc.negotiatedProtocol);
            shm.extensions.produce(shc, serverHelloExtensions);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ServerHello handshake message", shm);
            }
            shm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            if (shc.isResumption && shc.resumingSession != null) {
                final SSLTrafficKeyDerivation kdg = SSLTrafficKeyDerivation.valueOf(shc.negotiatedProtocol);
                if (kdg == null) {
                    throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + shc.negotiatedProtocol);
                }
                shc.handshakeKeyDerivation = kdg.createKeyDerivation(shc, shc.resumingSession.getMasterSecret());
                shc.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            }
            return null;
        }
        
        private static KeyExchangeProperties chooseCipherSuite(final ServerHandshakeContext shc, final ClientHello.ClientHelloMessage clientHello) throws IOException {
            List<CipherSuite> preferred;
            List<CipherSuite> proposed;
            if (shc.sslConfig.preferLocalCipherSuites) {
                preferred = shc.activeCipherSuites;
                proposed = clientHello.cipherSuites;
            }
            else {
                preferred = clientHello.cipherSuites;
                proposed = shc.activeCipherSuites;
            }
            final List<CipherSuite> legacySuites = new LinkedList<CipherSuite>();
            for (final CipherSuite cs : preferred) {
                if (!HandshakeContext.isNegotiable(proposed, shc.negotiatedProtocol, cs)) {
                    continue;
                }
                if (shc.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED) {
                    if (cs.keyExchange == CipherSuite.KeyExchange.K_DH_ANON) {
                        continue;
                    }
                    if (cs.keyExchange == CipherSuite.KeyExchange.K_ECDH_ANON) {
                        continue;
                    }
                }
                final SSLKeyExchange ke = SSLKeyExchange.valueOf(cs.keyExchange, shc.negotiatedProtocol);
                if (ke == null) {
                    continue;
                }
                if (!ServerHandshakeContext.legacyAlgorithmConstraints.permits(null, cs.name, null)) {
                    legacySuites.add(cs);
                }
                else {
                    final SSLPossession[] hcds = ke.createPossessions(shc);
                    if (hcds == null) {
                        continue;
                    }
                    if (hcds.length == 0) {
                        continue;
                    }
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("use cipher suite " + cs.name, new Object[0]);
                    }
                    return new KeyExchangeProperties(cs, ke, hcds);
                }
            }
            for (final CipherSuite cs : legacySuites) {
                final SSLKeyExchange ke = SSLKeyExchange.valueOf(cs.keyExchange, shc.negotiatedProtocol);
                if (ke != null) {
                    final SSLPossession[] hcds = ke.createPossessions(shc);
                    if (hcds != null && hcds.length != 0) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                            SSLLogger.warning("use legacy cipher suite " + cs.name, new Object[0]);
                        }
                        return new KeyExchangeProperties(cs, ke, hcds);
                    }
                    continue;
                }
            }
            throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "no cipher suites in common");
        }
        
        private static final class KeyExchangeProperties
        {
            final CipherSuite cipherSuite;
            final SSLKeyExchange keyExchange;
            final SSLPossession[] possessions;
            
            private KeyExchangeProperties(final CipherSuite cipherSuite, final SSLKeyExchange keyExchange, final SSLPossession[] possessions) {
                this.cipherSuite = cipherSuite;
                this.keyExchange = keyExchange;
                this.possessions = possessions;
            }
        }
    }
    
    private static final class T13ServerHelloProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ClientHello.ClientHelloMessage clientHello = (ClientHello.ClientHelloMessage)message;
            if (!shc.isResumption || shc.resumingSession == null) {
                if (!shc.sslConfig.enableSessionCreation) {
                    throw new SSLException("Not resumption, and no new session is allowed");
                }
                if (shc.localSupportedSignAlgs == null) {
                    shc.localSupportedSignAlgs = SignatureScheme.getSupportedAlgorithms(shc.sslConfig, shc.algorithmConstraints, shc.activeProtocols);
                }
                final SSLSessionImpl session = new SSLSessionImpl(shc, CipherSuite.C_NULL);
                session.setMaximumPacketSize(shc.sslConfig.maximumPacketSize);
                shc.handshakeSession = session;
                final SSLExtension[] enabledExtensions = shc.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO, shc.negotiatedProtocol);
                clientHello.extensions.consumeOnTrade(shc, enabledExtensions);
                final CipherSuite cipherSuite = chooseCipherSuite(shc, clientHello);
                if (cipherSuite == null) {
                    throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "no cipher suites in common");
                }
                shc.negotiatedCipherSuite = cipherSuite;
                shc.handshakeSession.setSuite(cipherSuite);
                shc.handshakeHash.determine(shc.negotiatedProtocol, shc.negotiatedCipherSuite);
            }
            else {
                shc.handshakeSession = shc.resumingSession;
                final SSLExtension[] enabledExtensions2 = shc.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO, shc.negotiatedProtocol);
                clientHello.extensions.consumeOnTrade(shc, enabledExtensions2);
                shc.negotiatedProtocol = shc.resumingSession.getProtocolVersion();
                shc.negotiatedCipherSuite = shc.resumingSession.getSuite();
                shc.handshakeHash.determine(shc.negotiatedProtocol, shc.negotiatedCipherSuite);
                setUpPskKD(shc, shc.resumingSession.consumePreSharedKey());
                final SSLSessionContextImpl sessionCache = (SSLSessionContextImpl)shc.sslContext.engineGetServerSessionContext();
                sessionCache.remove(shc.resumingSession.getSessionId());
            }
            shc.handshakeProducers.put(SSLHandshake.ENCRYPTED_EXTENSIONS.id, SSLHandshake.ENCRYPTED_EXTENSIONS);
            shc.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            final ServerHelloMessage shm = new ServerHelloMessage(shc, ProtocolVersion.TLS12, clientHello.sessionId, shc.negotiatedCipherSuite, new RandomCookie(shc), clientHello);
            shc.serverHelloRandom = shm.serverRandom;
            final SSLExtension[] serverHelloExtensions = shc.sslConfig.getEnabledExtensions(SSLHandshake.SERVER_HELLO, shc.negotiatedProtocol);
            shm.extensions.produce(shc, serverHelloExtensions);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ServerHello handshake message", shm);
            }
            shm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            shc.handshakeHash.update();
            final SSLKeyExchange ke = shc.handshakeKeyExchange;
            if (ke == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not negotiated key shares");
            }
            final SSLKeyDerivation handshakeKD = ke.createKeyDerivation(shc);
            final SecretKey handshakeSecret = handshakeKD.deriveKey("TlsHandshakeSecret", null);
            final SSLTrafficKeyDerivation kdg = SSLTrafficKeyDerivation.valueOf(shc.negotiatedProtocol);
            if (kdg == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + shc.negotiatedProtocol);
            }
            final SSLKeyDerivation kd = new SSLSecretDerivation(shc, handshakeSecret);
            final SecretKey readSecret = kd.deriveKey("TlsClientHandshakeTrafficSecret", null);
            final SSLKeyDerivation readKD = kdg.createKeyDerivation(shc, readSecret);
            final SecretKey readKey = readKD.deriveKey("TlsKey", null);
            final SecretKey readIvSecret = readKD.deriveKey("TlsIv", null);
            final IvParameterSpec readIv = new IvParameterSpec(readIvSecret.getEncoded());
            SSLCipher.SSLReadCipher readCipher;
            try {
                readCipher = shc.negotiatedCipherSuite.bulkCipher.createReadCipher(Authenticator.valueOf(shc.negotiatedProtocol), shc.negotiatedProtocol, readKey, readIv, shc.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException gse) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing cipher algorithm", gse);
            }
            if (readCipher == null) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + shc.negotiatedCipherSuite + ") and protocol version (" + shc.negotiatedProtocol + ")");
            }
            shc.baseReadSecret = readSecret;
            shc.conContext.inputRecord.changeReadCiphers(readCipher);
            final SecretKey writeSecret = kd.deriveKey("TlsServerHandshakeTrafficSecret", null);
            final SSLKeyDerivation writeKD = kdg.createKeyDerivation(shc, writeSecret);
            final SecretKey writeKey = writeKD.deriveKey("TlsKey", null);
            final SecretKey writeIvSecret = writeKD.deriveKey("TlsIv", null);
            final IvParameterSpec writeIv = new IvParameterSpec(writeIvSecret.getEncoded());
            SSLCipher.SSLWriteCipher writeCipher;
            try {
                writeCipher = shc.negotiatedCipherSuite.bulkCipher.createWriteCipher(Authenticator.valueOf(shc.negotiatedProtocol), shc.negotiatedProtocol, writeKey, writeIv, shc.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException gse2) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing cipher algorithm", gse2);
            }
            if (writeCipher == null) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + shc.negotiatedCipherSuite + ") and protocol version (" + shc.negotiatedProtocol + ")");
            }
            shc.baseWriteSecret = writeSecret;
            shc.conContext.outputRecord.changeWriteCiphers(writeCipher, clientHello.sessionId.length() != 0);
            shc.handshakeKeyDerivation = kd;
            return null;
        }
        
        private static CipherSuite chooseCipherSuite(final ServerHandshakeContext shc, final ClientHello.ClientHelloMessage clientHello) throws IOException {
            List<CipherSuite> preferred;
            List<CipherSuite> proposed;
            if (shc.sslConfig.preferLocalCipherSuites) {
                preferred = shc.activeCipherSuites;
                proposed = clientHello.cipherSuites;
            }
            else {
                preferred = clientHello.cipherSuites;
                proposed = shc.activeCipherSuites;
            }
            CipherSuite legacySuite = null;
            final AlgorithmConstraints legacyConstraints = ServerHandshakeContext.legacyAlgorithmConstraints;
            for (final CipherSuite cs : preferred) {
                if (!HandshakeContext.isNegotiable(proposed, shc.negotiatedProtocol, cs)) {
                    continue;
                }
                if (legacySuite != null || legacyConstraints.permits(null, cs.name, null)) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("use cipher suite " + cs.name, new Object[0]);
                    }
                    return cs;
                }
                legacySuite = cs;
            }
            if (legacySuite != null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("use legacy cipher suite " + legacySuite.name, new Object[0]);
                }
                return legacySuite;
            }
            return null;
        }
    }
    
    private static final class T13HelloRetryRequestProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ClientHello.ClientHelloMessage clientHello = (ClientHello.ClientHelloMessage)message;
            final CipherSuite cipherSuite = chooseCipherSuite(shc, clientHello);
            if (cipherSuite == null) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "no cipher suites in common for hello retry request");
            }
            final ServerHelloMessage hhrm = new ServerHelloMessage(shc, ProtocolVersion.TLS12, clientHello.sessionId, cipherSuite, RandomCookie.hrrRandom, clientHello);
            shc.negotiatedCipherSuite = cipherSuite;
            shc.handshakeHash.determine(shc.negotiatedProtocol, shc.negotiatedCipherSuite);
            final SSLExtension[] serverHelloExtensions = shc.sslConfig.getEnabledExtensions(SSLHandshake.HELLO_RETRY_REQUEST, shc.negotiatedProtocol);
            hhrm.extensions.produce(shc, serverHelloExtensions);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced HelloRetryRequest handshake message", hhrm);
            }
            hhrm.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            shc.handshakeHash.finish();
            shc.handshakeExtensions.clear();
            shc.handshakeConsumers.put(SSLHandshake.CLIENT_HELLO.id, SSLHandshake.CLIENT_HELLO);
            return null;
        }
    }
    
    private static final class T13HelloRetryRequestReproducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            final ClientHello.ClientHelloMessage clientHello = (ClientHello.ClientHelloMessage)message;
            final CipherSuite cipherSuite = shc.negotiatedCipherSuite;
            final ServerHelloMessage hhrm = new ServerHelloMessage(shc, ProtocolVersion.TLS12, clientHello.sessionId, cipherSuite, RandomCookie.hrrRandom, clientHello);
            final SSLExtension[] serverHelloExtensions = shc.sslConfig.getEnabledExtensions(SSLHandshake.MESSAGE_HASH, shc.negotiatedProtocol);
            hhrm.extensions.produce(shc, serverHelloExtensions);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Reproduced HelloRetryRequest handshake message", hhrm);
            }
            final HandshakeOutStream hos = new HandshakeOutStream(null);
            hhrm.write(hos);
            return hos.toByteArray();
        }
    }
    
    private static final class ServerHelloConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            chc.handshakeConsumers.remove(SSLHandshake.SERVER_HELLO.id);
            if (!chc.handshakeConsumers.isEmpty()) {
                chc.handshakeConsumers.remove(SSLHandshake.HELLO_VERIFY_REQUEST.id);
            }
            if (!chc.handshakeConsumers.isEmpty()) {
                throw chc.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "No more message expected before ServerHello is processed");
            }
            final ServerHelloMessage shm = new ServerHelloMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ServerHello handshake message", shm);
            }
            if (shm.serverRandom.isHelloRetryRequest()) {
                this.onHelloRetryRequest(chc, shm);
            }
            else {
                this.onServerHello(chc, shm);
            }
        }
        
        private void onHelloRetryRequest(final ClientHandshakeContext chc, final ServerHelloMessage helloRetryRequest) throws IOException {
            final SSLExtension[] extTypes = { SSLExtension.HRR_SUPPORTED_VERSIONS };
            helloRetryRequest.extensions.consumeOnLoad(chc, extTypes);
            final SupportedVersionsExtension.SHSupportedVersionsSpec svs = chc.handshakeExtensions.get(SSLExtension.HRR_SUPPORTED_VERSIONS);
            ProtocolVersion serverVersion;
            if (svs != null) {
                serverVersion = ProtocolVersion.valueOf(svs.selectedVersion);
            }
            else {
                serverVersion = helloRetryRequest.serverVersion;
            }
            if (!chc.activeProtocols.contains(serverVersion)) {
                throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "The server selected protocol version " + serverVersion + " is not accepted by client preferences " + chc.activeProtocols);
            }
            if (!serverVersion.useTLS13PlusSpec()) {
                throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "Unexpected HelloRetryRequest for " + serverVersion.name);
            }
            chc.negotiatedProtocol = serverVersion;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Negotiated protocol version: " + serverVersion.name, new Object[0]);
            }
            chc.handshakePossessions.clear();
            if (serverVersion.isDTLS) {
                ServerHello.d13HrrHandshakeConsumer.consume(chc, helloRetryRequest);
            }
            else {
                ServerHello.t13HrrHandshakeConsumer.consume(chc, helloRetryRequest);
            }
        }
        
        private void onServerHello(final ClientHandshakeContext chc, final ServerHelloMessage serverHello) throws IOException {
            final SSLExtension[] extTypes = { SSLExtension.SH_SUPPORTED_VERSIONS };
            serverHello.extensions.consumeOnLoad(chc, extTypes);
            final SupportedVersionsExtension.SHSupportedVersionsSpec svs = chc.handshakeExtensions.get(SSLExtension.SH_SUPPORTED_VERSIONS);
            ProtocolVersion serverVersion;
            if (svs != null) {
                serverVersion = ProtocolVersion.valueOf(svs.selectedVersion);
            }
            else {
                serverVersion = serverHello.serverVersion;
            }
            if (!chc.activeProtocols.contains(serverVersion)) {
                throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "The server selected protocol version " + serverVersion + " is not accepted by client preferences " + chc.activeProtocols);
            }
            chc.negotiatedProtocol = serverVersion;
            if (!chc.conContext.isNegotiated) {
                chc.conContext.protocolVersion = chc.negotiatedProtocol;
                chc.conContext.outputRecord.setVersion(chc.negotiatedProtocol);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Negotiated protocol version: " + serverVersion.name, new Object[0]);
            }
            if (serverHello.serverRandom.isVersionDowngrade(chc)) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "A potential protocol version downgrade attack");
            }
            if (serverVersion.isDTLS) {
                if (serverVersion.useTLS13PlusSpec()) {
                    ServerHello.d13HandshakeConsumer.consume(chc, serverHello);
                }
                else {
                    chc.handshakePossessions.clear();
                    ServerHello.d12HandshakeConsumer.consume(chc, serverHello);
                }
            }
            else if (serverVersion.useTLS13PlusSpec()) {
                ServerHello.t13HandshakeConsumer.consume(chc, serverHello);
            }
            else {
                chc.handshakePossessions.clear();
                ServerHello.t12HandshakeConsumer.consume(chc, serverHello);
            }
        }
    }
    
    private static final class T12ServerHelloConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final ServerHelloMessage serverHello = (ServerHelloMessage)message;
            if (!chc.isNegotiable(serverHello.serverVersion)) {
                throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "Server chose " + serverHello.serverVersion + ", but that protocol version is not enabled or not supported by the client.");
            }
            chc.negotiatedCipherSuite = serverHello.cipherSuite;
            chc.handshakeHash.determine(chc.negotiatedProtocol, chc.negotiatedCipherSuite);
            chc.serverHelloRandom = serverHello.serverRandom;
            if (chc.negotiatedCipherSuite.keyExchange == null) {
                throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "TLS 1.2 or prior version does not support the server cipher suite: " + chc.negotiatedCipherSuite.name);
            }
            SSLExtension[] extTypes = { SSLExtension.SH_RENEGOTIATION_INFO };
            serverHello.extensions.consumeOnLoad(chc, extTypes);
            if (chc.resumingSession != null) {
                if (serverHello.sessionId.equals(chc.resumingSession.getSessionId())) {
                    final CipherSuite sessionSuite = chc.resumingSession.getSuite();
                    if (chc.negotiatedCipherSuite != sessionSuite) {
                        throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "Server returned wrong cipher suite for session");
                    }
                    final ProtocolVersion sessionVersion = chc.resumingSession.getProtocolVersion();
                    if (chc.negotiatedProtocol != sessionVersion) {
                        throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "Server resumed with wrong protocol version");
                    }
                    chc.isResumption = true;
                    chc.resumingSession.setAsSessionResumption(true);
                    chc.handshakeSession = chc.resumingSession;
                }
                else {
                    if (chc.resumingSession != null) {
                        chc.resumingSession.invalidate();
                        chc.resumingSession = null;
                    }
                    chc.isResumption = false;
                    if (!chc.sslConfig.enableSessionCreation) {
                        throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "New session creation is disabled");
                    }
                }
            }
            extTypes = chc.sslConfig.getEnabledExtensions(SSLHandshake.SERVER_HELLO);
            serverHello.extensions.consumeOnLoad(chc, extTypes);
            if (!chc.isResumption) {
                if (chc.resumingSession != null) {
                    chc.resumingSession.invalidate();
                    chc.resumingSession = null;
                }
                if (!chc.sslConfig.enableSessionCreation) {
                    throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "New session creation is disabled");
                }
                (chc.handshakeSession = new SSLSessionImpl(chc, chc.negotiatedCipherSuite, serverHello.sessionId)).setMaximumPacketSize(chc.sslConfig.maximumPacketSize);
            }
            serverHello.extensions.consumeOnTrade(chc, extTypes);
            if (chc.isResumption) {
                final SSLTrafficKeyDerivation kdg = SSLTrafficKeyDerivation.valueOf(chc.negotiatedProtocol);
                if (kdg == null) {
                    throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + chc.negotiatedProtocol);
                }
                chc.handshakeKeyDerivation = kdg.createKeyDerivation(chc, chc.resumingSession.getMasterSecret());
                chc.conContext.consumers.putIfAbsent(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t10Consumer);
                chc.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            }
            else {
                final SSLKeyExchange ke = SSLKeyExchange.valueOf(chc.negotiatedCipherSuite.keyExchange, chc.negotiatedProtocol);
                if ((chc.handshakeKeyExchange = ke) != null) {
                    for (final SSLHandshake handshake : ke.getRelatedHandshakers(chc)) {
                        chc.handshakeConsumers.put(handshake.id, handshake);
                    }
                }
                chc.handshakeConsumers.put(SSLHandshake.SERVER_HELLO_DONE.id, SSLHandshake.SERVER_HELLO_DONE);
            }
        }
    }
    
    private static final class T13ServerHelloConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final ServerHelloMessage serverHello = (ServerHelloMessage)message;
            if (serverHello.serverVersion != ProtocolVersion.TLS12) {
                throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "The ServerHello.legacy_version field is not TLS 1.2");
            }
            chc.negotiatedCipherSuite = serverHello.cipherSuite;
            chc.handshakeHash.determine(chc.negotiatedProtocol, chc.negotiatedCipherSuite);
            chc.serverHelloRandom = serverHello.serverRandom;
            final SSLExtension[] extTypes = chc.sslConfig.getEnabledExtensions(SSLHandshake.SERVER_HELLO);
            serverHello.extensions.consumeOnLoad(chc, extTypes);
            if (!chc.isResumption) {
                if (chc.resumingSession != null) {
                    chc.resumingSession.invalidate();
                    chc.resumingSession = null;
                }
                if (!chc.sslConfig.enableSessionCreation) {
                    throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "New session creation is disabled");
                }
                (chc.handshakeSession = new SSLSessionImpl(chc, chc.negotiatedCipherSuite, serverHello.sessionId)).setMaximumPacketSize(chc.sslConfig.maximumPacketSize);
            }
            else {
                final SecretKey psk = chc.resumingSession.consumePreSharedKey();
                if (psk == null) {
                    throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "No PSK available. Unable to resume.");
                }
                chc.handshakeSession = chc.resumingSession;
                setUpPskKD(chc, psk);
            }
            serverHello.extensions.consumeOnTrade(chc, extTypes);
            chc.handshakeHash.update();
            final SSLKeyExchange ke = chc.handshakeKeyExchange;
            if (ke == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Not negotiated key shares");
            }
            final SSLKeyDerivation handshakeKD = ke.createKeyDerivation(chc);
            final SecretKey handshakeSecret = handshakeKD.deriveKey("TlsHandshakeSecret", null);
            final SSLTrafficKeyDerivation kdg = SSLTrafficKeyDerivation.valueOf(chc.negotiatedProtocol);
            if (kdg == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + chc.negotiatedProtocol);
            }
            final SSLKeyDerivation secretKD = new SSLSecretDerivation(chc, handshakeSecret);
            final SecretKey readSecret = secretKD.deriveKey("TlsServerHandshakeTrafficSecret", null);
            final SSLKeyDerivation readKD = kdg.createKeyDerivation(chc, readSecret);
            final SecretKey readKey = readKD.deriveKey("TlsKey", null);
            final SecretKey readIvSecret = readKD.deriveKey("TlsIv", null);
            final IvParameterSpec readIv = new IvParameterSpec(readIvSecret.getEncoded());
            SSLCipher.SSLReadCipher readCipher;
            try {
                readCipher = chc.negotiatedCipherSuite.bulkCipher.createReadCipher(Authenticator.valueOf(chc.negotiatedProtocol), chc.negotiatedProtocol, readKey, readIv, chc.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException gse) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing cipher algorithm", gse);
            }
            if (readCipher == null) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + chc.negotiatedCipherSuite + ") and protocol version (" + chc.negotiatedProtocol + ")");
            }
            chc.baseReadSecret = readSecret;
            chc.conContext.inputRecord.changeReadCiphers(readCipher);
            final SecretKey writeSecret = secretKD.deriveKey("TlsClientHandshakeTrafficSecret", null);
            final SSLKeyDerivation writeKD = kdg.createKeyDerivation(chc, writeSecret);
            final SecretKey writeKey = writeKD.deriveKey("TlsKey", null);
            final SecretKey writeIvSecret = writeKD.deriveKey("TlsIv", null);
            final IvParameterSpec writeIv = new IvParameterSpec(writeIvSecret.getEncoded());
            SSLCipher.SSLWriteCipher writeCipher;
            try {
                writeCipher = chc.negotiatedCipherSuite.bulkCipher.createWriteCipher(Authenticator.valueOf(chc.negotiatedProtocol), chc.negotiatedProtocol, writeKey, writeIv, chc.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException gse2) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing cipher algorithm", gse2);
            }
            if (writeCipher == null) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + chc.negotiatedCipherSuite + ") and protocol version (" + chc.negotiatedProtocol + ")");
            }
            chc.baseWriteSecret = writeSecret;
            chc.conContext.outputRecord.changeWriteCiphers(writeCipher, serverHello.sessionId.length() != 0);
            chc.handshakeKeyDerivation = secretKD;
            chc.conContext.consumers.putIfAbsent(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t13Consumer);
            chc.handshakeConsumers.put(SSLHandshake.ENCRYPTED_EXTENSIONS.id, SSLHandshake.ENCRYPTED_EXTENSIONS);
            chc.handshakeConsumers.put(SSLHandshake.CERTIFICATE_REQUEST.id, SSLHandshake.CERTIFICATE_REQUEST);
            chc.handshakeConsumers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            chc.handshakeConsumers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            chc.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
        }
    }
    
    private static final class T13HelloRetryRequestConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final ServerHelloMessage helloRetryRequest = (ServerHelloMessage)message;
            if (helloRetryRequest.serverVersion != ProtocolVersion.TLS12) {
                throw chc.conContext.fatal(Alert.PROTOCOL_VERSION, "The HelloRetryRequest.legacy_version is not TLS 1.2");
            }
            chc.negotiatedCipherSuite = helloRetryRequest.cipherSuite;
            final SSLExtension[] extTypes = chc.sslConfig.getEnabledExtensions(SSLHandshake.HELLO_RETRY_REQUEST);
            helloRetryRequest.extensions.consumeOnLoad(chc, extTypes);
            helloRetryRequest.extensions.consumeOnTrade(chc, extTypes);
            chc.handshakeHash.finish();
            final HandshakeOutStream hos = new HandshakeOutStream(null);
            try {
                chc.initialClientHelloMsg.write(hos);
            }
            catch (final IOException ioe) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Failed to construct message hash", ioe);
            }
            chc.handshakeHash.deliver(hos.toByteArray());
            chc.handshakeHash.determine(chc.negotiatedProtocol, chc.negotiatedCipherSuite);
            final byte[] clientHelloHash = chc.handshakeHash.digest();
            final int hashLen = chc.negotiatedCipherSuite.hashAlg.hashLength;
            final byte[] hashedClientHello = new byte[4 + hashLen];
            hashedClientHello[0] = SSLHandshake.MESSAGE_HASH.id;
            hashedClientHello[2] = (hashedClientHello[1] = 0);
            hashedClientHello[3] = (byte)(hashLen & 0xFF);
            System.arraycopy(clientHelloHash, 0, hashedClientHello, 4, hashLen);
            chc.handshakeHash.finish();
            chc.handshakeHash.deliver(hashedClientHello);
            final int hrrBodyLen = helloRetryRequest.handshakeRecord.remaining();
            final byte[] hrrMessage = new byte[4 + hrrBodyLen];
            hrrMessage[0] = SSLHandshake.HELLO_RETRY_REQUEST.id;
            hrrMessage[1] = (byte)(hrrBodyLen >> 16 & 0xFF);
            hrrMessage[2] = (byte)(hrrBodyLen >> 8 & 0xFF);
            hrrMessage[3] = (byte)(hrrBodyLen & 0xFF);
            final ByteBuffer hrrBody = helloRetryRequest.handshakeRecord.duplicate();
            hrrBody.get(hrrMessage, 4, hrrBodyLen);
            chc.handshakeHash.receive(hrrMessage);
            chc.initialClientHelloMsg.extensions.reproduce(chc, new SSLExtension[] { SSLExtension.CH_COOKIE, SSLExtension.CH_KEY_SHARE, SSLExtension.CH_PRE_SHARED_KEY });
            SSLHandshake.CLIENT_HELLO.produce(context, helloRetryRequest);
        }
    }
}
