package org.bouncycastle.crypto.signers;

import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECConstants;
import java.util.Random;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.DSA;

public class ECGOST3410Signer implements DSA
{
    ECKeyParameters key;
    SecureRandom random;
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (b) {
            if (cipherParameters instanceof ParametersWithRandom) {
                final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.random = parametersWithRandom.getRandom();
                this.key = (ECPrivateKeyParameters)parametersWithRandom.getParameters();
            }
            else {
                this.random = new SecureRandom();
                this.key = (ECPrivateKeyParameters)cipherParameters;
            }
        }
        else {
            this.key = (ECPublicKeyParameters)cipherParameters;
        }
    }
    
    public BigInteger[] generateSignature(final byte[] array) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = array[array2.length - 1 - i];
        }
        final BigInteger bigInteger = new BigInteger(1, array2);
        final ECDomainParameters parameters = this.key.getParameters();
        final BigInteger n = parameters.getN();
        final BigInteger d = ((ECPrivateKeyParameters)this.key).getD();
        final ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        BigInteger mod;
        BigInteger mod2;
        while (true) {
            final BigInteger bigInteger2 = new BigInteger(n.bitLength(), this.random);
            if (!bigInteger2.equals(ECConstants.ZERO)) {
                mod = basePointMultiplier.multiply(parameters.getG(), bigInteger2).normalize().getAffineXCoord().toBigInteger().mod(n);
                if (mod.equals(ECConstants.ZERO)) {
                    continue;
                }
                mod2 = bigInteger2.multiply(bigInteger).add(d.multiply(mod)).mod(n);
                if (!mod2.equals(ECConstants.ZERO)) {
                    break;
                }
                continue;
            }
        }
        return new BigInteger[] { mod, mod2 };
    }
    
    public boolean verifySignature(final byte[] array, final BigInteger bigInteger, final BigInteger bigInteger2) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = array[array2.length - 1 - i];
        }
        final BigInteger bigInteger3 = new BigInteger(1, array2);
        final BigInteger n = this.key.getParameters().getN();
        if (bigInteger.compareTo(ECConstants.ONE) < 0 || bigInteger.compareTo(n) >= 0) {
            return false;
        }
        if (bigInteger2.compareTo(ECConstants.ONE) < 0 || bigInteger2.compareTo(n) >= 0) {
            return false;
        }
        final BigInteger modInverse = bigInteger3.modInverse(n);
        final ECPoint normalize = ECAlgorithms.sumOfTwoMultiplies(this.key.getParameters().getG(), bigInteger2.multiply(modInverse).mod(n), ((ECPublicKeyParameters)this.key).getQ(), n.subtract(bigInteger).multiply(modInverse).mod(n)).normalize();
        return !normalize.isInfinity() && normalize.getAffineXCoord().toBigInteger().mod(n).equals(bigInteger);
    }
    
    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}
