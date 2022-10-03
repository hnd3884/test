package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import java.io.Serializable;
import org.apache.commons.math3.RealFieldElement;

public class DerivativeStructure implements RealFieldElement<DerivativeStructure>, Serializable
{
    private static final long serialVersionUID = 20120730L;
    private transient DSCompiler compiler;
    private final double[] data;
    
    private DerivativeStructure(final DSCompiler compiler) {
        this.compiler = compiler;
        this.data = new double[compiler.getSize()];
    }
    
    public DerivativeStructure(final int parameters, final int order) throws NumberIsTooLargeException {
        this(DSCompiler.getCompiler(parameters, order));
    }
    
    public DerivativeStructure(final int parameters, final int order, final double value) throws NumberIsTooLargeException {
        this(parameters, order);
        this.data[0] = value;
    }
    
    public DerivativeStructure(final int parameters, final int order, final int index, final double value) throws NumberIsTooLargeException {
        this(parameters, order, value);
        if (index >= parameters) {
            throw new NumberIsTooLargeException(index, parameters, false);
        }
        if (order > 0) {
            this.data[DSCompiler.getCompiler(index, order).getSize()] = 1.0;
        }
    }
    
    public DerivativeStructure(final double a1, final DerivativeStructure ds1, final double a2, final DerivativeStructure ds2) throws DimensionMismatchException {
        this(ds1.compiler);
        this.compiler.checkCompatibility(ds2.compiler);
        this.compiler.linearCombination(a1, ds1.data, 0, a2, ds2.data, 0, this.data, 0);
    }
    
    public DerivativeStructure(final double a1, final DerivativeStructure ds1, final double a2, final DerivativeStructure ds2, final double a3, final DerivativeStructure ds3) throws DimensionMismatchException {
        this(ds1.compiler);
        this.compiler.checkCompatibility(ds2.compiler);
        this.compiler.checkCompatibility(ds3.compiler);
        this.compiler.linearCombination(a1, ds1.data, 0, a2, ds2.data, 0, a3, ds3.data, 0, this.data, 0);
    }
    
    public DerivativeStructure(final double a1, final DerivativeStructure ds1, final double a2, final DerivativeStructure ds2, final double a3, final DerivativeStructure ds3, final double a4, final DerivativeStructure ds4) throws DimensionMismatchException {
        this(ds1.compiler);
        this.compiler.checkCompatibility(ds2.compiler);
        this.compiler.checkCompatibility(ds3.compiler);
        this.compiler.checkCompatibility(ds4.compiler);
        this.compiler.linearCombination(a1, ds1.data, 0, a2, ds2.data, 0, a3, ds3.data, 0, a4, ds4.data, 0, this.data, 0);
    }
    
    public DerivativeStructure(final int parameters, final int order, final double... derivatives) throws DimensionMismatchException, NumberIsTooLargeException {
        this(parameters, order);
        if (derivatives.length != this.data.length) {
            throw new DimensionMismatchException(derivatives.length, this.data.length);
        }
        System.arraycopy(derivatives, 0, this.data, 0, this.data.length);
    }
    
    private DerivativeStructure(final DerivativeStructure ds) {
        this.compiler = ds.compiler;
        this.data = ds.data.clone();
    }
    
    public int getFreeParameters() {
        return this.compiler.getFreeParameters();
    }
    
    public int getOrder() {
        return this.compiler.getOrder();
    }
    
    public DerivativeStructure createConstant(final double c) {
        return new DerivativeStructure(this.getFreeParameters(), this.getOrder(), c);
    }
    
    public double getReal() {
        return this.data[0];
    }
    
    public double getValue() {
        return this.data[0];
    }
    
    public double getPartialDerivative(final int... orders) throws DimensionMismatchException, NumberIsTooLargeException {
        return this.data[this.compiler.getPartialDerivativeIndex(orders)];
    }
    
    public double[] getAllDerivatives() {
        return this.data.clone();
    }
    
