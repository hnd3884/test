package org.bouncycastle.crypto.signers;

import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.crypto.params.ECDomainParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.math.ec.ECConstants;

public class ECDSASigner implements ECConstants, DSA
{
    private final DSAKCalculator kCalculator;
    private ECKeyParameters key;
    private SecureRandom random;
    
    public ECDSASigner() {
        this.kCalculator = new RandomDSAKCalculator();
    }
    
    public ECDSASigner(final DSAKCalculator kCalculator) {
        this.kCalculator = kCalculator;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        SecureRandom random = null;
        if (b) {
            if (cipherParameters instanceof ParametersWithRandom) {
                final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.key = (ECPrivateKeyParameters)parametersWithRandom.getParameters();
                random = parametersWithRandom.getRandom();
            }
            else {
                this.key = (ECPrivateKeyParameters)cipherParameters;
            }
        }
        else {
            this.key = (ECPublicKeyParameters)cipherParameters;
        }
        this.random = this.initSecureRandom(b && !this.kCalculator.isDeterministic(), random);
    }
    
    public BigInteger[] generateSignature(final byte[] array) {
        final ECDomainParameters parameters = this.key.getParameters();
        final BigInteger n = parameters.getN();
        final BigInteger calculateE = this.calculateE(n, array);
        final BigInteger d = ((ECPrivateKeyParameters)this.key).getD();
        if (this.kCalculator.isDeterministic()) {
            this.kCalculator.init(n, d, array);
        }
        else {
            this.kCalculator.init(n, this.random);
        }
        final ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        BigInteger mod;
        BigInteger mod2;
        while (true) {
            final BigInteger nextK = this.kCalculator.nextK();
            mod = basePointMultiplier.multiply(parameters.getG(), nextK).normalize().getAffineXCoord().toBigInteger().mod(n);
            if (!mod.equals(ECDSASigner.ZERO)) {
                mod2 = nextK.modInverse(n).multiply(calculateE.add(d.multiply(mod))).mod(n);
                if (!mod2.equals(ECDSASigner.ZERO)) {
                    break;
                }
                continue;
            }
        }
        return new BigInteger[] { mod, mod2 };
    }
    
    public boolean verifySignature(final byte[] array, BigInteger add, final BigInteger bigInteger) {
        final ECDomainParameters parameters = this.key.getParameters();
        final BigInteger n = parameters.getN();
        final BigInteger calculateE = this.calculateE(n, array);
        if (add.compareTo(ECDSASigner.ONE) < 0 || add.compareTo(n) >= 0) {
            return false;
        }
        if (bigInteger.compareTo(ECDSASigner.ONE) < 0 || bigInteger.compareTo(n) >= 0) {
            return false;
        }
        final BigInteger modInverse = bigInteger.modInverse(n);
        final ECPoint sumOfTwoMultiplies = ECAlgorithms.sumOfTwoMultiplies(parameters.getG(), calculateE.multiply(modInverse).mod(n), ((ECPublicKeyParameters)this.key).getQ(), add.multiply(modInverse).mod(n));
        if (sumOfTwoMultiplies.isInfinity()) {
            return false;
        }
        final ECCurve curve = sumOfTwoMultiplies.getCurve();
        if (curve != null) {
            final BigInteger cofactor = curve.getCofactor();
            if (cofactor != null && cofactor.compareTo(ECDSASigner.EIGHT) <= 0) {
                final ECFieldElement denominator = this.getDenominator(curve.getCoordinateSystem(), sumOfTwoMultiplies);
                if (denominator != null && !denominator.isZero()) {
                    final ECFieldElement xCoord = sumOfTwoMultiplies.getXCoord();
                    while (curve.isValidFieldElement(add)) {
                        if (curve.fromBigInteger(add).multiply(denominator).equals(xCoord)) {
                            return true;
                        }
                        add = add.add(n);
                    }
                    return false;
                }
            }
        }
        return sumOfTwoMultiplies.normalize().getAffineXCoord().toBigInteger().mod(n).equals(add);
    }
    
    protected BigInteger calculateE(final BigInteger bigInteger, final byte[] array) {
        final int bitLength = bigInteger.bitLength();
        final int n = array.length * 8;
        BigInteger shiftRight = new BigInteger(1, array);
        if (bitLength < n) {
            shiftRight = shiftRight.shiftRight(n - bitLength);
        }
        return shiftRight;
    }
    
    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
    
    protected ECFieldElement getDenominator(final int n, final ECPoint ecPoint) {
        switch (n) {
            case 1:
            case 6:
            case 7: {
                return ecPoint.getZCoord(0);
            }
            case 2:
            case 3:
            case 4: {
                return ecPoint.getZCoord(0).square();
            }
            default: {
                return null;
            }
        }
    }
    
    protected SecureRandom initSecureRandom(final boolean b, final SecureRandom secureRandom) {
        return b ? ((secureRandom != null) ? secureRandom : new SecureRandom()) : null;
    }
}
