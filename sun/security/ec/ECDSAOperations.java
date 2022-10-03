package sun.security.ec;

import sun.security.util.math.MutableIntegerModuloP;
import sun.security.util.math.ImmutableIntegerModuloP;
import sun.security.util.math.IntegerModuloP;
import sun.security.util.ArrayUtil;
import java.security.ProviderException;
import java.util.Optional;
import java.security.spec.ECParameterSpec;
import sun.security.util.math.IntegerFieldModuloP;
import java.security.spec.ECPoint;
import sun.security.ec.point.AffinePoint;

public class ECDSAOperations
{
    private final ECOperations ecOps;
    private final AffinePoint basePoint;
    
    public ECDSAOperations(final ECOperations ecOps, final ECPoint ecPoint) {
        this.ecOps = ecOps;
        this.basePoint = toAffinePoint(ecPoint, ecOps.getField());
    }
    
    public ECOperations getEcOperations() {
        return this.ecOps;
    }
    
    public AffinePoint basePointMultiply(final byte[] array) {
        return this.ecOps.multiply(this.basePoint, array).asAffine();
    }
    
    public static AffinePoint toAffinePoint(final ECPoint ecPoint, final IntegerFieldModuloP integerFieldModuloP) {
        return new AffinePoint(integerFieldModuloP.getElement(ecPoint.getAffineX()), integerFieldModuloP.getElement(ecPoint.getAffineY()));
    }
    
    public static Optional<ECDSAOperations> forParameters(final ECParameterSpec ecParameterSpec) {
        return ECOperations.forParameters(ecParameterSpec).map(ecOperations -> new ECDSAOperations(ecOperations, ecParameterSpec2.getGenerator()));
    }
    
    public byte[] signDigest(final byte[] array, final byte[] array2, final Seed seed) throws ECOperations.IntermediateValueException {
        return this.signDigest(array, array2, new Nonce(this.ecOps.seedToScalar(seed.getSeedValue())));
    }
    
    public byte[] signDigest(final byte[] array, final byte[] array2, final Nonce nonce) throws ECOperations.IntermediateValueException {
        final IntegerFieldModuloP orderField = this.ecOps.getOrderField();
        final int bitLength = orderField.getSize().bitLength();
        if (bitLength % 8 != 0 && bitLength < array2.length * 8) {
            throw new ProviderException("Invalid digest length");
        }
        final byte[] nonceValue = nonce.getNonceValue();
        final int n = (orderField.getSize().bitLength() + 7) / 8;
        if (nonceValue.length != n) {
            throw new ProviderException("Incorrect nonce length");
        }
        final ImmutableIntegerModuloP x = this.ecOps.multiply(this.basePoint, nonceValue).asAffine().getX();
        final byte[] array3 = new byte[n];
        ((IntegerModuloP)x).asByteArray(array3);
        final ImmutableIntegerModuloP element = orderField.getElement(array3);
        ((IntegerModuloP)element).asByteArray(array3);
        final byte[] array4 = new byte[2 * n];
        ArrayUtil.reverse(array3);
        System.arraycopy(array3, 0, array4, 0, n);
        if (ECOperations.allZero(array3)) {
            throw new ECOperations.IntermediateValueException();
        }
        final ImmutableIntegerModuloP element2 = orderField.getElement(array);
        final int min = Math.min(n, array2.length);
        final byte[] array5 = new byte[min];
        System.arraycopy(array2, 0, array5, 0, min);
        ArrayUtil.reverse(array5);
        final ImmutableIntegerModuloP element3 = orderField.getElement(array5);
        final ImmutableIntegerModuloP multiplicativeInverse = ((IntegerModuloP)orderField.getElement(nonceValue)).multiplicativeInverse();
        final MutableIntegerModuloP mutable = ((IntegerModuloP)element).mutable();
        mutable.setProduct((IntegerModuloP)element2).setSum((IntegerModuloP)element3).setProduct((IntegerModuloP)multiplicativeInverse);
        mutable.asByteArray(array3);
        ArrayUtil.reverse(array3);
        System.arraycopy(array3, 0, array4, n, n);
        if (ECOperations.allZero(array3)) {
            throw new ECOperations.IntermediateValueException();
        }
        return array4;
    }
    
    public static class Seed
    {
        private final byte[] seedValue;
        
        public Seed(final byte[] seedValue) {
            this.seedValue = seedValue;
        }
        
        public byte[] getSeedValue() {
            return this.seedValue;
        }
    }
    
    public static class Nonce
    {
        private final byte[] nonceValue;
        
        public Nonce(final byte[] nonceValue) {
            this.nonceValue = nonceValue;
        }
        
        public byte[] getNonceValue() {
            return this.nonceValue;
        }
    }
}