    public DerivativeStructure add(final double a) {
        final DerivativeStructure ds = new DerivativeStructure(this);
        final double[] data = ds.data;
        final int n = 0;
        data[n] += a;
        return ds;
    }
    
    public DerivativeStructure add(final DerivativeStructure a) throws DimensionMismatchException {
        this.compiler.checkCompatibility(a.compiler);
        final DerivativeStructure ds = new DerivativeStructure(this);
        this.compiler.add(this.data, 0, a.data, 0, ds.data, 0);
        return ds;
    }
    
    public DerivativeStructure subtract(final double a) {
        return this.add(-a);
    }
    
    public DerivativeStructure subtract(final DerivativeStructure a) throws DimensionMismatchException {
        this.compiler.checkCompatibility(a.compiler);
        final DerivativeStructure ds = new DerivativeStructure(this);
        this.compiler.subtract(this.data, 0, a.data, 0, ds.data, 0);
        return ds;
    }
    
    public DerivativeStructure multiply(final int n) {
        return this.multiply((double)n);
    }
    
    public DerivativeStructure multiply(final double a) {
        final DerivativeStructure ds = new DerivativeStructure(this);
        for (int i = 0; i < ds.data.length; ++i) {
            final double[] data = ds.data;
            final int n = i;
            data[n] *= a;
        }
        return ds;
    }
    
