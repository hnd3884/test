package sun.security.ec;

import java.security.GeneralSecurityException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import sun.security.util.math.IntegerFieldModuloP;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECPoint;
import sun.security.ec.point.AffinePoint;
import java.util.Optional;
import java.security.ProviderException;
import sun.security.jca.JCAUtil;
import java.security.KeyPair;
import java.security.AlgorithmParameters;
import java.io.IOException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.ECGenParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.ECParameterSpec;
import java.security.InvalidParameterException;
import java.security.Provider;
import sun.security.util.ECUtil;
import sun.security.util.SecurityProviderConstants;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.KeyPairGeneratorSpi;

public final class ECKeyPairGenerator extends KeyPairGeneratorSpi
{
    private static final int KEY_SIZE_MIN = 112;
    private static final int KEY_SIZE_MAX = 571;
    private SecureRandom random;
    private int keySize;
    private AlgorithmParameterSpec params;
    
    public ECKeyPairGenerator() {
        this.params = null;
        this.initialize(SecurityProviderConstants.DEF_EC_KEY_SIZE, null);
    }
    
    @Override
    public void initialize(final int n, final SecureRandom random) {
        this.checkKeySize(n);
        this.params = ECUtil.getECParameterSpec(null, n);
        if (this.params == null) {
            throw new InvalidParameterException("No EC parameters available for key size " + n + " bits");
        }
        this.random = random;
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws InvalidAlgorithmParameterException {
        ECParameterSpec params;
        if (algorithmParameterSpec instanceof ECParameterSpec) {
            params = ECUtil.getECParameterSpec(null, (ECParameterSpec)algorithmParameterSpec);
            if (params == null) {
                throw new InvalidAlgorithmParameterException("Unsupported curve: " + algorithmParameterSpec);
            }
        }
        else {
            if (!(algorithmParameterSpec instanceof ECGenParameterSpec)) {
                throw new InvalidAlgorithmParameterException("ECParameterSpec or ECGenParameterSpec required for EC");
            }
            final String name = ((ECGenParameterSpec)algorithmParameterSpec).getName();
            params = ECUtil.getECParameterSpec(null, name);
            if (params == null) {
                throw new InvalidAlgorithmParameterException("Unknown curve name: " + name);
            }
        }
        ensureCurveIsSupported(params);
        this.params = params;
        this.keySize = params.getCurve().getField().getFieldSize();
        this.random = random;
    }
    
    private static void ensureCurveIsSupported(final ECParameterSpec ecParameterSpec) throws InvalidAlgorithmParameterException {
        final AlgorithmParameters ecParameters = ECUtil.getECParameters(null);
        byte[] encoded;
        try {
            ecParameters.init(ecParameterSpec);
            encoded = ecParameters.getEncoded();
        }
        catch (final InvalidParameterSpecException ex) {
            throw new InvalidAlgorithmParameterException("Unsupported curve: " + ecParameterSpec.toString());
        }
        catch (final IOException ex2) {
            throw new RuntimeException(ex2);
        }
        if (!isCurveSupported(encoded)) {
            throw new InvalidAlgorithmParameterException("Unsupported curve: " + ecParameters.toString());
        }
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (this.random == null) {
            this.random = JCAUtil.getSecureRandom();
        }
        try {
            final Optional<KeyPair> generateKeyPairImpl = this.generateKeyPairImpl(this.random);
            if (generateKeyPairImpl.isPresent()) {
                return generateKeyPairImpl.get();
            }
            return this.generateKeyPairNative(this.random);
        }
        catch (final Exception ex) {
            throw new ProviderException(ex);
        }
    }
    
    private byte[] generatePrivateScalar(final SecureRandom secureRandom, final ECOperations ecOperations, final int n) {
        final int n2 = 128;
        final byte[] array = new byte[n];
        int i = 0;
        while (i < n2) {
            secureRandom.nextBytes(array);
            try {
                return ecOperations.seedToScalar(array);
            }
            catch (final ECOperations.IntermediateValueException ex) {
                ++i;
                continue;
            }
            break;
        }
        throw new ProviderException("Unable to produce private key after " + n2 + " attempts");
    }
    
    private Optional<KeyPair> generateKeyPairImpl(final SecureRandom secureRandom) throws InvalidKeyException {
        final ECParameterSpec ecParameterSpec = (ECParameterSpec)this.params;
        final Optional<ECOperations> forParameters = ECOperations.forParameters(ecParameterSpec);
        if (!forParameters.isPresent()) {
            return Optional.empty();
        }
        final ECOperations ecOperations = forParameters.get();
        final IntegerFieldModuloP field = ecOperations.getField();
        final byte[] generatePrivateScalar = this.generatePrivateScalar(secureRandom, ecOperations, (ecParameterSpec.getOrder().bitLength() + 64 + 7) / 8);
        final ECPoint generator = ecParameterSpec.getGenerator();
        final AffinePoint affine = ecOperations.multiply(new AffinePoint(field.getElement(generator.getAffineX()), field.getElement(generator.getAffineY())), generatePrivateScalar).asAffine();
        return Optional.of(new KeyPair(new ECPublicKeyImpl(new ECPoint(affine.getX().asBigInteger(), affine.getY().asBigInteger()), ecParameterSpec), new ECPrivateKeyImpl(generatePrivateScalar, ecParameterSpec)));
    }
    
    private KeyPair generateKeyPairNative(final SecureRandom secureRandom) throws Exception {
        final ECParameterSpec ecParameterSpec = (ECParameterSpec)this.params;
        final byte[] encodeECParameterSpec = ECUtil.encodeECParameterSpec(null, ecParameterSpec);
        final byte[] array = new byte[((this.keySize + 7 >> 3) + 1) * 2];
        secureRandom.nextBytes(array);
        final Object[] generateECKeyPair = generateECKeyPair(this.keySize, encodeECParameterSpec, array);
        return new KeyPair(new ECPublicKeyImpl(ECUtil.decodePoint((byte[])generateECKeyPair[1], ecParameterSpec.getCurve()), ecParameterSpec), new ECPrivateKeyImpl(new BigInteger(1, (byte[])generateECKeyPair[0]), ecParameterSpec));
    }
    
    private void checkKeySize(final int keySize) throws InvalidParameterException {
        if (keySize < 112) {
            throw new InvalidParameterException("Key size must be at least 112 bits");
        }
        if (keySize > 571) {
            throw new InvalidParameterException("Key size must be at most 571 bits");
        }
        this.keySize = keySize;
    }
    
    private static native boolean isCurveSupported(final byte[] p0);
    
    private static native Object[] generateECKeyPair(final int p0, final byte[] p1, final byte[] p2) throws GeneralSecurityException;
}
