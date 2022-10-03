package org.apache.commons.math3.linear;

import org.apache.commons.math3.util.FastMath;

public class RectangularCholeskyDecomposition
{
    private final RealMatrix root;
    private int rank;
    
    public RectangularCholeskyDecomposition(final RealMatrix matrix) throws NonPositiveDefiniteMatrixException {
        this(matrix, 0.0);
    }
    
    public RectangularCholeskyDecomposition(final RealMatrix matrix, final double small) throws NonPositiveDefiniteMatrixException {
        final int order = matrix.getRowDimension();
        final double[][] c = matrix.getData();
        final double[][] b = new double[order][order];
        final int[] index = new int[order];
        for (int i = 0; i < order; ++i) {
            index[i] = i;
        }
        int r = 0;
        boolean loop = true;
        while (loop) {
            int swapR = r;
            for (int j = r + 1; j < order; ++j) {
                final int ii = index[j];
                final int isr = index[swapR];
                if (c[ii][ii] > c[isr][isr]) {
                    swapR = j;
                }
            }
            if (swapR != r) {
                final int tmpIndex = index[r];
                index[r] = index[swapR];
                index[swapR] = tmpIndex;
                final double[] tmpRow = b[r];
                b[r] = b[swapR];
                b[swapR] = tmpRow;
            }
            final int ir = index[r];
            if (c[ir][ir] <= small) {
                if (r == 0) {
                    throw new NonPositiveDefiniteMatrixException(c[ir][ir], ir, small);
                }
                for (int k = r; k < order; ++k) {
                    if (c[index[k]][index[k]] < -small) {
                        throw new NonPositiveDefiniteMatrixException(c[index[k]][index[k]], k, small);
                    }
                }
                loop = false;
            }
            else {
                final double sqrt = FastMath.sqrt(c[ir][ir]);
                b[r][r] = sqrt;
                final double inverse = 1.0 / sqrt;
                final double inverse2 = 1.0 / c[ir][ir];
                for (int l = r + 1; l < order; ++l) {
                    final int ii2 = index[l];
                    final double e = inverse * c[ii2][ir];
                    b[l][r] = e;
                    final double[] array = c[ii2];
                    final int n = ii2;
                    array[n] -= c[ii2][ir] * c[ii2][ir] * inverse2;
                    for (int m = r + 1; m < l; ++m) {
                        final int ij = index[m];
                        final double f = c[ii2][ij] - e * b[m][r];
                        c[ii2][ij] = f;
                        c[ij][ii2] = f;
                    }
                }
                loop = (++r < order);
            }
        }
        this.rank = r;
        this.root = MatrixUtils.createRealMatrix(order, r);
        for (int i2 = 0; i2 < order; ++i2) {
            for (int j2 = 0; j2 < r; ++j2) {
                this.root.setEntry(index[i2], j2, b[i2][j2]);
            }
        }
    }
    
    public RealMatrix getRootMatrix() {
        return this.root;
    }
    
    public int getRank() {
        return this.rank;
    }
}
