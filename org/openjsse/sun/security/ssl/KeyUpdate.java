package org.openjsse.sun.security.ssl;

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
        
        KeyUpdateMessage(final PostHandshakeContext context, final KeyUpdateRequest status) {
            super(context);
            this.status = status;
        }
        
        KeyUpdateMessage(final PostHandshakeContext context, final ByteBuffer m) throws IOException {
            super(context);
            if (m.remaining() != 1) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "KeyUpdate has an unexpected length of " + m.remaining());
            }
            final byte request = m.get();
            this.status = KeyUpdateRequest.valueOf(request);
            if (this.status == null) {
                throw context.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Invalid KeyUpdate message value: " + KeyUpdateRequest.nameOf(request));
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.KEY_UPDATE;
        }
        
        public int messageLength() {
            return 1;
        }
        
        public void send(final HandshakeOutStream s) throws IOException {
            s.putInt8(this.status.id);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"KeyUpdate\": '{'\n  \"request_update\": {0}\n'}'", Locale.ENGLISH);
            final Object[] messageFields = { this.status.name };
            return messageFormat.format(messageFields);
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
        
        static KeyUpdateRequest valueOf(final byte id) {
            for (final KeyUpdateRequest kur : values()) {
                if (kur.id == id) {
                    return kur;
                }
            }
            return null;
        }
        
        static String nameOf(final byte id) {
            for (final KeyUpdateRequest kur : values()) {
                if (kur.id == id) {
                    return kur.name;
                }
            }
            return "<UNKNOWN KeyUpdateRequest TYPE: " + (id & 0xFF) + ">";
        }
    }
    
    private static final class KeyUpdateKickstartProducer implements SSLProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context) throws IOException {
            final PostHandshakeContext hc = (PostHandshakeContext)context;
            return KeyUpdate.handshakeProducer.produce(context, new KeyUpdateMessage(hc, KeyUpdateRequest.REQUESTED));
        }
    }
    
    private static final class KeyUpdateConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final PostHandshakeContext hc = (PostHandshakeContext)context;
            final KeyUpdateMessage km = new KeyUpdateMessage(hc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming KeyUpdate post-handshake message", km);
            }
            final SSLTrafficKeyDerivation kdg = SSLTrafficKeyDerivation.valueOf(hc.conContext.protocolVersion);
            if (kdg == null) {
                throw hc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + hc.conContext.protocolVersion);
            }
            final SSLKeyDerivation skd = kdg.createKeyDerivation(hc, hc.conContext.inputRecord.readCipher.baseSecret);
            if (skd == null) {
                throw hc.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SecretKey nplus1 = skd.deriveKey("TlsUpdateNplus1", null);
            final SSLKeyDerivation kd = kdg.createKeyDerivation(hc, nplus1);
            final SecretKey key = kd.deriveKey("TlsKey", null);
            final IvParameterSpec ivSpec = new IvParameterSpec(kd.deriveKey("TlsIv", null).getEncoded());
            try {
                final SSLCipher.SSLReadCipher rc = hc.negotiatedCipherSuite.bulkCipher.createReadCipher(Authenticator.valueOf(hc.conContext.protocolVersion), hc.conContext.protocolVersion, key, ivSpec, hc.sslContext.getSecureRandom());
                if (rc == null) {
                    throw hc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + hc.negotiatedCipherSuite + ") and protocol version (" + hc.negotiatedProtocol + ")");
                }
                rc.baseSecret = nplus1;
                hc.conContext.inputRecord.changeReadCiphers(rc);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("KeyUpdate: read key updated", new Object[0]);
                }
            }
            catch (final GeneralSecurityException gse) {
                throw hc.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive read secrets", gse);
            }
            if (km.status == KeyUpdateRequest.REQUESTED) {
                KeyUpdate.handshakeProducer.produce(hc, new KeyUpdateMessage(hc, KeyUpdateRequest.NOTREQUESTED));
                return;
            }
            hc.conContext.finishPostHandshake();
        }
    }
    
    private static final class KeyUpdateProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final PostHandshakeContext hc = (PostHandshakeContext)context;
            final KeyUpdateMessage km = (KeyUpdateMessage)message;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced KeyUpdate post-handshake message", km);
            }
            final SSLTrafficKeyDerivation kdg = SSLTrafficKeyDerivation.valueOf(hc.conContext.protocolVersion);
            if (kdg == null) {
                throw hc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + hc.conContext.protocolVersion);
            }
            final SSLKeyDerivation skd = kdg.createKeyDerivation(hc, hc.conContext.outputRecord.writeCipher.baseSecret);
            if (skd == null) {
                throw hc.conContext.fatal(Alert.INTERNAL_ERROR, "no key derivation");
            }
            final SecretKey nplus1 = skd.deriveKey("TlsUpdateNplus1", null);
            final SSLKeyDerivation kd = kdg.createKeyDerivation(hc, nplus1);
            final SecretKey key = kd.deriveKey("TlsKey", null);
            final IvParameterSpec ivSpec = new IvParameterSpec(kd.deriveKey("TlsIv", null).getEncoded());
            SSLCipher.SSLWriteCipher wc;
            try {
                wc = hc.negotiatedCipherSuite.bulkCipher.createWriteCipher(Authenticator.valueOf(hc.conContext.protocolVersion), hc.conContext.protocolVersion, key, ivSpec, hc.sslContext.getSecureRandom());
            }
            catch (final GeneralSecurityException gse) {
                throw hc.conContext.fatal(Alert.INTERNAL_ERROR, "Failure to derive write secrets", gse);
            }
            if (wc == null) {
                throw hc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Illegal cipher suite (" + hc.negotiatedCipherSuite + ") and protocol version (" + hc.negotiatedProtocol + ")");
            }
            wc.baseSecret = nplus1;
            hc.conContext.outputRecord.changeWriteCiphers(wc, km.status.id);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("KeyUpdate: write key updated", new Object[0]);
            }
            hc.conContext.finishPostHandshake();
            return null;
        }
    }
}
