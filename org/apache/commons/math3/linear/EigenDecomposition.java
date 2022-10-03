package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.Precision;

public class EigenDecomposition
{
    private static final double EPSILON = 1.0E-12;
    private byte maxIter;
    private double[] main;
    private double[] secondary;
    private TriDiagonalTransformer transformer;
    private double[] realEigenvalues;
    private double[] imagEigenvalues;
    private ArrayRealVector[] eigenvectors;
    private RealMatrix cachedV;
    private RealMatrix cachedD;
    private RealMatrix cachedVt;
    private final boolean isSymmetric;
    
    public EigenDecomposition(final RealMatrix matrix) throws MathArithmeticException {
        this.maxIter = 30;
        final double symTol = 10 * matrix.getRowDimension() * matrix.getColumnDimension() * Precision.EPSILON;
        this.isSymmetric = MatrixUtils.isSymmetric(matrix, symTol);
        if (this.isSymmetric) {
            this.transformToTridiagonal(matrix);
            this.findEigenVectors(this.transformer.getQ().getData());
        }
        else {
            final SchurTransformer t = this.transformToSchur(matrix);
            this.findEigenVectorsFromSchur(t);
        }
    }
    
    @Deprecated
    public EigenDecomposition(final RealMatrix matrix, final double splitTolerance) throws MathArithmeticException {
        this(matrix);
    }
    
    public EigenDecomposition(final double[] main, final double[] secondary) {
        this.maxIter = 30;
        this.isSymmetric = true;
        this.main = main.clone();
        this.secondary = secondary.clone();
        this.transformer = null;
        final int size = main.length;
        final double[][] z = new double[size][size];
        for (int i = 0; i < size; ++i) {
            z[i][i] = 1.0;
        }
        this.findEigenVectors(z);
    }
    
    @Deprecated
    public EigenDecomposition(final double[] main, final double[] secondary, final double splitTolerance) {
        this(main, secondary);
    }
    
    public RealMatrix getV() {
        if (this.cachedV == null) {
            final int m = this.eigenvectors.length;
            this.cachedV = MatrixUtils.createRealMatrix(m, m);
            for (int k = 0; k < m; ++k) {
                this.cachedV.setColumnVector(k, this.eigenvectors[k]);
            }
        }
        return this.cachedV;
    }
    
    public RealMatrix getD() {
        if (this.cachedD == null) {
            this.cachedD = MatrixUtils.createRealDiagonalMatrix(this.realEigenvalues);
            for (int i = 0; i < this.imagEigenvalues.length; ++i) {
                if (Precision.compareTo(this.imagEigenvalues[i], 0.0, 1.0E-12) > 0) {
                    this.cachedD.setEntry(i, i + 1, this.imagEigenvalues[i]);
                }
                else if (Precision.compareTo(this.imagEigenvalues[i], 0.0, 1.0E-12) < 0) {
                    this.cachedD.setEntry(i, i - 1, this.imagEigenvalues[i]);
                }
            }
        }
        return this.cachedD;
    }
    
    public RealMatrix getVT() {
        if (this.cachedVt == null) {
            final int m = this.eigenvectors.length;
            this.cachedVt = MatrixUtils.createRealMatrix(m, m);
            for (int k = 0; k < m; ++k) {
                this.cachedVt.setRowVector(k, this.eigenvectors[k]);
            }
        }
        return this.cachedVt;
    }
    
    public boolean hasComplexEigenvalues() {
        for (int i = 0; i < this.imagEigenvalues.length; ++i) {
            if (!Precision.equals(this.imagEigenvalues[i], 0.0, 1.0E-12)) {
                return true;
            }
        }
        return false;
    }
    
    public double[] getRealEigenvalues() {
        return this.realEigenvalues.clone();
    }
    
    public double getRealEigenvalue(final int i) {
        return this.realEigenvalues[i];
    }
    
    public double[] getImagEigenvalues() {
        return this.imagEigenvalues.clone();
    }
    
