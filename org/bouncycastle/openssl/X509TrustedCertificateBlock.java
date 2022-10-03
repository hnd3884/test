package org.bouncycastle.openssl;

import org.bouncycastle.util.Arrays;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.cert.X509CertificateHolder;

public class X509TrustedCertificateBlock
{
    private final X509CertificateHolder certificateHolder;
    private final CertificateTrustBlock trustBlock;
    
    public X509TrustedCertificateBlock(final X509CertificateHolder certificateHolder, final CertificateTrustBlock trustBlock) {
        this.certificateHolder = certificateHolder;
        this.trustBlock = trustBlock;
    }
    
    public X509TrustedCertificateBlock(final byte[] array) throws IOException {
        final ASN1InputStream asn1InputStream = new ASN1InputStream(array);
        this.certificateHolder = new X509CertificateHolder(asn1InputStream.readObject().getEncoded());
        this.trustBlock = new CertificateTrustBlock(asn1InputStream.readObject().getEncoded());
    }
    
    public byte[] getEncoded() throws IOException {
        return Arrays.concatenate(this.certificateHolder.getEncoded(), this.trustBlock.toASN1Sequence().getEncoded());
    }
    
    public X509CertificateHolder getCertificateHolder() {
        return this.certificateHolder;
    }
    
    public CertificateTrustBlock getTrustBlock() {
        return this.trustBlock;
    }
}
