package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class TlsDSSSigner extends TlsDSASigner
{
    public boolean isValidPublicKey(final AsymmetricKeyParameter asymmetricKeyParameter) {
        return asymmetricKeyParameter instanceof DSAPublicKeyParameters;
    }
    
    @Override
    protected DSA createDSAImpl(final short n) {
        return new DSASigner(new HMacDSAKCalculator(TlsUtils.createHash(n)));
    }
    
    @Override
    protected short getSignatureAlgorithm() {
        return 2;
    }
}
