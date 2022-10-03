package com.unboundid.util.ssl.cert;

import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum SignatureAlgorithmIdentifier
{
    MD2_WITH_RSA("1.2.840.113549.1.1.2", "MD2withRSA", "MD2 with RSA"), 
    MD5_WITH_RSA("1.2.840.113549.1.1.4", "MD5withRSA", "MD5 with RSA"), 
    SHA_1_WITH_RSA("1.2.840.113549.1.1.5", "SHA1withRSA", "SHA-1 with RSA"), 
    SHA_224_WITH_RSA("1.2.840.113549.1.1.14", "SHA224withRSA", "SHA-224 with RSA"), 
    SHA_256_WITH_RSA("1.2.840.113549.1.1.11", "SHA256withRSA", "SHA-256 with RSA"), 
    SHA_384_WITH_RSA("1.2.840.113549.1.1.12", "SHA384withRSA", "SHA-384 with RSA"), 
    SHA_512_WITH_RSA("1.2.840.113549.1.1.13", "SHA512withRSA", "SHA-512 with RSA"), 
    SHA_1_WITH_DSA("1.2.840.10040.4.3", "SHA1withDSA", "SHA-1 with DSA"), 
    SHA_224_WITH_DSA("2.16.840.1.101.3.4.3.1", "SHA224withDSA", "SHA-224 with DSA"), 
    SHA_256_WITH_DSA("2.16.840.1.101.3.4.3.2", "SHA256withDSA", "SHA-256 with DSA"), 
    SHA_1_WITH_ECDSA("1.2.840.10045.4.1", "SHA1withECDSA", "SHA-1 with ECDSA"), 
    SHA_224_WITH_ECDSA("1.2.840.10045.4.3.1", "SHA224withECDSA", "SHA-224 with ECDSA"), 
    SHA_256_WITH_ECDSA("1.2.840.10045.4.3.2", "SHA256withECDSA", "SHA-256 with ECDSA"), 
    SHA_384_WITH_ECDSA("1.2.840.10045.4.3.3", "SHA384withECDSA", "SHA-384 with ECDSA"), 
    SHA_512_WITH_ECDSA("1.2.840.10045.4.3.4", "SHA512withECDSA", "SHA-512 with ECDSA");
    
    private final OID oid;
    private final String javaName;
    private final String userFriendlyName;
    
    private SignatureAlgorithmIdentifier(final String oidString, final String javaName, final String userFriendlyName) {
        this.javaName = javaName;
        this.userFriendlyName = userFriendlyName;
        this.oid = new OID(oidString);
    }
    
    public OID getOID() {
        return this.oid;
    }
    
    public String getJavaName() {
        return this.javaName;
    }
    
    public String getUserFriendlyName() {
        return this.userFriendlyName;
    }
    
    public static SignatureAlgorithmIdentifier forOID(final OID oid) {
        for (final SignatureAlgorithmIdentifier v : values()) {
            if (v.oid.equals(oid)) {
                return v;
            }
        }
        return null;
    }
    
    public static SignatureAlgorithmIdentifier forName(final String name) {
        final String preparedName = prepareName(name);
        for (final SignatureAlgorithmIdentifier v : values()) {
            if (v.javaName.equalsIgnoreCase(preparedName)) {
                return v;
            }
        }
        return null;
    }
    
    private static String prepareName(final String name) {
        final StringBuilder buffer = new StringBuilder(name.length());
        for (final char c : name.toCharArray()) {
            switch (c) {
                case ' ':
                case '-':
                case '_': {
                    break;
                }
                default: {
                    buffer.append(c);
                    break;
                }
            }
        }
        return buffer.toString();
    }
    
    public static String getNameOrOID(final OID oid) {
        final SignatureAlgorithmIdentifier id = forOID(oid);
        if (id == null) {
            return oid.toString();
        }
        return id.userFriendlyName;
    }
    
    @Override
    public String toString() {
        return this.userFriendlyName;
    }
}