    public double getImagEigenvalue(final int i) {
        return this.imagEigenvalues[i];
    }
    
    public RealVector getEigenvector(final int i) {
        return this.eigenvectors[i].copy();
    }
    
    public double getDeterminant() {
        double determinant = 1.0;
        for (final double lambda : this.realEigenvalues) {
            determinant *= lambda;
        }
        return determinant;
    }
    
    public RealMatrix getSquareRoot() {
        if (!this.isSymmetric) {
            throw new MathUnsupportedOperationException();
        }
        final double[] sqrtEigenValues = new double[this.realEigenvalues.length];
        for (int i = 0; i < this.realEigenvalues.length; ++i) {
            final double eigen = this.realEigenvalues[i];
            if (eigen <= 0.0) {
                throw new MathUnsupportedOperationException();
            }
            sqrtEigenValues[i] = FastMath.sqrt(eigen);
        }
        final RealMatrix sqrtEigen = MatrixUtils.createRealDiagonalMatrix(sqrtEigenValues);
        final RealMatrix v = this.getV();
        final RealMatrix vT = this.getVT();
        return v.multiply(sqrtEigen).multiply(vT);
    }
    
    public DecompositionSolver getSolver() {
        if (this.hasComplexEigenvalues()) {
            throw new MathUnsupportedOperationException();
        }
        return new Solver(this.realEigenvalues, this.imagEigenvalues, this.eigenvectors);
    }
    
    private void transformToTridiagonal(final RealMatrix matrix) {
        this.transformer = new TriDiagonalTransformer(matrix);
        this.main = this.transformer.getMainDiagonalRef();
        this.secondary = this.transformer.getSecondaryDiagonalRef();
    }
    
