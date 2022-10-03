package sun.security.ec;

import sun.security.util.math.intpoly.P521OrderField;
import sun.security.util.math.intpoly.P384OrderField;
import sun.security.util.math.intpoly.P256OrderField;
import java.util.Collections;
import sun.security.util.math.intpoly.IntegerPolynomialP521;
import sun.security.util.math.intpoly.IntegerPolynomialP384;
import sun.security.util.math.intpoly.IntegerPolynomialP256;
import java.util.HashMap;
import sun.security.ec.point.MutablePoint;
import sun.security.ec.point.AffinePoint;
import sun.security.util.math.MutableIntegerModuloP;
import java.security.ProviderException;
import sun.security.ec.point.Point;
import java.security.spec.EllipticCurve;
import sun.security.util.math.IntegerModuloP;
import java.security.spec.ECFieldFp;
import java.util.Optional;
import java.security.spec.ECParameterSpec;
import sun.security.ec.point.ProjectivePoint;
import sun.security.util.math.SmallValue;
import sun.security.util.math.ImmutableIntegerModuloP;
import sun.security.util.math.IntegerFieldModuloP;
import java.math.BigInteger;
import java.util.Map;

public class ECOperations
{
    static final Map<BigInteger, IntegerFieldModuloP> fields;
    static final Map<BigInteger, IntegerFieldModuloP> orderFields;
    final ImmutableIntegerModuloP b;
    final SmallValue one;
    final SmallValue two;
    final SmallValue three;
    final SmallValue four;
    final ProjectivePoint.Immutable neutral;
    private final IntegerFieldModuloP orderField;
    
    public static Optional<ECOperations> forParameters(final ECParameterSpec ecParameterSpec) {
        final EllipticCurve curve = ecParameterSpec.getCurve();
        if (!(curve.getField() instanceof ECFieldFp)) {
            return Optional.empty();
        }
        final ECFieldFp ecFieldFp = (ECFieldFp)curve.getField();
        if (!ecFieldFp.getP().subtract(curve.getA()).equals(BigInteger.valueOf(3L))) {
            return Optional.empty();
        }
        final IntegerFieldModuloP integerFieldModuloP = ECOperations.fields.get(ecFieldFp.getP());
        if (integerFieldModuloP == null) {
            return Optional.empty();
        }
        final IntegerFieldModuloP integerFieldModuloP2 = ECOperations.orderFields.get(ecParameterSpec.getOrder());
        if (integerFieldModuloP2 == null) {
            return Optional.empty();
        }
        return Optional.of(new ECOperations((IntegerModuloP)integerFieldModuloP.getElement(curve.getB()), integerFieldModuloP2));
    }
    
    public ECOperations(final IntegerModuloP integerModuloP, final IntegerFieldModuloP orderField) {
        this.b = integerModuloP.fixed();
        this.orderField = orderField;
        this.one = integerModuloP.getField().getSmallValue(1);
        this.two = integerModuloP.getField().getSmallValue(2);
        this.three = integerModuloP.getField().getSmallValue(3);
        this.four = integerModuloP.getField().getSmallValue(4);
        final IntegerFieldModuloP field = integerModuloP.getField();
        this.neutral = new ProjectivePoint.Immutable(field.get0(), field.get1(), field.get0());
    }
    
    public IntegerFieldModuloP getField() {
        return this.b.getField();
    }
    
    public IntegerFieldModuloP getOrderField() {
        return this.orderField;
    }
    
    protected ProjectivePoint.Immutable getNeutral() {
        return this.neutral;
    }
    
    public boolean isNeutral(final Point point) {
        final IntegerModuloP z = ((ProjectivePoint)point).getZ();
        return allZero(z.asByteArray((z.getField().getSize().bitLength() + 7) / 8));
    }
    
