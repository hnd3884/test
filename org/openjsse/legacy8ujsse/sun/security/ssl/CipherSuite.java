package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;

final class CipherSuite implements Comparable<CipherSuite>
{
    static final int SUPPORTED_SUITES_PRIORITY = 1;
    static final int DEFAULT_SUITES_PRIORITY = 300;
    private static final boolean ALLOW_ECC;
    private static final Map<Integer, CipherSuite> idMap;
    private static final Map<String, CipherSuite> nameMap;
    final String name;
    final int id;
    final int priority;
    final KeyExchange keyExchange;
    final BulkCipher cipher;
    final MacAlg macAlg;
    final PRF prfAlg;
    final boolean exportable;
    final boolean allowed;
    final int obsoleted;
    final int supported;
    static final BulkCipher B_NULL;
    static final BulkCipher B_RC4_40;
    static final BulkCipher B_RC2_40;
    static final BulkCipher B_DES_40;
    static final BulkCipher B_RC4_128;
    static final BulkCipher B_DES;
    static final BulkCipher B_3DES;
    static final BulkCipher B_IDEA;
    static final BulkCipher B_AES_128;
    static final BulkCipher B_AES_256;
    static final BulkCipher B_AES_128_GCM;
    static final BulkCipher B_AES_256_GCM;
    static final MacAlg M_NULL;
    static final MacAlg M_MD5;
    static final MacAlg M_SHA;
    static final MacAlg M_SHA256;
    static final MacAlg M_SHA384;
    static final CipherSuite C_NULL;
    static final CipherSuite C_SCSV;
    
    private CipherSuite(final String name, final int id, final int priority, final KeyExchange keyExchange, final BulkCipher cipher, boolean allowed, final int obsoleted, final int supported, final PRF prfAlg) {
        this.name = name;
        this.id = id;
        this.priority = priority;
        this.keyExchange = keyExchange;
        this.cipher = cipher;
        this.exportable = cipher.exportable;
        if (cipher.cipherType == CipherType.AEAD_CIPHER) {
            this.macAlg = CipherSuite.M_NULL;
        }
        else if (name.endsWith("_MD5")) {
            this.macAlg = CipherSuite.M_MD5;
        }
        else if (name.endsWith("_SHA")) {
            this.macAlg = CipherSuite.M_SHA;
        }
        else if (name.endsWith("_SHA256")) {
            this.macAlg = CipherSuite.M_SHA256;
        }
        else if (name.endsWith("_SHA384")) {
            this.macAlg = CipherSuite.M_SHA384;
        }
        else if (name.endsWith("_NULL")) {
            this.macAlg = CipherSuite.M_NULL;
        }
        else {
            if (!name.endsWith("_SCSV")) {
                throw new IllegalArgumentException("Unknown MAC algorithm for ciphersuite " + name);
            }
            this.macAlg = CipherSuite.M_NULL;
        }
        allowed &= keyExchange.allowed;
        allowed &= cipher.allowed;
        this.allowed = allowed;
        this.obsoleted = obsoleted;
        this.supported = supported;
        this.prfAlg = prfAlg;
    }
    
    private CipherSuite(final String name, final int id) {
        this.name = name;
        this.id = id;
        this.allowed = false;
        this.priority = 0;
        this.keyExchange = null;
        this.cipher = null;
        this.macAlg = null;
        this.exportable = false;
        this.obsoleted = 65535;
        this.supported = 0;
        this.prfAlg = PRF.P_NONE;
    }
    
    boolean isAvailable() {
        return this.allowed && this.keyExchange.isAvailable() && this.cipher.isAvailable();
    }
    
    boolean isNegotiable() {
        return this != CipherSuite.C_SCSV && this.isAvailable();
    }
    
