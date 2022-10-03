package org.openjsse.sun.security.ssl;

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
import java.security.spec.ECGenParameterSpec;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.spec.ECPoint;
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
        
        static ECDHECredentials valueOf(final SupportedGroupsExtension.NamedGroup namedGroup, final byte[] encodedPoint) throws IOException, GeneralSecurityException {
            if (namedGroup.type != SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE) {
                throw new RuntimeException("Credentials decoding:  Not ECDHE named group");
            }
            if (encodedPoint == null || encodedPoint.length == 0) {
                return null;
            }
            final ECParameterSpec parameters = JsseJce.getECParameterSpec(namedGroup.oid);
            if (parameters == null) {
                return null;
            }
            final ECPoint point = JsseJce.decodePoint(encodedPoint, parameters.getCurve());
            final KeyFactory factory = JsseJce.getKeyFactory("EC");
            final ECPublicKey publicKey = (ECPublicKey)factory.generatePublic(new ECPublicKeySpec(point, parameters));
            return new ECDHECredentials(publicKey, namedGroup);
        }
    }
    
    static final class ECDHEPossession implements SSLPossession
    {
        final PrivateKey privateKey;
        final ECPublicKey publicKey;
        final SupportedGroupsExtension.NamedGroup namedGroup;
        
        ECDHEPossession(final SupportedGroupsExtension.NamedGroup namedGroup, final SecureRandom random) {
            try {
                final KeyPairGenerator kpg = JsseJce.getKeyPairGenerator("EC");
                final ECGenParameterSpec params = (ECGenParameterSpec)namedGroup.getParameterSpec();
                kpg.initialize(params, random);
                final KeyPair kp = kpg.generateKeyPair();
                this.privateKey = kp.getPrivate();
                this.publicKey = (ECPublicKey)kp.getPublic();
            }
            catch (final GeneralSecurityException e) {
                throw new RuntimeException("Could not generate ECDH keypair", e);
            }
            this.namedGroup = namedGroup;
        }
        
        ECDHEPossession(final ECDHECredentials credentials, final SecureRandom random) {
            final ECParameterSpec params = credentials.popPublicKey.getParams();
            try {
                final KeyPairGenerator kpg = JsseJce.getKeyPairGenerator("EC");
                kpg.initialize(params, random);
                final KeyPair kp = kpg.generateKeyPair();
                this.privateKey = kp.getPrivate();
                this.publicKey = (ECPublicKey)kp.getPublic();
            }
            catch (final GeneralSecurityException e) {
                throw new RuntimeException("Could not generate ECDH keypair", e);
            }
            this.namedGroup = credentials.namedGroup;
        }
        
        @Override
        public byte[] encode() {
            return ECUtil.encodePoint(this.publicKey.getW(), this.publicKey.getParams().getCurve());
        }
        
        SecretKey getAgreedSecret(final PublicKey peerPublicKey) throws SSLHandshakeException {
            try {
                final KeyAgreement ka = JsseJce.getKeyAgreement("ECDH");
                ka.init(this.privateKey);
                ka.doPhase(peerPublicKey, true);
                return ka.generateSecret("TlsPremasterSecret");
            }
            catch (final GeneralSecurityException e) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(e);
            }
        }
        
        SecretKey getAgreedSecret(final byte[] encodedPoint) throws SSLHandshakeException {
            try {
                final ECParameterSpec params = this.publicKey.getParams();
                final ECPoint point = JsseJce.decodePoint(encodedPoint, params.getCurve());
                final KeyFactory kf = JsseJce.getKeyFactory("EC");
                final ECPublicKeySpec spec = new ECPublicKeySpec(point, params);
                final PublicKey peerPublicKey = kf.generatePublic(spec);
                return this.getAgreedSecret(peerPublicKey);
            }
            catch (final GeneralSecurityException | IOException e) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(e);
            }
        }
        
        void checkConstraints(final AlgorithmConstraints constraints, final byte[] encodedPoint) throws SSLHandshakeException {
            try {
                final ECParameterSpec params = this.publicKey.getParams();
                final ECPoint point = JsseJce.decodePoint(encodedPoint, params.getCurve());
                final ECPublicKeySpec spec = new ECPublicKeySpec(point, params);
                final KeyFactory kf = JsseJce.getKeyFactory("EC");
                final ECPublicKey pubKey = (ECPublicKey)kf.generatePublic(spec);
                if (!constraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), pubKey)) {
                    throw new SSLHandshakeException("ECPublicKey does not comply to algorithm constraints");
                }
            }
            catch (final GeneralSecurityException | IOException e) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate ECPublicKey").initCause(e);
            }
        }
    }
    
    private static final class ECDHEPossessionGenerator implements SSLPossessionGenerator
    {
        @Override
        public SSLPossession createPossession(final HandshakeContext context) {
            SupportedGroupsExtension.NamedGroup preferableNamedGroup = null;
            if (context.clientRequestedNamedGroups != null && !context.clientRequestedNamedGroups.isEmpty()) {
                preferableNamedGroup = SupportedGroupsExtension.SupportedGroups.getPreferredGroup(context.negotiatedProtocol, context.algorithmConstraints, SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE, context.clientRequestedNamedGroups);
            }
            else {
                preferableNamedGroup = SupportedGroupsExtension.SupportedGroups.getPreferredGroup(context.negotiatedProtocol, context.algorithmConstraints, SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE);
            }
            if (preferableNamedGroup != null) {
                return new ECDHEPossession(preferableNamedGroup, context.sslContext.getSecureRandom());
            }
            return null;
        }
    }
    
    private static final class ECDHKAGenerator implements SSLKeyAgreementGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext context) throws IOException {
            if (context instanceof ServerHandshakeContext) {
                return this.createServerKeyDerivation((ServerHandshakeContext)context);
            }
            return this.createClientKeyDerivation((ClientHandshakeContext)context);
        }
        
        private SSLKeyDerivation createServerKeyDerivation(final ServerHandshakeContext shc) throws IOException {
            X509Authentication.X509Possession x509Possession = null;
            ECDHECredentials ecdheCredentials = null;
            for (final SSLPossession poss : shc.handshakePossessions) {
                if (!(poss instanceof X509Authentication.X509Possession)) {
                    continue;
                }
                final ECParameterSpec params = ((X509Authentication.X509Possession)poss).getECParameterSpec();
                if (params == null) {
                    continue;
                }
                final SupportedGroupsExtension.NamedGroup ng = SupportedGroupsExtension.NamedGroup.valueOf(params);
                if (ng == null) {
                    throw shc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported EC server cert for ECDH key exchange");
                }
                for (final SSLCredentials cred : shc.handshakeCredentials) {
                    if (!(cred instanceof ECDHECredentials)) {
                        continue;
                    }
                    if (ng.equals(((ECDHECredentials)cred).namedGroup)) {
                        ecdheCredentials = (ECDHECredentials)cred;
                        break;
                    }
                }
                if (ecdheCredentials != null) {
                    x509Possession = (X509Authentication.X509Possession)poss;
                    break;
                }
            }
            if (x509Possession == null || ecdheCredentials == null) {
                throw shc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No sufficient ECDHE key agreement parameters negotiated");
            }
            return new ECDHEKAKeyDerivation(shc, x509Possession.popPrivateKey, ecdheCredentials.popPublicKey);
        }
        
        private SSLKeyDerivation createClientKeyDerivation(final ClientHandshakeContext chc) throws IOException {
            ECDHEPossession ecdhePossession = null;
            X509Authentication.X509Credentials x509Credentials = null;
            for (final SSLPossession poss : chc.handshakePossessions) {
                if (!(poss instanceof ECDHEPossession)) {
                    continue;
                }
                final SupportedGroupsExtension.NamedGroup ng = ((ECDHEPossession)poss).namedGroup;
                for (final SSLCredentials cred : chc.handshakeCredentials) {
                    if (!(cred instanceof X509Authentication.X509Credentials)) {
                        continue;
                    }
                    final PublicKey publicKey = ((X509Authentication.X509Credentials)cred).popPublicKey;
                    if (!publicKey.getAlgorithm().equals("EC")) {
                        continue;
                    }
                    final ECParameterSpec params = ((ECPublicKey)publicKey).getParams();
                    final SupportedGroupsExtension.NamedGroup namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(params);
                    if (namedGroup == null) {
                        throw chc.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Unsupported EC server cert for ECDH key exchange");
                    }
                    if (ng.equals(namedGroup)) {
                        x509Credentials = (X509Authentication.X509Credentials)cred;
                        break;
                    }
                }
                if (x509Credentials != null) {
                    ecdhePossession = (ECDHEPossession)poss;
                    break;
                }
            }
            if (ecdhePossession == null || x509Credentials == null) {
                throw chc.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No sufficient ECDH key agreement parameters negotiated");
            }
            return new ECDHEKAKeyDerivation(chc, ecdhePossession.privateKey, x509Credentials.popPublicKey);
        }
    }
    
    private static final class ECDHEKAGenerator implements SSLKeyAgreementGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext context) throws IOException {
            ECDHEPossession ecdhePossession = null;
            ECDHECredentials ecdheCredentials = null;
            for (final SSLPossession poss : context.handshakePossessions) {
                if (!(poss instanceof ECDHEPossession)) {
                    continue;
                }
                final SupportedGroupsExtension.NamedGroup ng = ((ECDHEPossession)poss).namedGroup;
                for (final SSLCredentials cred : context.handshakeCredentials) {
                    if (!(cred instanceof ECDHECredentials)) {
                        continue;
                    }
                    if (ng.equals(((ECDHECredentials)cred).namedGroup)) {
                        ecdheCredentials = (ECDHECredentials)cred;
                        break;
                    }
                }
                if (ecdheCredentials != null) {
                    ecdhePossession = (ECDHEPossession)poss;
                    break;
                }
            }
            if (ecdhePossession == null || ecdheCredentials == null) {
                throw context.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No sufficient ECDHE key agreement parameters negotiated");
            }
            return new ECDHEKAKeyDerivation(context, ecdhePossession.privateKey, ecdheCredentials.popPublicKey);
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
        public SecretKey deriveKey(final String algorithm, final AlgorithmParameterSpec params) throws IOException {
            if (!this.context.negotiatedProtocol.useTLS13PlusSpec()) {
                return this.t12DeriveKey(algorithm, params);
            }
            return this.t13DeriveKey(algorithm, params);
        }
        
        private SecretKey t12DeriveKey(final String algorithm, final AlgorithmParameterSpec params) throws IOException {
            try {
                final KeyAgreement ka = JsseJce.getKeyAgreement("ECDH");
                ka.init(this.localPrivateKey);
                ka.doPhase(this.peerPublicKey, true);
                final SecretKey preMasterSecret = ka.generateSecret("TlsPremasterSecret");
                final SSLMasterKeyDerivation mskd = SSLMasterKeyDerivation.valueOf(this.context.negotiatedProtocol);
                if (mskd == null) {
                    throw new SSLHandshakeException("No expected master key derivation for protocol: " + this.context.negotiatedProtocol.name);
                }
                final SSLKeyDerivation kd = mskd.createKeyDerivation(this.context, preMasterSecret);
                return kd.deriveKey("MasterSecret", params);
            }
            catch (final GeneralSecurityException gse) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(gse);
            }
        }
        
        private SecretKey t13DeriveKey(final String algorithm, final AlgorithmParameterSpec params) throws IOException {
            try {
                final KeyAgreement ka = JsseJce.getKeyAgreement("ECDH");
                ka.init(this.localPrivateKey);
                ka.doPhase(this.peerPublicKey, true);
                final SecretKey sharedSecret = ka.generateSecret("TlsPremasterSecret");
                final CipherSuite.HashAlg hashAlg = this.context.negotiatedCipherSuite.hashAlg;
                SSLKeyDerivation kd = this.context.handshakeKeyDerivation;
                final HKDF hkdf = new HKDF(hashAlg.name);
                if (kd == null) {
                    final byte[] zeros = new byte[hashAlg.hashLength];
                    final SecretKeySpec ikm = new SecretKeySpec(zeros, "TlsPreSharedSecret");
                    final SecretKey earlySecret = hkdf.extract(zeros, ikm, "TlsEarlySecret");
                    kd = new SSLSecretDerivation(this.context, earlySecret);
                }
                final SecretKey saltSecret = kd.deriveKey("TlsSaltSecret", null);
                return hkdf.extract(saltSecret, sharedSecret, algorithm);
            }
            catch (final GeneralSecurityException gse) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(gse);
            }
        }
    }
}
