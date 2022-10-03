package org.bouncycastle.openssl.jcajce;

import org.bouncycastle.util.io.pem.PemGenerationException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.operator.OutputEncryptor;
import java.security.PrivateKey;
import org.bouncycastle.openssl.PKCS8Generator;

public class JcaPKCS8Generator extends PKCS8Generator
{
    public JcaPKCS8Generator(final PrivateKey privateKey, final OutputEncryptor outputEncryptor) throws PemGenerationException {
        super(PrivateKeyInfo.getInstance((Object)privateKey.getEncoded()), outputEncryptor);
    }
}
