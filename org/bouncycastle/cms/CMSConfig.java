package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class CMSConfig
{
    public static void setSigningEncryptionAlgorithmMapping(final String s, final String s2) {
        CMSSignedHelper.INSTANCE.setSigningEncryptionAlgorithmMapping(new ASN1ObjectIdentifier(s), s2);
    }
    
    public static void setSigningDigestAlgorithmMapping(final String s, final String s2) {
        CMSSignedHelper.INSTANCE.setSigningDigestAlgorithmMapping(new ASN1ObjectIdentifier(s), s2);
    }
}
