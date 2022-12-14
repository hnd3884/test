package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1Object;

public class Request extends ASN1Object
{
    CertID reqCert;
    Extensions singleRequestExtensions;
    
    public Request(final CertID reqCert, final Extensions singleRequestExtensions) {
        this.reqCert = reqCert;
        this.singleRequestExtensions = singleRequestExtensions;
    }
    
    private Request(final ASN1Sequence asn1Sequence) {
        this.reqCert = CertID.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() == 2) {
            this.singleRequestExtensions = Extensions.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(1), true);
        }
    }
    
    public static Request getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static Request getInstance(final Object o) {
        if (o instanceof Request) {
            return (Request)o;
        }
        if (o != null) {
            return new Request(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public CertID getReqCert() {
        return this.reqCert;
    }
    
    public Extensions getSingleRequestExtensions() {
        return this.singleRequestExtensions;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.reqCert);
        if (this.singleRequestExtensions != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.singleRequestExtensions));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
