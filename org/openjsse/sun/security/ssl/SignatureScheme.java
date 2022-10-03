package org.openjsse.sun.security.ssl;

import java.security.spec.PSSParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Collections;
import java.util.EnumSet;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import sun.security.util.SignatureUtil;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.security.spec.ECParameterSpec;
import java.security.PrivateKey;
import java.util.AbstractMap;
import java.security.Key;
import sun.security.util.KeyUtil;
import java.security.Signature;
import java.util.Map;
import java.util.Iterator;
import java.security.AlgorithmParameters;
import java.util.LinkedList;
import java.security.AlgorithmConstraints;
import java.security.Security;
import java.util.Arrays;
import java.security.CryptoPrimitive;
import java.util.Set;
import java.util.List;

enum SignatureScheme
{
    ED25519(2055, "ed25519", "ed25519", "ed25519", ProtocolVersion.PROTOCOLS_OF_13), 
    ED448(2056, "ed448", "ed448", "ed448", ProtocolVersion.PROTOCOLS_OF_13), 
    ECDSA_SECP256R1_SHA256(1027, "ecdsa_secp256r1_sha256", "SHA256withECDSA", "EC", SupportedGroupsExtension.NamedGroup.SECP256_R1, ProtocolVersion.PROTOCOLS_TO_13), 
    ECDSA_SECP384R1_SHA384(1283, "ecdsa_secp384r1_sha384", "SHA384withECDSA", "EC", SupportedGroupsExtension.NamedGroup.SECP384_R1, ProtocolVersion.PROTOCOLS_TO_13), 
    ECDSA_SECP512R1_SHA512(1539, "ecdsa_secp521r1_sha512", "SHA512withECDSA", "EC", SupportedGroupsExtension.NamedGroup.SECP521_R1, ProtocolVersion.PROTOCOLS_TO_13), 
    RSA_PSS_RSAE_SHA256(2052, "rsa_pss_rsae_sha256", "RSASSA-PSS", "RSA", SigAlgParamSpec.RSA_PSS_SHA256, 528, ProtocolVersion.PROTOCOLS_12_13), 
    RSA_PSS_RSAE_SHA384(2053, "rsa_pss_rsae_sha384", "RSASSA-PSS", "RSA", SigAlgParamSpec.RSA_PSS_SHA384, 784, ProtocolVersion.PROTOCOLS_12_13), 
    RSA_PSS_RSAE_SHA512(2054, "rsa_pss_rsae_sha512", "RSASSA-PSS", "RSA", SigAlgParamSpec.RSA_PSS_SHA512, 1040, ProtocolVersion.PROTOCOLS_12_13), 
    RSA_PSS_PSS_SHA256(2057, "rsa_pss_pss_sha256", "RSASSA-PSS", "RSASSA-PSS", SigAlgParamSpec.RSA_PSS_SHA256, 528, ProtocolVersion.PROTOCOLS_12_13), 
    RSA_PSS_PSS_SHA384(2058, "rsa_pss_pss_sha384", "RSASSA-PSS", "RSASSA-PSS", SigAlgParamSpec.RSA_PSS_SHA384, 784, ProtocolVersion.PROTOCOLS_12_13), 
    RSA_PSS_PSS_SHA512(2059, "rsa_pss_pss_sha512", "RSASSA-PSS", "RSASSA-PSS", SigAlgParamSpec.RSA_PSS_SHA512, 1040, ProtocolVersion.PROTOCOLS_12_13), 
    RSA_PKCS1_SHA256(1025, "rsa_pkcs1_sha256", "SHA256withRSA", "RSA", (SigAlgParamSpec)null, (SupportedGroupsExtension.NamedGroup)null, 511, ProtocolVersion.PROTOCOLS_TO_13, ProtocolVersion.PROTOCOLS_TO_12), 
    RSA_PKCS1_SHA384(1281, "rsa_pkcs1_sha384", "SHA384withRSA", "RSA", (SigAlgParamSpec)null, (SupportedGroupsExtension.NamedGroup)null, 768, ProtocolVersion.PROTOCOLS_TO_13, ProtocolVersion.PROTOCOLS_TO_12), 
    RSA_PKCS1_SHA512(1537, "rsa_pkcs1_sha512", "SHA512withRSA", "RSA", (SigAlgParamSpec)null, (SupportedGroupsExtension.NamedGroup)null, 768, ProtocolVersion.PROTOCOLS_TO_13, ProtocolVersion.PROTOCOLS_TO_12), 
    DSA_SHA256(1026, "dsa_sha256", "SHA256withDSA", "DSA", ProtocolVersion.PROTOCOLS_TO_12), 
    ECDSA_SHA224(771, "ecdsa_sha224", "SHA224withECDSA", "EC", ProtocolVersion.PROTOCOLS_TO_12), 
    RSA_SHA224(769, "rsa_sha224", "SHA224withRSA", "RSA", 511, ProtocolVersion.PROTOCOLS_TO_12), 
    DSA_SHA224(770, "dsa_sha224", "SHA224withDSA", "DSA", ProtocolVersion.PROTOCOLS_TO_12), 
    ECDSA_SHA1(515, "ecdsa_sha1", "SHA1withECDSA", "EC", ProtocolVersion.PROTOCOLS_TO_13), 
    RSA_PKCS1_SHA1(513, "rsa_pkcs1_sha1", "SHA1withRSA", "RSA", (SigAlgParamSpec)null, (SupportedGroupsExtension.NamedGroup)null, 511, ProtocolVersion.PROTOCOLS_TO_13, ProtocolVersion.PROTOCOLS_TO_12), 
    DSA_SHA1(514, "dsa_sha1", "SHA1withDSA", "DSA", ProtocolVersion.PROTOCOLS_TO_12), 
    RSA_MD5(257, "rsa_md5", "MD5withRSA", "RSA", 511, ProtocolVersion.PROTOCOLS_TO_12);
    
