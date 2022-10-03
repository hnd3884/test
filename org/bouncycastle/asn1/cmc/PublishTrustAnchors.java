package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class PublishTrustAnchors extends ASN1Object
{
    private final ASN1Integer seqNumber;
    private final AlgorithmIdentifier hashAlgorithm;
    private final ASN1Sequence anchorHashes;
    
    public PublishTrustAnchors(final BigInteger bigInteger, final AlgorithmIdentifier hashAlgorithm, final byte[][] array) {
        this.seqNumber = new ASN1Integer(bigInteger);
        this.hashAlgorithm = hashAlgorithm;
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != array.length; ++i) {
            asn1EncodableVector.add(new DEROctetString(Arrays.clone(array[i])));
        }
        this.anchorHashes = new DERSequence(asn1EncodableVector);
    }
    
    private PublishTrustAnchors(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.seqNumber = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
        this.hashAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.anchorHashes = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(2));
    }
    
    public static PublishTrustAnchors getInstance(final Object o) {
        if (o instanceof PublishTrustAnchors) {
            return (PublishTrustAnchors)o;
        }
        if (o != null) {
            return new PublishTrustAnchors(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public BigInteger getSeqNumber() {
        return this.seqNumber.getValue();
    }
    
    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    public byte[][] getAnchorHashes() {
        final byte[][] array = new byte[this.anchorHashes.size()][];
        for (int i = 0; i != array.length; ++i) {
            array[i] = Arrays.clone(ASN1OctetString.getInstance(this.anchorHashes.getObjectAt(i)).getOctets());
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.seqNumber);
        asn1EncodableVector.add(this.hashAlgorithm);
        asn1EncodableVector.add(this.anchorHashes);
        return new DERSequence(asn1EncodableVector);
    }
}