    byte[] seedToScalar(final byte[] array) throws IntermediateValueException {
        final int n = this.orderField.getSize().bitLength() + 64;
        if (array.length * 8 < n) {
            throw new ProviderException("Incorrect seed length: " + array.length * 8 + " < " + n);
        }
        final int n2 = n % 8;
        if (n2 != 0) {
            final int n3 = n / 8;
            final byte b = (byte)(255 >>> 8 - n2);
            final int n4 = n3;
            array[n4] &= b;
        }
        final ImmutableIntegerModuloP element = this.orderField.getElement(array, 0, (n + 7) / 8, (byte)0);
        final byte[] array2 = new byte[(this.orderField.getSize().bitLength() + 7) / 8];
        ((IntegerModuloP)element).asByteArray(array2);
        if (allZero(array2)) {
            throw new IntermediateValueException();
        }
        return array2;
    }
    
    public static boolean allZero(final byte[] array) {
        byte b = 0;
        for (int i = 0; i < array.length; ++i) {
            b |= array[i];
        }
        return b == 0;
    }
    
    private void lookup4(final ProjectivePoint.Immutable[] array, final int n, final ProjectivePoint.Mutable mutable, final IntegerModuloP integerModuloP) {
        for (int i = 0; i < 16; ++i) {
            final int n2 = n ^ i;
            mutable.conditionalSet(array[i], 1 - ((n2 & 0x1) | (n2 & 0x2) >>> 1 | (n2 & 0x4) >>> 2 | (n2 & 0x8) >>> 3));
        }
    }
    
    private void double4(final ProjectivePoint.Mutable mutable, final MutableIntegerModuloP mutableIntegerModuloP, final MutableIntegerModuloP mutableIntegerModuloP2, final MutableIntegerModuloP mutableIntegerModuloP3, final MutableIntegerModuloP mutableIntegerModuloP4, final MutableIntegerModuloP mutableIntegerModuloP5) {
        for (int i = 0; i < 4; ++i) {
            this.setDouble(mutable, mutableIntegerModuloP, mutableIntegerModuloP2, mutableIntegerModuloP3, mutableIntegerModuloP4, mutableIntegerModuloP5);
        }
    }
    
    public MutablePoint multiply(final AffinePoint value, final byte[] array) {
        final IntegerFieldModuloP field = value.getX().getField();
        final ImmutableIntegerModuloP get0 = field.get0();
        final MutableIntegerModuloP mutable = get0.mutable();
        final MutableIntegerModuloP mutable2 = get0.mutable();
        final MutableIntegerModuloP mutable3 = get0.mutable();
        final MutableIntegerModuloP mutable4 = get0.mutable();
        final MutableIntegerModuloP mutable5 = get0.mutable();
        final ProjectivePoint.Mutable mutable6 = new ProjectivePoint.Mutable(field);
        mutable6.getY().setValue((IntegerModuloP)field.get1().mutable());
        final ProjectivePoint.Immutable[] array2 = new ProjectivePoint.Immutable[16];
        array2[0] = mutable6.fixed();
        final ProjectivePoint.Mutable mutable7 = new ProjectivePoint.Mutable(field);
        mutable7.setValue(value);
        array2[1] = mutable7.fixed();
        for (int i = 2; i < 16; ++i) {
            this.setSum(mutable7, value, mutable, mutable2, mutable3, mutable4, mutable5);
            array2[i] = mutable7.fixed();
        }
        final ProjectivePoint.Mutable mutable8 = mutable7.mutable();
        for (int j = array.length - 1; j >= 0; --j) {
            this.double4(mutable6, mutable, mutable2, mutable3, mutable4, mutable5);
            this.lookup4(array2, (0xFF & array[j]) >>> 4, mutable8, (IntegerModuloP)get0);
            this.setSum(mutable6, mutable8, mutable, mutable2, mutable3, mutable4, mutable5);
            this.double4(mutable6, mutable, mutable2, mutable3, mutable4, mutable5);
            this.lookup4(array2, 0xF & array[j], mutable8, (IntegerModuloP)get0);
            this.setSum(mutable6, mutable8, mutable, mutable2, mutable3, mutable4, mutable5);
        }
        return mutable6;
    }
    
