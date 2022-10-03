package org.bouncycastle.cms.jcajce;

import org.bouncycastle.asn1.x500.X500Name;
import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import org.bouncycastle.cms.KeyTransRecipientId;

public class JceKeyTransRecipientId extends KeyTransRecipientId
{
    public JceKeyTransRecipientId(final X509Certificate x509Certificate) {
        super(convertPrincipal(x509Certificate.getIssuerX500Principal()), x509Certificate.getSerialNumber(), CMSUtils.getSubjectKeyId(x509Certificate));
    }
    
    public JceKeyTransRecipientId(final X500Principal x500Principal, final BigInteger bigInteger) {
        super(convertPrincipal(x500Principal), bigInteger);
    }
    
    public JceKeyTransRecipientId(final X500Principal x500Principal, final BigInteger bigInteger, final byte[] array) {
        super(convertPrincipal(x500Principal), bigInteger, array);
    }
    
    private static X500Name convertPrincipal(final X500Principal x500Principal) {
        if (x500Principal == null) {
            return null;
        }
        return X500Name.getInstance((Object)x500Principal.getEncoded());
    }
}
