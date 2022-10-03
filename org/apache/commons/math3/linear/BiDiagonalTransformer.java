package org.apache.commons.math3.linear;

import org.apache.commons.math3.util.FastMath;

class BiDiagonalTransformer
{
    private final double[][] householderVectors;
    private final double[] main;
    private final double[] secondary;
    private RealMatrix cachedU;
    private RealMatrix cachedB;
    private RealMatrix cachedV;
    
    BiDiagonalTransformer(final RealMatrix matrix) {
        final int m = matrix.getRowDimension();
        final int n = matrix.getColumnDimension();
        final int p = FastMath.min(m, n);
        this.householderVectors = matrix.getData();
        this.main = new double[p];
        this.secondary = new double[p - 1];
        this.cachedU = null;
        this.cachedB = null;
        this.cachedV = null;
        if (m >= n) {
            this.transformToUpperBiDiagonal();
        }
        else {
            this.transformToLowerBiDiagonal();
        }
    }
    
    public RealMatrix getU() {
        if (this.cachedU == null) {
            final int m = this.householderVectors.length;
            final int n = this.householderVectors[0].length;
            final int p = this.main.length;
            final int diagOffset = (m < n) ? 1 : 0;
            final double[] diagonal = (m >= n) ? this.main : this.secondary;
            final double[][] ua = new double[m][m];
            for (int k = m - 1; k >= p; --k) {
                ua[k][k] = 1.0;
            }
            for (int k = p - 1; k >= diagOffset; --k) {
                final double[] hK = this.householderVectors[k];
                ua[k][k] = 1.0;
                if (hK[k - diagOffset] != 0.0) {
                    for (int j = k; j < m; ++j) {
                        double alpha = 0.0;
                        for (int i = k; i < m; ++i) {
                            alpha -= ua[i][j] * this.householderVectors[i][k - diagOffset];
                        }
                        alpha /= diagonal[k - diagOffset] * hK[k - diagOffset];
                        for (int i = k; i < m; ++i) {
                            final double[] array = ua[i];
                            final int n2 = j;
                            array[n2] += -alpha * this.householderVectors[i][k - diagOffset];
                        }
                    }
                }
            }
            if (diagOffset > 0) {
                ua[0][0] = 1.0;
            }
            this.cachedU = MatrixUtils.createRealMatrix(ua);
        }
        return this.cachedU;
    }
    
    public RealMatrix getB() {
        if (this.cachedB == null) {
            final int m = this.householderVectors.length;
            final int n = this.householderVectors[0].length;
            final double[][] ba = new double[m][n];
            for (int i = 0; i < this.main.length; ++i) {
                ba[i][i] = this.main[i];
                if (m < n) {
                    if (i > 0) {
                        ba[i][i - 1] = this.secondary[i - 1];
                    }
                }
                else if (i < this.main.length - 1) {
                    ba[i][i + 1] = this.secondary[i];
                }
            }
            this.cachedB = MatrixUtils.createRealMatrix(ba);
        }
        return this.cachedB;
    }
    
    public RealMatrix getV() {
        if (this.cachedV == null) {
            final int m = this.householderVectors.length;
            final int n = this.householderVectors[0].length;
            final int p = this.main.length;
            final int diagOffset = (m >= n) ? 1 : 0;
            final double[] diagonal = (m >= n) ? this.secondary : this.main;
            final double[][] va = new double[n][n];
            for (int k = n - 1; k >= p; --k) {
                va[k][k] = 1.0;
            }
            for (int k = p - 1; k >= diagOffset; --k) {
                final double[] hK = this.householderVectors[k - diagOffset];
                va[k][k] = 1.0;
                if (hK[k] != 0.0) {
                    for (int j = k; j < n; ++j) {
                        double beta = 0.0;
                        for (int i = k; i < n; ++i) {
                            beta -= va[i][j] * hK[i];
                        }
                        beta /= diagonal[k - diagOffset] * hK[k];
                        for (int i = k; i < n; ++i) {
                            final double[] array = va[i];
                            final int n2 = j;
                            array[n2] += -beta * hK[i];
                        }
                    }
                }
            }
            if (diagOffset > 0) {
                va[0][0] = 1.0;
            }
            this.cachedV = MatrixUtils.createRealMatrix(va);
        }
        return this.cachedV;
    }
    
    double[][] getHouseholderVectorsRef() {
        return this.householderVectors;
    }
    
    double[] getMainDiagonalRef() {
        return this.main;
    }
    
    double[] getSecondaryDiagonalRef() {
        return this.secondary;
    }
    
    boolean isUpperBiDiagonal() {
        return this.householderVectors.length >= this.householderVectors[0].length;
    }
    
