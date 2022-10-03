package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class DefaultTlsSignerCredentials extends AbstractTlsSignerCredentials
{
    protected TlsContext context;
    protected Certificate certificate;
    protected AsymmetricKeyParameter privateKey;
    protected SignatureAndHashAlgorithm signatureAndHashAlgorithm;
    protected TlsSigner signer;
    
    public DefaultTlsSignerCredentials(final TlsContext tlsContext, final Certificate certificate, final AsymmetricKeyParameter asymmetricKeyParameter) {
        this(tlsContext, certificate, asymmetricKeyParameter, null);
    }
    
    public DefaultTlsSignerCredentials(final TlsContext context, final Certificate certificate, final AsymmetricKeyParameter privateKey, final SignatureAndHashAlgorithm signatureAndHashAlgorithm) {
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
        if (TlsUtils.isTLSv12(context) && signatureAndHashAlgorithm == null) {
            throw new IllegalArgumentException("'signatureAndHashAlgorithm' cannot be null for (D)TLS 1.2+");
        }
        if (privateKey instanceof RSAKeyParameters) {
            this.signer = new TlsRSASigner();
        }
        else if (privateKey instanceof DSAPrivateKeyParameters) {
            this.signer = new TlsDSSSigner();
        }
        else {
            if (!(privateKey instanceof ECPrivateKeyParameters)) {
                throw new IllegalArgumentException("'privateKey' type not supported: " + privateKey.getClass().getName());
            }
            this.signer = new TlsECDSASigner();
        }
        this.signer.init(context);
        this.context = context;
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.signatureAndHashAlgorithm = signatureAndHashAlgorithm;
    }
    
    public Certificate getCertificate() {
        return this.certificate;
    }
    
    public byte[] generateCertificateSignature(final byte[] array) throws IOException {
        try {
            if (TlsUtils.isTLSv12(this.context)) {
                return this.signer.generateRawSignature(this.signatureAndHashAlgorithm, this.privateKey, array);
            }
            return this.signer.generateRawSignature(this.privateKey, array);
        }
        catch (final CryptoException ex) {
            throw new TlsFatalAlert((short)80, ex);
        }
    }
    
    @Override
    public SignatureAndHashAlgorithm getSignatureAndHashAlgorithm() {
        return this.signatureAndHashAlgorithm;
    }
}
