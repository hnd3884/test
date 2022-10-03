package org.bouncycastle.cert.crmf.jcajce;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x500.X500Name;
import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import org.bouncycastle.cert.crmf.CertificateRequestMessageBuilder;

public class JcaCertificateRequestMessageBuilder extends CertificateRequestMessageBuilder
{
    public JcaCertificateRequestMessageBuilder(final BigInteger bigInteger) {
        super(bigInteger);
    }
    
    public JcaCertificateRequestMessageBuilder setIssuer(final X500Principal x500Principal) {
        if (x500Principal != null) {
            this.setIssuer(X500Name.getInstance((Object)x500Principal.getEncoded()));
        }
        return this;
    }
    
    public JcaCertificateRequestMessageBuilder setSubject(final X500Principal x500Principal) {
        if (x500Principal != null) {
            this.setSubject(X500Name.getInstance((Object)x500Principal.getEncoded()));
        }
        return this;
    }
    
    public JcaCertificateRequestMessageBuilder setAuthInfoSender(final X500Principal x500Principal) {
        if (x500Principal != null) {
            this.setAuthInfoSender(new GeneralName(X500Name.getInstance((Object)x500Principal.getEncoded())));
        }
        return this;
    }
    
    public JcaCertificateRequestMessageBuilder setPublicKey(final PublicKey publicKey) {
        this.setPublicKey(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
        return this;
    }
}
