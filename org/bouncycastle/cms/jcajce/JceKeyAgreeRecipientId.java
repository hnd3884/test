package org.bouncycastle.cms.jcajce;

import org.bouncycastle.asn1.x500.X500Name;
import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import org.bouncycastle.cms.KeyAgreeRecipientId;

public class JceKeyAgreeRecipientId extends KeyAgreeRecipientId
{
    public JceKeyAgreeRecipientId(final X509Certificate x509Certificate) {
        this(x509Certificate.getIssuerX500Principal(), x509Certificate.getSerialNumber());
    }
    
    public JceKeyAgreeRecipientId(final X500Principal x500Principal, final BigInteger bigInteger) {
        super(X500Name.getInstance((Object)x500Principal.getEncoded()), bigInteger);
    }
}
