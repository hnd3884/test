package org.bouncycastle.crypto.generators;

import java.security.SecureRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.NaccacheSternPrivateKeyParameters;
import org.bouncycastle.crypto.params.NaccacheSternKeyParameters;
import java.util.Random;
import java.util.Vector;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.NaccacheSternKeyGenerationParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class NaccacheSternKeyPairGenerator implements AsymmetricCipherKeyPairGenerator
{
    private static int[] smallPrimes;
    private NaccacheSternKeyGenerationParameters param;
    private static final BigInteger ONE;
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.param = (NaccacheSternKeyGenerationParameters)keyGenerationParameters;
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        final int strength = this.param.getStrength();
        final SecureRandom random = this.param.getRandom();
        final int certainty = this.param.getCertainty();
        final boolean debug = this.param.isDebug();
        if (debug) {
            System.out.println("Fetching first " + this.param.getCntSmallPrimes() + " primes.");
        }
        final Vector permuteList = permuteList(findFirstPrimes(this.param.getCntSmallPrimes()), random);
        BigInteger bigInteger = NaccacheSternKeyPairGenerator.ONE;
        BigInteger bigInteger2 = NaccacheSternKeyPairGenerator.ONE;
        for (int i = 0; i < permuteList.size() / 2; ++i) {
            bigInteger = bigInteger.multiply((BigInteger)permuteList.elementAt(i));
        }
        for (int j = permuteList.size() / 2; j < permuteList.size(); ++j) {
            bigInteger2 = bigInteger2.multiply((BigInteger)permuteList.elementAt(j));
        }
        final BigInteger multiply = bigInteger.multiply(bigInteger2);
        final int n = strength - multiply.bitLength() - 48;
        final BigInteger generatePrime = generatePrime(n / 2 + 1, certainty, random);
        final BigInteger generatePrime2 = generatePrime(n / 2 + 1, certainty, random);
        long n2 = 0L;
        if (debug) {
            System.out.println("generating p and q");
        }
        final BigInteger shiftLeft = generatePrime.multiply(bigInteger).shiftLeft(1);
        final BigInteger shiftLeft2 = generatePrime2.multiply(bigInteger2).shiftLeft(1);
        BigInteger generatePrime3;
        BigInteger add;
        BigInteger generatePrime4;
        BigInteger add2;
        while (true) {
            ++n2;
            generatePrime3 = generatePrime(24, certainty, random);
            add = generatePrime3.multiply(shiftLeft).add(NaccacheSternKeyPairGenerator.ONE);
            if (!add.isProbablePrime(certainty)) {
                continue;
            }
            while (true) {
                generatePrime4 = generatePrime(24, certainty, random);
                if (generatePrime3.equals(generatePrime4)) {
                    continue;
                }
                add2 = generatePrime4.multiply(shiftLeft2).add(NaccacheSternKeyPairGenerator.ONE);
                if (add2.isProbablePrime(certainty)) {
                    break;
                }
            }
            if (!multiply.gcd(generatePrime3.multiply(generatePrime4)).equals(NaccacheSternKeyPairGenerator.ONE)) {
                continue;
            }
            if (add.multiply(add2).bitLength() >= strength) {
                break;
            }
            if (!debug) {
                continue;
            }
            System.out.println("key size too small. Should be " + strength + " but is actually " + add.multiply(add2).bitLength());
        }
        if (debug) {
            System.out.println("needed " + n2 + " tries to generate p and q.");
        }
        final BigInteger multiply2 = add.multiply(add2);
        final BigInteger multiply3 = add.subtract(NaccacheSternKeyPairGenerator.ONE).multiply(add2.subtract(NaccacheSternKeyPairGenerator.ONE));
        long n3 = 0L;
        if (debug) {
            System.out.println("generating g");
        }
        BigInteger bigInteger4;
        while (true) {
            final Vector<BigInteger> vector = new Vector<BigInteger>();
            for (int k = 0; k != permuteList.size(); ++k) {
                final BigInteger divide = multiply3.divide(permuteList.elementAt(k));
                BigInteger bigInteger3;
                do {
                    ++n3;
                    bigInteger3 = new BigInteger(strength, certainty, random);
                } while (bigInteger3.modPow(divide, multiply2).equals(NaccacheSternKeyPairGenerator.ONE));
                vector.addElement(bigInteger3);
            }
            bigInteger4 = NaccacheSternKeyPairGenerator.ONE;
            for (int l = 0; l < permuteList.size(); ++l) {
                bigInteger4 = bigInteger4.multiply(vector.elementAt(l).modPow(multiply.divide((BigInteger)permuteList.elementAt(l)), multiply2)).mod(multiply2);
            }
            boolean b = false;
            for (int n4 = 0; n4 < permuteList.size(); ++n4) {
                if (bigInteger4.modPow(multiply3.divide((BigInteger)permuteList.elementAt(n4)), multiply2).equals(NaccacheSternKeyPairGenerator.ONE)) {
                    if (debug) {
                        System.out.println("g has order phi(n)/" + permuteList.elementAt(n4) + "\n g: " + bigInteger4);
                    }
                    b = true;
                    break;
                }
            }
            if (b) {
                continue;
            }
            if (bigInteger4.modPow(multiply3.divide(BigInteger.valueOf(4L)), multiply2).equals(NaccacheSternKeyPairGenerator.ONE)) {
                if (!debug) {
                    continue;
                }
                System.out.println("g has order phi(n)/4\n g:" + bigInteger4);
            }
            else if (bigInteger4.modPow(multiply3.divide(generatePrime3), multiply2).equals(NaccacheSternKeyPairGenerator.ONE)) {
                if (!debug) {
                    continue;
                }
                System.out.println("g has order phi(n)/p'\n g: " + bigInteger4);
            }
            else if (bigInteger4.modPow(multiply3.divide(generatePrime4), multiply2).equals(NaccacheSternKeyPairGenerator.ONE)) {
                if (!debug) {
                    continue;
                }
                System.out.println("g has order phi(n)/q'\n g: " + bigInteger4);
            }
            else if (bigInteger4.modPow(multiply3.divide(generatePrime), multiply2).equals(NaccacheSternKeyPairGenerator.ONE)) {
                if (!debug) {
                    continue;
                }
                System.out.println("g has order phi(n)/a\n g: " + bigInteger4);
            }
            else {
                if (!bigInteger4.modPow(multiply3.divide(generatePrime2), multiply2).equals(NaccacheSternKeyPairGenerator.ONE)) {
                    break;
                }
                if (!debug) {
                    continue;
                }
                System.out.println("g has order phi(n)/b\n g: " + bigInteger4);
            }
        }
        if (debug) {
            System.out.println("needed " + n3 + " tries to generate g");
            System.out.println();
            System.out.println("found new NaccacheStern cipher variables:");
            System.out.println("smallPrimes: " + permuteList);
            System.out.println("sigma:...... " + multiply + " (" + multiply.bitLength() + " bits)");
            System.out.println("a:.......... " + generatePrime);
            System.out.println("b:.......... " + generatePrime2);
            System.out.println("p':......... " + generatePrime3);
            System.out.println("q':......... " + generatePrime4);
            System.out.println("p:.......... " + add);
            System.out.println("q:.......... " + add2);
            System.out.println("n:.......... " + multiply2);
            System.out.println("phi(n):..... " + multiply3);
            System.out.println("g:.......... " + bigInteger4);
            System.out.println();
        }
        return new AsymmetricCipherKeyPair(new NaccacheSternKeyParameters(false, bigInteger4, multiply2, multiply.bitLength()), new NaccacheSternPrivateKeyParameters(bigInteger4, multiply2, multiply.bitLength(), permuteList, multiply3));
    }
    
    private static BigInteger generatePrime(final int n, final int n2, final SecureRandom secureRandom) {
        BigInteger bigInteger;
        for (bigInteger = new BigInteger(n, n2, secureRandom); bigInteger.bitLength() != n; bigInteger = new BigInteger(n, n2, secureRandom)) {}
        return bigInteger;
    }
    
    private static Vector permuteList(final Vector vector, final SecureRandom secureRandom) {
        final Vector vector2 = new Vector();
        final Vector vector3 = new Vector();
        for (int i = 0; i < vector.size(); ++i) {
            vector3.addElement(vector.elementAt(i));
        }
        vector2.addElement(vector3.elementAt(0));
        vector3.removeElementAt(0);
        while (vector3.size() != 0) {
            vector2.insertElementAt(vector3.elementAt(0), getInt(secureRandom, vector2.size() + 1));
            vector3.removeElementAt(0);
        }
        return vector2;
    }
    
    private static int getInt(final SecureRandom secureRandom, final int n) {
        if ((n & -n) == n) {
            return (int)(n * (long)(secureRandom.nextInt() & Integer.MAX_VALUE) >> 31);
        }
        int n2;
        int n3;
        do {
            n2 = (secureRandom.nextInt() & Integer.MAX_VALUE);
            n3 = n2 % n;
        } while (n2 - n3 + (n - 1) < 0);
        return n3;
    }
    
    private static Vector findFirstPrimes(final int n) {
        final Vector vector = new Vector(n);
        for (int i = 0; i != n; ++i) {
            vector.addElement(BigInteger.valueOf(NaccacheSternKeyPairGenerator.smallPrimes[i]));
        }
        return vector;
    }
    
    static {
        NaccacheSternKeyPairGenerator.smallPrimes = new int[] { 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557 };
        ONE = BigInteger.valueOf(1L);
    }
}
