package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.asn1.ASN1InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Vector;
import java.util.Hashtable;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class PKCS12BagAttributeCarrierImpl implements PKCS12BagAttributeCarrier
{
    private Hashtable pkcs12Attributes;
    private Vector pkcs12Ordering;
    
    PKCS12BagAttributeCarrierImpl(final Hashtable pkcs12Attributes, final Vector pkcs12Ordering) {
        this.pkcs12Attributes = pkcs12Attributes;
        this.pkcs12Ordering = pkcs12Ordering;
    }
    
    public PKCS12BagAttributeCarrierImpl() {
        this(new Hashtable(), new Vector());
    }
    
    public void setBagAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable asn1Encodable) {
        if (this.pkcs12Attributes.containsKey(asn1ObjectIdentifier)) {
            this.pkcs12Attributes.put(asn1ObjectIdentifier, asn1Encodable);
        }
        else {
            this.pkcs12Attributes.put(asn1ObjectIdentifier, asn1Encodable);
            this.pkcs12Ordering.addElement(asn1ObjectIdentifier);
        }
    }
    
    public ASN1Encodable getBagAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return this.pkcs12Attributes.get(asn1ObjectIdentifier);
    }
    
    public Enumeration getBagAttributeKeys() {
        return this.pkcs12Ordering.elements();
    }
    
    int size() {
        return this.pkcs12Ordering.size();
    }
    
    Hashtable getAttributes() {
        return this.pkcs12Attributes;
    }
    
    Vector getOrdering() {
        return this.pkcs12Ordering;
    }
    
    public void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (this.pkcs12Ordering.size() == 0) {
            objectOutputStream.writeObject(new Hashtable());
            objectOutputStream.writeObject(new Vector());
        }
        else {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final ASN1OutputStream asn1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
            final Enumeration bagAttributeKeys = this.getBagAttributeKeys();
            while (bagAttributeKeys.hasMoreElements()) {
                final ASN1ObjectIdentifier asn1ObjectIdentifier = bagAttributeKeys.nextElement();
                asn1OutputStream.writeObject(asn1ObjectIdentifier);
                asn1OutputStream.writeObject((ASN1Encodable)this.pkcs12Attributes.get(asn1ObjectIdentifier));
            }
            objectOutputStream.writeObject(byteArrayOutputStream.toByteArray());
        }
    }
    
    public void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final Object object = objectInputStream.readObject();
        if (object instanceof Hashtable) {
            this.pkcs12Attributes = (Hashtable)object;
            this.pkcs12Ordering = (Vector)objectInputStream.readObject();
        }
        else {
            final ASN1InputStream asn1InputStream = new ASN1InputStream((byte[])object);
            ASN1ObjectIdentifier asn1ObjectIdentifier;
            while ((asn1ObjectIdentifier = (ASN1ObjectIdentifier)asn1InputStream.readObject()) != null) {
                this.setBagAttribute(asn1ObjectIdentifier, asn1InputStream.readObject());
            }
        }
    }
}
