package org.bouncycastle.asn1.x9;

import org.bouncycastle.crypto.params.ECDomainParameters;
import java.util.Vector;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.anssi.ANSSINamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECNamedCurves;

public class ECNamedCurveTable
{
    public static X9ECParameters getByName(final String s) {
        X9ECParameters x9ECParameters = X962NamedCurves.getByName(s);
        if (x9ECParameters == null) {
            x9ECParameters = SECNamedCurves.getByName(s);
        }
        if (x9ECParameters == null) {
            x9ECParameters = NISTNamedCurves.getByName(s);
        }
        if (x9ECParameters == null) {
            x9ECParameters = TeleTrusTNamedCurves.getByName(s);
        }
        if (x9ECParameters == null) {
            x9ECParameters = ANSSINamedCurves.getByName(s);
        }
        if (x9ECParameters == null) {
            x9ECParameters = fromDomainParameters(ECGOST3410NamedCurves.getByName(s));
        }
        if (x9ECParameters == null) {
            x9ECParameters = GMNamedCurves.getByName(s);
        }
        return x9ECParameters;
    }
    
    public static ASN1ObjectIdentifier getOID(final String s) {
        ASN1ObjectIdentifier asn1ObjectIdentifier = X962NamedCurves.getOID(s);
        if (asn1ObjectIdentifier == null) {
            asn1ObjectIdentifier = SECNamedCurves.getOID(s);
        }
        if (asn1ObjectIdentifier == null) {
            asn1ObjectIdentifier = NISTNamedCurves.getOID(s);
        }
        if (asn1ObjectIdentifier == null) {
            asn1ObjectIdentifier = TeleTrusTNamedCurves.getOID(s);
        }
        if (asn1ObjectIdentifier == null) {
            asn1ObjectIdentifier = ANSSINamedCurves.getOID(s);
        }
        if (asn1ObjectIdentifier == null) {
            asn1ObjectIdentifier = ECGOST3410NamedCurves.getOID(s);
        }
        if (asn1ObjectIdentifier == null) {
            asn1ObjectIdentifier = GMNamedCurves.getOID(s);
        }
        return asn1ObjectIdentifier;
    }
    
    public static String getName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        String s = X962NamedCurves.getName(asn1ObjectIdentifier);
        if (s == null) {
            s = SECNamedCurves.getName(asn1ObjectIdentifier);
        }
        if (s == null) {
            s = NISTNamedCurves.getName(asn1ObjectIdentifier);
        }
        if (s == null) {
            s = TeleTrusTNamedCurves.getName(asn1ObjectIdentifier);
        }
        if (s == null) {
            s = ANSSINamedCurves.getName(asn1ObjectIdentifier);
        }
        if (s == null) {
            s = ECGOST3410NamedCurves.getName(asn1ObjectIdentifier);
        }
        if (s == null) {
            s = GMNamedCurves.getName(asn1ObjectIdentifier);
        }
        return s;
    }
    
    public static X9ECParameters getByOID(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        X9ECParameters x9ECParameters = X962NamedCurves.getByOID(asn1ObjectIdentifier);
        if (x9ECParameters == null) {
            x9ECParameters = SECNamedCurves.getByOID(asn1ObjectIdentifier);
        }
        if (x9ECParameters == null) {
            x9ECParameters = TeleTrusTNamedCurves.getByOID(asn1ObjectIdentifier);
        }
        if (x9ECParameters == null) {
            x9ECParameters = ANSSINamedCurves.getByOID(asn1ObjectIdentifier);
        }
        if (x9ECParameters == null) {
            x9ECParameters = fromDomainParameters(ECGOST3410NamedCurves.getByOID(asn1ObjectIdentifier));
        }
        if (x9ECParameters == null) {
            x9ECParameters = GMNamedCurves.getByOID(asn1ObjectIdentifier);
        }
        return x9ECParameters;
    }
    
    public static Enumeration getNames() {
        final Vector vector = new Vector();
        addEnumeration(vector, X962NamedCurves.getNames());
        addEnumeration(vector, SECNamedCurves.getNames());
        addEnumeration(vector, NISTNamedCurves.getNames());
        addEnumeration(vector, TeleTrusTNamedCurves.getNames());
        addEnumeration(vector, ANSSINamedCurves.getNames());
        addEnumeration(vector, ECGOST3410NamedCurves.getNames());
        addEnumeration(vector, GMNamedCurves.getNames());
        return vector.elements();
    }
    
    private static void addEnumeration(final Vector vector, final Enumeration enumeration) {
        while (enumeration.hasMoreElements()) {
            vector.addElement(enumeration.nextElement());
        }
    }
    
    private static X9ECParameters fromDomainParameters(final ECDomainParameters ecDomainParameters) {
        return (ecDomainParameters == null) ? null : new X9ECParameters(ecDomainParameters.getCurve(), ecDomainParameters.getG(), ecDomainParameters.getN(), ecDomainParameters.getH(), ecDomainParameters.getSeed());
    }
}
