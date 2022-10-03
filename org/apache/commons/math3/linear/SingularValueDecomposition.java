package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.FastMath;

public class SingularValueDecomposition
{
    private static final double EPS = 2.220446049250313E-16;
    private static final double TINY = 1.6033346880071782E-291;
    private final double[] singularValues;
    private final int m;
    private final int n;
    private final boolean transposed;
    private final RealMatrix cachedU;
    private RealMatrix cachedUt;
    private RealMatrix cachedS;
    private final RealMatrix cachedV;
    private RealMatrix cachedVt;
    private final double tol;
    
    public SingularValueDecomposition(final RealMatrix matrix) {
        double[][] A;
        if (matrix.getRowDimension() < matrix.getColumnDimension()) {
            this.transposed = true;
            A = matrix.transpose().getData();
            this.m = matrix.getColumnDimension();
            this.n = matrix.getRowDimension();
        }
        else {
            this.transposed = false;
            A = matrix.getData();
            this.m = matrix.getRowDimension();
            this.n = matrix.getColumnDimension();
        }
        this.singularValues = new double[this.n];
        final double[][] U = new double[this.m][this.n];
        final double[][] V = new double[this.n][this.n];
        final double[] e = new double[this.n];
        final double[] work = new double[this.m];
        final int nct = FastMath.min(this.m - 1, this.n);
        final int nrt = FastMath.max(0, this.n - 2);
        for (int k = 0; k < FastMath.max(nct, nrt); ++k) {
            if (k < nct) {
                this.singularValues[k] = 0.0;
                for (int i = k; i < this.m; ++i) {
                    this.singularValues[k] = FastMath.hypot(this.singularValues[k], A[i][k]);
                }
                if (this.singularValues[k] != 0.0) {
                    if (A[k][k] < 0.0) {
                        this.singularValues[k] = -this.singularValues[k];
                    }
                    for (int i = k; i < this.m; ++i) {
                        final double[] array = A[i];
                        final int n = k;
                        array[n] /= this.singularValues[k];
                    }
                    final double[] array2 = A[k];
                    final int n2 = k;
                    ++array2[n2];
                }
                this.singularValues[k] = -this.singularValues[k];
            }
            for (int j = k + 1; j < this.n; ++j) {
                if (k < nct && this.singularValues[k] != 0.0) {
                    double t = 0.0;
                    for (int l = k; l < this.m; ++l) {
                        t += A[l][k] * A[l][j];
                    }
                    t = -t / A[k][k];
                    for (int l = k; l < this.m; ++l) {
                        final double[] array3 = A[l];
                        final int n3 = j;
                        array3[n3] += t * A[l][k];
                    }
                }
                e[j] = A[k][j];
            }
            if (k < nct) {
                for (int i = k; i < this.m; ++i) {
                    U[i][k] = A[i][k];
                }
            }
            if (k < nrt) {
                e[k] = 0.0;
                for (int i = k + 1; i < this.n; ++i) {
                    e[k] = FastMath.hypot(e[k], e[i]);
                }
                if (e[k] != 0.0) {
                    if (e[k + 1] < 0.0) {
                        e[k] = -e[k];
                    }
                    for (int i = k + 1; i < this.n; ++i) {
                        final double[] array4 = e;
                        final int n4 = i;
                        array4[n4] /= e[k];
                    }
                    final double[] array5 = e;
                    final int n5 = k + 1;
                    ++array5[n5];
                }
                e[k] = -e[k];
                if (k + 1 < this.m && e[k] != 0.0) {
                    for (int i = k + 1; i < this.m; ++i) {
                        work[i] = 0.0;
                    }
                    for (int j = k + 1; j < this.n; ++j) {
                        for (int m = k + 1; m < this.m; ++m) {
                            final double[] array6 = work;
                            final int n6 = m;
                            array6[n6] += e[j] * A[m][j];
                        }
                    }
                    for (int j = k + 1; j < this.n; ++j) {
                        final double t = -e[j] / e[k + 1];
                        for (int l = k + 1; l < this.m; ++l) {
                            final double[] array7 = A[l];
                            final int n7 = j;
                            array7[n7] += t * work[l];
                        }
                    }
                }
                for (int i = k + 1; i < this.n; ++i) {
                    V[i][k] = e[i];
                }
            }
        }
        int p = this.n;
        if (nct < this.n) {
            this.singularValues[nct] = A[nct][nct];
        }
        if (this.m < p) {
            this.singularValues[p - 1] = 0.0;
        }
        if (nrt + 1 < p) {
            e[nrt] = A[nrt][p - 1];
        }
        e[p - 1] = 0.0;
        for (int j = nct; j < this.n; ++j) {
            for (int m = 0; m < this.m; ++m) {
                U[m][j] = 0.0;
            }
            U[j][j] = 1.0;
        }
        for (int k2 = nct - 1; k2 >= 0; --k2) {
            if (this.singularValues[k2] != 0.0) {
                for (int j2 = k2 + 1; j2 < this.n; ++j2) {
                    double t2 = 0.0;
                    for (int i2 = k2; i2 < this.m; ++i2) {
                        t2 += U[i2][k2] * U[i2][j2];
                    }
                    t2 = -t2 / U[k2][k2];
                    for (int i2 = k2; i2 < this.m; ++i2) {
                        final double[] array8 = U[i2];
                        final int n8 = j2;
                        array8[n8] += t2 * U[i2][k2];
                    }
                }
                for (int m = k2; m < this.m; ++m) {
                    U[m][k2] = -U[m][k2];
                }
                ++U[k2][k2];
                for (int m = 0; m < k2 - 1; ++m) {
                    U[m][k2] = 0.0;
                }
            }
            else {
                for (int m = 0; m < this.m; ++m) {
                    U[m][k2] = 0.0;
                }
                U[k2][k2] = 1.0;
            }
        }
        for (int k2 = this.n - 1; k2 >= 0; --k2) {
            if (k2 < nrt && e[k2] != 0.0) {
                for (int j2 = k2 + 1; j2 < this.n; ++j2) {
                    double t2 = 0.0;
                    for (int i2 = k2 + 1; i2 < this.n; ++i2) {
                        t2 += V[i2][k2] * V[i2][j2];
                    }
                    t2 = -t2 / V[k2 + 1][k2];
                    for (int i2 = k2 + 1; i2 < this.n; ++i2) {
                        final double[] array9 = V[i2];
                        final int n9 = j2;
                        array9[n9] += t2 * V[i2][k2];
                    }
                }
            }
            for (int m = 0; m < this.n; ++m) {
                V[m][k2] = 0.0;
            }
            V[k2][k2] = 1.0;
        }
        final int pp = p - 1;
        while (p > 0) {
            int k3;
            for (k3 = p - 2; k3 >= 0; --k3) {
                final double threshold = 1.6033346880071782E-291 + 2.220446049250313E-16 * (FastMath.abs(this.singularValues[k3]) + FastMath.abs(this.singularValues[k3 + 1]));
                if (FastMath.abs(e[k3]) <= threshold) {
                    e[k3] = 0.0;
                    break;
                }
            }
            int kase;
            if (k3 == p - 2) {
                kase = 4;
            }
            else {
                int ks;
                for (ks = p - 1; ks >= k3; --ks) {
                    if (ks == k3) {
                        break;
                    }
                    final double t3 = ((ks != p) ? FastMath.abs(e[ks]) : 0.0) + ((ks != k3 + 1) ? FastMath.abs(e[ks - 1]) : 0.0);
                    if (FastMath.abs(this.singularValues[ks]) <= 1.6033346880071782E-291 + 2.220446049250313E-16 * t3) {
                        this.singularValues[ks] = 0.0;
                        break;
                    }
                }
                if (ks == k3) {
                    kase = 3;
                }
                else if (ks == p - 1) {
                    kase = 1;
                }
                else {
                    kase = 2;
                    k3 = ks;
                }
            }
            ++k3;
            switch (kase) {
                case 1: {
                    double f = e[p - 2];
                    e[p - 2] = 0.0;
                    for (int j3 = p - 2; j3 >= k3; --j3) {
                        double t4 = FastMath.hypot(this.singularValues[j3], f);
                        final double cs = this.singularValues[j3] / t4;
                        final double sn = f / t4;
                        this.singularValues[j3] = t4;
                        if (j3 != k3) {
                            f = -sn * e[j3 - 1];
                            e[j3 - 1] *= cs;
                        }
                        for (int i3 = 0; i3 < this.n; ++i3) {
                            t4 = cs * V[i3][j3] + sn * V[i3][p - 1];
                            V[i3][p - 1] = -sn * V[i3][j3] + cs * V[i3][p - 1];
                            V[i3][j3] = t4;
                        }
                    }
                    continue;
                }
                case 2: {
                    double f = e[k3 - 1];
                    e[k3 - 1] = 0.0;
                    for (int j3 = k3; j3 < p; ++j3) {
                        double t4 = FastMath.hypot(this.singularValues[j3], f);
                        final double cs = this.singularValues[j3] / t4;
                        final double sn = f / t4;
                        this.singularValues[j3] = t4;
                        f = -sn * e[j3];
                        e[j3] *= cs;
                        for (int i3 = 0; i3 < this.m; ++i3) {
                            t4 = cs * U[i3][j3] + sn * U[i3][k3 - 1];
                            U[i3][k3 - 1] = -sn * U[i3][j3] + cs * U[i3][k3 - 1];
                            U[i3][j3] = t4;
                        }
                    }
                    continue;
                }
                case 3: {
                    final double maxPm1Pm2 = FastMath.max(FastMath.abs(this.singularValues[p - 1]), FastMath.abs(this.singularValues[p - 2]));
                    final double scale = FastMath.max(FastMath.max(FastMath.max(maxPm1Pm2, FastMath.abs(e[p - 2])), FastMath.abs(this.singularValues[k3])), FastMath.abs(e[k3]));
                    final double sp = this.singularValues[p - 1] / scale;
                    final double spm1 = this.singularValues[p - 2] / scale;
                    final double epm1 = e[p - 2] / scale;
                    final double sk = this.singularValues[k3] / scale;
                    final double ek = e[k3] / scale;
                    final double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0;
                    final double c = sp * epm1 * (sp * epm1);
                    double shift = 0.0;
                    if (b != 0.0 || c != 0.0) {
                        shift = FastMath.sqrt(b * b + c);
                        if (b < 0.0) {
                            shift = -shift;
                        }
                        shift = c / (b + shift);
                    }
                    double f2 = (sk + sp) * (sk - sp) + shift;
                    double g = sk * ek;
                    for (int j4 = k3; j4 < p - 1; ++j4) {
                        double t5 = FastMath.hypot(f2, g);
                        double cs2 = f2 / t5;
                        double sn2 = g / t5;
                        if (j4 != k3) {
                            e[j4 - 1] = t5;
                        }
                        f2 = cs2 * this.singularValues[j4] + sn2 * e[j4];
                        e[j4] = cs2 * e[j4] - sn2 * this.singularValues[j4];
                        g = sn2 * this.singularValues[j4 + 1];
                        this.singularValues[j4 + 1] *= cs2;
                        for (int i4 = 0; i4 < this.n; ++i4) {
                            t5 = cs2 * V[i4][j4] + sn2 * V[i4][j4 + 1];
                            V[i4][j4 + 1] = -sn2 * V[i4][j4] + cs2 * V[i4][j4 + 1];
                            V[i4][j4] = t5;
                        }
                        t5 = FastMath.hypot(f2, g);
                        cs2 = f2 / t5;
                        sn2 = g / t5;
                        this.singularValues[j4] = t5;
                        f2 = cs2 * e[j4] + sn2 * this.singularValues[j4 + 1];
                        this.singularValues[j4 + 1] = -sn2 * e[j4] + cs2 * this.singularValues[j4 + 1];
                        g = sn2 * e[j4 + 1];
                        e[j4 + 1] *= cs2;
                        if (j4 < this.m - 1) {
                            for (int i4 = 0; i4 < this.m; ++i4) {
                                t5 = cs2 * U[i4][j4] + sn2 * U[i4][j4 + 1];
                                U[i4][j4 + 1] = -sn2 * U[i4][j4] + cs2 * U[i4][j4 + 1];
                                U[i4][j4] = t5;
                            }
                        }
                    }
                    e[p - 2] = f2;
                    continue;
                }
                default: {
                    if (this.singularValues[k3] <= 0.0) {
                        this.singularValues[k3] = ((this.singularValues[k3] < 0.0) ? (-this.singularValues[k3]) : 0.0);
                        for (int l = 0; l <= pp; ++l) {
                            V[l][k3] = -V[l][k3];
                        }
                    }
                    while (k3 < pp && this.singularValues[k3] < this.singularValues[k3 + 1]) {
                        double t6 = this.singularValues[k3];
                        this.singularValues[k3] = this.singularValues[k3 + 1];
                        this.singularValues[k3 + 1] = t6;
                        if (k3 < this.n - 1) {
                            for (int i5 = 0; i5 < this.n; ++i5) {
                                t6 = V[i5][k3 + 1];
                                V[i5][k3 + 1] = V[i5][k3];
                                V[i5][k3] = t6;
                            }
                        }
                        if (k3 < this.m - 1) {
                            for (int i5 = 0; i5 < this.m; ++i5) {
                                t6 = U[i5][k3 + 1];
                                U[i5][k3 + 1] = U[i5][k3];
                                U[i5][k3] = t6;
                            }
                        }
                        ++k3;
                    }
                    --p;
                    continue;
                }
            }
        }
        this.tol = FastMath.max(this.m * this.singularValues[0] * 2.220446049250313E-16, FastMath.sqrt(Precision.SAFE_MIN));
        if (!this.transposed) {
            this.cachedU = MatrixUtils.createRealMatrix(U);
            this.cachedV = MatrixUtils.createRealMatrix(V);
        }
        else {
            this.cachedU = MatrixUtils.createRealMatrix(V);
            this.cachedV = MatrixUtils.createRealMatrix(U);
        }
    }
    
