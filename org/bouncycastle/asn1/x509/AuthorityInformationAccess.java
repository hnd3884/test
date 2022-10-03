package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class AuthorityInformationAccess extends ASN1Object
{
    private AccessDescription[] descriptions;
    
    public static AuthorityInformationAccess getInstance(final Object o) {
        if (o instanceof AuthorityInformationAccess) {
            return (AuthorityInformationAccess)o;
        }
        if (o != null) {
            return new AuthorityInformationAccess(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static AuthorityInformationAccess fromExtensions(final Extensions extensions) {
        return getInstance(extensions.getExtensionParsedValue(Extension.authorityInfoAccess));
    }
    
    private AuthorityInformationAccess(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1) {
            throw new IllegalArgumentException("sequence may not be empty");
        }
        this.descriptions = new AccessDescription[asn1Sequence.size()];
        for (int i = 0; i != asn1Sequence.size(); ++i) {
            this.descriptions[i] = AccessDescription.getInstance(asn1Sequence.getObjectAt(i));
        }
    }
    
    public AuthorityInformationAccess(final AccessDescription accessDescription) {
        this(new AccessDescription[] { accessDescription });
    }
    
    public AuthorityInformationAccess(final AccessDescription[] array) {
        System.arraycopy(array, 0, this.descriptions = new AccessDescription[array.length], 0, array.length);
    }
    
    public AuthorityInformationAccess(final ASN1ObjectIdentifier asn1ObjectIdentifier, final GeneralName generalName) {
        this(new AccessDescription(asn1ObjectIdentifier, generalName));
    }
    
    public AccessDescription[] getAccessDescriptions() {
        return this.descriptions;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != this.descriptions.length; ++i) {
            asn1EncodableVector.add(this.descriptions[i]);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        return "AuthorityInformationAccess: Oid(" + this.descriptions[0].getAccessMethod().getId() + ")";
    }
}
