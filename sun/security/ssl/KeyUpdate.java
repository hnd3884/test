package sun.security.ssl;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import java.nio.ByteBuffer;

final class KeyUpdate
{
    static final SSLProducer kickstartProducer;
    static final SSLConsumer handshakeConsumer;
    static final HandshakeProducer handshakeProducer;
    
    static {
        kickstartProducer = new KeyUpdateKickstartProducer();
        handshakeConsumer = new KeyUpdateConsumer();
        handshakeProducer = new KeyUpdateProducer();
    }
    
    static final class KeyUpdateMessage extends SSLHandshake.HandshakeMessage
    {
        private final KeyUpdateRequest status;
        
        KeyUpdateMessage(final PostHandshakeContext postHandshakeContext, final KeyUpdateRequest status) {
            super(postHandshakeContext);
            this.status = status;
        }
        
        KeyUpdateMessage(final PostHandshakeContext postHandshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(postHandshakeContext);
            if (byteBuffer.remaining() != 1) {
                throw postHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "KeyUpdate has an unexpected length of " + byteBuffer.remaining());
            }
            final byte value = byteBuffer.get();
            this.status = KeyUpdateRequest.valueOf(value);
            if (this.status == null) {
                throw postHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid KeyUpdate message value: " + KeyUpdateRequest.nameOf(value));
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.KEY_UPDATE;
        }
        
        public int messageLength() {
            return 1;
        }
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putInt8(this.status.id);
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"KeyUpdate\": '{'\n  \"request_update\": {0}\n'}'", Locale.ENGLISH).format(new Object[] { this.status.name });
        }
    }
    
    enum KeyUpdateRequest
    {
        NOTREQUESTED((byte)0, "update_not_requested"), 
        REQUESTED((byte)1, "update_requested");
        
        final byte id;
        final String name;
        
        private KeyUpdateRequest(final byte id, final String name) {
            this.id = id;
            this.name = name;
        }
        
        static KeyUpdateRequest valueOf(final byte b) {
            for (final KeyUpdateRequest keyUpdateRequest : values()) {
                if (keyUpdateRequest.id == b) {
                    return keyUpdateRequest;
                }
            }
            return null;
        }
        
        static String nameOf(final byte b) {
            for (final KeyUpdateRequest keyUpdateRequest : values()) {
                if (keyUpdateRequest.id == b) {
                    return keyUpdateRequest.name;
                }
            }
            return "<UNKNOWN KeyUpdateRequest TYPE: " + (b & 0xFF) + ">";
        }
    }
    
    private static final class KeyUpdateKickstartProducer implements SSLProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext) throws IOException {
            return KeyUpdate.handshakeProducer.produce(connectionContext, new KeyUpdateMessage((PostHandshakeContext)connectionContext, KeyUpdateRequest.REQUESTED));
        }
    }
    
    private static final class KeyUpdateConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final PostHandshakeContext postHandshakeContext = (PostHandshakeContext)connectionContext;
            final KeyUpdateMessage keyUpdateMessage = new KeyUpdateMessage(postHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming KeyUpdate post-handshake message", keyUpdateMessage);
            }
            final SSLTrafficKeyDerivation value = SSLTrafficKeyDerivation.valueOf(postHandshakeContext.conContext.protocolVersion);
            if (value == null) {
                throw postHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + postHandshakeContext.conContext.protocolVersion);
            }
            final SSLKeyDerivation keyDerivation = value.createKeyDerivation(postHandshakeContext, postHandshakeContext.conContext.inputRecord.readCipher.baseSecret);
            if (keyDerivation == null) {
                throw postHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SecretKey deriveKey = keyDerivation.deriveKey("TlsUpdateNplus1", null);
            final SSLKeyDerivation keyDerivation2 = value.createKeyDerivation(postHandshakeContext, deriveKey);
            final SecretKey deriveKey2 = keyDerivation2.deriveKey("TlsKey", null);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(keyDerivation2.deriveKey("TlsIv", null).getEncoded());
            try {
                final SSLCipher.SSLReadCipher readCipher = postHandshakeContext.negotiatedCipherSuite.bulkCipher.createReadCipher(Authenticator.valueOf(postHandshakeContext.conContext.protocolVersion), postHandshakeContext.conContext.protocolVersion, deriveKey2, ivParameterSpec, postHandshakeContext.sslContext.getSecureRandom());
                if (readCipher == null) {
                    throw postHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + postHandshakeContext.negotiatedCipherSuite + ") and protocol version (" + postHandshakeContext.negotiatedProtocol + ")");
                }
                readCipher.baseSecret = deriveKey;
                postHandshakeContext.conContext.inputRecord.changeReadCiphers(readCipher);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("KeyUpdate: read key updated", new Object[0]);
                }
            }
            catch (final GeneralSecurityException ex) {
                throw postHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive read secrets", ex);
            }
            if (keyUpdateMessage.status == KeyUpdateRequest.REQUESTED) {
                KeyUpdate.handshakeProducer.produce(postHandshakeContext, new KeyUpdateMessage(postHandshakeContext, KeyUpdateRequest.NOTREQUESTED));
                return;
            }
            postHandshakeContext.conContext.finishPostHandshake();
        }
    }
    
    private static final class KeyUpdateProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final PostHandshakeContext postHandshakeContext = (PostHandshakeContext)connectionContext;
            final KeyUpdateMessage keyUpdateMessage = (KeyUpdateMessage)handshakeMessage;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced KeyUpdate post-handshake message", keyUpdateMessage);
            }
            final SSLTrafficKeyDerivation value = SSLTrafficKeyDerivation.valueOf(postHandshakeContext.conContext.protocolVersion);
            if (value == null) {
                throw postHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + postHandshakeContext.conContext.protocolVersion);
            }
            final SSLKeyDerivation keyDerivation = value.createKeyDerivation(postHandshakeContext, postHandshakeContext.conContext.outputRecord.writeCipher.baseSecret);
            if (keyDerivation == null) {
                throw postHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SecretKey deriveKey = keyDerivation.deriveKey("TlsUpdateNplus1", null);
            final SSLKeyDerivation keyDerivation2 = value.createKeyDerivation(postHandshakeContext, deriveKey);
            final SecretKey deriveKey2 = keyDerivation2.deriveKey("TlsKey", null);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(keyDerivation2.deriveKey("TlsIv", null).getEncoded());
            SSLCipher.SSLWriteCipher writeCipher;
            try {
                writeCipher = postHandshakeContext.negotiatedCipherSuite.bulkCipher.createWriteCipher(Authenticator.valueOf(postHandshakeContext.conContext.protocolVersion), postHandshakeContext.conContext.protocolVersion, deriveKey2, ivParameterSpec, postHandshakeContext.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException ex) {
                throw postHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive write secrets", ex);
            }
            if (writeCipher == null) {
                throw postHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + postHandshakeContext.negotiatedCipherSuite + ") and protocol version (" + postHandshakeContext.negotiatedProtocol + ")");
            }
            writeCipher.baseSecret = deriveKey;
            postHandshakeContext.conContext.outputRecord.changeWriteCiphers(writeCipher, keyUpdateMessage.status.id);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("KeyUpdate: write key updated", new Object[0]);
            }
            postHandshakeContext.conContext.finishPostHandshake();
            return null;
        }
    }
}
