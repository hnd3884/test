package sun.security.ssl;

import java.security.spec.PSSParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.Collections;
import java.util.EnumSet;
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
import java.util.LinkedList;
import java.security.AlgorithmParameters;
import java.security.AlgorithmConstraints;
import java.security.Security;
import java.util.Arrays;
import java.security.CryptoPrimitive;
import java.util.Set;
import java.util.List;
import java.security.spec.AlgorithmParameterSpec;

enum SignatureScheme
{
    ED25519(2055, "ed25519", "ed25519", "ed25519", ProtocolVersion.PROTOCOLS_OF_13), 
    ED448(2056, "ed448", "ed448", "ed448", ProtocolVersion.PROTOCOLS_OF_13), 
    ECDSA_SECP256R1_SHA256(1027, "ecdsa_secp256r1_sha256", "SHA256withECDSA", "EC", SupportedGroupsExtension.NamedGroup.SECP256_R1, ProtocolVersion.PROTOCOLS_TO_13), 
    ECDSA_SECP384R1_SHA384(1283, "ecdsa_secp384r1_sha384", "SHA384withECDSA", "EC", SupportedGroupsExtension.NamedGroup.SECP384_R1, ProtocolVersion.PROTOCOLS_TO_13), 
    ECDSA_SECP521R1_SHA512(1539, "ecdsa_secp521r1_sha512", "SHA512withECDSA", "EC", SupportedGroupsExtension.NamedGroup.SECP521_R1, ProtocolVersion.PROTOCOLS_TO_13), 
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
    private final AlgorithmParameterSpec signAlgParameter;
    private final SupportedGroupsExtension.NamedGroup namedGroup;
    final int minimalKeySize;
    final List<ProtocolVersion> supportedProtocols;
    final List<ProtocolVersion> handshakeSupportedProtocols;
    final boolean isAvailable;
    private static final String[] hashAlgorithms;
    private static final String[] signatureAlgorithms;
    private static final Set<CryptoPrimitive> SIGNATURE_PRIMITIVE_SET;
    
    private SignatureScheme(final int n2, final String s2, final String s3, final String s4, final ProtocolVersion[] array) {
        this(n2, s2, s3, s4, -1, array);
    }
    
    private SignatureScheme(final int n2, final String s2, final String s3, final String s4, final int n3, final ProtocolVersion[] array) {
        this(n2, s2, s3, s4, null, n3, array);
    }
    
    private SignatureScheme(final int n2, final String s2, final String s3, final String s4, final SigAlgParamSpec sigAlgParamSpec, final int n3, final ProtocolVersion[] array) {
        this(n2, s2, s3, s4, sigAlgParamSpec, null, n3, array, array);
    }
    
    private SignatureScheme(final int n2, final String s2, final String s3, final String s4, final SupportedGroupsExtension.NamedGroup namedGroup, final ProtocolVersion[] array) {
        this(n2, s2, s3, s4, null, namedGroup, -1, array, array);
    }
    
