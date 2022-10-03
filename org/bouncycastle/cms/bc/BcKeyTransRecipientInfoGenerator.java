package org.bouncycastle.cms.bc;

import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.operator.bc.BcAsymmetricKeyWrapper;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.KeyTransRecipientInfoGenerator;

public abstract class BcKeyTransRecipientInfoGenerator extends KeyTransRecipientInfoGenerator
{
    public BcKeyTransRecipientInfoGenerator(final X509CertificateHolder x509CertificateHolder, final BcAsymmetricKeyWrapper bcAsymmetricKeyWrapper) {
        super(new IssuerAndSerialNumber(x509CertificateHolder.toASN1Structure()), bcAsymmetricKeyWrapper);
    }
    
    public BcKeyTransRecipientInfoGenerator(final byte[] array, final BcAsymmetricKeyWrapper bcAsymmetricKeyWrapper) {
        super(array, bcAsymmetricKeyWrapper);
    }
}
