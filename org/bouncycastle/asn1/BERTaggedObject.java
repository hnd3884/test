package org.bouncycastle.asn1;

import java.util.Enumeration;
import java.io.IOException;

public class BERTaggedObject extends ASN1TaggedObject
{
    public BERTaggedObject(final int n, final ASN1Encodable asn1Encodable) {
        super(true, n, asn1Encodable);
    }
    
    public BERTaggedObject(final boolean b, final int n, final ASN1Encodable asn1Encodable) {
        super(b, n, asn1Encodable);
    }
    
    public BERTaggedObject(final int n) {
        super(false, n, new BERSequence());
    }
    
    @Override
    boolean isConstructed() {
        return this.empty || this.explicit || this.obj.toASN1Primitive().toDERObject().isConstructed();
    }
    
    @Override
    int encodedLength() throws IOException {
        if (this.empty) {
            return StreamUtil.calculateTagLength(this.tagNo) + 1;
        }
        final int encodedLength = this.obj.toASN1Primitive().encodedLength();
        if (this.explicit) {
            return StreamUtil.calculateTagLength(this.tagNo) + StreamUtil.calculateBodyLength(encodedLength) + encodedLength;
        }
        return StreamUtil.calculateTagLength(this.tagNo) + (encodedLength - 1);
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        asn1OutputStream.writeTag(160, this.tagNo);
        asn1OutputStream.write(128);
        if (!this.empty) {
            if (!this.explicit) {
                Enumeration enumeration;
                if (this.obj instanceof ASN1OctetString) {
                    if (this.obj instanceof BEROctetString) {
                        enumeration = ((BEROctetString)this.obj).getObjects();
                    }
                    else {
                        enumeration = new BEROctetString(((ASN1OctetString)this.obj).getOctets()).getObjects();
                    }
                }
                else if (this.obj instanceof ASN1Sequence) {
                    enumeration = ((ASN1Sequence)this.obj).getObjects();
                }
                else {
                    if (!(this.obj instanceof ASN1Set)) {
                        throw new ASN1Exception("not implemented: " + this.obj.getClass().getName());
                    }
                    enumeration = ((ASN1Set)this.obj).getObjects();
                }
                while (enumeration.hasMoreElements()) {
                    asn1OutputStream.writeObject((ASN1Encodable)enumeration.nextElement());
                }
            }
            else {
                asn1OutputStream.writeObject(this.obj);
            }
        }
        asn1OutputStream.write(0);
        asn1OutputStream.write(0);
    }
}
