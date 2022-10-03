package sun.security.ssl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import sun.security.util.AlgorithmDecomposer;

class SSLAlgorithmDecomposer extends AlgorithmDecomposer
{
    private final boolean onlyX509;
    
    SSLAlgorithmDecomposer(final boolean onlyX509) {
        this.onlyX509 = onlyX509;
    }
    
    SSLAlgorithmDecomposer() {
        this(false);
    }
    
    private Set<String> decomposes(final CipherSuite.KeyExchange keyExchange) {
        final HashSet set = new HashSet();
        switch (keyExchange) {
            case K_NULL: {
                if (!this.onlyX509) {
                    set.add("K_NULL");
                    break;
                }
                break;
            }
            case K_RSA: {
                set.add("RSA");
                break;
            }
            case K_RSA_EXPORT: {
                set.add("RSA");
                set.add("RSA_EXPORT");
                break;
            }
            case K_DH_RSA: {
                set.add("RSA");
                set.add("DH");
                set.add("DiffieHellman");
                set.add("DH_RSA");
                break;
            }
            case K_DH_DSS: {
                set.add("DSA");
                set.add("DSS");
                set.add("DH");
                set.add("DiffieHellman");
                set.add("DH_DSS");
                break;
            }
            case K_DHE_DSS: {
                set.add("DSA");
                set.add("DSS");
                set.add("DH");
                set.add("DHE");
                set.add("DiffieHellman");
                set.add("DHE_DSS");
                break;
            }
            case K_DHE_RSA: {
                set.add("RSA");
                set.add("DH");
                set.add("DHE");
                set.add("DiffieHellman");
                set.add("DHE_RSA");
                break;
            }
            case K_DH_ANON: {
                if (!this.onlyX509) {
                    set.add("ANON");
                    set.add("DH");
                    set.add("DiffieHellman");
                    set.add("DH_ANON");
                    break;
                }
                break;
            }
            case K_ECDH_ECDSA: {
                set.add("ECDH");
                set.add("ECDSA");
                set.add("ECDH_ECDSA");
                break;
            }
            case K_ECDH_RSA: {
                set.add("ECDH");
                set.add("RSA");
                set.add("ECDH_RSA");
                break;
            }
            case K_ECDHE_ECDSA: {
                set.add("ECDHE");
                set.add("ECDSA");
                set.add("ECDHE_ECDSA");
                break;
            }
            case K_ECDHE_RSA: {
                set.add("ECDHE");
                set.add("RSA");
                set.add("ECDHE_RSA");
                break;
            }
            case K_ECDH_ANON: {
                if (!this.onlyX509) {
                    set.add("ECDH");
                    set.add("ANON");
                    set.add("ECDH_ANON");
                    break;
                }
                break;
            }
        }
        return set;
    }
    
    private Set<String> decomposes(final SSLCipher sslCipher) {
        final HashSet set = new HashSet();
        if (sslCipher.transformation != null) {
            set.addAll(super.decompose(sslCipher.transformation));
        }
        switch (sslCipher) {
            case B_NULL: {
                set.add("C_NULL");
                break;
            }
            case B_RC2_40: {
                set.add("RC2_CBC_40");
                break;
            }
            case B_RC4_40: {
                set.add("RC4_40");
                break;
            }
            case B_RC4_128: {
                set.add("RC4_128");
                break;
            }
            case B_DES_40: {
                set.add("DES40_CBC");
                set.add("DES_CBC_40");
                break;
            }
            case B_DES: {
                set.add("DES_CBC");
                break;
            }
            case B_3DES: {
                set.add("3DES_EDE_CBC");
                break;
            }
            case B_AES_128: {
                set.add("AES_128_CBC");
                break;
            }
            case B_AES_256: {
                set.add("AES_256_CBC");
                break;
            }
            case B_AES_128_GCM: {
                set.add("AES_128_GCM");
                break;
            }
            case B_AES_256_GCM: {
                set.add("AES_256_GCM");
                break;
            }
        }
        return set;
    }
    
    private Set<String> decomposes(final CipherSuite.MacAlg macAlg, final SSLCipher sslCipher) {
        final HashSet set = new HashSet();
        if (macAlg == CipherSuite.MacAlg.M_NULL && sslCipher.cipherType != CipherType.AEAD_CIPHER) {
            set.add("M_NULL");
        }
        else if (macAlg == CipherSuite.MacAlg.M_MD5) {
            set.add("MD5");
            set.add("HmacMD5");
        }
        else if (macAlg == CipherSuite.MacAlg.M_SHA) {
            set.add("SHA1");
            set.add("SHA-1");
            set.add("HmacSHA1");
        }
        else if (macAlg == CipherSuite.MacAlg.M_SHA256) {
            set.add("SHA256");
            set.add("SHA-256");
            set.add("HmacSHA256");
        }
        else if (macAlg == CipherSuite.MacAlg.M_SHA384) {
            set.add("SHA384");
            set.add("SHA-384");
            set.add("HmacSHA384");
        }
        return set;
    }
    
    private Set<String> decomposes(final CipherSuite.HashAlg hashAlg) {
        final HashSet set = new HashSet();
        if (hashAlg == CipherSuite.HashAlg.H_SHA256) {
            set.add("SHA256");
            set.add("SHA-256");
            set.add("HmacSHA256");
        }
        else if (hashAlg == CipherSuite.HashAlg.H_SHA384) {
            set.add("SHA384");
            set.add("SHA-384");
            set.add("HmacSHA384");
        }
        return set;
    }
    
    private Set<String> decompose(final CipherSuite.KeyExchange keyExchange, final SSLCipher sslCipher, final CipherSuite.MacAlg macAlg, final CipherSuite.HashAlg hashAlg) {
        final HashSet set = new HashSet();
        if (keyExchange != null) {
            set.addAll(this.decomposes(keyExchange));
        }
        if (this.onlyX509) {
            return set;
        }
        if (sslCipher != null) {
            set.addAll(this.decomposes(sslCipher));
        }
        if (macAlg != null) {
            set.addAll(this.decomposes(macAlg, sslCipher));
        }
        if (hashAlg != null) {
            set.addAll(this.decomposes(hashAlg));
        }
        return set;
    }
    
    @Override
    public Set<String> decompose(final String s) {
        if (s.startsWith("SSL_") || s.startsWith("TLS_")) {
            CipherSuite name = null;
            try {
                name = CipherSuite.nameOf(s);
            }
            catch (final IllegalArgumentException ex) {}
            if (name != null && name != CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV) {
                return this.decompose(name.keyExchange, name.bulkCipher, name.macAlg, name.hashAlg);
            }
        }
        return super.decompose(s);
    }
}
