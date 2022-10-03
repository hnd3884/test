package com.maverick.crypto.asn1.x509;

import com.maverick.crypto.asn1.DEREncodableVector;
import com.maverick.crypto.asn1.DERSequence;
import com.maverick.crypto.asn1.ASN1EncodableVector;
import com.maverick.crypto.asn1.DERObject;
import java.util.Enumeration;
import com.maverick.crypto.asn1.ASN1OctetString;
import com.maverick.crypto.asn1.DERBoolean;
import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.ASN1TaggedObject;
import java.util.Vector;
import java.util.Hashtable;
import com.maverick.crypto.asn1.DERObjectIdentifier;
import com.maverick.crypto.asn1.DEREncodable;

public class X509Extensions implements DEREncodable
{
    public static final DERObjectIdentifier SubjectKeyIdentifier;
    public static final DERObjectIdentifier KeyUsage;
    public static final DERObjectIdentifier PrivateKeyUsagePeriod;
    public static final DERObjectIdentifier SubjectAlternativeName;
    public static final DERObjectIdentifier IssuerAlternativeName;
    public static final DERObjectIdentifier BasicConstraints;
    public static final DERObjectIdentifier CRLNumber;
    public static final DERObjectIdentifier ReasonCode;
    public static final DERObjectIdentifier InstructionCode;
    public static final DERObjectIdentifier InvalidityDate;
    public static final DERObjectIdentifier DeltaCRLIndicator;
    public static final DERObjectIdentifier IssuingDistributionPoint;
    public static final DERObjectIdentifier CertificateIssuer;
    public static final DERObjectIdentifier NameConstraints;
    public static final DERObjectIdentifier CRLDistributionPoints;
    public static final DERObjectIdentifier CertificatePolicies;
    public static final DERObjectIdentifier PolicyMappings;
    public static final DERObjectIdentifier AuthorityKeyIdentifier;
    public static final DERObjectIdentifier PolicyConstraints;
    public static final DERObjectIdentifier ExtendedKeyUsage;
    public static final DERObjectIdentifier InhibitAnyPolicy;
    public static final DERObjectIdentifier AuthorityInfoAccess;
    private Hashtable ab;
    private Vector bb;
    
    public static X509Extensions getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static X509Extensions getInstance(final Object o) {
        if (o == null || o instanceof X509Extensions) {
            return (X509Extensions)o;
        }
        if (o instanceof ASN1Sequence) {
            return new X509Extensions((ASN1Sequence)o);
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public X509Extensions(final ASN1Sequence asn1Sequence) {
        this.ab = new Hashtable();
        this.bb = new Vector();
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1Sequence asn1Sequence2 = objects.nextElement();
            if (asn1Sequence2.size() == 3) {
                this.ab.put(asn1Sequence2.getObjectAt(0), new X509Extension((DERBoolean)asn1Sequence2.getObjectAt(1), (ASN1OctetString)asn1Sequence2.getObjectAt(2)));
            }
            else {
                this.ab.put(asn1Sequence2.getObjectAt(0), new X509Extension(false, (ASN1OctetString)asn1Sequence2.getObjectAt(1)));
            }
            this.bb.addElement(asn1Sequence2.getObjectAt(0));
        }
    }
    
    public X509Extensions(final Hashtable hashtable) {
        this(null, hashtable);
    }
    
    public X509Extensions(final Vector vector, final Hashtable hashtable) {
        this.ab = new Hashtable();
        this.bb = new Vector();
        Enumeration enumeration;
        if (vector == null) {
            enumeration = hashtable.keys();
        }
        else {
            enumeration = vector.elements();
        }
        while (enumeration.hasMoreElements()) {
            this.bb.addElement(enumeration.nextElement());
        }
        final Enumeration elements = this.bb.elements();
        while (elements.hasMoreElements()) {
            final DERObjectIdentifier derObjectIdentifier = (DERObjectIdentifier)elements.nextElement();
            this.ab.put(derObjectIdentifier, hashtable.get(derObjectIdentifier));
        }
    }
    
    public Enumeration oids() {
        return this.bb.elements();
    }
    
    public X509Extension getExtension(final DERObjectIdentifier derObjectIdentifier) {
        return this.ab.get(derObjectIdentifier);
    }
    
    public DERObject getDERObject() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Enumeration elements = this.bb.elements();
        while (elements.hasMoreElements()) {
            final DERObjectIdentifier derObjectIdentifier = (DERObjectIdentifier)elements.nextElement();
            final X509Extension x509Extension = this.ab.get(derObjectIdentifier);
            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            asn1EncodableVector2.add(derObjectIdentifier);
            if (x509Extension.isCritical()) {
                asn1EncodableVector2.add(new DERBoolean(true));
            }
            asn1EncodableVector2.add(x509Extension.getValue());
            asn1EncodableVector.add(new DERSequence(asn1EncodableVector2));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    public int hashCode() {
        final Enumeration keys = this.ab.keys();
        int n = 0;
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            n = (n ^ nextElement.hashCode() ^ this.ab.get(nextElement).hashCode());
        }
        return n;
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof X509Extensions)) {
            return false;
        }
        final X509Extensions x509Extensions = (X509Extensions)o;
        final Enumeration keys = this.ab.keys();
        final Enumeration keys2 = x509Extensions.ab.keys();
        while (keys.hasMoreElements() && keys2.hasMoreElements()) {
            if (!keys.nextElement().equals(keys2.nextElement())) {
                return false;
            }
        }
        return !keys.hasMoreElements() && !keys2.hasMoreElements();
    }
    
    static {
        SubjectKeyIdentifier = new DERObjectIdentifier("2.5.29.14");
        KeyUsage = new DERObjectIdentifier("2.5.29.15");
        PrivateKeyUsagePeriod = new DERObjectIdentifier("2.5.29.16");
        SubjectAlternativeName = new DERObjectIdentifier("2.5.29.17");
        IssuerAlternativeName = new DERObjectIdentifier("2.5.29.18");
        BasicConstraints = new DERObjectIdentifier("2.5.29.19");
        CRLNumber = new DERObjectIdentifier("2.5.29.20");
        ReasonCode = new DERObjectIdentifier("2.5.29.21");
        InstructionCode = new DERObjectIdentifier("2.5.29.23");
        InvalidityDate = new DERObjectIdentifier("2.5.29.24");
        DeltaCRLIndicator = new DERObjectIdentifier("2.5.29.27");
        IssuingDistributionPoint = new DERObjectIdentifier("2.5.29.28");
        CertificateIssuer = new DERObjectIdentifier("2.5.29.29");
        NameConstraints = new DERObjectIdentifier("2.5.29.30");
        CRLDistributionPoints = new DERObjectIdentifier("2.5.29.31");
        CertificatePolicies = new DERObjectIdentifier("2.5.29.32");
        PolicyMappings = new DERObjectIdentifier("2.5.29.33");
        AuthorityKeyIdentifier = new DERObjectIdentifier("2.5.29.35");
        PolicyConstraints = new DERObjectIdentifier("2.5.29.36");
        ExtendedKeyUsage = new DERObjectIdentifier("2.5.29.37");
        InhibitAnyPolicy = new DERObjectIdentifier("2.5.29.54");
        AuthorityInfoAccess = new DERObjectIdentifier("1.3.6.1.5.5.7.1.1");
    }
}
