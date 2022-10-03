package org.bouncycastle.crypto.ec;

import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.CipherParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

public class ECFixedTransform implements ECPairFactorTransform
{
    private ECPublicKeyParameters key;
    private BigInteger k;
    
    public ECFixedTransform(final BigInteger k) {
        this.k = k;
    }
    
    public void init(final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("ECPublicKeyParameters are required for fixed transform.");
        }
        this.key = (ECPublicKeyParameters)cipherParameters;
    }
    
    public ECPair transform(final ECPair ecPair) {
        if (this.key == null) {
            throw new IllegalStateException("ECFixedTransform not initialised");
        }
        final ECDomainParameters parameters = this.key.getParameters();
        final BigInteger n = parameters.getN();
        final ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        final BigInteger mod = this.k.mod(n);
        final ECPoint[] array = { basePointMultiplier.multiply(parameters.getG(), mod).add(ecPair.getX()), this.key.getQ().multiply(mod).add(ecPair.getY()) };
        parameters.getCurve().normalizeAll(array);
        return new ECPair(array[0], array[1]);
    }
    
    public BigInteger getTransformValue() {
        return this.k;
    }
    
    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}
