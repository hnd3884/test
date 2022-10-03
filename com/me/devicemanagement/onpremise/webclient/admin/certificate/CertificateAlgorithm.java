package com.me.devicemanagement.onpremise.webclient.admin.certificate;

import java.security.PublicKey;
import java.security.PrivateKey;

public interface CertificateAlgorithm
{
    boolean verifySignature(final String p0, final String p1);
    
    PrivateKey loadPrivateKeyFromFile(final String p0);
    
    PublicKey loadPublicKeyFromFile(final String p0);
}
