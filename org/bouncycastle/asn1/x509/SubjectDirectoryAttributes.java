package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Object;

public class SubjectDirectoryAttributes extends ASN1Object
{
    private Vector attributes;
    
    public static SubjectDirectoryAttributes getInstance(final Object o) {
        if (o instanceof SubjectDirectoryAttributes) {
            return (SubjectDirectoryAttributes)o;
        }
        if (o != null) {
            return new SubjectDirectoryAttributes(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private SubjectDirectoryAttributes(final ASN1Sequence asn1Sequence) {
        this.attributes = new Vector();
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            this.attributes.addElement(Attribute.getInstance(ASN1Sequence.getInstance(objects.nextElement())));
        }
    }
    
    public SubjectDirectoryAttributes(final Vector vector) {
        this.attributes = new Vector();
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            this.attributes.addElement(elements.nextElement());
        }
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Enumeration elements = this.attributes.elements();
        while (elements.hasMoreElements()) {
            asn1EncodableVector.add((ASN1Encodable)elements.nextElement());
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    public Vector getAttributes() {
        return this.attributes;
    }
}