    private SignatureScheme(final int id, final String name, final String algorithm, final String keyAlgorithm, final SigAlgParamSpec sigAlgParamSpec, final SupportedGroupsExtension.NamedGroup namedGroup, final int minimalKeySize, final ProtocolVersion[] array, final ProtocolVersion[] array2) {
        this.id = id;
        this.name = name;
        this.algorithm = algorithm;
        this.keyAlgorithm = keyAlgorithm;
        this.signAlgParameter = ((sigAlgParamSpec != null) ? sigAlgParamSpec.parameterSpec : null);
        this.namedGroup = namedGroup;
        this.minimalKeySize = minimalKeySize;
        this.supportedProtocols = Arrays.asList(array);
        this.handshakeSupportedProtocols = Arrays.asList(array2);
        boolean isAvailable = true;
        if ("EC".equals(keyAlgorithm)) {
            isAvailable = JsseJce.isEcAvailable();
        }
        if (isAvailable) {
            if (sigAlgParamSpec != null) {
                isAvailable = sigAlgParamSpec.isAvailable;
            }
            else {
                try {
                    JsseJce.getSignature(algorithm);
                }
                catch (final Exception ex) {
                    isAvailable = false;
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.warning("Signature algorithm, " + algorithm + ", is not supported by the underlying providers", new Object[0]);
                    }
                }
            }
        }
        if (isAvailable && (id >> 8 & 0xFF) == 0x3 && Security.getProvider("SunMSCAPI") != null) {
            isAvailable = false;
        }
        this.isAvailable = isAvailable;
    }
    
    static SignatureScheme valueOf(final int n) {
        for (final SignatureScheme signatureScheme : values()) {
            if (signatureScheme.id == n) {
                return signatureScheme;
            }
        }
        return null;
    }
    
    static String nameOf(final int n) {
        for (final SignatureScheme signatureScheme : values()) {
            if (signatureScheme.id == n) {
                return signatureScheme.name;
            }
        }
        final int n2 = n >> 8 & 0xFF;
        final int n3 = n & 0xFF;
        return ((n3 >= SignatureScheme.signatureAlgorithms.length) ? ("UNDEFINED-SIGNATURE(" + n3 + ")") : SignatureScheme.signatureAlgorithms[n3]) + "_" + ((n2 >= SignatureScheme.hashAlgorithms.length) ? ("UNDEFINED-HASH(" + n2 + ")") : SignatureScheme.hashAlgorithms[n2]);
    }
    
    static SignatureScheme nameOf(final String s) {
        for (final SignatureScheme signatureScheme : values()) {
            if (signatureScheme.name.equalsIgnoreCase(s)) {
                return signatureScheme;
            }
        }
        return null;
    }
    
    static int sizeInRecord() {
        return 2;
    }
    
    private boolean isPermitted(final AlgorithmConstraints algorithmConstraints) {
        return algorithmConstraints.permits(SignatureScheme.SIGNATURE_PRIMITIVE_SET, this.name, null) && algorithmConstraints.permits(SignatureScheme.SIGNATURE_PRIMITIVE_SET, this.keyAlgorithm, null) && algorithmConstraints.permits(SignatureScheme.SIGNATURE_PRIMITIVE_SET, this.algorithm, null) && (this.namedGroup == null || this.namedGroup.isPermitted(algorithmConstraints));
    }
    
    static List<SignatureScheme> getSupportedAlgorithms(final SSLConfiguration sslConfiguration, final AlgorithmConstraints algorithmConstraints, final List<ProtocolVersion> list) {
        final LinkedList list2 = new LinkedList();
        for (final SignatureScheme signatureScheme : values()) {
            if (!signatureScheme.isAvailable || (!sslConfiguration.signatureSchemes.isEmpty() && !sslConfiguration.signatureSchemes.contains(signatureScheme))) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Ignore unsupported signature scheme: " + signatureScheme.name, new Object[0]);
                }
            }
            else {
                boolean b = false;
                final Iterator<ProtocolVersion> iterator = list.iterator();
                while (iterator.hasNext()) {
                    if (signatureScheme.supportedProtocols.contains(iterator.next())) {
                        b = true;
                        break;
                    }
                }
                if (b) {
                    if (signatureScheme.isPermitted(algorithmConstraints)) {
                        list2.add(signatureScheme);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                        SSLLogger.finest("Ignore disabled signature scheme: " + signatureScheme.name, new Object[0]);
                    }
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                    SSLLogger.finest("Ignore inactive signature scheme: " + signatureScheme.name, new Object[0]);
                }
            }
        }
        return list2;
    }
    
    static List<SignatureScheme> getSupportedAlgorithms(final SSLConfiguration sslConfiguration, final AlgorithmConstraints algorithmConstraints, final ProtocolVersion protocolVersion, final int[] array) {
        final LinkedList list = new LinkedList();
        for (final int n : array) {
            final SignatureScheme value = valueOf(n);
            if (value == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Unsupported signature scheme: " + nameOf(n), new Object[0]);
                }
            }
            else if (value.isAvailable && value.supportedProtocols.contains(protocolVersion) && (sslConfiguration.signatureSchemes.isEmpty() || sslConfiguration.signatureSchemes.contains(value)) && value.isPermitted(algorithmConstraints)) {
                list.add(value);
            }
            else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.warning("Unsupported signature scheme: " + value.name, new Object[0]);
            }
        }
        return list;
    }
    
    static SignatureScheme getPreferableAlgorithm(final AlgorithmConstraints algorithmConstraints, final List<SignatureScheme> list, final SignatureScheme signatureScheme, final ProtocolVersion protocolVersion) {
        for (final SignatureScheme signatureScheme2 : list) {
            if (signatureScheme2.isAvailable && signatureScheme2.handshakeSupportedProtocols.contains(protocolVersion) && signatureScheme.keyAlgorithm.equalsIgnoreCase(signatureScheme2.keyAlgorithm) && signatureScheme2.isPermitted(algorithmConstraints)) {
                return signatureScheme2;
            }
        }
        return null;
    }
    
    static Map.Entry<SignatureScheme, Signature> getSignerOfPreferableAlgorithm(final AlgorithmConstraints algorithmConstraints, final List<SignatureScheme> list, final X509Authentication.X509Possession x509Possession, final ProtocolVersion protocolVersion) {
        final PrivateKey popPrivateKey = x509Possession.popPrivateKey;
        final String algorithm = popPrivateKey.getAlgorithm();
        int keySize;
        if (algorithm.equalsIgnoreCase("RSA") || algorithm.equalsIgnoreCase("RSASSA-PSS")) {
            keySize = KeyUtil.getKeySize(popPrivateKey);
        }
        else {
            keySize = Integer.MAX_VALUE;
        }
        for (final SignatureScheme signatureScheme : list) {
            if (signatureScheme.isAvailable && keySize >= signatureScheme.minimalKeySize && signatureScheme.handshakeSupportedProtocols.contains(protocolVersion) && algorithm.equalsIgnoreCase(signatureScheme.keyAlgorithm) && signatureScheme.isPermitted(algorithmConstraints)) {
                if (signatureScheme.namedGroup != null && signatureScheme.namedGroup.type == SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_ECDHE) {
                    final ECParameterSpec ecParameterSpec = x509Possession.getECParameterSpec();
                    if (ecParameterSpec != null && signatureScheme.namedGroup == SupportedGroupsExtension.NamedGroup.valueOf(ecParameterSpec)) {
                        final Signature signer = signatureScheme.getSigner(popPrivateKey);
                        if (signer != null) {
                            return new AbstractMap.SimpleImmutableEntry<SignatureScheme, Signature>(signatureScheme, signer);
                        }
                    }
                    if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake,verbose")) {
                        continue;
                    }
                    SSLLogger.finest("Ignore the signature algorithm (" + signatureScheme + "), unsupported EC parameter spec: " + ecParameterSpec, new Object[0]);
                }
                else if ("EC".equals(signatureScheme.keyAlgorithm)) {
                    final ECParameterSpec ecParameterSpec2 = x509Possession.getECParameterSpec();
                    if (ecParameterSpec2 != null) {
                        final SupportedGroupsExtension.NamedGroup value = SupportedGroupsExtension.NamedGroup.valueOf(ecParameterSpec2);
                        if (value != null && SupportedGroupsExtension.SupportedGroups.isSupported(value)) {
                            final Signature signer2 = signatureScheme.getSigner(popPrivateKey);
                            if (signer2 != null) {
                                return new AbstractMap.SimpleImmutableEntry<SignatureScheme, Signature>(signatureScheme, signer2);
                            }
                        }
                    }
                    if (!SSLLogger.isOn || !SSLLogger.isOn("ssl,handshake,verbose")) {
                        continue;
                    }
                    SSLLogger.finest("Ignore the legacy signature algorithm (" + signatureScheme + "), unsupported EC parameter spec: " + ecParameterSpec2, new Object[0]);
                }
                else {
                    final Signature signer3 = signatureScheme.getSigner(popPrivateKey);
                    if (signer3 != null) {
                        return new AbstractMap.SimpleImmutableEntry<SignatureScheme, Signature>(signatureScheme, signer3);
                    }
                    continue;
                }
            }
        }
        return null;
    }
    
    static String[] getAlgorithmNames(final Collection<SignatureScheme> collection) {
        if (collection != null) {
            final ArrayList list = new ArrayList(collection.size());
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                list.add(((SignatureScheme)iterator.next()).algorithm);
            }
            return list.toArray(new String[0]);
        }
        return new String[0];
    }
    
    Signature getVerifier(final PublicKey publicKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        if (!this.isAvailable) {
            return null;
        }
        final Signature instance = Signature.getInstance(this.algorithm);
        SignatureUtil.initVerifyWithParam(instance, publicKey, this.signAlgParameter);
        return instance;
    }
    
    private Signature getSigner(final PrivateKey privateKey) {
        if (!this.isAvailable) {
            return null;
        }
        try {
            final Signature instance = Signature.getInstance(this.algorithm);
            SignatureUtil.initSignWithParam(instance, privateKey, this.signAlgParameter, (SecureRandom)null);
            return instance;
        }
        catch (final NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake,verbose")) {
                SSLLogger.finest("Ignore unsupported signature algorithm (" + this.name + ")", ex);
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
        final boolean isAvailable;
        
        private SigAlgParamSpec(final String s2, final int n2) {
            final PSSParameterSpec parameter = new PSSParameterSpec(s2, "MGF1", new MGF1ParameterSpec(s2), n2, 1);
            boolean isAvailable = true;
            try {
                JsseJce.getSignature("RSASSA-PSS").setParameter(parameter);
            }
            catch (final InvalidAlgorithmParameterException | NoSuchAlgorithmException ex) {
                isAvailable = false;
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("RSASSA-PSS signature with " + s2 + " is not supported by the underlying providers", ex);
                }
            }
            this.isAvailable = isAvailable;
            this.parameterSpec = (isAvailable ? parameter : null);
        }
        
        AlgorithmParameterSpec getParameterSpec() {
            return this.parameterSpec;
        }
    }
}
