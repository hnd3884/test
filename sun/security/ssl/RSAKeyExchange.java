package sun.security.ssl;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.util.Iterator;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import sun.security.util.KeyUtil;
import javax.crypto.KeyGenerator;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import java.security.Key;
import java.security.SecureRandom;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

final class RSAKeyExchange
{
    static final SSLPossessionGenerator poGenerator;
    static final SSLKeyAgreementGenerator kaGenerator;
    
    static {
        poGenerator = new EphemeralRSAPossessionGenerator();
        kaGenerator = new RSAKAGenerator();
    }
    
    static final class EphemeralRSAPossession implements SSLPossession
    {
        final RSAPublicKey popPublicKey;
        final PrivateKey popPrivateKey;
        
        EphemeralRSAPossession(final PrivateKey popPrivateKey, final RSAPublicKey popPublicKey) {
            this.popPublicKey = popPublicKey;
            this.popPrivateKey = popPrivateKey;
        }
    }
    
    static final class EphemeralRSACredentials implements SSLCredentials
    {
        final RSAPublicKey popPublicKey;
        
        EphemeralRSACredentials(final RSAPublicKey popPublicKey) {
            this.popPublicKey = popPublicKey;
        }
    }
    
    private static final class EphemeralRSAPossessionGenerator implements SSLPossessionGenerator
    {
        @Override
        public SSLPossession createPossession(final HandshakeContext handshakeContext) {
            try {
                final KeyPair rsaKeyPair = handshakeContext.sslContext.getEphemeralKeyManager().getRSAKeyPair(true, handshakeContext.sslContext.getSecureRandom());
                if (rsaKeyPair != null) {
                    return new EphemeralRSAPossession(rsaKeyPair.getPrivate(), (RSAPublicKey)rsaKeyPair.getPublic());
                }
                return null;
            }
            catch (final RuntimeException ex) {
                return null;
            }
        }
    }
    
    static final class RSAPremasterSecret implements SSLPossession, SSLCredentials
    {
        final SecretKey premasterSecret;
        
        RSAPremasterSecret(final SecretKey premasterSecret) {
            this.premasterSecret = premasterSecret;
        }
        
        byte[] getEncoded(final PublicKey publicKey, final SecureRandom secureRandom) throws GeneralSecurityException {
            final Cipher cipher = JsseJce.getCipher("RSA/ECB/PKCS1Padding");
            cipher.init(3, publicKey, secureRandom);
            return cipher.wrap(this.premasterSecret);
        }
        
        static RSAPremasterSecret createPremasterSecret(final ClientHandshakeContext clientHandshakeContext) throws GeneralSecurityException {
            final KeyGenerator keyGenerator = JsseJce.getKeyGenerator(clientHandshakeContext.negotiatedProtocol.useTLS12PlusSpec() ? "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret");
            keyGenerator.init(new TlsRsaPremasterSecretParameterSpec(clientHandshakeContext.clientHelloVersion, clientHandshakeContext.negotiatedProtocol.id), clientHandshakeContext.sslContext.getSecureRandom());
            return new RSAPremasterSecret(keyGenerator.generateKey());
        }
        
        static RSAPremasterSecret decode(final ServerHandshakeContext serverHandshakeContext, final PrivateKey privateKey, final byte[] array) throws GeneralSecurityException {
            byte[] doFinal = null;
            final Cipher cipher = JsseJce.getCipher("RSA/ECB/PKCS1Padding");
            boolean b;
            try {
                cipher.init(4, privateKey, new TlsRsaPremasterSecretParameterSpec(serverHandshakeContext.clientHelloVersion, serverHandshakeContext.negotiatedProtocol.id), serverHandshakeContext.sslContext.getSecureRandom());
                b = !KeyUtil.isOracleJCEProvider(cipher.getProvider().getName());
            }
            catch (final InvalidKeyException | UnsupportedOperationException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("The Cipher provider " + safeProviderName(cipher) + " caused exception: " + ((Throwable)ex).getMessage(), new Object[0]);
                }
                b = true;
            }
            SecretKey generatePremasterSecret;
            if (b) {
                final Cipher cipher2 = JsseJce.getCipher("RSA/ECB/PKCS1Padding");
                cipher2.init(2, privateKey);
                boolean b2 = false;
                try {
                    doFinal = cipher2.doFinal(array);
                }
                catch (final BadPaddingException ex2) {
                    b2 = true;
                }
                generatePremasterSecret = generatePremasterSecret(serverHandshakeContext.clientHelloVersion, serverHandshakeContext.negotiatedProtocol.id, KeyUtil.checkTlsPreMasterSecretKey(serverHandshakeContext.clientHelloVersion, serverHandshakeContext.negotiatedProtocol.id, serverHandshakeContext.sslContext.getSecureRandom(), doFinal, b2), serverHandshakeContext.sslContext.getSecureRandom());
            }
            else {
                generatePremasterSecret = (SecretKey)cipher.unwrap(array, "TlsRsaPremasterSecret", 3);
            }
            return new RSAPremasterSecret(generatePremasterSecret);
        }
        
