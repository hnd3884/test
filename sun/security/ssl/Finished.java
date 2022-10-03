package sun.security.ssl;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Mac;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.internal.spec.TlsPrfParameterSpec;
import sun.misc.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.security.MessageDigest;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.time.Instant;
import javax.net.ssl.SSLPeerUnverifiedException;
import jdk.internal.event.EventHelper;
import jdk.jfr.events.TLSHandshakeEvent;

final class Finished
{
    static final SSLConsumer t12HandshakeConsumer;
    static final HandshakeProducer t12HandshakeProducer;
    static final SSLConsumer t13HandshakeConsumer;
    static final HandshakeProducer t13HandshakeProducer;
    
    private static void recordEvent(final SSLSessionImpl sslSessionImpl) {
        final TLSHandshakeEvent tlsHandshakeEvent = new TLSHandshakeEvent();
        if (tlsHandshakeEvent.shouldCommit() || EventHelper.isLoggingSecurity()) {
            int hashCode = 0;
            try {
                hashCode = sslSessionImpl.getCertificateChain()[0].hashCode();
            }
            catch (final SSLPeerUnverifiedException ex) {}
            if (tlsHandshakeEvent.shouldCommit()) {
                tlsHandshakeEvent.peerHost = sslSessionImpl.getPeerHost();
                tlsHandshakeEvent.peerPort = sslSessionImpl.getPeerPort();
                tlsHandshakeEvent.cipherSuite = sslSessionImpl.getCipherSuite();
                tlsHandshakeEvent.protocolVersion = sslSessionImpl.getProtocol();
                tlsHandshakeEvent.certificateId = hashCode;
                tlsHandshakeEvent.commit();
            }
            if (EventHelper.isLoggingSecurity()) {
                EventHelper.logTLSHandshakeEvent((Instant)null, sslSessionImpl.getPeerHost(), sslSessionImpl.getPeerPort(), sslSessionImpl.getCipherSuite(), sslSessionImpl.getProtocol(), (long)hashCode);
            }
        }
    }
    
    static {
        t12HandshakeConsumer = new T12FinishedConsumer();
        t12HandshakeProducer = new T12FinishedProducer();
        t13HandshakeConsumer = new T13FinishedConsumer();
        t13HandshakeProducer = new T13FinishedProducer();
    }
    
    private static final class FinishedMessage extends SSLHandshake.HandshakeMessage
    {
        private final byte[] verifyData;
        
        FinishedMessage(final HandshakeContext handshakeContext) throws IOException {
            super(handshakeContext);
            final VerifyDataScheme value = VerifyDataScheme.valueOf(handshakeContext.negotiatedProtocol);
            byte[] verifyData;
            try {
                verifyData = value.createVerifyData(handshakeContext, false);
            }
            catch (final IOException ex) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Failed to generate verify_data", ex);
            }
            this.verifyData = verifyData;
        }
        
        FinishedMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            int hashLength = 12;
            if (handshakeContext.negotiatedProtocol == ProtocolVersion.SSL30) {
                hashLength = 36;
            }
            else if (handshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
                hashLength = handshakeContext.negotiatedCipherSuite.hashAlg.hashLength;
            }
            if (byteBuffer.remaining() != hashLength) {
                throw handshakeContext.conContext.fatal(Alert.DECODE_ERROR, "Inappropriate finished message: need " + hashLength + " but remaining " + byteBuffer.remaining() + " bytes verify_data");
            }
            byteBuffer.get(this.verifyData = new byte[hashLength]);
            final VerifyDataScheme value = VerifyDataScheme.valueOf(handshakeContext.negotiatedProtocol);
            byte[] verifyData;
            try {
                verifyData = value.createVerifyData(handshakeContext, true);
            }
            catch (final IOException ex) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Failed to generate verify_data", ex);
            }
            if (!MessageDigest.isEqual(verifyData, this.verifyData)) {
                throw handshakeContext.conContext.fatal(Alert.DECRYPT_ERROR, "The Finished message cannot be verified.");
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.FINISHED;
        }
        
        public int messageLength() {
            return this.verifyData.length;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.write(this.verifyData);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"Finished\": '{'\n  \"verify data\": '{'\n{0}\n  '}''}'", Locale.ENGLISH).format(new Object[] { Utilities.indent(new HexDumpEncoder().encode(this.verifyData), "    ") });
        }
    }
    
    enum VerifyDataScheme
    {
        SSL30("kdf_ssl30", (VerifyDataGenerator)new S30VerifyDataGenerator()), 
        TLS10("kdf_tls10", (VerifyDataGenerator)new T10VerifyDataGenerator()), 
        TLS12("kdf_tls12", (VerifyDataGenerator)new T12VerifyDataGenerator()), 
        TLS13("kdf_tls13", (VerifyDataGenerator)new T13VerifyDataGenerator());
        
        final String name;
        final VerifyDataGenerator generator;
        
        private VerifyDataScheme(final String name, final VerifyDataGenerator generator) {
            this.name = name;
            this.generator = generator;
        }
        
        static VerifyDataScheme valueOf(final ProtocolVersion protocolVersion) {
            switch (protocolVersion) {
                case SSL30: {
                    return VerifyDataScheme.SSL30;
                }
                case TLS10:
                case TLS11: {
                    return VerifyDataScheme.TLS10;
                }
                case TLS12: {
                    return VerifyDataScheme.TLS12;
                }
                case TLS13: {
                    return VerifyDataScheme.TLS13;
                }
                default: {
                    return null;
                }
            }
        }
        
        public byte[] createVerifyData(final HandshakeContext handshakeContext, final boolean b) throws IOException {
            if (this.generator != null) {
                return this.generator.createVerifyData(handshakeContext, b);
            }
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private static final class S30VerifyDataGenerator implements VerifyDataGenerator
    {
        @Override
        public byte[] createVerifyData(final HandshakeContext handshakeContext, final boolean b) throws IOException {
            return handshakeContext.handshakeHash.digest((handshakeContext.sslConfig.isClientMode && !b) || (!handshakeContext.sslConfig.isClientMode && b), handshakeContext.handshakeSession.getMasterSecret());
        }
    }
    
    private static final class T10VerifyDataGenerator implements VerifyDataGenerator
    {
        @Override
        public byte[] createVerifyData(final HandshakeContext handshakeContext, final boolean b) throws IOException {
            final HandshakeHash handshakeHash = handshakeContext.handshakeHash;
            final SecretKey masterSecret = handshakeContext.handshakeSession.getMasterSecret();
            String s;
            if ((handshakeContext.sslConfig.isClientMode && !b) || (!handshakeContext.sslConfig.isClientMode && b)) {
                s = "client finished";
            }
            else {
                s = "server finished";
            }
            try {
                final byte[] digest = handshakeHash.digest();
                final String s2 = "SunTlsPrf";
                final CipherSuite.HashAlg h_NONE = CipherSuite.HashAlg.H_NONE;
                final TlsPrfParameterSpec tlsPrfParameterSpec = new TlsPrfParameterSpec(masterSecret, s, digest, 12, h_NONE.name, h_NONE.hashLength, h_NONE.blockSize);
                final KeyGenerator keyGenerator = JsseJce.getKeyGenerator(s2);
                keyGenerator.init(tlsPrfParameterSpec);
                final SecretKey generateKey = keyGenerator.generateKey();
                if (!"RAW".equals(generateKey.getFormat())) {
                    throw new ProviderException("Invalid PRF output, format must be RAW. Format received: " + generateKey.getFormat());
                }
                return generateKey.getEncoded();
            }
            catch (final GeneralSecurityException ex) {
                throw new RuntimeException("PRF failed", ex);
            }
        }
    }
    
    private static final class T12VerifyDataGenerator implements VerifyDataGenerator
    {
        @Override
        public byte[] createVerifyData(final HandshakeContext handshakeContext, final boolean b) throws IOException {
            final CipherSuite negotiatedCipherSuite = handshakeContext.negotiatedCipherSuite;
            final HandshakeHash handshakeHash = handshakeContext.handshakeHash;
            final SecretKey masterSecret = handshakeContext.handshakeSession.getMasterSecret();
            String s;
            if ((handshakeContext.sslConfig.isClientMode && !b) || (!handshakeContext.sslConfig.isClientMode && b)) {
                s = "client finished";
            }
            else {
                s = "server finished";
            }
            try {
                final byte[] digest = handshakeHash.digest();
                final String s2 = "SunTls12Prf";
                final CipherSuite.HashAlg hashAlg = negotiatedCipherSuite.hashAlg;
                final TlsPrfParameterSpec tlsPrfParameterSpec = new TlsPrfParameterSpec(masterSecret, s, digest, 12, hashAlg.name, hashAlg.hashLength, hashAlg.blockSize);
                final KeyGenerator keyGenerator = JsseJce.getKeyGenerator(s2);
                keyGenerator.init(tlsPrfParameterSpec);
                final SecretKey generateKey = keyGenerator.generateKey();
                if (!"RAW".equals(generateKey.getFormat())) {
                    throw new ProviderException("Invalid PRF output, format must be RAW. Format received: " + generateKey.getFormat());
                }
                return generateKey.getEncoded();
            }
            catch (final GeneralSecurityException ex) {
                throw new RuntimeException("PRF failed", ex);
            }
        }
    }
    
    private static final class T13VerifyDataGenerator implements VerifyDataGenerator
    {
        private static final byte[] hkdfLabel;
        private static final byte[] hkdfContext;
        
        @Override
        public byte[] createVerifyData(final HandshakeContext handshakeContext, final boolean b) throws IOException {
            final CipherSuite.HashAlg hashAlg = handshakeContext.negotiatedCipherSuite.hashAlg;
            final SecretKey deriveKey = new SSLBasicKeyDerivation(b ? handshakeContext.baseReadSecret : handshakeContext.baseWriteSecret, hashAlg.name, T13VerifyDataGenerator.hkdfLabel, T13VerifyDataGenerator.hkdfContext, hashAlg.hashLength).deriveKey("TlsFinishedSecret", new SSLBasicKeyDerivation.SecretSizeSpec(hashAlg.hashLength));
            final String string = "Hmac" + hashAlg.name.replace("-", "");
            try {
                final Mac mac = JsseJce.getMac(string);
                mac.init(deriveKey);
                return mac.doFinal(handshakeContext.handshakeHash.digest());
            }
            catch (final NoSuchAlgorithmException | InvalidKeyException ex) {
                throw new ProviderException("Failed to generate verify_data", (Throwable)ex);
            }
        }
        
        static {
            hkdfLabel = "tls13 finished".getBytes();
            hkdfContext = new byte[0];
        }
    }
    
    private static final class T12FinishedProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            if (((HandshakeContext)connectionContext).sslConfig.isClientMode) {
                return this.onProduceFinished((ClientHandshakeContext)connectionContext, handshakeMessage);
            }
            return this.onProduceFinished((ServerHandshakeContext)connectionContext, handshakeMessage);
        }
        
        private byte[] onProduceFinished(final ClientHandshakeContext clientHandshakeContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            clientHandshakeContext.handshakeHash.update();
            final FinishedMessage finishedMessage = new FinishedMessage(clientHandshakeContext);
            ChangeCipherSpec.t10Producer.produce(clientHandshakeContext, handshakeMessage);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced client Finished handshake message", finishedMessage);
            }
            finishedMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            if (clientHandshakeContext.conContext.secureRenegotiation) {
                clientHandshakeContext.conContext.clientVerifyData = finishedMessage.verifyData;
            }
            if (!clientHandshakeContext.isResumption) {
                clientHandshakeContext.conContext.consumers.put(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t10Consumer);
                clientHandshakeContext.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            }
            else {
                if (clientHandshakeContext.handshakeSession.isRejoinable()) {
                    ((SSLSessionContextImpl)clientHandshakeContext.sslContext.engineGetClientSessionContext()).put(clientHandshakeContext.handshakeSession);
                }
                clientHandshakeContext.conContext.conSession = clientHandshakeContext.handshakeSession.finish();
                clientHandshakeContext.conContext.protocolVersion = clientHandshakeContext.negotiatedProtocol;
                clientHandshakeContext.handshakeFinished = true;
                clientHandshakeContext.conContext.finishHandshake();
            }
            return null;
        }
        
        private byte[] onProduceFinished(final ServerHandshakeContext serverHandshakeContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            serverHandshakeContext.handshakeHash.update();
            final FinishedMessage finishedMessage = new FinishedMessage(serverHandshakeContext);
            ChangeCipherSpec.t10Producer.produce(serverHandshakeContext, handshakeMessage);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server Finished handshake message", finishedMessage);
            }
            finishedMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            if (serverHandshakeContext.conContext.secureRenegotiation) {
                serverHandshakeContext.conContext.serverVerifyData = finishedMessage.verifyData;
            }
            if (serverHandshakeContext.isResumption) {
                serverHandshakeContext.conContext.consumers.put(ContentType.CHANGE_CIPHER_SPEC.id, ChangeCipherSpec.t10Consumer);
                serverHandshakeContext.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            }
            else {
                if (serverHandshakeContext.handshakeSession.isRejoinable()) {
                    ((SSLSessionContextImpl)serverHandshakeContext.sslContext.engineGetServerSessionContext()).put(serverHandshakeContext.handshakeSession);
                }
                serverHandshakeContext.conContext.conSession = serverHandshakeContext.handshakeSession.finish();
                serverHandshakeContext.conContext.protocolVersion = serverHandshakeContext.negotiatedProtocol;
                serverHandshakeContext.handshakeFinished = true;
                serverHandshakeContext.conContext.finishHandshake();
            }
            return null;
        }
    }
    
    private static final class T12FinishedConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final HandshakeContext handshakeContext = (HandshakeContext)connectionContext;
            handshakeContext.handshakeConsumers.remove(SSLHandshake.FINISHED.id);
            if (handshakeContext.conContext.consumers.containsKey(ContentType.CHANGE_CIPHER_SPEC.id)) {
                throw handshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Missing ChangeCipherSpec message");
            }
            if (handshakeContext.sslConfig.isClientMode) {
                this.onConsumeFinished((ClientHandshakeContext)connectionContext, byteBuffer);
            }
            else {
                this.onConsumeFinished((ServerHandshakeContext)connectionContext, byteBuffer);
            }
        }
        
        private void onConsumeFinished(final ClientHandshakeContext clientHandshakeContext, final ByteBuffer byteBuffer) throws IOException {
            final FinishedMessage finishedMessage = new FinishedMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming server Finished handshake message", finishedMessage);
            }
            if (clientHandshakeContext.conContext.secureRenegotiation) {
                clientHandshakeContext.conContext.serverVerifyData = finishedMessage.verifyData;
            }
            if (!clientHandshakeContext.isResumption) {
                if (clientHandshakeContext.handshakeSession.isRejoinable()) {
                    ((SSLSessionContextImpl)clientHandshakeContext.sslContext.engineGetClientSessionContext()).put(clientHandshakeContext.handshakeSession);
                }
                clientHandshakeContext.conContext.conSession = clientHandshakeContext.handshakeSession.finish();
                clientHandshakeContext.conContext.protocolVersion = clientHandshakeContext.negotiatedProtocol;
                clientHandshakeContext.handshakeFinished = true;
                recordEvent(clientHandshakeContext.conContext.conSession);
                clientHandshakeContext.conContext.finishHandshake();
            }
            else {
                clientHandshakeContext.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            }
            final SSLHandshake[] array = { SSLHandshake.FINISHED };
            for (int length = array.length, i = 0; i < length; ++i) {
                final HandshakeProducer handshakeProducer = clientHandshakeContext.handshakeProducers.remove(array[i].id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(clientHandshakeContext, finishedMessage);
                }
            }
        }
        
        private void onConsumeFinished(final ServerHandshakeContext serverHandshakeContext, final ByteBuffer byteBuffer) throws IOException {
            if (!serverHandshakeContext.isResumption && serverHandshakeContext.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE_VERIFY.id)) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected Finished handshake message");
            }
            final FinishedMessage finishedMessage = new FinishedMessage(serverHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming client Finished handshake message", finishedMessage);
            }
            if (serverHandshakeContext.conContext.secureRenegotiation) {
                serverHandshakeContext.conContext.clientVerifyData = finishedMessage.verifyData;
            }
            if (serverHandshakeContext.isResumption) {
                if (serverHandshakeContext.handshakeSession.isRejoinable()) {
                    ((SSLSessionContextImpl)serverHandshakeContext.sslContext.engineGetServerSessionContext()).put(serverHandshakeContext.handshakeSession);
                }
                serverHandshakeContext.conContext.conSession = serverHandshakeContext.handshakeSession.finish();
                serverHandshakeContext.conContext.protocolVersion = serverHandshakeContext.negotiatedProtocol;
                serverHandshakeContext.handshakeFinished = true;
                recordEvent(serverHandshakeContext.conContext.conSession);
                serverHandshakeContext.conContext.finishHandshake();
            }
            else {
                serverHandshakeContext.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            }
            final SSLHandshake[] array = { SSLHandshake.FINISHED };
            for (int length = array.length, i = 0; i < length; ++i) {
                final HandshakeProducer handshakeProducer = serverHandshakeContext.handshakeProducers.remove(array[i].id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(serverHandshakeContext, finishedMessage);
                }
            }
        }
    }
    
    private static final class T13FinishedProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            if (((HandshakeContext)connectionContext).sslConfig.isClientMode) {
                return this.onProduceFinished((ClientHandshakeContext)connectionContext, handshakeMessage);
            }
            return this.onProduceFinished((ServerHandshakeContext)connectionContext, handshakeMessage);
        }
        
        private byte[] onProduceFinished(final ClientHandshakeContext clientHandshakeContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            clientHandshakeContext.handshakeHash.update();
            final FinishedMessage finishedMessage = new FinishedMessage(clientHandshakeContext);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced client Finished handshake message", finishedMessage);
            }
            finishedMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            if (clientHandshakeContext.conContext.secureRenegotiation) {
                clientHandshakeContext.conContext.clientVerifyData = finishedMessage.verifyData;
            }
            final SSLKeyDerivation handshakeKeyDerivation = clientHandshakeContext.handshakeKeyDerivation;
            if (handshakeKeyDerivation == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SSLTrafficKeyDerivation value = SSLTrafficKeyDerivation.valueOf(clientHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + clientHandshakeContext.negotiatedProtocol);
            }
            try {
                final SecretKey deriveKey = handshakeKeyDerivation.deriveKey("TlsClientAppTrafficSecret", null);
                final SSLKeyDerivation keyDerivation = value.createKeyDerivation(clientHandshakeContext, deriveKey);
                final SSLCipher.SSLWriteCipher writeCipher = clientHandshakeContext.negotiatedCipherSuite.bulkCipher.createWriteCipher(Authenticator.valueOf(clientHandshakeContext.negotiatedProtocol), clientHandshakeContext.negotiatedProtocol, keyDerivation.deriveKey("TlsKey", null), new IvParameterSpec(keyDerivation.deriveKey("TlsIv", null).getEncoded()), clientHandshakeContext.sslContext.getSecureRandom());
                if (writeCipher == null) {
                    throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + clientHandshakeContext.negotiatedCipherSuite + ") and protocol version (" + clientHandshakeContext.negotiatedProtocol + ")");
                }
                clientHandshakeContext.baseWriteSecret = deriveKey;
                clientHandshakeContext.conContext.outputRecord.changeWriteCiphers(writeCipher, false);
            }
            catch (final GeneralSecurityException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive application secrets", ex);
            }
            clientHandshakeContext.handshakeSession.setResumptionMasterSecret(((SSLSecretDerivation)handshakeKeyDerivation).forContext(clientHandshakeContext).deriveKey("TlsResumptionMasterSecret", null));
            clientHandshakeContext.conContext.conSession = clientHandshakeContext.handshakeSession.finish();
            clientHandshakeContext.conContext.protocolVersion = clientHandshakeContext.negotiatedProtocol;
            clientHandshakeContext.handshakeFinished = true;
            clientHandshakeContext.conContext.finishHandshake();
            recordEvent(clientHandshakeContext.conContext.conSession);
            return null;
        }
        
        private byte[] onProduceFinished(final ServerHandshakeContext serverHandshakeContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            serverHandshakeContext.handshakeHash.update();
            final FinishedMessage finishedMessage = new FinishedMessage(serverHandshakeContext);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced server Finished handshake message", finishedMessage);
            }
            finishedMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            final SSLKeyDerivation handshakeKeyDerivation = serverHandshakeContext.handshakeKeyDerivation;
            if (handshakeKeyDerivation == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SSLTrafficKeyDerivation value = SSLTrafficKeyDerivation.valueOf(serverHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + serverHandshakeContext.negotiatedProtocol);
            }
            try {
                final SecretKey deriveKey = handshakeKeyDerivation.deriveKey("TlsSaltSecret", null);
                final CipherSuite.HashAlg hashAlg = serverHandshakeContext.negotiatedCipherSuite.hashAlg;
                final SSLSecretDerivation handshakeKeyDerivation2 = new SSLSecretDerivation(serverHandshakeContext, new HKDF(hashAlg.name).extract(deriveKey, new SecretKeySpec(new byte[hashAlg.hashLength], "TlsZeroSecret"), "TlsMasterSecret"));
                final SecretKey deriveKey2 = handshakeKeyDerivation2.deriveKey("TlsServerAppTrafficSecret", null);
                final SSLKeyDerivation keyDerivation = value.createKeyDerivation(serverHandshakeContext, deriveKey2);
                final SSLCipher.SSLWriteCipher writeCipher = serverHandshakeContext.negotiatedCipherSuite.bulkCipher.createWriteCipher(Authenticator.valueOf(serverHandshakeContext.negotiatedProtocol), serverHandshakeContext.negotiatedProtocol, keyDerivation.deriveKey("TlsKey", null), new IvParameterSpec(keyDerivation.deriveKey("TlsIv", null).getEncoded()), serverHandshakeContext.sslContext.getSecureRandom());
                if (writeCipher == null) {
                    throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + serverHandshakeContext.negotiatedCipherSuite + ") and protocol version (" + serverHandshakeContext.negotiatedProtocol + ")");
                }
                serverHandshakeContext.baseWriteSecret = deriveKey2;
                serverHandshakeContext.conContext.outputRecord.changeWriteCiphers(writeCipher, false);
                serverHandshakeContext.handshakeKeyDerivation = handshakeKeyDerivation2;
            }
            catch (final GeneralSecurityException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive application secrets", ex);
            }
            if (serverHandshakeContext.conContext.secureRenegotiation) {
                serverHandshakeContext.conContext.serverVerifyData = finishedMessage.verifyData;
            }
            serverHandshakeContext.handshakeConsumers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            return null;
        }
    }
    
    private static final class T13FinishedConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            if (((HandshakeContext)connectionContext).sslConfig.isClientMode) {
                this.onConsumeFinished((ClientHandshakeContext)connectionContext, byteBuffer);
            }
            else {
                this.onConsumeFinished((ServerHandshakeContext)connectionContext, byteBuffer);
            }
        }
        
        private void onConsumeFinished(final ClientHandshakeContext clientHandshakeContext, final ByteBuffer byteBuffer) throws IOException {
            if (!clientHandshakeContext.isResumption && (clientHandshakeContext.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE.id) || clientHandshakeContext.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE_VERIFY.id))) {
                throw clientHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected Finished handshake message");
            }
            final FinishedMessage finishedMessage = new FinishedMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming server Finished handshake message", finishedMessage);
            }
            if (clientHandshakeContext.conContext.secureRenegotiation) {
                clientHandshakeContext.conContext.serverVerifyData = finishedMessage.verifyData;
            }
            clientHandshakeContext.conContext.consumers.remove(ContentType.CHANGE_CIPHER_SPEC.id);
            clientHandshakeContext.handshakeHash.update();
            final SSLKeyDerivation handshakeKeyDerivation = clientHandshakeContext.handshakeKeyDerivation;
            if (handshakeKeyDerivation == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SSLTrafficKeyDerivation value = SSLTrafficKeyDerivation.valueOf(clientHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + clientHandshakeContext.negotiatedProtocol);
            }
            if (!clientHandshakeContext.isResumption && clientHandshakeContext.handshakeSession.isRejoinable()) {
                ((SSLSessionContextImpl)clientHandshakeContext.sslContext.engineGetClientSessionContext()).put(clientHandshakeContext.handshakeSession);
            }
            try {
                final SecretKey deriveKey = handshakeKeyDerivation.deriveKey("TlsSaltSecret", null);
                final CipherSuite.HashAlg hashAlg = clientHandshakeContext.negotiatedCipherSuite.hashAlg;
                final SSLSecretDerivation handshakeKeyDerivation2 = new SSLSecretDerivation(clientHandshakeContext, new HKDF(hashAlg.name).extract(deriveKey, new SecretKeySpec(new byte[hashAlg.hashLength], "TlsZeroSecret"), "TlsMasterSecret"));
                final SecretKey deriveKey2 = handshakeKeyDerivation2.deriveKey("TlsServerAppTrafficSecret", null);
                final SSLKeyDerivation keyDerivation = value.createKeyDerivation(clientHandshakeContext, deriveKey2);
                final SSLCipher.SSLReadCipher readCipher = clientHandshakeContext.negotiatedCipherSuite.bulkCipher.createReadCipher(Authenticator.valueOf(clientHandshakeContext.negotiatedProtocol), clientHandshakeContext.negotiatedProtocol, keyDerivation.deriveKey("TlsKey", null), new IvParameterSpec(keyDerivation.deriveKey("TlsIv", null).getEncoded()), clientHandshakeContext.sslContext.getSecureRandom());
                if (readCipher == null) {
                    throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + clientHandshakeContext.negotiatedCipherSuite + ") and protocol version (" + clientHandshakeContext.negotiatedProtocol + ")");
                }
                clientHandshakeContext.baseReadSecret = deriveKey2;
                clientHandshakeContext.conContext.inputRecord.changeReadCiphers(readCipher);
                clientHandshakeContext.handshakeKeyDerivation = handshakeKeyDerivation2;
            }
            catch (final GeneralSecurityException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive application secrets", ex);
            }
            clientHandshakeContext.handshakeProducers.put(SSLHandshake.FINISHED.id, SSLHandshake.FINISHED);
            final SSLHandshake[] array = { SSLHandshake.CERTIFICATE, SSLHandshake.CERTIFICATE_VERIFY, SSLHandshake.FINISHED };
            for (int length = array.length, i = 0; i < length; ++i) {
                final HandshakeProducer handshakeProducer = clientHandshakeContext.handshakeProducers.remove(array[i].id);
                if (handshakeProducer != null) {
                    handshakeProducer.produce(clientHandshakeContext, null);
                }
            }
        }
        
        private void onConsumeFinished(final ServerHandshakeContext serverHandshakeContext, final ByteBuffer byteBuffer) throws IOException {
            if (!serverHandshakeContext.isResumption && (serverHandshakeContext.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE.id) || serverHandshakeContext.handshakeConsumers.containsKey(SSLHandshake.CERTIFICATE_VERIFY.id))) {
                throw serverHandshakeContext.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected Finished handshake message");
            }
            final FinishedMessage finishedMessage = new FinishedMessage(serverHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming client Finished handshake message", finishedMessage);
            }
            if (serverHandshakeContext.conContext.secureRenegotiation) {
                serverHandshakeContext.conContext.clientVerifyData = finishedMessage.verifyData;
            }
            final SSLKeyDerivation handshakeKeyDerivation = serverHandshakeContext.handshakeKeyDerivation;
            if (handshakeKeyDerivation == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SSLTrafficKeyDerivation value = SSLTrafficKeyDerivation.valueOf(serverHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + serverHandshakeContext.negotiatedProtocol);
            }
            if (!serverHandshakeContext.isResumption && serverHandshakeContext.handshakeSession.isRejoinable()) {
                ((SSLSessionContextImpl)serverHandshakeContext.sslContext.engineGetServerSessionContext()).put(serverHandshakeContext.handshakeSession);
            }
            try {
                final SecretKey deriveKey = handshakeKeyDerivation.deriveKey("TlsClientAppTrafficSecret", null);
                final SSLKeyDerivation keyDerivation = value.createKeyDerivation(serverHandshakeContext, deriveKey);
                final SSLCipher.SSLReadCipher readCipher = serverHandshakeContext.negotiatedCipherSuite.bulkCipher.createReadCipher(Authenticator.valueOf(serverHandshakeContext.negotiatedProtocol), serverHandshakeContext.negotiatedProtocol, keyDerivation.deriveKey("TlsKey", null), new IvParameterSpec(keyDerivation.deriveKey("TlsIv", null).getEncoded()), serverHandshakeContext.sslContext.getSecureRandom());
                if (readCipher == null) {
                    throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + serverHandshakeContext.negotiatedCipherSuite + ") and protocol version (" + serverHandshakeContext.negotiatedProtocol + ")");
                }
                serverHandshakeContext.baseReadSecret = deriveKey;
                serverHandshakeContext.conContext.inputRecord.changeReadCiphers(readCipher);
                serverHandshakeContext.handshakeHash.update();
                serverHandshakeContext.handshakeSession.setResumptionMasterSecret(((SSLSecretDerivation)handshakeKeyDerivation).forContext(serverHandshakeContext).deriveKey("TlsResumptionMasterSecret", null));
            }
            catch (final GeneralSecurityException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive application secrets", ex);
            }
            serverHandshakeContext.conContext.conSession = serverHandshakeContext.handshakeSession.finish();
            serverHandshakeContext.conContext.protocolVersion = serverHandshakeContext.negotiatedProtocol;
            serverHandshakeContext.handshakeFinished = true;
            serverHandshakeContext.conContext.finishHandshake();
            recordEvent(serverHandshakeContext.conContext.conSession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Sending new session ticket", new Object[0]);
            }
            NewSessionTicket.kickstartProducer.produce(serverHandshakeContext);
        }
    }
    
    interface VerifyDataGenerator
    {
        byte[] createVerifyData(final HandshakeContext p0, final boolean p1) throws IOException;
    }
}
