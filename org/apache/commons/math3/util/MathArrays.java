package org.apache.commons.math3.util;

import java.util.Iterator;
import java.util.TreeSet;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.exception.NoDataException;
import java.util.Arrays;
import java.lang.reflect.Array;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.DimensionMismatchException;

public class MathArrays
{
    private MathArrays() {
    }
    
    public static double[] scale(final double val, final double[] arr) {
        final double[] newArr = new double[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            newArr[i] = arr[i] * val;
        }
        return newArr;
    }
    
    public static void scaleInPlace(final double val, final double[] arr) {
        for (int i = 0; i < arr.length; ++i) {
            final int n = i;
            arr[n] *= val;
        }
    }
    
    public static double[] ebeAdd(final double[] a, final double[] b) throws DimensionMismatchException {
        checkEqualLength(a, b);
        final double[] result = a.clone();
        for (int i = 0; i < a.length; ++i) {
            final double[] array = result;
            final int n = i;
            array[n] += b[i];
        }
        return result;
    }
    
    public static double[] ebeSubtract(final double[] a, final double[] b) throws DimensionMismatchException {
        checkEqualLength(a, b);
        final double[] result = a.clone();
        for (int i = 0; i < a.length; ++i) {
            final double[] array = result;
            final int n = i;
            array[n] -= b[i];
        }
        return result;
    }
    
    public static double[] ebeMultiply(final double[] a, final double[] b) throws DimensionMismatchException {
        checkEqualLength(a, b);
        final double[] result = a.clone();
        for (int i = 0; i < a.length; ++i) {
            final double[] array = result;
            final int n = i;
            array[n] *= b[i];
        }
        return result;
    }
    
    public static double[] ebeDivide(final double[] a, final double[] b) throws DimensionMismatchException {
        checkEqualLength(a, b);
        final double[] result = a.clone();
        for (int i = 0; i < a.length; ++i) {
            final double[] array = result;
            final int n = i;
            array[n] /= b[i];
        }
        return result;
    }
    
    public static double distance1(final double[] p1, final double[] p2) throws DimensionMismatchException {
        checkEqualLength(p1, p2);
        double sum = 0.0;
        for (int i = 0; i < p1.length; ++i) {
            sum += FastMath.abs(p1[i] - p2[i]);
        }
        return sum;
    }
    
    public static int distance1(final int[] p1, final int[] p2) throws DimensionMismatchException {
        checkEqualLength(p1, p2);
        int sum = 0;
        for (int i = 0; i < p1.length; ++i) {
            sum += FastMath.abs(p1[i] - p2[i]);
        }
        return sum;
    }
    
    public static double distance(final double[] p1, final double[] p2) throws DimensionMismatchException {
        checkEqualLength(p1, p2);
        double sum = 0.0;
        for (int i = 0; i < p1.length; ++i) {
            final double dp = p1[i] - p2[i];
            sum += dp * dp;
        }
        return FastMath.sqrt(sum);
    }
    
    public static double cosAngle(final double[] v1, final double[] v2) {
        return linearCombination(v1, v2) / (safeNorm(v1) * safeNorm(v2));
    }
    
    public static double distance(final int[] p1, final int[] p2) throws DimensionMismatchException {
        checkEqualLength(p1, p2);
        double sum = 0.0;
        for (int i = 0; i < p1.length; ++i) {
            final double dp = p1[i] - p2[i];
            sum += dp * dp;
        }
        return FastMath.sqrt(sum);
    }
    
    public static double distanceInf(final double[] p1, final double[] p2) throws DimensionMismatchException {
        checkEqualLength(p1, p2);
        double max = 0.0;
        for (int i = 0; i < p1.length; ++i) {
            max = FastMath.max(max, FastMath.abs(p1[i] - p2[i]));
        }
        return max;
    }
    
    public static int distanceInf(final int[] p1, final int[] p2) throws DimensionMismatchException {
        checkEqualLength(p1, p2);
        int max = 0;
        for (int i = 0; i < p1.length; ++i) {
            max = FastMath.max(max, FastMath.abs(p1[i] - p2[i]));
        }
        return max;
    }
    
