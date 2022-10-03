package org.bouncycastle.asn1.bc;

import org.bouncycastle.util.Arrays;
import java.util.Iterator;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.util.Iterable;
import org.bouncycastle.asn1.ASN1Object;

public class ObjectDataSequence extends ASN1Object implements Iterable<ASN1Encodable>
{
    private final ASN1Encodable[] dataSequence;
    
    public ObjectDataSequence(final ObjectData[] array) {
        System.arraycopy(array, 0, this.dataSequence = new ASN1Encodable[array.length], 0, array.length);
    }
    
    private ObjectDataSequence(final ASN1Sequence asn1Sequence) {
        this.dataSequence = new ASN1Encodable[asn1Sequence.size()];
        for (int i = 0; i != this.dataSequence.length; ++i) {
            this.dataSequence[i] = ObjectData.getInstance(asn1Sequence.getObjectAt(i));
        }
    }
    
    public static ObjectDataSequence getInstance(final Object o) {
        if (o instanceof ObjectDataSequence) {
            return (ObjectDataSequence)o;
        }
        if (o != null) {
            return new ObjectDataSequence(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.dataSequence);
    }
    
    public Iterator<ASN1Encodable> iterator() {
        return new Arrays.Iterator<ASN1Encodable>(this.dataSequence);
    }
}
