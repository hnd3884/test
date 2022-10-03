package org.openjsse.sun.security.ssl;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyAgreement;
import javax.net.ssl.SSLHandshakeException;
import javax.crypto.SecretKey;
import java.util.Iterator;
import sun.security.action.GetPropertyAction;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.security.PublicKey;
import java.security.InvalidKeyException;
import sun.security.util.KeyUtil;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.interfaces.DHPublicKey;

final class DHKeyExchange
{
    static final SSLPossessionGenerator poGenerator;
    static final SSLPossessionGenerator poExportableGenerator;
    static final SSLKeyAgreementGenerator kaGenerator;
    
    static {
        poGenerator = new DHEPossessionGenerator(false);
        poExportableGenerator = new DHEPossessionGenerator(true);
        kaGenerator = new DHEKAGenerator();
    }
    
    static final class DHECredentials implements SSLCredentials
    {
        final DHPublicKey popPublicKey;
        final SupportedGroupsExtension.NamedGroup namedGroup;
        
        DHECredentials(final DHPublicKey popPublicKey, final SupportedGroupsExtension.NamedGroup namedGroup) {
            this.popPublicKey = popPublicKey;
            this.namedGroup = namedGroup;
        }
        
        static DHECredentials valueOf(final SupportedGroupsExtension.NamedGroup ng, final byte[] encodedPublic) throws IOException, GeneralSecurityException {
            if (ng.type != SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_FFDHE) {
                throw new RuntimeException("Credentials decoding:  Not FFDHE named group");
            }
            if (encodedPublic == null || encodedPublic.length == 0) {
                return null;
            }
            final DHParameterSpec params = (DHParameterSpec)ng.getParameterSpec();
            if (params == null) {
                return null;
            }
            final KeyFactory kf = JsseJce.getKeyFactory("DiffieHellman");
            final DHPublicKeySpec spec = new DHPublicKeySpec(new BigInteger(1, encodedPublic), params.getP(), params.getG());
            final DHPublicKey publicKey = (DHPublicKey)kf.generatePublic(spec);
            return new DHECredentials(publicKey, ng);
        }
    }
    
    static final class DHEPossession implements SSLPossession
    {
        final PrivateKey privateKey;
        final DHPublicKey publicKey;
        final SupportedGroupsExtension.NamedGroup namedGroup;
        
        DHEPossession(final SupportedGroupsExtension.NamedGroup namedGroup, final SecureRandom random) {
            try {
                final KeyPairGenerator kpg = JsseJce.getKeyPairGenerator("DiffieHellman");
                final DHParameterSpec params = (DHParameterSpec)namedGroup.getParameterSpec();
                kpg.initialize(params, random);
                final KeyPair kp = this.generateDHKeyPair(kpg);
                if (kp == null) {
                    throw new RuntimeException("Could not generate DH keypair");
                }
                this.privateKey = kp.getPrivate();
                this.publicKey = (DHPublicKey)kp.getPublic();
            }
            catch (final GeneralSecurityException gse) {
                throw new RuntimeException("Could not generate DH keypair", gse);
            }
            this.namedGroup = namedGroup;
        }
        
        DHEPossession(final int keyLength, final SecureRandom random) {
            final DHParameterSpec params = PredefinedDHParameterSpecs.definedParams.get(keyLength);
            try {
                final KeyPairGenerator kpg = JsseJce.getKeyPairGenerator("DiffieHellman");
                if (params != null) {
                    kpg.initialize(params, random);
                }
                else {
                    kpg.initialize(keyLength, random);
                }
                final KeyPair kp = this.generateDHKeyPair(kpg);
                if (kp == null) {
                    throw new RuntimeException("Could not generate DH keypair of " + keyLength + " bits");
                }
                this.privateKey = kp.getPrivate();
                this.publicKey = (DHPublicKey)kp.getPublic();
            }
            catch (final GeneralSecurityException gse) {
                throw new RuntimeException("Could not generate DH keypair", gse);
            }
            this.namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(this.publicKey.getParams());
        }
        
