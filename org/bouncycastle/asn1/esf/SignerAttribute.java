package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class SignerAttribute extends ASN1Object
{
    private Object[] values;
    
    public static SignerAttribute getInstance(final Object o) {
        if (o instanceof SignerAttribute) {
            return (SignerAttribute)o;
        }
        if (o != null) {
            return new SignerAttribute(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private SignerAttribute(final ASN1Sequence asn1Sequence) {
        int n = 0;
        this.values = new Object[asn1Sequence.size()];
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(objects.nextElement());
            if (instance.getTagNo() == 0) {
                final ASN1Sequence instance2 = ASN1Sequence.getInstance(instance, true);
                final Attribute[] array = new Attribute[instance2.size()];
                for (int i = 0; i != array.length; ++i) {
                    array[i] = Attribute.getInstance(instance2.getObjectAt(i));
                }
                this.values[n] = array;
            }
            else {
                if (instance.getTagNo() != 1) {
                    throw new IllegalArgumentException("illegal tag: " + instance.getTagNo());
                }
                this.values[n] = AttributeCertificate.getInstance(ASN1Sequence.getInstance(instance, true));
            }
            ++n;
        }
    }
    
    public SignerAttribute(final Attribute[] array) {
        (this.values = new Object[1])[0] = array;
    }
    
    public SignerAttribute(final AttributeCertificate attributeCertificate) {
        (this.values = new Object[1])[0] = attributeCertificate;
    }
    
    public Object[] getValues() {
        final Object[] array = new Object[this.values.length];
        System.arraycopy(this.values, 0, array, 0, array.length);
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != this.values.length; ++i) {
            if (this.values[i] instanceof Attribute[]) {
                asn1EncodableVector.add(new DERTaggedObject(0, new DERSequence((ASN1Encodable[])this.values[i])));
            }
            else {
                asn1EncodableVector.add(new DERTaggedObject(1, (ASN1Encodable)this.values[i]));
            }
        }
        return new DERSequence(asn1EncodableVector);
    }
}
