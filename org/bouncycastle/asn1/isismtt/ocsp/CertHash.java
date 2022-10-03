package org.bouncycastle.asn1.isismtt.ocsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class CertHash extends ASN1Object
{
    private AlgorithmIdentifier hashAlgorithm;
    private byte[] certificateHash;
    
    public static CertHash getInstance(final Object o) {
        if (o == null || o instanceof CertHash) {
            return (CertHash)o;
        }
        if (o instanceof ASN1Sequence) {
            return new CertHash((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    private CertHash(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.hashAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.certificateHash = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(1)).getOctets();
    }
    
    public CertHash(final AlgorithmIdentifier hashAlgorithm, final byte[] array) {
        this.hashAlgorithm = hashAlgorithm;
        System.arraycopy(array, 0, this.certificateHash = new byte[array.length], 0, array.length);
    }
    
    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    public byte[] getCertificateHash() {
        return this.certificateHash;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.hashAlgorithm);
        asn1EncodableVector.add(new DEROctetString(this.certificateHash));
        return new DERSequence(asn1EncodableVector);
    }
}