    private void findEigenVectors(final double[][] householderMatrix) {
        final double[][] z = householderMatrix.clone();
        final int n = this.main.length;
        this.realEigenvalues = new double[n];
        this.imagEigenvalues = new double[n];
        final double[] e = new double[n];
        for (int i = 0; i < n - 1; ++i) {
            this.realEigenvalues[i] = this.main[i];
            e[i] = this.secondary[i];
        }
        this.realEigenvalues[n - 1] = this.main[n - 1];
        e[n - 1] = 0.0;
        double maxAbsoluteValue = 0.0;
        for (int j = 0; j < n; ++j) {
            if (FastMath.abs(this.realEigenvalues[j]) > maxAbsoluteValue) {
                maxAbsoluteValue = FastMath.abs(this.realEigenvalues[j]);
            }
            if (FastMath.abs(e[j]) > maxAbsoluteValue) {
                maxAbsoluteValue = FastMath.abs(e[j]);
            }
        }
        if (maxAbsoluteValue != 0.0) {
            for (int j = 0; j < n; ++j) {
                if (FastMath.abs(this.realEigenvalues[j]) <= Precision.EPSILON * maxAbsoluteValue) {
                    this.realEigenvalues[j] = 0.0;
                }
                if (FastMath.abs(e[j]) <= Precision.EPSILON * maxAbsoluteValue) {
                    e[j] = 0.0;
                }
            }
        }
        for (int k = 0; k < n; ++k) {
            int its = 0;
            int m;
            do {
                for (m = k; m < n - 1; ++m) {
                    final double delta = FastMath.abs(this.realEigenvalues[m]) + FastMath.abs(this.realEigenvalues[m + 1]);
                    if (FastMath.abs(e[m]) + delta == delta) {
                        break;
                    }
                }
                if (m != k) {
                    if (its == this.maxIter) {
                        throw new MaxCountExceededException(LocalizedFormats.CONVERGENCE_FAILED, this.maxIter, new Object[0]);
                    }
                    ++its;
                    double q = (this.realEigenvalues[k + 1] - this.realEigenvalues[k]) / (2.0 * e[k]);
                    double t = FastMath.sqrt(1.0 + q * q);
                    if (q < 0.0) {
                        q = this.realEigenvalues[m] - this.realEigenvalues[k] + e[k] / (q - t);
                    }
                    else {
                        q = this.realEigenvalues[m] - this.realEigenvalues[k] + e[k] / (q + t);
                    }
                    double u = 0.0;
                    double s = 1.0;
                    double c = 1.0;
                    int l;
                    for (l = m - 1; l >= k; --l) {
                        double p = s * e[l];
                        final double h = c * e[l];
                        if (FastMath.abs(p) >= FastMath.abs(q)) {
                            c = q / p;
                            t = FastMath.sqrt(c * c + 1.0);
                            e[l + 1] = p * t;
                            s = 1.0 / t;
                            c *= s;
                        }
                        else {
                            s = p / q;
                            t = FastMath.sqrt(s * s + 1.0);
                            e[l + 1] = q * t;
                            c = 1.0 / t;
                            s *= c;
                        }
                        if (e[l + 1] == 0.0) {
                            final double[] realEigenvalues = this.realEigenvalues;
                            final int n2 = l + 1;
                            realEigenvalues[n2] -= u;
                            e[m] = 0.0;
                            break;
                        }
                        q = this.realEigenvalues[l + 1] - u;
                        t = (this.realEigenvalues[l] - q) * s + 2.0 * c * h;
                        u = s * t;
                        this.realEigenvalues[l + 1] = q + u;
                        q = c * t - h;
                        for (int ia = 0; ia < n; ++ia) {
                            p = z[ia][l + 1];
                            z[ia][l + 1] = s * z[ia][l] + c * p;
                            z[ia][l] = c * z[ia][l] - s * p;
                        }
                    }
                    if (t == 0.0 && l >= k) {
                        continue;
                    }
                    final double[] realEigenvalues2 = this.realEigenvalues;
                    final int n3 = k;
                    realEigenvalues2[n3] -= u;
                    e[k] = q;
                    e[m] = 0.0;
                }
            } while (m != k);
        }
        for (int j = 0; j < n; ++j) {
            int k2 = j;
            double p2 = this.realEigenvalues[j];
            for (int j2 = j + 1; j2 < n; ++j2) {
                if (this.realEigenvalues[j2] > p2) {
                    k2 = j2;
                    p2 = this.realEigenvalues[j2];
                }
            }
            if (k2 != j) {
                this.realEigenvalues[k2] = this.realEigenvalues[j];
                this.realEigenvalues[j] = p2;
                for (int j2 = 0; j2 < n; ++j2) {
                    p2 = z[j2][j];
                    z[j2][j] = z[j2][k2];
                    z[j2][k2] = p2;
                }
            }
        }
        maxAbsoluteValue = 0.0;
        for (int j = 0; j < n; ++j) {
            if (FastMath.abs(this.realEigenvalues[j]) > maxAbsoluteValue) {
                maxAbsoluteValue = FastMath.abs(this.realEigenvalues[j]);
            }
        }
        if (maxAbsoluteValue != 0.0) {
            for (int j = 0; j < n; ++j) {
                if (FastMath.abs(this.realEigenvalues[j]) < Precision.EPSILON * maxAbsoluteValue) {
                    this.realEigenvalues[j] = 0.0;
                }
            }
        }
        this.eigenvectors = new ArrayRealVector[n];
        final double[] tmp = new double[n];
        for (int i2 = 0; i2 < n; ++i2) {
            for (int j3 = 0; j3 < n; ++j3) {
                tmp[j3] = z[j3][i2];
            }
            this.eigenvectors[i2] = new ArrayRealVector(tmp);
        }
    }
    
