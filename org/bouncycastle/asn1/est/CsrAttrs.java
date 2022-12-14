package org.bouncycastle.asn1.est;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CsrAttrs extends ASN1Object
{
    private final AttrOrOID[] attrOrOIDs;
    
    public static CsrAttrs getInstance(final Object o) {
        if (o instanceof CsrAttrs) {
            return (CsrAttrs)o;
        }
        if (o != null) {
            return new CsrAttrs(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static CsrAttrs getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public CsrAttrs(final AttrOrOID attrOrOID) {
        this.attrOrOIDs = new AttrOrOID[] { attrOrOID };
    }
    
    public CsrAttrs(final AttrOrOID[] array) {
        this.attrOrOIDs = Utils.clone(array);
    }
    
    private CsrAttrs(final ASN1Sequence asn1Sequence) {
        this.attrOrOIDs = new AttrOrOID[asn1Sequence.size()];
        for (int i = 0; i != asn1Sequence.size(); ++i) {
            this.attrOrOIDs[i] = AttrOrOID.getInstance(asn1Sequence.getObjectAt(i));
        }
    }
    
    public AttrOrOID[] getAttrOrOIDs() {
        return Utils.clone(this.attrOrOIDs);
    }
    
    public int size() {
        return this.attrOrOIDs.length;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.attrOrOIDs);
    }
}
