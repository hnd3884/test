package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.DirectoryString;
import org.bouncycastle.asn1.ASN1Object;

public class SignerLocation extends ASN1Object
{
    private DirectoryString countryName;
    private DirectoryString localityName;
    private ASN1Sequence postalAddress;
    
    private SignerLocation(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject asn1TaggedObject = objects.nextElement();
            switch (asn1TaggedObject.getTagNo()) {
                case 0: {
                    this.countryName = DirectoryString.getInstance(asn1TaggedObject, true);
                    continue;
                }
                case 1: {
                    this.localityName = DirectoryString.getInstance(asn1TaggedObject, true);
                    continue;
                }
                case 2: {
                    if (asn1TaggedObject.isExplicit()) {
                        this.postalAddress = ASN1Sequence.getInstance(asn1TaggedObject, true);
                    }
                    else {
                        this.postalAddress = ASN1Sequence.getInstance(asn1TaggedObject, false);
                    }
                    if (this.postalAddress != null && this.postalAddress.size() > 6) {
                        throw new IllegalArgumentException("postal address must contain less than 6 strings");
                    }
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("illegal tag");
                }
            }
        }
    }
    
    private SignerLocation(final DirectoryString countryName, final DirectoryString localityName, final ASN1Sequence postalAddress) {
        if (postalAddress != null && postalAddress.size() > 6) {
            throw new IllegalArgumentException("postal address must contain less than 6 strings");
        }
        this.countryName = countryName;
        this.localityName = localityName;
        this.postalAddress = postalAddress;
    }
    
    public SignerLocation(final DirectoryString directoryString, final DirectoryString directoryString2, final DirectoryString[] array) {
        this(directoryString, directoryString2, new DERSequence(array));
    }
    
    public SignerLocation(final DERUTF8String derutf8String, final DERUTF8String derutf8String2, final ASN1Sequence asn1Sequence) {
        this(DirectoryString.getInstance(derutf8String), DirectoryString.getInstance(derutf8String2), asn1Sequence);
    }
    
    public static SignerLocation getInstance(final Object o) {
        if (o == null || o instanceof SignerLocation) {
            return (SignerLocation)o;
        }
        return new SignerLocation(ASN1Sequence.getInstance(o));
    }
    
    public DirectoryString getCountry() {
        return this.countryName;
    }
    
    public DirectoryString getLocality() {
        return this.localityName;
    }
    
    public DirectoryString[] getPostal() {
        if (this.postalAddress == null) {
            return null;
        }
        final DirectoryString[] array = new DirectoryString[this.postalAddress.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = DirectoryString.getInstance(this.postalAddress.getObjectAt(i));
        }
        return array;
    }
    
    @Deprecated
    public DERUTF8String getCountryName() {
        if (this.countryName == null) {
            return null;
        }
        return new DERUTF8String(this.getCountry().getString());
    }
    
    @Deprecated
    public DERUTF8String getLocalityName() {
        if (this.localityName == null) {
            return null;
        }
        return new DERUTF8String(this.getLocality().getString());
    }
    
    public ASN1Sequence getPostalAddress() {
        return this.postalAddress;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.countryName != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.countryName));
        }
        if (this.localityName != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, this.localityName));
        }
        if (this.postalAddress != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 2, this.postalAddress));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
