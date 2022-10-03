package org.openjsse.sun.security.ssl;

import java.security.KeyFactory;
import java.security.Key;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.spec.KeySpec;
import java.math.BigInteger;
import org.openjsse.sun.security.util.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.security.interfaces.RSAPublicKey;
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
        
        private RSAServerKeyExchangeMessage(final HandshakeContext handshakeContext, final X509Authentication.X509Possession x509Possession, final RSAKeyExchange.EphemeralRSAPossession rsaPossession) throws IOException {
            super(handshakeContext);
            final ServerHandshakeContext shc = (ServerHandshakeContext)handshakeContext;
            final RSAPublicKey publicKey = rsaPossession.popPublicKey;
            final RSAPublicKeySpec spec = JsseJce.getRSAPublicKeySpec(publicKey);
            this.modulus = Utilities.toByteArray(spec.getModulus());
            this.exponent = Utilities.toByteArray(spec.getPublicExponent());
            byte[] signature = null;
            try {
                final Signature signer = RSASignature.getInstance();
                signer.initSign(x509Possession.popPrivateKey, shc.sslContext.getSecureRandom());
                this.updateSignature(signer, shc.clientHelloRandom.randomBytes, shc.serverHelloRandom.randomBytes);
                signature = signer.sign();
            }
            catch (final NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
                throw shc.conContext.fatal(Alert.INTERNAL_ERROR, "Failed to sign ephemeral RSA parameters", ex);
            }
            this.paramsSignature = signature;
        }
        
        RSAServerKeyExchangeMessage(final HandshakeContext handshakeContext, final ByteBuffer m) throws IOException {
            super(handshakeContext);
            final ClientHandshakeContext chc = (ClientHandshakeContext)handshakeContext;
            this.modulus = Record.getBytes16(m);
            this.exponent = Record.getBytes16(m);
            this.paramsSignature = Record.getBytes16(m);
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLCredentials cd : chc.handshakeCredentials) {
                if (cd instanceof X509Authentication.X509Credentials) {
                    x509Credentials = (X509Authentication.X509Credentials)cd;
                    break;
                }
            }
            if (x509Credentials == null) {
                throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No RSA credentials negotiated for server key exchange");
            }
            try {
                final Signature signer = RSASignature.getInstance();
                signer.initVerify(x509Credentials.popPublicKey);
                this.updateSignature(signer, chc.clientHelloRandom.randomBytes, chc.serverHelloRandom.randomBytes);
                if (!signer.verify(this.paramsSignature)) {
                    throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Invalid signature of RSA ServerKeyExchange message");
                }
            }
            catch (final NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
                throw chc.conContext.fatal(Alert.INTERNAL_ERROR, "Failed to sign ephemeral RSA parameters", ex);
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
        void send(final HandshakeOutStream hos) throws IOException {
            hos.putBytes16(this.modulus);
            hos.putBytes16(this.exponent);
            hos.putBytes16(this.paramsSignature);
        }
        
        @Override
        public String toString() {
            final MessageFormat messageFormat = new MessageFormat("\"RSA ServerKeyExchange\": '{'\n  \"parameters\": '{'\n    \"rsa_modulus\": '{'\n{0}\n    '}',\n    \"rsa_exponent\": '{'\n{1}\n    '}'\n  '}',\n  \"digital signature\":  '{'\n    \"signature\": '{'\n{2}\n    '}',\n  '}'\n'}'", Locale.ENGLISH);
            final HexDumpEncoder hexEncoder = new HexDumpEncoder();
            final Object[] messageFields = { Utilities.indent(hexEncoder.encodeBuffer(this.modulus), "      "), Utilities.indent(hexEncoder.encodeBuffer(this.exponent), "      "), Utilities.indent(hexEncoder.encodeBuffer(this.paramsSignature), "      ") };
            return messageFormat.format(messageFields);
        }
        
        private void updateSignature(final Signature signature, final byte[] clntNonce, final byte[] svrNonce) throws SignatureException {
            signature.update(clntNonce);
            signature.update(svrNonce);
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
        public byte[] produce(final ConnectionContext context, final SSLHandshake.HandshakeMessage message) throws IOException {
            final ServerHandshakeContext shc = (ServerHandshakeContext)context;
            RSAKeyExchange.EphemeralRSAPossession rsaPossession = null;
            X509Authentication.X509Possession x509Possession = null;
            for (final SSLPossession possession : shc.handshakePossessions) {
                if (possession instanceof RSAKeyExchange.EphemeralRSAPossession) {
                    rsaPossession = (RSAKeyExchange.EphemeralRSAPossession)possession;
                    if (x509Possession != null) {
                        break;
                    }
                    continue;
                }
                else {
                    if (!(possession instanceof X509Authentication.X509Possession)) {
                        continue;
                    }
                    x509Possession = (X509Authentication.X509Possession)possession;
                    if (rsaPossession != null) {
                        break;
                    }
                    continue;
                }
            }
            if (rsaPossession == null) {
                return null;
            }
            if (x509Possession == null) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No RSA certificate negotiated for server key exchange");
            }
            if (!"RSA".equals(x509Possession.popPrivateKey.getAlgorithm())) {
                throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "No X.509 possession can be used for ephemeral RSA ServerKeyExchange");
            }
            final RSAServerKeyExchangeMessage skem = new RSAServerKeyExchangeMessage((HandshakeContext)shc, x509Possession, rsaPossession);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Produced RSA ServerKeyExchange handshake message", skem);
            }
            skem.write(shc.handshakeOutput);
            shc.handshakeOutput.flush();
            return null;
        }
    }
    
    private static final class RSAServerKeyExchangeConsumer implements SSLConsumer
    {
        @Override
        public void consume(final ConnectionContext context, final ByteBuffer message) throws IOException {
            final ClientHandshakeContext chc = (ClientHandshakeContext)context;
            final RSAServerKeyExchangeMessage skem = new RSAServerKeyExchangeMessage(chc, message);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Consuming RSA ServerKeyExchange handshake message", skem);
            }
            RSAPublicKey publicKey;
            try {
                final KeyFactory kf = JsseJce.getKeyFactory("RSA");
                final RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(1, skem.modulus), new BigInteger(1, skem.exponent));
                publicKey = (RSAPublicKey)kf.generatePublic(spec);
            }
            catch (final GeneralSecurityException gse) {
                throw chc.conContext.fatal(Alert.INSUFFICIENT_SECURITY, "Could not generate RSAPublicKey", gse);
            }
            if (!chc.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), publicKey)) {
                throw chc.conContext.fatal(Alert.INSUFFICIENT_SECURITY, "RSA ServerKeyExchange does not comply to algorithm constraints");
            }
            chc.handshakeCredentials.add(new RSAKeyExchange.EphemeralRSACredentials(publicKey));
        }
    }
}
