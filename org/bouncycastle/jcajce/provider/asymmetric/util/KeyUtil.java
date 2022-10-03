package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KeyUtil
{
    public static byte[] getEncodedSubjectPublicKeyInfo(final AlgorithmIdentifier algorithmIdentifier, final ASN1Encodable asn1Encodable) {
        try {
            return getEncodedSubjectPublicKeyInfo(new SubjectPublicKeyInfo(algorithmIdentifier, asn1Encodable));
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public static byte[] getEncodedSubjectPublicKeyInfo(final AlgorithmIdentifier algorithmIdentifier, final byte[] array) {
        try {
            return getEncodedSubjectPublicKeyInfo(new SubjectPublicKeyInfo(algorithmIdentifier, array));
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public static byte[] getEncodedSubjectPublicKeyInfo(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        try {
            return subjectPublicKeyInfo.getEncoded("DER");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public static byte[] getEncodedPrivateKeyInfo(final AlgorithmIdentifier algorithmIdentifier, final ASN1Encodable asn1Encodable) {
        try {
            return getEncodedPrivateKeyInfo(new PrivateKeyInfo(algorithmIdentifier, asn1Encodable.toASN1Primitive()));
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public static byte[] getEncodedPrivateKeyInfo(final PrivateKeyInfo privateKeyInfo) {
        try {
            return privateKeyInfo.getEncoded("DER");
        }
        catch (final Exception ex) {
            return null;
        }
    }
}
