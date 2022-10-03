package org.apache.commons.math3.transform;

import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.util.FastMath;
import java.io.Serializable;

public class FastFourierTransformer implements Serializable
{
    static final long serialVersionUID = 20120210L;
    private static final double[] W_SUB_N_R;
    private static final double[] W_SUB_N_I;
    private final DftNormalization normalization;
    
    public FastFourierTransformer(final DftNormalization normalization) {
        this.normalization = normalization;
    }
    
    private static void bitReversalShuffle2(final double[] a, final double[] b) {
        final int n = a.length;
        assert b.length == n;
        final int halfOfN = n >> 1;
        int j = 0;
        for (int i = 0; i < n; ++i) {
            if (i < j) {
                double temp = a[i];
                a[i] = a[j];
                a[j] = temp;
                temp = b[i];
                b[i] = b[j];
                b[j] = temp;
            }
            int k;
            for (k = halfOfN; k <= j && k > 0; j -= k, k >>= 1) {}
            j += k;
        }
    }
    
    private static void normalizeTransformedData(final double[][] dataRI, final DftNormalization normalization, final TransformType type) {
        final double[] dataR = dataRI[0];
        final double[] dataI = dataRI[1];
        final int n = dataR.length;
        assert dataI.length == n;
        switch (normalization) {
            case STANDARD: {
                if (type == TransformType.INVERSE) {
                    final double scaleFactor = 1.0 / n;
                    for (int i = 0; i < n; ++i) {
                        final double[] array = dataR;
                        final int n2 = i;
                        array[n2] *= scaleFactor;
                        final double[] array2 = dataI;
                        final int n3 = i;
                        array2[n3] *= scaleFactor;
                    }
                    break;
                }
                break;
            }
            case UNITARY: {
                final double scaleFactor = 1.0 / FastMath.sqrt(n);
                for (int i = 0; i < n; ++i) {
                    final double[] array3 = dataR;
                    final int n4 = i;
                    array3[n4] *= scaleFactor;
                    final double[] array4 = dataI;
                    final int n5 = i;
                    array4[n5] *= scaleFactor;
                }
                break;
            }
            default: {
                throw new MathIllegalStateException();
            }
        }
    }
    
