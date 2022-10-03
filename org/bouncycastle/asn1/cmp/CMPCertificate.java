package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class CMPCertificate extends ASN1Object implements ASN1Choice
{
    private Certificate x509v3PKCert;
    private int otherTagValue;
    private ASN1Object otherCert;
    
    @Deprecated
    public CMPCertificate(final AttributeCertificate attributeCertificate) {
        this(1, attributeCertificate);
    }
    
    public CMPCertificate(final int otherTagValue, final ASN1Object otherCert) {
        this.otherTagValue = otherTagValue;
        this.otherCert = otherCert;
    }
    
    public CMPCertificate(final Certificate x509v3PKCert) {
        if (x509v3PKCert.getVersionNumber() != 3) {
            throw new IllegalArgumentException("only version 3 certificates allowed");
        }
        this.x509v3PKCert = x509v3PKCert;
    }
    
    public static CMPCertificate getInstance(Object fromByteArray) {
        if (fromByteArray == null || fromByteArray instanceof CMPCertificate) {
            return (CMPCertificate)fromByteArray;
        }
        if (fromByteArray instanceof byte[]) {
            try {
                fromByteArray = ASN1Primitive.fromByteArray((byte[])fromByteArray);
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("Invalid encoding in CMPCertificate");
            }
        }
        if (fromByteArray instanceof ASN1Sequence) {
            return new CMPCertificate(Certificate.getInstance(fromByteArray));
        }
        if (fromByteArray instanceof ASN1TaggedObject) {
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)fromByteArray;
            return new CMPCertificate(asn1TaggedObject.getTagNo(), asn1TaggedObject.getObject());
        }
        throw new IllegalArgumentException("Invalid object: " + fromByteArray.getClass().getName());
    }
    
    public boolean isX509v3PKCert() {
        return this.x509v3PKCert != null;
    }
    
    public Certificate getX509v3PKCert() {
        return this.x509v3PKCert;
    }
    
    @Deprecated
    public AttributeCertificate getX509v2AttrCert() {
        return AttributeCertificate.getInstance(this.otherCert);
    }
    
    public int getOtherCertTag() {
        return this.otherTagValue;
    }
    
    public ASN1Object getOtherCert() {
        return this.otherCert;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.otherCert != null) {
            return new DERTaggedObject(true, this.otherTagValue, this.otherCert);
        }
        return this.x509v3PKCert.toASN1Primitive();
    }
}
