package sun.security.ssl;

import javax.crypto.spec.SecretKeySpec;
import java.util.Iterator;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.AlgorithmConstraints;
import javax.crypto.KeyAgreement;
import javax.net.ssl.SSLHandshakeException;
import java.security.Key;
import javax.crypto.SecretKey;
import java.security.PublicKey;
import sun.security.util.ECUtil;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.security.spec.ECParameterSpec;
import java.security.spec.KeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.interfaces.ECPublicKey;

final class ECDHKeyExchange
{
    static final SSLPossessionGenerator poGenerator;
    static final SSLKeyAgreementGenerator ecdheKAGenerator;
    static final SSLKeyAgreementGenerator ecdhKAGenerator;
    
    static {
        poGenerator = new ECDHEPossessionGenerator();
        ecdheKAGenerator = new ECDHEKAGenerator();
        ecdhKAGenerator = new ECDHKAGenerator();
    }
    
    static final class ECDHECredentials implements SSLCredentials
    {
        final ECPublicKey popPublicKey;
        final SupportedGroupsExtension.NamedGroup namedGroup;
        
        ECDHECredentials(final ECPublicKey popPublicKey, final SupportedGroupsExtension.NamedGroup namedGroup) {
            this.popPublicKey = popPublicKey;
            this.namedGroup = namedGroup;
        }
        
        static ECDHECredentials valueOf(final SupportedGroupsExtension.NamedGroup namedGroup, final byte[] array) throws IOException, GeneralSecurityException {
            if (namedGroup.type != SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE) {
                throw new RuntimeException("Credentials decoding:  Not ECDHE named group");
            }
            if (array == null || array.length == 0) {
                return null;
            }
            final ECParameterSpec ecParameterSpec = JsseJce.getECParameterSpec(namedGroup.oid);
            if (ecParameterSpec == null) {
                return null;
            }
            return new ECDHECredentials((ECPublicKey)JsseJce.getKeyFactory("EC").generatePublic(new ECPublicKeySpec(JsseJce.decodePoint(array, ecParameterSpec.getCurve()), ecParameterSpec)), namedGroup);
        }
    }
    
    static final class ECDHEPossession implements SSLPossession
    {
        final PrivateKey privateKey;
        final ECPublicKey publicKey;
        final SupportedGroupsExtension.NamedGroup namedGroup;
        
        ECDHEPossession(final SupportedGroupsExtension.NamedGroup namedGroup, final SecureRandom secureRandom) {
            try {
                final KeyPairGenerator keyPairGenerator = JsseJce.getKeyPairGenerator("EC");
                keyPairGenerator.initialize(namedGroup.getParameterSpec(), secureRandom);
                final KeyPair generateKeyPair = keyPairGenerator.generateKeyPair();
                this.privateKey = generateKeyPair.getPrivate();
                this.publicKey = (ECPublicKey)generateKeyPair.getPublic();
            }
            catch (final GeneralSecurityException ex) {
                throw new RuntimeException("Could not generate ECDH keypair", ex);
            }
            this.namedGroup = namedGroup;
        }
        
        ECDHEPossession(final ECDHECredentials ecdheCredentials, final SecureRandom secureRandom) {
            final ECParameterSpec params = ecdheCredentials.popPublicKey.getParams();
            try {
                final KeyPairGenerator keyPairGenerator = JsseJce.getKeyPairGenerator("EC");
                keyPairGenerator.initialize(params, secureRandom);
                final KeyPair generateKeyPair = keyPairGenerator.generateKeyPair();
                this.privateKey = generateKeyPair.getPrivate();
                this.publicKey = (ECPublicKey)generateKeyPair.getPublic();
            }
            catch (final GeneralSecurityException ex) {
                throw new RuntimeException("Could not generate ECDH keypair", ex);
            }
            this.namedGroup = ecdheCredentials.namedGroup;
        }
        
        @Override
        public byte[] encode() {
            return ECUtil.encodePoint(this.publicKey.getW(), this.publicKey.getParams().getCurve());
        }
        
        SecretKey getAgreedSecret(final PublicKey publicKey) throws SSLHandshakeException {
            try {
                final KeyAgreement keyAgreement = JsseJce.getKeyAgreement("ECDH");
                keyAgreement.init(this.privateKey);
                keyAgreement.doPhase(publicKey, true);
                return keyAgreement.generateSecret("TlsPremasterSecret");
            }
            catch (final GeneralSecurityException ex) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(ex);
            }
        }
        
