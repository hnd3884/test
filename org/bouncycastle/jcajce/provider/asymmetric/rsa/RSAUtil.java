package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Arrays;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import java.security.interfaces.RSAPublicKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class RSAUtil
{
    public static final ASN1ObjectIdentifier[] rsaOids;
    
    public static boolean isRsaOid(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        for (int i = 0; i != RSAUtil.rsaOids.length; ++i) {
            if (asn1ObjectIdentifier.equals(RSAUtil.rsaOids[i])) {
                return true;
            }
        }
        return false;
    }
    
    static RSAKeyParameters generatePublicKeyParameter(final RSAPublicKey rsaPublicKey) {
        return new RSAKeyParameters(false, rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
    }
    
    static RSAKeyParameters generatePrivateKeyParameter(final RSAPrivateKey rsaPrivateKey) {
        if (rsaPrivateKey instanceof RSAPrivateCrtKey) {
            final RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey)rsaPrivateKey;
            return new RSAPrivateCrtKeyParameters(rsaPrivateCrtKey.getModulus(), rsaPrivateCrtKey.getPublicExponent(), rsaPrivateCrtKey.getPrivateExponent(), rsaPrivateCrtKey.getPrimeP(), rsaPrivateCrtKey.getPrimeQ(), rsaPrivateCrtKey.getPrimeExponentP(), rsaPrivateCrtKey.getPrimeExponentQ(), rsaPrivateCrtKey.getCrtCoefficient());
        }
        return new RSAKeyParameters(true, rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
    }
    
    static String generateKeyFingerprint(final BigInteger bigInteger, final BigInteger bigInteger2) {
        return new Fingerprint(Arrays.concatenate(bigInteger.toByteArray(), bigInteger2.toByteArray())).toString();
    }
    
    static {
        rsaOids = new ASN1ObjectIdentifier[] { PKCSObjectIdentifiers.rsaEncryption, X509ObjectIdentifiers.id_ea_rsa, PKCSObjectIdentifiers.id_RSAES_OAEP, PKCSObjectIdentifiers.id_RSASSA_PSS };
    }
}
