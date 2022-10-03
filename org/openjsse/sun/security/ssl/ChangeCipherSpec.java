package org.openjsse.sun.security.ssl;

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
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final HandshakeContext hc = (HandshakeContext)context;
            final SSLKeyDerivation kd = hc.handshakeKeyDerivation;
            if (!(kd instanceof SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation)) {
                throw new UnsupportedOperationException("Not supported.");
            }
            final SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation tkd = (SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation)kd;
            final CipherSuite ncs = hc.negotiatedCipherSuite;
            Authenticator writeAuthenticator;
            if (ncs.bulkCipher.cipherType == CipherType.AEAD_CIPHER) {
                writeAuthenticator = Authenticator.valueOf(hc.negotiatedProtocol);
            }
            else {
                try {
                    writeAuthenticator = Authenticator.valueOf(hc.negotiatedProtocol, ncs.macAlg, tkd.getTrafficKey(hc.sslConfig.isClientMode ? "clientMacKey" : "serverMacKey"));
                }
                catch (final NoSuchAlgorithmException | InvalidKeyException e) {
                    throw new SSLException("Algorithm missing:  ", e);
                }
            }
            final SecretKey writeKey = tkd.getTrafficKey(hc.sslConfig.isClientMode ? "clientWriteKey" : "serverWriteKey");
            final SecretKey writeIv = tkd.getTrafficKey(hc.sslConfig.isClientMode ? "clientWriteIv" : "serverWriteIv");
            final IvParameterSpec iv = (writeIv == null) ? null : new IvParameterSpec(writeIv.getEncoded());
            SSLCipher.SSLWriteCipher writeCipher;
            try {
                writeCipher = ncs.bulkCipher.createWriteCipher(writeAuthenticator, hc.negotiatedProtocol, writeKey, iv, hc.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException gse) {
                throw new SSLException("Algorithm missing:  ", gse);
            }
            if (writeCipher == null) {
                throw hc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + ncs + ") and protocol version (" + hc.negotiatedProtocol + ")");
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced ChangeCipherSpec message", new Object[0]);
            }
            hc.conContext.outputRecord.changeWriteCiphers(writeCipher, true);
            return null;
        }
    }
    
    private static final class T10ChangeCipherSpecConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final TransportContext tc = (TransportContext)context;
            tc.consumers.remove(ContentType.CHANGE_CIPHER_SPEC.id);
            if (message.remaining() != 1 || message.get() != 1) {
                throw tc.fatal(Alert.UNEXPECTED_MESSAGE, "Malformed or unexpected ChangeCipherSpec message");
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ChangeCipherSpec message", new Object[0]);
            }
            if (tc.handshakeContext == null) {
                throw tc.fatal(Alert.HANDSHAKE_FAILURE, "Unexpected ChangeCipherSpec message");
            }
            final HandshakeContext hc = tc.handshakeContext;
            if (hc.handshakeKeyDerivation == null) {
                throw tc.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected ChangeCipherSpec message");
            }
            final SSLKeyDerivation kd = hc.handshakeKeyDerivation;
            if (!(kd instanceof SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation)) {
                throw new UnsupportedOperationException("Not supported.");
            }
            final SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation tkd = (SSLTrafficKeyDerivation.LegacyTrafficKeyDerivation)kd;
            final CipherSuite ncs = hc.negotiatedCipherSuite;
            Authenticator readAuthenticator;
            if (ncs.bulkCipher.cipherType == CipherType.AEAD_CIPHER) {
                readAuthenticator = Authenticator.valueOf(hc.negotiatedProtocol);
            }
            else {
                try {
                    readAuthenticator = Authenticator.valueOf(hc.negotiatedProtocol, ncs.macAlg, tkd.getTrafficKey(hc.sslConfig.isClientMode ? "serverMacKey" : "clientMacKey"));
                }
                catch (final NoSuchAlgorithmException | InvalidKeyException e) {
                    throw new SSLException("Algorithm missing:  ", e);
                }
            }
            final SecretKey readKey = tkd.getTrafficKey(hc.sslConfig.isClientMode ? "serverWriteKey" : "clientWriteKey");
            final SecretKey readIv = tkd.getTrafficKey(hc.sslConfig.isClientMode ? "serverWriteIv" : "clientWriteIv");
            final IvParameterSpec iv = (readIv == null) ? null : new IvParameterSpec(readIv.getEncoded());
            SSLCipher.SSLReadCipher readCipher;
            try {
                readCipher = ncs.bulkCipher.createReadCipher(readAuthenticator, hc.negotiatedProtocol, readKey, iv, hc.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException gse) {
                throw new SSLException("Algorithm missing:  ", gse);
            }
            if (readCipher == null) {
                throw hc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + hc.negotiatedCipherSuite + ") and protocol version (" + hc.negotiatedProtocol + ")");
            }
            tc.inputRecord.changeReadCiphers(readCipher);
        }
    }
    
    private static final class T13ChangeCipherSpecConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final TransportContext tc = (TransportContext)context;
            tc.consumers.remove(ContentType.CHANGE_CIPHER_SPEC.id);
            if (message.remaining() != 1 || message.get() != 1) {
                throw tc.fatal(Alert.UNEXPECTED_MESSAGE, "Malformed or unexpected ChangeCipherSpec message");
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming ChangeCipherSpec message", new Object[0]);
            }
        }
    }
}
