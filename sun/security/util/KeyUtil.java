package sun.security.util;

import java.math.BigInteger;
import sun.security.jca.JCAUtil;
import java.security.SecureRandom;
import javax.crypto.spec.DHPublicKeySpec;
import java.security.spec.KeySpec;
import java.security.InvalidKeyException;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.AlgorithmParameters;
import java.security.interfaces.DSAParams;
import javax.crypto.interfaces.DHKey;
import java.security.interfaces.DSAKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import javax.crypto.SecretKey;
import java.security.Key;

public final class KeyUtil
{
    public static final int getKeySize(final Key key) {
        int n = -1;
        if (key instanceof Length) {
            try {
                n = ((Length)key).length();
            }
            catch (final UnsupportedOperationException ex) {}
            if (n >= 0) {
                return n;
            }
        }
        if (key instanceof SecretKey) {
            final SecretKey secretKey = (SecretKey)key;
            if ("RAW".equals(secretKey.getFormat()) && secretKey.getEncoded() != null) {
                n = secretKey.getEncoded().length * 8;
            }
        }
        else if (key instanceof RSAKey) {
            n = ((RSAKey)key).getModulus().bitLength();
        }
        else if (key instanceof ECKey) {
            n = ((ECKey)key).getParams().getOrder().bitLength();
        }
        else if (key instanceof DSAKey) {
            final DSAParams params = ((DSAKey)key).getParams();
            n = ((params != null) ? params.getP().bitLength() : -1);
        }
        else if (key instanceof DHKey) {
            n = ((DHKey)key).getParams().getP().bitLength();
        }
        return n;
    }
    
    public static final int getKeySize(final AlgorithmParameters algorithmParameters) {
        final String algorithm = algorithmParameters.getAlgorithm();
        switch (algorithm) {
            case "EC": {
                try {
                    final ECKeySizeParameterSpec ecKeySizeParameterSpec = algorithmParameters.getParameterSpec(ECKeySizeParameterSpec.class);
                    if (ecKeySizeParameterSpec != null) {
                        return ecKeySizeParameterSpec.getKeySize();
                    }
                }
                catch (final InvalidParameterSpecException ex) {}
                try {
                    final ECParameterSpec ecParameterSpec = algorithmParameters.getParameterSpec(ECParameterSpec.class);
                    if (ecParameterSpec != null) {
                        return ecParameterSpec.getOrder().bitLength();
                    }
                }
                catch (final InvalidParameterSpecException ex2) {}
                break;
            }
            case "DiffieHellman": {
                try {
                    final DHParameterSpec dhParameterSpec = algorithmParameters.getParameterSpec(DHParameterSpec.class);
                    if (dhParameterSpec != null) {
                        return dhParameterSpec.getP().bitLength();
                    }
                }
                catch (final InvalidParameterSpecException ex3) {}
                break;
            }
        }
        return -1;
    }
    
    public static final void validate(final Key key) throws InvalidKeyException {
        if (key == null) {
            throw new NullPointerException("The key to be validated cannot be null");
        }
        if (key instanceof DHPublicKey) {
            validateDHPublicKey((DHPublicKey)key);
        }
    }
    
    public static final void validate(final KeySpec keySpec) throws InvalidKeyException {
        if (keySpec == null) {
            throw new NullPointerException("The key spec to be validated cannot be null");
        }
        if (keySpec instanceof DHPublicKeySpec) {
            validateDHPublicKey((DHPublicKeySpec)keySpec);
        }
    }
    
    public static final boolean isOracleJCEProvider(final String s) {
        return s != null && (s.equals("SunJCE") || s.equals("SunMSCAPI") || s.equals("OracleUcrypto") || s.startsWith("SunPKCS11"));
    }
    
    public static byte[] checkTlsPreMasterSecretKey(final int n, final int n2, SecureRandom secureRandom, byte[] array, final boolean b) {
        if (secureRandom == null) {
            secureRandom = JCAUtil.getSecureRandom();
        }
        final byte[] array2 = new byte[48];
        secureRandom.nextBytes(array2);
        if (b || array == null) {
            return array2;
        }
        if (array.length != 48) {
            return array2;
        }
        final int n3 = (array[0] & 0xFF) << 8 | (array[1] & 0xFF);
        if (n != n3 && (n > 769 || n2 != n3)) {
            array = array2;
        }
        return array;
    }
    
    private static void validateDHPublicKey(final DHPublicKey dhPublicKey) throws InvalidKeyException {
        final DHParameterSpec params = dhPublicKey.getParams();
        validateDHPublicKey(params.getP(), params.getG(), dhPublicKey.getY());
    }
    
    private static void validateDHPublicKey(final DHPublicKeySpec dhPublicKeySpec) throws InvalidKeyException {
        validateDHPublicKey(dhPublicKeySpec.getP(), dhPublicKeySpec.getG(), dhPublicKeySpec.getY());
    }
    
    private static void validateDHPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) throws InvalidKeyException {
        final BigInteger one = BigInteger.ONE;
        final BigInteger subtract = bigInteger.subtract(BigInteger.ONE);
        if (bigInteger3.compareTo(one) <= 0) {
            throw new InvalidKeyException("Diffie-Hellman public key is too small");
        }
        if (bigInteger3.compareTo(subtract) >= 0) {
            throw new InvalidKeyException("Diffie-Hellman public key is too large");
        }
        if (bigInteger.remainder(bigInteger3).equals(BigInteger.ZERO)) {
            throw new InvalidKeyException("Invalid Diffie-Hellman parameters");
        }
    }
    
    public static byte[] trimZeroes(final byte[] array) {
        int n;
        for (n = 0; n < array.length - 1 && array[n] == 0; ++n) {}
        if (n == 0) {
            return array;
        }
        final byte[] array2 = new byte[array.length - n];
        System.arraycopy(array, n, array2, 0, array2.length);
        return array2;
    }
}
