package org.bouncycastle.cert.crmf.jcajce;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x500.X500Name;
import java.security.PrivateKey;
import org.bouncycastle.cert.crmf.PKIArchiveControlBuilder;

public class JcaPKIArchiveControlBuilder extends PKIArchiveControlBuilder
{
    public JcaPKIArchiveControlBuilder(final PrivateKey privateKey, final X500Name x500Name) {
        this(privateKey, new GeneralName(x500Name));
    }
    
    public JcaPKIArchiveControlBuilder(final PrivateKey privateKey, final X500Principal x500Principal) {
        this(privateKey, X500Name.getInstance((Object)x500Principal.getEncoded()));
    }
    
    public JcaPKIArchiveControlBuilder(final PrivateKey privateKey, final GeneralName generalName) {
        super(PrivateKeyInfo.getInstance((Object)privateKey.getEncoded()), generalName);
    }
}
