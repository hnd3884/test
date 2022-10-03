package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CrlListID extends ASN1Object
{
    private ASN1Sequence crls;
    
    public static CrlListID getInstance(final Object o) {
        if (o instanceof CrlListID) {
            return (CrlListID)o;
        }
        if (o != null) {
            return new CrlListID(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private CrlListID(final ASN1Sequence asn1Sequence) {
        this.crls = (ASN1Sequence)asn1Sequence.getObjectAt(0);
        final Enumeration objects = this.crls.getObjects();
        while (objects.hasMoreElements()) {
            CrlValidatedID.getInstance(objects.nextElement());
        }
    }
    
    public CrlListID(final CrlValidatedID[] array) {
        this.crls = new DERSequence(array);
    }
    
    public CrlValidatedID[] getCrls() {
        final CrlValidatedID[] array = new CrlValidatedID[this.crls.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = CrlValidatedID.getInstance(this.crls.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.crls);
    }
}
