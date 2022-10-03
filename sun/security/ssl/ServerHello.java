package sun.security.ssl;

import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;
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
    private static final HandshakeConsumer t13HrrHandshakeConsumer;
    
    private static void setUpPskKD(final HandshakeContext handshakeContext, final SecretKey secretKey) throws SSLHandshakeException {
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
            SSLLogger.fine("Using PSK to derive early secret", new Object[0]);
        }
        try {
            final CipherSuite.HashAlg hashAlg = handshakeContext.negotiatedCipherSuite.hashAlg;
            handshakeContext.handshakeKeyDerivation = new SSLSecretDerivation(handshakeContext, new HKDF(hashAlg.name).extract(new byte[hashAlg.hashLength], secretKey, "TlsEarlySecret"));
        }
        catch (final GeneralSecurityException ex) {
            throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(ex);
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
        t13HrrHandshakeConsumer = new T13HelloRetryRequestConsumer();
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
        
        ServerHelloMessage(final HandshakeContext handshakeContext, final ProtocolVersion serverVersion, final SessionId sessionId, final CipherSuite cipherSuite, final RandomCookie serverRandom, final ClientHello.ClientHelloMessage clientHello) {
            super(handshakeContext);
            this.serverVersion = serverVersion;
            this.serverRandom = serverRandom;
            this.sessionId = sessionId;
            this.cipherSuite = cipherSuite;
            this.compressionMethod = 0;
            this.extensions = new SSLExtensions(this);
            this.clientHello = clientHello;
            this.handshakeRecord = null;
        }
        
        ServerHelloMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            this.handshakeRecord = byteBuffer.duplicate();
            final byte value = byteBuffer.get();
            final byte value2 = byteBuffer.get();
            this.serverVersion = ProtocolVersion.valueOf(value, value2);
            if (this.serverVersion == null) {
                throw handshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "Unsupported protocol version: " + ProtocolVersion.nameOf(value, value2));
            }
            this.serverRandom = new RandomCookie(byteBuffer);
            this.sessionId = new SessionId(Record.getBytes8(byteBuffer));
            try {
                this.sessionId.checkLength(this.serverVersion.id);
            }
            catch (final SSLProtocolException ex) {
                throw this.handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, ex);
            }
            final int int16 = Record.getInt16(byteBuffer);
            this.cipherSuite = CipherSuite.valueOf(int16);
            if (this.cipherSuite == null || !handshakeContext.isNegotiable(this.cipherSuite)) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Server selected improper ciphersuite " + CipherSuite.nameOf(int16));
            }
            this.compressionMethod = byteBuffer.get();
            if (this.compressionMethod != 0) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "compression type not supported, " + this.compressionMethod);
            }
            SSLExtension[] array;
            if (this.serverRandom.isHelloRetryRequest()) {
                array = handshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.HELLO_RETRY_REQUEST);
            }
            else {
                array = handshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.SERVER_HELLO);
            }
            if (byteBuffer.hasRemaining()) {
                this.extensions = new SSLExtensions(this, byteBuffer, array);
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
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putInt8(this.serverVersion.major);
            handshakeOutStream.putInt8(this.serverVersion.minor);
            handshakeOutStream.write(this.serverRandom.randomBytes);
            handshakeOutStream.putBytes8(this.sessionId.getId());
            handshakeOutStream.putInt8(this.cipherSuite.id >> 8 & 0xFF);
            handshakeOutStream.putInt8(this.cipherSuite.id & 0xFF);
            handshakeOutStream.putInt8(this.compressionMethod);
            this.extensions.send(handshakeOutStream);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"{0}\": '{'\n  \"server version\"      : \"{1}\",\n  \"random\"              : \"{2}\",\n  \"session id\"          : \"{3}\",\n  \"cipher suite\"        : \"{4}\",\n  \"compression methods\" : \"{5}\",\n  \"extensions\"          : [\n{6}\n  ]\n'}'", Locale.ENGLISH).format(new Object[] { this.serverRandom.isHelloRetryRequest() ? "HelloRetryRequest" : "ServerHello", this.serverVersion.name, Utilities.toHexString(this.serverRandom.randomBytes), this.sessionId.toString(), this.cipherSuite.name + "(" + Utilities.byte16HexString(this.cipherSuite.id) + ")", Utilities.toHexString(this.compressionMethod), Utilities.indent(this.extensions.toString(), "    ") });
        }
    }
    
    private static final class T12ServerHelloProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final ClientHello.ClientHelloMessage clientHelloMessage = (ClientHello.ClientHelloMessage)handshakeMessage;
            if (!serverHandshakeContext.isResumption || serverHandshakeContext.resumingSession == null) {
                if (!serverHandshakeContext.sslConfig.enableSessionCreation) {
                    throw new SSLException("Not resumption, and no new session is allowed");
                }
                if (serverHandshakeContext.localSupportedSignAlgs == null) {
                    serverHandshakeContext.localSupportedSignAlgs = SignatureScheme.getSupportedAlgorithms(serverHandshakeContext.sslConfig, serverHandshakeContext.algorithmConstraints, serverHandshakeContext.activeProtocols);
                }
                final SSLSessionImpl handshakeSession = new SSLSessionImpl(serverHandshakeContext, CipherSuite.C_NULL);
                handshakeSession.setMaximumPacketSize(serverHandshakeContext.sslConfig.maximumPacketSize);
                serverHandshakeContext.handshakeSession = handshakeSession;
                clientHelloMessage.extensions.consumeOnTrade(serverHandshakeContext, serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO, serverHandshakeContext.negotiatedProtocol));
                final KeyExchangeProperties chooseCipherSuite = chooseCipherSuite(serverHandshakeContext, clientHelloMessage);
                if (chooseCipherSuite == null) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "no cipher suites in common");
                }
                serverHandshakeContext.negotiatedCipherSuite = chooseCipherSuite.cipherSuite;
                serverHandshakeContext.handshakeKeyExchange = chooseCipherSuite.keyExchange;
                serverHandshakeContext.handshakeSession.setSuite(chooseCipherSuite.cipherSuite);
                serverHandshakeContext.handshakePossessions.addAll(Arrays.asList(chooseCipherSuite.possessions));
                serverHandshakeContext.handshakeHash.determine(serverHandshakeContext.negotiatedProtocol, serverHandshakeContext.negotiatedCipherSuite);
                serverHandshakeContext.stapleParams = StatusResponseManager.processStapling(serverHandshakeContext);
                serverHandshakeContext.staplingActive = (serverHandshakeContext.stapleParams != null);
                final SSLKeyExchange keyExchange = chooseCipherSuite.keyExchange;
                if (keyExchange != null) {
                    for (final Map.Entry<Byte, HandshakeProducer> entry : keyExchange.getHandshakeProducers(serverHandshakeContext)) {
                        serverHandshakeContext.handshakeProducers.put(entry.getKey(), entry.getValue());
                    }
                }
                if (keyExchange != null && serverHandshakeContext.sslConfig.clientAuthType != ClientAuthType.CLIENT_AUTH_NONE && !serverHandshakeContext.negotiatedCipherSuite.isAnonymous()) {
                    final SSLHandshake[] relatedHandshakers = keyExchange.getRelatedHandshakers(serverHandshakeContext);
                    for (int length2 = relatedHandshakers.length, j = 0; j < length2; ++j) {
                        if (relatedHandshakers[j] == SSLHandshake.CERTIFICATE) {
                            serverHandshakeContext.handshakeProducers.put(SSLHandshake.CERTIFICATE_REQUEST.id, SSLHandshake.CERTIFICATE_REQUEST);
                            break;
                        }
                    }
                }
                serverHandshakeContext.handshakeProducers.put(SSLHandshake.SERVER_HELLO_DONE.id, SSLHandshake.SERVER_HELLO_DONE);
            }
            else {
                serverHandshakeContext.handshakeSession = serverHandshakeContext.resumingSession;
                serverHandshakeContext.negotiatedProtocol = serverHandshakeContext.resumingSession.getProtocolVersion();
                serverHandshakeContext.negotiatedCipherSuite = serverHandshakeContext.resumingSession.getSuite();
                serverHandshakeContext.handshakeHash.determine(serverHandshakeContext.negotiatedProtocol, serverHandshakeContext.negotiatedCipherSuite);
            }
            final ServerHelloMessage serverHelloMessage = new ServerHelloMessage(serverHandshakeContext, serverHandshakeContext.negotiatedProtocol, serverHandshakeContext.handshakeSession.getSessionId(), serverHandshakeContext.negotiatedCipherSuite, new RandomCookie(serverHandshakeContext), clientHelloMessage);
            serverHandshakeContext.serverHelloRandom = serverHelloMessage.serverRandom;
            serverHelloMessage.extensions.produce(serverHandshakeContext, serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.SERVER_HELLO, serverHandshakeContext.negotiatedProtocol));
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ServerHello handshake message", serverHelloMessage);
            }
            serverHelloMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            if (serverHandshakeContext.isResumption && serverHandshakeContext.resumingSession != null) {
                final SSLTrafficKeyDerivation value = SSLTrafficKeyDerivation.valueOf(serverHandshakeContext.negotiatedProtocol);
                if (value == null) {
                    throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + serverHandshakeContext.negotiatedProtocol);
                }
                serverHandshakeContext.handshakeKeyDerivation = value.createKeyDerivation(serverHandshakeContext, serverHandshakeContext.resumingSession.getMasterSecret());
                serverHandshakeContext.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            }
            return null;
        }
        
        private static KeyExchangeProperties chooseCipherSuite(final ServerHandshakeContext serverHandshakeContext, final ClientHello.ClientHelloMessage clientHelloMessage) throws IOException {
            List<CipherSuite> list;
            List<CipherSuite> list2;
            if (serverHandshakeContext.sslConfig.preferLocalCipherSuites) {
                list = serverHandshakeContext.activeCipherSuites;
                list2 = clientHelloMessage.cipherSuites;
            }
            else {
                list = clientHelloMessage.cipherSuites;
                list2 = serverHandshakeContext.activeCipherSuites;
            }
            final LinkedList list3 = new LinkedList();
            for (final CipherSuite cipherSuite : list) {
                if (!HandshakeContext.isNegotiable(list2, serverHandshakeContext.negotiatedProtocol, cipherSuite)) {
                    continue;
                }
                if (serverHandshakeContext.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED) {
                    if (cipherSuite.keyExchange == CipherSuite.KeyExchange.K_DH_ANON) {
                        continue;
                    }
                    if (cipherSuite.keyExchange == CipherSuite.KeyExchange.K_ECDH_ANON) {
                        continue;
                    }
                }
                final SSLKeyExchange value = SSLKeyExchange.valueOf(cipherSuite.keyExchange, serverHandshakeContext.negotiatedProtocol);
                if (value == null) {
                    continue;
                }
                if (!ServerHandshakeContext.legacyAlgorithmConstraints.permits(null, cipherSuite.name, null)) {
                    list3.add(cipherSuite);
                }
                else {
                    final SSLPossession[] possessions = value.createPossessions(serverHandshakeContext);
                    if (possessions == null) {
                        continue;
                    }
                    if (possessions.length == 0) {
                        continue;
                    }
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("use cipher suite " + cipherSuite.name, new Object[0]);
                    }
                    return new KeyExchangeProperties(cipherSuite, value, possessions);
                }
            }
            for (final CipherSuite cipherSuite2 : list3) {
                final SSLKeyExchange value2 = SSLKeyExchange.valueOf(cipherSuite2.keyExchange, serverHandshakeContext.negotiatedProtocol);
                if (value2 != null) {
                    final SSLPossession[] possessions2 = value2.createPossessions(serverHandshakeContext);
                    if (possessions2 != null && possessions2.length != 0) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                            SSLLogger.warning("use legacy cipher suite " + cipherSuite2.name, new Object[0]);
                        }
                        return new KeyExchangeProperties(cipherSuite2, value2, possessions2);
                    }
                    continue;
                }
            }
            throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "no cipher suites in common");
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
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final ClientHello.ClientHelloMessage clientHelloMessage = (ClientHello.ClientHelloMessage)handshakeMessage;
            if (!serverHandshakeContext.isResumption || serverHandshakeContext.resumingSession == null) {
                if (!serverHandshakeContext.sslConfig.enableSessionCreation) {
                    throw new SSLException("Not resumption, and no new session is allowed");
                }
                if (serverHandshakeContext.localSupportedSignAlgs == null) {
                    serverHandshakeContext.localSupportedSignAlgs = SignatureScheme.getSupportedAlgorithms(serverHandshakeContext.sslConfig, serverHandshakeContext.algorithmConstraints, serverHandshakeContext.activeProtocols);
                }
                final SSLSessionImpl handshakeSession = new SSLSessionImpl(serverHandshakeContext, CipherSuite.C_NULL);
                handshakeSession.setMaximumPacketSize(serverHandshakeContext.sslConfig.maximumPacketSize);
                serverHandshakeContext.handshakeSession = handshakeSession;
                clientHelloMessage.extensions.consumeOnTrade(serverHandshakeContext, serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO, serverHandshakeContext.negotiatedProtocol));
                final CipherSuite chooseCipherSuite = chooseCipherSuite(serverHandshakeContext, clientHelloMessage);
                if (chooseCipherSuite == null) {
                    throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "no cipher suites in common");
                }
                serverHandshakeContext.negotiatedCipherSuite = chooseCipherSuite;
                serverHandshakeContext.handshakeSession.setSuite(chooseCipherSuite);
                serverHandshakeContext.handshakeHash.determine(serverHandshakeContext.negotiatedProtocol, serverHandshakeContext.negotiatedCipherSuite);
            }
            else {
                serverHandshakeContext.handshakeSession = serverHandshakeContext.resumingSession;
                clientHelloMessage.extensions.consumeOnTrade(serverHandshakeContext, serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.CLIENT_HELLO, serverHandshakeContext.negotiatedProtocol));
                serverHandshakeContext.negotiatedProtocol = serverHandshakeContext.resumingSession.getProtocolVersion();
                serverHandshakeContext.negotiatedCipherSuite = serverHandshakeContext.resumingSession.getSuite();
                serverHandshakeContext.handshakeHash.determine(serverHandshakeContext.negotiatedProtocol, serverHandshakeContext.negotiatedCipherSuite);
                setUpPskKD(serverHandshakeContext, serverHandshakeContext.resumingSession.consumePreSharedKey());
                ((SSLSessionContextImpl)serverHandshakeContext.sslContext.engineGetServerSessionContext()).remove(serverHandshakeContext.resumingSession.getSessionId());
            }
            serverHandshakeContext.handshakeProducers.put(SSLHandshake.ENCRYPTED_EXTENSIONS.id, SSLHandshake.ENCRYPTED_EXTENSIONS);
            serverHandshakeContext.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            final ServerHelloMessage serverHelloMessage = new ServerHelloMessage(serverHandshakeContext, ProtocolVersion.TLS12, clientHelloMessage.sessionId, serverHandshakeContext.negotiatedCipherSuite, new RandomCookie(serverHandshakeContext), clientHelloMessage);
            serverHandshakeContext.serverHelloRandom = serverHelloMessage.serverRandom;
            serverHelloMessage.extensions.produce(serverHandshakeContext, serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.SERVER_HELLO, serverHandshakeContext.negotiatedProtocol));
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ServerHello handshake message", serverHelloMessage);
            }
            serverHelloMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            serverHandshakeContext.handshakeHash.update();
            final SSLKeyExchange handshakeKeyExchange = serverHandshakeContext.handshakeKeyExchange;
            if (handshakeKeyExchange == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not negotiated key shares");
            }
            final SecretKey deriveKey = handshakeKeyExchange.createKeyDerivation(serverHandshakeContext).deriveKey("TlsHandshakeSecret", null);
            final SSLTrafficKeyDerivation value = SSLTrafficKeyDerivation.valueOf(serverHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + serverHandshakeContext.negotiatedProtocol);
            }
            final SSLSecretDerivation handshakeKeyDerivation = new SSLSecretDerivation(serverHandshakeContext, deriveKey);
            final SecretKey deriveKey2 = handshakeKeyDerivation.deriveKey("TlsClientHandshakeTrafficSecret", null);
            final SSLKeyDerivation keyDerivation = value.createKeyDerivation(serverHandshakeContext, deriveKey2);
            final SecretKey deriveKey3 = keyDerivation.deriveKey("TlsKey", null);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(keyDerivation.deriveKey("TlsIv", null).getEncoded());
            SSLCipher.SSLReadCipher readCipher;
            try {
                readCipher = serverHandshakeContext.negotiatedCipherSuite.bulkCipher.createReadCipher(Authenticator.valueOf(serverHandshakeContext.negotiatedProtocol), serverHandshakeContext.negotiatedProtocol, deriveKey3, ivParameterSpec, serverHandshakeContext.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing cipher algorithm", ex);
            }
            if (readCipher == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + serverHandshakeContext.negotiatedCipherSuite + ") and protocol version (" + serverHandshakeContext.negotiatedProtocol + ")");
            }
            serverHandshakeContext.baseReadSecret = deriveKey2;
            serverHandshakeContext.conContext.inputRecord.changeReadCiphers(readCipher);
            final SecretKey deriveKey4 = handshakeKeyDerivation.deriveKey("TlsServerHandshakeTrafficSecret", null);
            final SSLKeyDerivation keyDerivation2 = value.createKeyDerivation(serverHandshakeContext, deriveKey4);
            final SecretKey deriveKey5 = keyDerivation2.deriveKey("TlsKey", null);
            final IvParameterSpec ivParameterSpec2 = new IvParameterSpec(keyDerivation2.deriveKey("TlsIv", null).getEncoded());
            SSLCipher.SSLWriteCipher writeCipher;
            try {
                writeCipher = serverHandshakeContext.negotiatedCipherSuite.bulkCipher.createWriteCipher(Authenticator.valueOf(serverHandshakeContext.negotiatedProtocol), serverHandshakeContext.negotiatedProtocol, deriveKey5, ivParameterSpec2, serverHandshakeContext.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException ex2) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing cipher algorithm", ex2);
            }
            if (writeCipher == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + serverHandshakeContext.negotiatedCipherSuite + ") and protocol version (" + serverHandshakeContext.negotiatedProtocol + ")");
            }
            serverHandshakeContext.baseWriteSecret = deriveKey4;
            serverHandshakeContext.conContext.outputRecord.changeWriteCiphers(writeCipher, clientHelloMessage.sessionId.length() != 0);
            serverHandshakeContext.handshakeKeyDerivation = handshakeKeyDerivation;
            return null;
        }
        
        private static CipherSuite chooseCipherSuite(final ServerHandshakeContext serverHandshakeContext, final ClientHello.ClientHelloMessage clientHelloMessage) throws IOException {
            List<CipherSuite> list;
            List<CipherSuite> list2;
            if (serverHandshakeContext.sslConfig.preferLocalCipherSuites) {
                list = serverHandshakeContext.activeCipherSuites;
                list2 = clientHelloMessage.cipherSuites;
            }
            else {
                list = clientHelloMessage.cipherSuites;
                list2 = serverHandshakeContext.activeCipherSuites;
            }
            CipherSuite cipherSuite = null;
            final AlgorithmConstraints legacyAlgorithmConstraints = ServerHandshakeContext.legacyAlgorithmConstraints;
            for (final CipherSuite cipherSuite2 : list) {
                if (!HandshakeContext.isNegotiable(list2, serverHandshakeContext.negotiatedProtocol, cipherSuite2)) {
                    continue;
                }
                if (cipherSuite != null || legacyAlgorithmConstraints.permits(null, cipherSuite2.name, null)) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("use cipher suite " + cipherSuite2.name, new Object[0]);
                    }
                    return cipherSuite2;
                }
                cipherSuite = cipherSuite2;
            }
            if (cipherSuite != null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("use legacy cipher suite " + cipherSuite.name, new Object[0]);
                }
                return cipherSuite;
            }
            return null;
        }
    }
    
    private static final class T13HelloRetryRequestProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final ClientHello.ClientHelloMessage clientHelloMessage = (ClientHello.ClientHelloMessage)handshakeMessage;
            final CipherSuite access$1000 = chooseCipherSuite(serverHandshakeContext, clientHelloMessage);
            if (access$1000 == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "no cipher suites in common for hello retry request");
            }
            final ServerHelloMessage serverHelloMessage = new ServerHelloMessage(serverHandshakeContext, ProtocolVersion.TLS12, clientHelloMessage.sessionId, access$1000, RandomCookie.hrrRandom, clientHelloMessage);
            serverHandshakeContext.negotiatedCipherSuite = access$1000;
            serverHandshakeContext.handshakeHash.determine(serverHandshakeContext.negotiatedProtocol, serverHandshakeContext.negotiatedCipherSuite);
            serverHelloMessage.extensions.produce(serverHandshakeContext, serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.HELLO_RETRY_REQUEST, serverHandshakeContext.negotiatedProtocol));
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced HelloRetryRequest handshake message", serverHelloMessage);
            }
            serverHelloMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            serverHandshakeContext.handshakeHash.finish();
            serverHandshakeContext.handshakeExtensions.clear();
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.CLIENT_HELLO.id, SSLHandshake.CLIENT_HELLO);
            return null;
        }
    }
    
    private static final class T13HelloRetryRequestReproducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            final ClientHello.ClientHelloMessage clientHelloMessage = (ClientHello.ClientHelloMessage)handshakeMessage;
            final ServerHelloMessage serverHelloMessage = new ServerHelloMessage(serverHandshakeContext, ProtocolVersion.TLS12, clientHelloMessage.sessionId, serverHandshakeContext.negotiatedCipherSuite, RandomCookie.hrrRandom, clientHelloMessage);
            serverHelloMessage.extensions.produce(serverHandshakeContext, serverHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.MESSAGE_HASH, serverHandshakeContext.negotiatedProtocol));
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Reproduced HelloRetryRequest handshake message", serverHelloMessage);
            }
            final HandshakeOutStream handshakeOutStream = new HandshakeOutStream(null);
            serverHelloMessage.write(handshakeOutStream);
            return handshakeOutStream.toByteArray();
        }
    }
    
    private static final class ServerHelloConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            clientHandshakeContext.handshakeConsumers.remove(SSLHandshake.SERVER_HELLO.id);
            if (!clientHandshakeContext.handshakeConsumers.isEmpty()) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "No more message expected before ServerHello is processed");
            }
            final ServerHelloMessage serverHelloMessage = new ServerHelloMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ServerHello handshake message", serverHelloMessage);
            }
            if (serverHelloMessage.serverRandom.isHelloRetryRequest()) {
                this.onHelloRetryRequest(clientHandshakeContext, serverHelloMessage);
            }
            else {
                this.onServerHello(clientHandshakeContext, serverHelloMessage);
            }
        }
        
        private void onHelloRetryRequest(final ClientHandshakeContext clientHandshakeContext, final ServerHelloMessage serverHelloMessage) throws IOException {
            serverHelloMessage.extensions.consumeOnLoad(clientHandshakeContext, new SSLExtension[] { SSLExtension.HRR_SUPPORTED_VERSIONS });
            final SupportedVersionsExtension.SHSupportedVersionsSpec shSupportedVersionsSpec = clientHandshakeContext.handshakeExtensions.get(SSLExtension.HRR_SUPPORTED_VERSIONS);
            ProtocolVersion negotiatedProtocol;
            if (shSupportedVersionsSpec != null) {
                negotiatedProtocol = ProtocolVersion.valueOf(shSupportedVersionsSpec.selectedVersion);
            }
            else {
                negotiatedProtocol = serverHelloMessage.serverVersion;
            }
            if (!clientHandshakeContext.activeProtocols.contains(negotiatedProtocol)) {
                throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "The server selected protocol version " + negotiatedProtocol + " is not accepted by client preferences " + clientHandshakeContext.activeProtocols);
            }
            if (!negotiatedProtocol.useTLS13PlusSpec()) {
                throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "Unexpected HelloRetryRequest for " + negotiatedProtocol.name);
            }
            clientHandshakeContext.negotiatedProtocol = negotiatedProtocol;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Negotiated protocol version: " + negotiatedProtocol.name, new Object[0]);
            }
            clientHandshakeContext.handshakePossessions.clear();
            ServerHello.t13HrrHandshakeConsumer.consume(clientHandshakeContext, serverHelloMessage);
        }
        
        private void onServerHello(final ClientHandshakeContext clientHandshakeContext, final ServerHelloMessage serverHelloMessage) throws IOException {
            serverHelloMessage.extensions.consumeOnLoad(clientHandshakeContext, new SSLExtension[] { SSLExtension.SH_SUPPORTED_VERSIONS });
            final SupportedVersionsExtension.SHSupportedVersionsSpec shSupportedVersionsSpec = clientHandshakeContext.handshakeExtensions.get(SSLExtension.SH_SUPPORTED_VERSIONS);
            ProtocolVersion negotiatedProtocol;
            if (shSupportedVersionsSpec != null) {
                negotiatedProtocol = ProtocolVersion.valueOf(shSupportedVersionsSpec.selectedVersion);
            }
            else {
                negotiatedProtocol = serverHelloMessage.serverVersion;
            }
            if (!clientHandshakeContext.activeProtocols.contains(negotiatedProtocol)) {
                throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "The server selected protocol version " + negotiatedProtocol + " is not accepted by client preferences " + clientHandshakeContext.activeProtocols);
            }
            clientHandshakeContext.negotiatedProtocol = negotiatedProtocol;
            if (!clientHandshakeContext.conContext.isNegotiated) {
                clientHandshakeContext.conContext.protocolVersion = clientHandshakeContext.negotiatedProtocol;
                clientHandshakeContext.conContext.outputRecord.setVersion(clientHandshakeContext.negotiatedProtocol);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Negotiated protocol version: " + negotiatedProtocol.name, new Object[0]);
            }
            if (serverHelloMessage.serverRandom.isVersionDowngrade(clientHandshakeContext)) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "A potential protocol version downgrade attack");
            }
            if (negotiatedProtocol.useTLS13PlusSpec()) {
                ServerHello.t13HandshakeConsumer.consume(clientHandshakeContext, serverHelloMessage);
            }
            else {
                clientHandshakeContext.handshakePossessions.clear();
                ServerHello.t12HandshakeConsumer.consume(clientHandshakeContext, serverHelloMessage);
            }
        }
    }
    
    private static final class T12ServerHelloConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final ServerHelloMessage serverHelloMessage = (ServerHelloMessage)handshakeMessage;
            if (!clientHandshakeContext.isNegotiable(serverHelloMessage.serverVersion)) {
                throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "Server chose " + serverHelloMessage.serverVersion + ", but that protocol version is not enabled or not supported by the client.");
            }
            clientHandshakeContext.negotiatedCipherSuite = serverHelloMessage.cipherSuite;
            clientHandshakeContext.handshakeHash.determine(clientHandshakeContext.negotiatedProtocol, clientHandshakeContext.negotiatedCipherSuite);
            clientHandshakeContext.serverHelloRandom = serverHelloMessage.serverRandom;
            if (clientHandshakeContext.negotiatedCipherSuite.keyExchange == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "TLS 1.2 or prior version does not support the server cipher suite: " + clientHandshakeContext.negotiatedCipherSuite.name);
            }
            serverHelloMessage.extensions.consumeOnLoad(clientHandshakeContext, new SSLExtension[] { SSLExtension.SH_RENEGOTIATION_INFO });
            if (clientHandshakeContext.resumingSession != null) {
                if (serverHelloMessage.sessionId.equals(clientHandshakeContext.resumingSession.getSessionId())) {
                    final CipherSuite suite = clientHandshakeContext.resumingSession.getSuite();
                    if (clientHandshakeContext.negotiatedCipherSuite != suite) {
                        throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "Server returned wrong cipher suite for session");
                    }
                    if (clientHandshakeContext.negotiatedProtocol != clientHandshakeContext.resumingSession.getProtocolVersion()) {
                        throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "Server resumed with wrong protocol version");
                    }
                    if (suite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || suite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
                        final Principal localPrincipal = clientHandshakeContext.resumingSession.getLocalPrincipal();
                        Subject subject;
                        try {
                            subject = AccessController.doPrivileged((PrivilegedExceptionAction<Subject>)new PrivilegedExceptionAction<Subject>() {
                                @Override
                                public Subject run() throws Exception {
                                    return Krb5Helper.getClientSubject(clientHandshakeContext.conContext.acc);
                                }
                            });
                        }
                        catch (final PrivilegedActionException ex) {
                            subject = null;
                            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                                SSLLogger.fine("Attempt to obtain subject failed!", new Object[0]);
                            }
                        }
                        if (subject == null) {
                            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                                SSLLogger.fine("Kerberos credentials are not present in the current Subject; check if javax.security.auth.useSubjectCredsOnly system property has been set to false", new Object[0]);
                            }
                            throw new SSLProtocolException("Server resumed session with no subject");
                        }
                        if (!subject.getPrincipals(Principal.class).contains(localPrincipal)) {
                            throw new SSLProtocolException("Server resumed session with wrong subject identity");
                        }
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                            SSLLogger.fine("Subject identity is same", new Object[0]);
                        }
                    }
                    clientHandshakeContext.isResumption = true;
                    clientHandshakeContext.resumingSession.setAsSessionResumption(true);
                    clientHandshakeContext.handshakeSession = clientHandshakeContext.resumingSession;
                }
                else {
                    if (clientHandshakeContext.resumingSession != null) {
                        clientHandshakeContext.resumingSession.invalidate();
                        clientHandshakeContext.resumingSession = null;
                    }
                    clientHandshakeContext.isResumption = false;
                    if (!clientHandshakeContext.sslConfig.enableSessionCreation) {
                        throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "New session creation is disabled");
                    }
                }
            }
            final SSLExtension[] enabledExtensions = clientHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.SERVER_HELLO);
            serverHelloMessage.extensions.consumeOnLoad(clientHandshakeContext, enabledExtensions);
            if (!clientHandshakeContext.isResumption) {
                if (clientHandshakeContext.resumingSession != null) {
                    clientHandshakeContext.resumingSession.invalidate();
                    clientHandshakeContext.resumingSession = null;
                }
                if (!clientHandshakeContext.sslConfig.enableSessionCreation) {
                    throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "New session creation is disabled");
                }
                (clientHandshakeContext.handshakeSession = new SSLSessionImpl(clientHandshakeContext, clientHandshakeContext.negotiatedCipherSuite, serverHelloMessage.sessionId)).setMaximumPacketSize(clientHandshakeContext.sslConfig.maximumPacketSize);
            }
            serverHelloMessage.extensions.consumeOnTrade(clientHandshakeContext, enabledExtensions);
            if (clientHandshakeContext.isResumption) {
                final SSLTrafficKeyDerivation value = SSLTrafficKeyDerivation.valueOf(clientHandshakeContext.negotiatedProtocol);
                if (value == null) {
                    throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + clientHandshakeContext.negotiatedProtocol);
                }
                clientHandshakeContext.handshakeKeyDerivation = value.createKeyDerivation(clientHandshakeContext, clientHandshakeContext.resumingSession.getMasterSecret());
                clientHandshakeContext.conContext.consumers.putIfAbsent(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t10Consumer);
                clientHandshakeContext.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            }
            else {
                final SSLKeyExchange value2 = SSLKeyExchange.valueOf(clientHandshakeContext.negotiatedCipherSuite.keyExchange, clientHandshakeContext.negotiatedProtocol);
                if ((clientHandshakeContext.handshakeKeyExchange = value2) != null) {
                    for (final SSLHandshake sslHandshake : value2.getRelatedHandshakers(clientHandshakeContext)) {
                        clientHandshakeContext.handshakeConsumers.put(sslHandshake.id, sslHandshake);
                    }
                }
                clientHandshakeContext.handshakeConsumers.put(SSLHandshake.SERVER_HELLO_DONE.id, SSLHandshake.SERVER_HELLO_DONE);
            }
        }
    }
    
    private static final class T13ServerHelloConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final ServerHelloMessage serverHelloMessage = (ServerHelloMessage)handshakeMessage;
            if (serverHelloMessage.serverVersion != ProtocolVersion.TLS12) {
                throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "The ServerHello.legacy_version field is not TLS 1.2");
            }
            clientHandshakeContext.negotiatedCipherSuite = serverHelloMessage.cipherSuite;
            clientHandshakeContext.handshakeHash.determine(clientHandshakeContext.negotiatedProtocol, clientHandshakeContext.negotiatedCipherSuite);
            clientHandshakeContext.serverHelloRandom = serverHelloMessage.serverRandom;
            final SSLExtension[] enabledExtensions = clientHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.SERVER_HELLO);
            serverHelloMessage.extensions.consumeOnLoad(clientHandshakeContext, enabledExtensions);
            if (!clientHandshakeContext.isResumption) {
                if (clientHandshakeContext.resumingSession != null) {
                    clientHandshakeContext.resumingSession.invalidate();
                    clientHandshakeContext.resumingSession = null;
                }
                if (!clientHandshakeContext.sslConfig.enableSessionCreation) {
                    throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "New session creation is disabled");
                }
                (clientHandshakeContext.handshakeSession = new SSLSessionImpl(clientHandshakeContext, clientHandshakeContext.negotiatedCipherSuite, serverHelloMessage.sessionId)).setMaximumPacketSize(clientHandshakeContext.sslConfig.maximumPacketSize);
            }
            else {
                final SecretKey consumePreSharedKey = clientHandshakeContext.resumingSession.consumePreSharedKey();
                if (consumePreSharedKey == null) {
                    throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "No PSK available. Unable to resume.");
                }
                clientHandshakeContext.handshakeSession = clientHandshakeContext.resumingSession;
                setUpPskKD(clientHandshakeContext, consumePreSharedKey);
            }
            serverHelloMessage.extensions.consumeOnTrade(clientHandshakeContext, enabledExtensions);
            clientHandshakeContext.handshakeHash.update();
            final SSLKeyExchange handshakeKeyExchange = clientHandshakeContext.handshakeKeyExchange;
            if (handshakeKeyExchange == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not negotiated key shares");
            }
            final SecretKey deriveKey = handshakeKeyExchange.createKeyDerivation(clientHandshakeContext).deriveKey("TlsHandshakeSecret", null);
            final SSLTrafficKeyDerivation value = SSLTrafficKeyDerivation.valueOf(clientHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + clientHandshakeContext.negotiatedProtocol);
            }
            final SSLSecretDerivation handshakeKeyDerivation = new SSLSecretDerivation(clientHandshakeContext, deriveKey);
            final SecretKey deriveKey2 = handshakeKeyDerivation.deriveKey("TlsServerHandshakeTrafficSecret", null);
            final SSLKeyDerivation keyDerivation = value.createKeyDerivation(clientHandshakeContext, deriveKey2);
            final SecretKey deriveKey3 = keyDerivation.deriveKey("TlsKey", null);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(keyDerivation.deriveKey("TlsIv", null).getEncoded());
            SSLCipher.SSLReadCipher readCipher;
            try {
                readCipher = clientHandshakeContext.negotiatedCipherSuite.bulkCipher.createReadCipher(Authenticator.valueOf(clientHandshakeContext.negotiatedProtocol), clientHandshakeContext.negotiatedProtocol, deriveKey3, ivParameterSpec, clientHandshakeContext.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing cipher algorithm", ex);
            }
            if (readCipher == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + clientHandshakeContext.negotiatedCipherSuite + ") and protocol version (" + clientHandshakeContext.negotiatedProtocol + ")");
            }
            clientHandshakeContext.baseReadSecret = deriveKey2;
            clientHandshakeContext.conContext.inputRecord.changeReadCiphers(readCipher);
            final SecretKey deriveKey4 = handshakeKeyDerivation.deriveKey("TlsClientHandshakeTrafficSecret", null);
            final SSLKeyDerivation keyDerivation2 = value.createKeyDerivation(clientHandshakeContext, deriveKey4);
            final SecretKey deriveKey5 = keyDerivation2.deriveKey("TlsKey", null);
            final IvParameterSpec ivParameterSpec2 = new IvParameterSpec(keyDerivation2.deriveKey("TlsIv", null).getEncoded());
            SSLCipher.SSLWriteCipher writeCipher;
            try {
                writeCipher = clientHandshakeContext.negotiatedCipherSuite.bulkCipher.createWriteCipher(Authenticator.valueOf(clientHandshakeContext.negotiatedProtocol), clientHandshakeContext.negotiatedProtocol, deriveKey5, ivParameterSpec2, clientHandshakeContext.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException ex2) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Missing cipher algorithm", ex2);
            }
            if (writeCipher == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + clientHandshakeContext.negotiatedCipherSuite + ") and protocol version (" + clientHandshakeContext.negotiatedProtocol + ")");
            }
            clientHandshakeContext.baseWriteSecret = deriveKey4;
            clientHandshakeContext.conContext.outputRecord.changeWriteCiphers(writeCipher, serverHelloMessage.sessionId.length() != 0);
            clientHandshakeContext.handshakeKeyDerivation = handshakeKeyDerivation;
            clientHandshakeContext.conContext.consumers.putIfAbsent(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t13Consumer);
            clientHandshakeContext.handshakeConsumers.put(SSLHandshake.ENCRYPTED_EXTENSIONS.id, SSLHandshake.ENCRYPTED_EXTENSIONS);
            clientHandshakeContext.handshakeConsumers.put(SSLHandshake.CERTIFICATE_REQUEST.id, SSLHandshake.CERTIFICATE_REQUEST);
            clientHandshakeContext.handshakeConsumers.put(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE);
            clientHandshakeContext.handshakeConsumers.put(SSLHandshake.CERTIFICATE_VERIFY.id, SSLHandshake.CERTIFICATE_VERIFY);
            clientHandshakeContext.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
        }
    }
    
    private static final class T13HelloRetryRequestConsumer implements HandshakeConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final ServerHelloMessage serverHelloMessage = (ServerHelloMessage)handshakeMessage;
            if (serverHelloMessage.serverVersion != ProtocolVersion.TLS12) {
                throw clientHandshakeContext.conContext.fatal(Alert.PROTOCOL_VERSION, "The HelloRetryRequest.legacy_version is not TLS 1.2");
            }
            clientHandshakeContext.negotiatedCipherSuite = serverHelloMessage.cipherSuite;
            final SSLExtension[] enabledExtensions = clientHandshakeContext.sslConfig.getEnabledExtensions(SSLHandshake.HELLO_RETRY_REQUEST);
            serverHelloMessage.extensions.consumeOnLoad(clientHandshakeContext, enabledExtensions);
            serverHelloMessage.extensions.consumeOnTrade(clientHandshakeContext, enabledExtensions);
            clientHandshakeContext.handshakeHash.finish();
            final HandshakeOutStream handshakeOutStream = new HandshakeOutStream(null);
            try {
                clientHandshakeContext.initialClientHelloMsg.write(handshakeOutStream);
            }
            catch (final IOException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Failed to construct message hash", ex);
            }
            clientHandshakeContext.handshakeHash.deliver(handshakeOutStream.toByteArray());
            clientHandshakeContext.handshakeHash.determine(clientHandshakeContext.negotiatedProtocol, clientHandshakeContext.negotiatedCipherSuite);
            final byte[] digest = clientHandshakeContext.handshakeHash.digest();
            final int hashLength = clientHandshakeContext.negotiatedCipherSuite.hashAlg.hashLength;
            final byte[] array = new byte[4 + hashLength];
            array[0] = SSLHandshake.MESSAGE_HASH.id;
            array[2] = (array[1] = 0);
            array[3] = (byte)(hashLength & 0xFF);
            System.arraycopy(digest, 0, array, 4, hashLength);
            clientHandshakeContext.handshakeHash.finish();
            clientHandshakeContext.handshakeHash.deliver(array);
            final int remaining = serverHelloMessage.handshakeRecord.remaining();
            final byte[] array2 = new byte[4 + remaining];
            array2[0] = SSLHandshake.HELLO_RETRY_REQUEST.id;
            array2[1] = (byte)(remaining >> 16 & 0xFF);
            array2[2] = (byte)(remaining >> 8 & 0xFF);
            array2[3] = (byte)(remaining & 0xFF);
            serverHelloMessage.handshakeRecord.duplicate().get(array2, 4, remaining);
            clientHandshakeContext.handshakeHash.receive(array2);
            clientHandshakeContext.initialClientHelloMsg.extensions.reproduce(clientHandshakeContext, new SSLExtension[] { SSLExtension.CH_COOKIE, SSLExtension.CH_KEY_SHARE, SSLExtension.CH_PRE_SHARED_KEY });
            SSLHandshake.CLIENT_HELLO.produce(connectionContext, serverHelloMessage);
        }
    }
}
