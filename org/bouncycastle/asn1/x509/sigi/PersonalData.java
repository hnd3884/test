package org.bouncycastle.asn1.x509.sigi;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.DirectoryString;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class PersonalData extends ASN1Object
{
    private NameOrPseudonym nameOrPseudonym;
    private BigInteger nameDistinguisher;
    private ASN1GeneralizedTime dateOfBirth;
    private DirectoryString placeOfBirth;
    private String gender;
    private DirectoryString postalAddress;
    
    public static PersonalData getInstance(final Object o) {
        if (o == null || o instanceof PersonalData) {
            return (PersonalData)o;
        }
        if (o instanceof ASN1Sequence) {
            return new PersonalData((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    private PersonalData(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        this.nameOrPseudonym = NameOrPseudonym.getInstance(objects.nextElement());
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(objects.nextElement());
            switch (instance.getTagNo()) {
                case 0: {
                    this.nameDistinguisher = ASN1Integer.getInstance(instance, false).getValue();
                    continue;
                }
                case 1: {
                    this.dateOfBirth = ASN1GeneralizedTime.getInstance(instance, false);
                    continue;
                }
                case 2: {
                    this.placeOfBirth = DirectoryString.getInstance(instance, true);
                    continue;
                }
                case 3: {
                    this.gender = DERPrintableString.getInstance(instance, false).getString();
                    continue;
                }
                case 4: {
                    this.postalAddress = DirectoryString.getInstance(instance, true);
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("Bad tag number: " + instance.getTagNo());
                }
            }
        }
    }
    
    public PersonalData(final NameOrPseudonym nameOrPseudonym, final BigInteger nameDistinguisher, final ASN1GeneralizedTime dateOfBirth, final DirectoryString placeOfBirth, final String gender, final DirectoryString postalAddress) {
        this.nameOrPseudonym = nameOrPseudonym;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.nameDistinguisher = nameDistinguisher;
        this.postalAddress = postalAddress;
        this.placeOfBirth = placeOfBirth;
    }
    
    public NameOrPseudonym getNameOrPseudonym() {
        return this.nameOrPseudonym;
    }
    
    public BigInteger getNameDistinguisher() {
        return this.nameDistinguisher;
    }
    
    public ASN1GeneralizedTime getDateOfBirth() {
        return this.dateOfBirth;
    }
    
    public DirectoryString getPlaceOfBirth() {
        return this.placeOfBirth;
    }
    
    public String getGender() {
        return this.gender;
    }
    
    public DirectoryString getPostalAddress() {
        return this.postalAddress;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.nameOrPseudonym);
        if (this.nameDistinguisher != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, new ASN1Integer(this.nameDistinguisher)));
        }
        if (this.dateOfBirth != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.dateOfBirth));
        }
        if (this.placeOfBirth != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 2, this.placeOfBirth));
        }
        if (this.gender != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 3, new DERPrintableString(this.gender, true)));
        }
        if (this.postalAddress != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 4, this.postalAddress));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