    final int id;
    final String name;
    private final String algorithm;
    final String keyAlgorithm;
    private final SigAlgParamSpec signAlgParams;
    private final SupportedGroupsExtension.NamedGroup namedGroup;
    final int minimalKeySize;
    final List<ProtocolVersion> supportedProtocols;
    final List<ProtocolVersion> handshakeSupportedProtocols;
    final boolean isAvailable;
    private static final String[] hashAlgorithms;
    private static final String[] signatureAlgorithms;
    private static final Set<CryptoPrimitive> SIGNATURE_PRIMITIVE_SET;
    
    private SignatureScheme(final int id, final String name, final String algorithm, final String keyAlgorithm, final ProtocolVersion[] supportedProtocols) {
        this(id, name, algorithm, keyAlgorithm, -1, supportedProtocols);
    }
    
    private SignatureScheme(final int id, final String name, final String algorithm, final String keyAlgorithm, final int minimalKeySize, final ProtocolVersion[] supportedProtocols) {
        this(id, name, algorithm, keyAlgorithm, null, minimalKeySize, supportedProtocols);
    }
    
    private SignatureScheme(final int id, final String name, final String algorithm, final String keyAlgorithm, final SigAlgParamSpec signAlgParamSpec, final int minimalKeySize, final ProtocolVersion[] supportedProtocols) {
        this(id, name, algorithm, keyAlgorithm, signAlgParamSpec, null, minimalKeySize, supportedProtocols, supportedProtocols);
    }
    
    private SignatureScheme(final int id, final String name, final String algorithm, final String keyAlgorithm, final SupportedGroupsExtension.NamedGroup namedGroup, final ProtocolVersion[] supportedProtocols) {
        this(id, name, algorithm, keyAlgorithm, null, namedGroup, -1, supportedProtocols, supportedProtocols);
    }
    
