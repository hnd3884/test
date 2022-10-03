package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;

public class V2AttributeCertificateInfoGenerator
{
    private ASN1Integer version;
    private Holder holder;
    private AttCertIssuer issuer;
    private AlgorithmIdentifier signature;
    private ASN1Integer serialNumber;
    private ASN1EncodableVector attributes;
    private DERBitString issuerUniqueID;
    private Extensions extensions;
    private ASN1GeneralizedTime startDate;
    private ASN1GeneralizedTime endDate;
    
    public V2AttributeCertificateInfoGenerator() {
        this.version = new ASN1Integer(1L);
        this.attributes = new ASN1EncodableVector();
    }
    
    public void setHolder(final Holder holder) {
        this.holder = holder;
    }
    
    public void addAttribute(final String s, final ASN1Encodable asn1Encodable) {
        this.attributes.add(new Attribute(new ASN1ObjectIdentifier(s), new DERSet(asn1Encodable)));
    }
    
    public void addAttribute(final Attribute attribute) {
        this.attributes.add(attribute);
    }
    
    public void setSerialNumber(final ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public void setSignature(final AlgorithmIdentifier signature) {
        this.signature = signature;
    }
    
    public void setIssuer(final AttCertIssuer issuer) {
        this.issuer = issuer;
    }
    
    public void setStartDate(final ASN1GeneralizedTime startDate) {
        this.startDate = startDate;
    }
    
    public void setEndDate(final ASN1GeneralizedTime endDate) {
        this.endDate = endDate;
    }
    
    public void setIssuerUniqueID(final DERBitString issuerUniqueID) {
        this.issuerUniqueID = issuerUniqueID;
    }
    
    @Deprecated
    public void setExtensions(final X509Extensions x509Extensions) {
        this.extensions = Extensions.getInstance(x509Extensions.toASN1Primitive());
    }
    
    public void setExtensions(final Extensions extensions) {
        this.extensions = extensions;
    }
    
    public AttributeCertificateInfo generateAttributeCertificateInfo() {
        if (this.serialNumber == null || this.signature == null || this.issuer == null || this.startDate == null || this.endDate == null || this.holder == null || this.attributes == null) {
            throw new IllegalStateException("not all mandatory fields set in V2 AttributeCertificateInfo generator");
        }
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.holder);
        asn1EncodableVector.add(this.issuer);
        asn1EncodableVector.add(this.signature);
        asn1EncodableVector.add(this.serialNumber);
        asn1EncodableVector.add(new AttCertValidityPeriod(this.startDate, this.endDate));
        asn1EncodableVector.add(new DERSequence(this.attributes));
        if (this.issuerUniqueID != null) {
            asn1EncodableVector.add(this.issuerUniqueID);
        }
        if (this.extensions != null) {
            asn1EncodableVector.add(this.extensions);
        }
        return AttributeCertificateInfo.getInstance(new DERSequence(asn1EncodableVector));
    }
}
