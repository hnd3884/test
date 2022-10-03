package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class ResponseData extends ASN1Object
{
    private static final ASN1Integer V1;
    private boolean versionPresent;
    private ASN1Integer version;
    private ResponderID responderID;
    private ASN1GeneralizedTime producedAt;
    private ASN1Sequence responses;
    private Extensions responseExtensions;
    
    public ResponseData(final ASN1Integer version, final ResponderID responderID, final ASN1GeneralizedTime producedAt, final ASN1Sequence responses, final Extensions responseExtensions) {
        this.version = version;
        this.responderID = responderID;
        this.producedAt = producedAt;
        this.responses = responses;
        this.responseExtensions = responseExtensions;
    }
    
    @Deprecated
    public ResponseData(final ResponderID responderID, final ASN1GeneralizedTime asn1GeneralizedTime, final ASN1Sequence asn1Sequence, final X509Extensions x509Extensions) {
        this(ResponseData.V1, responderID, ASN1GeneralizedTime.getInstance(asn1GeneralizedTime), asn1Sequence, Extensions.getInstance(x509Extensions));
    }
    
    public ResponseData(final ResponderID responderID, final ASN1GeneralizedTime asn1GeneralizedTime, final ASN1Sequence asn1Sequence, final Extensions extensions) {
        this(ResponseData.V1, responderID, asn1GeneralizedTime, asn1Sequence, extensions);
    }
    
    private ResponseData(final ASN1Sequence asn1Sequence) {
        int n = 0;
        if (asn1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
            if (((ASN1TaggedObject)asn1Sequence.getObjectAt(0)).getTagNo() == 0) {
                this.versionPresent = true;
                this.version = ASN1Integer.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(0), true);
                ++n;
            }
            else {
                this.version = ResponseData.V1;
            }
        }
        else {
            this.version = ResponseData.V1;
        }
        this.responderID = ResponderID.getInstance(asn1Sequence.getObjectAt(n++));
        this.producedAt = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(n++));
        this.responses = (ASN1Sequence)asn1Sequence.getObjectAt(n++);
        if (asn1Sequence.size() > n) {
            this.responseExtensions = Extensions.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(n), true);
        }
    }
    
    public static ResponseData getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static ResponseData getInstance(final Object o) {
        if (o instanceof ResponseData) {
            return (ResponseData)o;
        }
        if (o != null) {
            return new ResponseData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public ResponderID getResponderID() {
        return this.responderID;
    }
    
    public ASN1GeneralizedTime getProducedAt() {
        return this.producedAt;
    }
    
    public ASN1Sequence getResponses() {
        return this.responses;
    }
    
    public Extensions getResponseExtensions() {
        return this.responseExtensions;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.versionPresent || !this.version.equals(ResponseData.V1)) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.version));
        }
        asn1EncodableVector.add(this.responderID);
        asn1EncodableVector.add(this.producedAt);
        asn1EncodableVector.add(this.responses);
        if (this.responseExtensions != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, this.responseExtensions));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        V1 = new ASN1Integer(0L);
    }
}
