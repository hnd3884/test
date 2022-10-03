package org.bouncycastle.crypto.tls;

import java.math.BigInteger;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class DefaultTlsAgreementCredentials extends AbstractTlsAgreementCredentials
{
    protected Certificate certificate;
    protected AsymmetricKeyParameter privateKey;
    protected BasicAgreement basicAgreement;
    protected boolean truncateAgreement;
    
    public DefaultTlsAgreementCredentials(final Certificate certificate, final AsymmetricKeyParameter privateKey) {
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
        if (privateKey instanceof DHPrivateKeyParameters) {
            this.basicAgreement = new DHBasicAgreement();
            this.truncateAgreement = true;
        }
        else {
            if (!(privateKey instanceof ECPrivateKeyParameters)) {
                throw new IllegalArgumentException("'privateKey' type not supported: " + privateKey.getClass().getName());
            }
            this.basicAgreement = new ECDHBasicAgreement();
            this.truncateAgreement = false;
        }
        this.certificate = certificate;
        this.privateKey = privateKey;
    }
    
    public Certificate getCertificate() {
        return this.certificate;
    }
    
    public byte[] generateAgreement(final AsymmetricKeyParameter asymmetricKeyParameter) {
        this.basicAgreement.init(this.privateKey);
        final BigInteger calculateAgreement = this.basicAgreement.calculateAgreement(asymmetricKeyParameter);
        if (this.truncateAgreement) {
            return BigIntegers.asUnsignedByteArray(calculateAgreement);
        }
        return BigIntegers.asUnsignedByteArray(this.basicAgreement.getFieldSize(), calculateAgreement);
    }
}
