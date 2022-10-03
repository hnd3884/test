package org.bouncycastle.pkix;

import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

public class PKIXIdentity
{
    private final PrivateKeyInfo privateKeyInfo;
    private final X509CertificateHolder[] certificateHolders;
    
    public PKIXIdentity(final PrivateKeyInfo privateKeyInfo, final X509CertificateHolder[] array) {
        this.privateKeyInfo = privateKeyInfo;
        System.arraycopy(array, 0, this.certificateHolders = new X509CertificateHolder[array.length], 0, array.length);
    }
    
    public PrivateKeyInfo getPrivateKeyInfo() {
        return this.privateKeyInfo;
    }
    
    public X509CertificateHolder getCertificate() {
        return this.certificateHolders[0];
    }
    
    public RecipientId getRecipientId() {
        return new KeyTransRecipientId(this.certificateHolders[0].getIssuer(), this.certificateHolders[0].getSerialNumber(), this.getSubjectKeyIdentifier());
    }
    
    private byte[] getSubjectKeyIdentifier() {
        final SubjectKeyIdentifier fromExtensions = SubjectKeyIdentifier.fromExtensions(this.certificateHolders[0].getExtensions());
        if (fromExtensions == null) {
            return null;
        }
        return fromExtensions.getKeyIdentifier();
    }
}
