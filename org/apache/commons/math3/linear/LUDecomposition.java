package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;

public class LUDecomposition
{
    private static final double DEFAULT_TOO_SMALL = 1.0E-11;
    private final double[][] lu;
    private final int[] pivot;
    private boolean even;
    private boolean singular;
    private RealMatrix cachedL;
    private RealMatrix cachedU;
    private RealMatrix cachedP;
    
    public LUDecomposition(final RealMatrix matrix) {
        this(matrix, 1.0E-11);
    }
    
    public LUDecomposition(final RealMatrix matrix, final double singularityThreshold) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        final int m = matrix.getColumnDimension();
        this.lu = matrix.getData();
        this.pivot = new int[m];
        this.cachedL = null;
        this.cachedU = null;
        this.cachedP = null;
        for (int row = 0; row < m; ++row) {
            this.pivot[row] = row;
        }
        this.even = true;
        this.singular = false;
        for (int col = 0; col < m; ++col) {
            for (int row2 = 0; row2 < col; ++row2) {
                final double[] luRow = this.lu[row2];
                double sum = luRow[col];
                for (int i = 0; i < row2; ++i) {
                    sum -= luRow[i] * this.lu[i][col];
                }
                luRow[col] = sum;
            }
            int max = col;
            double largest = Double.NEGATIVE_INFINITY;
            for (int row3 = col; row3 < m; ++row3) {
                final double[] luRow2 = this.lu[row3];
                double sum2 = luRow2[col];
                for (int j = 0; j < col; ++j) {
                    sum2 -= luRow2[j] * this.lu[j][col];
                }
                luRow2[col] = sum2;
                if (FastMath.abs(sum2) > largest) {
                    largest = FastMath.abs(sum2);
                    max = row3;
                }
            }
            if (FastMath.abs(this.lu[max][col]) < singularityThreshold) {
                this.singular = true;
                return;
            }
            if (max != col) {
                double tmp = 0.0;
                final double[] luMax = this.lu[max];
                final double[] luCol = this.lu[col];
                for (int j = 0; j < m; ++j) {
                    tmp = luMax[j];
                    luMax[j] = luCol[j];
                    luCol[j] = tmp;
                }
                final int temp = this.pivot[max];
                this.pivot[max] = this.pivot[col];
                this.pivot[col] = temp;
                this.even = !this.even;
            }
            final double luDiag = this.lu[col][col];
            for (int row4 = col + 1; row4 < m; ++row4) {
                final double[] array = this.lu[row4];
                final int n = col;
                array[n] /= luDiag;
            }
        }
    }
    
    public RealMatrix getL() {
        if (this.cachedL == null && !this.singular) {
            final int m = this.pivot.length;
            this.cachedL = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; ++i) {
                final double[] luI = this.lu[i];
                for (int j = 0; j < i; ++j) {
                    this.cachedL.setEntry(i, j, luI[j]);
                }
                this.cachedL.setEntry(i, i, 1.0);
            }
        }
        return this.cachedL;
    }
    
    public RealMatrix getU() {
        if (this.cachedU == null && !this.singular) {
            final int m = this.pivot.length;
            this.cachedU = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; ++i) {
                final double[] luI = this.lu[i];
                for (int j = i; j < m; ++j) {
                    this.cachedU.setEntry(i, j, luI[j]);
                }
            }
        }
        return this.cachedU;
    }
    
    public RealMatrix getP() {
        if (this.cachedP == null && !this.singular) {
            final int m = this.pivot.length;
            this.cachedP = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; ++i) {
                this.cachedP.setEntry(i, this.pivot[i], 1.0);
            }
        }
        return this.cachedP;
    }
    
    public int[] getPivot() {
        return this.pivot.clone();
    }
    
    public double getDeterminant() {
        if (this.singular) {
            return 0.0;
        }
        final int m = this.pivot.length;
        double determinant = this.even ? 1.0 : -1.0;
        for (int i = 0; i < m; ++i) {
            determinant *= this.lu[i][i];
        }
        return determinant;
    }
    
    public DecompositionSolver getSolver() {
        return new Solver(this.lu, this.pivot, this.singular);
    }
    
    private static class Solver implements DecompositionSolver
    {
        private final double[][] lu;
        private final int[] pivot;
        private final boolean singular;
        
        private Solver(final double[][] lu, final int[] pivot, final boolean singular) {
            this.lu = lu;
            this.pivot = pivot;
            this.singular = singular;
        }
        
        public boolean isNonSingular() {
            return !this.singular;
        }
        
        public RealVector solve(final RealVector b) {
            final int m = this.pivot.length;
            if (b.getDimension() != m) {
                throw new DimensionMismatchException(b.getDimension(), m);
            }
            if (this.singular) {
                throw new SingularMatrixException();
            }
            final double[] bp = new double[m];
            for (int row = 0; row < m; ++row) {
                bp[row] = b.getEntry(this.pivot[row]);
            }
            for (int col = 0; col < m; ++col) {
                final double bpCol = bp[col];
                for (int i = col + 1; i < m; ++i) {
                    final double[] array = bp;
                    final int n = i;
                    array[n] -= bpCol * this.lu[i][col];
                }
            }
            for (int col = m - 1; col >= 0; --col) {
                final double[] array2 = bp;
                final int n2 = col;
                array2[n2] /= this.lu[col][col];
                final double bpCol = bp[col];
                for (int i = 0; i < col; ++i) {
                    final double[] array3 = bp;
                    final int n3 = i;
                    array3[n3] -= bpCol * this.lu[i][col];
                }
            }
            return new ArrayRealVector(bp, false);
        }
        
        public RealMatrix solve(final RealMatrix b) {
            final int m = this.pivot.length;
            if (b.getRowDimension() != m) {
                throw new DimensionMismatchException(b.getRowDimension(), m);
            }
            if (this.singular) {
                throw new SingularMatrixException();
            }
            final int nColB = b.getColumnDimension();
            final double[][] bp = new double[m][nColB];
            for (int row = 0; row < m; ++row) {
                final double[] bpRow = bp[row];
                final int pRow = this.pivot[row];
                for (int col = 0; col < nColB; ++col) {
                    bpRow[col] = b.getEntry(pRow, col);
                }
            }
            for (int col2 = 0; col2 < m; ++col2) {
                final double[] bpCol = bp[col2];
                for (int i = col2 + 1; i < m; ++i) {
                    final double[] bpI = bp[i];
                    final double luICol = this.lu[i][col2];
                    for (int j = 0; j < nColB; ++j) {
                        final double[] array = bpI;
                        final int n = j;
                        array[n] -= bpCol[j] * luICol;
                    }
                }
            }
            for (int col2 = m - 1; col2 >= 0; --col2) {
                final double[] bpCol = bp[col2];
                final double luDiag = this.lu[col2][col2];
                for (int k = 0; k < nColB; ++k) {
                    final double[] array2 = bpCol;
                    final int n2 = k;
                    array2[n2] /= luDiag;
                }
                for (int l = 0; l < col2; ++l) {
                    final double[] bpI2 = bp[l];
                    final double luICol2 = this.lu[l][col2];
                    for (int j2 = 0; j2 < nColB; ++j2) {
                        final double[] array3 = bpI2;
                        final int n3 = j2;
                        array3[n3] -= bpCol[j2] * luICol2;
                    }
                }
            }
            return new Array2DRowRealMatrix(bp, false);
        }
        
        public RealMatrix getInverse() {
            return this.solve(MatrixUtils.createRealIdentityMatrix(this.pivot.length));
        }
    }
}
