package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.AsymmetricBlockCipher;

public class RSABlindingEngine implements AsymmetricBlockCipher
{
    private RSACoreEngine core;
    private RSAKeyParameters key;
    private BigInteger blindingFactor;
    private boolean forEncryption;
    
    public RSABlindingEngine() {
        this.core = new RSACoreEngine();
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        RSABlindingParameters rsaBlindingParameters;
        if (cipherParameters instanceof ParametersWithRandom) {
            rsaBlindingParameters = (RSABlindingParameters)((ParametersWithRandom)cipherParameters).getParameters();
        }
        else {
            rsaBlindingParameters = (RSABlindingParameters)cipherParameters;
        }
        this.core.init(forEncryption, rsaBlindingParameters.getPublicKey());
        this.forEncryption = forEncryption;
        this.key = rsaBlindingParameters.getPublicKey();
        this.blindingFactor = rsaBlindingParameters.getBlindingFactor();
    }
    
    public int getInputBlockSize() {
        return this.core.getInputBlockSize();
    }
    
    public int getOutputBlockSize() {
        return this.core.getOutputBlockSize();
    }
    
    public byte[] processBlock(final byte[] array, final int n, final int n2) {
        final BigInteger convertInput = this.core.convertInput(array, n, n2);
        BigInteger bigInteger;
        if (this.forEncryption) {
            bigInteger = this.blindMessage(convertInput);
        }
        else {
            bigInteger = this.unblindMessage(convertInput);
        }
        return this.core.convertOutput(bigInteger);
    }
    
    private BigInteger blindMessage(final BigInteger bigInteger) {
        return bigInteger.multiply(this.blindingFactor.modPow(this.key.getExponent(), this.key.getModulus())).mod(this.key.getModulus());
    }
    
    private BigInteger unblindMessage(final BigInteger bigInteger) {
        final BigInteger modulus = this.key.getModulus();
        return bigInteger.multiply(this.blindingFactor.modInverse(modulus)).mod(modulus);
    }
}
