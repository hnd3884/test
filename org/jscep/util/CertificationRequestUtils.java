package org.jscep.util;

import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.io.IOException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import java.security.PublicKey;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public final class CertificationRequestUtils
{
    private CertificationRequestUtils() {
    }
    
    public static PublicKey getPublicKey(final PKCS10CertificationRequest csr) throws IOException {
        final SubjectPublicKeyInfo pkInfo = csr.getSubjectPublicKeyInfo();
        final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        return converter.getPublicKey(pkInfo);
    }
    
    public static String getChallengePassword(final PKCS10CertificationRequest csr) {
        final Attribute[] attributes;
        final Attribute[] attrs = attributes = csr.getAttributes();
        for (final Attribute attr : attributes) {
            if (attr.getAttrType().equals((Object)PKCSObjectIdentifiers.pkcs_9_at_challengePassword)) {
                final ASN1String challengePassword = (ASN1String)attr.getAttrValues().getObjectAt(0);
                return challengePassword.getString();
            }
        }
        return null;
    }
}
