package org.bouncycastle.asn1.x509.qualified;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class SemanticsInformation extends ASN1Object
{
    private ASN1ObjectIdentifier semanticsIdentifier;
    private GeneralName[] nameRegistrationAuthorities;
    
    public static SemanticsInformation getInstance(final Object o) {
        if (o instanceof SemanticsInformation) {
            return (SemanticsInformation)o;
        }
        if (o != null) {
            return new SemanticsInformation(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private SemanticsInformation(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        if (asn1Sequence.size() < 1) {
            throw new IllegalArgumentException("no objects in SemanticsInformation");
        }
        Object o = objects.nextElement();
        if (o instanceof ASN1ObjectIdentifier) {
            this.semanticsIdentifier = ASN1ObjectIdentifier.getInstance(o);
            if (objects.hasMoreElements()) {
                o = objects.nextElement();
            }
            else {
                o = null;
            }
        }
        if (o != null) {
            final ASN1Sequence instance = ASN1Sequence.getInstance(o);
            this.nameRegistrationAuthorities = new GeneralName[instance.size()];
            for (int i = 0; i < instance.size(); ++i) {
                this.nameRegistrationAuthorities[i] = GeneralName.getInstance(instance.getObjectAt(i));
            }
        }
    }
    
    public SemanticsInformation(final ASN1ObjectIdentifier semanticsIdentifier, final GeneralName[] array) {
        this.semanticsIdentifier = semanticsIdentifier;
        this.nameRegistrationAuthorities = cloneNames(array);
    }
    
    public SemanticsInformation(final ASN1ObjectIdentifier semanticsIdentifier) {
        this.semanticsIdentifier = semanticsIdentifier;
        this.nameRegistrationAuthorities = null;
    }
    
    public SemanticsInformation(final GeneralName[] array) {
        this.semanticsIdentifier = null;
        this.nameRegistrationAuthorities = cloneNames(array);
    }
    
    public ASN1ObjectIdentifier getSemanticsIdentifier() {
        return this.semanticsIdentifier;
    }
    
    public GeneralName[] getNameRegistrationAuthorities() {
        return cloneNames(this.nameRegistrationAuthorities);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.semanticsIdentifier != null) {
            asn1EncodableVector.add(this.semanticsIdentifier);
        }
        if (this.nameRegistrationAuthorities != null) {
            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            for (int i = 0; i < this.nameRegistrationAuthorities.length; ++i) {
                asn1EncodableVector2.add(this.nameRegistrationAuthorities[i]);
            }
            asn1EncodableVector.add(new DERSequence(asn1EncodableVector2));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    private static GeneralName[] cloneNames(final GeneralName[] array) {
        if (array != null) {
            final GeneralName[] array2 = new GeneralName[array.length];
            System.arraycopy(array, 0, array2, 0, array.length);
            return array2;
        }
        return null;
    }
}