    public static void transformInPlace(final double[][] dataRI, final DftNormalization normalization, final TransformType type) {
        if (dataRI.length != 2) {
            throw new DimensionMismatchException(dataRI.length, 2);
        }
        final double[] dataR = dataRI[0];
        final double[] dataI = dataRI[1];
        if (dataR.length != dataI.length) {
            throw new DimensionMismatchException(dataI.length, dataR.length);
        }
        final int n = dataR.length;
        if (!ArithmeticUtils.isPowerOfTwo(n)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING, new Object[] { n });
        }
        if (n == 1) {
            return;
        }
        if (n == 2) {
            final double srcR0 = dataR[0];
            final double srcI0 = dataI[0];
            final double srcR2 = dataR[1];
            final double srcI2 = dataI[1];
            dataR[0] = srcR0 + srcR2;
            dataI[0] = srcI0 + srcI2;
            dataR[1] = srcR0 - srcR2;
            dataI[1] = srcI0 - srcI2;
            normalizeTransformedData(dataRI, normalization, type);
            return;
        }
        bitReversalShuffle2(dataR, dataI);
        if (type == TransformType.INVERSE) {
            for (int i0 = 0; i0 < n; i0 += 4) {
                final int i2 = i0 + 1;
                final int i3 = i0 + 2;
                final int i4 = i0 + 3;
                final double srcR3 = dataR[i0];
                final double srcI3 = dataI[i0];
                final double srcR4 = dataR[i3];
                final double srcI4 = dataI[i3];
                final double srcR5 = dataR[i2];
                final double srcI5 = dataI[i2];
                final double srcR6 = dataR[i4];
                final double srcI6 = dataI[i4];
                dataR[i0] = srcR3 + srcR4 + srcR5 + srcR6;
                dataI[i0] = srcI3 + srcI4 + srcI5 + srcI6;
                dataR[i2] = srcR3 - srcR5 + (srcI6 - srcI4);
                dataI[i2] = srcI3 - srcI5 + (srcR4 - srcR6);
                dataR[i3] = srcR3 - srcR4 + srcR5 - srcR6;
                dataI[i3] = srcI3 - srcI4 + srcI5 - srcI6;
                dataR[i4] = srcR3 - srcR5 + (srcI4 - srcI6);
                dataI[i4] = srcI3 - srcI5 + (srcR6 - srcR4);
            }
        }
        else {
            for (int i0 = 0; i0 < n; i0 += 4) {
                final int i2 = i0 + 1;
                final int i3 = i0 + 2;
                final int i4 = i0 + 3;
                final double srcR3 = dataR[i0];
                final double srcI3 = dataI[i0];
                final double srcR4 = dataR[i3];
                final double srcI4 = dataI[i3];
                final double srcR5 = dataR[i2];
                final double srcI5 = dataI[i2];
                final double srcR6 = dataR[i4];
                final double srcI6 = dataI[i4];
                dataR[i0] = srcR3 + srcR4 + srcR5 + srcR6;
                dataI[i0] = srcI3 + srcI4 + srcI5 + srcI6;
                dataR[i2] = srcR3 - srcR5 + (srcI4 - srcI6);
                dataI[i2] = srcI3 - srcI5 + (srcR6 - srcR4);
                dataR[i3] = srcR3 - srcR4 + srcR5 - srcR6;
                dataI[i3] = srcI3 - srcI4 + srcI5 - srcI6;
                dataR[i4] = srcR3 - srcR5 + (srcI6 - srcI4);
                dataI[i4] = srcI3 - srcI5 + (srcR4 - srcR6);
            }
        }
        int lastN0 = 4;
        int lastLogN0 = 2;
        while (lastN0 < n) {
            final int n2 = lastN0 << 1;
            final int logN0 = lastLogN0 + 1;
            final double wSubN0R = FastFourierTransformer.W_SUB_N_R[logN0];
            double wSubN0I = FastFourierTransformer.W_SUB_N_I[logN0];
            if (type == TransformType.INVERSE) {
                wSubN0I = -wSubN0I;
            }
            for (int destEvenStartIndex = 0; destEvenStartIndex < n; destEvenStartIndex += n2) {
                final int destOddStartIndex = destEvenStartIndex + lastN0;
                double wSubN0ToRR = 1.0;
                double wSubN0ToRI = 0.0;
                for (int r = 0; r < lastN0; ++r) {
                    final double grR = dataR[destEvenStartIndex + r];
                    final double grI = dataI[destEvenStartIndex + r];
                    final double hrR = dataR[destOddStartIndex + r];
                    final double hrI = dataI[destOddStartIndex + r];
                    dataR[destEvenStartIndex + r] = grR + wSubN0ToRR * hrR - wSubN0ToRI * hrI;
                    dataI[destEvenStartIndex + r] = grI + wSubN0ToRR * hrI + wSubN0ToRI * hrR;
                    dataR[destOddStartIndex + r] = grR - (wSubN0ToRR * hrR - wSubN0ToRI * hrI);
                    dataI[destOddStartIndex + r] = grI - (wSubN0ToRR * hrI + wSubN0ToRI * hrR);
                    final double nextWsubN0ToRR = wSubN0ToRR * wSubN0R - wSubN0ToRI * wSubN0I;
                    final double nextWsubN0ToRI = wSubN0ToRR * wSubN0I + wSubN0ToRI * wSubN0R;
                    wSubN0ToRR = nextWsubN0ToRR;
                    wSubN0ToRI = nextWsubN0ToRI;
                }
            }
            lastN0 = n2;
            lastLogN0 = logN0;
        }
        normalizeTransformedData(dataRI, normalization, type);
    }
    
    public Complex[] transform(final double[] f, final TransformType type) {
        final double[][] dataRI = { MathArrays.copyOf(f, f.length), new double[f.length] };
        transformInPlace(dataRI, this.normalization, type);
        return TransformUtils.createComplexArray(dataRI);
    }
    
    public Complex[] transform(final UnivariateFunction f, final double min, final double max, final int n, final TransformType type) {
        final double[] data = FunctionUtils.sample(f, min, max, n);
        return this.transform(data, type);
    }
    
    public Complex[] transform(final Complex[] f, final TransformType type) {
        final double[][] dataRI = TransformUtils.createRealImaginaryArray(f);
        transformInPlace(dataRI, this.normalization, type);
        return TransformUtils.createComplexArray(dataRI);
    }
    
    @Deprecated
    public Object mdfft(final Object mdca, final TransformType type) {
        final MultiDimensionalComplexMatrix mdcm = (MultiDimensionalComplexMatrix)new MultiDimensionalComplexMatrix(mdca).clone();
        final int[] dimensionSize = mdcm.getDimensionSizes();
        for (int i = 0; i < dimensionSize.length; ++i) {
            this.mdfft(mdcm, type, i, new int[0]);
        }
        return mdcm.getArray();
    }
    
    @Deprecated
    private void mdfft(final MultiDimensionalComplexMatrix mdcm, final TransformType type, final int d, final int[] subVector) {
        final int[] dimensionSize = mdcm.getDimensionSizes();
        if (subVector.length == dimensionSize.length) {
            Complex[] temp = new Complex[dimensionSize[d]];
            for (int i = 0; i < dimensionSize[d]; ++i) {
                temp[subVector[d] = i] = mdcm.get(subVector);
            }
            temp = this.transform(temp, type);
            for (int i = 0; i < dimensionSize[d]; ++i) {
                subVector[d] = i;
                mdcm.set(temp[i], subVector);
            }
        }
        else {
            final int[] vector = new int[subVector.length + 1];
            System.arraycopy(subVector, 0, vector, 0, subVector.length);
            if (subVector.length == d) {
                vector[d] = 0;
                this.mdfft(mdcm, type, d, vector);
            }
            else {
                for (int i = 0; i < dimensionSize[subVector.length]; ++i) {
                    vector[subVector.length] = i;
                    this.mdfft(mdcm, type, d, vector);
                }
            }
        }
    }
    
    static {
        W_SUB_N_R = new double[] { 1.0, -1.0, 6.123233995736766E-17, 0.7071067811865476, 0.9238795325112867, 0.9807852804032304, 0.9951847266721969, 0.9987954562051724, 0.9996988186962042, 0.9999247018391445, 0.9999811752826011, 0.9999952938095762, 0.9999988234517019, 0.9999997058628822, 0.9999999264657179, 0.9999999816164293, 0.9999999954041073, 0.9999999988510269, 0.9999999997127567, 0.9999999999281892, 0.9999999999820472, 0.9999999999955118, 0.999999999998878, 0.9999999999997194, 0.9999999999999298, 0.9999999999999825, 0.9999999999999957, 0.9999999999999989, 0.9999999999999998, 0.9999999999999999, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
        W_SUB_N_I = new double[] { 2.4492935982947064E-16, -1.2246467991473532E-16, -1.0, -0.7071067811865475, -0.3826834323650898, -0.19509032201612825, -0.0980171403295606, -0.049067674327418015, -0.024541228522912288, -0.012271538285719925, -0.006135884649154475, -0.003067956762965976, -0.0015339801862847655, -7.669903187427045E-4, -3.8349518757139556E-4, -1.917475973107033E-4, -9.587379909597734E-5, -4.793689960306688E-5, -2.396844980841822E-5, -1.1984224905069705E-5, -5.9921124526424275E-6, -2.996056226334661E-6, -1.4980281131690111E-6, -7.490140565847157E-7, -3.7450702829238413E-7, -1.8725351414619535E-7, -9.362675707309808E-8, -4.681337853654909E-8, -2.340668926827455E-8, -1.1703344634137277E-8, -5.8516723170686385E-9, -2.9258361585343192E-9, -1.4629180792671596E-9, -7.314590396335798E-10, -3.657295198167899E-10, -1.8286475990839495E-10, -9.143237995419748E-11, -4.571618997709874E-11, -2.285809498854937E-11, -1.1429047494274685E-11, -5.714523747137342E-12, -2.857261873568671E-12, -1.4286309367843356E-12, -7.143154683921678E-13, -3.571577341960839E-13, -1.7857886709804195E-13, -8.928943354902097E-14, -4.4644716774510487E-14, -2.2322358387255243E-14, -1.1161179193627622E-14, -5.580589596813811E-15, -2.7902947984069054E-15, -1.3951473992034527E-15, -6.975736996017264E-16, -3.487868498008632E-16, -1.743934249004316E-16, -8.71967124502158E-17, -4.35983562251079E-17, -2.179917811255395E-17, -1.0899589056276974E-17, -5.449794528138487E-18, -2.7248972640692436E-18, -1.3624486320346218E-18 };
    }
    
    @Deprecated
    private static class MultiDimensionalComplexMatrix implements Cloneable
    {
        protected int[] dimensionSize;
        protected Object multiDimensionalComplexArray;
        
        MultiDimensionalComplexMatrix(final Object multiDimensionalComplexArray) {
            this.multiDimensionalComplexArray = multiDimensionalComplexArray;
            int numOfDimensions = 0;
            Object[] array;
            for (Object lastDimension = multiDimensionalComplexArray; lastDimension instanceof Object[]; lastDimension = array[0]) {
                array = (Object[])lastDimension;
                ++numOfDimensions;
            }
            this.dimensionSize = new int[numOfDimensions];
            numOfDimensions = 0;
            for (Object lastDimension = multiDimensionalComplexArray; lastDimension instanceof Object[]; lastDimension = array[0]) {
                array = (Object[])lastDimension;
                this.dimensionSize[numOfDimensions++] = array.length;
            }
        }
        
        public Complex get(final int... vector) throws DimensionMismatchException {
            if (vector == null) {
                if (this.dimensionSize.length > 0) {
                    throw new DimensionMismatchException(0, this.dimensionSize.length);
                }
                return null;
            }
            else {
                if (vector.length != this.dimensionSize.length) {
                    throw new DimensionMismatchException(vector.length, this.dimensionSize.length);
                }
                Object lastDimension = this.multiDimensionalComplexArray;
                for (int i = 0; i < this.dimensionSize.length; ++i) {
                    lastDimension = ((Object[])lastDimension)[vector[i]];
                }
                return (Complex)lastDimension;
            }
        }
        
        public Complex set(final Complex magnitude, final int... vector) throws DimensionMismatchException {
            if (vector == null) {
                if (this.dimensionSize.length > 0) {
                    throw new DimensionMismatchException(0, this.dimensionSize.length);
                }
                return null;
            }
            else {
                if (vector.length != this.dimensionSize.length) {
                    throw new DimensionMismatchException(vector.length, this.dimensionSize.length);
                }
                Object[] lastDimension = (Object[])this.multiDimensionalComplexArray;
                for (int i = 0; i < this.dimensionSize.length - 1; ++i) {
                    lastDimension = (Object[])lastDimension[vector[i]];
                }
                final Complex lastValue = (Complex)lastDimension[vector[this.dimensionSize.length - 1]];
                lastDimension[vector[this.dimensionSize.length - 1]] = magnitude;
                return lastValue;
            }
        }
        
        public int[] getDimensionSizes() {
            return this.dimensionSize.clone();
        }
        
        public Object getArray() {
            return this.multiDimensionalComplexArray;
        }
        
        public Object clone() {
            final MultiDimensionalComplexMatrix mdcm = new MultiDimensionalComplexMatrix(Array.newInstance(Complex.class, this.dimensionSize));
            this.clone(mdcm);
            return mdcm;
        }
        
        private void clone(final MultiDimensionalComplexMatrix mdcm) {
            final int[] vector = new int[this.dimensionSize.length];
            int size = 1;
            for (int i = 0; i < this.dimensionSize.length; ++i) {
                size *= this.dimensionSize[i];
            }
            int[][] arr$;
            final int[][] vectorList = arr$ = new int[size][this.dimensionSize.length];
            for (final int[] nextVector : arr$) {
                System.arraycopy(vector, 0, nextVector, 0, this.dimensionSize.length);
                for (int j = 0; j < this.dimensionSize.length; ++j) {
                    final int[] array = vector;
                    final int n = j;
                    ++array[n];
                    if (vector[j] < this.dimensionSize[j]) {
                        break;
                    }
                    vector[j] = 0;
                }
            }
            arr$ = vectorList;
            for (final int[] nextVector : arr$) {
                mdcm.set(this.get(nextVector), nextVector);
            }
        }
    }
}
