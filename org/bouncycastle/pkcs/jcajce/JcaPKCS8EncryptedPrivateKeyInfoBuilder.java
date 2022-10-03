package org.bouncycastle.pkcs.jcajce;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.PrivateKey;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfoBuilder;

public class JcaPKCS8EncryptedPrivateKeyInfoBuilder extends PKCS8EncryptedPrivateKeyInfoBuilder
{
    public JcaPKCS8EncryptedPrivateKeyInfoBuilder(final PrivateKey privateKey) {
        super(PrivateKeyInfo.getInstance((Object)privateKey.getEncoded()));
    }
}
