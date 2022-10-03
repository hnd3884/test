package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.Security;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Collections;
import java.util.EnumSet;
import java.security.Key;
import sun.security.util.KeyUtil;
import java.security.PrivateKey;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.security.AlgorithmParameters;
import java.security.AlgorithmConstraints;
import java.util.Map;
import java.security.CryptoPrimitive;
import java.util.Set;

final class SignatureAndHashAlgorithm
{
    static final int SUPPORTED_ALG_PRIORITY_MAX_NUM = 240;
    private static final Set<CryptoPrimitive> SIGNATURE_PRIMITIVE_SET;
    private static final Map<Integer, SignatureAndHashAlgorithm> supportedMap;
    private static final Map<Integer, SignatureAndHashAlgorithm> priorityMap;
    private final String name;
    private final String curve;
    private final SignatureAlgorithm signature;
    private HashAlgorithm hash;
    private int id;
    private String algorithm;
    private int priority;
    
    private SignatureAndHashAlgorithm(final String name, final HashAlgorithm hash, final SignatureAlgorithm signature, final String algorithm, final String curve, final int priority) {
        this.name = name;
        this.hash = hash;
        this.signature = signature;
        this.algorithm = algorithm;
        this.id = ((hash.value & 0xFF) << 8 | (signature.value & 0xFF));
        this.curve = curve;
        this.priority = priority;
    }
    
    private SignatureAndHashAlgorithm(final String name, final HashAlgorithm hash, final SignatureAlgorithm signature, final String algorithm, final int priority) {
        this(name, hash, signature, algorithm, null, priority);
    }
    
    private SignatureAndHashAlgorithm(final String algorithm, final int id, final int sequence) {
        this.name = "unknown";
        this.hash = HashAlgorithm.valueOf(id >> 8 & 0xFF);
        this.signature = SignatureAlgorithm.valueOf(id & 0xFF);
        this.algorithm = algorithm;
        this.id = id;
        this.curve = null;
        this.priority = 240 + sequence + 1;
    }
    
    static SignatureAndHashAlgorithm valueOf(int hash, int signature, final int sequence) {
        hash &= 0xFF;
        signature &= 0xFF;
        final int id = hash << 8 | signature;
        SignatureAndHashAlgorithm signAlg = SignatureAndHashAlgorithm.supportedMap.get(id);
        if (signAlg == null) {
            signAlg = new SignatureAndHashAlgorithm("Unknown (hash:0x" + Integer.toString(hash, 16) + ", signature:0x" + Integer.toString(signature, 16) + ")", id, sequence);
        }
        return signAlg;
    }
    
    int getHashValue() {
        return this.id >> 8 & 0xFF;
    }
    
    int getSignatureValue() {
        return this.id & 0xFF;
    }
    
    String getAlgorithmName() {
        return this.algorithm;
    }
    
    static int sizeInRecord() {
        return 2;
    }
    
    private boolean isPermitted(final AlgorithmConstraints constraints) {
        return constraints.permits(SignatureAndHashAlgorithm.SIGNATURE_PRIMITIVE_SET, this.name, null) && constraints.permits(SignatureAndHashAlgorithm.SIGNATURE_PRIMITIVE_SET, this.signature.name, null) && constraints.permits(SignatureAndHashAlgorithm.SIGNATURE_PRIMITIVE_SET, this.algorithm, null) && (this.curve == null || EllipticCurvesExtension.isPermitted(this.curve, constraints));
    }
    
    static Collection<SignatureAndHashAlgorithm> getSupportedAlgorithms(final AlgorithmConstraints constraints) {
        final Collection<SignatureAndHashAlgorithm> supported = new ArrayList<SignatureAndHashAlgorithm>();
        for (final SignatureAndHashAlgorithm sigAlg : SignatureAndHashAlgorithm.priorityMap.values()) {
            if (sigAlg.priority <= 240 && sigAlg.isPermitted(constraints)) {
                supported.add(sigAlg);
            }
        }
        return supported;
    }
    
    static Collection<SignatureAndHashAlgorithm> getSupportedAlgorithms(final AlgorithmConstraints constraints, final Collection<SignatureAndHashAlgorithm> algorithms) {
        final Collection<SignatureAndHashAlgorithm> supported = new ArrayList<SignatureAndHashAlgorithm>();
        for (final SignatureAndHashAlgorithm sigAlg : algorithms) {
            if (sigAlg.priority <= 240 && sigAlg.isPermitted(constraints)) {
                supported.add(sigAlg);
            }
        }
        return supported;
    }
    
