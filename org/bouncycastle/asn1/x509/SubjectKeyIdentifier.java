package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Object;

public class SubjectKeyIdentifier extends ASN1Object
{
    private byte[] keyidentifier;
    
    public static SubjectKeyIdentifier getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1OctetString.getInstance(asn1TaggedObject, b));
    }
    
    public static SubjectKeyIdentifier getInstance(final Object o) {
        if (o instanceof SubjectKeyIdentifier) {
            return (SubjectKeyIdentifier)o;
        }
        if (o != null) {
            return new SubjectKeyIdentifier(ASN1OctetString.getInstance(o));
        }
        return null;
    }
    
    public static SubjectKeyIdentifier fromExtensions(final Extensions extensions) {
        return getInstance(extensions.getExtensionParsedValue(Extension.subjectKeyIdentifier));
    }
    
    public SubjectKeyIdentifier(final byte[] array) {
        this.keyidentifier = Arrays.clone(array);
    }
    
    protected SubjectKeyIdentifier(final ASN1OctetString asn1OctetString) {
        this(asn1OctetString.getOctets());
    }
    
    public byte[] getKeyIdentifier() {
        return Arrays.clone(this.keyidentifier);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.getKeyIdentifier());
    }
}