    public static <T extends Comparable<? super T>> boolean isMonotonic(final T[] val, final OrderDirection dir, final boolean strict) {
        T previous = val[0];
        for (int max = val.length, i = 1; i < max; ++i) {
            switch (dir) {
                case INCREASING: {
                    final int comp = previous.compareTo(val[i]);
                    if (strict) {
                        if (comp >= 0) {
                            return false;
                        }
                        break;
                    }
                    else {
                        if (comp > 0) {
                            return false;
                        }
                        break;
                    }
                    break;
                }
                case DECREASING: {
                    final int comp = val[i].compareTo(previous);
                    if (strict) {
                        if (comp >= 0) {
                            return false;
                        }
                        break;
                    }
                    else {
                        if (comp > 0) {
                            return false;
                        }
                        break;
                    }
                    break;
                }
                default: {
                    throw new MathInternalError();
                }
            }
            previous = val[i];
        }
        return true;
    }
    
    public static boolean isMonotonic(final double[] val, final OrderDirection dir, final boolean strict) {
        return checkOrder(val, dir, strict, false);
    }
    
    public static boolean checkEqualLength(final double[] a, final double[] b, final boolean abort) {
        if (a.length == b.length) {
            return true;
        }
        if (abort) {
            throw new DimensionMismatchException(a.length, b.length);
        }
        return false;
    }
    
    public static void checkEqualLength(final double[] a, final double[] b) {
        checkEqualLength(a, b, true);
    }
    
    public static boolean checkEqualLength(final int[] a, final int[] b, final boolean abort) {
        if (a.length == b.length) {
            return true;
        }
        if (abort) {
            throw new DimensionMismatchException(a.length, b.length);
        }
        return false;
    }
    
    public static void checkEqualLength(final int[] a, final int[] b) {
        checkEqualLength(a, b, true);
    }
    
    public static boolean checkOrder(final double[] val, final OrderDirection dir, final boolean strict, final boolean abort) throws NonMonotonicSequenceException {
        double previous = val[0];
        int max = 0;
        int index = 0;
    Label_0132:
        for (max = val.length, index = 1; index < max; ++index) {
            switch (dir) {
                case INCREASING: {
                    if (strict) {
                        if (val[index] <= previous) {
                            break Label_0132;
                        }
                        break;
                    }
                    else {
                        if (val[index] < previous) {
                            break Label_0132;
                        }
                        break;
                    }
                    break;
                }
                case DECREASING: {
                    if (strict) {
                        if (val[index] >= previous) {
                            break Label_0132;
                        }
                        break;
                    }
                    else {
                        if (val[index] > previous) {
                            break Label_0132;
                        }
                        break;
                    }
                    break;
                }
                default: {
                    throw new MathInternalError();
                }
            }
            previous = val[index];
        }
        if (index == max) {
            return true;
        }
        if (abort) {
            throw new NonMonotonicSequenceException(val[index], previous, index, dir, strict);
        }
        return false;
    }
    
    public static void checkOrder(final double[] val, final OrderDirection dir, final boolean strict) throws NonMonotonicSequenceException {
        checkOrder(val, dir, strict, true);
    }
    
    public static void checkOrder(final double[] val) throws NonMonotonicSequenceException {
        checkOrder(val, OrderDirection.INCREASING, true);
    }
    