    static String[] getAlgorithmNames(final Collection<SignatureAndHashAlgorithm> algorithms) {
        final ArrayList<String> algorithmNames = new ArrayList<String>();
        if (algorithms != null) {
            for (final SignatureAndHashAlgorithm sigAlg : algorithms) {
                algorithmNames.add(sigAlg.algorithm);
            }
        }
        final String[] array = new String[algorithmNames.size()];
        return algorithmNames.toArray(array);
    }
    
    static Set<String> getHashAlgorithmNames(final Collection<SignatureAndHashAlgorithm> algorithms) {
        final Set<String> algorithmNames = new HashSet<String>();
        if (algorithms != null) {
            for (final SignatureAndHashAlgorithm sigAlg : algorithms) {
                if (sigAlg.hash.value > 0) {
                    algorithmNames.add(sigAlg.hash.standardName);
                }
            }
        }
        return algorithmNames;
    }
    
    static String getHashAlgorithmName(final SignatureAndHashAlgorithm algorithm) {
        return algorithm.hash.standardName;
    }
    
    private static void supports(final String name, final HashAlgorithm hash, final SignatureAlgorithm signature, final String algorithm, final int priority) {
        supports(name, hash, signature, algorithm, null, priority);
    }
    
    private static void supports(final String name, final HashAlgorithm hash, final SignatureAlgorithm signature, final String algorithm, final String curve, final int priority) {
        final SignatureAndHashAlgorithm pair = new SignatureAndHashAlgorithm(name, hash, signature, algorithm, curve, priority);
        if (SignatureAndHashAlgorithm.supportedMap.put(pair.id, pair) != null) {
            throw new RuntimeException("Duplicate SignatureAndHashAlgorithm definition, id: " + pair.id);
        }
        if (SignatureAndHashAlgorithm.priorityMap.put(pair.priority, pair) != null) {
            throw new RuntimeException("Duplicate SignatureAndHashAlgorithm definition, priority: " + pair.priority);
        }
    }
    
    static SignatureAndHashAlgorithm getPreferableAlgorithm(final Collection<SignatureAndHashAlgorithm> algorithms, final AlgorithmConstraints constraints, final String expected) {
        return getPreferableAlgorithm(algorithms, constraints, expected, null);
    }
    
    static SignatureAndHashAlgorithm getPreferableAlgorithm(final Collection<SignatureAndHashAlgorithm> algorithms, final AlgorithmConstraints constraints, final String expected, final PrivateKey signingKey) {
        final int maxDigestLength = getMaxDigestLength(signingKey);
        for (final SignatureAndHashAlgorithm algorithm : algorithms) {
            if (algorithm.isPermitted(constraints)) {
                final int signValue = algorithm.id & 0xFF;
                if ((expected == null || (expected.equalsIgnoreCase("rsa") && signValue == SignatureAlgorithm.RSA.value) || (expected.equalsIgnoreCase("dsa") && signValue == SignatureAlgorithm.DSA.value) || (expected.equalsIgnoreCase("ecdsa") && signValue == SignatureAlgorithm.ECDSA.value) || (expected.equalsIgnoreCase("ec") && signValue == SignatureAlgorithm.ECDSA.value)) && algorithm.priority <= 240 && algorithm.hash.length <= maxDigestLength) {
                    return algorithm;
                }
                continue;
            }
        }
        return null;
    }
    
    private static int getMaxDigestLength(final PrivateKey signingKey) {
        int maxDigestLength = Integer.MAX_VALUE;
        if (signingKey != null && "rsa".equalsIgnoreCase(signingKey.getAlgorithm())) {
            final int keySize = KeyUtil.getKeySize(signingKey);
            if (keySize >= 768) {
                maxDigestLength = HashAlgorithm.SHA512.length;
            }
            else if (keySize >= 512 && keySize < 768) {
                maxDigestLength = HashAlgorithm.SHA256.length;
            }
            else if (keySize > 0 && keySize < 512) {
                maxDigestLength = HashAlgorithm.SHA1.length;
            }
        }
        return maxDigestLength;
    }
    
