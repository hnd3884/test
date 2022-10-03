package org.bouncycastle.crypto.generators;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.crypto.params.DSAValidationParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import java.math.BigInteger;

public class DSAParametersGenerator
{
    private static final BigInteger ZERO;
    private static final BigInteger ONE;
    private static final BigInteger TWO;
    private Digest digest;
    private int L;
    private int N;
    private int certainty;
    private int iterations;
    private SecureRandom random;
    private boolean use186_3;
    private int usageIndex;
    
    public DSAParametersGenerator() {
        this(DigestFactory.createSHA1());
    }
    
    public DSAParametersGenerator(final Digest digest) {
        this.digest = digest;
    }
    
    public void init(final int l, final int certainty, final SecureRandom random) {
        this.L = l;
        this.N = getDefaultN(l);
        this.certainty = certainty;
        this.iterations = Math.max(getMinimumIterations(this.L), (certainty + 1) / 2);
        this.random = random;
        this.use186_3 = false;
        this.usageIndex = -1;
    }
    
    public void init(final DSAParameterGenerationParameters dsaParameterGenerationParameters) {
        final int l = dsaParameterGenerationParameters.getL();
        final int n = dsaParameterGenerationParameters.getN();
        if (l < 1024 || l > 3072 || l % 1024 != 0) {
            throw new IllegalArgumentException("L values must be between 1024 and 3072 and a multiple of 1024");
        }
        if (l == 1024 && n != 160) {
            throw new IllegalArgumentException("N must be 160 for L = 1024");
        }
        if (l == 2048 && n != 224 && n != 256) {
            throw new IllegalArgumentException("N must be 224 or 256 for L = 2048");
        }
        if (l == 3072 && n != 256) {
            throw new IllegalArgumentException("N must be 256 for L = 3072");
        }
        if (this.digest.getDigestSize() * 8 < n) {
            throw new IllegalStateException("Digest output size too small for value of N");
        }
        this.L = l;
        this.N = n;
        this.certainty = dsaParameterGenerationParameters.getCertainty();
        this.iterations = Math.max(getMinimumIterations(l), (this.certainty + 1) / 2);
        this.random = dsaParameterGenerationParameters.getRandom();
        this.use186_3 = true;
        this.usageIndex = dsaParameterGenerationParameters.getUsageIndex();
    }
    
    public DSAParameters generateParameters() {
        return this.use186_3 ? this.generateParameters_FIPS186_3() : this.generateParameters_FIPS186_2();
    }
    
    private DSAParameters generateParameters_FIPS186_2() {
        final byte[] array = new byte[20];
        final byte[] array2 = new byte[20];
        final byte[] array3 = new byte[20];
        final byte[] array4 = new byte[20];
        final int n = (this.L - 1) / 160;
        final byte[] array5 = new byte[this.L / 8];
        if (!(this.digest instanceof SHA1Digest)) {
            throw new IllegalStateException("can only use SHA-1 for generating FIPS 186-2 parameters");
        }
        BigInteger bigInteger = null;
        int j = 0;
        BigInteger subtract = null;
    Block_7:
        while (true) {
            this.random.nextBytes(array);
            hash(this.digest, array, array2, 0);
            System.arraycopy(array, 0, array3, 0, array.length);
            inc(array3);
            hash(this.digest, array3, array3, 0);
            for (int i = 0; i != array4.length; ++i) {
                array4[i] = (byte)(array2[i] ^ array3[i]);
            }
            final byte[] array6 = array4;
            final int n2 = 0;
            array6[n2] |= 0xFFFFFF80;
            final byte[] array7 = array4;
            final int n3 = 19;
            array7[n3] |= 0x1;
            bigInteger = new BigInteger(1, array4);
            if (!this.isProbablePrime(bigInteger)) {
                continue;
            }
            final byte[] clone = Arrays.clone(array);
            inc(clone);
            for (j = 0; j < 4096; ++j) {
                for (int k = 1; k <= n; ++k) {
                    inc(clone);
                    hash(this.digest, clone, array5, array5.length - k * array2.length);
                }
                final int n4 = array5.length - n * array2.length;
                inc(clone);
                hash(this.digest, clone, array2, 0);
                System.arraycopy(array2, array2.length - n4, array5, 0, n4);
                final byte[] array8 = array5;
                final int n5 = 0;
                array8[n5] |= 0xFFFFFF80;
                final BigInteger bigInteger2 = new BigInteger(1, array5);
                subtract = bigInteger2.subtract(bigInteger2.mod(bigInteger.shiftLeft(1)).subtract(DSAParametersGenerator.ONE));
                if (subtract.bitLength() == this.L) {
                    if (this.isProbablePrime(subtract)) {
                        break Block_7;
                    }
                }
            }
        }
        return new DSAParameters(subtract, bigInteger, calculateGenerator_FIPS186_2(subtract, bigInteger, this.random), new DSAValidationParameters(array, j));
    }
    
