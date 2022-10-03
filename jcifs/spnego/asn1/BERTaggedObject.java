package jcifs.spnego.asn1;

import java.io.IOException;
import java.util.Enumeration;

public class BERTaggedObject extends DERTaggedObject
{
    public BERTaggedObject(final int tagNo, final DEREncodable obj) {
        super(tagNo, obj);
    }
    
    public BERTaggedObject(final boolean explicit, final int tagNo, final DEREncodable obj) {
        super(explicit, tagNo, obj);
    }
    
    public BERTaggedObject(final int tagNo) {
        super(false, tagNo, new BERConstructedSequence());
    }
    
    void encode(final DEROutputStream out) throws IOException {
        if (out instanceof ASN1OutputStream || out instanceof BEROutputStream) {
            out.write(0xA0 | this.tagNo);
            out.write(128);
            if (!this.empty) {
                if (!this.explicit) {
                    if (this.obj instanceof ASN1OctetString) {
                        Enumeration e;
                        if (this.obj instanceof BERConstructedOctetString) {
                            e = ((BERConstructedOctetString)this.obj).getObjects();
                        }
                        else {
                            final ASN1OctetString octs = (ASN1OctetString)this.obj;
                            final BERConstructedOctetString berO = new BERConstructedOctetString(octs.getOctets());
                            e = berO.getObjects();
                        }
                        while (e.hasMoreElements()) {
                            out.writeObject(e.nextElement());
                        }
                    }
                    else if (this.obj instanceof ASN1Sequence) {
                        final Enumeration e = ((ASN1Sequence)this.obj).getObjects();
                        while (e.hasMoreElements()) {
                            out.writeObject(e.nextElement());
                        }
                    }
                    else {
                        if (!(this.obj instanceof ASN1Set)) {
                            throw new RuntimeException("not implemented: " + this.obj.getClass().getName());
                        }
                        final Enumeration e = ((ASN1Set)this.obj).getObjects();
                        while (e.hasMoreElements()) {
                            out.writeObject(e.nextElement());
                        }
                    }
                }
                else {
                    out.writeObject(this.obj);
                }
            }
            out.write(0);
            out.write(0);
        }
        else {
            super.encode(out);
        }
    }
}
