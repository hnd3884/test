package org.bouncycastle.pkcs.bc;

import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public class BcPKCS10CertificationRequest extends PKCS10CertificationRequest
{
    public BcPKCS10CertificationRequest(final CertificationRequest certificationRequest) {
        super(certificationRequest);
    }
    
    public BcPKCS10CertificationRequest(final byte[] array) throws IOException {
        super(array);
    }
    
    public BcPKCS10CertificationRequest(final PKCS10CertificationRequest pkcs10CertificationRequest) {
        super(pkcs10CertificationRequest.toASN1Structure());
    }
    
    public AsymmetricKeyParameter getPublicKey() throws PKCSException {
        try {
            return PublicKeyFactory.createKey(this.getSubjectPublicKeyInfo());
        }
        catch (final IOException ex) {
            throw new PKCSException("error extracting key encoding: " + ex.getMessage(), ex);
        }
    }
}
