package org.bouncycastle.asn1;

import java.io.IOException;

public abstract class ASN1TaggedObject extends ASN1Primitive implements ASN1TaggedObjectParser
{
    int tagNo;
    boolean empty;
    boolean explicit;
    ASN1Encodable obj;
    
    public static ASN1TaggedObject getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        if (b) {
            return (ASN1TaggedObject)asn1TaggedObject.getObject();
        }
        throw new IllegalArgumentException("implicitly tagged tagged object");
    }
    
    public static ASN1TaggedObject getInstance(final Object o) {
        if (o == null || o instanceof ASN1TaggedObject) {
            return (ASN1TaggedObject)o;
        }
        if (o instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[])o));
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("failed to construct tagged object from byte[]: " + ex.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + o.getClass().getName());
    }
    
    public ASN1TaggedObject(final boolean explicit, final int tagNo, final ASN1Encodable asn1Encodable) {
        this.empty = false;
        this.explicit = true;
        this.obj = null;
        if (asn1Encodable instanceof ASN1Choice) {
            this.explicit = true;
        }
        else {
            this.explicit = explicit;
        }
        this.tagNo = tagNo;
        if (this.explicit) {
            this.obj = asn1Encodable;
        }
        else {
            if (asn1Encodable.toASN1Primitive() instanceof ASN1Set) {}
            this.obj = asn1Encodable;
        }
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        if (!(asn1Primitive instanceof ASN1TaggedObject)) {
            return false;
        }
        final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Primitive;
        if (this.tagNo != asn1TaggedObject.tagNo || this.empty != asn1TaggedObject.empty || this.explicit != asn1TaggedObject.explicit) {
            return false;
        }
        if (this.obj == null) {
            if (asn1TaggedObject.obj != null) {
                return false;
            }
        }
        else if (!this.obj.toASN1Primitive().equals(asn1TaggedObject.obj.toASN1Primitive())) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int tagNo = this.tagNo;
        if (this.obj != null) {
            tagNo ^= this.obj.hashCode();
        }
        return tagNo;
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
    
    public ASN1Primitive getObject() {
        if (this.obj != null) {
            return this.obj.toASN1Primitive();
        }
        return null;
    }
    
    public ASN1Encodable getObjectParser(final int n, final boolean b) throws IOException {
        switch (n) {
            case 17: {
                return ASN1Set.getInstance(this, b).parser();
            }
            case 16: {
                return ASN1Sequence.getInstance(this, b).parser();
            }
            case 4: {
                return ASN1OctetString.getInstance(this, b).parser();
            }
            default: {
                if (b) {
                    return this.getObject();
                }
                throw new ASN1Exception("implicit tagging not implemented for tag: " + n);
            }
        }
    }
    
    public ASN1Primitive getLoadedObject() {
        return this.toASN1Primitive();
    }
    
    @Override
    ASN1Primitive toDERObject() {
        return new DERTaggedObject(this.explicit, this.tagNo, this.obj);
    }
    
    @Override
    ASN1Primitive toDLObject() {
        return new DLTaggedObject(this.explicit, this.tagNo, this.obj);
    }
    
    @Override
    abstract void encode(final ASN1OutputStream p0) throws IOException;
    
    @Override
    public String toString() {
        return "[" + this.tagNo + "]" + this.obj;
    }
}