    private void transformToUpperBiDiagonal() {
        final int m = this.householderVectors.length;
        for (int n = this.householderVectors[0].length, k = 0; k < n; ++k) {
            double xNormSqr = 0.0;
            for (int i = k; i < m; ++i) {
                final double c = this.householderVectors[i][k];
                xNormSqr += c * c;
            }
            final double[] hK = this.householderVectors[k];
            final double a = (hK[k] > 0.0) ? (-FastMath.sqrt(xNormSqr)) : FastMath.sqrt(xNormSqr);
            this.main[k] = a;
            if (a != 0.0) {
                final double[] array = hK;
                final int n2 = k;
                array[n2] -= a;
                for (int j = k + 1; j < n; ++j) {
                    double alpha = 0.0;
                    for (int l = k; l < m; ++l) {
                        final double[] hI = this.householderVectors[l];
                        alpha -= hI[j] * hI[k];
                    }
                    alpha /= a * this.householderVectors[k][k];
                    for (int l = k; l < m; ++l) {
                        final double[] array2;
                        final double[] hI = array2 = this.householderVectors[l];
                        final int n3 = j;
                        array2[n3] -= alpha * hI[k];
                    }
                }
            }
            if (k < n - 1) {
                xNormSqr = 0.0;
                for (int j = k + 1; j < n; ++j) {
                    final double c2 = hK[j];
                    xNormSqr += c2 * c2;
                }
                final double b = (hK[k + 1] > 0.0) ? (-FastMath.sqrt(xNormSqr)) : FastMath.sqrt(xNormSqr);
                this.secondary[k] = b;
                if (b != 0.0) {
                    final double[] array3 = hK;
                    final int n4 = k + 1;
                    array3[n4] -= b;
                    for (int i2 = k + 1; i2 < m; ++i2) {
                        final double[] hI2 = this.householderVectors[i2];
                        double beta = 0.0;
                        for (int j2 = k + 1; j2 < n; ++j2) {
                            beta -= hI2[j2] * hK[j2];
                        }
                        beta /= b * hK[k + 1];
                        for (int j2 = k + 1; j2 < n; ++j2) {
                            final double[] array4 = hI2;
                            final int n5 = j2;
                            array4[n5] -= beta * hK[j2];
                        }
                    }
                }
            }
        }
    }
    
    private void transformToLowerBiDiagonal() {
        final int m = this.householderVectors.length;
        final int n = this.householderVectors[0].length;
        for (int k = 0; k < m; ++k) {
            final double[] hK = this.householderVectors[k];
            double xNormSqr = 0.0;
            for (int j = k; j < n; ++j) {
                final double c = hK[j];
                xNormSqr += c * c;
            }
            final double a = (hK[k] > 0.0) ? (-FastMath.sqrt(xNormSqr)) : FastMath.sqrt(xNormSqr);
            this.main[k] = a;
            if (a != 0.0) {
                final double[] array = hK;
                final int n2 = k;
                array[n2] -= a;
                for (int i = k + 1; i < m; ++i) {
                    final double[] hI = this.householderVectors[i];
                    double alpha = 0.0;
                    for (int l = k; l < n; ++l) {
                        alpha -= hI[l] * hK[l];
                    }
                    alpha /= a * this.householderVectors[k][k];
                    for (int l = k; l < n; ++l) {
                        final double[] array2 = hI;
                        final int n3 = l;
                        array2[n3] -= alpha * hK[l];
                    }
                }
            }
            if (k < m - 1) {
                final double[] hKp1 = this.householderVectors[k + 1];
                xNormSqr = 0.0;
                for (int i2 = k + 1; i2 < m; ++i2) {
                    final double c2 = this.householderVectors[i2][k];
                    xNormSqr += c2 * c2;
                }
                final double b = (hKp1[k] > 0.0) ? (-FastMath.sqrt(xNormSqr)) : FastMath.sqrt(xNormSqr);
                this.secondary[k] = b;
                if (b != 0.0) {
                    final double[] array3 = hKp1;
                    final int n4 = k;
                    array3[n4] -= b;
                    for (int j2 = k + 1; j2 < n; ++j2) {
                        double beta = 0.0;
                        for (int i3 = k + 1; i3 < m; ++i3) {
                            final double[] hI2 = this.householderVectors[i3];
                            beta -= hI2[j2] * hI2[k];
                        }
                        beta /= b * hKp1[k];
                        for (int i3 = k + 1; i3 < m; ++i3) {
                            final double[] array4;
                            final double[] hI2 = array4 = this.householderVectors[i3];
                            final int n5 = j2;
                            array4[n5] -= beta * hI2[k];
                        }
                    }
                }
            }
        }
    }
}
