package org.bouncycastle.cert.ocsp.jcajce;

import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.DigestCalculator;
import java.security.PublicKey;
import org.bouncycastle.cert.ocsp.BasicOCSPRespBuilder;

public class JcaBasicOCSPRespBuilder extends BasicOCSPRespBuilder
{
    public JcaBasicOCSPRespBuilder(final PublicKey publicKey, final DigestCalculator digestCalculator) throws OCSPException {
        super(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()), digestCalculator);
    }
}