        DHEPossession(final DHECredentials credentials, final SecureRandom random) {
            try {
                final KeyPairGenerator kpg = JsseJce.getKeyPairGenerator("DiffieHellman");
                kpg.initialize(credentials.popPublicKey.getParams(), random);
                final KeyPair kp = this.generateDHKeyPair(kpg);
                if (kp == null) {
                    throw new RuntimeException("Could not generate DH keypair");
                }
                this.privateKey = kp.getPrivate();
                this.publicKey = (DHPublicKey)kp.getPublic();
            }
            catch (final GeneralSecurityException gse) {
                throw new RuntimeException("Could not generate DH keypair", gse);
            }
            this.namedGroup = credentials.namedGroup;
        }
        
        private KeyPair generateDHKeyPair(final KeyPairGenerator kpg) throws GeneralSecurityException {
            final boolean doExtraValiadtion = !KeyUtil.isOracleJCEProvider(kpg.getProvider().getName());
            boolean isRecovering = false;
            int i = 0;
            while (i <= 2) {
                final KeyPair kp = kpg.generateKeyPair();
                if (doExtraValiadtion) {
                    final DHPublicKeySpec spec = getDHPublicKeySpec(kp.getPublic());
                    Label_0075: {
                        try {
                            KeyUtil.validate(spec);
                        }
                        catch (final InvalidKeyException ivke) {
                            if (isRecovering) {
                                throw ivke;
                            }
                            isRecovering = true;
                            break Label_0075;
                        }
                        return kp;
                    }
                    ++i;
                    continue;
                }
                return kp;
            }
            return null;
        }
        
        private static DHPublicKeySpec getDHPublicKeySpec(final PublicKey key) {
            if (key instanceof DHPublicKey) {
                final DHPublicKey dhKey = (DHPublicKey)key;
                final DHParameterSpec params = dhKey.getParams();
                return new DHPublicKeySpec(dhKey.getY(), params.getP(), params.getG());
            }
            try {
                final KeyFactory factory = JsseJce.getKeyFactory("DiffieHellman");
                return factory.getKeySpec(key, DHPublicKeySpec.class);
            }
            catch (final NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException("Unable to get DHPublicKeySpec", e);
            }
        }
        
        @Override
        public byte[] encode() {
            byte[] encoded = Utilities.toByteArray(this.publicKey.getY());
            final int pSize = KeyUtil.getKeySize(this.publicKey) + 7 >>> 3;
            if (pSize > 0 && encoded.length < pSize) {
                final byte[] buffer = new byte[pSize];
                System.arraycopy(encoded, 0, buffer, pSize - encoded.length, encoded.length);
                encoded = buffer;
            }
            return encoded;
        }
    }
    
    private static final class DHEPossessionGenerator implements SSLPossessionGenerator
    {
        private static final boolean useSmartEphemeralDHKeys;
        private static final boolean useLegacyEphemeralDHKeys;
        private static final int customizedDHKeySize;
        private final boolean exportable;
        
        private DHEPossessionGenerator(final boolean exportable) {
            this.exportable = exportable;
        }
        
        @Override
        public SSLPossession createPossession(final HandshakeContext context) {
            SupportedGroupsExtension.NamedGroup preferableNamedGroup = null;
            if (!DHEPossessionGenerator.useLegacyEphemeralDHKeys && context.clientRequestedNamedGroups != null && !context.clientRequestedNamedGroups.isEmpty()) {
                preferableNamedGroup = SupportedGroupsExtension.SupportedGroups.getPreferredGroup(context.negotiatedProtocol, context.algorithmConstraints, SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_FFDHE, context.clientRequestedNamedGroups);
                if (preferableNamedGroup != null) {
                    return new DHEPossession(preferableNamedGroup, context.sslContext.getSecureRandom());
                }
            }
            int keySize = this.exportable ? 512 : 1024;
            if (!this.exportable) {
                if (DHEPossessionGenerator.useLegacyEphemeralDHKeys) {
                    keySize = 768;
                }
                else if (DHEPossessionGenerator.useSmartEphemeralDHKeys) {
                    PrivateKey key = null;
                    final ServerHandshakeContext shc = (ServerHandshakeContext)context;
                    if (shc.interimAuthn instanceof X509Authentication.X509Possession) {
                        key = ((X509Authentication.X509Possession)shc.interimAuthn).popPrivateKey;
                    }
                    if (key != null) {
                        final int ks = KeyUtil.getKeySize(key);
                        keySize = ((ks <= 1024) ? 1024 : 2048);
                    }
                }
                else if (DHEPossessionGenerator.customizedDHKeySize > 0) {
                    keySize = DHEPossessionGenerator.customizedDHKeySize;
                }
            }
            return new DHEPossession(keySize, context.sslContext.getSecureRandom());
        }
        
