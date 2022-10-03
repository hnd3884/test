package org.bouncycastle.crypto.ec;

import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.ECMultiplier;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

public class ECNewPublicKeyTransform implements ECPairTransform
{
    private ECPublicKeyParameters key;
    private SecureRandom random;
    
    public void init(final CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithRandom) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            if (!(parametersWithRandom.getParameters() instanceof ECPublicKeyParameters)) {
                throw new IllegalArgumentException("ECPublicKeyParameters are required for new public key transform.");
            }
            this.key = (ECPublicKeyParameters)parametersWithRandom.getParameters();
            this.random = parametersWithRandom.getRandom();
        }
        else {
            if (!(cipherParameters instanceof ECPublicKeyParameters)) {
                throw new IllegalArgumentException("ECPublicKeyParameters are required for new public key transform.");
            }
            this.key = (ECPublicKeyParameters)cipherParameters;
            this.random = new SecureRandom();
        }
    }
    
    public ECPair transform(final ECPair ecPair) {
        if (this.key == null) {
            throw new IllegalStateException("ECNewPublicKeyTransform not initialised");
        }
        final ECDomainParameters parameters = this.key.getParameters();
        final BigInteger n = parameters.getN();
        final ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        final BigInteger generateK = ECUtil.generateK(n, this.random);
        final ECPoint[] array = { basePointMultiplier.multiply(parameters.getG(), generateK), this.key.getQ().multiply(generateK).add(ecPair.getY()) };
        parameters.getCurve().normalizeAll(array);
        return new ECPair(array[0], array[1]);
    }
    
    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}
