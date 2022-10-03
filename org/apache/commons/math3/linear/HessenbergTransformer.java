package org.apache.commons.math3.linear;

import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.FastMath;

class HessenbergTransformer
{
    private final double[][] householderVectors;
    private final double[] ort;
    private RealMatrix cachedP;
    private RealMatrix cachedPt;
    private RealMatrix cachedH;
    
    HessenbergTransformer(final RealMatrix matrix) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        final int m = matrix.getRowDimension();
        this.householderVectors = matrix.getData();
        this.ort = new double[m];
        this.cachedP = null;
        this.cachedPt = null;
        this.cachedH = null;
        this.transform();
    }
    
    public RealMatrix getP() {
        if (this.cachedP == null) {
            final int n = this.householderVectors.length;
            final int high = n - 1;
            final double[][] pa = new double[n][n];
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    pa[i][j] = ((i == j) ? 1.0 : 0.0);
                }
            }
            for (int m = high - 1; m >= 1; --m) {
                if (this.householderVectors[m][m - 1] != 0.0) {
                    for (int k = m + 1; k <= high; ++k) {
                        this.ort[k] = this.householderVectors[k][m - 1];
                    }
                    for (int j = m; j <= high; ++j) {
                        double g = 0.0;
                        for (int l = m; l <= high; ++l) {
                            g += this.ort[l] * pa[l][j];
                        }
                        g = g / this.ort[m] / this.householderVectors[m][m - 1];
                        for (int l = m; l <= high; ++l) {
                            final double[] array = pa[l];
                            final int n2 = j;
                            array[n2] += g * this.ort[l];
                        }
                    }
                }
            }
            this.cachedP = MatrixUtils.createRealMatrix(pa);
        }
        return this.cachedP;
    }
    
    public RealMatrix getPT() {
        if (this.cachedPt == null) {
            this.cachedPt = this.getP().transpose();
        }
        return this.cachedPt;
    }
    
    public RealMatrix getH() {
        if (this.cachedH == null) {
            final int m = this.householderVectors.length;
            final double[][] h = new double[m][m];
            for (int i = 0; i < m; ++i) {
                if (i > 0) {
                    h[i][i - 1] = this.householderVectors[i][i - 1];
                }
                for (int j = i; j < m; ++j) {
                    h[i][j] = this.householderVectors[i][j];
                }
            }
            this.cachedH = MatrixUtils.createRealMatrix(h);
        }
        return this.cachedH;
    }
    
    double[][] getHouseholderVectorsRef() {
        return this.householderVectors;
    }
    
    private void transform() {
        final int n = this.householderVectors.length;
        for (int high = n - 1, m = 1; m <= high - 1; ++m) {
            double scale = 0.0;
            for (int i = m; i <= high; ++i) {
                scale += FastMath.abs(this.householderVectors[i][m - 1]);
            }
            if (!Precision.equals(scale, 0.0)) {
                double h = 0.0;
                for (int j = high; j >= m; --j) {
                    this.ort[j] = this.householderVectors[j][m - 1] / scale;
                    h += this.ort[j] * this.ort[j];
                }
                final double g = (this.ort[m] > 0.0) ? (-FastMath.sqrt(h)) : FastMath.sqrt(h);
                h -= this.ort[m] * g;
                final double[] ort = this.ort;
                final int n2 = m;
                ort[n2] -= g;
                for (int k = m; k < n; ++k) {
                    double f = 0.0;
                    for (int l = high; l >= m; --l) {
                        f += this.ort[l] * this.householderVectors[l][k];
                    }
                    f /= h;
                    for (int l = m; l <= high; ++l) {
                        final double[] array = this.householderVectors[l];
                        final int n3 = k;
                        array[n3] -= f * this.ort[l];
                    }
                }
                for (int i2 = 0; i2 <= high; ++i2) {
                    double f = 0.0;
                    for (int j2 = high; j2 >= m; --j2) {
                        f += this.ort[j2] * this.householderVectors[i2][j2];
                    }
                    f /= h;
                    for (int j2 = m; j2 <= high; ++j2) {
                        final double[] array2 = this.householderVectors[i2];
                        final int n4 = j2;
                        array2[n4] -= f * this.ort[j2];
                    }
                }
                this.ort[m] *= scale;
                this.householderVectors[m][m - 1] = scale * g;
            }
        }
    }
}
