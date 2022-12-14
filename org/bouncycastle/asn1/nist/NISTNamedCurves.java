package org.bouncycastle.asn1.nist;

import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import java.util.Enumeration;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Hashtable;

public class NISTNamedCurves
{
    static final Hashtable objIds;
    static final Hashtable names;
    
    static void defineCurve(final String s, final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        NISTNamedCurves.objIds.put(s, asn1ObjectIdentifier);
        NISTNamedCurves.names.put(asn1ObjectIdentifier, s);
    }
    
    public static X9ECParameters getByName(final String s) {
        final ASN1ObjectIdentifier asn1ObjectIdentifier = NISTNamedCurves.objIds.get(Strings.toUpperCase(s));
        if (asn1ObjectIdentifier != null) {
            return getByOID(asn1ObjectIdentifier);
        }
        return null;
    }
    
    public static X9ECParameters getByOID(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return SECNamedCurves.getByOID(asn1ObjectIdentifier);
    }
    
    public static ASN1ObjectIdentifier getOID(final String s) {
        return NISTNamedCurves.objIds.get(Strings.toUpperCase(s));
    }
    
    public static String getName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return NISTNamedCurves.names.get(asn1ObjectIdentifier);
    }
    
    public static Enumeration getNames() {
        return NISTNamedCurves.objIds.keys();
    }
    
    static {
        objIds = new Hashtable();
        names = new Hashtable();
        defineCurve("B-571", SECObjectIdentifiers.sect571r1);
        defineCurve("B-409", SECObjectIdentifiers.sect409r1);
        defineCurve("B-283", SECObjectIdentifiers.sect283r1);
        defineCurve("B-233", SECObjectIdentifiers.sect233r1);
        defineCurve("B-163", SECObjectIdentifiers.sect163r2);
        defineCurve("K-571", SECObjectIdentifiers.sect571k1);
        defineCurve("K-409", SECObjectIdentifiers.sect409k1);
        defineCurve("K-283", SECObjectIdentifiers.sect283k1);
        defineCurve("K-233", SECObjectIdentifiers.sect233k1);
        defineCurve("K-163", SECObjectIdentifiers.sect163k1);
        defineCurve("P-521", SECObjectIdentifiers.secp521r1);
        defineCurve("P-384", SECObjectIdentifiers.secp384r1);
        defineCurve("P-256", SECObjectIdentifiers.secp256r1);
        defineCurve("P-224", SECObjectIdentifiers.secp224r1);
        defineCurve("P-192", SECObjectIdentifiers.secp192r1);
    }
}
