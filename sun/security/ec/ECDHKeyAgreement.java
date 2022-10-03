package sun.security.ec;

import java.security.GeneralSecurityException;
import java.security.Provider;
import sun.security.util.ECUtil;
import sun.security.ec.point.MutablePoint;
import sun.security.util.math.MutableIntegerModuloP;
import sun.security.util.math.IntegerFieldModuloP;
import java.security.spec.ECParameterSpec;
import java.util.Optional;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import java.security.spec.EllipticCurve;
import sun.security.ec.point.Point;
import sun.security.util.ArrayUtil;
import sun.security.ec.point.AffinePoint;
import java.security.ProviderException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Key;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.ECPrivateKey;
import javax.crypto.KeyAgreementSpi;

public final class ECDHKeyAgreement extends KeyAgreementSpi
{
    private ECPrivateKey privateKey;
    private ECPublicKey publicKey;
    private int secretLen;
    
    @Override
    protected void engineInit(final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("Key must be instance of PrivateKey");
        }
        this.privateKey = (ECPrivateKey)ECKeyFactory.toECKey(key);
        this.publicKey = null;
    }
    
    @Override
    protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("Parameters not supported");
        }
        this.engineInit(key, secureRandom);
    }
    
    @Override
    protected Key engineDoPhase(final Key key, final boolean b) throws InvalidKeyException, IllegalStateException {
        if (this.privateKey == null) {
            throw new IllegalStateException("Not initialized");
        }
        if (this.publicKey != null) {
            throw new IllegalStateException("Phase already executed");
        }
        if (!b) {
            throw new IllegalStateException("Only two party agreement supported, lastPhase must be true");
        }
        if (!(key instanceof ECPublicKey)) {
            throw new InvalidKeyException("Key must be a PublicKey with algorithm EC");
        }
        this.publicKey = (ECPublicKey)key;
        this.secretLen = this.publicKey.getParams().getCurve().getField().getFieldSize() + 7 >> 3;
        return null;
    }
    
    private static void validateCoordinate(final BigInteger bigInteger, final BigInteger bigInteger2) {
        if (bigInteger.compareTo(BigInteger.ZERO) < 0) {
            throw new ProviderException("invalid coordinate");
        }
        if (bigInteger.compareTo(bigInteger2) >= 0) {
            throw new ProviderException("invalid coordinate");
        }
    }
    
    private static void validate(final ECOperations ecOperations, final ECPublicKey ecPublicKey) {
        final BigInteger affineX = ecPublicKey.getW().getAffineX();
        final BigInteger affineY = ecPublicKey.getW().getAffineY();
        final BigInteger size = ecOperations.getField().getSize();
        validateCoordinate(affineX, size);
        validateCoordinate(affineY, size);
        final EllipticCurve curve = ecPublicKey.getParams().getCurve();
        if (!affineX.modPow(BigInteger.valueOf(3L), size).add(curve.getA().multiply(affineX)).add(curve.getB()).mod(size).equals(affineY.modPow(BigInteger.valueOf(2L), size).mod(size))) {
            throw new ProviderException("point is not on curve");
        }
        final AffinePoint affinePoint = new AffinePoint(ecOperations.getField().getElement(affineX), ecOperations.getField().getElement(affineY));
        final byte[] byteArray = ecPublicKey.getParams().getOrder().toByteArray();
        ArrayUtil.reverse(byteArray);
        if (!ecOperations.isNeutral(ecOperations.multiply(affinePoint, byteArray))) {
            throw new ProviderException("point has incorrect order");
        }
    }
    
    @Override
    protected byte[] engineGenerateSecret() throws IllegalStateException {
        if (this.privateKey == null || this.publicKey == null) {
            throw new IllegalStateException("Not initialized correctly");
        }
        return deriveKeyImpl(this.privateKey, this.publicKey).orElseGet(() -> deriveKeyNative(this.privateKey, this.publicKey));
    }
    
    @Override
    protected int engineGenerateSecret(final byte[] array, final int n) throws IllegalStateException, ShortBufferException {
        if (n + this.secretLen > array.length) {
            throw new ShortBufferException("Need " + this.secretLen + " bytes, only " + (array.length - n) + " available");
        }
        final byte[] engineGenerateSecret = this.engineGenerateSecret();
        System.arraycopy(engineGenerateSecret, 0, array, n, engineGenerateSecret.length);
        return engineGenerateSecret.length;
    }
    
    @Override
    protected SecretKey engineGenerateSecret(final String s) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        if (s == null) {
            throw new NoSuchAlgorithmException("Algorithm must not be null");
        }
        if (!s.equals("TlsPremasterSecret")) {
            throw new NoSuchAlgorithmException("Only supported for algorithm TlsPremasterSecret");
        }
        return new SecretKeySpec(this.engineGenerateSecret(), "TlsPremasterSecret");
    }
    
    private static Optional<byte[]> deriveKeyImpl(final ECPrivateKey ecPrivateKey, final ECPublicKey ecPublicKey) {
        final ECParameterSpec params = ecPrivateKey.getParams();
        final EllipticCurve curve = params.getCurve();
        final Optional<ECOperations> forParameters = ECOperations.forParameters(params);
        if (!forParameters.isPresent()) {
            return Optional.empty();
        }
        final ECOperations ecOperations = forParameters.get();
        if (!(ecPrivateKey instanceof ECPrivateKeyImpl)) {
            return Optional.empty();
        }
        final byte[] arrayS = ((ECPrivateKeyImpl)ecPrivateKey).getArrayS();
        validate(ecOperations, ecPublicKey);
        final IntegerFieldModuloP field = ecOperations.getField();
        final MutableIntegerModuloP mutable = field.getElement(arrayS).mutable();
        mutable.setProduct(field.getSmallValue(ecPrivateKey.getParams().getCofactor()));
        final int n = (curve.getField().getFieldSize() + 7) / 8;
        final MutablePoint multiply = ecOperations.multiply(new AffinePoint(field.getElement(ecPublicKey.getW().getAffineX()), field.getElement(ecPublicKey.getW().getAffineY())), mutable.asByteArray(n));
        if (ecOperations.isNeutral(multiply)) {
            throw new ProviderException("Product is zero");
        }
        final byte[] byteArray = multiply.asAffine().getX().asByteArray(n);
        ArrayUtil.reverse(byteArray);
        return Optional.of(byteArray);
    }
    
    private static byte[] deriveKeyNative(final ECPrivateKey ecPrivateKey, final ECPublicKey ecPublicKey) {
        final ECParameterSpec params = ecPrivateKey.getParams();
        final byte[] byteArray = ecPrivateKey.getS().toByteArray();
        final byte[] encodeECParameterSpec = ECUtil.encodeECParameterSpec(null, params);
        byte[] array;
        if (ecPublicKey instanceof ECPublicKeyImpl) {
            array = ((ECPublicKeyImpl)ecPublicKey).getEncodedPublicValue();
        }
        else {
            array = ECUtil.encodePoint(ecPublicKey.getW(), params.getCurve());
        }
        try {
            return deriveKey(byteArray, array, encodeECParameterSpec);
        }
        catch (final GeneralSecurityException ex) {
            throw new ProviderException("Could not derive key", ex);
        }
    }
    
    private static native byte[] deriveKey(final byte[] p0, final byte[] p1, final byte[] p2) throws GeneralSecurityException;
}
