package org.apache.commons.math3.linear;

import java.util.Arrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;

public class QRDecomposition
{
    private double[][] qrt;
    private double[] rDiag;
    private RealMatrix cachedQ;
    private RealMatrix cachedQT;
    private RealMatrix cachedR;
    private RealMatrix cachedH;
    private final double threshold;
    
    public QRDecomposition(final RealMatrix matrix) {
        this(matrix, 0.0);
    }
    
    public QRDecomposition(final RealMatrix matrix, final double threshold) {
        this.threshold = threshold;
        final int m = matrix.getRowDimension();
        final int n = matrix.getColumnDimension();
        this.qrt = matrix.transpose().getData();
        this.rDiag = new double[FastMath.min(m, n)];
        this.cachedQ = null;
        this.cachedQT = null;
        this.cachedR = null;
        this.cachedH = null;
        this.decompose(this.qrt);
    }
    
    protected void decompose(final double[][] matrix) {
        for (int minor = 0; minor < FastMath.min(matrix.length, matrix[0].length); ++minor) {
            this.performHouseholderReflection(minor, matrix);
        }
    }
    
    protected void performHouseholderReflection(final int minor, final double[][] matrix) {
        final double[] qrtMinor = matrix[minor];
        double xNormSqr = 0.0;
        for (int row = minor; row < qrtMinor.length; ++row) {
            final double c = qrtMinor[row];
            xNormSqr += c * c;
        }
        final double a = (qrtMinor[minor] > 0.0) ? (-FastMath.sqrt(xNormSqr)) : FastMath.sqrt(xNormSqr);
        this.rDiag[minor] = a;
        if (a != 0.0) {
            final double[] array = qrtMinor;
            array[minor] -= a;
            for (int col = minor + 1; col < matrix.length; ++col) {
                final double[] qrtCol = matrix[col];
                double alpha = 0.0;
                for (int row2 = minor; row2 < qrtCol.length; ++row2) {
                    alpha -= qrtCol[row2] * qrtMinor[row2];
                }
                alpha /= a * qrtMinor[minor];
                for (int row2 = minor; row2 < qrtCol.length; ++row2) {
                    final double[] array2 = qrtCol;
                    final int n = row2;
                    array2[n] -= alpha * qrtMinor[row2];
                }
            }
        }
    }
    
    public RealMatrix getR() {
        if (this.cachedR == null) {
            final int n = this.qrt.length;
            final int m = this.qrt[0].length;
            final double[][] ra = new double[m][n];
            for (int row = FastMath.min(m, n) - 1; row >= 0; --row) {
                ra[row][row] = this.rDiag[row];
                for (int col = row + 1; col < n; ++col) {
                    ra[row][col] = this.qrt[col][row];
                }
            }
            this.cachedR = MatrixUtils.createRealMatrix(ra);
        }
        return this.cachedR;
    }
    
    public RealMatrix getQ() {
        if (this.cachedQ == null) {
            this.cachedQ = this.getQT().transpose();
        }
        return this.cachedQ;
    }
    
    public RealMatrix getQT() {
        if (this.cachedQT == null) {
            final int n = this.qrt.length;
            final int m = this.qrt[0].length;
            final double[][] qta = new double[m][m];
            for (int minor = m - 1; minor >= FastMath.min(m, n); --minor) {
                qta[minor][minor] = 1.0;
            }
            for (int minor = FastMath.min(m, n) - 1; minor >= 0; --minor) {
                final double[] qrtMinor = this.qrt[minor];
                qta[minor][minor] = 1.0;
                if (qrtMinor[minor] != 0.0) {
                    for (int col = minor; col < m; ++col) {
                        double alpha = 0.0;
                        for (int row = minor; row < m; ++row) {
                            alpha -= qta[col][row] * qrtMinor[row];
                        }
                        alpha /= this.rDiag[minor] * qrtMinor[minor];
                        for (int row = minor; row < m; ++row) {
                            final double[] array = qta[col];
                            final int n2 = row;
                            array[n2] += -alpha * qrtMinor[row];
                        }
                    }
                }
            }
            this.cachedQT = MatrixUtils.createRealMatrix(qta);
        }
        return this.cachedQT;
    }
    
    public RealMatrix getH() {
        if (this.cachedH == null) {
            final int n = this.qrt.length;
            final int m = this.qrt[0].length;
            final double[][] ha = new double[m][n];
            for (int i = 0; i < m; ++i) {
                for (int j = 0; j < FastMath.min(i + 1, n); ++j) {
                    ha[i][j] = this.qrt[j][i] / -this.rDiag[j];
                }
            }
            this.cachedH = MatrixUtils.createRealMatrix(ha);
        }
        return this.cachedH;
    }
    
    public DecompositionSolver getSolver() {
        return new Solver(this.qrt, this.rDiag, this.threshold);
    }
    
    private static class Solver implements DecompositionSolver
    {
        private final double[][] qrt;
        private final double[] rDiag;
        private final double threshold;
        
        private Solver(final double[][] qrt, final double[] rDiag, final double threshold) {
            this.qrt = qrt;
            this.rDiag = rDiag;
            this.threshold = threshold;
        }
        
