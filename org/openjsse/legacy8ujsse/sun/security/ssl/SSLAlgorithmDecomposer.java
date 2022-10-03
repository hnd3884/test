package org.openjsse.legacy8ujsse.sun.security.ssl;

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
        final Set<String> components = new HashSet<String>();
        switch (keyExchange) {
            case K_NULL: {
                if (!this.onlyX509) {
                    components.add("K_NULL");
                    break;
                }
                break;
            }
            case K_RSA: {
                components.add("RSA");
                break;
            }
            case K_RSA_EXPORT: {
                components.add("RSA");
                components.add("RSA_EXPORT");
                break;
            }
            case K_DH_RSA: {
                components.add("RSA");
                components.add("DH");
                components.add("DiffieHellman");
                components.add("DH_RSA");
                break;
            }
            case K_DH_DSS: {
                components.add("DSA");
                components.add("DSS");
                components.add("DH");
                components.add("DiffieHellman");
                components.add("DH_DSS");
                break;
            }
            case K_DHE_DSS: {
                components.add("DSA");
                components.add("DSS");
                components.add("DH");
                components.add("DHE");
                components.add("DiffieHellman");
                components.add("DHE_DSS");
                break;
            }
            case K_DHE_RSA: {
                components.add("RSA");
                components.add("DH");
                components.add("DHE");
                components.add("DiffieHellman");
                components.add("DHE_RSA");
                break;
            }
            case K_DH_ANON: {
                if (!this.onlyX509) {
                    components.add("ANON");
                    components.add("DH");
                    components.add("DiffieHellman");
                    components.add("DH_ANON");
                    break;
                }
                break;
            }
            case K_ECDH_ECDSA: {
                components.add("ECDH");
                components.add("ECDSA");
                components.add("ECDH_ECDSA");
                break;
            }
            case K_ECDH_RSA: {
                components.add("ECDH");
                components.add("RSA");
                components.add("ECDH_RSA");
                break;
            }
            case K_ECDHE_ECDSA: {
                components.add("ECDHE");
                components.add("ECDSA");
                components.add("ECDHE_ECDSA");
                break;
            }
            case K_ECDHE_RSA: {
                components.add("ECDHE");
                components.add("RSA");
                components.add("ECDHE_RSA");
                break;
            }
            case K_ECDH_ANON: {
                if (!this.onlyX509) {
                    components.add("ECDH");
                    components.add("ANON");
                    components.add("ECDH_ANON");
                    break;
                }
                break;
            }
            case K_KRB5: {
                if (!this.onlyX509) {
                    components.add("KRB5");
                    break;
                }
                break;
            }
            case K_KRB5_EXPORT: {
                if (!this.onlyX509) {
                    components.add("KRB5_EXPORT");
                    break;
                }
                break;
            }
        }
        return components;
    }
    
    private Set<String> decomposes(final CipherSuite.BulkCipher bulkCipher) {
        final Set<String> components = new HashSet<String>();
        if (bulkCipher.transformation != null) {
            components.addAll(super.decompose(bulkCipher.transformation));
        }
        if (bulkCipher == CipherSuite.B_NULL) {
            components.add("C_NULL");
        }
        else if (bulkCipher == CipherSuite.B_RC2_40) {
            components.add("RC2_CBC_40");
        }
        else if (bulkCipher == CipherSuite.B_RC4_40) {
            components.add("RC4_40");
        }
        else if (bulkCipher == CipherSuite.B_RC4_128) {
            components.add("RC4_128");
        }
        else if (bulkCipher == CipherSuite.B_DES_40) {
            components.add("DES40_CBC");
            components.add("DES_CBC_40");
        }
        else if (bulkCipher == CipherSuite.B_DES) {
            components.add("DES_CBC");
        }
        else if (bulkCipher == CipherSuite.B_3DES) {
            components.add("3DES_EDE_CBC");
        }
        else if (bulkCipher == CipherSuite.B_AES_128) {
            components.add("AES_128_CBC");
        }
        else if (bulkCipher == CipherSuite.B_AES_256) {
            components.add("AES_256_CBC");
        }
        else if (bulkCipher == CipherSuite.B_AES_128_GCM) {
            components.add("AES_128_GCM");
        }
        else if (bulkCipher == CipherSuite.B_AES_256_GCM) {
            components.add("AES_256_GCM");
        }
        return components;
    }
    
    private Set<String> decomposes(final CipherSuite.MacAlg macAlg, final CipherSuite.BulkCipher cipher) {
        final Set<String> components = new HashSet<String>();
        if (macAlg == CipherSuite.M_NULL && cipher.cipherType != CipherSuite.CipherType.AEAD_CIPHER) {
            components.add("M_NULL");
        }
        else if (macAlg == CipherSuite.M_MD5) {
            components.add("MD5");
            components.add("HmacMD5");
        }
        else if (macAlg == CipherSuite.M_SHA) {
            components.add("SHA1");
            components.add("SHA-1");
            components.add("HmacSHA1");
        }
        else if (macAlg == CipherSuite.M_SHA256) {
            components.add("SHA256");
            components.add("SHA-256");
            components.add("HmacSHA256");
        }
        else if (macAlg == CipherSuite.M_SHA384) {
            components.add("SHA384");
            components.add("SHA-384");
            components.add("HmacSHA384");
        }
        return components;
    }
    
    private Set<String> decompose(final CipherSuite.KeyExchange keyExchange, final CipherSuite.BulkCipher cipher, final CipherSuite.MacAlg macAlg) {
        final Set<String> components = new HashSet<String>();
        if (keyExchange != null) {
            components.addAll(this.decomposes(keyExchange));
        }
        if (this.onlyX509) {
            return components;
        }
        if (cipher != null) {
            components.addAll(this.decomposes(cipher));
        }
        if (macAlg != null) {
            components.addAll(this.decomposes(macAlg, cipher));
        }
        return components;
    }
    
    @Override
    public Set<String> decompose(final String algorithm) {
        if (algorithm.startsWith("SSL_") || algorithm.startsWith("TLS_")) {
            CipherSuite cipherSuite = null;
            try {
                cipherSuite = CipherSuite.valueOf(algorithm);
            }
            catch (final IllegalArgumentException ex) {}
            if (cipherSuite != null && cipherSuite != CipherSuite.C_SCSV) {
                return this.decompose(cipherSuite.keyExchange, cipherSuite.cipher, cipherSuite.macAlg);
            }
        }
        return super.decompose(algorithm);
    }
}