    private void setDouble(final ProjectivePoint.Mutable mutable, final MutableIntegerModuloP mutableIntegerModuloP, final MutableIntegerModuloP product, final MutableIntegerModuloP mutableIntegerModuloP2, final MutableIntegerModuloP mutableIntegerModuloP3, final MutableIntegerModuloP value) {
        mutableIntegerModuloP.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getX()).setSquare();
        product.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getY()).setSquare();
        mutableIntegerModuloP2.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getZ()).setSquare();
        mutableIntegerModuloP3.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getX()).setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        value.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getY()).setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutableIntegerModuloP3.setSum((IntegerModuloP)mutableIntegerModuloP3);
        mutable.getZ().setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getX());
        mutable.getZ().setProduct(this.two);
        mutable.getY().setValue((IntegerModuloP)mutableIntegerModuloP2).setProduct((IntegerModuloP)this.b);
        mutable.getY().setDifference(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getX().setValue(((ProjectivePoint<IntegerModuloP>)mutable).getY()).setProduct(this.two);
        mutable.getY().setSum(((ProjectivePoint<IntegerModuloP>)mutable).getX());
        mutable.getY().setReduced();
        mutable.getX().setValue((IntegerModuloP)product).setDifference(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutable.getY().setSum((IntegerModuloP)product);
        mutable.getY().setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getX());
        mutable.getX().setProduct((IntegerModuloP)mutableIntegerModuloP3);
        mutableIntegerModuloP3.setValue((IntegerModuloP)mutableIntegerModuloP2).setProduct(this.two);
        mutableIntegerModuloP2.setSum((IntegerModuloP)mutableIntegerModuloP3);
        mutable.getZ().setProduct((IntegerModuloP)this.b);
        mutableIntegerModuloP2.setReduced();
        mutable.getZ().setDifference((IntegerModuloP)mutableIntegerModuloP2);
        mutable.getZ().setDifference((IntegerModuloP)mutableIntegerModuloP);
        mutableIntegerModuloP3.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getZ()).setProduct(this.two);
        mutable.getZ().setReduced();
        mutable.getZ().setSum((IntegerModuloP)mutableIntegerModuloP3);
        mutableIntegerModuloP.setProduct(this.three);
        mutableIntegerModuloP.setDifference((IntegerModuloP)mutableIntegerModuloP2);
        mutableIntegerModuloP.setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getY().setSum((IntegerModuloP)mutableIntegerModuloP);
        value.setSum((IntegerModuloP)value);
        mutable.getZ().setProduct((IntegerModuloP)value);
        mutable.getX().setDifference(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getZ().setValue((IntegerModuloP)value).setProduct((IntegerModuloP)product);
        mutable.getZ().setProduct(this.four);
    }
    
    public void setSum(final MutablePoint mutablePoint, final AffinePoint affinePoint) {
        final ImmutableIntegerModuloP get0 = mutablePoint.getField().get0();
        this.setSum((ProjectivePoint.Mutable)mutablePoint, affinePoint, ((IntegerModuloP)get0).mutable(), ((IntegerModuloP)get0).mutable(), ((IntegerModuloP)get0).mutable(), ((IntegerModuloP)get0).mutable(), ((IntegerModuloP)get0).mutable());
    }
    
    private void setSum(final ProjectivePoint.Mutable mutable, final AffinePoint affinePoint, final MutableIntegerModuloP product, final MutableIntegerModuloP mutableIntegerModuloP, final MutableIntegerModuloP mutableIntegerModuloP2, final MutableIntegerModuloP mutableIntegerModuloP3, final MutableIntegerModuloP mutableIntegerModuloP4) {
        product.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getX()).setProduct((IntegerModuloP)affinePoint.getX());
        mutableIntegerModuloP.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getY()).setProduct((IntegerModuloP)affinePoint.getY());
        mutableIntegerModuloP3.setValue((IntegerModuloP)affinePoint.getX()).setSum((IntegerModuloP)affinePoint.getY());
        mutableIntegerModuloP4.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getX()).setSum(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutable.getX().setReduced();
        mutableIntegerModuloP3.setProduct((IntegerModuloP)mutableIntegerModuloP4);
        mutableIntegerModuloP4.setValue((IntegerModuloP)product).setSum((IntegerModuloP)mutableIntegerModuloP);
        mutableIntegerModuloP3.setDifference((IntegerModuloP)mutableIntegerModuloP4);
        mutableIntegerModuloP4.setValue((IntegerModuloP)affinePoint.getY()).setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutableIntegerModuloP4.setSum(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutable.getY().setValue((IntegerModuloP)affinePoint.getX()).setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getY().setSum(((ProjectivePoint<IntegerModuloP>)mutable).getX());
        mutableIntegerModuloP2.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getZ().setProduct((IntegerModuloP)this.b);
        mutable.getX().setValue(((ProjectivePoint<IntegerModuloP>)mutable).getY()).setDifference(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getX().setReduced();
        mutable.getZ().setValue(((ProjectivePoint<IntegerModuloP>)mutable).getX()).setProduct(this.two);
        mutable.getX().setSum(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getZ().setValue((IntegerModuloP)mutableIntegerModuloP).setDifference(((ProjectivePoint<IntegerModuloP>)mutable).getX());
        mutable.getX().setSum((IntegerModuloP)mutableIntegerModuloP);
        mutable.getY().setProduct((IntegerModuloP)this.b);
        mutableIntegerModuloP.setValue((IntegerModuloP)mutableIntegerModuloP2).setProduct(this.two);
        mutableIntegerModuloP2.setSum((IntegerModuloP)mutableIntegerModuloP);
        mutableIntegerModuloP2.setReduced();
        mutable.getY().setDifference((IntegerModuloP)mutableIntegerModuloP2);
        mutable.getY().setDifference((IntegerModuloP)product);
        mutable.getY().setReduced();
        mutableIntegerModuloP.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getY()).setProduct(this.two);
        mutable.getY().setSum((IntegerModuloP)mutableIntegerModuloP);
        mutableIntegerModuloP.setValue((IntegerModuloP)product).setProduct(this.two);
        product.setSum((IntegerModuloP)mutableIntegerModuloP);
        product.setDifference((IntegerModuloP)mutableIntegerModuloP2);
        mutableIntegerModuloP.setValue((IntegerModuloP)mutableIntegerModuloP4).setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutableIntegerModuloP2.setValue((IntegerModuloP)product).setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutable.getY().setValue(((ProjectivePoint<IntegerModuloP>)mutable).getX()).setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getY().setSum((IntegerModuloP)mutableIntegerModuloP2);
        mutable.getX().setProduct((IntegerModuloP)mutableIntegerModuloP3);
        mutable.getX().setDifference((IntegerModuloP)mutableIntegerModuloP);
        mutable.getZ().setProduct((IntegerModuloP)mutableIntegerModuloP4);
        mutableIntegerModuloP.setValue((IntegerModuloP)mutableIntegerModuloP3).setProduct((IntegerModuloP)product);
        mutable.getZ().setSum((IntegerModuloP)mutableIntegerModuloP);
    }
    
    private void setSum(final ProjectivePoint.Mutable mutable, final ProjectivePoint.Mutable mutable2, final MutableIntegerModuloP mutableIntegerModuloP, final MutableIntegerModuloP sum, final MutableIntegerModuloP mutableIntegerModuloP2, final MutableIntegerModuloP mutableIntegerModuloP3, final MutableIntegerModuloP mutableIntegerModuloP4) {
        mutableIntegerModuloP.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getX()).setProduct(((ProjectivePoint<IntegerModuloP>)mutable2).getX());
        sum.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getY()).setProduct(((ProjectivePoint<IntegerModuloP>)mutable2).getY());
        mutableIntegerModuloP2.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getZ()).setProduct(((ProjectivePoint<IntegerModuloP>)mutable2).getZ());
        mutableIntegerModuloP3.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getX()).setSum(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutableIntegerModuloP4.setValue(((ProjectivePoint<IntegerModuloP>)mutable2).getX()).setSum(((ProjectivePoint<IntegerModuloP>)mutable2).getY());
        mutableIntegerModuloP3.setProduct((IntegerModuloP)mutableIntegerModuloP4);
        mutableIntegerModuloP4.setValue((IntegerModuloP)mutableIntegerModuloP).setSum((IntegerModuloP)sum);
        mutableIntegerModuloP3.setDifference((IntegerModuloP)mutableIntegerModuloP4);
        mutableIntegerModuloP4.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getY()).setSum(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getY().setValue(((ProjectivePoint<IntegerModuloP>)mutable2).getY()).setSum(((ProjectivePoint<IntegerModuloP>)mutable2).getZ());
        mutableIntegerModuloP4.setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutable.getY().setValue((IntegerModuloP)sum).setSum((IntegerModuloP)mutableIntegerModuloP2);
        mutableIntegerModuloP4.setDifference(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutable.getX().setSum(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getY().setValue(((ProjectivePoint<IntegerModuloP>)mutable2).getX()).setSum(((ProjectivePoint<IntegerModuloP>)mutable2).getZ());
        mutable.getX().setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutable.getY().setValue((IntegerModuloP)mutableIntegerModuloP).setSum((IntegerModuloP)mutableIntegerModuloP2);
        mutable.getY().setAdditiveInverse().setSum(((ProjectivePoint<IntegerModuloP>)mutable).getX());
        mutable.getY().setReduced();
        mutable.getZ().setValue((IntegerModuloP)mutableIntegerModuloP2).setProduct((IntegerModuloP)this.b);
        mutable.getX().setValue(((ProjectivePoint<IntegerModuloP>)mutable).getY()).setDifference(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getZ().setValue(((ProjectivePoint<IntegerModuloP>)mutable).getX()).setProduct(this.two);
        mutable.getX().setSum(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getX().setReduced();
        mutable.getZ().setValue((IntegerModuloP)sum).setDifference(((ProjectivePoint<IntegerModuloP>)mutable).getX());
        mutable.getX().setSum((IntegerModuloP)sum);
        mutable.getY().setProduct((IntegerModuloP)this.b);
        sum.setValue((IntegerModuloP)mutableIntegerModuloP2).setSum((IntegerModuloP)mutableIntegerModuloP2);
        mutableIntegerModuloP2.setSum((IntegerModuloP)sum);
        mutableIntegerModuloP2.setReduced();
        mutable.getY().setDifference((IntegerModuloP)mutableIntegerModuloP2);
        mutable.getY().setDifference((IntegerModuloP)mutableIntegerModuloP);
        mutable.getY().setReduced();
        sum.setValue(((ProjectivePoint<IntegerModuloP>)mutable).getY()).setSum(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutable.getY().setSum((IntegerModuloP)sum);
        sum.setValue((IntegerModuloP)mutableIntegerModuloP).setProduct(this.two);
        mutableIntegerModuloP.setSum((IntegerModuloP)sum);
        mutableIntegerModuloP.setDifference((IntegerModuloP)mutableIntegerModuloP2);
        sum.setValue((IntegerModuloP)mutableIntegerModuloP4).setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutableIntegerModuloP2.setValue((IntegerModuloP)mutableIntegerModuloP).setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getY());
        mutable.getY().setValue(((ProjectivePoint<IntegerModuloP>)mutable).getX()).setProduct(((ProjectivePoint<IntegerModuloP>)mutable).getZ());
        mutable.getY().setSum((IntegerModuloP)mutableIntegerModuloP2);
        mutable.getX().setProduct((IntegerModuloP)mutableIntegerModuloP3);
        mutable.getX().setDifference((IntegerModuloP)sum);
        mutable.getZ().setProduct((IntegerModuloP)mutableIntegerModuloP4);
        sum.setValue((IntegerModuloP)mutableIntegerModuloP3).setProduct((IntegerModuloP)mutableIntegerModuloP);
        mutable.getZ().setSum((IntegerModuloP)sum);
    }
    
    static {
        final HashMap hashMap = new HashMap();
        hashMap.put(IntegerPolynomialP256.MODULUS, new IntegerPolynomialP256());
        hashMap.put(IntegerPolynomialP384.MODULUS, new IntegerPolynomialP384());
        hashMap.put(IntegerPolynomialP521.MODULUS, new IntegerPolynomialP521());
        fields = Collections.unmodifiableMap((Map<?, ?>)hashMap);
        final HashMap hashMap2 = new HashMap();
        hashMap2.put(P256OrderField.MODULUS, new P256OrderField());
        hashMap2.put(P384OrderField.MODULUS, new P384OrderField());
        hashMap2.put(P521OrderField.MODULUS, new P521OrderField());
        orderFields = Collections.unmodifiableMap((Map<?, ?>)hashMap2);
    }
    
    static class IntermediateValueException extends Exception
    {
        private static final long serialVersionUID = 1L;
    }
}
