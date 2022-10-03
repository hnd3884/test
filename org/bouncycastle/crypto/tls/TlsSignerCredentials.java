package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface TlsSignerCredentials extends TlsCredentials
{
    byte[] generateCertificateSignature(final byte[] p0) throws IOException;
    
    SignatureAndHashAlgorithm getSignatureAndHashAlgorithm();
}