        static {
            final String property = GetPropertyAction.privilegedGetProperty("jdk.tls.ephemeralDHKeySize");
            if (property == null || property.length() == 0) {
                useLegacyEphemeralDHKeys = false;
                useSmartEphemeralDHKeys = false;
                customizedDHKeySize = -1;
            }
            else if ("matched".equals(property)) {
                useLegacyEphemeralDHKeys = false;
                useSmartEphemeralDHKeys = true;
                customizedDHKeySize = -1;
            }
            else if ("legacy".equals(property)) {
                useLegacyEphemeralDHKeys = true;
                useSmartEphemeralDHKeys = false;
                customizedDHKeySize = -1;
            }
            else {
                useLegacyEphemeralDHKeys = false;
                useSmartEphemeralDHKeys = false;
                try {
                    customizedDHKeySize = Integer.parseUnsignedInt(property);
                    if (DHEPossessionGenerator.customizedDHKeySize < 1024 || DHEPossessionGenerator.customizedDHKeySize > 8192 || (DHEPossessionGenerator.customizedDHKeySize & 0x3F) != 0x0) {
                        throw new IllegalArgumentException("Unsupported customized DH key size: " + DHEPossessionGenerator.customizedDHKeySize + ". The key size must be multiple of 64, and range from 1024 to 8192 (inclusive)");
                    }
                }
                catch (final NumberFormatException nfe) {
                    throw new IllegalArgumentException("Invalid system property jdk.tls.ephemeralDHKeySize");
                }
            }
        }
    }
    
    private static final class DHEKAGenerator implements SSLKeyAgreementGenerator
    {
        private static DHEKAGenerator instance;
        
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext context) throws IOException {
            DHEPossession dhePossession = null;
            DHECredentials dheCredentials = null;
            for (final SSLPossession poss : context.handshakePossessions) {
                if (!(poss instanceof DHEPossession)) {
                    continue;
                }
                final DHEPossession dhep = (DHEPossession)poss;
                for (final SSLCredentials cred : context.handshakeCredentials) {
                    if (!(cred instanceof DHECredentials)) {
                        continue;
                    }
                    final DHECredentials dhec = (DHECredentials)cred;
                    if (dhep.namedGroup != null && dhec.namedGroup != null) {
                        if (dhep.namedGroup.equals(dhec.namedGroup)) {
                            dheCredentials = (DHECredentials)cred;
                            break;
                        }
                        continue;
                    }
                    else {
                        final DHParameterSpec pps = dhep.publicKey.getParams();
                        final DHParameterSpec cps = dhec.popPublicKey.getParams();
                        if (pps.getP().equals(cps.getP()) && pps.getG().equals(cps.getG())) {
                            dheCredentials = (DHECredentials)cred;
                            break;
                        }
                        continue;
                    }
                }
                if (dheCredentials != null) {
                    dhePossession = (DHEPossession)poss;
                    break;
                }
            }
            if (dhePossession == null || dheCredentials == null) {
                throw context.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No sufficient DHE key agreement parameters negotiated");
            }
            return new DHEKAKeyDerivation(context, dhePossession.privateKey, dheCredentials.popPublicKey);
        }
        
        static {
            DHEKAGenerator.instance = new DHEKAGenerator();
        }
        
        private static final class DHEKAKeyDerivation implements SSLKeyDerivation
        {
            private final HandshakeContext context;
            private final PrivateKey localPrivateKey;
            private final PublicKey peerPublicKey;
            
            DHEKAKeyDerivation(final HandshakeContext context, final PrivateKey localPrivateKey, final PublicKey peerPublicKey) {
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
                    final KeyAgreement ka = JsseJce.getKeyAgreement("DiffieHellman");
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
                    final KeyAgreement ka = JsseJce.getKeyAgreement("DiffieHellman");
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
}
