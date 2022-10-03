package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class DVCSResponse extends ASN1Object implements ASN1Choice
{
    private DVCSCertInfo dvCertInfo;
    private DVCSErrorNotice dvErrorNote;
    
    public DVCSResponse(final DVCSCertInfo dvCertInfo) {
        this.dvCertInfo = dvCertInfo;
    }
    
    public DVCSResponse(final DVCSErrorNotice dvErrorNote) {
        this.dvErrorNote = dvErrorNote;
    }
    
    public static DVCSResponse getInstance(final Object o) {
        if (o == null || o instanceof DVCSResponse) {
            return (DVCSResponse)o;
        }
        if (o instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[])o));
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("failed to construct sequence from byte[]: " + ex.getMessage());
            }
        }
        if (o instanceof ASN1Sequence) {
            return new DVCSResponse(DVCSCertInfo.getInstance(o));
        }
        if (o instanceof ASN1TaggedObject) {
            return new DVCSResponse(DVCSErrorNotice.getInstance(ASN1TaggedObject.getInstance(o), false));
        }
        throw new IllegalArgumentException("Couldn't convert from object to DVCSResponse: " + o.getClass().getName());
    }
    
    public static DVCSResponse getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public DVCSCertInfo getCertInfo() {
        return this.dvCertInfo;
    }
    
    public DVCSErrorNotice getErrorNotice() {
        return this.dvErrorNote;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.dvCertInfo != null) {
            return this.dvCertInfo.toASN1Primitive();
        }
        return new DERTaggedObject(false, 0, this.dvErrorNote);
    }
    
    @Override
    public String toString() {
        if (this.dvCertInfo != null) {
            return "DVCSResponse {\ndvCertInfo: " + this.dvCertInfo.toString() + "}\n";
        }
        return "DVCSResponse {\ndvErrorNote: " + this.dvErrorNote.toString() + "}\n";
    }
}
