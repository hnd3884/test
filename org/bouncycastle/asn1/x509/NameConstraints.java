package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class NameConstraints extends ASN1Object
{
    private GeneralSubtree[] permitted;
    private GeneralSubtree[] excluded;
    
    public static NameConstraints getInstance(final Object o) {
        if (o instanceof NameConstraints) {
            return (NameConstraints)o;
        }
        if (o != null) {
            return new NameConstraints(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private NameConstraints(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(objects.nextElement());
            switch (instance.getTagNo()) {
                case 0: {
                    this.permitted = this.createArray(ASN1Sequence.getInstance(instance, false));
                    continue;
                }
                case 1: {
                    this.excluded = this.createArray(ASN1Sequence.getInstance(instance, false));
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("Unknown tag encountered: " + instance.getTagNo());
                }
            }
        }
    }
    
    public NameConstraints(final GeneralSubtree[] array, final GeneralSubtree[] array2) {
        this.permitted = cloneSubtree(array);
        this.excluded = cloneSubtree(array2);
    }
    
    private GeneralSubtree[] createArray(final ASN1Sequence asn1Sequence) {
        final GeneralSubtree[] array = new GeneralSubtree[asn1Sequence.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = GeneralSubtree.getInstance(asn1Sequence.getObjectAt(i));
        }
        return array;
    }
    
    public GeneralSubtree[] getPermittedSubtrees() {
        return cloneSubtree(this.permitted);
    }
    
    public GeneralSubtree[] getExcludedSubtrees() {
        return cloneSubtree(this.excluded);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.permitted != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, new DERSequence(this.permitted)));
        }
        if (this.excluded != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, new DERSequence(this.excluded)));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    private static GeneralSubtree[] cloneSubtree(final GeneralSubtree[] array) {
        if (array != null) {
            final GeneralSubtree[] array2 = new GeneralSubtree[array.length];
            System.arraycopy(array, 0, array2, 0, array2.length);
            return array2;
        }
        return null;
    }
}