        public boolean isNonSingular() {
            for (final double diag : this.rDiag) {
                if (FastMath.abs(diag) <= this.threshold) {
                    return false;
                }
            }
            return true;
        }
        
        public RealVector solve(final RealVector b) {
            final int n = this.qrt.length;
            final int m = this.qrt[0].length;
            if (b.getDimension() != m) {
                throw new DimensionMismatchException(b.getDimension(), m);
            }
            if (!this.isNonSingular()) {
                throw new SingularMatrixException();
            }
            final double[] x = new double[n];
            final double[] y = b.toArray();
            for (int minor = 0; minor < FastMath.min(m, n); ++minor) {
                final double[] qrtMinor = this.qrt[minor];
                double dotProduct = 0.0;
                for (int row = minor; row < m; ++row) {
                    dotProduct += y[row] * qrtMinor[row];
                }
                dotProduct /= this.rDiag[minor] * qrtMinor[minor];
                for (int row = minor; row < m; ++row) {
                    final double[] array = y;
                    final int n2 = row;
                    array[n2] += dotProduct * qrtMinor[row];
                }
            }
            for (int row2 = this.rDiag.length - 1; row2 >= 0; --row2) {
                final double[] array2 = y;
                final int n3 = row2;
                array2[n3] /= this.rDiag[row2];
                final double yRow = y[row2];
                final double[] qrtRow = this.qrt[row2];
                x[row2] = yRow;
                for (int i = 0; i < row2; ++i) {
                    final double[] array3 = y;
                    final int n4 = i;
                    array3[n4] -= yRow * qrtRow[i];
                }
            }
            return new ArrayRealVector(x, false);
        }
        
        public RealMatrix solve(final RealMatrix b) {
            final int n = this.qrt.length;
            final int m = this.qrt[0].length;
            if (b.getRowDimension() != m) {
                throw new DimensionMismatchException(b.getRowDimension(), m);
            }
            if (!this.isNonSingular()) {
                throw new SingularMatrixException();
            }
            final int columns = b.getColumnDimension();
            final int blockSize = 52;
            final int cBlocks = (columns + 52 - 1) / 52;
            final double[][] xBlocks = BlockRealMatrix.createBlocksLayout(n, columns);
            final double[][] y = new double[b.getRowDimension()][52];
            final double[] alpha = new double[52];
            for (int kBlock = 0; kBlock < cBlocks; ++kBlock) {
                final int kStart = kBlock * 52;
                final int kEnd = FastMath.min(kStart + 52, columns);
                final int kWidth = kEnd - kStart;
                b.copySubMatrix(0, m - 1, kStart, kEnd - 1, y);
                for (int minor = 0; minor < FastMath.min(m, n); ++minor) {
                    final double[] qrtMinor = this.qrt[minor];
                    final double factor = 1.0 / (this.rDiag[minor] * qrtMinor[minor]);
                    Arrays.fill(alpha, 0, kWidth, 0.0);
                    for (int row = minor; row < m; ++row) {
                        final double d = qrtMinor[row];
                        final double[] yRow = y[row];
                        for (int k = 0; k < kWidth; ++k) {
                            final double[] array = alpha;
                            final int n2 = k;
                            array[n2] += d * yRow[k];
                        }
                    }
                    for (int i = 0; i < kWidth; ++i) {
                        final double[] array2 = alpha;
                        final int n3 = i;
                        array2[n3] *= factor;
                    }
                    for (int row = minor; row < m; ++row) {
                        final double d = qrtMinor[row];
                        final double[] yRow = y[row];
                        for (int k = 0; k < kWidth; ++k) {
                            final double[] array3 = yRow;
                            final int n4 = k;
                            array3[n4] += alpha[k] * d;
                        }
                    }
                }
                for (int j = this.rDiag.length - 1; j >= 0; --j) {
                    final int jBlock = j / 52;
                    final int jStart = jBlock * 52;
                    final double factor2 = 1.0 / this.rDiag[j];
                    final double[] yJ = y[j];
                    final double[] xBlock = xBlocks[jBlock * cBlocks + kBlock];
                    int index = (j - jStart) * kWidth;
                    for (int k = 0; k < kWidth; ++k) {
                        final double[] array4 = yJ;
                        final int n5 = k;
                        array4[n5] *= factor2;
                        xBlock[index++] = yJ[k];
                    }
                    final double[] qrtJ = this.qrt[j];
                    for (int l = 0; l < j; ++l) {
                        final double rIJ = qrtJ[l];
                        final double[] yI = y[l];
                        for (int k2 = 0; k2 < kWidth; ++k2) {
                            final double[] array5 = yI;
                            final int n6 = k2;
                            array5[n6] -= yJ[k2] * rIJ;
                        }
                    }
                }
            }
            return new BlockRealMatrix(n, columns, xBlocks, false);
        }
        
        public RealMatrix getInverse() {
            return this.solve(MatrixUtils.createRealIdentityMatrix(this.qrt[0].length));
        }
    }
}
