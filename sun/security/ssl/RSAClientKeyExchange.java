package sun.security.ssl;

import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.util.Iterator;
import java.security.spec.AlgorithmParameterSpec;
import sun.misc.HexDumpEncoder;
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
        
        RSAClientKeyExchangeMessage(final HandshakeContext handshakeContext, final RSAKeyExchange.RSAPremasterSecret rsaPremasterSecret, final PublicKey publicKey) throws GeneralSecurityException {
            super(handshakeContext);
            this.protocolVersion = handshakeContext.clientHelloVersion;
            this.encrypted = rsaPremasterSecret.getEncoded(publicKey, handshakeContext.sslContext.getSecureRandom());
            this.useTLS10PlusSpec = ProtocolVersion.useTLS10PlusSpec(this.protocolVersion);
        }
        
        RSAClientKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            if (byteBuffer.remaining() < 2) {
                throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid RSA ClientKeyExchange message: insufficient data");
            }
            this.protocolVersion = handshakeContext.clientHelloVersion;
            this.useTLS10PlusSpec = ProtocolVersion.useTLS10PlusSpec(this.protocolVersion);
            if (this.useTLS10PlusSpec) {
                this.encrypted = Record.getBytes16(byteBuffer);
            }
            else {
                byteBuffer.get(this.encrypted = new byte[byteBuffer.remaining()]);
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
        
        public void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            if (this.useTLS10PlusSpec) {
                handshakeOutStream.putBytes16(this.encrypted);
            }
            else {
                handshakeOutStream.write(this.encrypted);
            }
        }
        
        @Override
        public String toString() {
            return new MessageFormat("\"RSA ClientKeyExchange\": '{'\n  \"client_version\":  {0}\n  \"encncrypted\": '{'\n{1}\n  '}'\n'}'", Locale.ENGLISH).format(new Object[] { ProtocolVersion.nameOf(this.protocolVersion), Utilities.indent(new HexDumpEncoder().encodeBuffer(this.encrypted), "    ") });
        }
    }
    
    private static final class RSAClientKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            RSAKeyExchange.EphemeralRSACredentials ephemeralRSACredentials = null;
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials sslCredentials : clientHandshakeContext.handshakeCredentials) {
                if (sslCredentials instanceof RSAKeyExchange.EphemeralRSACredentials) {
                    ephemeralRSACredentials = (RSAKeyExchange.EphemeralRSACredentials)sslCredentials;
                    if (x509Credentials != null) {
                        break;
                    }
                    continue;
                }
                else {
                    if (!(sslCredentials instanceof X509Authentication.X509Credentials)) {
                        continue;
                    }
                    x509Credentials = (X509Authentication.X509Credentials)sslCredentials;
                    if (ephemeralRSACredentials != null) {
                        break;
                    }
                    continue;
                }
            }
            if (ephemeralRSACredentials == null && x509Credentials == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No RSA credentials negotiated for client key exchange");
            }
            final PublicKey publicKey = (ephemeralRSACredentials != null) ? ephemeralRSACredentials.popPublicKey : x509Credentials.popPublicKey;
            if (!publicKey.getAlgorithm().equals("RSA")) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Not RSA public key for client key exchange");
            }
            RSAClientKeyExchangeMessage rsaClientKeyExchangeMessage;
            try {
                final RSAKeyExchange.RSAPremasterSecret premasterSecret = RSAKeyExchange.RSAPremasterSecret.createPremasterSecret(clientHandshakeContext);
                clientHandshakeContext.handshakePossessions.add(premasterSecret);
                rsaClientKeyExchangeMessage = new RSAClientKeyExchangeMessage(clientHandshakeContext, premasterSecret, publicKey);
            }
            catch (final GeneralSecurityException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Cannot generate RSA premaster secret", ex);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced RSA ClientKeyExchange handshake message", rsaClientKeyExchangeMessage);
            }
            rsaClientKeyExchangeMessage.write(clientHandshakeContext.handshakeOutput);
            clientHandshakeContext.handshakeOutput.flush();
            final SSLKeyExchange value = SSLKeyExchange.valueOf(clientHandshakeContext.negotiatedCipherSuite.keyExchange, clientHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final SecretKey deriveKey = value.createKeyDerivation(clientHandshakeContext).deriveKey("MasterSecret", null);
            clientHandshakeContext.handshakeSession.setMasterSecret(deriveKey);
            final SSLTrafficKeyDerivation value2 = SSLTrafficKeyDerivation.valueOf(clientHandshakeContext.negotiatedProtocol);
            if (value2 == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + clientHandshakeContext.negotiatedProtocol);
            }
            clientHandshakeContext.handshakeKeyDerivation = value2.createKeyDerivation(clientHandshakeContext, deriveKey);
            return null;
        }
    }
    
    private static final class RSAClientKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            RSAKeyExchange.EphemeralRSAPossession ephemeralRSAPossession = null;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession sslPossession : serverHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof RSAKeyExchange.EphemeralRSAPossession) {
                    ephemeralRSAPossession = (RSAKeyExchange.EphemeralRSAPossession)sslPossession;
                    break;
                }
                if (!(sslPossession instanceof X509Authentication.X509Possession)) {
                    continue;
                }
                x509Possession = (X509Authentication.X509Possession)sslPossession;
                if (ephemeralRSAPossession != null) {
                    break;
                }
            }
            if (ephemeralRSAPossession == null && x509Possession == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No RSA possessions negotiated for client key exchange");
            }
            final PrivateKey privateKey = (ephemeralRSAPossession != null) ? ephemeralRSAPossession.popPrivateKey : x509Possession.popPrivateKey;
            if (!privateKey.getAlgorithm().equals("RSA")) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Not RSA private key for client key exchange");
            }
            final RSAClientKeyExchangeMessage rsaClientKeyExchangeMessage = new RSAClientKeyExchangeMessage(serverHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming RSA ClientKeyExchange handshake message", rsaClientKeyExchangeMessage);
            }
            try {
                serverHandshakeContext.handshakeCredentials.add(RSAKeyExchange.RSAPremasterSecret.decode(serverHandshakeContext, privateKey, rsaClientKeyExchangeMessage.encrypted));
            }
            catch (final GeneralSecurityException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Cannot decode RSA premaster secret", ex);
            }
            final SSLKeyExchange value = SSLKeyExchange.valueOf(serverHandshakeContext.negotiatedCipherSuite.keyExchange, serverHandshakeContext.negotiatedProtocol);
            if (value == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key exchange type");
            }
            final SecretKey deriveKey = value.createKeyDerivation(serverHandshakeContext).deriveKey("MasterSecret", null);
            serverHandshakeContext.handshakeSession.setMasterSecret(deriveKey);
            final SSLTrafficKeyDerivation value2 = SSLTrafficKeyDerivation.valueOf(serverHandshakeContext.negotiatedProtocol);
            if (value2 == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Not supported key derivation: " + serverHandshakeContext.negotiatedProtocol);
            }
            serverHandshakeContext.handshakeKeyDerivation = value2.createKeyDerivation(serverHandshakeContext, deriveKey);
        }
    }
}
