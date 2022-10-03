package org.bouncycastle.pkcs.jcajce;

import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.PublicKey;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

public class JcaPKCS10CertificationRequestBuilder extends PKCS10CertificationRequestBuilder
{
    public JcaPKCS10CertificationRequestBuilder(final X500Name x500Name, final PublicKey publicKey) {
        super(x500Name, SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
    
    public JcaPKCS10CertificationRequestBuilder(final X500Principal x500Principal, final PublicKey publicKey) {
        super(X500Name.getInstance((Object)x500Principal.getEncoded()), SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
}
