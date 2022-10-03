package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class X500Name extends ASN1Object implements ASN1Choice
{
    private static X500NameStyle defaultStyle;
    private boolean isHashCodeCalculated;
    private int hashCodeValue;
    private X500NameStyle style;
    private RDN[] rdns;
    
    @Deprecated
    public X500Name(final X500NameStyle style, final X500Name x500Name) {
        this.rdns = x500Name.rdns;
        this.style = style;
    }
    
    public static X500Name getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, true));
    }
    
    public static X500Name getInstance(final Object o) {
        if (o instanceof X500Name) {
            return (X500Name)o;
        }
        if (o != null) {
            return new X500Name(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static X500Name getInstance(final X500NameStyle x500NameStyle, final Object o) {
        if (o instanceof X500Name) {
            return new X500Name(x500NameStyle, (X500Name)o);
        }
        if (o != null) {
            return new X500Name(x500NameStyle, ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private X500Name(final ASN1Sequence asn1Sequence) {
        this(X500Name.defaultStyle, asn1Sequence);
    }
    
    private X500Name(final X500NameStyle style, final ASN1Sequence asn1Sequence) {
        this.style = style;
        this.rdns = new RDN[asn1Sequence.size()];
        int n = 0;
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            this.rdns[n++] = RDN.getInstance(objects.nextElement());
        }
    }
    
    public X500Name(final RDN[] array) {
        this(X500Name.defaultStyle, array);
    }
    
    public X500Name(final X500NameStyle style, final RDN[] rdns) {
        this.rdns = rdns;
        this.style = style;
    }
    
    public X500Name(final String s) {
        this(X500Name.defaultStyle, s);
    }
    
    public X500Name(final X500NameStyle style, final String s) {
        this(style.fromString(s));
        this.style = style;
    }
    
    public RDN[] getRDNs() {
        final RDN[] array = new RDN[this.rdns.length];
        System.arraycopy(this.rdns, 0, array, 0, array.length);
        return array;
    }
    
    public ASN1ObjectIdentifier[] getAttributeTypes() {
        int n = 0;
        for (int i = 0; i != this.rdns.length; ++i) {
            n += this.rdns[i].size();
        }
        final ASN1ObjectIdentifier[] array = new ASN1ObjectIdentifier[n];
        int n2 = 0;
        for (int j = 0; j != this.rdns.length; ++j) {
            final RDN rdn = this.rdns[j];
            if (rdn.isMultiValued()) {
                final AttributeTypeAndValue[] typesAndValues = rdn.getTypesAndValues();
                for (int k = 0; k != typesAndValues.length; ++k) {
                    array[n2++] = typesAndValues[k].getType();
                }
            }
            else if (rdn.size() != 0) {
                array[n2++] = rdn.getFirst().getType();
            }
        }
        return array;
    }
    
    public RDN[] getRDNs(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final RDN[] array = new RDN[this.rdns.length];
        int n = 0;
        for (int i = 0; i != this.rdns.length; ++i) {
            final RDN rdn = this.rdns[i];
            if (rdn.isMultiValued()) {
                final AttributeTypeAndValue[] typesAndValues = rdn.getTypesAndValues();
                for (int j = 0; j != typesAndValues.length; ++j) {
                    if (typesAndValues[j].getType().equals(asn1ObjectIdentifier)) {
                        array[n++] = rdn;
                        break;
                    }
                }
            }
            else if (rdn.getFirst().getType().equals(asn1ObjectIdentifier)) {
                array[n++] = rdn;
            }
        }
        final RDN[] array2 = new RDN[n];
        System.arraycopy(array, 0, array2, 0, array2.length);
        return array2;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.rdns);
    }
    
    @Override
    public int hashCode() {
        if (this.isHashCodeCalculated) {
            return this.hashCodeValue;
        }
        this.isHashCodeCalculated = true;
        return this.hashCodeValue = this.style.calculateHashCode(this);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof X500Name) && !(o instanceof ASN1Sequence)) {
            return false;
        }
        if (this.toASN1Primitive().equals(((ASN1Encodable)o).toASN1Primitive())) {
            return true;
        }
        try {
            return this.style.areEqual(this, new X500Name(ASN1Sequence.getInstance(((ASN1Encodable)o).toASN1Primitive())));
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return this.style.toString(this);
    }
    
    public static void setDefaultStyle(final X500NameStyle defaultStyle) {
        if (defaultStyle == null) {
            throw new NullPointerException("cannot set style to null");
        }
        X500Name.defaultStyle = defaultStyle;
    }
    
    public static X500NameStyle getDefaultStyle() {
        return X500Name.defaultStyle;
    }
    
    static {
        X500Name.defaultStyle = BCStyle.INSTANCE;
    }
}
