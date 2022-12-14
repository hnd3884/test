package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.style.BCStyle;
import java.util.Vector;

public class X500NameBuilder
{
    private X500NameStyle template;
    private Vector rdns;
    
    public X500NameBuilder() {
        this(BCStyle.INSTANCE);
    }
    
    public X500NameBuilder(final X500NameStyle template) {
        this.rdns = new Vector();
        this.template = template;
    }
    
    public X500NameBuilder addRDN(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        this.addRDN(asn1ObjectIdentifier, this.template.stringToValue(asn1ObjectIdentifier, s));
        return this;
    }
    
    public X500NameBuilder addRDN(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable asn1Encodable) {
        this.rdns.addElement(new RDN(asn1ObjectIdentifier, asn1Encodable));
        return this;
    }
    
    public X500NameBuilder addRDN(final AttributeTypeAndValue attributeTypeAndValue) {
        this.rdns.addElement(new RDN(attributeTypeAndValue));
        return this;
    }
    
    public X500NameBuilder addMultiValuedRDN(final ASN1ObjectIdentifier[] array, final String[] array2) {
        final ASN1Encodable[] array3 = new ASN1Encodable[array2.length];
        for (int i = 0; i != array3.length; ++i) {
            array3[i] = this.template.stringToValue(array[i], array2[i]);
        }
        return this.addMultiValuedRDN(array, array3);
    }
    
    public X500NameBuilder addMultiValuedRDN(final ASN1ObjectIdentifier[] array, final ASN1Encodable[] array2) {
        final AttributeTypeAndValue[] array3 = new AttributeTypeAndValue[array.length];
        for (int i = 0; i != array.length; ++i) {
            array3[i] = new AttributeTypeAndValue(array[i], array2[i]);
        }
        return this.addMultiValuedRDN(array3);
    }
    
    public X500NameBuilder addMultiValuedRDN(final AttributeTypeAndValue[] array) {
        this.rdns.addElement(new RDN(array));
        return this;
    }
    
    public X500Name build() {
        final RDN[] array = new RDN[this.rdns.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = (RDN)this.rdns.elementAt(i);
        }
        return new X500Name(this.template, array);
    }
}