        SecretKey getAgreedSecret(final byte[] array) throws SSLHandshakeException {
            try {
                final ECParameterSpec params = this.publicKey.getParams();
                return this.getAgreedSecret(JsseJce.getKeyFactory("EC").generatePublic(new ECPublicKeySpec(JsseJce.decodePoint(array, params.getCurve()), params)));
            }
            catch (final GeneralSecurityException | IOException ex) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause((Throwable)ex);
            }
        }
        
        void checkConstraints(final AlgorithmConstraints algorithmConstraints, final byte[] array) throws SSLHandshakeException {
            try {
                final ECParameterSpec params = this.publicKey.getParams();
                if (!algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), JsseJce.getKeyFactory("EC").generatePublic(new ECPublicKeySpec(JsseJce.decodePoint(array, params.getCurve()), params)))) {
                    throw new SSLHandshakeException("ECPublicKey does not comply to algorithm constraints");
                }
            }
            catch (final GeneralSecurityException | IOException ex) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate ECPublicKey").initCause((Throwable)ex);
            }
        }
    }
    
    private static final class ECDHEPossessionGenerator implements SSLPossessionGenerator
    {
        @Override
        public SSLPossession createPossession(final HandshakeContext handshakeContext) {
            SupportedGroupsExtension.NamedGroup namedGroup;
            if (handshakeContext.clientRequestedNamedGroups != null && !handshakeContext.clientRequestedNamedGroups.isEmpty()) {
                namedGroup = SupportedGroupsExtension.SupportedGroups.getPreferredGroup(handshakeContext.negotiatedProtocol, handshakeContext.algorithmConstraints, SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE, handshakeContext.clientRequestedNamedGroups);
            }
            else {
                namedGroup = SupportedGroupsExtension.SupportedGroups.getPreferredGroup(handshakeContext.negotiatedProtocol, handshakeContext.algorithmConstraints, SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE);
            }
            if (namedGroup != null) {
                return new ECDHEPossession(namedGroup, handshakeContext.sslContext.getSecureRandom());
            }
            return null;
        }
    }
    
    private static final class ECDHKAGenerator implements SSLKeyAgreementGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext) throws IOException {
            if (handshakeContext instanceof ServerHandshakeContext) {
                return this.createServerKeyDerivation((ServerHandshakeContext)handshakeContext);
            }
            return this.createClientKeyDerivation((ClientHandshakeContext)handshakeContext);
        }
        
        private SSLKeyDerivation createServerKeyDerivation(final ServerHandshakeContext serverHandshakeContext) throws IOException {
            X509Authentication.X509Possession x509Possession = null;
            ECDHECredentials ecdheCredentials = null;
            for (final SSLPossession sslPossession : serverHandshakeContext.handshakePossessions) {
                if (!(sslPossession instanceof X509Authentication.X509Possession)) {
                    continue;
                }
                final ECParameterSpec ecParameterSpec = ((X509Authentication.X509Possession)sslPossession).getECParameterSpec();
                if (ecParameterSpec == null) {
                    continue;
                }
                final SupportedGroupsExtension.NamedGroup value = SupportedGroupsExtension.NamedGroup.valueOf(ecParameterSpec);
                if (value == null) {
                    throw serverHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported EC server cert for ECDH key exchange");
                }
                for (final SSLCredentials sslCredentials : serverHandshakeContext.handshakeCredentials) {
                    if (!(sslCredentials instanceof ECDHECredentials)) {
                        continue;
                    }
                    if (value.equals(((ECDHECredentials)sslCredentials).namedGroup)) {
                        ecdheCredentials = (ECDHECredentials)sslCredentials;
                        break;
                    }
                }
                if (ecdheCredentials != null) {
                    x509Possession = (X509Authentication.X509Possession)sslPossession;
                    break;
                }
            }
            if (x509Possession == null || ecdheCredentials == null) {
                throw serverHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No sufficient ECDHE key agreement parameters negotiated");
            }
            return new ECDHEKAKeyDerivation(serverHandshakeContext, x509Possession.popPrivateKey, ecdheCredentials.popPublicKey);
        }
        
        private SSLKeyDerivation createClientKeyDerivation(final ClientHandshakeContext clientHandshakeContext) throws IOException {
            ECDHEPossession ecdhePossession = null;
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLPossession sslPossession : clientHandshakeContext.handshakePossessions) {
                if (!(sslPossession instanceof ECDHEPossession)) {
                    continue;
                }
                final SupportedGroupsExtension.NamedGroup namedGroup = ((ECDHEPossession)sslPossession).namedGroup;
                for (final SSLCredentials sslCredentials : clientHandshakeContext.handshakeCredentials) {
                    if (!(sslCredentials instanceof X509Authentication.X509Credentials)) {
                        continue;
                    }
                    final PublicKey popPublicKey = ((X509Authentication.X509Credentials)sslCredentials).popPublicKey;
                    if (!popPublicKey.getAlgorithm().equals("EC")) {
                        continue;
                    }
                    final SupportedGroupsExtension.NamedGroup value = SupportedGroupsExtension.NamedGroup.valueOf(((ECPublicKey)popPublicKey).getParams());
                    if (value == null) {
                        throw clientHandshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported EC server cert for ECDH key exchange");
                    }
                    if (namedGroup.equals(value)) {
                        x509Credentials = (X509Authentication.X509Credentials)sslCredentials;
                        break;
                    }
                }
                if (x509Credentials != null) {
                    ecdhePossession = (ECDHEPossession)sslPossession;
                    break;
                }
            }
            if (ecdhePossession == null || x509Credentials == null) {
                throw clientHandshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No sufficient ECDH key agreement parameters negotiated");
            }
            return new ECDHEKAKeyDerivation(clientHandshakeContext, ecdhePossession.privateKey, x509Credentials.popPublicKey);
        }
    }
    
    private static final class ECDHEKAGenerator implements SSLKeyAgreementGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext) throws IOException {
            ECDHEPossession ecdhePossession = null;
            ECDHECredentials ecdheCredentials = null;
            for (final SSLPossession sslPossession : handshakeContext.handshakePossessions) {
                if (!(sslPossession instanceof ECDHEPossession)) {
                    continue;
                }
                final SupportedGroupsExtension.NamedGroup namedGroup = ((ECDHEPossession)sslPossession).namedGroup;
                for (final SSLCredentials sslCredentials : handshakeContext.handshakeCredentials) {
                    if (!(sslCredentials instanceof ECDHECredentials)) {
                        continue;
                    }
                    if (namedGroup.equals(((ECDHECredentials)sslCredentials).namedGroup)) {
                        ecdheCredentials = (ECDHECredentials)sslCredentials;
                        break;
                    }
                }
                if (ecdheCredentials != null) {
                    ecdhePossession = (ECDHEPossession)sslPossession;
                    break;
                }
            }
            if (ecdhePossession == null || ecdheCredentials == null) {
                throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No sufficient ECDHE key agreement parameters negotiated");
            }
            return new ECDHEKAKeyDerivation(handshakeContext, ecdhePossession.privateKey, ecdheCredentials.popPublicKey);
        }
    }
    
    private static final class ECDHEKAKeyDerivation implements SSLKeyDerivation
    {
        private final HandshakeContext context;
        private final PrivateKey localPrivateKey;
        private final PublicKey peerPublicKey;
        
        ECDHEKAKeyDerivation(final HandshakeContext context, final PrivateKey localPrivateKey, final PublicKey peerPublicKey) {
            this.context = context;
            this.localPrivateKey = localPrivateKey;
            this.peerPublicKey = peerPublicKey;
        }
        
        @Override
        public SecretKey deriveKey(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws IOException {
            if (!this.context.negotiatedProtocol.useTLS13PlusSpec()) {
                return this.t12DeriveKey(s, algorithmParameterSpec);
            }
            return this.t13DeriveKey(s, algorithmParameterSpec);
        }
        
        private SecretKey t12DeriveKey(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws IOException {
            try {
                final KeyAgreement keyAgreement = JsseJce.getKeyAgreement("ECDH");
                keyAgreement.init(this.localPrivateKey);
                keyAgreement.doPhase(this.peerPublicKey, true);
                final SecretKey generateSecret = keyAgreement.generateSecret("TlsPremasterSecret");
                final SSLMasterKeyDerivation value = SSLMasterKeyDerivation.valueOf(this.context.negotiatedProtocol);
                if (value == null) {
                    throw new SSLHandshakeException("No expected master key derivation for protocol: " + this.context.negotiatedProtocol.name);
                }
                return value.createKeyDerivation(this.context, generateSecret).deriveKey("MasterSecret", algorithmParameterSpec);
            }
            catch (final GeneralSecurityException ex) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(ex);
            }
        }
        
        private SecretKey t13DeriveKey(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws IOException {
            try {
                final KeyAgreement keyAgreement = JsseJce.getKeyAgreement("ECDH");
                keyAgreement.init(this.localPrivateKey);
                keyAgreement.doPhase(this.peerPublicKey, true);
                final SecretKey generateSecret = keyAgreement.generateSecret("TlsPremasterSecret");
                final CipherSuite.HashAlg hashAlg = this.context.negotiatedCipherSuite.hashAlg;
                SSLKeyDerivation handshakeKeyDerivation = this.context.handshakeKeyDerivation;
                final HKDF hkdf = new HKDF(hashAlg.name);
                if (handshakeKeyDerivation == null) {
                    final byte[] array = new byte[hashAlg.hashLength];
                    handshakeKeyDerivation = new SSLSecretDerivation(this.context, hkdf.extract(array, new SecretKeySpec(array, "TlsPreSharedSecret"), "TlsEarlySecret"));
                }
                return hkdf.extract(handshakeKeyDerivation.deriveKey("TlsSaltSecret", null), generateSecret, s);
            }
            catch (final GeneralSecurityException ex) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(ex);
            }
        }
    }
}
