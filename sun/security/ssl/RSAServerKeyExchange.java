package sun.security.ssl;

import java.security.Key;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import sun.misc.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

final class RSAServerKeyExchange
{
    static final SSLConsumer rsaHandshakeConsumer;
    static final HandshakeProducer rsaHandshakeProducer;
    
    static {
        rsaHandshakeConsumer = new RSAServerKeyExchangeConsumer();
        rsaHandshakeProducer = new RSAServerKeyExchangeProducer();
    }
    
    private static final class RSAServerKeyExchangeMessage extends SSLHandshake.HandshakeMessage
    {
        private final byte[] modulus;
        private final byte[] exponent;
        private final byte[] paramsSignature;
        
        private RSAServerKeyExchangeMessage(final HandshakeContext handshakeContext, final X509Authentication.X509Possession x509Possession, final RSAKeyExchange.EphemeralRSAPossession ephemeralRSAPossession) throws IOException {
            super(handshakeContext);
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)handshakeContext;
            final RSAPublicKeySpec rsaPublicKeySpec = JsseJce.getRSAPublicKeySpec(ephemeralRSAPossession.popPublicKey);
            this.modulus = Utilities.toByteArray(rsaPublicKeySpec.getModulus());
            this.exponent = Utilities.toByteArray(rsaPublicKeySpec.getPublicExponent());
            byte[] sign;
            try {
                final Signature instance = RSASignature.getInstance();
                instance.initSign(x509Possession.popPrivateKey, serverHandshakeContext.sslContext.getSecureRandom());
                this.updateSignature(instance, serverHandshakeContext.clientHelloRandom.randomBytes, serverHandshakeContext.serverHelloRandom.randomBytes);
                sign = instance.sign();
            }
            catch (final NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
                throw serverHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Failed to sign ephemeral RSA parameters", (Throwable)ex);
            }
            this.paramsSignature = sign;
        }
        
        RSAServerKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer byteBuffer) throws IOException {
            super(handshakeContext);
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)handshakeContext;
            this.modulus = Record.getBytes16(byteBuffer);
            this.exponent = Record.getBytes16(byteBuffer);
            this.paramsSignature = Record.getBytes16(byteBuffer);
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials sslCredentials : clientHandshakeContext.handshakeCredentials) {
                if (sslCredentials instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)sslCredentials;
                    break;
                }
            }
            if (x509Credentials == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No RSA credentials negotiated for server key exchange");
            }
            try {
                final Signature instance = RSASignature.getInstance();
                instance.initVerify(x509Credentials.popPublicKey);
                this.updateSignature(instance, clientHandshakeContext.clientHelloRandom.randomBytes, clientHandshakeContext.serverHelloRandom.randomBytes);
                if (!instance.verify(this.paramsSignature)) {
                    throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature of RSA ServerKeyExchange message");
                }
            }
            catch (final NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.INTERNAL_ERROR, "Failed to sign ephemeral RSA parameters", (Throwable)ex);
            }
        }
        
        @Override
        SSLHandshake handshakeType() {
            return SSLHandshake.SERVER_KEY_EXCHANGE;
        }
        
        @Override
        int messageLength() {
            return 6 + this.modulus.length + this.exponent.length + this.paramsSignature.length;
        }
        
        @Override
        void send(final HandshakeOutStream handshakeOutStream) throws IOException {
            handshakeOutStream.putBytes16(this.modulus);
            handshakeOutStream.putBytes16(this.exponent);
            handshakeOutStream.putBytes16(this.paramsSignature);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"RSA ServerKeyExchange\": '{'\n  \"parameters\": '{'\n    \"rsa_modulus\": '{'\n{0}\n    '}',\n    \"rsa_exponent\": '{'\n{1}\n    '}'\n  '}',\n  \"digital signature\":  '{'\n    \"signature\": '{'\n{2}\n    '}',\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
            return messageFormat.format(new Object[] { Utilities.indent(hexDumpEncoder.encodeBuffer(this.modulus), "      "), Utilities.indent(hexDumpEncoder.encodeBuffer(this.exponent), "      "), Utilities.indent(hexDumpEncoder.encodeBuffer(this.paramsSignature), "      ") });
        }
        
        private void updateSignature(final Signature signature, final byte[] array, final byte[] array2) throws SignatureException {
            signature.update(array);
            signature.update(array2);
            signature.update((byte)(this.modulus.length >> 8));
            signature.update((byte)(this.modulus.length & 0xFF));
            signature.update(this.modulus);
            signature.update((byte)(this.exponent.length >> 8));
            signature.update((byte)(this.exponent.length & 0xFF));
            signature.update(this.exponent);
        }
    }
    
    private static final class RSAServerKeyExchangeProducer implements HandshakeProducer
    {
        @Override
        public byte[] produce(final ConnectionContext connectionContext, final SSLHandshake.HandshakeMessage handshakeMessage) throws IOException {
            final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)connectionContext;
            RSAKeyExchange.EphemeralRSAPossession ephemeralRSAPossession = null;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession sslPossession : serverHandshakeContext.handshakePossessions) {
                if (sslPossession instanceof RSAKeyExchange.EphemeralRSAPossession) {
                    ephemeralRSAPossession = (RSAKeyExchange.EphemeralRSAPossession)sslPossession;
                    if (x509Possession != null) {
                        break;
                    }
                    continue;
                }
                else {
                    if (!(sslPossession instanceof X509Authentication.X509Possession)) {
                        continue;
                    }
                    x509Possession = (X509Authentication.X509Possession)sslPossession;
                    if (ephemeralRSAPossession != null) {
                        break;
                    }
                    continue;
                }
            }
            if (ephemeralRSAPossession == null) {
                return null;
            }
            if (x509Possession == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No RSA certificate negotiated for server key exchange");
            }
            if (!"RSA".equals(x509Possession.popPrivateKey.getAlgorithm())) {
                throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No X.509 possession can be used for ephemeral RSA ServerKeyExchange");
            }
            final RSAServerKeyExchangeMessage rsaServerKeyExchangeMessage = new RSAServerKeyExchangeMessage((HandshakeContext)serverHandshakeContext, x509Possession, ephemeralRSAPossession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced RSA ServerKeyExchange handshake message", rsaServerKeyExchangeMessage);
            }
            rsaServerKeyExchangeMessage.write(serverHandshakeContext.handshakeOutput);
            serverHandshakeContext.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class RSAServerKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext connectionContext, final ByteBuffer byteBuffer) throws IOException {
            final ClientHandshakeContext clientHandshakeContext = (ClientHandshakeContext)connectionContext;
            final RSAServerKeyExchangeMessage rsaServerKeyExchangeMessage = new RSAServerKeyExchangeMessage(clientHandshakeContext, byteBuffer);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming RSA ServerKeyExchange handshake message", rsaServerKeyExchangeMessage);
            }
            RSAPublicKey rsaPublicKey;
            try {
                rsaPublicKey = (RSAPublicKey)JsseJce.getKeyFactory("RSA").generatePublic(new RSAPublicKeySpec(new BigInteger(1, rsaServerKeyExchangeMessage.modulus), new BigInteger(1, rsaServerKeyExchangeMessage.exponent)));
            }
            catch (final GeneralSecurityException ex) {
                throw clientHandshakeContext.conContext.fatal(Alert.INSUFFICIENT_SECURITY, "Could not generate RSAPublicKey", ex);
            }
            if (!clientHandshakeContext.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), rsaPublicKey)) {
                throw clientHandshakeContext.conContext.fatal(Alert.INSUFFICIENT_SECURITY, "RSA ServerKeyExchange does not comply to algorithm constraints");
            }
            clientHandshakeContext.handshakeCredentials.add(new RSAKeyExchange.EphemeralRSACredentials(rsaPublicKey));
        }
    }
}
