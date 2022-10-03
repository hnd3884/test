package org.bouncycastle.crypto.signers;

import java.util.Random;
import org.bouncycastle.crypto.params.DSAParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.DSAKeyParameters;
import org.bouncycastle.crypto.DSA;

public class DSASigner implements DSA
{
    private final DSAKCalculator kCalculator;
    private DSAKeyParameters key;
    private SecureRandom random;
    
    public DSASigner() {
        this.kCalculator = new RandomDSAKCalculator();
    }
    
    public DSASigner(final DSAKCalculator kCalculator) {
        this.kCalculator = kCalculator;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        SecureRandom random = null;
        if (b) {
            if (cipherParameters instanceof ParametersWithRandom) {
                final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.key = (DSAPrivateKeyParameters)parametersWithRandom.getParameters();
                random = parametersWithRandom.getRandom();
            }
            else {
                this.key = (DSAPrivateKeyParameters)cipherParameters;
            }
        }
        else {
            this.key = (DSAPublicKeyParameters)cipherParameters;
        }
        this.random = this.initSecureRandom(b && !this.kCalculator.isDeterministic(), random);
    }
    
    public BigInteger[] generateSignature(final byte[] array) {
        final DSAParameters parameters = this.key.getParameters();
        final BigInteger q = parameters.getQ();
        final BigInteger calculateE = this.calculateE(q, array);
        final BigInteger x = ((DSAPrivateKeyParameters)this.key).getX();
        if (this.kCalculator.isDeterministic()) {
            this.kCalculator.init(q, x, array);
        }
        else {
            this.kCalculator.init(q, this.random);
        }
        final BigInteger nextK = this.kCalculator.nextK();
        final BigInteger mod = parameters.getG().modPow(nextK.add(this.getRandomizer(q, this.random)), parameters.getP()).mod(q);
        return new BigInteger[] { mod, nextK.modInverse(q).multiply(calculateE.add(x.multiply(mod))).mod(q) };
    }
    
    public boolean verifySignature(final byte[] array, final BigInteger bigInteger, final BigInteger bigInteger2) {
        final DSAParameters parameters = this.key.getParameters();
        final BigInteger q = parameters.getQ();
        final BigInteger calculateE = this.calculateE(q, array);
        final BigInteger value = BigInteger.valueOf(0L);
        if (value.compareTo(bigInteger) >= 0 || q.compareTo(bigInteger) <= 0) {
            return false;
        }
        if (value.compareTo(bigInteger2) >= 0 || q.compareTo(bigInteger2) <= 0) {
            return false;
        }
        final BigInteger modInverse = bigInteger2.modInverse(q);
        final BigInteger mod = calculateE.multiply(modInverse).mod(q);
        final BigInteger mod2 = bigInteger.multiply(modInverse).mod(q);
        final BigInteger p3 = parameters.getP();
        return parameters.getG().modPow(mod, p3).multiply(((DSAPublicKeyParameters)this.key).getY().modPow(mod2, p3)).mod(p3).mod(q).equals(bigInteger);
    }
    
    private BigInteger calculateE(final BigInteger bigInteger, final byte[] array) {
        if (bigInteger.bitLength() >= array.length * 8) {
            return new BigInteger(1, array);
        }
        final byte[] array2 = new byte[bigInteger.bitLength() / 8];
        System.arraycopy(array, 0, array2, 0, array2.length);
        return new BigInteger(1, array2);
    }
    
    protected SecureRandom initSecureRandom(final boolean b, final SecureRandom secureRandom) {
        return b ? ((secureRandom != null) ? secureRandom : new SecureRandom()) : null;
    }
    
    private BigInteger getRandomizer(final BigInteger bigInteger, final SecureRandom secureRandom) {
        return new BigInteger(7, (secureRandom != null) ? secureRandom : new SecureRandom()).add(BigInteger.valueOf(128L)).multiply(bigInteger);
    }
}
