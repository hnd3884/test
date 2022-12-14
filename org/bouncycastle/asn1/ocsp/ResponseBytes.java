package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class ResponseBytes extends ASN1Object
{
    ASN1ObjectIdentifier responseType;
    ASN1OctetString response;
    
    public ResponseBytes(final ASN1ObjectIdentifier responseType, final ASN1OctetString response) {
        this.responseType = responseType;
        this.response = response;
    }
    
    @Deprecated
    public ResponseBytes(final ASN1Sequence asn1Sequence) {
        this.responseType = (ASN1ObjectIdentifier)asn1Sequence.getObjectAt(0);
        this.response = (ASN1OctetString)asn1Sequence.getObjectAt(1);
    }
    
    public static ResponseBytes getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static ResponseBytes getInstance(final Object o) {
        if (o instanceof ResponseBytes) {
            return (ResponseBytes)o;
        }
        if (o != null) {
            return new ResponseBytes(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1ObjectIdentifier getResponseType() {
        return this.responseType;
    }
    
    public ASN1OctetString getResponse() {
        return this.response;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.responseType);
        asn1EncodableVector.add(this.response);
        return new DERSequence(asn1EncodableVector);
    }
}
