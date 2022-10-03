package org.bouncycastle.cms.bc;

import java.io.IOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.bc.BcAsymmetricKeyWrapper;
import org.bouncycastle.operator.bc.BcRSAAsymmetricKeyWrapper;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class BcRSAKeyTransRecipientInfoGenerator extends BcKeyTransRecipientInfoGenerator
{
    public BcRSAKeyTransRecipientInfoGenerator(final byte[] array, final AlgorithmIdentifier algorithmIdentifier, final AsymmetricKeyParameter asymmetricKeyParameter) {
        super(array, new BcRSAAsymmetricKeyWrapper(algorithmIdentifier, asymmetricKeyParameter));
    }
    
    public BcRSAKeyTransRecipientInfoGenerator(final X509CertificateHolder x509CertificateHolder) throws IOException {
        super(x509CertificateHolder, new BcRSAAsymmetricKeyWrapper(x509CertificateHolder.getSubjectPublicKeyInfo().getAlgorithmId(), x509CertificateHolder.getSubjectPublicKeyInfo()));
    }
}
