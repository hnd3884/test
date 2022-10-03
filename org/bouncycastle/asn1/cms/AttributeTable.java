package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSet;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.util.Hashtable;

public class AttributeTable
{
    private Hashtable attributes;
    
    public AttributeTable(final Hashtable hashtable) {
        this.attributes = new Hashtable();
        this.attributes = this.copyTable(hashtable);
    }
    
    public AttributeTable(final ASN1EncodableVector asn1EncodableVector) {
        this.attributes = new Hashtable();
        for (int i = 0; i != asn1EncodableVector.size(); ++i) {
            final Attribute instance = Attribute.getInstance(asn1EncodableVector.get(i));
            this.addAttribute(instance.getAttrType(), instance);
        }
    }
    
    public AttributeTable(final ASN1Set set) {
        this.attributes = new Hashtable();
        for (int i = 0; i != set.size(); ++i) {
            final Attribute instance = Attribute.getInstance(set.getObjectAt(i));
            this.addAttribute(instance.getAttrType(), instance);
        }
    }
    
    public AttributeTable(final Attribute attribute) {
        this.attributes = new Hashtable();
        this.addAttribute(attribute.getAttrType(), attribute);
    }
    
    public AttributeTable(final Attributes attributes) {
        this(ASN1Set.getInstance(attributes.toASN1Primitive()));
    }
    
    private void addAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final Attribute attribute) {
        final Vector value = this.attributes.get(asn1ObjectIdentifier);
        if (value == null) {
            this.attributes.put(asn1ObjectIdentifier, attribute);
        }
        else {
            Vector vector;
            if (value instanceof Attribute) {
                vector = new Vector();
                vector.addElement(value);
                vector.addElement(attribute);
            }
            else {
                vector = value;
                vector.addElement(attribute);
            }
            this.attributes.put(asn1ObjectIdentifier, vector);
        }
    }
    
    public Attribute get(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final Attribute value = this.attributes.get(asn1ObjectIdentifier);
        if (value instanceof Vector) {
            return ((Vector<Attribute>)value).elementAt(0);
        }
        return value;
    }
    
    public ASN1EncodableVector getAll(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Attribute value = this.attributes.get(asn1ObjectIdentifier);
        if (value instanceof Vector) {
            final Enumeration elements = ((Vector)value).elements();
            while (elements.hasMoreElements()) {
                asn1EncodableVector.add((ASN1Encodable)elements.nextElement());
            }
        }
        else if (value != null) {
            asn1EncodableVector.add(value);
        }
        return asn1EncodableVector;
    }
    
    public int size() {
        int n = 0;
        final Enumeration elements = this.attributes.elements();
        while (elements.hasMoreElements()) {
            final Object nextElement = elements.nextElement();
            if (nextElement instanceof Vector) {
                n += ((Vector)nextElement).size();
            }
            else {
                ++n;
            }
        }
        return n;
    }
    
    public Hashtable toHashtable() {
        return this.copyTable(this.attributes);
    }
    
    public ASN1EncodableVector toASN1EncodableVector() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Enumeration elements = this.attributes.elements();
        while (elements.hasMoreElements()) {
            final Object nextElement = elements.nextElement();
            if (nextElement instanceof Vector) {
                final Enumeration elements2 = ((Vector)nextElement).elements();
                while (elements2.hasMoreElements()) {
                    asn1EncodableVector.add(Attribute.getInstance(elements2.nextElement()));
                }
            }
            else {
                asn1EncodableVector.add(Attribute.getInstance(nextElement));
            }
        }
        return asn1EncodableVector;
    }
    
    public Attributes toASN1Structure() {
        return new Attributes(this.toASN1EncodableVector());
    }
    
    private Hashtable copyTable(final Hashtable hashtable) {
        final Hashtable hashtable2 = new Hashtable();
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            hashtable2.put(nextElement, hashtable.get(nextElement));
        }
        return hashtable2;
    }
    
    public AttributeTable add(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable asn1Encodable) {
        final AttributeTable attributeTable = new AttributeTable(this.attributes);
        attributeTable.addAttribute(asn1ObjectIdentifier, new Attribute(asn1ObjectIdentifier, new DERSet(asn1Encodable)));
        return attributeTable;
    }
    
    public AttributeTable remove(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final AttributeTable attributeTable = new AttributeTable(this.attributes);
        attributeTable.attributes.remove(asn1ObjectIdentifier);
        return attributeTable;
    }
}
