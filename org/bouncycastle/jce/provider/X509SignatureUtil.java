package org.bouncycastle.jce.provider;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.io.IOException;
import java.security.SignatureException;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import java.security.Signature;
import org.bouncycastle.asn1.ASN1Null;

class X509SignatureUtil
{
    private static final ASN1Null derNull;
    
    static void setSignatureParameters(final Signature signature, final ASN1Encodable asn1Encodable) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (asn1Encodable != null && !X509SignatureUtil.derNull.equals(asn1Encodable)) {
            final AlgorithmParameters instance = AlgorithmParameters.getInstance(signature.getAlgorithm(), signature.getProvider());
            try {
                instance.init(asn1Encodable.toASN1Primitive().getEncoded());
            }
            catch (final IOException ex) {
                throw new SignatureException("IOException decoding parameters: " + ex.getMessage());
            }
            if (signature.getAlgorithm().endsWith("MGF1")) {
                try {
                    signature.setParameter(instance.getParameterSpec(PSSParameterSpec.class));
                }
                catch (final GeneralSecurityException ex2) {
                    throw new SignatureException("Exception extracting parameters: " + ex2.getMessage());
                }
            }
        }
    }
    
    static String getSignatureName(final AlgorithmIdentifier algorithmIdentifier) {
        final ASN1Encodable parameters = algorithmIdentifier.getParameters();
        if (parameters != null && !X509SignatureUtil.derNull.equals(parameters)) {
            if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
                return getDigestAlgName(RSASSAPSSparams.getInstance(parameters).getHashAlgorithm().getAlgorithm()) + "withRSAandMGF1";
            }
            if (algorithmIdentifier.getAlgorithm().equals(X9ObjectIdentifiers.ecdsa_with_SHA2)) {
                return getDigestAlgName(ASN1ObjectIdentifier.getInstance(ASN1Sequence.getInstance(parameters).getObjectAt(0))) + "withECDSA";
            }
        }
        return algorithmIdentifier.getAlgorithm().getId();
    }
    
    private static String getDigestAlgName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (PKCSObjectIdentifiers.md5.equals(asn1ObjectIdentifier)) {
            return "MD5";
        }
        if (OIWObjectIdentifiers.idSHA1.equals(asn1ObjectIdentifier)) {
            return "SHA1";
        }
        if (NISTObjectIdentifiers.id_sha224.equals(asn1ObjectIdentifier)) {
            return "SHA224";
        }
        if (NISTObjectIdentifiers.id_sha256.equals(asn1ObjectIdentifier)) {
            return "SHA256";
        }
        if (NISTObjectIdentifiers.id_sha384.equals(asn1ObjectIdentifier)) {
            return "SHA384";
        }
        if (NISTObjectIdentifiers.id_sha512.equals(asn1ObjectIdentifier)) {
            return "SHA512";
        }
        if (TeleTrusTObjectIdentifiers.ripemd128.equals(asn1ObjectIdentifier)) {
            return "RIPEMD128";
        }
        if (TeleTrusTObjectIdentifiers.ripemd160.equals(asn1ObjectIdentifier)) {
            return "RIPEMD160";
        }
        if (TeleTrusTObjectIdentifiers.ripemd256.equals(asn1ObjectIdentifier)) {
            return "RIPEMD256";
        }
        if (CryptoProObjectIdentifiers.gostR3411.equals(asn1ObjectIdentifier)) {
            return "GOST3411";
        }
        return asn1ObjectIdentifier.getId();
    }
    
    static {
        derNull = DERNull.INSTANCE;
    }
}