        private static String safeProviderName(final Cipher cipher) {
            try {
                return cipher.getProvider().toString();
            }
            catch (final Exception ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Retrieving The Cipher provider name caused exception ", ex);
                }
                try {
                    return cipher.toString() + " (provider name not available)";
                }
                catch (final Exception ex2) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Retrieving The Cipher name caused exception ", ex2);
                    }
                    return "(cipher/provider names not available)";
                }
            }
        }
        
        private static SecretKey generatePremasterSecret(final int n, final int n2, final byte[] array, final SecureRandom secureRandom) throws GeneralSecurityException {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Generating a premaster secret", new Object[0]);
            }
            try {
                final KeyGenerator keyGenerator = JsseJce.getKeyGenerator((n >= ProtocolVersion.TLS12.id) ? "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret");
                keyGenerator.init(new TlsRsaPremasterSecretParameterSpec(n, n2, array), secureRandom);
                return keyGenerator.generateKey();
            }
            catch (final InvalidAlgorithmParameterException | NoSuchAlgorithmException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("RSA premaster secret generation error:", new Object[0]);
                    ((Throwable)ex).printStackTrace(System.out);
                }
                throw new GeneralSecurityException("Could not generate premaster secret", (Throwable)ex);
            }
        }
    }
    
    private static final class RSAKAGenerator implements SSLKeyAgreementGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext) throws IOException {
            RSAPremasterSecret rsaPremasterSecret = null;
            if (handshakeContext instanceof ClientHandshakeContext) {
                for (final SSLPossession sslPossession : handshakeContext.handshakePossessions) {
                    if (sslPossession instanceof RSAPremasterSecret) {
                        rsaPremasterSecret = (RSAPremasterSecret)sslPossession;
                        break;
                    }
                }
            }
            else {
                for (final SSLCredentials sslCredentials : handshakeContext.handshakeCredentials) {
                    if (sslCredentials instanceof RSAPremasterSecret) {
                        rsaPremasterSecret = (RSAPremasterSecret)sslCredentials;
                        break;
                    }
                }
            }
            if (rsaPremasterSecret == null) {
                throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No sufficient RSA key agreement parameters negotiated");
            }
            return new RSAKAKeyDerivation(handshakeContext, rsaPremasterSecret.premasterSecret);
        }
        
        private static final class RSAKAKeyDerivation implements SSLKeyDerivation
        {
            private final HandshakeContext context;
            private final SecretKey preMasterSecret;
            
            RSAKAKeyDerivation(final HandshakeContext context, final SecretKey preMasterSecret) {
                this.context = context;
                this.preMasterSecret = preMasterSecret;
            }
            
            @Override
            public SecretKey deriveKey(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws IOException {
                final SSLMasterKeyDerivation value = SSLMasterKeyDerivation.valueOf(this.context.negotiatedProtocol);
                if (value == null) {
                    throw new SSLHandshakeException("No expected master key derivation for protocol: " + this.context.negotiatedProtocol.name);
                }
                return value.createKeyDerivation(this.context, this.preMasterSecret).deriveKey("MasterSecret", algorithmParameterSpec);
            }
        }
    }
}
