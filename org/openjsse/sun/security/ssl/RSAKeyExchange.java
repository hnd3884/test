package org.openjsse.sun.security.ssl;

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
        public SSLPossession createPossession(final HandshakeContext context) {
            try {
                final EphemeralKeyManager ekm = context.sslContext.getEphemeralKeyManager();
                final KeyPair kp = ekm.getRSAKeyPair(true, context.sslContext.getSecureRandom());
                if (kp != null) {
                    return new EphemeralRSAPossession(kp.getPrivate(), (RSAPublicKey)kp.getPublic());
                }
                return null;
            }
            catch (final RuntimeException rte) {
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
        
        static RSAPremasterSecret createPremasterSecret(final ClientHandshakeContext chc) throws GeneralSecurityException {
            final String algorithm = chc.negotiatedProtocol.useTLS12PlusSpec() ? "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret";
            final KeyGenerator kg = JsseJce.getKeyGenerator(algorithm);
            final TlsRsaPremasterSecretParameterSpec spec = new TlsRsaPremasterSecretParameterSpec(chc.clientHelloVersion, chc.negotiatedProtocol.id);
            kg.init(spec, chc.sslContext.getSecureRandom());
            return new RSAPremasterSecret(kg.generateKey());
        }
        
        static RSAPremasterSecret decode(final ServerHandshakeContext shc, final PrivateKey privateKey, final byte[] encrypted) throws GeneralSecurityException {
            byte[] encoded = null;
            boolean needFailover = false;
            Cipher cipher = JsseJce.getCipher("RSA/ECB/PKCS1Padding");
            try {
                cipher.init(4, privateKey, new TlsRsaPremasterSecretParameterSpec(shc.clientHelloVersion, shc.negotiatedProtocol.id), shc.sslContext.getSecureRandom());
                needFailover = !KeyUtil.isOracleJCEProvider(cipher.getProvider().getName());
            }
            catch (final InvalidKeyException | UnsupportedOperationException iue) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("The Cipher provider " + safeProviderName(cipher) + " caused exception: " + iue.getMessage(), new Object[0]);
                }
                needFailover = true;
            }
            SecretKey preMaster;
            if (needFailover) {
                cipher = JsseJce.getCipher("RSA/ECB/PKCS1Padding");
                cipher.init(2, privateKey);
                boolean failed = false;
                try {
                    encoded = cipher.doFinal(encrypted);
                }
                catch (final BadPaddingException bpe) {
                    failed = true;
                }
                encoded = KeyUtil.checkTlsPreMasterSecretKey(shc.clientHelloVersion, shc.negotiatedProtocol.id, shc.sslContext.getSecureRandom(), encoded, failed);
                preMaster = generatePremasterSecret(shc.clientHelloVersion, shc.negotiatedProtocol.id, encoded, shc.sslContext.getSecureRandom());
            }
            else {
                preMaster = (SecretKey)cipher.unwrap(encrypted, "TlsRsaPremasterSecret", 3);
            }
            return new RSAPremasterSecret(preMaster);
        }
        
        private static String safeProviderName(final Cipher cipher) {
            try {
                return cipher.getProvider().toString();
            }
            catch (final Exception e) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Retrieving The Cipher provider name caused exception ", e);
                }
                try {
                    return cipher.toString() + " (provider name not available)";
                }
                catch (final Exception e) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Retrieving The Cipher name caused exception ", e);
                    }
                    return "(cipher/provider names not available)";
                }
            }
        }
        
        private static SecretKey generatePremasterSecret(final int clientVersion, final int serverVersion, final byte[] encodedSecret, final SecureRandom generator) throws GeneralSecurityException {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Generating a premaster secret", new Object[0]);
            }
            try {
                final String s = (clientVersion >= ProtocolVersion.TLS12.id) ? "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret";
                final KeyGenerator kg = JsseJce.getKeyGenerator(s);
                kg.init(new TlsRsaPremasterSecretParameterSpec(clientVersion, serverVersion, encodedSecret), generator);
                return kg.generateKey();
            }
            catch (final InvalidAlgorithmParameterException | NoSuchAlgorithmException iae) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("RSA premaster secret generation error:", new Object[0]);
                    iae.printStackTrace(System.out);
                }
                throw new GeneralSecurityException("Could not generate premaster secret", iae);
            }
        }
    }
    
    private static final class RSAKAGenerator implements SSLKeyAgreementGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext context) throws IOException {
            RSAPremasterSecret premaster = null;
            if (context instanceof ClientHandshakeContext) {
                for (final SSLPossession possession : context.handshakePossessions) {
                    if (possession instanceof RSAPremasterSecret) {
                        premaster = (RSAPremasterSecret)possession;
                        break;
                    }
                }
            }
            else {
                for (final SSLCredentials credential : context.handshakeCredentials) {
                    if (credential instanceof RSAPremasterSecret) {
                        premaster = (RSAPremasterSecret)credential;
                        break;
                    }
                }
            }
            if (premaster == null) {
                throw context.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No sufficient RSA key agreement parameters negotiated");
            }
            return new RSAKAKeyDerivation(context, premaster.premasterSecret);
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
            public SecretKey deriveKey(final String algorithm, final AlgorithmParameterSpec params) throws IOException {
                final SSLMasterKeyDerivation mskd = SSLMasterKeyDerivation.valueOf(this.context.negotiatedProtocol);
                if (mskd == null) {
                    throw new SSLHandshakeException("No expected master key derivation for protocol: " + this.context.negotiatedProtocol.name);
                }
                final SSLKeyDerivation kd = mskd.createKeyDerivation(this.context, this.preMasterSecret);
                return kd.deriveKey("MasterSecret", params);
            }
        }
    }
}
