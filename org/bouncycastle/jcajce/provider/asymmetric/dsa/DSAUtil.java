package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Arrays;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import java.security.interfaces.DSAPrivateKey;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.interfaces.DSAPublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.DSAParameters;
import java.security.interfaces.DSAParams;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class DSAUtil
{
    public static final ASN1ObjectIdentifier[] dsaOids;
    
    public static boolean isDsaOid(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        for (int i = 0; i != DSAUtil.dsaOids.length; ++i) {
            if (asn1ObjectIdentifier.equals(DSAUtil.dsaOids[i])) {
                return true;
            }
        }
        return false;
    }
    
    static DSAParameters toDSAParameters(final DSAParams dsaParams) {
        if (dsaParams != null) {
            return new DSAParameters(dsaParams.getP(), dsaParams.getQ(), dsaParams.getG());
        }
        return null;
    }
    
    public static AsymmetricKeyParameter generatePublicKeyParameter(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCDSAPublicKey) {
            return ((BCDSAPublicKey)publicKey).engineGetKeyParameters();
        }
        if (publicKey instanceof DSAPublicKey) {
            return new BCDSAPublicKey((DSAPublicKey)publicKey).engineGetKeyParameters();
        }
        try {
            return new BCDSAPublicKey(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded())).engineGetKeyParameters();
        }
        catch (final Exception ex) {
            throw new InvalidKeyException("can't identify DSA public key: " + publicKey.getClass().getName());
        }
    }
    
    public static AsymmetricKeyParameter generatePrivateKeyParameter(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof DSAPrivateKey) {
            final DSAPrivateKey dsaPrivateKey = (DSAPrivateKey)privateKey;
            return new DSAPrivateKeyParameters(dsaPrivateKey.getX(), new DSAParameters(dsaPrivateKey.getParams().getP(), dsaPrivateKey.getParams().getQ(), dsaPrivateKey.getParams().getG()));
        }
        throw new InvalidKeyException("can't identify DSA private key.");
    }
    
    static String generateKeyFingerprint(final BigInteger bigInteger, final DSAParams dsaParams) {
        return new Fingerprint(Arrays.concatenate(bigInteger.toByteArray(), dsaParams.getP().toByteArray(), dsaParams.getQ().toByteArray(), dsaParams.getG().toByteArray())).toString();
    }
    
    static {
        dsaOids = new ASN1ObjectIdentifier[] { X9ObjectIdentifiers.id_dsa, OIWObjectIdentifiers.dsaWithSHA1, X9ObjectIdentifiers.id_dsa_with_sha1 };
    }
}
