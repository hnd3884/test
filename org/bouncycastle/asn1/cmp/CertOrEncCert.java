package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class CertOrEncCert extends ASN1Object implements ASN1Choice
{
    private CMPCertificate certificate;
    private EncryptedValue encryptedCert;
    
    private CertOrEncCert(final ASN1TaggedObject asn1TaggedObject) {
        if (asn1TaggedObject.getTagNo() == 0) {
            this.certificate = CMPCertificate.getInstance(asn1TaggedObject.getObject());
        }
        else {
            if (asn1TaggedObject.getTagNo() != 1) {
                throw new IllegalArgumentException("unknown tag: " + asn1TaggedObject.getTagNo());
            }
            this.encryptedCert = EncryptedValue.getInstance(asn1TaggedObject.getObject());
        }
    }
    
    public static CertOrEncCert getInstance(final Object o) {
        if (o instanceof CertOrEncCert) {
            return (CertOrEncCert)o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new CertOrEncCert((ASN1TaggedObject)o);
        }
        return null;
    }
    
    public CertOrEncCert(final CMPCertificate certificate) {
        if (certificate == null) {
            throw new IllegalArgumentException("'certificate' cannot be null");
        }
        this.certificate = certificate;
    }
    
    public CertOrEncCert(final EncryptedValue encryptedCert) {
        if (encryptedCert == null) {
            throw new IllegalArgumentException("'encryptedCert' cannot be null");
        }
        this.encryptedCert = encryptedCert;
    }
    
    public CMPCertificate getCertificate() {
        return this.certificate;
    }
    
    public EncryptedValue getEncryptedCert() {
        return this.encryptedCert;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.certificate != null) {
            return new DERTaggedObject(true, 0, this.certificate);
        }
        return new DERTaggedObject(true, 1, this.encryptedCert);
    }
}
