package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.cmp.RevDetails;
import org.bouncycastle.asn1.ASN1Integer;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;

public class RevocationDetailsBuilder
{
    private CertTemplateBuilder templateBuilder;
    
    public RevocationDetailsBuilder() {
        this.templateBuilder = new CertTemplateBuilder();
    }
    
    public RevocationDetailsBuilder setPublicKey(final SubjectPublicKeyInfo publicKey) {
        if (publicKey != null) {
            this.templateBuilder.setPublicKey(publicKey);
        }
        return this;
    }
    
    public RevocationDetailsBuilder setIssuer(final X500Name issuer) {
        if (issuer != null) {
            this.templateBuilder.setIssuer(issuer);
        }
        return this;
    }
    
    public RevocationDetailsBuilder setSerialNumber(final BigInteger bigInteger) {
        if (bigInteger != null) {
            this.templateBuilder.setSerialNumber(new ASN1Integer(bigInteger));
        }
        return this;
    }
    
    public RevocationDetailsBuilder setSubject(final X500Name subject) {
        if (subject != null) {
            this.templateBuilder.setSubject(subject);
        }
        return this;
    }
    
    public RevocationDetails build() {
        return new RevocationDetails(new RevDetails(this.templateBuilder.build()));
    }
}
