package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.crmf.PKIPublicationInfo;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class CMCPublicationInfo extends ASN1Object
{
    private final AlgorithmIdentifier hashAlg;
    private final ASN1Sequence certHashes;
    private final PKIPublicationInfo pubInfo;
    
    public CMCPublicationInfo(final AlgorithmIdentifier hashAlg, final byte[][] array, final PKIPublicationInfo pubInfo) {
        this.hashAlg = hashAlg;
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != array.length; ++i) {
            asn1EncodableVector.add(new DEROctetString(Arrays.clone(array[i])));
        }
        this.certHashes = new DERSequence(asn1EncodableVector);
        this.pubInfo = pubInfo;
    }
    
    private CMCPublicationInfo(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.hashAlg = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.certHashes = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(1));
        this.pubInfo = PKIPublicationInfo.getInstance(asn1Sequence.getObjectAt(2));
    }
    
    public static CMCPublicationInfo getInstance(final Object o) {
        if (o instanceof CMCPublicationInfo) {
            return (CMCPublicationInfo)o;
        }
        if (o != null) {
            return new CMCPublicationInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public AlgorithmIdentifier getHashAlg() {
        return this.hashAlg;
    }
    
    public byte[][] getCertHashes() {
        final byte[][] array = new byte[this.certHashes.size()][];
        for (int i = 0; i != array.length; ++i) {
            array[i] = Arrays.clone(ASN1OctetString.getInstance(this.certHashes.getObjectAt(i)).getOctets());
        }
        return array;
    }
    
    public PKIPublicationInfo getPubInfo() {
        return this.pubInfo;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.hashAlg);
        asn1EncodableVector.add(this.certHashes);
        asn1EncodableVector.add(this.pubInfo);
        return new DERSequence(asn1EncodableVector);
    }
}
