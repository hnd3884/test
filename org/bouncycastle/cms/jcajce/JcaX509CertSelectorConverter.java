package org.bouncycastle.cms.jcajce;

import org.bouncycastle.cms.SignerId;
import java.security.cert.X509CertSelector;
import org.bouncycastle.cms.KeyTransRecipientId;

public class JcaX509CertSelectorConverter extends org.bouncycastle.cert.selector.jcajce.JcaX509CertSelectorConverter
{
    public X509CertSelector getCertSelector(final KeyTransRecipientId keyTransRecipientId) {
        return this.doConversion(keyTransRecipientId.getIssuer(), keyTransRecipientId.getSerialNumber(), keyTransRecipientId.getSubjectKeyIdentifier());
    }
    
    public X509CertSelector getCertSelector(final SignerId signerId) {
        return this.doConversion(signerId.getIssuer(), signerId.getSerialNumber(), signerId.getSubjectKeyIdentifier());
    }
}
