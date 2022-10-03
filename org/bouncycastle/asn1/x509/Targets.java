package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class Targets extends ASN1Object
{
    private ASN1Sequence targets;
    
    public static Targets getInstance(final Object o) {
        if (o instanceof Targets) {
            return (Targets)o;
        }
        if (o != null) {
            return new Targets(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private Targets(final ASN1Sequence targets) {
        this.targets = targets;
    }
    
    public Targets(final Target[] array) {
        this.targets = new DERSequence(array);
    }
    
    public Target[] getTargets() {
        final Target[] array = new Target[this.targets.size()];
        int n = 0;
        final Enumeration objects = this.targets.getObjects();
        while (objects.hasMoreElements()) {
            array[n++] = Target.getInstance(objects.nextElement());
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.targets;
    }
}