    private static BigInteger calculateGenerator_FIPS186_2(final BigInteger bigInteger, final BigInteger bigInteger2, final SecureRandom secureRandom) {
        final BigInteger divide = bigInteger.subtract(DSAParametersGenerator.ONE).divide(bigInteger2);
        final BigInteger subtract = bigInteger.subtract(DSAParametersGenerator.TWO);
        BigInteger modPow;
        do {
            modPow = BigIntegers.createRandomInRange(DSAParametersGenerator.TWO, subtract, secureRandom).modPow(divide, bigInteger);
        } while (modPow.bitLength() <= 1);
        return modPow;
    }
    
    private DSAParameters generateParameters_FIPS186_3() {
        final Digest digest = this.digest;
        final int n = digest.getDigestSize() * 8;
        final byte[] array = new byte[this.N / 8];
        final int n2 = (this.L - 1) / n;
        final int n3 = (this.L - 1) % n;
        final byte[] array2 = new byte[this.L / 8];
        final byte[] array3 = new byte[digest.getDigestSize()];
        BigInteger setBit = null;
        int i = 0;
        BigInteger subtract = null;
    Block_5:
        while (true) {
            this.random.nextBytes(array);
            hash(digest, array, array3, 0);
            setBit = new BigInteger(1, array3).mod(DSAParametersGenerator.ONE.shiftLeft(this.N - 1)).setBit(0).setBit(this.N - 1);
            if (!this.isProbablePrime(setBit)) {
                continue;
            }
            final byte[] clone = Arrays.clone(array);
            for (final int n4 = 4 * this.L, i = 0; i < n4; ++i) {
                for (int j = 1; j <= n2; ++j) {
                    inc(clone);
                    hash(digest, clone, array2, array2.length - j * array3.length);
                }
                final int n5 = array2.length - n2 * array3.length;
                inc(clone);
                hash(digest, clone, array3, 0);
                System.arraycopy(array3, array3.length - n5, array2, 0, n5);
                final byte[] array4 = array2;
                final int n6 = 0;
                array4[n6] |= 0xFFFFFF80;
                final BigInteger bigInteger = new BigInteger(1, array2);
                subtract = bigInteger.subtract(bigInteger.mod(setBit.shiftLeft(1)).subtract(DSAParametersGenerator.ONE));
                if (subtract.bitLength() == this.L) {
                    if (this.isProbablePrime(subtract)) {
                        break Block_5;
                    }
                }
            }
        }
        if (this.usageIndex >= 0) {
            final BigInteger calculateGenerator_FIPS186_3_Verifiable = calculateGenerator_FIPS186_3_Verifiable(digest, subtract, setBit, array, this.usageIndex);
            if (calculateGenerator_FIPS186_3_Verifiable != null) {
                return new DSAParameters(subtract, setBit, calculateGenerator_FIPS186_3_Verifiable, new DSAValidationParameters(array, i, this.usageIndex));
            }
        }
        return new DSAParameters(subtract, setBit, calculateGenerator_FIPS186_3_Unverifiable(subtract, setBit, this.random), new DSAValidationParameters(array, i));
    }
    
    private boolean isProbablePrime(final BigInteger bigInteger) {
        return bigInteger.isProbablePrime(this.certainty);
    }
    
    private static BigInteger calculateGenerator_FIPS186_3_Unverifiable(final BigInteger bigInteger, final BigInteger bigInteger2, final SecureRandom secureRandom) {
        return calculateGenerator_FIPS186_2(bigInteger, bigInteger2, secureRandom);
    }
    
    private static BigInteger calculateGenerator_FIPS186_3_Verifiable(final Digest digest, final BigInteger bigInteger, final BigInteger bigInteger2, final byte[] array, final int n) {
        final BigInteger divide = bigInteger.subtract(DSAParametersGenerator.ONE).divide(bigInteger2);
        final byte[] decode = Hex.decode("6767656E");
        final byte[] array2 = new byte[array.length + decode.length + 1 + 2];
        System.arraycopy(array, 0, array2, 0, array.length);
        System.arraycopy(decode, 0, array2, array.length, decode.length);
        array2[array2.length - 3] = (byte)n;
        final byte[] array3 = new byte[digest.getDigestSize()];
        for (int i = 1; i < 65536; ++i) {
            inc(array2);
            hash(digest, array2, array3, 0);
            final BigInteger modPow = new BigInteger(1, array3).modPow(divide, bigInteger);
            if (modPow.compareTo(DSAParametersGenerator.TWO) >= 0) {
                return modPow;
            }
        }
        return null;
    }
    
    private static void hash(final Digest digest, final byte[] array, final byte[] array2, final int n) {
        digest.update(array, 0, array.length);
        digest.doFinal(array2, n);
    }
    
    private static int getDefaultN(final int n) {
        return (n > 1024) ? 256 : 160;
    }
    
    private static int getMinimumIterations(final int n) {
        return (n <= 1024) ? 40 : (48 + 8 * ((n - 1) / 1024));
    }
    
    private static void inc(final byte[] array) {
        for (int n = array.length - 1; n >= 0 && (array[n] = (byte)(array[n] + 1 & 0xFF)) == 0; --n) {}
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
        ONE = BigInteger.valueOf(1L);
        TWO = BigInteger.valueOf(2L);
    }
}
