package org.bouncycastle.pqc.jcajce.provider.xmss;

import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

class DigestUtil
{
    static Digest getDigest(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (asn1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha256)) {
            return new SHA256Digest();
        }
        if (asn1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha512)) {
            return new SHA512Digest();
        }
        if (asn1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake128)) {
            return new SHAKEDigest(128);
        }
        if (asn1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake256)) {
            return new SHAKEDigest(256);
        }
        throw new IllegalArgumentException("unrecognized digest OID: " + asn1ObjectIdentifier);
    }
    
    public static byte[] getDigestResult(final Digest digest) {
        final byte[] array = new byte[getDigestSize(digest)];
        if (digest instanceof Xof) {
            ((Xof)digest).doFinal(array, 0, array.length);
        }
        else {
            digest.doFinal(array, 0);
        }
        return array;
    }
    
    public static int getDigestSize(final Digest digest) {
        if (digest instanceof Xof) {
            return digest.getDigestSize() * 2;
        }
        return digest.getDigestSize();
    }
    
    public static String getXMSSDigestName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (asn1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha256)) {
            return "SHA256";
        }
        if (asn1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha512)) {
            return "SHA512";
        }
        if (asn1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake128)) {
            return "SHAKE128";
        }
        if (asn1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake256)) {
            return "SHAKE256";
        }
        throw new IllegalArgumentException("unrecognized digest OID: " + asn1ObjectIdentifier);
    }
}
