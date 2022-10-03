package com.maverick.crypto.asn1;

import java.io.IOException;

public class DERUnknownTag extends DERObject
{
    int qb;
    byte[] rb;
    
    public DERUnknownTag(final int qb, final byte[] rb) {
        this.qb = qb;
        this.rb = rb;
    }
    
    public int getTag() {
        return this.qb;
    }
    
    public byte[] getData() {
        return this.rb;
    }
    
    void encode(final DEROutputStream derOutputStream) throws IOException {
        derOutputStream.b(this.qb, this.rb);
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof DERUnknownTag)) {
            return false;
        }
        final DERUnknownTag derUnknownTag = (DERUnknownTag)o;
        if (this.qb != derUnknownTag.qb) {
            return false;
        }
        if (this.rb.length != derUnknownTag.rb.length) {
            return false;
        }
        for (int i = 0; i < this.rb.length; ++i) {
            if (this.rb[i] != derUnknownTag.rb[i]) {
                return false;
            }
        }
        return true;
    }
}
