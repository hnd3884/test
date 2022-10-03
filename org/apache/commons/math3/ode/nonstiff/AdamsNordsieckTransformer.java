package org.apache.commons.math3.ode.nonstiff;

import java.util.HashMap;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.FieldDecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldVector;
import org.apache.commons.math3.linear.ArrayFieldVector;
import java.util.Arrays;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import java.util.Map;

public class AdamsNordsieckTransformer
{
    private static final Map<Integer, AdamsNordsieckTransformer> CACHE;
    private final Array2DRowRealMatrix update;
    private final double[] c1;
    
    private AdamsNordsieckTransformer(final int n) {
        final int rows = n - 1;
        final FieldMatrix<BigFraction> bigP = this.buildP(rows);
        final FieldDecompositionSolver<BigFraction> pSolver = new FieldLUDecomposition<BigFraction>(bigP).getSolver();
        final BigFraction[] u = new BigFraction[rows];
        Arrays.fill(u, BigFraction.ONE);
        final BigFraction[] bigC1 = pSolver.solve(new ArrayFieldVector<BigFraction>(u, false)).toArray();
        final BigFraction[][] shiftedP = bigP.getData();
        for (int i = shiftedP.length - 1; i > 0; --i) {
            shiftedP[i] = shiftedP[i - 1];
        }
        Arrays.fill(shiftedP[0] = new BigFraction[rows], BigFraction.ZERO);
        final FieldMatrix<BigFraction> bigMSupdate = pSolver.solve(new Array2DRowFieldMatrix<BigFraction>(shiftedP, false));
        this.update = MatrixUtils.bigFractionMatrixToRealMatrix(bigMSupdate);
        this.c1 = new double[rows];
        for (int j = 0; j < rows; ++j) {
            this.c1[j] = bigC1[j].doubleValue();
        }
    }
    
    public static AdamsNordsieckTransformer getInstance(final int nSteps) {
        synchronized (AdamsNordsieckTransformer.CACHE) {
            AdamsNordsieckTransformer t = AdamsNordsieckTransformer.CACHE.get(nSteps);
            if (t == null) {
                t = new AdamsNordsieckTransformer(nSteps);
                AdamsNordsieckTransformer.CACHE.put(nSteps, t);
            }
            return t;
        }
    }
    
    @Deprecated
    public int getNSteps() {
        return this.c1.length;
    }
    
    private FieldMatrix<BigFraction> buildP(final int rows) {
        final BigFraction[][] pData = new BigFraction[rows][rows];
        for (int i = 1; i <= pData.length; ++i) {
            final BigFraction[] pI = pData[i - 1];
            int aj;
            final int factor = aj = -i;
            for (int j = 1; j <= pI.length; ++j) {
                pI[j - 1] = new BigFraction(aj * (j + 1));
                aj *= factor;
            }
        }
        return new Array2DRowFieldMatrix<BigFraction>(pData, false);
    }
    
    public Array2DRowRealMatrix initializeHighOrderDerivatives(final double h, final double[] t, final double[][] y, final double[][] yDot) {
        final double[][] a = new double[this.c1.length + 1][this.c1.length + 1];
        final double[][] b = new double[this.c1.length + 1][y[0].length];
        final double[] y2 = y[0];
        final double[] yDot2 = yDot[0];
        for (int i = 1; i < y.length; ++i) {
            final double di = t[i] - t[0];
            final double ratio = di / h;
            double dikM1Ohk = 1.0 / h;
            final double[] aI = a[2 * i - 2];
            final double[] aDotI = (double[])((2 * i - 1 < a.length) ? a[2 * i - 1] : null);
            for (int j = 0; j < aI.length; ++j) {
                dikM1Ohk *= ratio;
                aI[j] = di * dikM1Ohk;
                if (aDotI != null) {
                    aDotI[j] = (j + 2) * dikM1Ohk;
                }
            }
            final double[] yI = y[i];
            final double[] yDotI = yDot[i];
            final double[] bI = b[2 * i - 2];
            final double[] bDotI = (double[])((2 * i - 1 < b.length) ? b[2 * i - 1] : null);
            for (int k = 0; k < yI.length; ++k) {
                bI[k] = yI[k] - y2[k] - di * yDot2[k];
                if (bDotI != null) {
                    bDotI[k] = yDotI[k] - yDot2[k];
                }
            }
        }
        final QRDecomposition decomposition = new QRDecomposition(new Array2DRowRealMatrix(a, false));
        final RealMatrix x = decomposition.getSolver().solve(new Array2DRowRealMatrix(b, false));
        final Array2DRowRealMatrix truncatedX = new Array2DRowRealMatrix(x.getRowDimension() - 1, x.getColumnDimension());
        for (int l = 0; l < truncatedX.getRowDimension(); ++l) {
            for (int m = 0; m < truncatedX.getColumnDimension(); ++m) {
                truncatedX.setEntry(l, m, x.getEntry(l, m));
            }
        }
        return truncatedX;
    }
    
    public Array2DRowRealMatrix updateHighOrderDerivativesPhase1(final Array2DRowRealMatrix highOrder) {
        return this.update.multiply(highOrder);
    }
    
    public void updateHighOrderDerivativesPhase2(final double[] start, final double[] end, final Array2DRowRealMatrix highOrder) {
        final double[][] data = highOrder.getDataRef();
        for (int i = 0; i < data.length; ++i) {
            final double[] dataI = data[i];
            final double c1I = this.c1[i];
            for (int j = 0; j < dataI.length; ++j) {
                final double[] array = dataI;
                final int n = j;
                array[n] += c1I * (start[j] - end[j]);
            }
        }
    }
    
    static {
        CACHE = new HashMap<Integer, AdamsNordsieckTransformer>();
    }
}
