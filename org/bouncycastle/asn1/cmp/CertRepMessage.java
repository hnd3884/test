package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CertRepMessage extends ASN1Object
{
    private ASN1Sequence caPubs;
    private ASN1Sequence response;
    
    private CertRepMessage(final ASN1Sequence asn1Sequence) {
        int n = 0;
        if (asn1Sequence.size() > 1) {
            this.caPubs = ASN1Sequence.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(n++), true);
        }
        this.response = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(n));
    }
    
    public static CertRepMessage getInstance(final Object o) {
        if (o instanceof CertRepMessage) {
            return (CertRepMessage)o;
        }
        if (o != null) {
            return new CertRepMessage(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public CertRepMessage(final CMPCertificate[] array, final CertResponse[] array2) {
        if (array2 == null) {
            throw new IllegalArgumentException("'response' cannot be null");
        }
        if (array != null) {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            for (int i = 0; i < array.length; ++i) {
                asn1EncodableVector.add(array[i]);
            }
            this.caPubs = new DERSequence(asn1EncodableVector);
        }
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        for (int j = 0; j < array2.length; ++j) {
            asn1EncodableVector2.add(array2[j]);
        }
        this.response = new DERSequence(asn1EncodableVector2);
    }
    
    public CMPCertificate[] getCaPubs() {
        if (this.caPubs == null) {
            return null;
        }
        final CMPCertificate[] array = new CMPCertificate[this.caPubs.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = CMPCertificate.getInstance(this.caPubs.getObjectAt(i));
        }
        return array;
    }
    
    public CertResponse[] getResponse() {
        final CertResponse[] array = new CertResponse[this.response.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = CertResponse.getInstance(this.response.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.caPubs != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, this.caPubs));
        }
        asn1EncodableVector.add(this.response);
        return new DERSequence(asn1EncodableVector);
    }
}