    private SignatureScheme(final int id, final String name, final String algorithm, final String keyAlgorithm, final SigAlgParamSpec signAlgParamSpec, final SupportedGroupsExtension.NamedGroup namedGroup, final int minimalKeySize, final ProtocolVersion[] supportedProtocols, final ProtocolVersion[] handshakeSupportedProtocols) {
        this.id = id;
        this.name = name;
        this.algorithm = algorithm;
        this.keyAlgorithm = keyAlgorithm;
        this.signAlgParams = signAlgParamSpec;
        this.namedGroup = namedGroup;
        this.minimalKeySize = minimalKeySize;
        this.supportedProtocols = Arrays.asList(supportedProtocols);
        this.handshakeSupportedProtocols = Arrays.asList(handshakeSupportedProtocols);
        boolean mediator = true;
        if ("EC".equals(keyAlgorithm)) {
            mediator = JsseJce.isEcAvailable();
        }
        if (mediator) {
            if (signAlgParamSpec != null) {
                mediator = signAlgParamSpec.isAvailable;
            }
            else {
                try {
                    JsseJce.getSignature(algorithm);
                }
                catch (final Exception e) {
                    mediator = false;
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.warning("Signature algorithm, " + algorithm + ", is not supported by the underlying providers", new Object[0]);
                    }
                }
            }
        }
        if (mediator && (id >> 8 & 0xFF) == 0x3 && Security.getProvider("SunMSCAPI") != null) {
            mediator = false;
        }
        this.isAvailable = mediator;
    }
    
    static SignatureScheme valueOf(final int id) {
        for (final SignatureScheme ss : values()) {
            if (ss.id == id) {
                return ss;
            }
        }
        return null;
    }
    
    static String nameOf(final int id) {
        for (final SignatureScheme ss : values()) {
            if (ss.id == id) {
                return ss.name;
            }
        }
        final int hashId = id >> 8 & 0xFF;
        final int signId = id & 0xFF;
        final String hashName = (hashId >= SignatureScheme.hashAlgorithms.length) ? ("UNDEFINED-HASH(" + hashId + ")") : SignatureScheme.hashAlgorithms[hashId];
        final String signName = (signId >= SignatureScheme.signatureAlgorithms.length) ? ("UNDEFINED-SIGNATURE(" + signId + ")") : SignatureScheme.signatureAlgorithms[signId];
        return signName + "_" + hashName;
    }
    
    static SignatureScheme nameOf(final String signatureSchemeName) {
        for (final SignatureScheme ss : values()) {
            if (ss.name.equalsIgnoreCase(signatureSchemeName)) {
                return ss;
            }
        }
        return null;
    }
    
    static int sizeInRecord() {
        return 2;
    }
    
    static List<SignatureScheme> getSupportedAlgorithms(final SSLConfiguration config, final AlgorithmConstraints constraints, final List<ProtocolVersion> activeProtocols) {
        final List<SignatureScheme> supported = new LinkedList<SignatureScheme>();
        for (final SignatureScheme ss : values()) {
            if (!ss.isAvailable || (!config.signatureSchemes.isEmpty() && !config.signatureSchemes.contains(ss))) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Ignore unsupported signature scheme: " + ss.name, new Object[0]);
                }
            }
            else {
                boolean isMatch = false;
                for (final ProtocolVersion pv : activeProtocols) {
                    if (ss.supportedProtocols.contains(pv)) {
                        isMatch = true;
                        break;
                    }
                }
                if (isMatch) {
                    if (constraints.permits(SignatureScheme.SIGNATURE_PRIMITIVE_SET, ss.algorithm, null)) {
                        supported.add(ss);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("Ignore disabled signature scheme: " + ss.name, new Object[0]);
                    }
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Ignore inactive signature scheme: " + ss.name, new Object[0]);
                }
            }
        }
        return supported;
    }
    
    static List<SignatureScheme> getSupportedAlgorithms(final SSLConfiguration config, final AlgorithmConstraints constraints, final ProtocolVersion protocolVersion, final int[] algorithmIds) {
        final List<SignatureScheme> supported = new LinkedList<SignatureScheme>();
        for (final int ssid : algorithmIds) {
            final SignatureScheme ss = valueOf(ssid);
            if (ss == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Unsupported signature scheme: " + nameOf(ssid), new Object[0]);
                }
            }
            else if (ss.isAvailable && ss.supportedProtocols.contains(protocolVersion) && (config.signatureSchemes.isEmpty() || config.signatureSchemes.contains(ss)) && constraints.permits(SignatureScheme.SIGNATURE_PRIMITIVE_SET, ss.algorithm, null)) {
                supported.add(ss);
            }
            else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.warning("Unsupported signature scheme: " + ss.name, new Object[0]);
            }
        }
        return supported;
    }
    
    static SignatureScheme getPreferableAlgorithm(final List<SignatureScheme> schemes, final SignatureScheme certScheme, final ProtocolVersion version) {
        for (final SignatureScheme ss : schemes) {
            if (ss.isAvailable && ss.handshakeSupportedProtocols.contains(version) && certScheme.keyAlgorithm.equalsIgnoreCase(ss.keyAlgorithm)) {
                return ss;
            }
        }
        return null;
    }
    
    static Map.Entry<SignatureScheme, Signature> getSignerOfPreferableAlgorithm(final List<SignatureScheme> schemes, final X509Authentication.X509Possession x509Possession, final ProtocolVersion version) {
        final PrivateKey signingKey = x509Possession.popPrivateKey;
        final String keyAlgorithm = signingKey.getAlgorithm();
        int keySize;
        if (keyAlgorithm.equalsIgnoreCase("RSA") || keyAlgorithm.equalsIgnoreCase("RSASSA-PSS")) {
            keySize = KeyUtil.getKeySize(signingKey);
        }
        else {
            keySize = Integer.MAX_VALUE;
        }
        for (final SignatureScheme ss : schemes) {
            if (ss.isAvailable && keySize >= ss.minimalKeySize && ss.handshakeSupportedProtocols.contains(version) && keyAlgorithm.equalsIgnoreCase(ss.keyAlgorithm)) {
                if (ss.namedGroup != null && ss.namedGroup.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE) {
                    final ECParameterSpec params = x509Possession.getECParameterSpec();
                    if (params == null || ss.namedGroup != SupportedGroupsExtension.NamedGroup.valueOf(params)) {
                        continue;
                    }
                    final Signature signer = ss.getSigner(signingKey);
                    if (signer != null) {
                        return new AbstractMap.SimpleImmutableEntry<SignatureScheme, Signature>(ss, signer);
                    }
                    continue;
                }
                else {
                    final Signature signer2 = ss.getSigner(signingKey);
                    if (signer2 != null) {
                        return new AbstractMap.SimpleImmutableEntry<SignatureScheme, Signature>(ss, signer2);
                    }
                    continue;
                }
            }
        }
        return null;
    }
    
    static String[] getAlgorithmNames(final Collection<SignatureScheme> schemes) {
        if (schemes != null) {
            final ArrayList<String> names = new ArrayList<String>(schemes.size());
            for (final SignatureScheme scheme : schemes) {
                names.add(scheme.algorithm);
            }
            return names.toArray(new String[0]);
        }
        return new String[0];
    }
    
    Signature getVerifier(final PublicKey publicKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        if (!this.isAvailable) {
            return null;
        }
        final Signature verifier = Signature.getInstance(this.algorithm);
        SignatureUtil.initVerifyWithParam(verifier, publicKey, (this.signAlgParams != null) ? this.signAlgParams.parameterSpec : null);
        return verifier;
    }
    
    private Signature getSigner(final PrivateKey privateKey) {
        if (!this.isAvailable) {
            return null;
        }
        try {
            final Signature signer = Signature.getInstance(this.algorithm);
            SignatureUtil.initSignWithParam(signer, privateKey, (this.signAlgParams != null) ? this.signAlgParams.parameterSpec : null, (SecureRandom)null);
            return signer;
        }
        catch (final NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException nsae) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                SSLLogger.finest("Ignore unsupported signature algorithm (" + this.name + ")", nsae);
            }
            return null;
        }
    }
    
    static {
        hashAlgorithms = new String[] { "none", "md5", "sha1", "sha224", "sha256", "sha384", "sha512" };
        signatureAlgorithms = new String[] { "anonymous", "rsa", "dsa", "ecdsa" };
        SIGNATURE_PRIMITIVE_SET = Collections.unmodifiableSet((Set<? extends CryptoPrimitive>)EnumSet.of(CryptoPrimitive.SIGNATURE));
    }
    
    enum SigAlgParamSpec
    {
        RSA_PSS_SHA256("SHA-256", 32), 
        RSA_PSS_SHA384("SHA-384", 48), 
        RSA_PSS_SHA512("SHA-512", 64);
        
        private final AlgorithmParameterSpec parameterSpec;
        private final boolean isAvailable;
        
        private SigAlgParamSpec(final String hash, final int saltLength) {
            final PSSParameterSpec pssParamSpec = new PSSParameterSpec(hash, "MGF1", new MGF1ParameterSpec(hash), saltLength, 1);
            boolean mediator = true;
            try {
                final Signature signer = Signature.getInstance("RSASSA-PSS");
                signer.setParameter(pssParamSpec);
            }
            catch (final InvalidAlgorithmParameterException | NoSuchAlgorithmException | RuntimeException exp) {
                mediator = false;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("RSASSA-PSS signature with " + hash + " is not supported by the underlying providers", exp);
                }
            }
            this.isAvailable = mediator;
            this.parameterSpec = (mediator ? pssParamSpec : null);
        }
        
        AlgorithmParameterSpec getParameterSpec() {
            return this.parameterSpec;
        }
    }
}
