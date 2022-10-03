package jcifs.spnego.asn1;

import java.io.IOException;

public abstract class ASN1TaggedObject extends DERObject
{
    int tagNo;
    boolean empty;
    boolean explicit;
    DEREncodable obj;
    
    public static ASN1TaggedObject getInstance(final ASN1TaggedObject obj, final boolean explicit) {
        if (explicit) {
            return (ASN1TaggedObject)obj.getObject();
        }
        throw new IllegalArgumentException("implicitly tagged tagged object");
    }
    
    public ASN1TaggedObject(final int tagNo, final DEREncodable obj) {
        this.empty = false;
        this.explicit = true;
        this.obj = null;
        this.explicit = true;
        this.tagNo = tagNo;
        this.obj = obj;
    }
    
    public ASN1TaggedObject(final boolean explicit, final int tagNo, final DEREncodable obj) {
        this.empty = false;
        this.explicit = true;
        this.obj = null;
        this.explicit = explicit;
        this.tagNo = tagNo;
        this.obj = obj;
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof ASN1TaggedObject)) {
            return false;
        }
        final ASN1TaggedObject other = (ASN1TaggedObject)o;
        if (this.tagNo != other.tagNo || this.empty != other.empty || this.explicit != other.explicit) {
            return false;
        }
        if (this.obj == null) {
            if (other.obj != null) {
                return false;
            }
        }
        else if (!this.obj.equals(other.obj)) {
            return false;
        }
        return true;
    }
    
    public int getTagNo() {
        return this.tagNo;
    }
    
    public boolean isExplicit() {
        return this.explicit;
    }
    
    public boolean isEmpty() {
        return this.empty;
    }
    
    public DERObject getObject() {
        if (this.obj != null) {
            return this.obj.getDERObject();
        }
        return null;
    }
    
    abstract void encode(final DEROutputStream p0) throws IOException;
}
