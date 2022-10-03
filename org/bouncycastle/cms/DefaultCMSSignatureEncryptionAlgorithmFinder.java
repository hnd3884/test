package org.bouncycastle.cms;

import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import java.util.HashSet;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.Set;

public class DefaultCMSSignatureEncryptionAlgorithmFinder implements CMSSignatureEncryptionAlgorithmFinder
{
    private static final Set RSA_PKCS1d5;
    
    public AlgorithmIdentifier findEncryptionAlgorithm(final AlgorithmIdentifier algorithmIdentifier) {
        if (DefaultCMSSignatureEncryptionAlgorithmFinder.RSA_PKCS1d5.contains(algorithmIdentifier.getAlgorithm())) {
            return new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE);
        }
        return algorithmIdentifier;
    }
    
    static {
        (RSA_PKCS1d5 = new HashSet()).add(PKCSObjectIdentifiers.md2WithRSAEncryption);
        DefaultCMSSignatureEncryptionAlgorithmFinder.RSA_PKCS1d5.add(PKCSObjectIdentifiers.md4WithRSAEncryption);
        DefaultCMSSignatureEncryptionAlgorithmFinder.RSA_PKCS1d5.add(PKCSObjectIdentifiers.md5WithRSAEncryption);
        DefaultCMSSignatureEncryptionAlgorithmFinder.RSA_PKCS1d5.add(PKCSObjectIdentifiers.sha1WithRSAEncryption);
        DefaultCMSSignatureEncryptionAlgorithmFinder.RSA_PKCS1d5.add(OIWObjectIdentifiers.md4WithRSAEncryption);
        DefaultCMSSignatureEncryptionAlgorithmFinder.RSA_PKCS1d5.add(OIWObjectIdentifiers.md4WithRSA);
        DefaultCMSSignatureEncryptionAlgorithmFinder.RSA_PKCS1d5.add(OIWObjectIdentifiers.md5WithRSA);
        DefaultCMSSignatureEncryptionAlgorithmFinder.RSA_PKCS1d5.add(OIWObjectIdentifiers.sha1WithRSA);
        DefaultCMSSignatureEncryptionAlgorithmFinder.RSA_PKCS1d5.add(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        DefaultCMSSignatureEncryptionAlgorithmFinder.RSA_PKCS1d5.add(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        DefaultCMSSignatureEncryptionAlgorithmFinder.RSA_PKCS1d5.add(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
    }
}