    public RealMatrix getU() {
        return this.cachedU;
    }
    
    public RealMatrix getUT() {
        if (this.cachedUt == null) {
            this.cachedUt = this.getU().transpose();
        }
        return this.cachedUt;
    }
    
    public RealMatrix getS() {
        if (this.cachedS == null) {
            this.cachedS = MatrixUtils.createRealDiagonalMatrix(this.singularValues);
        }
        return this.cachedS;
    }
    
    public double[] getSingularValues() {
        return this.singularValues.clone();
    }
    
    public RealMatrix getV() {
        return this.cachedV;
    }
    
    public RealMatrix getVT() {
        if (this.cachedVt == null) {
            this.cachedVt = this.getV().transpose();
        }
        return this.cachedVt;
    }
    
    public RealMatrix getCovariance(final double minSingularValue) {
        int p;
        int dimension;
        for (p = this.singularValues.length, dimension = 0; dimension < p && this.singularValues[dimension] >= minSingularValue; ++dimension) {}
        if (dimension == 0) {
            throw new NumberIsTooLargeException(LocalizedFormats.TOO_LARGE_CUTOFF_SINGULAR_VALUE, minSingularValue, this.singularValues[0], true);
        }
        final double[][] data = new double[dimension][p];
        this.getVT().walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            @Override
            public void visit(final int row, final int column, final double value) {
                data[row][column] = value / SingularValueDecomposition.this.singularValues[row];
            }
        }, 0, dimension - 1, 0, p - 1);
        final RealMatrix jv = new Array2DRowRealMatrix(data, false);
        return jv.transpose().multiply(jv);
    }
    
    public double getNorm() {
        return this.singularValues[0];
    }
    
    public double getConditionNumber() {
        return this.singularValues[0] / this.singularValues[this.n - 1];
    }
    
    public double getInverseConditionNumber() {
        return this.singularValues[this.n - 1] / this.singularValues[0];
    }
    
    public int getRank() {
        int r = 0;
        for (int i = 0; i < this.singularValues.length; ++i) {
            if (this.singularValues[i] > this.tol) {
                ++r;
            }
        }
        return r;
    }
    
    public DecompositionSolver getSolver() {
        return new Solver(this.singularValues, this.getUT(), this.getV(), this.getRank() == this.m, this.tol);
    }
    
    private static class Solver implements DecompositionSolver
    {
        private final RealMatrix pseudoInverse;
        private boolean nonSingular;
        
        private Solver(final double[] singularValues, final RealMatrix uT, final RealMatrix v, final boolean nonSingular, final double tol) {
            final double[][] suT = uT.getData();
            for (int i = 0; i < singularValues.length; ++i) {
                double a;
                if (singularValues[i] > tol) {
                    a = 1.0 / singularValues[i];
                }
                else {
                    a = 0.0;
                }
                final double[] suTi = suT[i];
                for (int j = 0; j < suTi.length; ++j) {
                    final double[] array = suTi;
                    final int n = j;
                    array[n] *= a;
                }
            }
            this.pseudoInverse = v.multiply(new Array2DRowRealMatrix(suT, false));
            this.nonSingular = nonSingular;
        }
        
        public RealVector solve(final RealVector b) {
            return this.pseudoInverse.operate(b);
        }
        
        public RealMatrix solve(final RealMatrix b) {
            return this.pseudoInverse.multiply(b);
        }
        
        public boolean isNonSingular() {
            return this.nonSingular;
        }
        
        public RealMatrix getInverse() {
            return this.pseudoInverse;
        }
    }
}
