package com.unboundid.util.ssl.cert;

import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum NamedCurve
{
    BRAINPOOLP256R1("1.3.36.3.3.2.8.1.1.7", "brainpoolP256r1"), 
    BRAINPOOLP384R1("1.3.36.3.3.2.8.1.1.11", "brainpoolP384r1"), 
    BRAINPOOLP512R1("1.3.36.3.3.2.8.1.1.13", "brainpoolP512r1"), 
    SECP160K1("1.3.132.0.9", "secP160k1"), 
    SECP160R1("1.3.132.0.8", "secP160r1"), 
    SECP160R2("1.3.132.0.30", "secP160r2"), 
    SECP192K1("1.3.132.0.31", "secP192k1"), 
    SECP192R1("1.2.840.10045.3.1.1", "secP192r1"), 
    SECP224K1("1.3.132.0.32", "secP224k1"), 
    SECP224R1("1.3.132.0.33", "secP224r1"), 
    SECP256K1("1.3.132.0.10", "secP256k1"), 
    SECP256R1("1.2.840.10045.3.1.7", "secP256r1"), 
    SECP384R1("1.3.132.0.34", "secP384r1"), 
    SECP521R1("1.3.132.0.35", "secP521r1"), 
    SECT163K1("1.3.132.0.1", "secT163k1"), 
    SECT163R2("1.3.132.0.15", "secT163r2"), 
    SECT233K1("1.3.132.0.26", "secT233k1"), 
    SECT233R1("1.3.132.0.27", "secT233r1"), 
    SECT283K1("1.3.132.0.16", "secT283k1"), 
    SECT283R1("1.3.132.0.17", "secT283r1"), 
    SECT409K1("1.3.132.0.36", "secT409k1"), 
    SECT409R1("1.3.132.0.37", "secT409r1"), 
    SECT571K1("1.3.132.0.38", "secT571k1"), 
    SECT571R1("1.3.132.0.39", "secT571r1");
    
    private final OID oid;
    private final String name;
    
    private NamedCurve(final String oidString, final String name) {
        this.name = name;
        this.oid = new OID(oidString);
    }
    
    public OID getOID() {
        return this.oid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static NamedCurve forOID(final OID oid) {
        for (final NamedCurve curve : values()) {
            if (curve.oid.equals(oid)) {
                return curve;
            }
        }
        return null;
    }
    
    public static String getNameOrOID(final OID oid) {
        final NamedCurve curve = forOID(oid);
        if (curve == null) {
            return oid.toString();
        }
        return curve.name;
    }
    
    public static NamedCurve forName(final String name) {
        for (final NamedCurve namedCurve : values()) {
            if (namedCurve.name.equalsIgnoreCase(name) || namedCurve.name().equalsIgnoreCase(name)) {
                return namedCurve;
            }
        }
        return null;
    }
}
