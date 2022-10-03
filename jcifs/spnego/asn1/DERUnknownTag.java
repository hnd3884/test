package jcifs.spnego.asn1;

import java.io.IOException;

public class DERUnknownTag extends DERObject
{
    int tag;
    byte[] data;
    
    public DERUnknownTag(final int tag, final byte[] data) {
        this.tag = tag;
        this.data = data;
    }
    
    public int getTag() {
        return this.tag;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    void encode(final DEROutputStream out) throws IOException {
        out.writeEncoded(this.tag, this.data);
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof DERUnknownTag)) {
            return false;
        }
        final DERUnknownTag other = (DERUnknownTag)o;
        if (this.tag != other.tag) {
            return false;
        }
        if (this.data.length != other.data.length) {
            return false;
        }
        for (int i = 0; i < this.data.length; ++i) {
            if (this.data[i] != other.data[i]) {
                return false;
            }
        }
        return true;
    }
}
