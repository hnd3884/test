package org.bouncycastle.asn1.cms.ecc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class ECCCMSSharedInfo extends ASN1Object
{
    private final AlgorithmIdentifier keyInfo;
    private final byte[] entityUInfo;
    private final byte[] suppPubInfo;
    
    public ECCCMSSharedInfo(final AlgorithmIdentifier keyInfo, final byte[] array, final byte[] array2) {
        this.keyInfo = keyInfo;
        this.entityUInfo = Arrays.clone(array);
        this.suppPubInfo = Arrays.clone(array2);
    }
    
    public ECCCMSSharedInfo(final AlgorithmIdentifier keyInfo, final byte[] array) {
        this.keyInfo = keyInfo;
        this.entityUInfo = null;
        this.suppPubInfo = Arrays.clone(array);
    }
    
    private ECCCMSSharedInfo(final ASN1Sequence asn1Sequence) {
        this.keyInfo = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() == 2) {
            this.entityUInfo = null;
            this.suppPubInfo = ASN1OctetString.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(1), true).getOctets();
        }
        else {
            this.entityUInfo = ASN1OctetString.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(1), true).getOctets();
            this.suppPubInfo = ASN1OctetString.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(2), true).getOctets();
        }
    }
    
    public static ECCCMSSharedInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static ECCCMSSharedInfo getInstance(final Object o) {
        if (o instanceof ECCCMSSharedInfo) {
            return (ECCCMSSharedInfo)o;
        }
        if (o != null) {
            return new ECCCMSSharedInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.keyInfo);
        if (this.entityUInfo != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, new DEROctetString(this.entityUInfo)));
        }
        asn1EncodableVector.add(new DERTaggedObject(true, 2, new DEROctetString(this.suppPubInfo)));
        return new DERSequence(asn1EncodableVector);
    }
}
