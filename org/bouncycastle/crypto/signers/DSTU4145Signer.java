package org.bouncycastle.crypto.signers;

import org.bouncycastle.util.Arrays;
import java.util.Random;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ECKeyParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.DSA;

public class DSTU4145Signer implements DSA
{
    private static final BigInteger ONE;
    private ECKeyParameters key;
    private SecureRandom random;
    
    public void init(final boolean b, CipherParameters parameters) {
        if (b) {
            if (parameters instanceof ParametersWithRandom) {
                final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)parameters;
                this.random = parametersWithRandom.getRandom();
                parameters = parametersWithRandom.getParameters();
            }
            else {
                this.random = new SecureRandom();
            }
            this.key = (ECPrivateKeyParameters)parameters;
        }
        else {
            this.key = (ECPublicKeyParameters)parameters;
        }
    }
    
    public BigInteger[] generateSignature(final byte[] array) {
        final ECDomainParameters parameters = this.key.getParameters();
        final ECCurve curve = parameters.getCurve();
        ECFieldElement ecFieldElement = hash2FieldElement(curve, array);
        if (ecFieldElement.isZero()) {
            ecFieldElement = curve.fromBigInteger(DSTU4145Signer.ONE);
        }
        final BigInteger n = parameters.getN();
        final BigInteger d = ((ECPrivateKeyParameters)this.key).getD();
        final ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        BigInteger fieldElement2Integer;
        BigInteger mod;
        while (true) {
            final BigInteger generateRandomInteger = generateRandomInteger(n, this.random);
            final ECFieldElement affineXCoord = basePointMultiplier.multiply(parameters.getG(), generateRandomInteger).normalize().getAffineXCoord();
            if (!affineXCoord.isZero()) {
                fieldElement2Integer = fieldElement2Integer(n, ecFieldElement.multiply(affineXCoord));
                if (fieldElement2Integer.signum() == 0) {
                    continue;
                }
                mod = fieldElement2Integer.multiply(d).add(generateRandomInteger).mod(n);
                if (mod.signum() != 0) {
                    break;
                }
                continue;
            }
        }
        return new BigInteger[] { fieldElement2Integer, mod };
    }
    
    public boolean verifySignature(final byte[] array, final BigInteger bigInteger, final BigInteger bigInteger2) {
        if (bigInteger.signum() <= 0 || bigInteger2.signum() <= 0) {
            return false;
        }
        final ECDomainParameters parameters = this.key.getParameters();
        final BigInteger n = parameters.getN();
        if (bigInteger.compareTo(n) >= 0 || bigInteger2.compareTo(n) >= 0) {
            return false;
        }
        final ECCurve curve = parameters.getCurve();
        ECFieldElement ecFieldElement = hash2FieldElement(curve, array);
        if (ecFieldElement.isZero()) {
            ecFieldElement = curve.fromBigInteger(DSTU4145Signer.ONE);
        }
        final ECPoint normalize = ECAlgorithms.sumOfTwoMultiplies(parameters.getG(), bigInteger2, ((ECPublicKeyParameters)this.key).getQ(), bigInteger).normalize();
        return !normalize.isInfinity() && fieldElement2Integer(n, ecFieldElement.multiply(normalize.getAffineXCoord())).compareTo(bigInteger) == 0;
    }
    
    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
    
    private static BigInteger generateRandomInteger(final BigInteger bigInteger, final SecureRandom secureRandom) {
        return new BigInteger(bigInteger.bitLength() - 1, secureRandom);
    }
    
    private static ECFieldElement hash2FieldElement(final ECCurve ecCurve, final byte[] array) {
        return ecCurve.fromBigInteger(truncate(new BigInteger(1, Arrays.reverse(array)), ecCurve.getFieldSize()));
    }
    
    private static BigInteger fieldElement2Integer(final BigInteger bigInteger, final ECFieldElement ecFieldElement) {
        return truncate(ecFieldElement.toBigInteger(), bigInteger.bitLength() - 1);
    }
    
    private static BigInteger truncate(BigInteger mod, final int n) {
        if (mod.bitLength() > n) {
            mod = mod.mod(DSTU4145Signer.ONE.shiftLeft(n));
        }
        return mod;
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
}