    private SchurTransformer transformToSchur(final RealMatrix matrix) {
        final SchurTransformer schurTransform = new SchurTransformer(matrix);
        final double[][] matT = schurTransform.getT().getData();
        this.realEigenvalues = new double[matT.length];
        this.imagEigenvalues = new double[matT.length];
        for (int i = 0; i < this.realEigenvalues.length; ++i) {
            if (i == this.realEigenvalues.length - 1 || Precision.equals(matT[i + 1][i], 0.0, 1.0E-12)) {
                this.realEigenvalues[i] = matT[i][i];
            }
            else {
                final double x = matT[i + 1][i + 1];
                final double p = 0.5 * (matT[i][i] - x);
                final double z = FastMath.sqrt(FastMath.abs(p * p + matT[i + 1][i] * matT[i][i + 1]));
                this.realEigenvalues[i] = x + p;
                this.imagEigenvalues[i] = z;
                this.realEigenvalues[i + 1] = x + p;
                this.imagEigenvalues[i + 1] = -z;
                ++i;
            }
        }
        return schurTransform;
    }
    
    private Complex cdiv(final double xr, final double xi, final double yr, final double yi) {
        return new Complex(xr, xi).divide(new Complex(yr, yi));
    }
    
    private void findEigenVectorsFromSchur(final SchurTransformer schur) throws MathArithmeticException {
        final double[][] matrixT = schur.getT().getData();
        final double[][] matrixP = schur.getP().getData();
        final int n = matrixT.length;
        double norm = 0.0;
        for (int i = 0; i < n; ++i) {
            for (int j = FastMath.max(i - 1, 0); j < n; ++j) {
                norm += FastMath.abs(matrixT[i][j]);
            }
        }
        if (Precision.equals(norm, 0.0, 1.0E-12)) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        double r = 0.0;
        double s = 0.0;
        double z = 0.0;
        for (int idx = n - 1; idx >= 0; --idx) {
            final double p = this.realEigenvalues[idx];
            double q = this.imagEigenvalues[idx];
            if (Precision.equals(q, 0.0)) {
                int l = idx;
                matrixT[idx][idx] = 1.0;
                for (int k = idx - 1; k >= 0; --k) {
                    final double w = matrixT[k][k] - p;
                    r = 0.0;
                    for (int m = l; m <= idx; ++m) {
                        r += matrixT[k][m] * matrixT[m][idx];
                    }
                    if (Precision.compareTo(this.imagEigenvalues[k], 0.0, 1.0E-12) < 0) {
                        z = w;
                        s = r;
                    }
                    else {
                        l = k;
                        if (Precision.equals(this.imagEigenvalues[k], 0.0)) {
                            if (w != 0.0) {
                                matrixT[k][idx] = -r / w;
                            }
                            else {
                                matrixT[k][idx] = -r / (Precision.EPSILON * norm);
                            }
                        }
                        else {
                            final double x = matrixT[k][k + 1];
                            final double y = matrixT[k + 1][k];
                            q = (this.realEigenvalues[k] - p) * (this.realEigenvalues[k] - p) + this.imagEigenvalues[k] * this.imagEigenvalues[k];
                            final double t = (x * s - z * r) / q;
                            matrixT[k][idx] = t;
                            if (FastMath.abs(x) > FastMath.abs(z)) {
                                matrixT[k + 1][idx] = (-r - w * t) / x;
                            }
                            else {
                                matrixT[k + 1][idx] = (-s - y * t) / z;
                            }
                        }
                        final double t2 = FastMath.abs(matrixT[k][idx]);
                        if (Precision.EPSILON * t2 * t2 > 1.0) {
                            for (int j2 = k; j2 <= idx; ++j2) {
                                final double[] array = matrixT[j2];
                                final int n2 = idx;
                                array[n2] /= t2;
                            }
                        }
                    }
                }
            }
            else if (q < 0.0) {
                int l = idx - 1;
                if (FastMath.abs(matrixT[idx][idx - 1]) > FastMath.abs(matrixT[idx - 1][idx])) {
                    matrixT[idx - 1][idx - 1] = q / matrixT[idx][idx - 1];
                    matrixT[idx - 1][idx] = -(matrixT[idx][idx] - p) / matrixT[idx][idx - 1];
                }
                else {
                    final Complex result = this.cdiv(0.0, -matrixT[idx - 1][idx], matrixT[idx - 1][idx - 1] - p, q);
                    matrixT[idx - 1][idx - 1] = result.getReal();
                    matrixT[idx - 1][idx] = result.getImaginary();
                }
                matrixT[idx][idx - 1] = 0.0;
                matrixT[idx][idx] = 1.0;
                for (int k = idx - 2; k >= 0; --k) {
                    double ra = 0.0;
                    double sa = 0.0;
                    for (int j2 = l; j2 <= idx; ++j2) {
                        ra += matrixT[k][j2] * matrixT[j2][idx - 1];
                        sa += matrixT[k][j2] * matrixT[j2][idx];
                    }
                    final double w2 = matrixT[k][k] - p;
                    if (Precision.compareTo(this.imagEigenvalues[k], 0.0, 1.0E-12) < 0) {
                        z = w2;
                        r = ra;
                        s = sa;
                    }
                    else {
                        l = k;
                        if (Precision.equals(this.imagEigenvalues[k], 0.0)) {
                            final Complex c = this.cdiv(-ra, -sa, w2, q);
                            matrixT[k][idx - 1] = c.getReal();
                            matrixT[k][idx] = c.getImaginary();
                        }
                        else {
                            final double x2 = matrixT[k][k + 1];
                            final double y2 = matrixT[k + 1][k];
                            double vr = (this.realEigenvalues[k] - p) * (this.realEigenvalues[k] - p) + this.imagEigenvalues[k] * this.imagEigenvalues[k] - q * q;
                            final double vi = (this.realEigenvalues[k] - p) * 2.0 * q;
                            if (Precision.equals(vr, 0.0) && Precision.equals(vi, 0.0)) {
                                vr = Precision.EPSILON * norm * (FastMath.abs(w2) + FastMath.abs(q) + FastMath.abs(x2) + FastMath.abs(y2) + FastMath.abs(z));
                            }
                            final Complex c2 = this.cdiv(x2 * r - z * ra + q * sa, x2 * s - z * sa - q * ra, vr, vi);
                            matrixT[k][idx - 1] = c2.getReal();
                            matrixT[k][idx] = c2.getImaginary();
                            if (FastMath.abs(x2) > FastMath.abs(z) + FastMath.abs(q)) {
                                matrixT[k + 1][idx - 1] = (-ra - w2 * matrixT[k][idx - 1] + q * matrixT[k][idx]) / x2;
                                matrixT[k + 1][idx] = (-sa - w2 * matrixT[k][idx] - q * matrixT[k][idx - 1]) / x2;
                            }
                            else {
                                final Complex c3 = this.cdiv(-r - y2 * matrixT[k][idx - 1], -s - y2 * matrixT[k][idx], z, q);
                                matrixT[k + 1][idx - 1] = c3.getReal();
                                matrixT[k + 1][idx] = c3.getImaginary();
                            }
                        }
                        final double t = FastMath.max(FastMath.abs(matrixT[k][idx - 1]), FastMath.abs(matrixT[k][idx]));
                        if (Precision.EPSILON * t * t > 1.0) {
                            for (int j3 = k; j3 <= idx; ++j3) {
                                final double[] array2 = matrixT[j3];
                                final int n3 = idx - 1;
                                array2[n3] /= t;
                                final double[] array3 = matrixT[j3];
                                final int n4 = idx;
                                array3[n4] /= t;
                            }
                        }
                    }
                }
            }
        }
        for (int j4 = n - 1; j4 >= 0; --j4) {
            for (int i2 = 0; i2 <= n - 1; ++i2) {
                z = 0.0;
                for (int k2 = 0; k2 <= FastMath.min(j4, n - 1); ++k2) {
                    z += matrixP[i2][k2] * matrixT[k2][j4];
                }
                matrixP[i2][j4] = z;
            }
        }
        this.eigenvectors = new ArrayRealVector[n];
        final double[] tmp = new double[n];
        for (int i2 = 0; i2 < n; ++i2) {
            for (int j5 = 0; j5 < n; ++j5) {
                tmp[j5] = matrixP[j5][i2];
            }
            this.eigenvectors[i2] = new ArrayRealVector(tmp);
        }
    }
    
