package org.bouncycastle.asn1.x509;

import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class GeneralNames extends ASN1Object
{
    private final GeneralName[] names;
    
    public static GeneralNames getInstance(final Object o) {
        if (o instanceof GeneralNames) {
            return (GeneralNames)o;
        }
        if (o != null) {
            return new GeneralNames(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static GeneralNames getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static GeneralNames fromExtensions(final Extensions extensions, final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return getInstance(extensions.getExtensionParsedValue(asn1ObjectIdentifier));
    }
    
    public GeneralNames(final GeneralName generalName) {
        this.names = new GeneralName[] { generalName };
    }
    
    public GeneralNames(final GeneralName[] names) {
        this.names = names;
    }
    
    private GeneralNames(final ASN1Sequence asn1Sequence) {
        this.names = new GeneralName[asn1Sequence.size()];
        for (int i = 0; i != asn1Sequence.size(); ++i) {
            this.names[i] = GeneralName.getInstance(asn1Sequence.getObjectAt(i));
        }
    }
    
    public GeneralName[] getNames() {
        final GeneralName[] array = new GeneralName[this.names.length];
        System.arraycopy(this.names, 0, array, 0, this.names.length);
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.names);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        sb.append("GeneralNames:");
        sb.append(lineSeparator);
        for (int i = 0; i != this.names.length; ++i) {
            sb.append("    ");
            sb.append(this.names[i]);
            sb.append(lineSeparator);
        }
        return sb.toString();
    }
}
