package org.bouncycastle.cert.ocsp.jcajce;

import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.DigestCalculator;
import java.security.PublicKey;
import org.bouncycastle.asn1.x500.X500Name;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.cert.ocsp.RespID;

public class JcaRespID extends RespID
{
    public JcaRespID(final X500Principal x500Principal) {
        super(X500Name.getInstance((Object)x500Principal.getEncoded()));
    }
    
    public JcaRespID(final PublicKey publicKey, final DigestCalculator digestCalculator) throws OCSPException {
        super(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()), digestCalculator);
    }
}
