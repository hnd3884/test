package org.bouncycastle.jce;

import java.util.Enumeration;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

public class ECGOST3410NamedCurveTable
{
    public static ECNamedCurveParameterSpec getParameterSpec(final String s) {
        ECDomainParameters ecDomainParameters = ECGOST3410NamedCurves.getByName(s);
        if (ecDomainParameters == null) {
            try {
                ecDomainParameters = ECGOST3410NamedCurves.getByOID(new ASN1ObjectIdentifier(s));
            }
            catch (final IllegalArgumentException ex) {
                return null;
            }
        }
        if (ecDomainParameters == null) {
            return null;
        }
        return new ECNamedCurveParameterSpec(s, ecDomainParameters.getCurve(), ecDomainParameters.getG(), ecDomainParameters.getN(), ecDomainParameters.getH(), ecDomainParameters.getSeed());
    }
    
    public static Enumeration getNames() {
        return ECGOST3410NamedCurves.getNames();
    }
}
