package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class TlsECDSASigner extends TlsDSASigner
{
    public boolean isValidPublicKey(final AsymmetricKeyParameter asymmetricKeyParameter) {
        return asymmetricKeyParameter instanceof ECPublicKeyParameters;
    }
    
    @Override
    protected DSA createDSAImpl(final short n) {
        return new ECDSASigner(new HMacDSAKCalculator(TlsUtils.createHash(n)));
    }
    
    @Override
    protected short getSignatureAlgorithm() {
        return 3;
    }
}
