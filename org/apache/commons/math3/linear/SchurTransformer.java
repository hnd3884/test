package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

class SchurTransformer
{
    private static final int MAX_ITERATIONS = 100;
    private final double[][] matrixP;
    private final double[][] matrixT;
    private RealMatrix cachedP;
    private RealMatrix cachedT;
    private RealMatrix cachedPt;
    private final double epsilon;
    
    SchurTransformer(final RealMatrix matrix) {
        this.epsilon = Precision.EPSILON;
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        final HessenbergTransformer transformer = new HessenbergTransformer(matrix);
        this.matrixT = transformer.getH().getData();
        this.matrixP = transformer.getP().getData();
        this.cachedT = null;
        this.cachedP = null;
        this.cachedPt = null;
        this.transform();
    }
    
    public RealMatrix getP() {
        if (this.cachedP == null) {
            this.cachedP = MatrixUtils.createRealMatrix(this.matrixP);
        }
        return this.cachedP;
    }
    
    public RealMatrix getPT() {
        if (this.cachedPt == null) {
            this.cachedPt = this.getP().transpose();
        }
        return this.cachedPt;
    }
    
    public RealMatrix getT() {
        if (this.cachedT == null) {
            this.cachedT = MatrixUtils.createRealMatrix(this.matrixT);
        }
        return this.cachedT;
    }
    
    private void transform() {
        final int n = this.matrixT.length;
        final double norm = this.getNorm();
        final ShiftInfo shift = new ShiftInfo();
        int iteration = 0;
        int iu = n - 1;
        while (iu >= 0) {
            final int il = this.findSmallSubDiagonalElement(iu, norm);
            if (il == iu) {
                final double[] array = this.matrixT[iu];
                final int n2 = iu;
                array[n2] += shift.exShift;
                --iu;
                iteration = 0;
            }
            else if (il == iu - 1) {
                double p = (this.matrixT[iu - 1][iu - 1] - this.matrixT[iu][iu]) / 2.0;
                double q = p * p + this.matrixT[iu][iu - 1] * this.matrixT[iu - 1][iu];
                final double[] array2 = this.matrixT[iu];
                final int n3 = iu;
                array2[n3] += shift.exShift;
                final double[] array3 = this.matrixT[iu - 1];
                final int n4 = iu - 1;
                array3[n4] += shift.exShift;
                if (q >= 0.0) {
                    double z = FastMath.sqrt(FastMath.abs(q));
                    if (p >= 0.0) {
                        z += p;
                    }
                    else {
                        z = p - z;
                    }
                    final double x = this.matrixT[iu][iu - 1];
                    final double s = FastMath.abs(x) + FastMath.abs(z);
                    p = x / s;
                    q = z / s;
                    final double r = FastMath.sqrt(p * p + q * q);
                    p /= r;
                    q /= r;
                    for (int j = iu - 1; j < n; ++j) {
                        z = this.matrixT[iu - 1][j];
                        this.matrixT[iu - 1][j] = q * z + p * this.matrixT[iu][j];
                        this.matrixT[iu][j] = q * this.matrixT[iu][j] - p * z;
                    }
                    for (int i = 0; i <= iu; ++i) {
                        z = this.matrixT[i][iu - 1];
                        this.matrixT[i][iu - 1] = q * z + p * this.matrixT[i][iu];
                        this.matrixT[i][iu] = q * this.matrixT[i][iu] - p * z;
                    }
                    for (int i = 0; i <= n - 1; ++i) {
                        z = this.matrixP[i][iu - 1];
                        this.matrixP[i][iu - 1] = q * z + p * this.matrixP[i][iu];
                        this.matrixP[i][iu] = q * this.matrixP[i][iu] - p * z;
                    }
                }
                iu -= 2;
                iteration = 0;
            }
            else {
                this.computeShift(il, iu, iteration, shift);
                if (++iteration > 100) {
                    throw new MaxCountExceededException(LocalizedFormats.CONVERGENCE_FAILED, 100, new Object[0]);
                }
                final double[] hVec = new double[3];
                final int im = this.initQRStep(il, iu, shift, hVec);
                this.performDoubleQRStep(il, im, iu, shift, hVec);
            }
        }
    }
    
    private double getNorm() {
        double norm = 0.0;
        for (int i = 0; i < this.matrixT.length; ++i) {
            for (int j = FastMath.max(i - 1, 0); j < this.matrixT.length; ++j) {
                norm += FastMath.abs(this.matrixT[i][j]);
            }
        }
        return norm;
    }
    
    private int findSmallSubDiagonalElement(final int startIdx, final double norm) {
        int l;
        for (l = startIdx; l > 0; --l) {
            double s = FastMath.abs(this.matrixT[l - 1][l - 1]) + FastMath.abs(this.matrixT[l][l]);
            if (s == 0.0) {
                s = norm;
            }
            if (FastMath.abs(this.matrixT[l][l - 1]) < this.epsilon * s) {
                break;
            }
        }
        return l;
    }
    
