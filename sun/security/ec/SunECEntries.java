package sun.security.ec;

import java.util.Iterator;
import sun.security.util.NamedCurve;
import sun.security.util.CurveDB;
import java.util.regex.Pattern;
import java.util.Map;

final class SunECEntries
{
    private SunECEntries() {
    }
    
    static void putEntries(final Map<Object, Object> map, final boolean b) {
        map.put("KeyFactory.EC", "sun.security.ec.ECKeyFactory");
        map.put("Alg.Alias.KeyFactory.EllipticCurve", "EC");
        map.put("KeyFactory.EC ImplementedIn", "Software");
        map.put("AlgorithmParameters.EC", "sun.security.util.ECParameters");
        map.put("Alg.Alias.AlgorithmParameters.EllipticCurve", "EC");
        map.put("Alg.Alias.AlgorithmParameters.1.2.840.10045.2.1", "EC");
        map.put("AlgorithmParameters.EC KeySize", "256");
        map.put("AlgorithmParameters.EC ImplementedIn", "Software");
        int n = 1;
        final StringBuilder sb = new StringBuilder();
        final Pattern compile = Pattern.compile(",|\\[|\\]");
        for (final NamedCurve namedCurve : CurveDB.getSupportedCurves()) {
            if (n == 0) {
                sb.append("|");
            }
            else {
                n = 0;
            }
            sb.append("[");
            final String[] split = compile.split(namedCurve.getName());
            for (int length = split.length, i = 0; i < length; ++i) {
                sb.append(split[i].trim());
                sb.append(",");
            }
            sb.append(namedCurve.getObjectId());
            sb.append("]");
        }
        map.put("AlgorithmParameters.EC SupportedCurves", sb.toString());
        if (!b) {
            return;
        }
        map.put("Signature.NONEwithECDSA", "sun.security.ec.ECDSASignature$Raw");
        map.put("Signature.SHA1withECDSA", "sun.security.ec.ECDSASignature$SHA1");
        map.put("Alg.Alias.Signature.OID.1.2.840.10045.4.1", "SHA1withECDSA");
        map.put("Alg.Alias.Signature.1.2.840.10045.4.1", "SHA1withECDSA");
        map.put("Signature.SHA224withECDSA", "sun.security.ec.ECDSASignature$SHA224");
        map.put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.1", "SHA224withECDSA");
        map.put("Alg.Alias.Signature.1.2.840.10045.4.3.1", "SHA224withECDSA");
        map.put("Signature.SHA256withECDSA", "sun.security.ec.ECDSASignature$SHA256");
        map.put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.2", "SHA256withECDSA");
        map.put("Alg.Alias.Signature.1.2.840.10045.4.3.2", "SHA256withECDSA");
        map.put("Signature.SHA384withECDSA", "sun.security.ec.ECDSASignature$SHA384");
        map.put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.3", "SHA384withECDSA");
        map.put("Alg.Alias.Signature.1.2.840.10045.4.3.3", "SHA384withECDSA");
        map.put("Signature.SHA512withECDSA", "sun.security.ec.ECDSASignature$SHA512");
        map.put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.4", "SHA512withECDSA");
        map.put("Alg.Alias.Signature.1.2.840.10045.4.3.4", "SHA512withECDSA");
        final String s = "java.security.interfaces.ECPublicKey|java.security.interfaces.ECPrivateKey";
        map.put("Signature.NONEwithECDSA SupportedKeyClasses", s);
        map.put("Signature.SHA1withECDSA SupportedKeyClasses", s);
        map.put("Signature.SHA224withECDSA SupportedKeyClasses", s);
        map.put("Signature.SHA256withECDSA SupportedKeyClasses", s);
        map.put("Signature.SHA384withECDSA SupportedKeyClasses", s);
        map.put("Signature.SHA512withECDSA SupportedKeyClasses", s);
        map.put("Signature.SHA1withECDSA KeySize", "256");
        map.put("Signature.NONEwithECDSA ImplementedIn", "Software");
        map.put("Signature.SHA1withECDSA ImplementedIn", "Software");
        map.put("Signature.SHA224withECDSA ImplementedIn", "Software");
        map.put("Signature.SHA256withECDSA ImplementedIn", "Software");
        map.put("Signature.SHA384withECDSA ImplementedIn", "Software");
        map.put("Signature.SHA512withECDSA ImplementedIn", "Software");
        map.put("KeyPairGenerator.EC", "sun.security.ec.ECKeyPairGenerator");
        map.put("Alg.Alias.KeyPairGenerator.EllipticCurve", "EC");
        map.put("KeyPairGenerator.EC KeySize", "256");
        map.put("KeyPairGenerator.EC ImplementedIn", "Software");
        map.put("KeyAgreement.ECDH", "sun.security.ec.ECDHKeyAgreement");
        map.put("KeyAgreement.ECDH SupportedKeyClasses", s);
        map.put("KeyAgreement.ECDH ImplementedIn", "Software");
    }
}
