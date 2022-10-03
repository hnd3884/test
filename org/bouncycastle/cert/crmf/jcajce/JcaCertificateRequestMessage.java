package org.bouncycastle.cert.crmf.jcajce;

import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.PublicKey;
import org.bouncycastle.asn1.x500.X500Name;
import java.io.IOException;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;

public class JcaCertificateRequestMessage extends CertificateRequestMessage
{
    private CRMFHelper helper;
    
    public JcaCertificateRequestMessage(final byte[] array) {
        this(CertReqMsg.getInstance((Object)array));
    }
    
    public JcaCertificateRequestMessage(final CertificateRequestMessage certificateRequestMessage) {
        this(certificateRequestMessage.toASN1Structure());
    }
    
    public JcaCertificateRequestMessage(final CertReqMsg certReqMsg) {
        super(certReqMsg);
        this.helper = new CRMFHelper((JcaJceHelper)new DefaultJcaJceHelper());
    }
    
    public JcaCertificateRequestMessage setProvider(final String s) {
        this.helper = new CRMFHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public JcaCertificateRequestMessage setProvider(final Provider provider) {
        this.helper = new CRMFHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public X500Principal getSubjectX500Principal() {
        final X500Name subject = this.getCertTemplate().getSubject();
        if (subject != null) {
            try {
                return new X500Principal(subject.getEncoded("DER"));
            }
            catch (final IOException ex) {
                throw new IllegalStateException("unable to construct DER encoding of name: " + ex.getMessage());
            }
        }
        return null;
    }
    
    public PublicKey getPublicKey() throws CRMFException {
        final SubjectPublicKeyInfo publicKey = this.getCertTemplate().getPublicKey();
        if (publicKey != null) {
            return this.helper.toPublicKey(publicKey);
        }
        return null;
    }
}
