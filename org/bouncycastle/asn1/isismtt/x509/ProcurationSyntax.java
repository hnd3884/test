package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x500.DirectoryString;
import org.bouncycastle.asn1.ASN1Object;

public class ProcurationSyntax extends ASN1Object
{
    private String country;
    private DirectoryString typeOfSubstitution;
    private GeneralName thirdPerson;
    private IssuerSerial certRef;
    
    public static ProcurationSyntax getInstance(final Object o) {
        if (o == null || o instanceof ProcurationSyntax) {
            return (ProcurationSyntax)o;
        }
        if (o instanceof ASN1Sequence) {
            return new ProcurationSyntax((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    private ProcurationSyntax(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1 || asn1Sequence.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(objects.nextElement());
            switch (instance.getTagNo()) {
                case 1: {
                    this.country = DERPrintableString.getInstance(instance, true).getString();
                    continue;
                }
                case 2: {
                    this.typeOfSubstitution = DirectoryString.getInstance(instance, true);
                    continue;
                }
                case 3: {
                    final ASN1Primitive object = instance.getObject();
                    if (object instanceof ASN1TaggedObject) {
                        this.thirdPerson = GeneralName.getInstance(object);
                        continue;
                    }
                    this.certRef = IssuerSerial.getInstance(object);
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("Bad tag number: " + instance.getTagNo());
                }
            }
        }
    }
    
    public ProcurationSyntax(final String country, final DirectoryString typeOfSubstitution, final IssuerSerial certRef) {
        this.country = country;
        this.typeOfSubstitution = typeOfSubstitution;
        this.thirdPerson = null;
        this.certRef = certRef;
    }
    
    public ProcurationSyntax(final String country, final DirectoryString typeOfSubstitution, final GeneralName thirdPerson) {
        this.country = country;
        this.typeOfSubstitution = typeOfSubstitution;
        this.thirdPerson = thirdPerson;
        this.certRef = null;
    }
    
    public String getCountry() {
        return this.country;
    }
    
    public DirectoryString getTypeOfSubstitution() {
        return this.typeOfSubstitution;
    }
    
    public GeneralName getThirdPerson() {
        return this.thirdPerson;
    }
    
    public IssuerSerial getCertRef() {
        return this.certRef;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.country != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, new DERPrintableString(this.country, true)));
        }
        if (this.typeOfSubstitution != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 2, this.typeOfSubstitution));
        }
        if (this.thirdPerson != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 3, this.thirdPerson));
        }
        else {
            asn1EncodableVector.add(new DERTaggedObject(true, 3, this.certRef));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
