package sun.security.rsa;

import java.util.Random;
import sun.security.jca.JCAUtil;
import java.util.WeakHashMap;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import javax.crypto.BadPaddingException;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAKey;
import java.math.BigInteger;
import java.util.Map;

public final class RSACore
{
    private static final boolean ENABLE_BLINDING = true;
    private static final Map<BigInteger, BlindingParameters> blindingCache;
    
    private RSACore() {
    }
    
    public static int getByteLength(final BigInteger bigInteger) {
        return bigInteger.bitLength() + 7 >> 3;
    }
    
    public static int getByteLength(final RSAKey rsaKey) {
        return getByteLength(rsaKey.getModulus());
    }
    
    public static byte[] convert(final byte[] array, final int n, final int n2) {
        if (n == 0 && n2 == array.length) {
            return array;
        }
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
    
    public static byte[] rsa(final byte[] array, final RSAPublicKey rsaPublicKey) throws BadPaddingException {
        return crypt(array, rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
    }
    
    @Deprecated
    public static byte[] rsa(final byte[] array, final RSAPrivateKey rsaPrivateKey) throws BadPaddingException {
        return rsa(array, rsaPrivateKey, true);
    }
    
    public static byte[] rsa(final byte[] array, final RSAPrivateKey rsaPrivateKey, final boolean b) throws BadPaddingException {
        if (rsaPrivateKey instanceof RSAPrivateCrtKey) {
            return crtCrypt(array, (RSAPrivateCrtKey)rsaPrivateKey, b);
        }
        return priCrypt(array, rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
    }
    
    private static byte[] crypt(final byte[] array, final BigInteger bigInteger, final BigInteger bigInteger2) throws BadPaddingException {
        return toByteArray(parseMsg(array, bigInteger).modPow(bigInteger2, bigInteger), getByteLength(bigInteger));
    }
    
    private static byte[] priCrypt(final byte[] array, final BigInteger bigInteger, final BigInteger bigInteger2) throws BadPaddingException {
        final BigInteger msg = parseMsg(array, bigInteger);
        final BlindingRandomPair blindingRandomPair = getBlindingRandomPair(null, bigInteger2, bigInteger);
        return toByteArray(msg.multiply(blindingRandomPair.u).mod(bigInteger).modPow(bigInteger2, bigInteger).multiply(blindingRandomPair.v).mod(bigInteger), getByteLength(bigInteger));
    }
    
    private static byte[] crtCrypt(final byte[] array, final RSAPrivateCrtKey rsaPrivateCrtKey, final boolean b) throws BadPaddingException {
        final BigInteger modulus = rsaPrivateCrtKey.getModulus();
        final BigInteger msg = parseMsg(array, modulus);
        final BigInteger primeP = rsaPrivateCrtKey.getPrimeP();
        final BigInteger primeQ = rsaPrivateCrtKey.getPrimeQ();
        final BigInteger primeExponentP = rsaPrivateCrtKey.getPrimeExponentP();
        final BigInteger primeExponentQ = rsaPrivateCrtKey.getPrimeExponentQ();
        final BigInteger crtCoefficient = rsaPrivateCrtKey.getCrtCoefficient();
        final BigInteger publicExponent = rsaPrivateCrtKey.getPublicExponent();
        final BlindingRandomPair blindingRandomPair = getBlindingRandomPair(publicExponent, rsaPrivateCrtKey.getPrivateExponent(), modulus);
        final BigInteger mod = msg.multiply(blindingRandomPair.u).mod(modulus);
        final BigInteger modPow = mod.modPow(primeExponentP, primeP);
        final BigInteger modPow2 = mod.modPow(primeExponentQ, primeQ);
        BigInteger bigInteger = modPow.subtract(modPow2);
        if (bigInteger.signum() < 0) {
            bigInteger = bigInteger.add(primeP);
        }
        final BigInteger mod2 = bigInteger.multiply(crtCoefficient).mod(primeP).multiply(primeQ).add(modPow2).multiply(blindingRandomPair.v).mod(modulus);
        if (b && !msg.equals(mod2.modPow(publicExponent, modulus))) {
            throw new BadPaddingException("RSA private key operation failed");
        }
        return toByteArray(mod2, getByteLength(modulus));
    }
    
    private static BigInteger parseMsg(final byte[] array, final BigInteger bigInteger) throws BadPaddingException {
        final BigInteger bigInteger2 = new BigInteger(1, array);
        if (bigInteger2.compareTo(bigInteger) >= 0) {
            throw new BadPaddingException("Message is larger than modulus");
        }
        return bigInteger2;
    }
    
    private static byte[] toByteArray(final BigInteger bigInteger, final int n) {
        final byte[] byteArray = bigInteger.toByteArray();
        final int length = byteArray.length;
        if (length == n) {
            return byteArray;
        }
        if (length == n + 1 && byteArray[0] == 0) {
            final byte[] array = new byte[n];
            System.arraycopy(byteArray, 1, array, 0, n);
            return array;
        }
        assert length < n;
        final byte[] array2 = new byte[n];
        System.arraycopy(byteArray, 0, array2, n - length, length);
        return array2;
    }
    
    private static BlindingRandomPair getBlindingRandomPair(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        BlindingParameters blindingParameters = null;
        synchronized (RSACore.blindingCache) {
            blindingParameters = RSACore.blindingCache.get(bigInteger3);
        }
        if (blindingParameters == null) {
            blindingParameters = new BlindingParameters(bigInteger, bigInteger2, bigInteger3);
            synchronized (RSACore.blindingCache) {
                RSACore.blindingCache.putIfAbsent(bigInteger3, blindingParameters);
            }
        }
        BlindingRandomPair blindingRandomPair = blindingParameters.getBlindingRandomPair(bigInteger, bigInteger2, bigInteger3);
        if (blindingRandomPair == null) {
            final BlindingParameters blindingParameters2 = new BlindingParameters(bigInteger, bigInteger2, bigInteger3);
            synchronized (RSACore.blindingCache) {
                RSACore.blindingCache.replace(bigInteger3, blindingParameters2);
            }
            blindingRandomPair = blindingParameters2.getBlindingRandomPair(bigInteger, bigInteger2, bigInteger3);
        }
        return blindingRandomPair;
    }
    
    static {
        blindingCache = new WeakHashMap<BigInteger, BlindingParameters>();
    }
    
    private static final class BlindingRandomPair
    {
        final BigInteger u;
        final BigInteger v;
        
        BlindingRandomPair(final BigInteger u, final BigInteger v) {
            this.u = u;
            this.v = v;
        }
    }
    
    private static final class BlindingParameters
    {
        private static final BigInteger BIG_TWO;
        private final BigInteger e;
        private final BigInteger d;
        private BigInteger u;
        private BigInteger v;
        
        BlindingParameters(final BigInteger e, final BigInteger d, final BigInteger bigInteger) {
            this.u = null;
            this.v = null;
            this.e = e;
            this.d = d;
            this.u = new BigInteger(bigInteger.bitLength(), JCAUtil.getSecureRandom()).mod(bigInteger);
            if (this.u.equals(BigInteger.ZERO)) {
                this.u = BigInteger.ONE;
            }
            try {
                this.v = this.u.modInverse(bigInteger);
            }
            catch (final ArithmeticException ex) {
                this.u = BigInteger.ONE;
                this.v = BigInteger.ONE;
            }
            if (e != null) {
                this.u = this.u.modPow(e, bigInteger);
            }
            else {
                this.v = this.v.modPow(d, bigInteger);
            }
        }
        
        BlindingRandomPair getBlindingRandomPair(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
            if ((this.e != null && this.e.equals(bigInteger)) || (this.d != null && this.d.equals(bigInteger2))) {
                BlindingRandomPair blindingRandomPair = null;
                synchronized (this) {
                    if (!this.u.equals(BigInteger.ZERO) && !this.v.equals(BigInteger.ZERO)) {
                        blindingRandomPair = new BlindingRandomPair(this.u, this.v);
                        if (this.u.compareTo(BigInteger.ONE) <= 0 || this.v.compareTo(BigInteger.ONE) <= 0) {
                            this.u = BigInteger.ZERO;
                            this.v = BigInteger.ZERO;
                        }
                        else {
                            this.u = this.u.modPow(BlindingParameters.BIG_TWO, bigInteger3);
                            this.v = this.v.modPow(BlindingParameters.BIG_TWO, bigInteger3);
                        }
                    }
                }
                return blindingRandomPair;
            }
            return null;
        }
        
        static {
            BIG_TWO = BigInteger.valueOf(2L);
        }
    }
}
