package org.bouncycastle.eac.operator.jcajce;

import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Hashtable;

abstract class EACHelper
{
    private static final Hashtable sigNames;
    
    public Signature getSignature(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws NoSuchProviderException, NoSuchAlgorithmException {
        return this.createSignature(EACHelper.sigNames.get(asn1ObjectIdentifier));
    }
    
    protected abstract Signature createSignature(final String p0) throws NoSuchProviderException, NoSuchAlgorithmException;
    
    static {
        (sigNames = new Hashtable()).put(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, "SHA1withRSA");
        EACHelper.sigNames.put(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, "SHA256withRSA");
        EACHelper.sigNames.put(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, "SHA1withRSAandMGF1");
        EACHelper.sigNames.put(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, "SHA256withRSAandMGF1");
        EACHelper.sigNames.put(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_512, "SHA512withRSA");
        EACHelper.sigNames.put(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_512, "SHA512withRSAandMGF1");
        EACHelper.sigNames.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1withECDSA");
        EACHelper.sigNames.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224withECDSA");
        EACHelper.sigNames.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256withECDSA");
        EACHelper.sigNames.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384withECDSA");
        EACHelper.sigNames.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512withECDSA");
    }
}