    private static class Solver implements DecompositionSolver
    {
        private double[] realEigenvalues;
        private double[] imagEigenvalues;
        private final ArrayRealVector[] eigenvectors;
        
        private Solver(final double[] realEigenvalues, final double[] imagEigenvalues, final ArrayRealVector[] eigenvectors) {
            this.realEigenvalues = realEigenvalues;
            this.imagEigenvalues = imagEigenvalues;
            this.eigenvectors = eigenvectors;
        }
        
        public RealVector solve(final RealVector b) {
            if (!this.isNonSingular()) {
                throw new SingularMatrixException();
            }
            final int m = this.realEigenvalues.length;
            if (b.getDimension() != m) {
                throw new DimensionMismatchException(b.getDimension(), m);
            }
            final double[] bp = new double[m];
            for (int i = 0; i < m; ++i) {
                final ArrayRealVector v = this.eigenvectors[i];
                final double[] vData = v.getDataRef();
                final double s = v.dotProduct(b) / this.realEigenvalues[i];
                for (int j = 0; j < m; ++j) {
                    final double[] array = bp;
                    final int n = j;
                    array[n] += s * vData[j];
                }
            }
            return new ArrayRealVector(bp, false);
        }
        
        public RealMatrix solve(final RealMatrix b) {
            if (!this.isNonSingular()) {
                throw new SingularMatrixException();
            }
            final int m = this.realEigenvalues.length;
            if (b.getRowDimension() != m) {
                throw new DimensionMismatchException(b.getRowDimension(), m);
            }
            final int nColB = b.getColumnDimension();
            final double[][] bp = new double[m][nColB];
            final double[] tmpCol = new double[m];
            for (int k = 0; k < nColB; ++k) {
                for (int i = 0; i < m; ++i) {
                    tmpCol[i] = b.getEntry(i, k);
                    bp[i][k] = 0.0;
                }
                for (int i = 0; i < m; ++i) {
                    final ArrayRealVector v = this.eigenvectors[i];
                    final double[] vData = v.getDataRef();
                    double s = 0.0;
                    for (int j = 0; j < m; ++j) {
                        s += v.getEntry(j) * tmpCol[j];
                    }
                    s /= this.realEigenvalues[i];
                    for (int j = 0; j < m; ++j) {
                        final double[] array = bp[j];
                        final int n = k;
                        array[n] += s * vData[j];
                    }
                }
            }
            return new Array2DRowRealMatrix(bp, false);
        }
        
