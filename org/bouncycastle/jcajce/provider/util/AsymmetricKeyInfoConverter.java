package org.bouncycastle.jcajce.provider.util;

import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.io.IOException;
import java.security.PrivateKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

public interface AsymmetricKeyInfoConverter
{
    PrivateKey generatePrivate(final PrivateKeyInfo p0) throws IOException;
    
    PublicKey generatePublic(final SubjectPublicKeyInfo p0) throws IOException;
}
