package org.bouncycastle.jcajce.provider.asymmetric.ec;

import org.bouncycastle.math.ec.ECCurve;
import java.math.BigInteger;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.asn1.x9.X962Parameters;
import java.security.spec.ECParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import java.security.spec.ECGenParameterSpec;
import java.security.InvalidKeyException;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.PublicKey;

class ECUtils
{
    static AsymmetricKeyParameter generatePublicKeyParameter(final PublicKey publicKey) throws InvalidKeyException {
        return (publicKey instanceof BCECPublicKey) ? ((BCECPublicKey)publicKey).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(publicKey);
    }
    
    static X9ECParameters getDomainParametersFromGenSpec(final ECGenParameterSpec ecGenParameterSpec) {
        return getDomainParametersFromName(ecGenParameterSpec.getName());
    }
    
    static X9ECParameters getDomainParametersFromName(String substring) {
        X9ECParameters x9ECParameters;
        try {
            if (substring.charAt(0) >= '0' && substring.charAt(0) <= '2') {
                x9ECParameters = ECUtil.getNamedCurveByOid(new ASN1ObjectIdentifier(substring));
            }
            else if (substring.indexOf(32) > 0) {
                substring = substring.substring(substring.indexOf(32) + 1);
                x9ECParameters = ECUtil.getNamedCurveByName(substring);
            }
            else {
                x9ECParameters = ECUtil.getNamedCurveByName(substring);
            }
        }
        catch (final IllegalArgumentException ex) {
            x9ECParameters = ECUtil.getNamedCurveByName(substring);
        }
        return x9ECParameters;
    }
    
    static X962Parameters getDomainParametersFromName(final ECParameterSpec ecParameterSpec, final boolean b) {
        X962Parameters x962Parameters;
        if (ecParameterSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier namedCurveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)ecParameterSpec).getName());
            if (namedCurveOid == null) {
                namedCurveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec)ecParameterSpec).getName());
            }
            x962Parameters = new X962Parameters(namedCurveOid);
        }
        else if (ecParameterSpec == null) {
            x962Parameters = new X962Parameters(DERNull.INSTANCE);
        }
        else {
            final ECCurve convertCurve = EC5Util.convertCurve(ecParameterSpec.getCurve());
            x962Parameters = new X962Parameters(new X9ECParameters(convertCurve, EC5Util.convertPoint(convertCurve, ecParameterSpec.getGenerator(), b), ecParameterSpec.getOrder(), BigInteger.valueOf(ecParameterSpec.getCofactor()), ecParameterSpec.getCurve().getSeed()));
        }
        return x962Parameters;
    }
}
