package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class Target extends ASN1Object implements ASN1Choice
{
    public static final int targetName = 0;
    public static final int targetGroup = 1;
    private GeneralName targName;
    private GeneralName targGroup;
    
    public static Target getInstance(final Object o) {
        if (o == null || o instanceof Target) {
            return (Target)o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new Target((ASN1TaggedObject)o);
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass());
    }
    
    private Target(final ASN1TaggedObject asn1TaggedObject) {
        switch (asn1TaggedObject.getTagNo()) {
            case 0: {
                this.targName = GeneralName.getInstance(asn1TaggedObject, true);
                break;
            }
            case 1: {
                this.targGroup = GeneralName.getInstance(asn1TaggedObject, true);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown tag: " + asn1TaggedObject.getTagNo());
            }
        }
    }
    
    public Target(final int n, final GeneralName generalName) {
        this(new DERTaggedObject(n, generalName));
    }
    
    public GeneralName getTargetGroup() {
        return this.targGroup;
    }
    
    public GeneralName getTargetName() {
        return this.targName;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.targName != null) {
            return new DERTaggedObject(true, 0, this.targName);
        }
        return new DERTaggedObject(true, 1, this.targGroup);
    }
}
