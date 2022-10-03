package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class TargetInformation extends ASN1Object
{
    private ASN1Sequence targets;
    
    public static TargetInformation getInstance(final Object o) {
        if (o instanceof TargetInformation) {
            return (TargetInformation)o;
        }
        if (o != null) {
            return new TargetInformation(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private TargetInformation(final ASN1Sequence targets) {
        this.targets = targets;
    }
    
    public Targets[] getTargetsObjects() {
        final Targets[] array = new Targets[this.targets.size()];
        int n = 0;
        final Enumeration objects = this.targets.getObjects();
        while (objects.hasMoreElements()) {
            array[n++] = Targets.getInstance(objects.nextElement());
        }
        return array;
    }
    
    public TargetInformation(final Targets targets) {
        this.targets = new DERSequence(targets);
    }
    
    public TargetInformation(final Target[] array) {
        this(new Targets(array));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.targets;
    }
}
