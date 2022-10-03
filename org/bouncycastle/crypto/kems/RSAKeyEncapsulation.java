package org.bouncycastle.crypto.kems;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.DerivationFunction;
import java.math.BigInteger;
import org.bouncycastle.crypto.KeyEncapsulation;

public class RSAKeyEncapsulation implements KeyEncapsulation
{
    private static final BigInteger ZERO;
    private static final BigInteger ONE;
    private DerivationFunction kdf;
    private SecureRandom rnd;
    private RSAKeyParameters key;
    
    public RSAKeyEncapsulation(final DerivationFunction kdf, final SecureRandom rnd) {
        this.kdf = kdf;
        this.rnd = rnd;
    }
    
    public void init(final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof RSAKeyParameters)) {
            throw new IllegalArgumentException("RSA key required");
        }
        this.key = (RSAKeyParameters)cipherParameters;
    }
    
    public CipherParameters encrypt(final byte[] array, final int n, final int n2) throws IllegalArgumentException {
        if (this.key.isPrivate()) {
            throw new IllegalArgumentException("Public key required for encryption");
        }
        final BigInteger modulus = this.key.getModulus();
        final BigInteger exponent = this.key.getExponent();
        final BigInteger randomInRange = BigIntegers.createRandomInRange(RSAKeyEncapsulation.ZERO, modulus.subtract(RSAKeyEncapsulation.ONE), this.rnd);
        final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray((modulus.bitLength() + 7) / 8, randomInRange.modPow(exponent, modulus));
        System.arraycopy(unsignedByteArray, 0, array, n, unsignedByteArray.length);
        return this.generateKey(modulus, randomInRange, n2);
    }
    
    public CipherParameters encrypt(final byte[] array, final int n) {
        return this.encrypt(array, 0, n);
    }
    
    public CipherParameters decrypt(final byte[] array, final int n, final int n2, final int n3) throws IllegalArgumentException {
        if (!this.key.isPrivate()) {
            throw new IllegalArgumentException("Private key required for decryption");
        }
        final BigInteger modulus = this.key.getModulus();
        final BigInteger exponent = this.key.getExponent();
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, array2.length);
        return this.generateKey(modulus, new BigInteger(1, array2).modPow(exponent, modulus), n3);
    }
    
    public CipherParameters decrypt(final byte[] array, final int n) {
        return this.decrypt(array, 0, array.length, n);
    }
    
    protected KeyParameter generateKey(final BigInteger bigInteger, final BigInteger bigInteger2, final int n) {
        this.kdf.init(new KDFParameters(BigIntegers.asUnsignedByteArray((bigInteger.bitLength() + 7) / 8, bigInteger2), null));
        final byte[] array = new byte[n];
        this.kdf.generateBytes(array, 0, array.length);
        return new KeyParameter(array);
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
        ONE = BigInteger.valueOf(1L);
    }
}