    public static void checkRectangular(final long[][] in) throws NullArgumentException, DimensionMismatchException {
        MathUtils.checkNotNull(in);
        for (int i = 1; i < in.length; ++i) {
            if (in[i].length != in[0].length) {
                throw new DimensionMismatchException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, in[i].length, in[0].length);
            }
        }
    }
    
    public static void checkPositive(final double[] in) throws NotStrictlyPositiveException {
        for (int i = 0; i < in.length; ++i) {
            if (in[i] <= 0.0) {
                throw new NotStrictlyPositiveException(in[i]);
            }
        }
    }
    
    public static void checkNotNaN(final double[] in) throws NotANumberException {
        for (int i = 0; i < in.length; ++i) {
            if (Double.isNaN(in[i])) {
                throw new NotANumberException();
            }
        }
    }
    
    public static void checkNonNegative(final long[] in) throws NotPositiveException {
        for (int i = 0; i < in.length; ++i) {
            if (in[i] < 0L) {
                throw new NotPositiveException(in[i]);
            }
        }
    }
    
    public static void checkNonNegative(final long[][] in) throws NotPositiveException {
        for (int i = 0; i < in.length; ++i) {
            for (int j = 0; j < in[i].length; ++j) {
                if (in[i][j] < 0L) {
                    throw new NotPositiveException(in[i][j]);
                }
            }
        }
    }
    
    public static double safeNorm(final double[] v) {
        final double rdwarf = 3.834E-20;
        final double rgiant = 1.304E19;
        double s1 = 0.0;
        double s2 = 0.0;
        double s3 = 0.0;
        double x1max = 0.0;
        double x3max = 0.0;
        final double floatn = v.length;
        final double agiant = rgiant / floatn;
        for (int i = 0; i < v.length; ++i) {
            final double xabs = FastMath.abs(v[i]);
            if (xabs < rdwarf || xabs > agiant) {
                if (xabs > rdwarf) {
                    if (xabs > x1max) {
                        final double r = x1max / xabs;
                        s1 = 1.0 + s1 * r * r;
                        x1max = xabs;
                    }
                    else {
                        final double r = xabs / x1max;
                        s1 += r * r;
                    }
                }
                else if (xabs > x3max) {
                    final double r = x3max / xabs;
                    s3 = 1.0 + s3 * r * r;
                    x3max = xabs;
                }
                else if (xabs != 0.0) {
                    final double r = xabs / x3max;
                    s3 += r * r;
                }
            }
            else {
                s2 += xabs * xabs;
            }
        }
        double norm;
        if (s1 != 0.0) {
            norm = x1max * Math.sqrt(s1 + s2 / x1max / x1max);
        }
        else if (s2 == 0.0) {
            norm = x3max * Math.sqrt(s3);
        }
        else if (s2 >= x3max) {
            norm = Math.sqrt(s2 * (1.0 + x3max / s2 * (x3max * s3)));
        }
        else {
            norm = Math.sqrt(x3max * (s2 / x3max + x3max * s3));
        }
        return norm;
    }
    
    public static void sortInPlace(final double[] x, final double[]... yList) throws DimensionMismatchException, NullArgumentException {
        sortInPlace(x, OrderDirection.INCREASING, yList);
    }
    
    public static void sortInPlace(final double[] x, final OrderDirection dir, final double[]... yList) throws NullArgumentException, DimensionMismatchException {
        if (x == null) {
            throw new NullArgumentException();
        }
        final int yListLen = yList.length;
        final int len = x.length;
        for (final double[] y : yList) {
            if (y == null) {
                throw new NullArgumentException();
            }
            if (y.length != len) {
                throw new DimensionMismatchException(y.length, len);
            }
        }
        final List<PairDoubleInteger> list = new ArrayList<PairDoubleInteger>(len);
        for (int i = 0; i < len; ++i) {
            list.add(new PairDoubleInteger(x[i], i));
        }
        final Comparator<PairDoubleInteger> comp = (dir == OrderDirection.INCREASING) ? new Comparator<PairDoubleInteger>() {
            public int compare(final PairDoubleInteger o1, final PairDoubleInteger o2) {
                return Double.compare(o1.getKey(), o2.getKey());
            }
        } : new Comparator<PairDoubleInteger>() {
            public int compare(final PairDoubleInteger o1, final PairDoubleInteger o2) {
                return Double.compare(o2.getKey(), o1.getKey());
            }
        };
        Collections.sort(list, comp);
        final int[] indices = new int[len];
        for (int k = 0; k < len; ++k) {
            final PairDoubleInteger e = list.get(k);
            x[k] = e.getKey();
            indices[k] = e.getValue();
        }
        for (final double[] yInPlace : yList) {
            final double[] yOrig = yInPlace.clone();
            for (int m = 0; m < len; ++m) {
                yInPlace[m] = yOrig[indices[m]];
            }
        }
    }
    
    public static int[] copyOf(final int[] source) {
        return copyOf(source, source.length);
    }
    
    public static double[] copyOf(final double[] source) {
        return copyOf(source, source.length);
    }
    
    public static int[] copyOf(final int[] source, final int len) {
        final int[] output = new int[len];
        System.arraycopy(source, 0, output, 0, FastMath.min(len, source.length));
        return output;
    }
    
    public static double[] copyOf(final double[] source, final int len) {
        final double[] output = new double[len];
        System.arraycopy(source, 0, output, 0, FastMath.min(len, source.length));
        return output;
    }
    
    public static double[] copyOfRange(final double[] source, final int from, final int to) {
        final int len = to - from;
        final double[] output = new double[len];
        System.arraycopy(source, from, output, 0, FastMath.min(len, source.length - from));
        return output;
    }
    
    public static double linearCombination(final double[] a, final double[] b) throws DimensionMismatchException {
        checkEqualLength(a, b);
        final int len = a.length;
        if (len == 1) {
            return a[0] * b[0];
        }
        final double[] prodHigh = new double[len];
        double prodLowSum = 0.0;
        for (int i = 0; i < len; ++i) {
            final double ai = a[i];
            final double aHigh = Double.longBitsToDouble(Double.doubleToRawLongBits(ai) & 0xFFFFFFFFF8000000L);
            final double aLow = ai - aHigh;
            final double bi = b[i];
            final double bHigh = Double.longBitsToDouble(Double.doubleToRawLongBits(bi) & 0xFFFFFFFFF8000000L);
            final double bLow = bi - bHigh;
            prodHigh[i] = ai * bi;
            final double prodLow = aLow * bLow - (prodHigh[i] - aHigh * bHigh - aLow * bHigh - aHigh * bLow);
            prodLowSum += prodLow;
        }
        final double prodHighCur = prodHigh[0];
        double prodHighNext = prodHigh[1];
        double sHighPrev = prodHighCur + prodHighNext;
        double sPrime = sHighPrev - prodHighNext;
        double sLowSum = prodHighNext - (sHighPrev - sPrime) + (prodHighCur - sPrime);
        for (int lenMinusOne = len - 1, j = 1; j < lenMinusOne; ++j) {
            prodHighNext = prodHigh[j + 1];
            final double sHighCur = sHighPrev + prodHighNext;
            sPrime = sHighCur - prodHighNext;
            sLowSum += prodHighNext - (sHighCur - sPrime) + (sHighPrev - sPrime);
            sHighPrev = sHighCur;
        }
        double result = sHighPrev + (prodLowSum + sLowSum);
        if (Double.isNaN(result)) {
            result = 0.0;
            for (int k = 0; k < len; ++k) {
                result += a[k] * b[k];
            }
        }
        return result;
    }
    
    public static double linearCombination(final double a1, final double b1, final double a2, final double b2) {
        final double a1High = Double.longBitsToDouble(Double.doubleToRawLongBits(a1) & 0xFFFFFFFFF8000000L);
        final double a1Low = a1 - a1High;
        final double b1High = Double.longBitsToDouble(Double.doubleToRawLongBits(b1) & 0xFFFFFFFFF8000000L);
        final double b1Low = b1 - b1High;
        final double prod1High = a1 * b1;
        final double prod1Low = a1Low * b1Low - (prod1High - a1High * b1High - a1Low * b1High - a1High * b1Low);
        final double a2High = Double.longBitsToDouble(Double.doubleToRawLongBits(a2) & 0xFFFFFFFFF8000000L);
        final double a2Low = a2 - a2High;
        final double b2High = Double.longBitsToDouble(Double.doubleToRawLongBits(b2) & 0xFFFFFFFFF8000000L);
        final double b2Low = b2 - b2High;
        final double prod2High = a2 * b2;
        final double prod2Low = a2Low * b2Low - (prod2High - a2High * b2High - a2Low * b2High - a2High * b2Low);
        final double s12High = prod1High + prod2High;
        final double s12Prime = s12High - prod2High;
        final double s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime);
        double result = s12High + (prod1Low + prod2Low + s12Low);
        if (Double.isNaN(result)) {
            result = a1 * b1 + a2 * b2;
        }
        return result;
    }
    
    public static double linearCombination(final double a1, final double b1, final double a2, final double b2, final double a3, final double b3) {
        final double a1High = Double.longBitsToDouble(Double.doubleToRawLongBits(a1) & 0xFFFFFFFFF8000000L);
        final double a1Low = a1 - a1High;
        final double b1High = Double.longBitsToDouble(Double.doubleToRawLongBits(b1) & 0xFFFFFFFFF8000000L);
        final double b1Low = b1 - b1High;
        final double prod1High = a1 * b1;
        final double prod1Low = a1Low * b1Low - (prod1High - a1High * b1High - a1Low * b1High - a1High * b1Low);
        final double a2High = Double.longBitsToDouble(Double.doubleToRawLongBits(a2) & 0xFFFFFFFFF8000000L);
        final double a2Low = a2 - a2High;
        final double b2High = Double.longBitsToDouble(Double.doubleToRawLongBits(b2) & 0xFFFFFFFFF8000000L);
        final double b2Low = b2 - b2High;
        final double prod2High = a2 * b2;
        final double prod2Low = a2Low * b2Low - (prod2High - a2High * b2High - a2Low * b2High - a2High * b2Low);
        final double a3High = Double.longBitsToDouble(Double.doubleToRawLongBits(a3) & 0xFFFFFFFFF8000000L);
        final double a3Low = a3 - a3High;
        final double b3High = Double.longBitsToDouble(Double.doubleToRawLongBits(b3) & 0xFFFFFFFFF8000000L);
        final double b3Low = b3 - b3High;
        final double prod3High = a3 * b3;
        final double prod3Low = a3Low * b3Low - (prod3High - a3High * b3High - a3Low * b3High - a3High * b3Low);
        final double s12High = prod1High + prod2High;
        final double s12Prime = s12High - prod2High;
        final double s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime);
        final double s123High = s12High + prod3High;
        final double s123Prime = s123High - prod3High;
        final double s123Low = prod3High - (s123High - s123Prime) + (s12High - s123Prime);
        double result = s123High + (prod1Low + prod2Low + prod3Low + s12Low + s123Low);
        if (Double.isNaN(result)) {
            result = a1 * b1 + a2 * b2 + a3 * b3;
        }
        return result;
    }
    
    public static double linearCombination(final double a1, final double b1, final double a2, final double b2, final double a3, final double b3, final double a4, final double b4) {
        final double a1High = Double.longBitsToDouble(Double.doubleToRawLongBits(a1) & 0xFFFFFFFFF8000000L);
        final double a1Low = a1 - a1High;
        final double b1High = Double.longBitsToDouble(Double.doubleToRawLongBits(b1) & 0xFFFFFFFFF8000000L);
        final double b1Low = b1 - b1High;
        final double prod1High = a1 * b1;
        final double prod1Low = a1Low * b1Low - (prod1High - a1High * b1High - a1Low * b1High - a1High * b1Low);
        final double a2High = Double.longBitsToDouble(Double.doubleToRawLongBits(a2) & 0xFFFFFFFFF8000000L);
        final double a2Low = a2 - a2High;
        final double b2High = Double.longBitsToDouble(Double.doubleToRawLongBits(b2) & 0xFFFFFFFFF8000000L);
        final double b2Low = b2 - b2High;
        final double prod2High = a2 * b2;
        final double prod2Low = a2Low * b2Low - (prod2High - a2High * b2High - a2Low * b2High - a2High * b2Low);
        final double a3High = Double.longBitsToDouble(Double.doubleToRawLongBits(a3) & 0xFFFFFFFFF8000000L);
        final double a3Low = a3 - a3High;
        final double b3High = Double.longBitsToDouble(Double.doubleToRawLongBits(b3) & 0xFFFFFFFFF8000000L);
        final double b3Low = b3 - b3High;
        final double prod3High = a3 * b3;
        final double prod3Low = a3Low * b3Low - (prod3High - a3High * b3High - a3Low * b3High - a3High * b3Low);
        final double a4High = Double.longBitsToDouble(Double.doubleToRawLongBits(a4) & 0xFFFFFFFFF8000000L);
        final double a4Low = a4 - a4High;
        final double b4High = Double.longBitsToDouble(Double.doubleToRawLongBits(b4) & 0xFFFFFFFFF8000000L);
        final double b4Low = b4 - b4High;
        final double prod4High = a4 * b4;
        final double prod4Low = a4Low * b4Low - (prod4High - a4High * b4High - a4Low * b4High - a4High * b4Low);
        final double s12High = prod1High + prod2High;
        final double s12Prime = s12High - prod2High;
        final double s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime);
        final double s123High = s12High + prod3High;
        final double s123Prime = s123High - prod3High;
        final double s123Low = prod3High - (s123High - s123Prime) + (s12High - s123Prime);
        final double s1234High = s123High + prod4High;
        final double s1234Prime = s1234High - prod4High;
        final double s1234Low = prod4High - (s1234High - s1234Prime) + (s123High - s1234Prime);
        double result = s1234High + (prod1Low + prod2Low + prod3Low + prod4Low + s12Low + s123Low + s1234Low);
        if (Double.isNaN(result)) {
            result = a1 * b1 + a2 * b2 + a3 * b3 + a4 * b4;
        }
        return result;
    }
    
    public static boolean equals(final float[] x, final float[] y) {
        if (x == null || y == null) {
            return !(x == null ^ y == null);
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!Precision.equals(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean equalsIncludingNaN(final float[] x, final float[] y) {
        if (x == null || y == null) {
            return !(x == null ^ y == null);
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!Precision.equalsIncludingNaN(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean equals(final double[] x, final double[] y) {
        if (x == null || y == null) {
            return !(x == null ^ y == null);
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!Precision.equals(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean equalsIncludingNaN(final double[] x, final double[] y) {
        if (x == null || y == null) {
            return !(x == null ^ y == null);
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!Precision.equalsIncludingNaN(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static double[] normalizeArray(final double[] values, final double normalizedSum) throws MathIllegalArgumentException, MathArithmeticException {
        if (Double.isInfinite(normalizedSum)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NORMALIZE_INFINITE, new Object[0]);
        }
        if (Double.isNaN(normalizedSum)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NORMALIZE_NAN, new Object[0]);
        }
        double sum = 0.0;
        final int len = values.length;
        final double[] out = new double[len];
        for (int i = 0; i < len; ++i) {
            if (Double.isInfinite(values[i])) {
                throw new MathIllegalArgumentException(LocalizedFormats.INFINITE_ARRAY_ELEMENT, new Object[] { values[i], i });
            }
            if (!Double.isNaN(values[i])) {
                sum += values[i];
            }
        }
        if (sum == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ARRAY_SUMS_TO_ZERO, new Object[0]);
        }
        for (int i = 0; i < len; ++i) {
            if (Double.isNaN(values[i])) {
                out[i] = Double.NaN;
            }
            else {
                out[i] = values[i] * normalizedSum / sum;
            }
        }
        return out;
    }
    
    public static <T> T[] buildArray(final Field<T> field, final int length) {
        final T[] array = (T[])Array.newInstance(field.getRuntimeClass(), length);
        Arrays.fill(array, field.getZero());
        return array;
    }
    
    public static <T> T[][] buildArray(final Field<T> field, final int rows, final int columns) {
        T[][] array;
        if (columns < 0) {
            final T[] dummyRow = buildArray(field, 0);
            array = (T[][])Array.newInstance(dummyRow.getClass(), rows);
        }
        else {
            array = (T[][])Array.newInstance(field.getRuntimeClass(), rows, columns);
            for (int i = 0; i < rows; ++i) {
                Arrays.fill(array[i], field.getZero());
            }
        }
        return array;
    }
    
    public static double[] convolve(final double[] x, final double[] h) throws NullArgumentException, NoDataException {
        MathUtils.checkNotNull(x);
        MathUtils.checkNotNull(h);
        final int xLen = x.length;
        final int hLen = h.length;
        if (xLen == 0 || hLen == 0) {
            throw new NoDataException();
        }
        final int totalLength = xLen + hLen - 1;
        final double[] y = new double[totalLength];
        for (int n = 0; n < totalLength; ++n) {
            double yn = 0.0;
            for (int k = FastMath.max(0, n + 1 - xLen), j = n - k; k < hLen && j >= 0; yn += x[j--] * h[k++]) {}
            y[n] = yn;
        }
        return y;
    }
    
    public static void shuffle(final int[] list, final int start, final Position pos) {
        shuffle(list, start, pos, new Well19937c());
    }
    
    public static void shuffle(final int[] list, final int start, final Position pos, final RandomGenerator rng) {
        switch (pos) {
            case TAIL: {
                for (int i = list.length - 1; i >= start; --i) {
                    int target;
                    if (i == start) {
                        target = start;
                    }
                    else {
                        target = new UniformIntegerDistribution(rng, start, i).sample();
                    }
                    final int temp = list[target];
                    list[target] = list[i];
                    list[i] = temp;
                }
                break;
            }
            case HEAD: {
                for (int i = 0; i <= start; ++i) {
                    int target;
                    if (i == start) {
                        target = start;
                    }
                    else {
                        target = new UniformIntegerDistribution(rng, i, start).sample();
                    }
                    final int temp = list[target];
                    list[target] = list[i];
                    list[i] = temp;
                }
                break;
            }
            default: {
                throw new MathInternalError();
            }
        }
    }
    
    public static void shuffle(final int[] list, final RandomGenerator rng) {
        shuffle(list, 0, Position.TAIL, rng);
    }
    
    public static void shuffle(final int[] list) {
        shuffle(list, new Well19937c());
    }
    
    public static int[] natural(final int n) {
        return sequence(n, 0, 1);
    }
    
    public static int[] sequence(final int size, final int start, final int stride) {
        final int[] a = new int[size];
        for (int i = 0; i < size; ++i) {
            a[i] = start + i * stride;
        }
        return a;
    }
    
    public static boolean verifyValues(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return verifyValues(values, begin, length, false);
    }
    
    public static boolean verifyValues(final double[] values, final int begin, final int length, final boolean allowEmpty) throws MathIllegalArgumentException {
        if (values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        }
        if (begin < 0) {
            throw new NotPositiveException(LocalizedFormats.START_POSITION, begin);
        }
        if (length < 0) {
            throw new NotPositiveException(LocalizedFormats.LENGTH, length);
        }
        if (begin + length > values.length) {
            throw new NumberIsTooLargeException(LocalizedFormats.SUBARRAY_ENDS_AFTER_ARRAY_END, begin + length, values.length, true);
        }
        return length != 0 || allowEmpty;
    }
    
    public static boolean verifyValues(final double[] values, final double[] weights, final int begin, final int length) throws MathIllegalArgumentException {
        return verifyValues(values, weights, begin, length, false);
    }
    
    public static boolean verifyValues(final double[] values, final double[] weights, final int begin, final int length, final boolean allowEmpty) throws MathIllegalArgumentException {
        if (weights == null || values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        }
        checkEqualLength(weights, values);
        boolean containsPositiveWeight = false;
        for (int i = begin; i < begin + length; ++i) {
            final double weight = weights[i];
            if (Double.isNaN(weight)) {
                throw new MathIllegalArgumentException(LocalizedFormats.NAN_ELEMENT_AT_INDEX, new Object[] { i });
            }
            if (Double.isInfinite(weight)) {
                throw new MathIllegalArgumentException(LocalizedFormats.INFINITE_ARRAY_ELEMENT, new Object[] { weight, i });
            }
            if (weight < 0.0) {
                throw new MathIllegalArgumentException(LocalizedFormats.NEGATIVE_ELEMENT_AT_INDEX, new Object[] { i, weight });
            }
            if (!containsPositiveWeight && weight > 0.0) {
                containsPositiveWeight = true;
            }
        }
        if (!containsPositiveWeight) {
            throw new MathIllegalArgumentException(LocalizedFormats.WEIGHT_AT_LEAST_ONE_NON_ZERO, new Object[0]);
        }
        return verifyValues(values, begin, length, allowEmpty);
    }
    
    public static double[] concatenate(final double[]... x) {
        int combinedLength = 0;
        for (final double[] a : x) {
            combinedLength += a.length;
        }
        int offset = 0;
        int curLength = 0;
        final double[] combined = new double[combinedLength];
        for (int i = 0; i < x.length; ++i) {
            curLength = x[i].length;
            System.arraycopy(x[i], 0, combined, offset, curLength);
            offset += curLength;
        }
        return combined;
    }
    
    public static double[] unique(final double[] data) {
        final TreeSet<Double> values = new TreeSet<Double>();
        for (int i = 0; i < data.length; ++i) {
            values.add(data[i]);
        }
        final int count = values.size();
        final double[] out = new double[count];
        final Iterator<Double> iterator = values.iterator();
        int j = 0;
        while (iterator.hasNext()) {
            out[count - ++j] = iterator.next();
        }
        return out;
    }
    
    public enum OrderDirection
    {
        INCREASING, 
        DECREASING;
    }
    
    private static class PairDoubleInteger
    {
        private final double key;
        private final int value;
        
        PairDoubleInteger(final double key, final int value) {
            this.key = key;
            this.value = value;
        }
        
        public double getKey() {
            return this.key;
        }
        
        public int getValue() {
            return this.value;
        }
    }
    
    public enum Position
    {
        HEAD, 
        TAIL;
    }
    
    public interface Function
    {
        double evaluate(final double[] p0);
        
        double evaluate(final double[] p0, final int p1, final int p2);
    }
}
