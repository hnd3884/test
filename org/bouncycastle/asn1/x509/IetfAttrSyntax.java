package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import java.util.Enumeration;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Object;

public class IetfAttrSyntax extends ASN1Object
{
    public static final int VALUE_OCTETS = 1;
    public static final int VALUE_OID = 2;
    public static final int VALUE_UTF8 = 3;
    GeneralNames policyAuthority;
    Vector values;
    int valueChoice;
    
    public static IetfAttrSyntax getInstance(final Object o) {
        if (o instanceof IetfAttrSyntax) {
            return (IetfAttrSyntax)o;
        }
        if (o != null) {
            return new IetfAttrSyntax(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private IetfAttrSyntax(ASN1Sequence asn1Sequence) {
        this.policyAuthority = null;
        this.values = new Vector();
        this.valueChoice = -1;
        int n = 0;
        if (asn1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.policyAuthority = GeneralNames.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(0), false);
            ++n;
        }
        else if (asn1Sequence.size() == 2) {
            this.policyAuthority = GeneralNames.getInstance(asn1Sequence.getObjectAt(0));
            ++n;
        }
        if (!(asn1Sequence.getObjectAt(n) instanceof ASN1Sequence)) {
            throw new IllegalArgumentException("Non-IetfAttrSyntax encoding");
        }
        asn1Sequence = (ASN1Sequence)asn1Sequence.getObjectAt(n);
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1Primitive asn1Primitive = objects.nextElement();
            int valueChoice;
            if (asn1Primitive instanceof ASN1ObjectIdentifier) {
                valueChoice = 2;
            }
            else if (asn1Primitive instanceof DERUTF8String) {
                valueChoice = 3;
            }
            else {
                if (!(asn1Primitive instanceof DEROctetString)) {
                    throw new IllegalArgumentException("Bad value type encoding IetfAttrSyntax");
                }
                valueChoice = 1;
            }
            if (this.valueChoice < 0) {
                this.valueChoice = valueChoice;
            }
            if (valueChoice != this.valueChoice) {
                throw new IllegalArgumentException("Mix of value types in IetfAttrSyntax");
            }
            this.values.addElement(asn1Primitive);
        }
    }
    
    public GeneralNames getPolicyAuthority() {
        return this.policyAuthority;
    }
    
    public int getValueType() {
        return this.valueChoice;
    }
    
    public Object[] getValues() {
        if (this.getValueType() == 1) {
            final ASN1OctetString[] array = new ASN1OctetString[this.values.size()];
            for (int i = 0; i != array.length; ++i) {
                array[i] = (ASN1OctetString)this.values.elementAt(i);
            }
            return array;
        }
        if (this.getValueType() == 2) {
            final ASN1ObjectIdentifier[] array2 = new ASN1ObjectIdentifier[this.values.size()];
            for (int j = 0; j != array2.length; ++j) {
                array2[j] = (ASN1ObjectIdentifier)this.values.elementAt(j);
            }
            return array2;
        }
        final DERUTF8String[] array3 = new DERUTF8String[this.values.size()];
        for (int k = 0; k != array3.length; ++k) {
            array3[k] = (DERUTF8String)this.values.elementAt(k);
        }
        return array3;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.policyAuthority != null) {
            asn1EncodableVector.add(new DERTaggedObject(0, this.policyAuthority));
        }
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        final Enumeration elements = this.values.elements();
        while (elements.hasMoreElements()) {
            asn1EncodableVector2.add((ASN1Encodable)elements.nextElement());
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector2));
        return new DERSequence(asn1EncodableVector);
    }
}
