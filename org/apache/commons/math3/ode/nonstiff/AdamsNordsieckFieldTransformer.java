package org.apache.commons.math3.ode.nonstiff;

import java.util.HashMap;
import org.apache.commons.math3.linear.FieldDecompositionSolver;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;
import org.apache.commons.math3.linear.ArrayFieldVector;
import java.util.Arrays;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.Field;
import java.util.Map;
import org.apache.commons.math3.RealFieldElement;

public class AdamsNordsieckFieldTransformer<T extends RealFieldElement<T>>
{
    private static final Map<Integer, Map<Field<? extends RealFieldElement<?>>, AdamsNordsieckFieldTransformer<? extends RealFieldElement<?>>>> CACHE;
    private final Field<T> field;
    private final Array2DRowFieldMatrix<T> update;
    private final T[] c1;
    
    private AdamsNordsieckFieldTransformer(final Field<T> field, final int n) {
        this.field = field;
        final int rows = n - 1;
        final FieldMatrix<T> bigP = this.buildP(rows);
        final FieldDecompositionSolver<T> pSolver = new FieldLUDecomposition<T>(bigP).getSolver();
        final T[] u = MathArrays.buildArray(field, rows);
        Arrays.fill(u, field.getOne());
        this.c1 = pSolver.solve(new ArrayFieldVector<T>(u, false)).toArray();
        final T[][] shiftedP = bigP.getData();
        for (int i = shiftedP.length - 1; i > 0; --i) {
            shiftedP[i] = shiftedP[i - 1];
        }
        Arrays.fill(shiftedP[0] = MathArrays.buildArray(field, rows), field.getZero());
        this.update = new Array2DRowFieldMatrix<T>(pSolver.solve(new Array2DRowFieldMatrix<T>(shiftedP, false)).getData());
    }
    
    public static <T extends RealFieldElement<T>> AdamsNordsieckFieldTransformer<T> getInstance(final Field<T> field, final int nSteps) {
        synchronized (AdamsNordsieckFieldTransformer.CACHE) {
            Map<Field<? extends RealFieldElement<?>>, AdamsNordsieckFieldTransformer<? extends RealFieldElement<?>>> map = AdamsNordsieckFieldTransformer.CACHE.get(nSteps);
            if (map == null) {
                map = new HashMap<Field<? extends RealFieldElement<?>>, AdamsNordsieckFieldTransformer<? extends RealFieldElement<?>>>();
                AdamsNordsieckFieldTransformer.CACHE.put(nSteps, map);
            }
            AdamsNordsieckFieldTransformer t = map.get(field);
            if (t == null) {
                t = new AdamsNordsieckFieldTransformer((Field<T>)field, nSteps);
                map.put(field, t);
            }
            return t;
        }
    }
    
    private FieldMatrix<T> buildP(final int rows) {
        final T[][] pData = MathArrays.buildArray(this.field, rows, rows);
        for (int i = 1; i <= pData.length; ++i) {
            final T[] pI = pData[i - 1];
            final int factor = -i;
            T aj = this.field.getZero().add(factor);
            for (int j = 1; j <= pI.length; ++j) {
                pI[j - 1] = aj.multiply(j + 1);
                aj = aj.multiply(factor);
            }
        }
        return new Array2DRowFieldMatrix<T>(pData, false);
    }
    
    public Array2DRowFieldMatrix<T> initializeHighOrderDerivatives(final T h, final T[] t, final T[][] y, final T[][] yDot) {
        final T[][] a = MathArrays.buildArray(this.field, this.c1.length + 1, this.c1.length + 1);
        final T[][] b = MathArrays.buildArray(this.field, this.c1.length + 1, y[0].length);
        final T[] y2 = y[0];
        final T[] yDot2 = yDot[0];
        for (int i = 1; i < y.length; ++i) {
            final T di = t[i].subtract(t[0]);
            final T ratio = di.divide(h);
            T dikM1Ohk = h.reciprocal();
            final T[] aI = a[2 * i - 2];
            final T[] aDotI = (T[])((2 * i - 1 < a.length) ? a[2 * i - 1] : null);
            for (int j = 0; j < aI.length; ++j) {
                dikM1Ohk = dikM1Ohk.multiply(ratio);
                aI[j] = di.multiply(dikM1Ohk);
                if (aDotI != null) {
                    aDotI[j] = dikM1Ohk.multiply(j + 2);
                }
            }
            final T[] yI = y[i];
            final T[] yDotI = yDot[i];
            final T[] bI = b[2 * i - 2];
            final T[] bDotI = (T[])((2 * i - 1 < b.length) ? b[2 * i - 1] : null);
            for (int k = 0; k < yI.length; ++k) {
                bI[k] = yI[k].subtract(y2[k]).subtract(di.multiply(yDot2[k]));
                if (bDotI != null) {
                    bDotI[k] = yDotI[k].subtract(yDot2[k]);
                }
            }
        }
        final FieldLUDecomposition<T> decomposition = new FieldLUDecomposition<T>(new Array2DRowFieldMatrix<T>(a, false));
        final FieldMatrix<T> x = decomposition.getSolver().solve(new Array2DRowFieldMatrix<T>(b, false));
        final Array2DRowFieldMatrix<T> truncatedX = new Array2DRowFieldMatrix<T>(this.field, x.getRowDimension() - 1, x.getColumnDimension());
        for (int l = 0; l < truncatedX.getRowDimension(); ++l) {
            for (int m = 0; m < truncatedX.getColumnDimension(); ++m) {
                truncatedX.setEntry(l, m, x.getEntry(l, m));
            }
        }
        return truncatedX;
    }
    
    public Array2DRowFieldMatrix<T> updateHighOrderDerivativesPhase1(final Array2DRowFieldMatrix<T> highOrder) {
        return this.update.multiply(highOrder);
    }
    
    public void updateHighOrderDerivativesPhase2(final T[] start, final T[] end, final Array2DRowFieldMatrix<T> highOrder) {
        final T[][] data = highOrder.getDataRef();
        for (int i = 0; i < data.length; ++i) {
            final T[] dataI = data[i];
            final T c1I = this.c1[i];
            for (int j = 0; j < dataI.length; ++j) {
                dataI[j] = dataI[j].add(c1I.multiply(start[j].subtract(end[j])));
            }
        }
    }
    
    static {
        CACHE = new HashMap<Integer, Map<Field<? extends RealFieldElement<?>>, AdamsNordsieckFieldTransformer<? extends RealFieldElement<?>>>>();
    }
}
