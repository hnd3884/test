package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.DataLengthException;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;

class RSACoreEngine
{
    private RSAKeyParameters key;
    private boolean forEncryption;
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithRandom) {
            this.key = (RSAKeyParameters)((ParametersWithRandom)cipherParameters).getParameters();
        }
        else {
            this.key = (RSAKeyParameters)cipherParameters;
        }
        this.forEncryption = forEncryption;
    }
    
    public int getInputBlockSize() {
        final int bitLength = this.key.getModulus().bitLength();
        if (this.forEncryption) {
            return (bitLength + 7) / 8 - 1;
        }
        return (bitLength + 7) / 8;
    }
    
    public int getOutputBlockSize() {
        final int bitLength = this.key.getModulus().bitLength();
        if (this.forEncryption) {
            return (bitLength + 7) / 8;
        }
        return (bitLength + 7) / 8 - 1;
    }
    
    public BigInteger convertInput(final byte[] array, final int n, final int n2) {
        if (n2 > this.getInputBlockSize() + 1) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        if (n2 == this.getInputBlockSize() + 1 && !this.forEncryption) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        byte[] array2;
        if (n != 0 || n2 != array.length) {
            array2 = new byte[n2];
            System.arraycopy(array, n, array2, 0, n2);
        }
        else {
            array2 = array;
        }
        final BigInteger bigInteger = new BigInteger(1, array2);
        if (bigInteger.compareTo(this.key.getModulus()) >= 0) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        return bigInteger;
    }
    
    public byte[] convertOutput(final BigInteger bigInteger) {
        final byte[] byteArray = bigInteger.toByteArray();
        if (this.forEncryption) {
            if (byteArray[0] == 0 && byteArray.length > this.getOutputBlockSize()) {
                final byte[] array = new byte[byteArray.length - 1];
                System.arraycopy(byteArray, 1, array, 0, array.length);
                return array;
            }
            if (byteArray.length < this.getOutputBlockSize()) {
                final byte[] array2 = new byte[this.getOutputBlockSize()];
                System.arraycopy(byteArray, 0, array2, array2.length - byteArray.length, byteArray.length);
                return array2;
            }
        }
        else if (byteArray[0] == 0) {
            final byte[] array3 = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, array3, 0, array3.length);
            return array3;
        }
        return byteArray;
    }
    
    public BigInteger processBlock(final BigInteger bigInteger) {
        if (this.key instanceof RSAPrivateCrtKeyParameters) {
            final RSAPrivateCrtKeyParameters rsaPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters)this.key;
            final BigInteger p = rsaPrivateCrtKeyParameters.getP();
            final BigInteger q = rsaPrivateCrtKeyParameters.getQ();
            final BigInteger dp = rsaPrivateCrtKeyParameters.getDP();
            final BigInteger dq = rsaPrivateCrtKeyParameters.getDQ();
            final BigInteger qInv = rsaPrivateCrtKeyParameters.getQInv();
            final BigInteger modPow = bigInteger.remainder(p).modPow(dp, p);
            final BigInteger modPow2 = bigInteger.remainder(q).modPow(dq, q);
            return modPow.subtract(modPow2).multiply(qInv).mod(p).multiply(q).add(modPow2);
        }
        return bigInteger.modPow(this.key.getExponent(), this.key.getModulus());
    }
}