        public boolean isNonSingular() {
            double largestEigenvalueNorm = 0.0;
            for (int i = 0; i < this.realEigenvalues.length; ++i) {
                largestEigenvalueNorm = FastMath.max(largestEigenvalueNorm, this.eigenvalueNorm(i));
            }
            if (largestEigenvalueNorm == 0.0) {
                return false;
            }
            for (int i = 0; i < this.realEigenvalues.length; ++i) {
                if (Precision.equals(this.eigenvalueNorm(i) / largestEigenvalueNorm, 0.0, 1.0E-12)) {
                    return false;
                }
            }
            return true;
        }
        
        private double eigenvalueNorm(final int i) {
            final double re = this.realEigenvalues[i];
            final double im = this.imagEigenvalues[i];
            return FastMath.sqrt(re * re + im * im);
        }
        
        public RealMatrix getInverse() {
            if (!this.isNonSingular()) {
                throw new SingularMatrixException();
            }
            final int m = this.realEigenvalues.length;
            final double[][] invData = new double[m][m];
            for (int i = 0; i < m; ++i) {
                final double[] invI = invData[i];
                for (int j = 0; j < m; ++j) {
                    double invIJ = 0.0;
                    for (int k = 0; k < m; ++k) {
                        final double[] vK = this.eigenvectors[k].getDataRef();
                        invIJ += vK[i] * vK[j] / this.realEigenvalues[k];
                    }
                    invI[j] = invIJ;
                }
            }
            return MatrixUtils.createRealMatrix(invData);
        }
    }
}
