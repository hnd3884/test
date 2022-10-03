package org.openjsse.sun.security.ssl;

import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.util.Iterator;
import java.security.spec.AlgorithmParameterSpec;
import org.openjsse.sun.security.util.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

final class RSAClientKeyExchange
{
    static final SSLConsumer rsaHandshakeConsumer;
    static final HandshakeProducer rsaHandshakeProducer;
    
    static {
        rsaHandshakeConsumer = new RSAClientKeyExchangeConsumer();
        rsaHandshakeProducer = new RSAClientKeyExchangeProducer();
    }
    
    private static final class RSAClientKeyExchangeMessage extends SSLHandshake.HandshakeMessage
    {
        final int protocolVersion;
        final boolean useTLS10PlusSpec;
        final byte[] encrypted;
        
        RSAClientKeyExchangeMessage(final HandshakeContext context, final RSAKeyExchange.RSAPremasterSecret premaster, final PublicKey publicKey) throws GeneralSecurityException {
            super(context);
            this.protocolVersion = context.clientHelloVersion;
            this.encrypted = premaster.getEncoded(publicKey, context.sslContext.getSecureRandom());
            this.useTLS10PlusSpec = ProtocolVersion.useTLS10PlusSpec(this.protocolVersion, context.sslContext.isDTLS());
        }
        
        RSAClientKeyExchangeMessage(final HandshakeContext context, final ByteBuffer m) throws IOException {
            super(context);
            if (m.remaining() < 2) {
                throw context.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid RSA ClientKeyExchange message: insufficient data");
            }
            this.protocolVersion = context.clientHelloVersion;
            this.useTLS10PlusSpec = ProtocolVersion.useTLS10PlusSpec(this.protocolVersion, context.sslContext.isDTLS());
            if (this.useTLS10PlusSpec) {
                this.encrypted = Record.getBytes16(m);
            }
            else {
                m.get(this.encrypted = new byte[m.remaining()]);
            }
        }
        
        public SSLHandshake handshakeType() {
            return SSLHandshake.CLIENT_KEY_EXCHANGE;
        }
        
        public int messageLength() {
            if (this.useTLS10PlusSpec) {
                return this.encrypted.length + 2;
            }
            return this.encrypted.length;
        }
        
        public void send(final HandshakeOutStream hos) throws IOException {
            if (this.useTLS10PlusSpec) {
                hos.putBytes16(this.encrypted);
            }
            else {
                hos.write(this.encrypted);
            }
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"RSA ClientKeyExchange\": '{'\n  \"client_version\":  {0}\n  \"encncrypted\": '{'\n{1}\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { ProtocolVersion.nameOf(this.protocolVersion), Utilities.indent(hexEncoder.encodeBuffer(this.encrypted), "    ") };
            return messageFormat.format(messageFields);
        }
    }
    
    private static final class RSAClientKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            RSAKeyExchange.EphemeralRSACredentials rsaCredentials = null;
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials credential : chc.handshakeCredentials) {
                if (credential instanceof RSAKeyExchange.EphemeralRSACredentials) {
                    rsaCredentials = (RSAKeyExchange.EphemeralRSACredentials)credential;
                    if (x509Credentials != null) {
                        break;
                    }
                    continue;
                }
                else {
                    if (!(credential instanceof X509Authentication.X509Credentials)) {
                        continue;
                    }
                    x509Credentials = (X509Authentication.X509Credentials)credential;
                    if (rsaCredentials != null) {
                        break;
                    }
                    continue;
                }
            }
            if (rsaCredentials == null && x509Credentials == null) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No RSA credentials negotiated for client key exchange");
            }
            final PublicKey publicKey = (rsaCredentials != null) ? rsaCredentials.popPublicKey : x509Credentials.popPublicKey;
            if (!publicKey.getAlgorithm().equals("RSA")) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Not RSA public key for client key exchange");
            }
            RSAClientKeyExchangeMessage ckem;
            try {
                final RSAKeyExchange.RSAPremasterSecret premaster = RSAKeyExchange.RSAPremasterSecret.createPremasterSecret(chc);
                chc.handshakePossessions.add(premaster);
                ckem = new RSAClientKeyExchangeMessage(chc, premaster, publicKey);
            }
            catch (final GeneralSecurityException gse) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Cannot generate RSA premaster secret", gse);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced RSA ClientKeyExchange handshake message", ckem);
            }
            ckem.write(chc.handshakeOutput);
            chc.handshakeOutput.flush();
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(chc.negotiatedCipherSuite.keyExchange, chc.negotiatedProtocol);
            if (ke == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final SSLKeyDerivation masterKD = ke.createKeyDerivation(chc);
            final SecretKey masterSecret = masterKD.deriveKey("MasterSecret", null);
            chc.handshakeSession.setMasterSecret(masterSecret);
            final SSLTrafficKeyDerivation kd = SSLTrafficKeyDerivation.valueOf(chc.negotiatedProtocol);
            if (kd == null) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + chc.negotiatedProtocol);
            }
            chc.handshakeKeyDerivation = kd.createKeyDerivation(chc, masterSecret);
            return null;
        }
    }
    
    private static final class RSAClientKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            RSAKeyExchange.EphemeralRSAPossession rsaPossession = null;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession possession : shc.handshakePossessions) {
                if (possession instanceof RSAKeyExchange.EphemeralRSAPossession) {
                    rsaPossession = (RSAKeyExchange.EphemeralRSAPossession)possession;
                    break;
                }
                if (!(possession instanceof X509Authentication.X509Possession)) {
                    continue;
                }
                x509Possession = (X509Authentication.X509Possession)possession;
                if (rsaPossession != null) {
                    break;
                }
            }
            if (rsaPossession == null && x509Possession == null) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No RSA possessions negotiated for client key exchange");
            }
            final PrivateKey privateKey = (rsaPossession != null) ? rsaPossession.popPrivateKey : x509Possession.popPrivateKey;
            if (!privateKey.getAlgorithm().equals("RSA")) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Not RSA private key for client key exchange");
            }
            final RSAClientKeyExchangeMessage ckem = new RSAClientKeyExchangeMessage(shc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming RSA ClientKeyExchange handshake message", ckem);
            }
            try {
                final RSAKeyExchange.RSAPremasterSecret premaster = RSAKeyExchange.RSAPremasterSecret.decode(shc, privateKey, ckem.encrypted);
                shc.handshakeCredentials.add(premaster);
            }
            catch (final GeneralSecurityException gse) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Cannot decode RSA premaster secret", gse);
            }
            final SSLKeyExchange ke = SSLKeyExchange.valueOf(shc.negotiatedCipherSuite.keyExchange, shc.negotiatedProtocol);
            if (ke == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final SSLKeyDerivation masterKD = ke.createKeyDerivation(shc);
            final SecretKey masterSecret = masterKD.deriveKey("MasterSecret", null);
            shc.handshakeSession.setMasterSecret(masterSecret);
            final SSLTrafficKeyDerivation kd = SSLTrafficKeyDerivation.valueOf(shc.negotiatedProtocol);
            if (kd == null) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + shc.negotiatedProtocol);
            }
            shc.handshakeKeyDerivation = kd.createKeyDerivation(shc, masterSecret);
        }
    }
}