    private void computeShift(final int l, final int idx, final int iteration, final ShiftInfo shift) {
        shift.x = this.matrixT[idx][idx];
        final double n = 0.0;
        shift.w = n;
        shift.y = n;
        if (l < idx) {
            shift.y = this.matrixT[idx - 1][idx - 1];
            shift.w = this.matrixT[idx][idx - 1] * this.matrixT[idx - 1][idx];
        }
        if (iteration == 10) {
            shift.exShift += shift.x;
            for (int i = 0; i <= idx; ++i) {
                final double[] array = this.matrixT[i];
                final int n2 = i;
                array[n2] -= shift.x;
            }
            final double s = FastMath.abs(this.matrixT[idx][idx - 1]) + FastMath.abs(this.matrixT[idx - 1][idx - 2]);
            shift.x = 0.75 * s;
            shift.y = 0.75 * s;
            shift.w = -0.4375 * s * s;
        }
        if (iteration == 30) {
            double s = (shift.y - shift.x) / 2.0;
            s = s * s + shift.w;
            if (s > 0.0) {
                s = FastMath.sqrt(s);
                if (shift.y < shift.x) {
                    s = -s;
                }
                s = shift.x - shift.w / ((shift.y - shift.x) / 2.0 + s);
                for (int j = 0; j <= idx; ++j) {
                    final double[] array2 = this.matrixT[j];
                    final int n3 = j;
                    array2[n3] -= s;
                }
                shift.exShift += s;
                final double x = 0.964;
                shift.w = x;
                shift.y = x;
                shift.x = x;
            }
        }
    }
    
    private int initQRStep(final int il, final int iu, final ShiftInfo shift, final double[] hVec) {
        int im;
        for (im = iu - 2; im >= il; --im) {
            final double z = this.matrixT[im][im];
            final double r = shift.x - z;
            final double s = shift.y - z;
            hVec[0] = (r * s - shift.w) / this.matrixT[im + 1][im] + this.matrixT[im][im + 1];
            hVec[1] = this.matrixT[im + 1][im + 1] - z - r - s;
            hVec[2] = this.matrixT[im + 2][im + 1];
            if (im == il) {
                break;
            }
            final double lhs = FastMath.abs(this.matrixT[im][im - 1]) * (FastMath.abs(hVec[1]) + FastMath.abs(hVec[2]));
            final double rhs = FastMath.abs(hVec[0]) * (FastMath.abs(this.matrixT[im - 1][im - 1]) + FastMath.abs(z) + FastMath.abs(this.matrixT[im + 1][im + 1]));
            if (lhs < this.epsilon * rhs) {
                break;
            }
        }
        return im;
    }
    
    private void performDoubleQRStep(final int il, final int im, final int iu, final ShiftInfo shift, final double[] hVec) {
        final int n = this.matrixT.length;
        double p = hVec[0];
        double q = hVec[1];
        double r = hVec[2];
        for (int k = im; k <= iu - 1; ++k) {
            final boolean notlast = k != iu - 1;
            if (k != im) {
                p = this.matrixT[k][k - 1];
                q = this.matrixT[k + 1][k - 1];
                r = (notlast ? this.matrixT[k + 2][k - 1] : 0.0);
                shift.x = FastMath.abs(p) + FastMath.abs(q) + FastMath.abs(r);
                if (Precision.equals(shift.x, 0.0, this.epsilon)) {
                    continue;
                }
                p /= shift.x;
                q /= shift.x;
                r /= shift.x;
            }
            double s = FastMath.sqrt(p * p + q * q + r * r);
            if (p < 0.0) {
                s = -s;
            }
            if (s != 0.0) {
                if (k != im) {
                    this.matrixT[k][k - 1] = -s * shift.x;
                }
                else if (il != im) {
                    this.matrixT[k][k - 1] = -this.matrixT[k][k - 1];
                }
                p += s;
                shift.x = p / s;
                shift.y = q / s;
                final double z = r / s;
                q /= p;
                r /= p;
                for (int j = k; j < n; ++j) {
                    p = this.matrixT[k][j] + q * this.matrixT[k + 1][j];
                    if (notlast) {
                        p += r * this.matrixT[k + 2][j];
                        final double[] array = this.matrixT[k + 2];
                        final int n2 = j;
                        array[n2] -= p * z;
                    }
                    final double[] array2 = this.matrixT[k];
                    final int n3 = j;
                    array2[n3] -= p * shift.x;
                    final double[] array3 = this.matrixT[k + 1];
                    final int n4 = j;
                    array3[n4] -= p * shift.y;
                }
                for (int i = 0; i <= FastMath.min(iu, k + 3); ++i) {
                    p = shift.x * this.matrixT[i][k] + shift.y * this.matrixT[i][k + 1];
                    if (notlast) {
                        p += z * this.matrixT[i][k + 2];
                        final double[] array4 = this.matrixT[i];
                        final int n5 = k + 2;
                        array4[n5] -= p * r;
                    }
                    final double[] array5 = this.matrixT[i];
                    final int n6 = k;
                    array5[n6] -= p;
                    final double[] array6 = this.matrixT[i];
                    final int n7 = k + 1;
                    array6[n7] -= p * q;
                }
                for (int high = this.matrixT.length - 1, l = 0; l <= high; ++l) {
                    p = shift.x * this.matrixP[l][k] + shift.y * this.matrixP[l][k + 1];
                    if (notlast) {
                        p += z * this.matrixP[l][k + 2];
                        final double[] array7 = this.matrixP[l];
                        final int n8 = k + 2;
                        array7[n8] -= p * r;
                    }
                    final double[] array8 = this.matrixP[l];
                    final int n9 = k;
                    array8[n9] -= p;
                    final double[] array9 = this.matrixP[l];
                    final int n10 = k + 1;
                    array9[n10] -= p * q;
                }
            }
        }
        for (int m = im + 2; m <= iu; ++m) {
            this.matrixT[m][m - 2] = 0.0;
            if (m > im + 2) {
                this.matrixT[m][m - 3] = 0.0;
            }
        }
    }
    
    private static class ShiftInfo
    {
        double x;
        double y;
        double w;
        double exShift;
    }
}
