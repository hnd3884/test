package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class SignerInfo extends ASN1Object
{
    private ASN1Integer version;
    private IssuerAndSerialNumber issuerAndSerialNumber;
    private AlgorithmIdentifier digAlgorithm;
    private ASN1Set authenticatedAttributes;
    private AlgorithmIdentifier digEncryptionAlgorithm;
    private ASN1OctetString encryptedDigest;
    private ASN1Set unauthenticatedAttributes;
    
    public static SignerInfo getInstance(final Object o) {
        if (o instanceof SignerInfo) {
            return (SignerInfo)o;
        }
        if (o instanceof ASN1Sequence) {
            return new SignerInfo((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }
    
    public SignerInfo(final ASN1Integer version, final IssuerAndSerialNumber issuerAndSerialNumber, final AlgorithmIdentifier digAlgorithm, final ASN1Set authenticatedAttributes, final AlgorithmIdentifier digEncryptionAlgorithm, final ASN1OctetString encryptedDigest, final ASN1Set unauthenticatedAttributes) {
        this.version = version;
        this.issuerAndSerialNumber = issuerAndSerialNumber;
        this.digAlgorithm = digAlgorithm;
        this.authenticatedAttributes = authenticatedAttributes;
        this.digEncryptionAlgorithm = digEncryptionAlgorithm;
        this.encryptedDigest = encryptedDigest;
        this.unauthenticatedAttributes = unauthenticatedAttributes;
    }
    
    public SignerInfo(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.version = (ASN1Integer)objects.nextElement();
        this.issuerAndSerialNumber = IssuerAndSerialNumber.getInstance(objects.nextElement());
        this.digAlgorithm = AlgorithmIdentifier.getInstance(objects.nextElement());
        final Object nextElement = objects.nextElement();
        if (nextElement instanceof ASN1TaggedObject) {
            this.authenticatedAttributes = ASN1Set.getInstance((ASN1TaggedObject)nextElement, false);
            this.digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(objects.nextElement());
        }
        else {
            this.authenticatedAttributes = null;
            this.digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(nextElement);
        }
        this.encryptedDigest = ASN1OctetString.getInstance(objects.nextElement());
        if (objects.hasMoreElements()) {
            this.unauthenticatedAttributes = ASN1Set.getInstance((ASN1TaggedObject)objects.nextElement(), false);
        }
        else {
            this.unauthenticatedAttributes = null;
        }
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        return this.issuerAndSerialNumber;
    }
    
    public ASN1Set getAuthenticatedAttributes() {
        return this.authenticatedAttributes;
    }
    
    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digAlgorithm;
    }
    
    public ASN1OctetString getEncryptedDigest() {
        return this.encryptedDigest;
    }
    
    public AlgorithmIdentifier getDigestEncryptionAlgorithm() {
        return this.digEncryptionAlgorithm;
    }
    
    public ASN1Set getUnauthenticatedAttributes() {
        return this.unauthenticatedAttributes;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.issuerAndSerialNumber);
        asn1EncodableVector.add(this.digAlgorithm);
        if (this.authenticatedAttributes != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.authenticatedAttributes));
        }
        asn1EncodableVector.add(this.digEncryptionAlgorithm);
        asn1EncodableVector.add(this.encryptedDigest);
        if (this.unauthenticatedAttributes != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.unauthenticatedAttributes));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