    static {
        SIGNATURE_PRIMITIVE_SET = Collections.unmodifiableSet((Set<? extends CryptoPrimitive>)EnumSet.of(CryptoPrimitive.SIGNATURE));
        supportedMap = Collections.synchronizedSortedMap(new TreeMap<Object, Object>());
        priorityMap = Collections.synchronizedSortedMap(new TreeMap<Object, Object>());
        synchronized (SignatureAndHashAlgorithm.supportedMap) {
            int p = 240;
            supports("rsa_md5", HashAlgorithm.MD5, SignatureAlgorithm.RSA, "MD5withRSA", --p);
            supports("dsa_sha1", HashAlgorithm.SHA1, SignatureAlgorithm.DSA, "SHA1withDSA", --p);
            supports("rsa_pkcs1_sha1", HashAlgorithm.SHA1, SignatureAlgorithm.RSA, "SHA1withRSA", --p);
            supports("ecdsa_sha1", HashAlgorithm.SHA1, SignatureAlgorithm.ECDSA, "SHA1withECDSA", --p);
            if (Security.getProvider("SunMSCAPI") == null) {
                supports("dsa_sha224", HashAlgorithm.SHA224, SignatureAlgorithm.DSA, "SHA224withDSA", --p);
                supports("rsa_sha224", HashAlgorithm.SHA224, SignatureAlgorithm.RSA, "SHA224withRSA", --p);
                supports("ecdsa_sha224", HashAlgorithm.SHA224, SignatureAlgorithm.ECDSA, "SHA224withECDSA", --p);
            }
            supports("dsa_sha256", HashAlgorithm.SHA256, SignatureAlgorithm.DSA, "SHA256withDSA", --p);
            supports("rsa_pkcs1_sha256", HashAlgorithm.SHA256, SignatureAlgorithm.RSA, "SHA256withRSA", --p);
            supports("ecdsa_secp256r1_sha256", HashAlgorithm.SHA256, SignatureAlgorithm.ECDSA, "SHA256withECDSA", "secp256r1", --p);
            supports("rsa_pkcs1_sha384", HashAlgorithm.SHA384, SignatureAlgorithm.RSA, "SHA384withRSA", --p);
            supports("ecdsa_secp384r1_sha384", HashAlgorithm.SHA384, SignatureAlgorithm.ECDSA, "SHA384withECDSA", "secp384r1", --p);
            supports("rsa_pkcs1_sha512", HashAlgorithm.SHA512, SignatureAlgorithm.RSA, "SHA512withRSA", --p);
            supports("ecdsa_secp521r1_sha512", HashAlgorithm.SHA512, SignatureAlgorithm.ECDSA, "SHA512withECDSA", "secp521r1", --p);
        }
    }
    
    enum HashAlgorithm
    {
        UNDEFINED("undefined", "", -1, -1), 
        NONE("none", "NONE", 0, -1), 
        MD5("md5", "MD5", 1, 16), 
        SHA1("sha1", "SHA-1", 2, 20), 
        SHA224("sha224", "SHA-224", 3, 28), 
        SHA256("sha256", "SHA-256", 4, 32), 
        SHA384("sha384", "SHA-384", 5, 48), 
        SHA512("sha512", "SHA-512", 6, 64);
        
        final String name;
        final String standardName;
        final int value;
        final int length;
        
        private HashAlgorithm(final String name, final String standardName, final int value, final int length) {
            this.name = name;
            this.standardName = standardName;
            this.value = value;
            this.length = length;
        }
        
        static HashAlgorithm valueOf(final int value) {
            HashAlgorithm algorithm = HashAlgorithm.UNDEFINED;
            switch (value) {
                case 0: {
                    algorithm = HashAlgorithm.NONE;
                    break;
                }
                case 1: {
                    algorithm = HashAlgorithm.MD5;
                    break;
                }
                case 2: {
                    algorithm = HashAlgorithm.SHA1;
                    break;
                }
                case 3: {
                    algorithm = HashAlgorithm.SHA224;
                    break;
                }
                case 4: {
                    algorithm = HashAlgorithm.SHA256;
                    break;
                }
                case 5: {
                    algorithm = HashAlgorithm.SHA384;
                    break;
                }
                case 6: {
                    algorithm = HashAlgorithm.SHA512;
                    break;
                }
            }
            return algorithm;
        }
    }
    
    enum SignatureAlgorithm
    {
        UNDEFINED("undefined", -1), 
        ANONYMOUS("anonymous", 0), 
        RSA("rsa", 1), 
        DSA("dsa", 2), 
        ECDSA("ecdsa", 3);
        
        final String name;
        final int value;
        
        private SignatureAlgorithm(final String name, final int value) {
            this.name = name;
            this.value = value;
        }
        
        static SignatureAlgorithm valueOf(final int value) {
            SignatureAlgorithm algorithm = SignatureAlgorithm.UNDEFINED;
            switch (value) {
                case 0: {
                    algorithm = SignatureAlgorithm.ANONYMOUS;
                    break;
                }
                case 1: {
                    algorithm = SignatureAlgorithm.RSA;
                    break;
                }
                case 2: {
                    algorithm = SignatureAlgorithm.DSA;
                    break;
                }
                case 3: {
                    algorithm = SignatureAlgorithm.ECDSA;
                    break;
                }
            }
            return algorithm;
        }
    }
}
