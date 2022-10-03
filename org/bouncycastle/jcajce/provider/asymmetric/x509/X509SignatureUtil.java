package org.bouncycastle.jcajce.provider.asymmetric.x509;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
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
                return getDigestAlgName((ASN1ObjectIdentifier)ASN1Sequence.getInstance(parameters).getObjectAt(0)) + "withECDSA";
            }
        }
        final Provider provider = Security.getProvider("BC");
        if (provider != null) {
            final String property = provider.getProperty("Alg.Alias.Signature." + algorithmIdentifier.getAlgorithm().getId());
            if (property != null) {
                return property;
            }
        }
        final Provider[] providers = Security.getProviders();
        for (int i = 0; i != providers.length; ++i) {
            final String property2 = providers[i].getProperty("Alg.Alias.Signature." + algorithmIdentifier.getAlgorithm().getId());
            if (property2 != null) {
                return property2;
            }
        }
        return algorithmIdentifier.getAlgorithm().getId();
    }
    
    private static String getDigestAlgName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String digestName = MessageDigestUtils.getDigestName(asn1ObjectIdentifier);
        final int index = digestName.indexOf(45);
        if (index > 0 && !digestName.startsWith("SHA3")) {
            return digestName.substring(0, index) + digestName.substring(index + 1);
        }
        return MessageDigestUtils.getDigestName(asn1ObjectIdentifier);
    }
    
    static {
        derNull = DERNull.INSTANCE;
    }
}