    @Override
    public int compareTo(final CipherSuite o) {
        return o.priority - this.priority;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    static CipherSuite valueOf(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        final CipherSuite c = CipherSuite.nameMap.get(s);
        if (c == null || !c.allowed) {
            throw new IllegalArgumentException("Unsupported ciphersuite " + s);
        }
        return c;
    }
    
    static CipherSuite valueOf(int id1, int id2) {
        id1 &= 0xFF;
        id2 &= 0xFF;
        final int id3 = id1 << 8 | id2;
        CipherSuite c = CipherSuite.idMap.get(id3);
        if (c == null) {
            final String h1 = Integer.toString(id1, 16);
            final String h2 = Integer.toString(id2, 16);
            c = new CipherSuite("Unknown 0x" + h1 + ":0x" + h2, id3);
        }
        return c;
    }
    
    static Collection<CipherSuite> allowedCipherSuites() {
        return CipherSuite.nameMap.values();
    }
    
    private static void add(final String name, final int id, final int priority, final KeyExchange keyExchange, final BulkCipher cipher, final boolean allowed, final int obsoleted, final int supported, final PRF prf) {
        final CipherSuite c = new CipherSuite(name, id, priority, keyExchange, cipher, allowed, obsoleted, supported, prf);
        if (CipherSuite.idMap.put(id, c) != null) {
            throw new RuntimeException("Duplicate ciphersuite definition: " + id + ", " + name);
        }
        if (c.allowed && CipherSuite.nameMap.put(name, c) != null) {
            throw new RuntimeException("Duplicate ciphersuite definition: " + id + ", " + name);
        }
    }
    
    private static void add(final String name, final int id, final int priority, final KeyExchange keyExchange, final BulkCipher cipher, final boolean allowed, final int obsoleted) {
        PRF prf = PRF.P_SHA256;
        if (obsoleted < ProtocolVersion.TLS12.v) {
            prf = PRF.P_NONE;
        }
        add(name, id, priority, keyExchange, cipher, allowed, obsoleted, 0, prf);
    }
    
    private static void add(final String name, final int id, final int priority, final KeyExchange keyExchange, final BulkCipher cipher, final boolean allowed) {
        add(name, id, priority, keyExchange, cipher, allowed, 65535);
    }
    
    private static void add(final String name, final int id) {
        final CipherSuite c = new CipherSuite(name, id);
        if (CipherSuite.idMap.put(id, c) != null) {
            throw new RuntimeException("Duplicate ciphersuite definition: " + id + ", " + name);
        }
    }
    
    static {
        ALLOW_ECC = Debug.getBooleanProperty("com.sun.net.ssl.enableECC", true);
        B_NULL = new BulkCipher("NULL", CipherType.STREAM_CIPHER, 0, 0, 0, 0, true);
        B_RC4_40 = new BulkCipher("RC4", CipherType.STREAM_CIPHER, 5, 16, 0, 0, true);
        B_RC2_40 = new BulkCipher("RC2", CipherType.BLOCK_CIPHER, 5, 16, 8, 0, false);
        B_DES_40 = new BulkCipher("DES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 5, 8, 8, 0, true);
        B_RC4_128 = new BulkCipher("RC4", CipherType.STREAM_CIPHER, 16, 0, 0, true);
        B_DES = new BulkCipher("DES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 8, 8, 0, true);
        B_3DES = new BulkCipher("DESede/CBC/NoPadding", CipherType.BLOCK_CIPHER, 24, 8, 0, true);
        B_IDEA = new BulkCipher("IDEA", CipherType.BLOCK_CIPHER, 16, 8, 0, false);
        B_AES_128 = new BulkCipher("AES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 16, 16, 0, true);
        B_AES_256 = new BulkCipher("AES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 32, 16, 0, true);
        B_AES_128_GCM = new BulkCipher("AES/GCM/NoPadding", CipherType.AEAD_CIPHER, 16, 12, 4, true);
        B_AES_256_GCM = new BulkCipher("AES/GCM/NoPadding", CipherType.AEAD_CIPHER, 32, 12, 4, true);
        M_NULL = new MacAlg("NULL", 0, 0, 0);
        M_MD5 = new MacAlg("MD5", 16, 64, 9);
        M_SHA = new MacAlg("SHA", 20, 64, 9);
        M_SHA256 = new MacAlg("SHA256", 32, 64, 9);
        M_SHA384 = new MacAlg("SHA384", 48, 128, 17);
        idMap = new HashMap<Integer, CipherSuite>();
        nameMap = new HashMap<String, CipherSuite>();
        final boolean F = false;
        final boolean T = true;
        final boolean N = !Legacy8uJSSE.isFIPS();
        add("SSL_NULL_WITH_NULL_NULL", 0, 1, KeyExchange.K_NULL, CipherSuite.B_NULL, false);
        int p = 600;
        final int max = 65535;
        final int tls11 = ProtocolVersion.TLS11.v;
        final int tls12 = ProtocolVersion.TLS12.v;
        add("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", 49188, --p, KeyExchange.K_ECDHE_ECDSA, CipherSuite.B_AES_256, true, max, tls12, PRF.P_SHA384);
        add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", 49192, --p, KeyExchange.K_ECDHE_RSA, CipherSuite.B_AES_256, true, max, tls12, PRF.P_SHA384);
        add("TLS_RSA_WITH_AES_256_CBC_SHA256", 61, --p, KeyExchange.K_RSA, CipherSuite.B_AES_256, true, max, tls12, PRF.P_SHA256);
        add("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", 49190, --p, KeyExchange.K_ECDH_ECDSA, CipherSuite.B_AES_256, true, max, tls12, PRF.P_SHA384);
        add("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", 49194, --p, KeyExchange.K_ECDH_RSA, CipherSuite.B_AES_256, true, max, tls12, PRF.P_SHA384);
        add("TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", 107, --p, KeyExchange.K_DHE_RSA, CipherSuite.B_AES_256, true, max, tls12, PRF.P_SHA256);
        add("TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", 106, --p, KeyExchange.K_DHE_DSS, CipherSuite.B_AES_256, true, max, tls12, PRF.P_SHA256);
        add("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", 49162, --p, KeyExchange.K_ECDHE_ECDSA, CipherSuite.B_AES_256, true);
        add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", 49172, --p, KeyExchange.K_ECDHE_RSA, CipherSuite.B_AES_256, true);
        add("TLS_RSA_WITH_AES_256_CBC_SHA", 53, --p, KeyExchange.K_RSA, CipherSuite.B_AES_256, true);
        add("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", 49157, --p, KeyExchange.K_ECDH_ECDSA, CipherSuite.B_AES_256, true);
        add("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", 49167, --p, KeyExchange.K_ECDH_RSA, CipherSuite.B_AES_256, true);
        add("TLS_DHE_RSA_WITH_AES_256_CBC_SHA", 57, --p, KeyExchange.K_DHE_RSA, CipherSuite.B_AES_256, true);
        add("TLS_DHE_DSS_WITH_AES_256_CBC_SHA", 56, --p, KeyExchange.K_DHE_DSS, CipherSuite.B_AES_256, true);
        add("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", 49187, --p, KeyExchange.K_ECDHE_ECDSA, CipherSuite.B_AES_128, true, max, tls12, PRF.P_SHA256);
        add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", 49191, --p, KeyExchange.K_ECDHE_RSA, CipherSuite.B_AES_128, true, max, tls12, PRF.P_SHA256);
        add("TLS_RSA_WITH_AES_128_CBC_SHA256", 60, --p, KeyExchange.K_RSA, CipherSuite.B_AES_128, true, max, tls12, PRF.P_SHA256);
        add("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", 49189, --p, KeyExchange.K_ECDH_ECDSA, CipherSuite.B_AES_128, true, max, tls12, PRF.P_SHA256);
        add("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", 49193, --p, KeyExchange.K_ECDH_RSA, CipherSuite.B_AES_128, true, max, tls12, PRF.P_SHA256);
        add("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", 103, --p, KeyExchange.K_DHE_RSA, CipherSuite.B_AES_128, true, max, tls12, PRF.P_SHA256);
        add("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", 64, --p, KeyExchange.K_DHE_DSS, CipherSuite.B_AES_128, true, max, tls12, PRF.P_SHA256);
        add("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", 49161, --p, KeyExchange.K_ECDHE_ECDSA, CipherSuite.B_AES_128, true);
        add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", 49171, --p, KeyExchange.K_ECDHE_RSA, CipherSuite.B_AES_128, true);
        add("TLS_RSA_WITH_AES_128_CBC_SHA", 47, --p, KeyExchange.K_RSA, CipherSuite.B_AES_128, true);
        add("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", 49156, --p, KeyExchange.K_ECDH_ECDSA, CipherSuite.B_AES_128, true);
        add("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", 49166, --p, KeyExchange.K_ECDH_RSA, CipherSuite.B_AES_128, true);
        add("TLS_DHE_RSA_WITH_AES_128_CBC_SHA", 51, --p, KeyExchange.K_DHE_RSA, CipherSuite.B_AES_128, true);
        add("TLS_DHE_DSS_WITH_AES_128_CBC_SHA", 50, --p, KeyExchange.K_DHE_DSS, CipherSuite.B_AES_128, true);
        add("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", 49196, --p, KeyExchange.K_ECDHE_ECDSA, CipherSuite.B_AES_256_GCM, true, max, tls12, PRF.P_SHA384);
        add("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", 49195, --p, KeyExchange.K_ECDHE_ECDSA, CipherSuite.B_AES_128_GCM, true, max, tls12, PRF.P_SHA256);
        add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", 49200, --p, KeyExchange.K_ECDHE_RSA, CipherSuite.B_AES_256_GCM, true, max, tls12, PRF.P_SHA384);
        add("TLS_RSA_WITH_AES_256_GCM_SHA384", 157, --p, KeyExchange.K_RSA, CipherSuite.B_AES_256_GCM, true, max, tls12, PRF.P_SHA384);
        add("TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384", 49198, --p, KeyExchange.K_ECDH_ECDSA, CipherSuite.B_AES_256_GCM, true, max, tls12, PRF.P_SHA384);
        add("TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384", 49202, --p, KeyExchange.K_ECDH_RSA, CipherSuite.B_AES_256_GCM, true, max, tls12, PRF.P_SHA384);
        add("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", 159, --p, KeyExchange.K_DHE_RSA, CipherSuite.B_AES_256_GCM, true, max, tls12, PRF.P_SHA384);
        add("TLS_DHE_DSS_WITH_AES_256_GCM_SHA384", 163, --p, KeyExchange.K_DHE_DSS, CipherSuite.B_AES_256_GCM, true, max, tls12, PRF.P_SHA384);
        add("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", 49199, --p, KeyExchange.K_ECDHE_RSA, CipherSuite.B_AES_128_GCM, true, max, tls12, PRF.P_SHA256);
        add("TLS_RSA_WITH_AES_128_GCM_SHA256", 156, --p, KeyExchange.K_RSA, CipherSuite.B_AES_128_GCM, true, max, tls12, PRF.P_SHA256);
        add("TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", 49197, --p, KeyExchange.K_ECDH_ECDSA, CipherSuite.B_AES_128_GCM, true, max, tls12, PRF.P_SHA256);
        add("TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", 49201, --p, KeyExchange.K_ECDH_RSA, CipherSuite.B_AES_128_GCM, true, max, tls12, PRF.P_SHA256);
        add("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", 158, --p, KeyExchange.K_DHE_RSA, CipherSuite.B_AES_128_GCM, true, max, tls12, PRF.P_SHA256);
        add("TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", 162, --p, KeyExchange.K_DHE_DSS, CipherSuite.B_AES_128_GCM, true, max, tls12, PRF.P_SHA256);
        add("TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", 49160, --p, KeyExchange.K_ECDHE_ECDSA, CipherSuite.B_3DES, true);
        add("TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", 49170, --p, KeyExchange.K_ECDHE_RSA, CipherSuite.B_3DES, true);
        add("SSL_RSA_WITH_3DES_EDE_CBC_SHA", 10, --p, KeyExchange.K_RSA, CipherSuite.B_3DES, true);
        add("TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA", 49155, --p, KeyExchange.K_ECDH_ECDSA, CipherSuite.B_3DES, true);
        add("TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA", 49165, --p, KeyExchange.K_ECDH_RSA, CipherSuite.B_3DES, true);
        add("SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA", 22, --p, KeyExchange.K_DHE_RSA, CipherSuite.B_3DES, true);
        add("SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA", 19, --p, KeyExchange.K_DHE_DSS, CipherSuite.B_3DES, N);
        add("TLS_EMPTY_RENEGOTIATION_INFO_SCSV", 255, --p, KeyExchange.K_SCSV, CipherSuite.B_NULL, true);
        p = 300;
        add("TLS_DH_anon_WITH_AES_256_GCM_SHA384", 167, --p, KeyExchange.K_DH_ANON, CipherSuite.B_AES_256_GCM, N, max, tls12, PRF.P_SHA384);
        add("TLS_DH_anon_WITH_AES_128_GCM_SHA256", 166, --p, KeyExchange.K_DH_ANON, CipherSuite.B_AES_128_GCM, N, max, tls12, PRF.P_SHA256);
        add("TLS_DH_anon_WITH_AES_256_CBC_SHA256", 109, --p, KeyExchange.K_DH_ANON, CipherSuite.B_AES_256, N, max, tls12, PRF.P_SHA256);
        add("TLS_ECDH_anon_WITH_AES_256_CBC_SHA", 49177, --p, KeyExchange.K_ECDH_ANON, CipherSuite.B_AES_256, N);
        add("TLS_DH_anon_WITH_AES_256_CBC_SHA", 58, --p, KeyExchange.K_DH_ANON, CipherSuite.B_AES_256, N);
        add("TLS_DH_anon_WITH_AES_128_CBC_SHA256", 108, --p, KeyExchange.K_DH_ANON, CipherSuite.B_AES_128, N, max, tls12, PRF.P_SHA256);
        add("TLS_ECDH_anon_WITH_AES_128_CBC_SHA", 49176, --p, KeyExchange.K_ECDH_ANON, CipherSuite.B_AES_128, N);
        add("TLS_DH_anon_WITH_AES_128_CBC_SHA", 52, --p, KeyExchange.K_DH_ANON, CipherSuite.B_AES_128, N);
        add("TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA", 49175, --p, KeyExchange.K_ECDH_ANON, CipherSuite.B_3DES, N);
        add("SSL_DH_anon_WITH_3DES_EDE_CBC_SHA", 27, --p, KeyExchange.K_DH_ANON, CipherSuite.B_3DES, N);
        add("TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", 49159, --p, KeyExchange.K_ECDHE_ECDSA, CipherSuite.B_RC4_128, N);
        add("TLS_ECDHE_RSA_WITH_RC4_128_SHA", 49169, --p, KeyExchange.K_ECDHE_RSA, CipherSuite.B_RC4_128, N);
        add("SSL_RSA_WITH_RC4_128_SHA", 5, --p, KeyExchange.K_RSA, CipherSuite.B_RC4_128, N);
        add("TLS_ECDH_ECDSA_WITH_RC4_128_SHA", 49154, --p, KeyExchange.K_ECDH_ECDSA, CipherSuite.B_RC4_128, N);
        add("TLS_ECDH_RSA_WITH_RC4_128_SHA", 49164, --p, KeyExchange.K_ECDH_RSA, CipherSuite.B_RC4_128, N);
        add("SSL_RSA_WITH_RC4_128_MD5", 4, --p, KeyExchange.K_RSA, CipherSuite.B_RC4_128, N);
        add("TLS_ECDH_anon_WITH_RC4_128_SHA", 49174, --p, KeyExchange.K_ECDH_ANON, CipherSuite.B_RC4_128, N);
        add("SSL_DH_anon_WITH_RC4_128_MD5", 24, --p, KeyExchange.K_DH_ANON, CipherSuite.B_RC4_128, N);
        add("SSL_RSA_WITH_DES_CBC_SHA", 9, --p, KeyExchange.K_RSA, CipherSuite.B_DES, N, tls12);
        add("SSL_DHE_RSA_WITH_DES_CBC_SHA", 21, --p, KeyExchange.K_DHE_RSA, CipherSuite.B_DES, N, tls12);
        add("SSL_DHE_DSS_WITH_DES_CBC_SHA", 18, --p, KeyExchange.K_DHE_DSS, CipherSuite.B_DES, N, tls12);
        add("SSL_DH_anon_WITH_DES_CBC_SHA", 26, --p, KeyExchange.K_DH_ANON, CipherSuite.B_DES, N, tls12);
        add("SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", 8, --p, KeyExchange.K_RSA_EXPORT, CipherSuite.B_DES_40, N, tls11);
        add("SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", 20, --p, KeyExchange.K_DHE_RSA, CipherSuite.B_DES_40, N, tls11);
        add("SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", 17, --p, KeyExchange.K_DHE_DSS, CipherSuite.B_DES_40, N, tls11);
        add("SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA", 25, --p, KeyExchange.K_DH_ANON, CipherSuite.B_DES_40, N, tls11);
        add("SSL_RSA_EXPORT_WITH_RC4_40_MD5", 3, --p, KeyExchange.K_RSA_EXPORT, CipherSuite.B_RC4_40, N, tls11);
        add("SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", 23, --p, KeyExchange.K_DH_ANON, CipherSuite.B_RC4_40, N, tls11);
        add("TLS_RSA_WITH_NULL_SHA256", 59, --p, KeyExchange.K_RSA, CipherSuite.B_NULL, N, max, tls12, PRF.P_SHA256);
        add("TLS_ECDHE_ECDSA_WITH_NULL_SHA", 49158, --p, KeyExchange.K_ECDHE_ECDSA, CipherSuite.B_NULL, N);
        add("TLS_ECDHE_RSA_WITH_NULL_SHA", 49168, --p, KeyExchange.K_ECDHE_RSA, CipherSuite.B_NULL, N);
        add("SSL_RSA_WITH_NULL_SHA", 2, --p, KeyExchange.K_RSA, CipherSuite.B_NULL, N);
        add("TLS_ECDH_ECDSA_WITH_NULL_SHA", 49153, --p, KeyExchange.K_ECDH_ECDSA, CipherSuite.B_NULL, N);
        add("TLS_ECDH_RSA_WITH_NULL_SHA", 49163, --p, KeyExchange.K_ECDH_RSA, CipherSuite.B_NULL, N);
        add("TLS_ECDH_anon_WITH_NULL_SHA", 49173, --p, KeyExchange.K_ECDH_ANON, CipherSuite.B_NULL, N);
        add("SSL_RSA_WITH_NULL_MD5", 1, --p, KeyExchange.K_RSA, CipherSuite.B_NULL, N);
        add("TLS_KRB5_WITH_3DES_EDE_CBC_SHA", 31, --p, KeyExchange.K_KRB5, CipherSuite.B_3DES, N);
        add("TLS_KRB5_WITH_3DES_EDE_CBC_MD5", 35, --p, KeyExchange.K_KRB5, CipherSuite.B_3DES, N);
        add("TLS_KRB5_WITH_RC4_128_SHA", 32, --p, KeyExchange.K_KRB5, CipherSuite.B_RC4_128, N);
        add("TLS_KRB5_WITH_RC4_128_MD5", 36, --p, KeyExchange.K_KRB5, CipherSuite.B_RC4_128, N);
        add("TLS_KRB5_WITH_DES_CBC_SHA", 30, --p, KeyExchange.K_KRB5, CipherSuite.B_DES, N, tls12);
        add("TLS_KRB5_WITH_DES_CBC_MD5", 34, --p, KeyExchange.K_KRB5, CipherSuite.B_DES, N, tls12);
        add("TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA", 38, --p, KeyExchange.K_KRB5_EXPORT, CipherSuite.B_DES_40, N, tls11);
        add("TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5", 41, --p, KeyExchange.K_KRB5_EXPORT, CipherSuite.B_DES_40, N, tls11);
        add("TLS_KRB5_EXPORT_WITH_RC4_40_SHA", 40, --p, KeyExchange.K_KRB5_EXPORT, CipherSuite.B_RC4_40, N, tls11);
        add("TLS_KRB5_EXPORT_WITH_RC4_40_MD5", 43, --p, KeyExchange.K_KRB5_EXPORT, CipherSuite.B_RC4_40, N, tls11);
        add("SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5", 6);
        add("SSL_RSA_WITH_IDEA_CBC_SHA", 7);
        add("SSL_DH_DSS_EXPORT_WITH_DES40_CBC_SHA", 11);
        add("SSL_DH_DSS_WITH_DES_CBC_SHA", 12);
        add("SSL_DH_DSS_WITH_3DES_EDE_CBC_SHA", 13);
        add("SSL_DH_RSA_EXPORT_WITH_DES40_CBC_SHA", 14);
        add("SSL_DH_RSA_WITH_DES_CBC_SHA", 15);
        add("SSL_DH_RSA_WITH_3DES_EDE_CBC_SHA", 16);
        add("SSL_FORTEZZA_DMS_WITH_NULL_SHA", 28);
        add("SSL_FORTEZZA_DMS_WITH_FORTEZZA_CBC_SHA", 29);
        add("SSL_RSA_EXPORT1024_WITH_DES_CBC_SHA", 98);
        add("SSL_DHE_DSS_EXPORT1024_WITH_DES_CBC_SHA", 99);
        add("SSL_RSA_EXPORT1024_WITH_RC4_56_SHA", 100);
        add("SSL_DHE_DSS_EXPORT1024_WITH_RC4_56_SHA", 101);
        add("SSL_DHE_DSS_WITH_RC4_128_SHA", 102);
        add("NETSCAPE_RSA_FIPS_WITH_3DES_EDE_CBC_SHA", 65504);
        add("NETSCAPE_RSA_FIPS_WITH_DES_CBC_SHA", 65505);
        add("SSL_RSA_FIPS_WITH_DES_CBC_SHA", 65278);
        add("SSL_RSA_FIPS_WITH_3DES_EDE_CBC_SHA", 65279);
        add("TLS_KRB5_WITH_IDEA_CBC_SHA", 33);
        add("TLS_KRB5_WITH_IDEA_CBC_MD5", 37);
        add("TLS_KRB5_EXPORT_WITH_RC2_CBC_40_SHA", 39);
        add("TLS_KRB5_EXPORT_WITH_RC2_CBC_40_MD5", 42);
        add("TLS_RSA_WITH_SEED_CBC_SHA", 150);
        add("TLS_DH_DSS_WITH_SEED_CBC_SHA", 151);
        add("TLS_DH_RSA_WITH_SEED_CBC_SHA", 152);
        add("TLS_DHE_DSS_WITH_SEED_CBC_SHA", 153);
        add("TLS_DHE_RSA_WITH_SEED_CBC_SHA", 154);
        add("TLS_DH_anon_WITH_SEED_CBC_SHA", 155);
        add("TLS_PSK_WITH_RC4_128_SHA", 138);
        add("TLS_PSK_WITH_3DES_EDE_CBC_SHA", 139);
        add("TLS_PSK_WITH_AES_128_CBC_SHA", 140);
        add("TLS_PSK_WITH_AES_256_CBC_SHA", 141);
        add("TLS_DHE_PSK_WITH_RC4_128_SHA", 142);
        add("TLS_DHE_PSK_WITH_3DES_EDE_CBC_SHA", 143);
        add("TLS_DHE_PSK_WITH_AES_128_CBC_SHA", 144);
        add("TLS_DHE_PSK_WITH_AES_256_CBC_SHA", 145);
        add("TLS_RSA_PSK_WITH_RC4_128_SHA", 146);
        add("TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA", 147);
        add("TLS_RSA_PSK_WITH_AES_128_CBC_SHA", 148);
        add("TLS_RSA_PSK_WITH_AES_256_CBC_SHA", 149);
        add("TLS_PSK_WITH_NULL_SHA", 44);
        add("TLS_DHE_PSK_WITH_NULL_SHA", 45);
        add("TLS_RSA_PSK_WITH_NULL_SHA", 46);
        add("TLS_DH_DSS_WITH_AES_128_CBC_SHA", 48);
        add("TLS_DH_RSA_WITH_AES_128_CBC_SHA", 49);
        add("TLS_DH_DSS_WITH_AES_256_CBC_SHA", 54);
        add("TLS_DH_RSA_WITH_AES_256_CBC_SHA", 55);
        add("TLS_DH_DSS_WITH_AES_128_CBC_SHA256", 62);
        add("TLS_DH_RSA_WITH_AES_128_CBC_SHA256", 63);
        add("TLS_DH_DSS_WITH_AES_256_CBC_SHA256", 104);
        add("TLS_DH_RSA_WITH_AES_256_CBC_SHA256", 105);
        add("TLS_DH_RSA_WITH_AES_128_GCM_SHA256", 160);
        add("TLS_DH_RSA_WITH_AES_256_GCM_SHA384", 161);
        add("TLS_DH_DSS_WITH_AES_128_GCM_SHA256", 164);
        add("TLS_DH_DSS_WITH_AES_256_GCM_SHA384", 165);
        add("TLS_PSK_WITH_AES_128_GCM_SHA256", 168);
        add("TLS_PSK_WITH_AES_256_GCM_SHA384", 169);
        add("TLS_DHE_PSK_WITH_AES_128_GCM_SHA256", 170);
        add("TLS_DHE_PSK_WITH_AES_256_GCM_SHA384", 171);
        add("TLS_RSA_PSK_WITH_AES_128_GCM_SHA256", 172);
        add("TLS_RSA_PSK_WITH_AES_256_GCM_SHA384", 173);
        add("TLS_PSK_WITH_AES_128_CBC_SHA256", 174);
        add("TLS_PSK_WITH_AES_256_CBC_SHA384", 175);
        add("TLS_PSK_WITH_NULL_SHA256", 176);
        add("TLS_PSK_WITH_NULL_SHA384", 177);
        add("TLS_DHE_PSK_WITH_AES_128_CBC_SHA256", 178);
        add("TLS_DHE_PSK_WITH_AES_256_CBC_SHA384", 179);
        add("TLS_DHE_PSK_WITH_NULL_SHA256", 180);
        add("TLS_DHE_PSK_WITH_NULL_SHA384", 181);
        add("TLS_RSA_PSK_WITH_AES_128_CBC_SHA256", 182);
        add("TLS_RSA_PSK_WITH_AES_256_CBC_SHA384", 183);
        add("TLS_RSA_PSK_WITH_NULL_SHA256", 184);
        add("TLS_RSA_PSK_WITH_NULL_SHA384", 185);
        add("TLS_RSA_WITH_CAMELLIA_128_CBC_SHA", 65);
        add("TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA", 66);
        add("TLS_DH_RSA_WITH_CAMELLIA_128_CBC_SHA", 67);
        add("TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA", 68);
        add("TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA", 69);
        add("TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA", 70);
        add("TLS_RSA_WITH_CAMELLIA_256_CBC_SHA", 132);
        add("TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA", 133);
        add("TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA", 134);
        add("TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA", 135);
        add("TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA", 136);
        add("TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA", 137);
        add("TLS_RSA_WITH_CAMELLIA_128_CBC_SHA256", 186);
        add("TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256", 187);
        add("TLS_DH_RSA_WITH_CAMELLIA_128_CBC_SHA256", 188);
        add("TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA256", 189);
        add("TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256", 190);
        add("TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256", 191);
        add("TLS_RSA_WITH_CAMELLIA_256_CBC_SHA256", 192);
        add("TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256", 193);
        add("TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA256", 194);
        add("TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA256", 195);
        add("TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA256", 196);
        add("TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256", 197);
        add("TLS_FALLBACK_SCSV", 22016);
        add("TLS_SRP_SHA_WITH_3DES_EDE_CBC_SHA", 49178);
        add("TLS_SRP_SHA_RSA_WITH_3DES_EDE_CBC_SHA", 49179);
        add("TLS_SRP_SHA_DSS_WITH_3DES_EDE_CBC_SHA", 49180);
        add("TLS_SRP_SHA_WITH_AES_128_CBC_SHA", 49181);
        add("TLS_SRP_SHA_RSA_WITH_AES_128_CBC_SHA", 49182);
        add("TLS_SRP_SHA_DSS_WITH_AES_128_CBC_SHA", 49183);
        add("TLS_SRP_SHA_WITH_AES_256_CBC_SHA", 49184);
        add("TLS_SRP_SHA_RSA_WITH_AES_256_CBC_SHA", 49185);
        add("TLS_SRP_SHA_DSS_WITH_AES_256_CBC_SHA", 49186);
        add("TLS_ECDHE_PSK_WITH_RC4_128_SHA", 49203);
        add("TLS_ECDHE_PSK_WITH_3DES_EDE_CBC_SHA", 49204);
        add("TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA", 49205);
        add("TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA", 49206);
        add("TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA256", 49207);
        add("TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA384", 49208);
        add("TLS_ECDHE_PSK_WITH_NULL_SHA", 49209);
        add("TLS_ECDHE_PSK_WITH_NULL_SHA256", 49210);
        add("TLS_ECDHE_PSK_WITH_NULL_SHA384", 49211);
        add("TLS_RSA_WITH_ARIA_128_CBC_SHA256", 49212);
        add("TLS_RSA_WITH_ARIA_256_CBC_SHA384", 49213);
        add("TLS_DH_DSS_WITH_ARIA_128_CBC_SHA256", 49214);
        add("TLS_DH_DSS_WITH_ARIA_256_CBC_SHA384", 49215);
        add("TLS_DH_RSA_WITH_ARIA_128_CBC_SHA256", 49216);
        add("TLS_DH_RSA_WITH_ARIA_256_CBC_SHA384", 49217);
        add("TLS_DHE_DSS_WITH_ARIA_128_CBC_SHA256", 49218);
        add("TLS_DHE_DSS_WITH_ARIA_256_CBC_SHA384", 49219);
        add("TLS_DHE_RSA_WITH_ARIA_128_CBC_SHA256", 49220);
        add("TLS_DHE_RSA_WITH_ARIA_256_CBC_SHA384", 49221);
        add("TLS_DH_anon_WITH_ARIA_128_CBC_SHA256", 49222);
        add("TLS_DH_anon_WITH_ARIA_256_CBC_SHA384", 49223);
        add("TLS_ECDHE_ECDSA_WITH_ARIA_128_CBC_SHA256", 49224);
        add("TLS_ECDHE_ECDSA_WITH_ARIA_256_CBC_SHA384", 49225);
        add("TLS_ECDH_ECDSA_WITH_ARIA_128_CBC_SHA256", 49226);
        add("TLS_ECDH_ECDSA_WITH_ARIA_256_CBC_SHA384", 49227);
        add("TLS_ECDHE_RSA_WITH_ARIA_128_CBC_SHA256", 49228);
        add("TLS_ECDHE_RSA_WITH_ARIA_256_CBC_SHA384", 49229);
        add("TLS_ECDH_RSA_WITH_ARIA_128_CBC_SHA256", 49230);
        add("TLS_ECDH_RSA_WITH_ARIA_256_CBC_SHA384", 49231);
        add("TLS_RSA_WITH_ARIA_128_GCM_SHA256", 49232);
        add("TLS_RSA_WITH_ARIA_256_GCM_SHA384", 49233);
        add("TLS_DHE_RSA_WITH_ARIA_128_GCM_SHA256", 49234);
        add("TLS_DHE_RSA_WITH_ARIA_256_GCM_SHA384", 49235);
        add("TLS_DH_RSA_WITH_ARIA_128_GCM_SHA256", 49236);
        add("TLS_DH_RSA_WITH_ARIA_256_GCM_SHA384", 49237);
        add("TLS_DHE_DSS_WITH_ARIA_128_GCM_SHA256", 49238);
        add("TLS_DHE_DSS_WITH_ARIA_256_GCM_SHA384", 49239);
        add("TLS_DH_DSS_WITH_ARIA_128_GCM_SHA256", 49240);
        add("TLS_DH_DSS_WITH_ARIA_256_GCM_SHA384", 49241);
        add("TLS_DH_anon_WITH_ARIA_128_GCM_SHA256", 49242);
        add("TLS_DH_anon_WITH_ARIA_256_GCM_SHA384", 49243);
        add("TLS_ECDHE_ECDSA_WITH_ARIA_128_GCM_SHA256", 49244);
        add("TLS_ECDHE_ECDSA_WITH_ARIA_256_GCM_SHA384", 49245);
        add("TLS_ECDH_ECDSA_WITH_ARIA_128_GCM_SHA256", 49246);
        add("TLS_ECDH_ECDSA_WITH_ARIA_256_GCM_SHA384", 49247);
        add("TLS_ECDHE_RSA_WITH_ARIA_128_GCM_SHA256", 49248);
        add("TLS_ECDHE_RSA_WITH_ARIA_256_GCM_SHA384", 49249);
        add("TLS_ECDH_RSA_WITH_ARIA_128_GCM_SHA256", 49250);
        add("TLS_ECDH_RSA_WITH_ARIA_256_GCM_SHA384", 49251);
        add("TLS_PSK_WITH_ARIA_128_CBC_SHA256", 49252);
        add("TLS_PSK_WITH_ARIA_256_CBC_SHA384", 49253);
        add("TLS_DHE_PSK_WITH_ARIA_128_CBC_SHA256", 49254);
        add("TLS_DHE_PSK_WITH_ARIA_256_CBC_SHA384", 49255);
        add("TLS_RSA_PSK_WITH_ARIA_128_CBC_SHA256", 49256);
        add("TLS_RSA_PSK_WITH_ARIA_256_CBC_SHA384", 49257);
        add("TLS_PSK_WITH_ARIA_128_GCM_SHA256", 49258);
        add("TLS_PSK_WITH_ARIA_256_GCM_SHA384", 49259);
        add("TLS_DHE_PSK_WITH_ARIA_128_GCM_SHA256", 49260);
        add("TLS_DHE_PSK_WITH_ARIA_256_GCM_SHA384", 49261);
        add("TLS_RSA_PSK_WITH_ARIA_128_GCM_SHA256", 49262);
        add("TLS_RSA_PSK_WITH_ARIA_256_GCM_SHA384", 49263);
        add("TLS_ECDHE_PSK_WITH_ARIA_128_CBC_SHA256", 49264);
        add("TLS_ECDHE_PSK_WITH_ARIA_256_CBC_SHA384", 49265);
        add("TLS_ECDHE_ECDSA_WITH_CAMELLIA_128_CBC_SHA256", 49266);
        add("TLS_ECDHE_ECDSA_WITH_CAMELLIA_256_CBC_SHA384", 49267);
        add("TLS_ECDH_ECDSA_WITH_CAMELLIA_128_CBC_SHA256", 49268);
        add("TLS_ECDH_ECDSA_WITH_CAMELLIA_256_CBC_SHA384", 49269);
        add("TLS_ECDHE_RSA_WITH_CAMELLIA_128_CBC_SHA256", 49270);
        add("TLS_ECDHE_RSA_WITH_CAMELLIA_256_CBC_SHA384", 49271);
        add("TLS_ECDH_RSA_WITH_CAMELLIA_128_CBC_SHA256", 49272);
        add("TLS_ECDH_RSA_WITH_CAMELLIA_256_CBC_SHA384", 49273);
        add("TLS_RSA_WITH_CAMELLIA_128_GCM_SHA256", 49274);
        add("TLS_RSA_WITH_CAMELLIA_256_GCM_SHA384", 49275);
        add("TLS_DHE_RSA_WITH_CAMELLIA_128_GCM_SHA256", 49276);
        add("TLS_DHE_RSA_WITH_CAMELLIA_256_GCM_SHA384", 49277);
        add("TLS_DH_RSA_WITH_CAMELLIA_128_GCM_SHA256", 49278);
        add("TLS_DH_RSA_WITH_CAMELLIA_256_GCM_SHA384", 49279);
        add("TLS_DHE_DSS_WITH_CAMELLIA_128_GCM_SHA256", 49280);
        add("TLS_DHE_DSS_WITH_CAMELLIA_256_GCM_SHA384", 49281);
        add("TLS_DH_DSS_WITH_CAMELLIA_128_GCM_SHA256", 49282);
        add("TLS_DH_DSS_WITH_CAMELLIA_256_GCM_SHA384", 49283);
        add("TLS_DH_anon_WITH_CAMELLIA_128_GCM_SHA256", 49284);
        add("TLS_DH_anon_WITH_CAMELLIA_256_GCM_SHA384", 49285);
        add("TLS_ECDHE_ECDSA_WITH_CAMELLIA_128_GCM_SHA256", 49286);
        add("TLS_ECDHE_ECDSA_WITH_CAMELLIA_256_GCM_SHA384", 49287);
        add("TLS_ECDH_ECDSA_WITH_CAMELLIA_128_GCM_SHA256", 49288);
        add("TLS_ECDH_ECDSA_WITH_CAMELLIA_256_GCM_SHA384", 49289);
        add("TLS_ECDHE_RSA_WITH_CAMELLIA_128_GCM_SHA256", 49290);
        add("TLS_ECDHE_RSA_WITH_CAMELLIA_256_GCM_SHA384", 49291);
        add("TLS_ECDH_RSA_WITH_CAMELLIA_128_GCM_SHA256", 49292);
        add("TLS_ECDH_RSA_WITH_CAMELLIA_256_GCM_SHA384", 49293);
        add("TLS_PSK_WITH_CAMELLIA_128_GCM_SHA256", 49294);
        add("TLS_PSK_WITH_CAMELLIA_256_GCM_SHA384", 49295);
        add("TLS_DHE_PSK_WITH_CAMELLIA_128_GCM_SHA256", 49296);
        add("TLS_DHE_PSK_WITH_CAMELLIA_256_GCM_SHA384", 49297);
        add("TLS_RSA_PSK_WITH_CAMELLIA_128_GCM_SHA256", 49298);
        add("TLS_RSA_PSK_WITH_CAMELLIA_256_GCM_SHA384", 49299);
        add("TLS_PSK_WITH_CAMELLIA_128_CBC_SHA256", 49300);
        add("TLS_PSK_WITH_CAMELLIA_256_CBC_SHA384", 49301);
        add("TLS_DHE_PSK_WITH_CAMELLIA_128_CBC_SHA256", 49302);
        add("TLS_DHE_PSK_WITH_CAMELLIA_256_CBC_SHA384", 49303);
        add("TLS_RSA_PSK_WITH_CAMELLIA_128_CBC_SHA256", 49304);
        add("TLS_RSA_PSK_WITH_CAMELLIA_256_CBC_SHA384", 49305);
        add("TLS_ECDHE_PSK_WITH_CAMELLIA_128_CBC_SHA256", 49306);
        add("TLS_ECDHE_PSK_WITH_CAMELLIA_256_CBC_SHA384", 49307);
        add("TLS_RSA_WITH_AES_128_CCM", 49308);
        add("TLS_RSA_WITH_AES_256_CCM", 49309);
        add("TLS_DHE_RSA_WITH_AES_128_CCM", 49310);
        add("TLS_DHE_RSA_WITH_AES_256_CCM", 49311);
        add("TLS_RSA_WITH_AES_128_CCM_8", 49312);
        add("TLS_RSA_WITH_AES_256_CCM_8", 49313);
        add("TLS_DHE_RSA_WITH_AES_128_CCM_8", 49314);
        add("TLS_DHE_RSA_WITH_AES_256_CCM_8", 49315);
        add("TLS_PSK_WITH_AES_128_CCM", 49316);
        add("TLS_PSK_WITH_AES_256_CCM", 49317);
        add("TLS_DHE_PSK_WITH_AES_128_CCM", 49318);
        add("TLS_DHE_PSK_WITH_AES_256_CCM", 49319);
        add("TLS_PSK_WITH_AES_128_CCM_8", 49320);
        add("TLS_PSK_WITH_AES_256_CCM_8", 49321);
        add("TLS_PSK_DHE_WITH_AES_128_CCM_8", 49322);
        add("TLS_PSK_DHE_WITH_AES_256_CCM_8", 49323);
        add("TLS_ECDHE_ECDSA_WITH_AES_128_CCM", 49324);
        add("TLS_ECDHE_ECDSA_WITH_AES_256_CCM", 49325);
        add("TLS_ECDHE_ECDSA_WITH_AES_128_CCM_8", 49326);
        add("TLS_ECDHE_ECDSA_WITH_AES_256_CCM_8", 49327);
        C_NULL = valueOf(0, 0);
        C_SCSV = valueOf(0, 255);
    }
    
    enum KeyExchange
    {
        K_NULL("NULL", false, false), 
        K_RSA("RSA", true, false), 
        K_RSA_EXPORT("RSA_EXPORT", true, false), 
        K_DH_RSA("DH_RSA", false, false), 
        K_DH_DSS("DH_DSS", false, false), 
        K_DHE_DSS("DHE_DSS", true, false), 
        K_DHE_RSA("DHE_RSA", true, false), 
        K_DH_ANON("DH_anon", true, false), 
        K_ECDH_ECDSA("ECDH_ECDSA", CipherSuite.ALLOW_ECC, true), 
        K_ECDH_RSA("ECDH_RSA", CipherSuite.ALLOW_ECC, true), 
        K_ECDHE_ECDSA("ECDHE_ECDSA", CipherSuite.ALLOW_ECC, true), 
        K_ECDHE_RSA("ECDHE_RSA", CipherSuite.ALLOW_ECC, true), 
        K_ECDH_ANON("ECDH_anon", CipherSuite.ALLOW_ECC, true), 
        K_KRB5("KRB5", true, false), 
        K_KRB5_EXPORT("KRB5_EXPORT", true, false), 
        K_SCSV("SCSV", true, false);
        
        final String name;
        final boolean allowed;
        final boolean isEC;
        private final boolean alwaysAvailable;
        
        private KeyExchange(final String name, final boolean allowed, final boolean isEC) {
            this.name = name;
            this.allowed = allowed;
            this.isEC = isEC;
            this.alwaysAvailable = (allowed && !name.startsWith("EC") && !name.startsWith("KRB"));
        }
        
        boolean isAvailable() {
            if (this.alwaysAvailable) {
                return true;
            }
            if (this.isEC) {
                return this.allowed && JsseJce.isEcAvailable();
            }
            if (this.name.startsWith("KRB")) {
                return this.allowed && JsseJce.isKerberosAvailable();
            }
            return this.allowed;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    enum CipherType
    {
        STREAM_CIPHER, 
        BLOCK_CIPHER, 
        AEAD_CIPHER;
    }
    
    static final class BulkCipher
    {
        final String description;
        final String transformation;
        final String algorithm;
        final boolean allowed;
        final int keySize;
        final int expandedKeySize;
        final int ivSize;
        final int fixedIvSize;
        final boolean exportable;
        final CipherType cipherType;
        final int tagSize = 16;
        private static final SecureRandom secureRandom;
        private final boolean isAvailable;
        
        BulkCipher(final String transformation, final CipherType cipherType, final int keySize, final int expandedKeySize, final int ivSize, final int fixedIvSize, final boolean allowed) {
            this.transformation = transformation;
            final String[] splits = transformation.split("/");
            this.algorithm = splits[0];
            this.cipherType = cipherType;
            this.description = this.algorithm + "/" + (keySize << 3);
            this.keySize = keySize;
            this.ivSize = ivSize;
            this.fixedIvSize = fixedIvSize;
            this.allowed = allowed;
            this.expandedKeySize = expandedKeySize;
            this.exportable = true;
            this.isAvailable = (allowed && isUnlimited(keySize, transformation));
        }
        
        BulkCipher(final String transformation, final CipherType cipherType, final int keySize, final int ivSize, final int fixedIvSize, final boolean allowed) {
            this.transformation = transformation;
            final String[] splits = transformation.split("/");
            this.algorithm = splits[0];
            this.cipherType = cipherType;
            this.description = this.algorithm + "/" + (keySize << 3);
            this.keySize = keySize;
            this.ivSize = ivSize;
            this.fixedIvSize = fixedIvSize;
            this.allowed = allowed;
            this.expandedKeySize = keySize;
            this.exportable = false;
            this.isAvailable = (allowed && isUnlimited(keySize, transformation));
        }
        
        CipherBox newCipher(final ProtocolVersion version, final SecretKey key, final IvParameterSpec iv, final SecureRandom random, final boolean encrypt) throws NoSuchAlgorithmException {
            return CipherBox.newCipherBox(version, this, key, iv, random, encrypt);
        }
        
        boolean isAvailable() {
            return this.isAvailable;
        }
        
        private static boolean isUnlimited(final int keySize, final String transformation) {
            final int keySizeInBits = keySize * 8;
            if (keySizeInBits > 128) {
                try {
                    if (Cipher.getMaxAllowedKeyLength(transformation) < keySizeInBits) {
                        return false;
                    }
                }
                catch (final Exception e) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public String toString() {
            return this.description;
        }
        
        static {
            try {
                secureRandom = JsseJce.getSecureRandom();
            }
            catch (final KeyManagementException kme) {
                throw new RuntimeException(kme);
            }
        }
    }
    
    static final class MacAlg
    {
        final String name;
        final int size;
        final int hashBlockSize;
        final int minimalPaddingSize;
        
        MacAlg(final String name, final int size, final int hashBlockSize, final int minimalPaddingSize) {
            this.name = name;
            this.size = size;
            this.hashBlockSize = hashBlockSize;
            this.minimalPaddingSize = minimalPaddingSize;
        }
        
        MAC newMac(final ProtocolVersion protocolVersion, final SecretKey secret) throws NoSuchAlgorithmException, InvalidKeyException {
            return new MAC(this, protocolVersion, secret);
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    enum PRF
    {
        P_NONE("NONE", 0, 0), 
        P_SHA256("SHA-256", 32, 64), 
        P_SHA384("SHA-384", 48, 128), 
        P_SHA512("SHA-512", 64, 128);
        
        private final String prfHashAlg;
        private final int prfHashLength;
        private final int prfBlockSize;
        
        private PRF(final String prfHashAlg, final int prfHashLength, final int prfBlockSize) {
            this.prfHashAlg = prfHashAlg;
            this.prfHashLength = prfHashLength;
            this.prfBlockSize = prfBlockSize;
        }
        
        String getPRFHashAlg() {
            return this.prfHashAlg;
        }
        
        int getPRFHashLength() {
            return this.prfHashLength;
        }
        
        int getPRFBlockSize() {
            return this.prfBlockSize;
        }
    }
}
