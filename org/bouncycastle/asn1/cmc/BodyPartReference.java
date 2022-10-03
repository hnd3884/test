package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class BodyPartReference extends ASN1Object implements ASN1Choice
{
    private final BodyPartID bodyPartID;
    private final BodyPartPath bodyPartPath;
    
    public BodyPartReference(final BodyPartID bodyPartID) {
        this.bodyPartID = bodyPartID;
        this.bodyPartPath = null;
    }
    
    public BodyPartReference(final BodyPartPath bodyPartPath) {
        this.bodyPartID = null;
        this.bodyPartPath = bodyPartPath;
    }
    
    public static BodyPartReference getInstance(final Object o) {
        if (o instanceof BodyPartReference) {
            return (BodyPartReference)o;
        }
        if (o != null) {
            if (o instanceof ASN1Encodable) {
                final ASN1Primitive asn1Primitive = ((ASN1Encodable)o).toASN1Primitive();
                if (asn1Primitive instanceof ASN1Integer) {
                    return new BodyPartReference(BodyPartID.getInstance(asn1Primitive));
                }
                if (asn1Primitive instanceof ASN1Sequence) {
                    return new BodyPartReference(BodyPartPath.getInstance(asn1Primitive));
                }
            }
            if (o instanceof byte[]) {
                try {
                    return getInstance(ASN1Primitive.fromByteArray((byte[])o));
                }
                catch (final IOException ex) {
                    throw new IllegalArgumentException("unknown encoding in getInstance()");
                }
            }
            throw new IllegalArgumentException("unknown object in getInstance(): " + o.getClass().getName());
        }
        return null;
    }
    
    public boolean isBodyPartID() {
        return this.bodyPartID != null;
    }
    
    public BodyPartID getBodyPartID() {
        return this.bodyPartID;
    }
    
    public BodyPartPath getBodyPartPath() {
        return this.bodyPartPath;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.bodyPartID != null) {
            return this.bodyPartID.toASN1Primitive();
        }
        return this.bodyPartPath.toASN1Primitive();
    }
}
