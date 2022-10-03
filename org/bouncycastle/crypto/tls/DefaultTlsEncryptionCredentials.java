package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class DefaultTlsEncryptionCredentials extends AbstractTlsEncryptionCredentials
{
    protected TlsContext context;
    protected Certificate certificate;
    protected AsymmetricKeyParameter privateKey;
    
    public DefaultTlsEncryptionCredentials(final TlsContext context, final Certificate certificate, final AsymmetricKeyParameter privateKey) {
        if (certificate == null) {
            throw new IllegalArgumentException("'certificate' cannot be null");
        }
        if (certificate.isEmpty()) {
            throw new IllegalArgumentException("'certificate' cannot be empty");
        }
        if (privateKey == null) {
            throw new IllegalArgumentException("'privateKey' cannot be null");
        }
        if (!privateKey.isPrivate()) {
            throw new IllegalArgumentException("'privateKey' must be private");
        }
        if (privateKey instanceof RSAKeyParameters) {
            this.context = context;
            this.certificate = certificate;
            this.privateKey = privateKey;
            return;
        }
        throw new IllegalArgumentException("'privateKey' type not supported: " + privateKey.getClass().getName());
    }
    
    public Certificate getCertificate() {
        return this.certificate;
    }
    
    public byte[] decryptPreMasterSecret(final byte[] array) throws IOException {
        return TlsRSAUtils.safeDecryptPreMasterSecret(this.context, (RSAKeyParameters)this.privateKey, array);
    }
}
