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
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class TBSRequest extends ASN1Object
{
    private static final ASN1Integer V1;
    ASN1Integer version;
    GeneralName requestorName;
    ASN1Sequence requestList;
    Extensions requestExtensions;
    boolean versionSet;
    
    @Deprecated
    public TBSRequest(final GeneralName requestorName, final ASN1Sequence requestList, final X509Extensions x509Extensions) {
        this.version = TBSRequest.V1;
        this.requestorName = requestorName;
        this.requestList = requestList;
        this.requestExtensions = Extensions.getInstance(x509Extensions);
    }
    
    public TBSRequest(final GeneralName requestorName, final ASN1Sequence requestList, final Extensions requestExtensions) {
        this.version = TBSRequest.V1;
        this.requestorName = requestorName;
        this.requestList = requestList;
        this.requestExtensions = requestExtensions;
    }
    
    private TBSRequest(final ASN1Sequence asn1Sequence) {
        int n = 0;
        if (asn1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
            if (((ASN1TaggedObject)asn1Sequence.getObjectAt(0)).getTagNo() == 0) {
                this.versionSet = true;
                this.version = ASN1Integer.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(0), true);
                ++n;
            }
            else {
                this.version = TBSRequest.V1;
            }
        }
        else {
            this.version = TBSRequest.V1;
        }
        if (asn1Sequence.getObjectAt(n) instanceof ASN1TaggedObject) {
            this.requestorName = GeneralName.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(n++), true);
        }
        this.requestList = (ASN1Sequence)asn1Sequence.getObjectAt(n++);
        if (asn1Sequence.size() == n + 1) {
            this.requestExtensions = Extensions.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(n), true);
        }
    }
    
    public static TBSRequest getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static TBSRequest getInstance(final Object o) {
        if (o instanceof TBSRequest) {
            return (TBSRequest)o;
        }
        if (o != null) {
            return new TBSRequest(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public GeneralName getRequestorName() {
        return this.requestorName;
    }
    
    public ASN1Sequence getRequestList() {
        return this.requestList;
    }
    
    public Extensions getRequestExtensions() {
        return this.requestExtensions;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (!this.version.equals(TBSRequest.V1) || this.versionSet) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.version));
        }
        if (this.requestorName != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, this.requestorName));
        }
        asn1EncodableVector.add(this.requestList);
        if (this.requestExtensions != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 2, this.requestExtensions));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        V1 = new ASN1Integer(0L);
    }
}
