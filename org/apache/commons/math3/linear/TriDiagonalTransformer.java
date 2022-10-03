package org.apache.commons.math3.linear;

import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;

class TriDiagonalTransformer
{
    private final double[][] householderVectors;
    private final double[] main;
    private final double[] secondary;
    private RealMatrix cachedQ;
    private RealMatrix cachedQt;
    private RealMatrix cachedT;
    
    TriDiagonalTransformer(final RealMatrix matrix) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        final int m = matrix.getRowDimension();
        this.householderVectors = matrix.getData();
        this.main = new double[m];
        this.secondary = new double[m - 1];
        this.cachedQ = null;
        this.cachedQt = null;
        this.cachedT = null;
        this.transform();
    }
    
    public RealMatrix getQ() {
        if (this.cachedQ == null) {
            this.cachedQ = this.getQT().transpose();
        }
        return this.cachedQ;
    }
    
    public RealMatrix getQT() {
        if (this.cachedQt == null) {
            final int m = this.householderVectors.length;
            final double[][] qta = new double[m][m];
            for (int k = m - 1; k >= 1; --k) {
                final double[] hK = this.householderVectors[k - 1];
                qta[k][k] = 1.0;
                if (hK[k] != 0.0) {
                    final double inv = 1.0 / (this.secondary[k - 1] * hK[k]);
                    double beta = 1.0 / this.secondary[k - 1];
                    qta[k][k] = 1.0 + beta * hK[k];
                    for (int i = k + 1; i < m; ++i) {
                        qta[k][i] = beta * hK[i];
                    }
                    for (int j = k + 1; j < m; ++j) {
                        beta = 0.0;
                        for (int l = k + 1; l < m; ++l) {
                            beta += qta[j][l] * hK[l];
                        }
                        beta *= inv;
                        qta[j][k] = beta * hK[k];
                        for (int l = k + 1; l < m; ++l) {
                            final double[] array = qta[j];
                            final int n = l;
                            array[n] += beta * hK[l];
                        }
                    }
                }
            }
            qta[0][0] = 1.0;
            this.cachedQt = MatrixUtils.createRealMatrix(qta);
        }
        return this.cachedQt;
    }
    
    public RealMatrix getT() {
        if (this.cachedT == null) {
            final int m = this.main.length;
            final double[][] ta = new double[m][m];
            for (int i = 0; i < m; ++i) {
                ta[i][i] = this.main[i];
                if (i > 0) {
                    ta[i][i - 1] = this.secondary[i - 1];
                }
                if (i < this.main.length - 1) {
                    ta[i][i + 1] = this.secondary[i];
                }
            }
            this.cachedT = MatrixUtils.createRealMatrix(ta);
        }
        return this.cachedT;
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
    
    private void transform() {
        final int m = this.householderVectors.length;
        final double[] z = new double[m];
        for (int k = 0; k < m - 1; ++k) {
            final double[] hK = this.householderVectors[k];
            this.main[k] = hK[k];
            double xNormSqr = 0.0;
            for (int j = k + 1; j < m; ++j) {
                final double c = hK[j];
                xNormSqr += c * c;
            }
            final double a = (hK[k + 1] > 0.0) ? (-FastMath.sqrt(xNormSqr)) : FastMath.sqrt(xNormSqr);
            this.secondary[k] = a;
            if (a != 0.0) {
                final double[] array = hK;
                final int n = k + 1;
                array[n] -= a;
                final double beta = -1.0 / (a * hK[k + 1]);
                Arrays.fill(z, k + 1, m, 0.0);
                for (int i = k + 1; i < m; ++i) {
                    final double[] hI = this.householderVectors[i];
                    final double hKI = hK[i];
                    double zI = hI[i] * hKI;
                    for (int l = i + 1; l < m; ++l) {
                        final double hIJ = hI[l];
                        zI += hIJ * hK[l];
                        final double[] array2 = z;
                        final int n2 = l;
                        array2[n2] += hIJ * hKI;
                    }
                    z[i] = beta * (z[i] + zI);
                }
                double gamma = 0.0;
                for (int i2 = k + 1; i2 < m; ++i2) {
                    gamma += z[i2] * hK[i2];
                }
                gamma *= beta / 2.0;
                for (int i2 = k + 1; i2 < m; ++i2) {
                    final double[] array3 = z;
                    final int n3 = i2;
                    array3[n3] -= gamma * hK[i2];
                }
                for (int i2 = k + 1; i2 < m; ++i2) {
                    final double[] hI2 = this.householderVectors[i2];
                    for (int j2 = i2; j2 < m; ++j2) {
                        final double[] array4 = hI2;
                        final int n4 = j2;
                        array4[n4] -= hK[i2] * z[j2] + z[i2] * hK[j2];
                    }
                }
            }
        }
        this.main[m - 1] = this.householderVectors[m - 1][m - 1];
    }
}