    public DerivativeStructure multiply(final DerivativeStructure a) throws DimensionMismatchException {
        this.compiler.checkCompatibility(a.compiler);
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.multiply(this.data, 0, a.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure divide(final double a) {
        final DerivativeStructure ds = new DerivativeStructure(this);
        for (int i = 0; i < ds.data.length; ++i) {
            final double[] data = ds.data;
            final int n = i;
            data[n] /= a;
        }
        return ds;
    }
    
    public DerivativeStructure divide(final DerivativeStructure a) throws DimensionMismatchException {
        this.compiler.checkCompatibility(a.compiler);
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.divide(this.data, 0, a.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure remainder(final double a) {
        final DerivativeStructure ds = new DerivativeStructure(this);
        ds.data[0] = FastMath.IEEEremainder(ds.data[0], a);
        return ds;
    }
    
    public DerivativeStructure remainder(final DerivativeStructure a) throws DimensionMismatchException {
        this.compiler.checkCompatibility(a.compiler);
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.remainder(this.data, 0, a.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure negate() {
        final DerivativeStructure ds = new DerivativeStructure(this.compiler);
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = -this.data[i];
        }
        return ds;
    }
    
    public DerivativeStructure abs() {
        if (Double.doubleToLongBits(this.data[0]) < 0L) {
            return this.negate();
        }
        return this;
    }
    
    public DerivativeStructure ceil() {
        return new DerivativeStructure(this.compiler.getFreeParameters(), this.compiler.getOrder(), FastMath.ceil(this.data[0]));
    }
    
    public DerivativeStructure floor() {
        return new DerivativeStructure(this.compiler.getFreeParameters(), this.compiler.getOrder(), FastMath.floor(this.data[0]));
    }
    
    public DerivativeStructure rint() {
        return new DerivativeStructure(this.compiler.getFreeParameters(), this.compiler.getOrder(), FastMath.rint(this.data[0]));
    }
    
    public long round() {
        return FastMath.round(this.data[0]);
    }
    
    public DerivativeStructure signum() {
        return new DerivativeStructure(this.compiler.getFreeParameters(), this.compiler.getOrder(), FastMath.signum(this.data[0]));
    }
    
    public DerivativeStructure copySign(final DerivativeStructure sign) {
        final long m = Double.doubleToLongBits(this.data[0]);
        final long s = Double.doubleToLongBits(sign.data[0]);
        if ((m >= 0L && s >= 0L) || (m < 0L && s < 0L)) {
            return this;
        }
        return this.negate();
    }
    
    public DerivativeStructure copySign(final double sign) {
        final long m = Double.doubleToLongBits(this.data[0]);
        final long s = Double.doubleToLongBits(sign);
        if ((m >= 0L && s >= 0L) || (m < 0L && s < 0L)) {
            return this;
        }
        return this.negate();
    }
    
    public int getExponent() {
        return FastMath.getExponent(this.data[0]);
    }
    
    public DerivativeStructure scalb(final int n) {
        final DerivativeStructure ds = new DerivativeStructure(this.compiler);
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = FastMath.scalb(this.data[i], n);
        }
        return ds;
    }
    
    public DerivativeStructure hypot(final DerivativeStructure y) throws DimensionMismatchException {
        this.compiler.checkCompatibility(y.compiler);
        if (Double.isInfinite(this.data[0]) || Double.isInfinite(y.data[0])) {
            return new DerivativeStructure(this.compiler.getFreeParameters(), this.compiler.getFreeParameters(), Double.POSITIVE_INFINITY);
        }
        if (Double.isNaN(this.data[0]) || Double.isNaN(y.data[0])) {
            return new DerivativeStructure(this.compiler.getFreeParameters(), this.compiler.getFreeParameters(), Double.NaN);
        }
        final int expX = this.getExponent();
        final int expY = y.getExponent();
        if (expX > expY + 27) {
            return this.abs();
        }
        if (expY > expX + 27) {
            return y.abs();
        }
        final int middleExp = (expX + expY) / 2;
        final DerivativeStructure scaledX = this.scalb(-middleExp);
        final DerivativeStructure scaledY = y.scalb(-middleExp);
        final DerivativeStructure scaledH = scaledX.multiply(scaledX).add(scaledY.multiply(scaledY)).sqrt();
        return scaledH.scalb(middleExp);
    }
    
    public static DerivativeStructure hypot(final DerivativeStructure x, final DerivativeStructure y) throws DimensionMismatchException {
        return x.hypot(y);
    }
    
    public DerivativeStructure compose(final double... f) throws DimensionMismatchException {
        if (f.length != this.getOrder() + 1) {
            throw new DimensionMismatchException(f.length, this.getOrder() + 1);
        }
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.compose(this.data, 0, f, result.data, 0);
        return result;
    }
    
    public DerivativeStructure reciprocal() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.pow(this.data, 0, -1, result.data, 0);
        return result;
    }
    
    public DerivativeStructure sqrt() {
        return this.rootN(2);
    }
    
    public DerivativeStructure cbrt() {
        return this.rootN(3);
    }
    
    public DerivativeStructure rootN(final int n) {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.rootN(this.data, 0, n, result.data, 0);
        return result;
    }
    
    public Field<DerivativeStructure> getField() {
        return new Field<DerivativeStructure>() {
            public DerivativeStructure getZero() {
                return new DerivativeStructure(DerivativeStructure.this.compiler.getFreeParameters(), DerivativeStructure.this.compiler.getOrder(), 0.0);
            }
            
            public DerivativeStructure getOne() {
                return new DerivativeStructure(DerivativeStructure.this.compiler.getFreeParameters(), DerivativeStructure.this.compiler.getOrder(), 1.0);
            }
            
            public Class<? extends FieldElement<DerivativeStructure>> getRuntimeClass() {
                return DerivativeStructure.class;
            }
        };
    }
    
    public static DerivativeStructure pow(final double a, final DerivativeStructure x) {
        final DerivativeStructure result = new DerivativeStructure(x.compiler);
        x.compiler.pow(a, x.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure pow(final double p) {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.pow(this.data, 0, p, result.data, 0);
        return result;
    }
    
    public DerivativeStructure pow(final int n) {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.pow(this.data, 0, n, result.data, 0);
        return result;
    }
    
    public DerivativeStructure pow(final DerivativeStructure e) throws DimensionMismatchException {
        this.compiler.checkCompatibility(e.compiler);
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.pow(this.data, 0, e.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure exp() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.exp(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure expm1() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.expm1(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure log() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.log(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure log1p() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.log1p(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure log10() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.log10(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure cos() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.cos(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure sin() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.sin(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure tan() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.tan(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure acos() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.acos(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure asin() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.asin(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure atan() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.atan(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure atan2(final DerivativeStructure x) throws DimensionMismatchException {
        this.compiler.checkCompatibility(x.compiler);
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.atan2(this.data, 0, x.data, 0, result.data, 0);
        return result;
    }
    
    public static DerivativeStructure atan2(final DerivativeStructure y, final DerivativeStructure x) throws DimensionMismatchException {
        return y.atan2(x);
    }
    
    public DerivativeStructure cosh() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.cosh(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure sinh() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.sinh(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure tanh() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.tanh(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure acosh() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.acosh(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure asinh() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.asinh(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure atanh() {
        final DerivativeStructure result = new DerivativeStructure(this.compiler);
        this.compiler.atanh(this.data, 0, result.data, 0);
        return result;
    }
    
    public DerivativeStructure toDegrees() {
        final DerivativeStructure ds = new DerivativeStructure(this.compiler);
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = FastMath.toDegrees(this.data[i]);
        }
        return ds;
    }
    
    public DerivativeStructure toRadians() {
        final DerivativeStructure ds = new DerivativeStructure(this.compiler);
        for (int i = 0; i < ds.data.length; ++i) {
            ds.data[i] = FastMath.toRadians(this.data[i]);
        }
        return ds;
    }
    
    public double taylor(final double... delta) throws MathArithmeticException {
        return this.compiler.taylor(this.data, 0, delta);
    }
    
    public DerivativeStructure linearCombination(final DerivativeStructure[] a, final DerivativeStructure[] b) throws DimensionMismatchException {
        final double[] aDouble = new double[a.length];
        for (int i = 0; i < a.length; ++i) {
            aDouble[i] = a[i].getValue();
        }
        final double[] bDouble = new double[b.length];
        for (int j = 0; j < b.length; ++j) {
            bDouble[j] = b[j].getValue();
        }
        final double accurateValue = MathArrays.linearCombination(aDouble, bDouble);
        DerivativeStructure simpleValue = a[0].getField().getZero();
        for (int k = 0; k < a.length; ++k) {
            simpleValue = simpleValue.add(a[k].multiply(b[k]));
        }
        final double[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return new DerivativeStructure(simpleValue.getFreeParameters(), simpleValue.getOrder(), all);
    }
    
    public DerivativeStructure linearCombination(final double[] a, final DerivativeStructure[] b) throws DimensionMismatchException {
        final double[] bDouble = new double[b.length];
        for (int i = 0; i < b.length; ++i) {
            bDouble[i] = b[i].getValue();
        }
        final double accurateValue = MathArrays.linearCombination(a, bDouble);
        DerivativeStructure simpleValue = b[0].getField().getZero();
        for (int j = 0; j < a.length; ++j) {
            simpleValue = simpleValue.add(b[j].multiply(a[j]));
        }
        final double[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return new DerivativeStructure(simpleValue.getFreeParameters(), simpleValue.getOrder(), all);
    }
    
    public DerivativeStructure linearCombination(final DerivativeStructure a1, final DerivativeStructure b1, final DerivativeStructure a2, final DerivativeStructure b2) throws DimensionMismatchException {
        final double accurateValue = MathArrays.linearCombination(a1.getValue(), b1.getValue(), a2.getValue(), b2.getValue());
        final DerivativeStructure simpleValue = a1.multiply(b1).add(a2.multiply(b2));
        final double[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return new DerivativeStructure(this.getFreeParameters(), this.getOrder(), all);
    }
    
    public DerivativeStructure linearCombination(final double a1, final DerivativeStructure b1, final double a2, final DerivativeStructure b2) throws DimensionMismatchException {
        final double accurateValue = MathArrays.linearCombination(a1, b1.getValue(), a2, b2.getValue());
        final DerivativeStructure simpleValue = b1.multiply(a1).add(b2.multiply(a2));
        final double[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return new DerivativeStructure(this.getFreeParameters(), this.getOrder(), all);
    }
    
    public DerivativeStructure linearCombination(final DerivativeStructure a1, final DerivativeStructure b1, final DerivativeStructure a2, final DerivativeStructure b2, final DerivativeStructure a3, final DerivativeStructure b3) throws DimensionMismatchException {
        final double accurateValue = MathArrays.linearCombination(a1.getValue(), b1.getValue(), a2.getValue(), b2.getValue(), a3.getValue(), b3.getValue());
        final DerivativeStructure simpleValue = a1.multiply(b1).add(a2.multiply(b2)).add(a3.multiply(b3));
        final double[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return new DerivativeStructure(this.getFreeParameters(), this.getOrder(), all);
    }
    
    public DerivativeStructure linearCombination(final double a1, final DerivativeStructure b1, final double a2, final DerivativeStructure b2, final double a3, final DerivativeStructure b3) throws DimensionMismatchException {
        final double accurateValue = MathArrays.linearCombination(a1, b1.getValue(), a2, b2.getValue(), a3, b3.getValue());
        final DerivativeStructure simpleValue = b1.multiply(a1).add(b2.multiply(a2)).add(b3.multiply(a3));
        final double[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return new DerivativeStructure(this.getFreeParameters(), this.getOrder(), all);
    }
    
    public DerivativeStructure linearCombination(final DerivativeStructure a1, final DerivativeStructure b1, final DerivativeStructure a2, final DerivativeStructure b2, final DerivativeStructure a3, final DerivativeStructure b3, final DerivativeStructure a4, final DerivativeStructure b4) throws DimensionMismatchException {
        final double accurateValue = MathArrays.linearCombination(a1.getValue(), b1.getValue(), a2.getValue(), b2.getValue(), a3.getValue(), b3.getValue(), a4.getValue(), b4.getValue());
        final DerivativeStructure simpleValue = a1.multiply(b1).add(a2.multiply(b2)).add(a3.multiply(b3)).add(a4.multiply(b4));
        final double[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return new DerivativeStructure(this.getFreeParameters(), this.getOrder(), all);
    }
    
    public DerivativeStructure linearCombination(final double a1, final DerivativeStructure b1, final double a2, final DerivativeStructure b2, final double a3, final DerivativeStructure b3, final double a4, final DerivativeStructure b4) throws DimensionMismatchException {
        final double accurateValue = MathArrays.linearCombination(a1, b1.getValue(), a2, b2.getValue(), a3, b3.getValue(), a4, b4.getValue());
        final DerivativeStructure simpleValue = b1.multiply(a1).add(b2.multiply(a2)).add(b3.multiply(a3)).add(b4.multiply(a4));
        final double[] all = simpleValue.getAllDerivatives();
        all[0] = accurateValue;
        return new DerivativeStructure(this.getFreeParameters(), this.getOrder(), all);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof DerivativeStructure) {
            final DerivativeStructure rhs = (DerivativeStructure)other;
            return this.getFreeParameters() == rhs.getFreeParameters() && this.getOrder() == rhs.getOrder() && MathArrays.equals(this.data, rhs.data);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 227 + 229 * this.getFreeParameters() + 233 * this.getOrder() + 239 * MathUtils.hash(this.data);
    }
    
    private Object writeReplace() {
        return new DataTransferObject(this.compiler.getFreeParameters(), this.compiler.getOrder(), this.data);
    }
    
    private static class DataTransferObject implements Serializable
    {
        private static final long serialVersionUID = 20120730L;
        private final int variables;
        private final int order;
        private final double[] data;
        
        DataTransferObject(final int variables, final int order, final double[] data) {
            this.variables = variables;
            this.order = order;
            this.data = data;
        }
        
        private Object readResolve() {
            return new DerivativeStructure(this.variables, this.order, this.data);
        }
    }
}
