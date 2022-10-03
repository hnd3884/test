package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import java.util.Vector;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class X509Extensions extends ASN1Object
{
    @Deprecated
    public static final ASN1ObjectIdentifier SubjectDirectoryAttributes;
    @Deprecated
    public static final ASN1ObjectIdentifier SubjectKeyIdentifier;
    @Deprecated
    public static final ASN1ObjectIdentifier KeyUsage;
    @Deprecated
    public static final ASN1ObjectIdentifier PrivateKeyUsagePeriod;
    @Deprecated
    public static final ASN1ObjectIdentifier SubjectAlternativeName;
    @Deprecated
    public static final ASN1ObjectIdentifier IssuerAlternativeName;
    @Deprecated
    public static final ASN1ObjectIdentifier BasicConstraints;
    @Deprecated
    public static final ASN1ObjectIdentifier CRLNumber;
    @Deprecated
    public static final ASN1ObjectIdentifier ReasonCode;
    @Deprecated
    public static final ASN1ObjectIdentifier InstructionCode;
    @Deprecated
    public static final ASN1ObjectIdentifier InvalidityDate;
    @Deprecated
    public static final ASN1ObjectIdentifier DeltaCRLIndicator;
    @Deprecated
    public static final ASN1ObjectIdentifier IssuingDistributionPoint;
    @Deprecated
    public static final ASN1ObjectIdentifier CertificateIssuer;
    @Deprecated
    public static final ASN1ObjectIdentifier NameConstraints;
    @Deprecated
    public static final ASN1ObjectIdentifier CRLDistributionPoints;
    @Deprecated
    public static final ASN1ObjectIdentifier CertificatePolicies;
    @Deprecated
    public static final ASN1ObjectIdentifier PolicyMappings;
    @Deprecated
    public static final ASN1ObjectIdentifier AuthorityKeyIdentifier;
    @Deprecated
    public static final ASN1ObjectIdentifier PolicyConstraints;
    @Deprecated
    public static final ASN1ObjectIdentifier ExtendedKeyUsage;
    @Deprecated
    public static final ASN1ObjectIdentifier FreshestCRL;
    @Deprecated
    public static final ASN1ObjectIdentifier InhibitAnyPolicy;
    @Deprecated
    public static final ASN1ObjectIdentifier AuthorityInfoAccess;
    @Deprecated
    public static final ASN1ObjectIdentifier SubjectInfoAccess;
    @Deprecated
    public static final ASN1ObjectIdentifier LogoType;
    @Deprecated
    public static final ASN1ObjectIdentifier BiometricInfo;
    @Deprecated
    public static final ASN1ObjectIdentifier QCStatements;
    @Deprecated
    public static final ASN1ObjectIdentifier AuditIdentity;
    @Deprecated
    public static final ASN1ObjectIdentifier NoRevAvail;
    @Deprecated
    public static final ASN1ObjectIdentifier TargetInformation;
    private Hashtable extensions;
    private Vector ordering;
    
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
        if (o instanceof Extensions) {
            return new X509Extensions((ASN1Sequence)((Extensions)o).toASN1Primitive());
        }
        if (o instanceof ASN1TaggedObject) {
            return getInstance(((ASN1TaggedObject)o).getObject());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public X509Extensions(final ASN1Sequence asn1Sequence) {
        this.extensions = new Hashtable();
        this.ordering = new Vector();
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1Sequence instance = ASN1Sequence.getInstance(objects.nextElement());
            if (instance.size() == 3) {
                this.extensions.put(instance.getObjectAt(0), new X509Extension(ASN1Boolean.getInstance(instance.getObjectAt(1)), ASN1OctetString.getInstance(instance.getObjectAt(2))));
            }
            else {
                if (instance.size() != 2) {
                    throw new IllegalArgumentException("Bad sequence size: " + instance.size());
                }
                this.extensions.put(instance.getObjectAt(0), new X509Extension(false, ASN1OctetString.getInstance(instance.getObjectAt(1))));
            }
            this.ordering.addElement(instance.getObjectAt(0));
        }
    }
    
    public X509Extensions(final Hashtable hashtable) {
        this(null, hashtable);
    }
    
    @Deprecated
    public X509Extensions(final Vector vector, final Hashtable hashtable) {
        this.extensions = new Hashtable();
        this.ordering = new Vector();
        Enumeration enumeration;
        if (vector == null) {
            enumeration = hashtable.keys();
        }
        else {
            enumeration = vector.elements();
        }
        while (enumeration.hasMoreElements()) {
            this.ordering.addElement(ASN1ObjectIdentifier.getInstance(enumeration.nextElement()));
        }
        final Enumeration elements = this.ordering.elements();
        while (elements.hasMoreElements()) {
            final ASN1ObjectIdentifier instance = ASN1ObjectIdentifier.getInstance(elements.nextElement());
            this.extensions.put(instance, hashtable.get(instance));
        }
    }
    
    @Deprecated
    public X509Extensions(final Vector vector, final Vector vector2) {
        this.extensions = new Hashtable();
        this.ordering = new Vector();
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            this.ordering.addElement(elements.nextElement());
        }
        int n = 0;
        final Enumeration elements2 = this.ordering.elements();
        while (elements2.hasMoreElements()) {
            this.extensions.put(elements2.nextElement(), vector2.elementAt(n));
            ++n;
        }
    }
    
    public Enumeration oids() {
        return this.ordering.elements();
    }
    
    public X509Extension getExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return this.extensions.get(asn1ObjectIdentifier);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Enumeration elements = this.ordering.elements();
        while (elements.hasMoreElements()) {
            final ASN1ObjectIdentifier asn1ObjectIdentifier = (ASN1ObjectIdentifier)elements.nextElement();
            final X509Extension x509Extension = this.extensions.get(asn1ObjectIdentifier);
            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            asn1EncodableVector2.add(asn1ObjectIdentifier);
            if (x509Extension.isCritical()) {
                asn1EncodableVector2.add(ASN1Boolean.TRUE);
            }
            asn1EncodableVector2.add(x509Extension.getValue());
            asn1EncodableVector.add(new DERSequence(asn1EncodableVector2));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    public boolean equivalent(final X509Extensions x509Extensions) {
        if (this.extensions.size() != x509Extensions.extensions.size()) {
            return false;
        }
        final Enumeration keys = this.extensions.keys();
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            if (!this.extensions.get(nextElement).equals(x509Extensions.extensions.get(nextElement))) {
                return false;
            }
        }
        return true;
    }
    
    public ASN1ObjectIdentifier[] getExtensionOIDs() {
        return this.toOidArray(this.ordering);
    }
    
    public ASN1ObjectIdentifier[] getNonCriticalExtensionOIDs() {
        return this.getExtensionOIDs(false);
    }
    
    public ASN1ObjectIdentifier[] getCriticalExtensionOIDs() {
        return this.getExtensionOIDs(true);
    }
    
    private ASN1ObjectIdentifier[] getExtensionOIDs(final boolean b) {
        final Vector vector = new Vector();
        for (int i = 0; i != this.ordering.size(); ++i) {
            final Object element = this.ordering.elementAt(i);
            if (((X509Extension)this.extensions.get(element)).isCritical() == b) {
                vector.addElement(element);
            }
        }
        return this.toOidArray(vector);
    }
    
    private ASN1ObjectIdentifier[] toOidArray(final Vector vector) {
        final ASN1ObjectIdentifier[] array = new ASN1ObjectIdentifier[vector.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = (ASN1ObjectIdentifier)vector.elementAt(i);
        }
        return array;
    }
    
    static {
        SubjectDirectoryAttributes = new ASN1ObjectIdentifier("2.5.29.9");
        SubjectKeyIdentifier = new ASN1ObjectIdentifier("2.5.29.14");
        KeyUsage = new ASN1ObjectIdentifier("2.5.29.15");
        PrivateKeyUsagePeriod = new ASN1ObjectIdentifier("2.5.29.16");
        SubjectAlternativeName = new ASN1ObjectIdentifier("2.5.29.17");
        IssuerAlternativeName = new ASN1ObjectIdentifier("2.5.29.18");
        BasicConstraints = new ASN1ObjectIdentifier("2.5.29.19");
        CRLNumber = new ASN1ObjectIdentifier("2.5.29.20");
        ReasonCode = new ASN1ObjectIdentifier("2.5.29.21");
        InstructionCode = new ASN1ObjectIdentifier("2.5.29.23");
        InvalidityDate = new ASN1ObjectIdentifier("2.5.29.24");
        DeltaCRLIndicator = new ASN1ObjectIdentifier("2.5.29.27");
        IssuingDistributionPoint = new ASN1ObjectIdentifier("2.5.29.28");
        CertificateIssuer = new ASN1ObjectIdentifier("2.5.29.29");
        NameConstraints = new ASN1ObjectIdentifier("2.5.29.30");
        CRLDistributionPoints = new ASN1ObjectIdentifier("2.5.29.31");
        CertificatePolicies = new ASN1ObjectIdentifier("2.5.29.32");
        PolicyMappings = new ASN1ObjectIdentifier("2.5.29.33");
        AuthorityKeyIdentifier = new ASN1ObjectIdentifier("2.5.29.35");
        PolicyConstraints = new ASN1ObjectIdentifier("2.5.29.36");
        ExtendedKeyUsage = new ASN1ObjectIdentifier("2.5.29.37");
        FreshestCRL = new ASN1ObjectIdentifier("2.5.29.46");
        InhibitAnyPolicy = new ASN1ObjectIdentifier("2.5.29.54");
        AuthorityInfoAccess = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.1");
        SubjectInfoAccess = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.11");
        LogoType = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.12");
        BiometricInfo = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.2");
        QCStatements = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.3");
        AuditIdentity = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.4");
        NoRevAvail = new ASN1ObjectIdentifier("2.5.29.56");
        TargetInformation = new ASN1ObjectIdentifier("2.5.29.55");
    }
}
