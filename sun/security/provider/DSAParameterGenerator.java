package sun.security.provider;

import java.security.ProviderException;
import java.security.MessageDigest;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.DSAParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.DSAGenParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.util.SecurityProviderConstants;
import java.security.InvalidParameterException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.AlgorithmParameterGeneratorSpi;

public class DSAParameterGenerator extends AlgorithmParameterGeneratorSpi
{
    private int valueL;
    private int valueN;
    private int seedLen;
    private SecureRandom random;
    private static final BigInteger TWO;
    
    public DSAParameterGenerator() {
        this.valueL = -1;
        this.valueN = -1;
        this.seedLen = -1;
    }
    
    @Override
    protected void engineInit(final int valueL, final SecureRandom random) {
        if (valueL != 2048 && valueL != 3072 && (valueL < 512 || valueL > 1024 || valueL % 64 != 0)) {
            throw new InvalidParameterException("Unexpected strength (size of prime): " + valueL + ". Prime size should be 512-1024, 2048, or 3072");
        }
        this.valueL = valueL;
        this.valueN = SecurityProviderConstants.getDefDSASubprimeSize(valueL);
        this.seedLen = this.valueN;
        this.random = random;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof DSAGenParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Invalid parameter");
        }
        final DSAGenParameterSpec dsaGenParameterSpec = (DSAGenParameterSpec)algorithmParameterSpec;
        this.valueL = dsaGenParameterSpec.getPrimePLength();
        this.valueN = dsaGenParameterSpec.getSubprimeQLength();
        this.seedLen = dsaGenParameterSpec.getSeedLength();
        this.random = random;
    }
    
    @Override
    protected AlgorithmParameters engineGenerateParameters() {
        AlgorithmParameters instance;
        try {
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            if (this.valueL == -1) {
                this.engineInit(SecurityProviderConstants.DEF_DSA_KEY_SIZE, this.random);
            }
            final BigInteger[] generatePandQ = generatePandQ(this.random, this.valueL, this.valueN, this.seedLen);
            final BigInteger bigInteger = generatePandQ[0];
            final BigInteger bigInteger2 = generatePandQ[1];
            final DSAParameterSpec dsaParameterSpec = new DSAParameterSpec(bigInteger, bigInteger2, generateG(bigInteger, bigInteger2));
            instance = AlgorithmParameters.getInstance("DSA", "SUN");
            instance.init(dsaParameterSpec);
        }
        catch (final InvalidParameterSpecException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new RuntimeException(ex2.getMessage());
        }
        catch (final NoSuchProviderException ex3) {
            throw new RuntimeException(ex3.getMessage());
        }
        return instance;
    }
    
    private static BigInteger[] generatePandQ(final SecureRandom secureRandom, final int n, final int n2, final int n3) {
        String s = null;
        if (n2 == 160) {
            s = "SHA";
        }
        else if (n2 == 224) {
            s = "SHA-224";
        }
        else if (n2 == 256) {
            s = "SHA-256";
        }
        MessageDigest instance = null;
        try {
            instance = MessageDigest.getInstance(s);
        }
        catch (final NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        final int n4 = instance.getDigestLength() * 8;
        final int n5 = (n - 1) / n4;
        final int n6 = (n - 1) % n4;
        final byte[] array = new byte[n3 / 8];
        final BigInteger pow = DSAParameterGenerator.TWO.pow(n3);
        int n7 = -1;
        if (n <= 1024) {
            n7 = 80;
        }
        else if (n == 2048) {
            n7 = 112;
        }
        else if (n == 3072) {
            n7 = 128;
        }
        if (n7 < 0) {
            throw new ProviderException("Invalid valueL: " + n);
        }
        BigInteger bigInteger = null;
        BigInteger subtract = null;
        int i = 0;
        BigInteger subtract2 = null;
    Block_14:
        while (true) {
            secureRandom.nextBytes(array);
            bigInteger = new BigInteger(1, array);
            final BigInteger mod = new BigInteger(1, instance.digest(array)).mod(DSAParameterGenerator.TWO.pow(n2 - 1));
            subtract = DSAParameterGenerator.TWO.pow(n2 - 1).add(mod).add(BigInteger.ONE).subtract(mod.mod(DSAParameterGenerator.TWO));
            if (subtract.isProbablePrime(n7)) {
                BigInteger bigInteger2 = BigInteger.ONE;
                for (i = 0; i < 4 * n; ++i) {
                    final BigInteger[] array2 = new BigInteger[n5 + 1];
                    for (int j = 0; j <= n5; ++j) {
                        array2[j] = new BigInteger(1, instance.digest(toByteArray(bigInteger.add(bigInteger2).add(BigInteger.valueOf(j)).mod(pow))));
                    }
                    BigInteger add = array2[0];
                    for (int k = 1; k < n5; ++k) {
                        add = add.add(array2[k].multiply(DSAParameterGenerator.TWO.pow(k * n4)));
                    }
                    final BigInteger add2 = add.add(array2[n5].mod(DSAParameterGenerator.TWO.pow(n6)).multiply(DSAParameterGenerator.TWO.pow(n5 * n4)));
                    final BigInteger pow2 = DSAParameterGenerator.TWO.pow(n - 1);
                    final BigInteger add3 = add2.add(pow2);
                    subtract2 = add3.subtract(add3.mod(subtract.multiply(DSAParameterGenerator.TWO)).subtract(BigInteger.ONE));
                    if (subtract2.compareTo(pow2) > -1 && subtract2.isProbablePrime(n7)) {
                        break Block_14;
                    }
                    bigInteger2 = bigInteger2.add(BigInteger.valueOf(n5)).add(BigInteger.ONE);
                }
            }
        }
        return new BigInteger[] { subtract2, subtract, bigInteger, BigInteger.valueOf(i) };
    }
    
    private static BigInteger generateG(final BigInteger bigInteger, final BigInteger bigInteger2) {
        BigInteger bigInteger3;
        BigInteger divide;
        BigInteger bigInteger4;
        for (bigInteger3 = BigInteger.ONE, divide = bigInteger.subtract(BigInteger.ONE).divide(bigInteger2), bigInteger4 = BigInteger.ONE; bigInteger4.compareTo(DSAParameterGenerator.TWO) < 0; bigInteger4 = bigInteger3.modPow(divide, bigInteger), bigInteger3 = bigInteger3.add(BigInteger.ONE)) {}
        return bigInteger4;
    }
    
    private static byte[] toByteArray(final BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray[0] == 0) {
            final byte[] array = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, array, 0, array.length);
            byteArray = array;
        }
        return byteArray;
    }
    
    static {
        TWO = BigInteger.valueOf(2L);
    }
}
