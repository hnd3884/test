package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class AttributeCertificateInfo extends ASN1Object
{
    private ASN1Integer version;
    private Holder holder;
    private AttCertIssuer issuer;
    private AlgorithmIdentifier signature;
    private ASN1Integer serialNumber;
    private AttCertValidityPeriod attrCertValidityPeriod;
    private ASN1Sequence attributes;
    private DERBitString issuerUniqueID;
    private Extensions extensions;
    
    public static AttributeCertificateInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static AttributeCertificateInfo getInstance(final Object o) {
        if (o instanceof AttributeCertificateInfo) {
            return (AttributeCertificateInfo)o;
        }
        if (o != null) {
            return new AttributeCertificateInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private AttributeCertificateInfo(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 6 || asn1Sequence.size() > 9) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        int n;
        if (asn1Sequence.getObjectAt(0) instanceof ASN1Integer) {
            this.version = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
            n = 1;
        }
        else {
            this.version = new ASN1Integer(0L);
            n = 0;
        }
        this.holder = Holder.getInstance(asn1Sequence.getObjectAt(n));
        this.issuer = AttCertIssuer.getInstance(asn1Sequence.getObjectAt(n + 1));
        this.signature = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(n + 2));
        this.serialNumber = ASN1Integer.getInstance(asn1Sequence.getObjectAt(n + 3));
        this.attrCertValidityPeriod = AttCertValidityPeriod.getInstance(asn1Sequence.getObjectAt(n + 4));
        this.attributes = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(n + 5));
        for (int i = n + 6; i < asn1Sequence.size(); ++i) {
            final ASN1Encodable object = asn1Sequence.getObjectAt(i);
            if (object instanceof DERBitString) {
                this.issuerUniqueID = DERBitString.getInstance(asn1Sequence.getObjectAt(i));
            }
            else if (object instanceof ASN1Sequence || object instanceof Extensions) {
                this.extensions = Extensions.getInstance(asn1Sequence.getObjectAt(i));
            }
        }
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public Holder getHolder() {
        return this.holder;
    }
    
    public AttCertIssuer getIssuer() {
        return this.issuer;
    }
    
    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }
    
    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }
    
    public AttCertValidityPeriod getAttrCertValidityPeriod() {
        return this.attrCertValidityPeriod;
    }
    
    public ASN1Sequence getAttributes() {
        return this.attributes;
    }
    
    public DERBitString getIssuerUniqueID() {
        return this.issuerUniqueID;
    }
    
    public Extensions getExtensions() {
        return this.extensions;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.version.getValue().intValue() != 0) {
            asn1EncodableVector.add(this.version);
        }
        asn1EncodableVector.add(this.holder);
        asn1EncodableVector.add(this.issuer);
        asn1EncodableVector.add(this.signature);
        asn1EncodableVector.add(this.serialNumber);
        asn1EncodableVector.add(this.attrCertValidityPeriod);
        asn1EncodableVector.add(this.attributes);
        if (this.issuerUniqueID != null) {
            asn1EncodableVector.add(this.issuerUniqueID);
        }
        if (this.extensions != null) {
            asn1EncodableVector.add(this.extensions);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
