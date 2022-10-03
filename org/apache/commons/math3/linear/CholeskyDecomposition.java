package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;

public class CholeskyDecomposition
{
    public static final double DEFAULT_RELATIVE_SYMMETRY_THRESHOLD = 1.0E-15;
    public static final double DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD = 1.0E-10;
    private double[][] lTData;
    private RealMatrix cachedL;
    private RealMatrix cachedLT;
    
    public CholeskyDecomposition(final RealMatrix matrix) {
        this(matrix, 1.0E-15, 1.0E-10);
    }
    
    public CholeskyDecomposition(final RealMatrix matrix, final double relativeSymmetryThreshold, final double absolutePositivityThreshold) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        final int order = matrix.getRowDimension();
        this.lTData = matrix.getData();
        this.cachedL = null;
        this.cachedLT = null;
        for (int i = 0; i < order; ++i) {
            final double[] lI = this.lTData[i];
            for (int j = i + 1; j < order; ++j) {
                final double[] lJ = this.lTData[j];
                final double lIJ = lI[j];
                final double lJI = lJ[i];
                final double maxDelta = relativeSymmetryThreshold * FastMath.max(FastMath.abs(lIJ), FastMath.abs(lJI));
                if (FastMath.abs(lIJ - lJI) > maxDelta) {
                    throw new NonSymmetricMatrixException(i, j, relativeSymmetryThreshold);
                }
                lJ[i] = 0.0;
            }
        }
        for (int i = 0; i < order; ++i) {
            final double[] ltI = this.lTData[i];
            if (ltI[i] <= absolutePositivityThreshold) {
                throw new NonPositiveDefiniteMatrixException(ltI[i], i, absolutePositivityThreshold);
            }
            ltI[i] = FastMath.sqrt(ltI[i]);
            final double inverse = 1.0 / ltI[i];
            for (int q = order - 1; q > i; --q) {
                final double[] array = ltI;
                final int n = q;
                array[n] *= inverse;
                final double[] ltQ = this.lTData[q];
                for (int p = q; p < order; ++p) {
                    final double[] array2 = ltQ;
                    final int n2 = p;
                    array2[n2] -= ltI[q] * ltI[p];
                }
            }
        }
    }
    
    public RealMatrix getL() {
        if (this.cachedL == null) {
            this.cachedL = this.getLT().transpose();
        }
        return this.cachedL;
    }
    
    public RealMatrix getLT() {
        if (this.cachedLT == null) {
            this.cachedLT = MatrixUtils.createRealMatrix(this.lTData);
        }
        return this.cachedLT;
    }
    
    public double getDeterminant() {
        double determinant = 1.0;
        for (int i = 0; i < this.lTData.length; ++i) {
            final double lTii = this.lTData[i][i];
            determinant *= lTii * lTii;
        }
        return determinant;
    }
    
    public DecompositionSolver getSolver() {
        return new Solver(this.lTData);
    }
    
    private static class Solver implements DecompositionSolver
    {
        private final double[][] lTData;
        
        private Solver(final double[][] lTData) {
            this.lTData = lTData;
        }
        
        public boolean isNonSingular() {
            return true;
        }
        
        public RealVector solve(final RealVector b) {
            final int m = this.lTData.length;
            if (b.getDimension() != m) {
                throw new DimensionMismatchException(b.getDimension(), m);
            }
            final double[] x = b.toArray();
            for (int j = 0; j < m; ++j) {
                final double[] lJ = this.lTData[j];
                final double[] array = x;
                final int n = j;
                array[n] /= lJ[j];
                final double xJ = x[j];
                for (int i = j + 1; i < m; ++i) {
                    final double[] array2 = x;
                    final int n2 = i;
                    array2[n2] -= xJ * lJ[i];
                }
            }
            for (int j = m - 1; j >= 0; --j) {
                final double[] array3 = x;
                final int n3 = j;
                array3[n3] /= this.lTData[j][j];
                final double xJ2 = x[j];
                for (int k = 0; k < j; ++k) {
                    final double[] array4 = x;
                    final int n4 = k;
                    array4[n4] -= xJ2 * this.lTData[k][j];
                }
            }
            return new ArrayRealVector(x, false);
        }
        
        public RealMatrix solve(final RealMatrix b) {
            final int m = this.lTData.length;
            if (b.getRowDimension() != m) {
                throw new DimensionMismatchException(b.getRowDimension(), m);
            }
            final int nColB = b.getColumnDimension();
            final double[][] x = b.getData();
            for (int j = 0; j < m; ++j) {
                final double[] lJ = this.lTData[j];
                final double lJJ = lJ[j];
                final double[] xJ = x[j];
                for (int k = 0; k < nColB; ++k) {
                    final double[] array = xJ;
                    final int n = k;
                    array[n] /= lJJ;
                }
                for (int i = j + 1; i < m; ++i) {
                    final double[] xI = x[i];
                    final double lJI = lJ[i];
                    for (int l = 0; l < nColB; ++l) {
                        final double[] array2 = xI;
                        final int n2 = l;
                        array2[n2] -= xJ[l] * lJI;
                    }
                }
            }
            for (int j = m - 1; j >= 0; --j) {
                final double lJJ2 = this.lTData[j][j];
                final double[] xJ2 = x[j];
                for (int k2 = 0; k2 < nColB; ++k2) {
                    final double[] array3 = xJ2;
                    final int n3 = k2;
                    array3[n3] /= lJJ2;
                }
                for (int i2 = 0; i2 < j; ++i2) {
                    final double[] xI2 = x[i2];
                    final double lIJ = this.lTData[i2][j];
                    for (int k3 = 0; k3 < nColB; ++k3) {
                        final double[] array4 = xI2;
                        final int n4 = k3;
                        array4[n4] -= xJ2[k3] * lIJ;
                    }
                }
            }
            return new Array2DRowRealMatrix(x);
        }
        
        public RealMatrix getInverse() {
            return this.solve(MatrixUtils.createRealIdentityMatrix(this.lTData.length));
        }
    }
}
