package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.params.GOST3410Parameters;
import java.util.Random;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.GOST3410KeyParameters;
import org.bouncycastle.crypto.DSA;

public class GOST3410Signer implements DSA
{
    GOST3410KeyParameters key;
    SecureRandom random;
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (b) {
            if (cipherParameters instanceof ParametersWithRandom) {
                final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.random = parametersWithRandom.getRandom();
                this.key = (GOST3410PrivateKeyParameters)parametersWithRandom.getParameters();
            }
            else {
                this.random = new SecureRandom();
                this.key = (GOST3410PrivateKeyParameters)cipherParameters;
            }
        }
        else {
            this.key = (GOST3410PublicKeyParameters)cipherParameters;
        }
    }
    
    public BigInteger[] generateSignature(final byte[] array) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = array[array2.length - 1 - i];
        }
        final BigInteger bigInteger = new BigInteger(1, array2);
        final GOST3410Parameters parameters = this.key.getParameters();
        BigInteger bigInteger2;
        do {
            bigInteger2 = new BigInteger(parameters.getQ().bitLength(), this.random);
        } while (bigInteger2.compareTo(parameters.getQ()) >= 0);
        final BigInteger mod = parameters.getA().modPow(bigInteger2, parameters.getP()).mod(parameters.getQ());
        return new BigInteger[] { mod, bigInteger2.multiply(bigInteger).add(((GOST3410PrivateKeyParameters)this.key).getX().multiply(mod)).mod(parameters.getQ()) };
    }
    
    public boolean verifySignature(final byte[] array, final BigInteger bigInteger, final BigInteger bigInteger2) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = array[array2.length - 1 - i];
        }
        final BigInteger bigInteger3 = new BigInteger(1, array2);
        final GOST3410Parameters parameters = this.key.getParameters();
        final BigInteger value = BigInteger.valueOf(0L);
        if (value.compareTo(bigInteger) >= 0 || parameters.getQ().compareTo(bigInteger) <= 0) {
            return false;
        }
        if (value.compareTo(bigInteger2) >= 0 || parameters.getQ().compareTo(bigInteger2) <= 0) {
            return false;
        }
        final BigInteger modPow = bigInteger3.modPow(parameters.getQ().subtract(new BigInteger("2")), parameters.getQ());
        return parameters.getA().modPow(bigInteger2.multiply(modPow).mod(parameters.getQ()), parameters.getP()).multiply(((GOST3410PublicKeyParameters)this.key).getY().modPow(parameters.getQ().subtract(bigInteger).multiply(modPow).mod(parameters.getQ()), parameters.getP())).mod(parameters.getP()).mod(parameters.getQ()).equals(bigInteger);
    }
}
