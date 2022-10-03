package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;

public class RevRepContentBuilder
{
    private ASN1EncodableVector status;
    private ASN1EncodableVector revCerts;
    private ASN1EncodableVector crls;
    
    public RevRepContentBuilder() {
        this.status = new ASN1EncodableVector();
        this.revCerts = new ASN1EncodableVector();
        this.crls = new ASN1EncodableVector();
    }
    
    public RevRepContentBuilder add(final PKIStatusInfo pkiStatusInfo) {
        this.status.add(pkiStatusInfo);
        return this;
    }
    
    public RevRepContentBuilder add(final PKIStatusInfo pkiStatusInfo, final CertId certId) {
        if (this.status.size() != this.revCerts.size()) {
            throw new IllegalStateException("status and revCerts sequence must be in common order");
        }
        this.status.add(pkiStatusInfo);
        this.revCerts.add(certId);
        return this;
    }
    
    public RevRepContentBuilder addCrl(final CertificateList list) {
        this.crls.add(list);
        return this;
    }
    
    public RevRepContent build() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new DERSequence(this.status));
        if (this.revCerts.size() != 0) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, new DERSequence(this.revCerts)));
        }
        if (this.crls.size() != 0) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, new DERSequence(this.crls)));
        }
        return RevRepContent.getInstance(new DERSequence(asn1EncodableVector));
    }
}
