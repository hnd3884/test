package sun.security.ssl;

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
        
        static DHECredentials valueOf(final SupportedGroupsExtension.NamedGroup namedGroup, final byte[] array) throws IOException, GeneralSecurityException {
            if (namedGroup.type != SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_FFDHE) {
                throw new RuntimeException("Credentials decoding:  Not FFDHE named group");
            }
            if (array == null || array.length == 0) {
                return null;
            }
            final DHParameterSpec dhParameterSpec = (DHParameterSpec)namedGroup.getParameterSpec();
            if (dhParameterSpec == null) {
                return null;
            }
            return new DHECredentials((DHPublicKey)JsseJce.getKeyFactory("DiffieHellman").generatePublic(new DHPublicKeySpec(new BigInteger(1, array), dhParameterSpec.getP(), dhParameterSpec.getG())), namedGroup);
        }
    }
    
    static final class DHEPossession implements SSLPossession
    {
        final PrivateKey privateKey;
        final DHPublicKey publicKey;
        final SupportedGroupsExtension.NamedGroup namedGroup;
        
        DHEPossession(final SupportedGroupsExtension.NamedGroup namedGroup, final SecureRandom secureRandom) {
            try {
                final KeyPairGenerator keyPairGenerator = JsseJce.getKeyPairGenerator("DiffieHellman");
                keyPairGenerator.initialize(namedGroup.getParameterSpec(), secureRandom);
                final KeyPair generateDHKeyPair = this.generateDHKeyPair(keyPairGenerator);
                if (generateDHKeyPair == null) {
                    throw new RuntimeException("Could not generate DH keypair");
                }
                this.privateKey = generateDHKeyPair.getPrivate();
                this.publicKey = (DHPublicKey)generateDHKeyPair.getPublic();
            }
            catch (final GeneralSecurityException ex) {
                throw new RuntimeException("Could not generate DH keypair", ex);
            }
            this.namedGroup = namedGroup;
        }
        
        DHEPossession(final int n, final SecureRandom secureRandom) {
            final DHParameterSpec dhParameterSpec = PredefinedDHParameterSpecs.definedParams.get(n);
            try {
                final KeyPairGenerator keyPairGenerator = JsseJce.getKeyPairGenerator("DiffieHellman");
                if (dhParameterSpec != null) {
                    keyPairGenerator.initialize(dhParameterSpec, secureRandom);
                }
                else {
                    keyPairGenerator.initialize(n, secureRandom);
                }
                final KeyPair generateDHKeyPair = this.generateDHKeyPair(keyPairGenerator);
                if (generateDHKeyPair == null) {
                    throw new RuntimeException("Could not generate DH keypair of " + n + " bits");
                }
                this.privateKey = generateDHKeyPair.getPrivate();
                this.publicKey = (DHPublicKey)generateDHKeyPair.getPublic();
            }
            catch (final GeneralSecurityException ex) {
                throw new RuntimeException("Could not generate DH keypair", ex);
            }
            this.namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(this.publicKey.getParams());
        }
        
        DHEPossession(final DHECredentials dheCredentials, final SecureRandom secureRandom) {
            try {
                final KeyPairGenerator keyPairGenerator = JsseJce.getKeyPairGenerator("DiffieHellman");
                keyPairGenerator.initialize(dheCredentials.popPublicKey.getParams(), secureRandom);
                final KeyPair generateDHKeyPair = this.generateDHKeyPair(keyPairGenerator);
                if (generateDHKeyPair == null) {
                    throw new RuntimeException("Could not generate DH keypair");
                }
                this.privateKey = generateDHKeyPair.getPrivate();
                this.publicKey = (DHPublicKey)generateDHKeyPair.getPublic();
            }
            catch (final GeneralSecurityException ex) {
                throw new RuntimeException("Could not generate DH keypair", ex);
            }
            this.namedGroup = dheCredentials.namedGroup;
        }
        
        private KeyPair generateDHKeyPair(final KeyPairGenerator keyPairGenerator) throws GeneralSecurityException {
            final boolean b = !KeyUtil.isOracleJCEProvider(keyPairGenerator.getProvider().getName());
            int n = 0;
            int i = 0;
            while (i <= 2) {
                final KeyPair generateKeyPair = keyPairGenerator.generateKeyPair();
                if (b) {
                    final DHPublicKeySpec dhPublicKeySpec = getDHPublicKeySpec(generateKeyPair.getPublic());
                    Label_0075: {
                        try {
                            KeyUtil.validate(dhPublicKeySpec);
                        }
                        catch (final InvalidKeyException ex) {
                            if (n != 0) {
                                throw ex;
                            }
                            n = 1;
                            break Label_0075;
                        }
                        return generateKeyPair;
                    }
                    ++i;
                    continue;
                }
                return generateKeyPair;
            }
            return null;
        }
        
        private static DHPublicKeySpec getDHPublicKeySpec(final PublicKey publicKey) {
            if (publicKey instanceof DHPublicKey) {
                final DHPublicKey dhPublicKey = (DHPublicKey)publicKey;
                final DHParameterSpec params = dhPublicKey.getParams();
                return new DHPublicKeySpec(dhPublicKey.getY(), params.getP(), params.getG());
            }
            try {
                return JsseJce.getKeyFactory("DiffieHellman").getKeySpec(publicKey, DHPublicKeySpec.class);
            }
            catch (final NoSuchAlgorithmException | InvalidKeySpecException ex) {
                throw new RuntimeException("Unable to get DHPublicKeySpec", (Throwable)ex);
            }
        }
        
        @Override
        public byte[] encode() {
            byte[] byteArray = Utilities.toByteArray(this.publicKey.getY());
            final int n = KeyUtil.getKeySize(this.publicKey) + 7 >>> 3;
            if (n > 0 && byteArray.length < n) {
                final byte[] array = new byte[n];
                System.arraycopy(byteArray, 0, array, n - byteArray.length, byteArray.length);
                byteArray = array;
            }
            return byteArray;
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
        public SSLPossession createPossession(final HandshakeContext handshakeContext) {
            if (!DHEPossessionGenerator.useLegacyEphemeralDHKeys && handshakeContext.clientRequestedNamedGroups != null && !handshakeContext.clientRequestedNamedGroups.isEmpty()) {
                final SupportedGroupsExtension.NamedGroup preferredGroup = SupportedGroupsExtension.SupportedGroups.getPreferredGroup(handshakeContext.negotiatedProtocol, handshakeContext.algorithmConstraints, SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_FFDHE, handshakeContext.clientRequestedNamedGroups);
                if (preferredGroup != null) {
                    return new DHEPossession(preferredGroup, handshakeContext.sslContext.getSecureRandom());
                }
            }
            int customizedDHKeySize = this.exportable ? 512 : 1024;
            if (!this.exportable) {
                if (DHEPossessionGenerator.useLegacyEphemeralDHKeys) {
                    customizedDHKeySize = 768;
                }
                else if (DHEPossessionGenerator.useSmartEphemeralDHKeys) {
                    Key popPrivateKey = null;
                    final ServerHandshakeContext serverHandshakeContext = (ServerHandshakeContext)handshakeContext;
                    if (serverHandshakeContext.interimAuthn instanceof X509Authentication.X509Possession) {
                        popPrivateKey = ((X509Authentication.X509Possession)serverHandshakeContext.interimAuthn).popPrivateKey;
                    }
                    if (popPrivateKey != null) {
                        customizedDHKeySize = ((KeyUtil.getKeySize(popPrivateKey) <= 1024) ? 1024 : 2048);
                    }
                }
                else if (DHEPossessionGenerator.customizedDHKeySize > 0) {
                    customizedDHKeySize = DHEPossessionGenerator.customizedDHKeySize;
                }
            }
            return new DHEPossession(customizedDHKeySize, handshakeContext.sslContext.getSecureRandom());
        }
        
        static {
            final String privilegedGetProperty = GetPropertyAction.privilegedGetProperty("jdk.tls.ephemeralDHKeySize");
            if (privilegedGetProperty == null || privilegedGetProperty.isEmpty()) {
                useLegacyEphemeralDHKeys = false;
                useSmartEphemeralDHKeys = false;
                customizedDHKeySize = -1;
            }
            else if ("matched".equals(privilegedGetProperty)) {
                useLegacyEphemeralDHKeys = false;
                useSmartEphemeralDHKeys = true;
                customizedDHKeySize = -1;
            }
            else if ("legacy".equals(privilegedGetProperty)) {
                useLegacyEphemeralDHKeys = true;
                useSmartEphemeralDHKeys = false;
                customizedDHKeySize = -1;
            }
            else {
                useLegacyEphemeralDHKeys = false;
                useSmartEphemeralDHKeys = false;
                try {
                    customizedDHKeySize = Integer.parseUnsignedInt(privilegedGetProperty);
                    if (DHEPossessionGenerator.customizedDHKeySize < 1024 || DHEPossessionGenerator.customizedDHKeySize > 8192 || (DHEPossessionGenerator.customizedDHKeySize & 0x3F) != 0x0) {
                        throw new IllegalArgumentException("Unsupported customized DH key size: " + DHEPossessionGenerator.customizedDHKeySize + ". The key size must be multiple of 64, and range from 1024 to 8192 (inclusive)");
                    }
                }
                catch (final NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid system property jdk.tls.ephemeralDHKeySize");
                }
            }
        }
    }
    
    private static final class DHEKAGenerator implements SSLKeyAgreementGenerator
    {
        private static DHEKAGenerator instance;
        
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext) throws IOException {
            DHEPossession dhePossession = null;
            DHECredentials dheCredentials = null;
            for (final SSLPossession sslPossession : handshakeContext.handshakePossessions) {
                if (!(sslPossession instanceof DHEPossession)) {
                    continue;
                }
                final DHEPossession dhePossession2 = (DHEPossession)sslPossession;
                for (final SSLCredentials sslCredentials : handshakeContext.handshakeCredentials) {
                    if (!(sslCredentials instanceof DHECredentials)) {
                        continue;
                    }
                    final DHECredentials dheCredentials2 = (DHECredentials)sslCredentials;
                    if (dhePossession2.namedGroup != null && dheCredentials2.namedGroup != null) {
                        if (dhePossession2.namedGroup.equals(dheCredentials2.namedGroup)) {
                            dheCredentials = (DHECredentials)sslCredentials;
                            break;
                        }
                        continue;
                    }
                    else {
                        final DHParameterSpec params = dhePossession2.publicKey.getParams();
                        final DHParameterSpec params2 = dheCredentials2.popPublicKey.getParams();
                        if (params.getP().equals(params2.getP()) && params.getG().equals(params2.getG())) {
                            dheCredentials = (DHECredentials)sslCredentials;
                            break;
                        }
                        continue;
                    }
                }
                if (dheCredentials != null) {
                    dhePossession = (DHEPossession)sslPossession;
                    break;
                }
            }
            if (dhePossession == null || dheCredentials == null) {
                throw handshakeContext.conContext.fatal(Alert.HANDSHAKE_FAILURE, "No sufficient DHE key agreement parameters negotiated");
            }
            return new DHEKAKeyDerivation(handshakeContext, dhePossession.privateKey, dheCredentials.popPublicKey);
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
            public SecretKey deriveKey(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws IOException {
                if (!this.context.negotiatedProtocol.useTLS13PlusSpec()) {
                    return this.t12DeriveKey(s, algorithmParameterSpec);
                }
                return this.t13DeriveKey(s, algorithmParameterSpec);
            }
            
            private SecretKey t12DeriveKey(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws IOException {
                try {
                    final KeyAgreement keyAgreement = JsseJce.getKeyAgreement("DiffieHellman");
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
                    final KeyAgreement keyAgreement = JsseJce.getKeyAgreement("DiffieHellman");
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
}
