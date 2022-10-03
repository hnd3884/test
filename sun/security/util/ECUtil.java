package sun.security.util;

import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.AlgorithmParameters;
import java.security.Provider;
import java.math.BigInteger;
import java.util.Arrays;
import java.io.IOException;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

public final class ECUtil
{
    public static ECPoint decodePoint(final byte[] array, final EllipticCurve ellipticCurve) throws IOException {
        if (array.length == 0 || array[0] != 4) {
            throw new IOException("Only uncompressed point format supported");
        }
        final int n = (array.length - 1) / 2;
        if (n != ellipticCurve.getField().getFieldSize() + 7 >> 3) {
            throw new IOException("Point does not match field size");
        }
        return new ECPoint(new BigInteger(1, Arrays.copyOfRange(array, 1, 1 + n)), new BigInteger(1, Arrays.copyOfRange(array, n + 1, n + 1 + n)));
    }
    
    public static byte[] encodePoint(final ECPoint ecPoint, final EllipticCurve ellipticCurve) {
        final int n = ellipticCurve.getField().getFieldSize() + 7 >> 3;
        final byte[] trimZeroes = trimZeroes(ecPoint.getAffineX().toByteArray());
        final byte[] trimZeroes2 = trimZeroes(ecPoint.getAffineY().toByteArray());
        if (trimZeroes.length > n || trimZeroes2.length > n) {
            throw new RuntimeException("Point coordinates do not match field size");
        }
        final byte[] array = new byte[1 + (n << 1)];
        array[0] = 4;
        System.arraycopy(trimZeroes, 0, array, n - trimZeroes.length + 1, trimZeroes.length);
        System.arraycopy(trimZeroes2, 0, array, array.length - trimZeroes2.length, trimZeroes2.length);
        return array;
    }
    
    public static byte[] trimZeroes(final byte[] array) {
        int n;
        for (n = 0; n < array.length - 1 && array[n] == 0; ++n) {}
        if (n == 0) {
            return array;
        }
        return Arrays.copyOfRange(array, n, array.length);
    }
    
    public static AlgorithmParameters getECParameters(final Provider provider) {
        try {
            if (provider != null) {
                return AlgorithmParameters.getInstance("EC", provider);
            }
            return AlgorithmParameters.getInstance("EC");
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static byte[] encodeECParameterSpec(final Provider provider, final ECParameterSpec ecParameterSpec) {
        final AlgorithmParameters ecParameters = getECParameters(provider);
        try {
            ecParameters.init(ecParameterSpec);
        }
        catch (final InvalidParameterSpecException ex) {
            throw new RuntimeException("Not a known named curve: " + ecParameterSpec);
        }
        try {
            return ecParameters.getEncoded();
        }
        catch (final IOException ex2) {
            throw new RuntimeException(ex2);
        }
    }
    
    public static ECParameterSpec getECParameterSpec(final Provider provider, final ECParameterSpec ecParameterSpec) {
        final AlgorithmParameters ecParameters = getECParameters(provider);
        try {
            ecParameters.init(ecParameterSpec);
            return ecParameters.getParameterSpec(ECParameterSpec.class);
        }
        catch (final InvalidParameterSpecException ex) {
            return null;
        }
    }
    
    public static ECParameterSpec getECParameterSpec(final Provider provider, final byte[] array) throws IOException {
        final AlgorithmParameters ecParameters = getECParameters(provider);
        ecParameters.init(array);
        try {
            return ecParameters.getParameterSpec(ECParameterSpec.class);
        }
        catch (final InvalidParameterSpecException ex) {
            return null;
        }
    }
    
    public static ECParameterSpec getECParameterSpec(final Provider provider, final String s) {
        final AlgorithmParameters ecParameters = getECParameters(provider);
        try {
            ecParameters.init(new ECGenParameterSpec(s));
            return ecParameters.getParameterSpec(ECParameterSpec.class);
        }
        catch (final InvalidParameterSpecException ex) {
            return null;
        }
    }
    
    public static ECParameterSpec getECParameterSpec(final Provider provider, final int n) {
        final AlgorithmParameters ecParameters = getECParameters(provider);
        try {
            ecParameters.init(new ECKeySizeParameterSpec(n));
            return ecParameters.getParameterSpec(ECParameterSpec.class);
        }
        catch (final InvalidParameterSpecException ex) {
            return null;
        }
    }
    
    public static String getCurveName(final Provider provider, final ECParameterSpec ecParameterSpec) {
        final AlgorithmParameters ecParameters = getECParameters(provider);
        ECGenParameterSpec ecGenParameterSpec;
        try {
            ecParameters.init(ecParameterSpec);
            ecGenParameterSpec = ecParameters.getParameterSpec(ECGenParameterSpec.class);
        }
        catch (final InvalidParameterSpecException ex) {
            return null;
        }
        if (ecGenParameterSpec == null) {
            return null;
        }
        return ecGenParameterSpec.getName();
    }
    
    public static boolean equals(final ECParameterSpec ecParameterSpec, final ECParameterSpec ecParameterSpec2) {
        return ecParameterSpec == ecParameterSpec2 || (ecParameterSpec != null && ecParameterSpec2 != null && ecParameterSpec.getCofactor() == ecParameterSpec2.getCofactor() && ecParameterSpec.getOrder().equals(ecParameterSpec2.getOrder()) && ecParameterSpec.getCurve().equals(ecParameterSpec2.getCurve()) && ecParameterSpec.getGenerator().equals(ecParameterSpec2.getGenerator()));
    }
    
    public static byte[] encodeSignature(final byte[] array) throws SignatureException {
        try {
            final int n = array.length >> 1;
            final byte[] array2 = new byte[n];
            System.arraycopy(array, 0, array2, 0, n);
            final BigInteger bigInteger = new BigInteger(1, array2);
            System.arraycopy(array, n, array2, 0, n);
            final BigInteger bigInteger2 = new BigInteger(1, array2);
            final DerOutputStream derOutputStream = new DerOutputStream(array.length + 10);
            derOutputStream.putInteger(bigInteger);
            derOutputStream.putInteger(bigInteger2);
            return new DerValue((byte)48, derOutputStream.toByteArray()).toByteArray();
        }
        catch (final Exception ex) {
            throw new SignatureException("Could not encode signature", ex);
        }
    }
    
    public static byte[] decodeSignature(final byte[] array) throws SignatureException {
        try {
            final DerInputStream derInputStream = new DerInputStream(array, 0, array.length, false);
            final DerValue[] sequence = derInputStream.getSequence(2);
            if (sequence.length != 2 || derInputStream.available() != 0) {
                throw new IOException("Invalid encoding for signature");
            }
            final BigInteger positiveBigInteger = sequence[0].getPositiveBigInteger();
            final BigInteger positiveBigInteger2 = sequence[1].getPositiveBigInteger();
            final byte[] trimZeroes = trimZeroes(positiveBigInteger.toByteArray());
            final byte[] trimZeroes2 = trimZeroes(positiveBigInteger2.toByteArray());
            final int max = Math.max(trimZeroes.length, trimZeroes2.length);
            final byte[] array2 = new byte[max << 1];
            System.arraycopy(trimZeroes, 0, array2, max - trimZeroes.length, trimZeroes.length);
            System.arraycopy(trimZeroes2, 0, array2, array2.length - trimZeroes2.length, trimZeroes2.length);
            return array2;
        }
        catch (final Exception ex) {
            throw new SignatureException("Invalid encoding for signature", ex);
        }
    }
    
    private ECUtil() {
    }
}
