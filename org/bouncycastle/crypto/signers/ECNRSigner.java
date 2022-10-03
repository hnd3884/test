package org.bouncycastle.crypto.signers;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.crypto.DataLengthException;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.DSA;

public class ECNRSigner implements DSA
{
    private boolean forSigning;
    private ECKeyParameters key;
    private SecureRandom random;
    
    public void init(final boolean forSigning, final CipherParameters cipherParameters) {
        this.forSigning = forSigning;
        if (forSigning) {
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
        if (!this.forSigning) {
            throw new IllegalStateException("not initialised for signing");
        }
        final BigInteger n = this.key.getParameters().getN();
        final int bitLength = n.bitLength();
        final BigInteger bigInteger = new BigInteger(1, array);
        final int bitLength2 = bigInteger.bitLength();
        final ECPrivateKeyParameters ecPrivateKeyParameters = (ECPrivateKeyParameters)this.key;
        if (bitLength2 > bitLength) {
            throw new DataLengthException("input too large for ECNR key.");
        }
        BigInteger mod;
        AsymmetricCipherKeyPair generateKeyPair;
        do {
            final ECKeyPairGenerator ecKeyPairGenerator = new ECKeyPairGenerator();
            ecKeyPairGenerator.init(new ECKeyGenerationParameters(ecPrivateKeyParameters.getParameters(), this.random));
            generateKeyPair = ecKeyPairGenerator.generateKeyPair();
            mod = ((ECPublicKeyParameters)generateKeyPair.getPublic()).getQ().getAffineXCoord().toBigInteger().add(bigInteger).mod(n);
        } while (mod.equals(ECConstants.ZERO));
        return new BigInteger[] { mod, ((ECPrivateKeyParameters)generateKeyPair.getPrivate()).getD().subtract(mod.multiply(ecPrivateKeyParameters.getD())).mod(n) };
    }
    
    public boolean verifySignature(final byte[] array, final BigInteger bigInteger, final BigInteger bigInteger2) {
        if (this.forSigning) {
            throw new IllegalStateException("not initialised for verifying");
        }
        final ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters)this.key;
        final BigInteger n = ecPublicKeyParameters.getParameters().getN();
        final int bitLength = n.bitLength();
        final BigInteger bigInteger3 = new BigInteger(1, array);
        if (bigInteger3.bitLength() > bitLength) {
            throw new DataLengthException("input too large for ECNR key.");
        }
        if (bigInteger.compareTo(ECConstants.ONE) < 0 || bigInteger.compareTo(n) >= 0) {
            return false;
        }
        if (bigInteger2.compareTo(ECConstants.ZERO) < 0 || bigInteger2.compareTo(n) >= 0) {
            return false;
        }
        final ECPoint normalize = ECAlgorithms.sumOfTwoMultiplies(ecPublicKeyParameters.getParameters().getG(), bigInteger2, ecPublicKeyParameters.getQ(), bigInteger).normalize();
        return !normalize.isInfinity() && bigInteger.subtract(normalize.getAffineXCoord().toBigInteger()).mod(n).equals(bigInteger3);
    }
}
