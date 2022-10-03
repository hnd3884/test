package jcifs.spnego.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class DERTaggedObject extends ASN1TaggedObject
{
    public DERTaggedObject(final int tagNo, final DEREncodable obj) {
        super(tagNo, obj);
    }
    
    public DERTaggedObject(final boolean explicit, final int tagNo, final DEREncodable obj) {
        super(explicit, tagNo, obj);
    }
    
    public DERTaggedObject(final int tagNo) {
        super(false, tagNo, new DERSequence());
    }
    
    void encode(final DEROutputStream out) throws IOException {
        if (!this.empty) {
            final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            final DEROutputStream dOut = new DEROutputStream(bOut);
            dOut.writeObject(this.obj);
            dOut.close();
            final byte[] bytes = bOut.toByteArray();
            if (this.explicit) {
                out.writeEncoded(0xA0 | this.tagNo, bytes);
            }
            else {
                if ((bytes[0] & 0x20) != 0x0) {
                    bytes[0] = (byte)(0xA0 | this.tagNo);
                }
                else {
                    bytes[0] = (byte)(0x80 | this.tagNo);
                }
                out.write(bytes);
            }
        }
        else {
            out.writeEncoded(0xA0 | this.tagNo, new byte[0]);
        }
    }
}
