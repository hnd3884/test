package com.unboundid.util.ssl.cert;

import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum PublicKeyAlgorithmIdentifier
{
    RSA("1.2.840.113549.1.1.1", "RSA"), 
    DSA("1.2.840.10040.4.1", "DSA"), 
    DIFFIE_HELLMAN("1.2.840.10046.2.1", "DiffieHellman"), 
    EC("1.2.840.10045.2.1", "EC");
    
    private final OID oid;
    private final String name;
    
    private PublicKeyAlgorithmIdentifier(final String oidString, final String name) {
        this.name = name;
        this.oid = new OID(oidString);
    }
    
    public OID getOID() {
        return this.oid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static PublicKeyAlgorithmIdentifier forOID(final OID oid) {
        for (final PublicKeyAlgorithmIdentifier v : values()) {
            if (v.oid.equals(oid)) {
                return v;
            }
        }
        return null;
    }
    
    public static PublicKeyAlgorithmIdentifier forName(final String name) {
        final String preparedName = prepareName(name);
        for (final PublicKeyAlgorithmIdentifier v : values()) {
            if (v.name.equalsIgnoreCase(preparedName)) {
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
        final PublicKeyAlgorithmIdentifier id = forOID(oid);
        if (id == null) {
            return oid.toString();
        }
        return id.name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
