package sun.security.ssl;

import java.nio.ByteBuffer;
import java.io.IOException;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLException;

final class ChangeCipherSpec
{
    static final SSLConsumer t10Consumer;
    static final HandshakeProducer t10Producer;
    static final SSLConsumer t13Consumer;
    
    static {
        t10Consumer = new T10ChangeCipherSpecConsumer();
        t10Producer = new T10ChangeCipherSpecProducer();
        t13Consumer = new T13ChangeCipherSpecConsumer();
    }
    
    private static final class T10ChangeCipherSpecProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final HandshakeContext handshakeContext = (HandshakeContext)connectionContext;
            final SSLKeyDerivation handshakeKeyDerivation = handshakeContext.handshakeKeyDerivation;
            if (!(handshakeKeyDerivation instanceof SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation)) {
                throw new UnsupportedOperationException("Not supported.");
            }
            final SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation legacyTrafficKeyDerivation = (SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation)handshakeKeyDerivation;
            final CipherSuite negotiatedCipherSuite = handshakeContext.negotiatedCipherSuite;
            Authenticator authenticator;
            if (negotiatedCipherSuite.bulkCipher.cipherType == CipherType.AEAD_CIPHER) {
                authenticator = Authenticator.valueOf(handshakeContext.negotiatedProtocol);
            }
            else {
                try {
                    authenticator = Authenticator.valueOf(handshakeContext.negotiatedProtocol, negotiatedCipherSuite.macAlg, legacyTrafficKeyDerivation.getTrafficKey(handshakeContext.sslConfig.isClientMode ? "clientMacKey" : "serverMacKey"));
                }
                catch (final NoSuchAlgorithmException | InvalidKeyException ex) {
                    throw new SSLException("Algorithm missing:  ", (Throwable)ex);
                }
            }
            final SecretKey trafficKey = legacyTrafficKeyDerivation.getTrafficKey(handshakeContext.sslConfig.isClientMode ? "clientWriteKey" : "serverWriteKey");
            final SecretKey trafficKey2 = legacyTrafficKeyDerivation.getTrafficKey(handshakeContext.sslConfig.isClientMode ? "clientWriteIv" : "serverWriteIv");
            final IvParameterSpec ivParameterSpec = (trafficKey2 == null) ? null : new IvParameterSpec(trafficKey2.getEncoded());
            SSLCipher.SSLWriteCipher writeCipher;
            try {
                writeCipher = negotiatedCipherSuite.bulkCipher.createWriteCipher(authenticator, handshakeContext.negotiatedProtocol, trafficKey, ivParameterSpec, handshakeContext.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException ex2) {
                throw new SSLException("Algorithm missing:  ", ex2);
            }
            if (writeCipher == null) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + negotiatedCipherSuite + ") and protocol version (" + handshakeContext.negotiatedProtocol + ")");
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ChangeCipherSpec message", new Object[0]);
            }
            handshakeContext.conContext.outputRecord.changeWriteCiphers(writeCipher, true);
            return null;
        }
    }
    
    private static final class T10ChangeCipherSpecConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final TransportContext transportContext = (TransportContext)connectionContext;
            transportContext.consumers.remove(ContentType.CHANGE_CIPHER_SPEC.id);
            if (byteBuffer.remaining() != 1 || byteBuffer.get() != 1) {
                throw transportContext.fatal(Alert.UNEXPECTED_MESSAGE, "Malformed or unexpected ChangeCipherSpec message");
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ChangeCipherSpec message", new Object[0]);
            }
            if (transportContext.handshakeContext == null) {
                throw transportContext.fatal(Alert.HANDSHAKE_FAILURE, "Unexpected ChangeCipherSpec message");
            }
            final HandshakeContext handshakeContext = transportContext.handshakeContext;
            if (handshakeContext.handshakeKeyDerivation == null) {
                throw transportContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ChangeCipherSpec message");
            }
            final SSLKeyDerivation handshakeKeyDerivation = handshakeContext.handshakeKeyDerivation;
            if (!(handshakeKeyDerivation instanceof SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation)) {
                throw new UnsupportedOperationException("Not supported.");
            }
            final SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation legacyTrafficKeyDerivation = (SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation)handshakeKeyDerivation;
            final CipherSuite negotiatedCipherSuite = handshakeContext.negotiatedCipherSuite;
            Authenticator authenticator;
            if (negotiatedCipherSuite.bulkCipher.cipherType == CipherType.AEAD_CIPHER) {
                authenticator = Authenticator.valueOf(handshakeContext.negotiatedProtocol);
            }
            else {
                try {
                    authenticator = Authenticator.valueOf(handshakeContext.negotiatedProtocol, negotiatedCipherSuite.macAlg, legacyTrafficKeyDerivation.getTrafficKey(handshakeContext.sslConfig.isClientMode ? "serverMacKey" : "clientMacKey"));
                }
                catch (final NoSuchAlgorithmException | InvalidKeyException ex) {
                    throw new SSLException("Algorithm missing:  ", (Throwable)ex);
                }
            }
            final SecretKey trafficKey = legacyTrafficKeyDerivation.getTrafficKey(handshakeContext.sslConfig.isClientMode ? "serverWriteKey" : "clientWriteKey");
            final SecretKey trafficKey2 = legacyTrafficKeyDerivation.getTrafficKey(handshakeContext.sslConfig.isClientMode ? "serverWriteIv" : "clientWriteIv");
            final IvParameterSpec ivParameterSpec = (trafficKey2 == null) ? null : new IvParameterSpec(trafficKey2.getEncoded());
            SSLCipher.SSLReadCipher readCipher;
            try {
                readCipher = negotiatedCipherSuite.bulkCipher.createReadCipher(authenticator, handshakeContext.negotiatedProtocol, trafficKey, ivParameterSpec, handshakeContext.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException ex2) {
                throw new SSLException("Algorithm missing:  ", ex2);
            }
            if (readCipher == null) {
                throw handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + handshakeContext.negotiatedCipherSuite + ") and protocol version (" + handshakeContext.negotiatedProtocol + ")");
            }
            transportContext.inputRecord.changeReadCiphers(readCipher);
        }
    }
    
    private static final class T13ChangeCipherSpecConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final TransportContext transportContext = (TransportContext)connectionContext;
            transportContext.consumers.remove(ContentType.CHANGE_CIPHER_SPEC.id);
            if (byteBuffer.remaining() != 1 || byteBuffer.get() != 1) {
                throw transportContext.fatal(Alert.UNEXPECTED_MESSAGE, "Malformed or unexpected ChangeCipherSpec message");
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ChangeCipherSpec message", new Object[0]);
            }
        }
    }
}
