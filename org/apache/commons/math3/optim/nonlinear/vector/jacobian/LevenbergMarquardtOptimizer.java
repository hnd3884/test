package org.apache.commons.math3.optim.nonlinear.vector.jacobian;

import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import java.util.Arrays;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Precision;

@Deprecated
public class LevenbergMarquardtOptimizer extends AbstractLeastSquaresOptimizer
{
    private static final double TWO_EPS;
    private int solvedCols;
    private double[] diagR;
    private double[] jacNorm;
    private double[] beta;
    private int[] permutation;
    private int rank;
    private double lmPar;
    private double[] lmDir;
    private final double initialStepBoundFactor;
    private final double costRelativeTolerance;
    private final double parRelativeTolerance;
    private final double orthoTolerance;
    private final double qrRankingThreshold;
    private double[] weightedResidual;
    private double[][] weightedJacobian;
    
    public LevenbergMarquardtOptimizer() {
        this(100.0, 1.0E-10, 1.0E-10, 1.0E-10, Precision.SAFE_MIN);
    }
    
    public LevenbergMarquardtOptimizer(final ConvergenceChecker<PointVectorValuePair> checker) {
        this(100.0, checker, 1.0E-10, 1.0E-10, 1.0E-10, Precision.SAFE_MIN);
    }
    
    public LevenbergMarquardtOptimizer(final double initialStepBoundFactor, final ConvergenceChecker<PointVectorValuePair> checker, final double costRelativeTolerance, final double parRelativeTolerance, final double orthoTolerance, final double threshold) {
        super(checker);
        this.initialStepBoundFactor = initialStepBoundFactor;
        this.costRelativeTolerance = costRelativeTolerance;
        this.parRelativeTolerance = parRelativeTolerance;
        this.orthoTolerance = orthoTolerance;
        this.qrRankingThreshold = threshold;
    }
    
    public LevenbergMarquardtOptimizer(final double costRelativeTolerance, final double parRelativeTolerance, final double orthoTolerance) {
        this(100.0, costRelativeTolerance, parRelativeTolerance, orthoTolerance, Precision.SAFE_MIN);
    }
    
    public LevenbergMarquardtOptimizer(final double initialStepBoundFactor, final double costRelativeTolerance, final double parRelativeTolerance, final double orthoTolerance, final double threshold) {
        super(null);
        this.initialStepBoundFactor = initialStepBoundFactor;
        this.costRelativeTolerance = costRelativeTolerance;
        this.parRelativeTolerance = parRelativeTolerance;
        this.orthoTolerance = orthoTolerance;
        this.qrRankingThreshold = threshold;
    }
    
    @Override
    protected PointVectorValuePair doOptimize() {
        this.checkParameters();
        final int nR = this.getTarget().length;
        final double[] currentPoint = this.getStartPoint();
        final int nC = currentPoint.length;
        this.solvedCols = FastMath.min(nR, nC);
        this.diagR = new double[nC];
        this.jacNorm = new double[nC];
        this.beta = new double[nC];
        this.permutation = new int[nC];
        this.lmDir = new double[nC];
        double delta = 0.0;
        double xNorm = 0.0;
        final double[] diag = new double[nC];
        final double[] oldX = new double[nC];
        double[] oldRes = new double[nR];
        double[] oldObj = new double[nR];
        final double[] qtf = new double[nR];
        final double[] work1 = new double[nC];
        final double[] work2 = new double[nC];
        final double[] work3 = new double[nC];
        final RealMatrix weightMatrixSqrt = this.getWeightSquareRoot();
        double[] currentObjective = this.computeObjectiveValue(currentPoint);
        double[] currentResiduals = this.computeResiduals(currentObjective);
        PointVectorValuePair current = new PointVectorValuePair(currentPoint, currentObjective);
        double currentCost = this.computeCost(currentResiduals);
        this.lmPar = 0.0;
        boolean firstIteration = true;
        final ConvergenceChecker<PointVectorValuePair> checker = this.getConvergenceChecker();
        while (true) {
            this.incrementIterationCount();
            final PointVectorValuePair previous = current;
            this.qrDecomposition(this.computeWeightedJacobian(currentPoint));
            this.weightedResidual = weightMatrixSqrt.operate(currentResiduals);
            for (int i = 0; i < nR; ++i) {
                qtf[i] = this.weightedResidual[i];
            }
            this.qTy(qtf);
            for (int k = 0; k < this.solvedCols; ++k) {
                final int pk = this.permutation[k];
                this.weightedJacobian[k][pk] = this.diagR[pk];
            }
            if (firstIteration) {
                xNorm = 0.0;
                for (int k = 0; k < nC; ++k) {
                    double dk = this.jacNorm[k];
                    if (dk == 0.0) {
                        dk = 1.0;
                    }
                    final double xk = dk * currentPoint[k];
                    xNorm += xk * xk;
                    diag[k] = dk;
                }
                xNorm = FastMath.sqrt(xNorm);
                delta = ((xNorm == 0.0) ? this.initialStepBoundFactor : (this.initialStepBoundFactor * xNorm));
            }
            double maxCosine = 0.0;
            if (currentCost != 0.0) {
                for (int j = 0; j < this.solvedCols; ++j) {
                    final int pj = this.permutation[j];
                    final double s = this.jacNorm[pj];
                    if (s != 0.0) {
                        double sum = 0.0;
                        for (int l = 0; l <= j; ++l) {
                            sum += this.weightedJacobian[l][pj] * qtf[l];
                        }
                        maxCosine = FastMath.max(maxCosine, FastMath.abs(sum) / (s * currentCost));
                    }
                }
            }
            if (maxCosine <= this.orthoTolerance) {
                this.setCost(currentCost);
                return current;
            }
            for (int j = 0; j < nC; ++j) {
                diag[j] = FastMath.max(diag[j], this.jacNorm[j]);
            }
            double ratio = 0.0;
            while (ratio < 1.0E-4) {
                for (int m = 0; m < this.solvedCols; ++m) {
                    final int pj2 = this.permutation[m];
                    oldX[pj2] = currentPoint[pj2];
                }
                final double previousCost = currentCost;
                double[] tmpVec = this.weightedResidual;
                this.weightedResidual = oldRes;
                oldRes = tmpVec;
                tmpVec = currentObjective;
                currentObjective = oldObj;
                oldObj = tmpVec;
                this.determineLMParameter(qtf, delta, diag, work1, work2, work3);
                double lmNorm = 0.0;
                for (int j2 = 0; j2 < this.solvedCols; ++j2) {
                    final int pj3 = this.permutation[j2];
                    this.lmDir[pj3] = -this.lmDir[pj3];
                    currentPoint[pj3] = oldX[pj3] + this.lmDir[pj3];
                    final double s2 = diag[pj3] * this.lmDir[pj3];
                    lmNorm += s2 * s2;
                }
                lmNorm = FastMath.sqrt(lmNorm);
                if (firstIteration) {
                    delta = FastMath.min(delta, lmNorm);
                }
                currentObjective = this.computeObjectiveValue(currentPoint);
                currentResiduals = this.computeResiduals(currentObjective);
                current = new PointVectorValuePair(currentPoint, currentObjective);
                currentCost = this.computeCost(currentResiduals);
                double actRed = -1.0;
                if (0.1 * currentCost < previousCost) {
                    final double r = currentCost / previousCost;
                    actRed = 1.0 - r * r;
                }
                for (int j3 = 0; j3 < this.solvedCols; ++j3) {
                    final int pj4 = this.permutation[j3];
                    final double dirJ = this.lmDir[pj4];
                    work1[j3] = 0.0;
                    for (int i2 = 0; i2 <= j3; ++i2) {
                        final double[] array = work1;
                        final int n = i2;
                        array[n] += this.weightedJacobian[i2][pj4] * dirJ;
                    }
                }
                double coeff1 = 0.0;
                for (int j4 = 0; j4 < this.solvedCols; ++j4) {
                    coeff1 += work1[j4] * work1[j4];
                }
                final double pc2 = previousCost * previousCost;
                coeff1 /= pc2;
                final double coeff2 = this.lmPar * lmNorm * lmNorm / pc2;
                final double preRed = coeff1 + 2.0 * coeff2;
                final double dirDer = -(coeff1 + coeff2);
                ratio = ((preRed == 0.0) ? 0.0 : (actRed / preRed));
                if (ratio <= 0.25) {
                    double tmp = (actRed < 0.0) ? (0.5 * dirDer / (dirDer + 0.5 * actRed)) : 0.5;
                    if (0.1 * currentCost >= previousCost || tmp < 0.1) {
                        tmp = 0.1;
                    }
                    delta = tmp * FastMath.min(delta, 10.0 * lmNorm);
                    this.lmPar /= tmp;
                }
                else if (this.lmPar == 0.0 || ratio >= 0.75) {
                    delta = 2.0 * lmNorm;
                    this.lmPar *= 0.5;
                }
                if (ratio >= 1.0E-4) {
                    firstIteration = false;
                    xNorm = 0.0;
                    for (int k2 = 0; k2 < nC; ++k2) {
                        final double xK = diag[k2] * currentPoint[k2];
                        xNorm += xK * xK;
                    }
                    xNorm = FastMath.sqrt(xNorm);
                    if (checker != null && checker.converged(this.getIterations(), previous, current)) {
                        this.setCost(currentCost);
                        return current;
                    }
                }
                else {
                    currentCost = previousCost;
                    for (int j5 = 0; j5 < this.solvedCols; ++j5) {
                        final int pj5 = this.permutation[j5];
                        currentPoint[pj5] = oldX[pj5];
                    }
                    tmpVec = this.weightedResidual;
                    this.weightedResidual = oldRes;
                    oldRes = tmpVec;
                    tmpVec = currentObjective;
                    currentObjective = oldObj;
                    oldObj = tmpVec;
                    current = new PointVectorValuePair(currentPoint, currentObjective);
                }
                if ((FastMath.abs(actRed) <= this.costRelativeTolerance && preRed <= this.costRelativeTolerance && ratio <= 2.0) || delta <= this.parRelativeTolerance * xNorm) {
                    this.setCost(currentCost);
                    return current;
                }
                if (FastMath.abs(actRed) <= LevenbergMarquardtOptimizer.TWO_EPS && preRed <= LevenbergMarquardtOptimizer.TWO_EPS && ratio <= 2.0) {
                    throw new ConvergenceException(LocalizedFormats.TOO_SMALL_COST_RELATIVE_TOLERANCE, new Object[] { this.costRelativeTolerance });
                }
                if (delta <= LevenbergMarquardtOptimizer.TWO_EPS * xNorm) {
                    throw new ConvergenceException(LocalizedFormats.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE, new Object[] { this.parRelativeTolerance });
                }
                if (maxCosine <= LevenbergMarquardtOptimizer.TWO_EPS) {
                    throw new ConvergenceException(LocalizedFormats.TOO_SMALL_ORTHOGONALITY_TOLERANCE, new Object[] { this.orthoTolerance });
                }
            }
        }
    }
    
    private void determineLMParameter(final double[] qy, final double delta, final double[] diag, final double[] work1, final double[] work2, final double[] work3) {
        final int nC = this.weightedJacobian[0].length;
        for (int j = 0; j < this.rank; ++j) {
            this.lmDir[this.permutation[j]] = qy[j];
        }
        for (int j = this.rank; j < nC; ++j) {
            this.lmDir[this.permutation[j]] = 0.0;
        }
        for (int k = this.rank - 1; k >= 0; --k) {
            final int pk = this.permutation[k];
            final double ypk = this.lmDir[pk] / this.diagR[pk];
            for (int i = 0; i < k; ++i) {
                final double[] lmDir = this.lmDir;
                final int n = this.permutation[i];
                lmDir[n] -= ypk * this.weightedJacobian[i][pk];
            }
            this.lmDir[pk] = ypk;
        }
        double dxNorm = 0.0;
        for (int l = 0; l < this.solvedCols; ++l) {
            final int pj = this.permutation[l];
            final double s = diag[pj] * this.lmDir[pj];
            work1[pj] = s;
            dxNorm += s * s;
        }
        dxNorm = FastMath.sqrt(dxNorm);
        double fp = dxNorm - delta;
        if (fp <= 0.1 * delta) {
            this.lmPar = 0.0;
            return;
        }
        double parl = 0.0;
        if (this.rank == this.solvedCols) {
            for (int m = 0; m < this.solvedCols; ++m) {
                final int n2;
                final int pj2 = n2 = this.permutation[m];
                work1[n2] *= diag[pj2] / dxNorm;
            }
            double sum2 = 0.0;
            for (int m = 0; m < this.solvedCols; ++m) {
                final int pj2 = this.permutation[m];
                double sum3 = 0.0;
                for (int i2 = 0; i2 < m; ++i2) {
                    sum3 += this.weightedJacobian[i2][pj2] * work1[this.permutation[i2]];
                }
                final double s2 = (work1[pj2] - sum3) / this.diagR[pj2];
                work1[pj2] = s2;
                sum2 += s2 * s2;
            }
            parl = fp / (delta * sum2);
        }
        double sum2 = 0.0;
        for (int m = 0; m < this.solvedCols; ++m) {
            final int pj2 = this.permutation[m];
            double sum3 = 0.0;
            for (int i2 = 0; i2 <= m; ++i2) {
                sum3 += this.weightedJacobian[i2][pj2] * qy[i2];
            }
            sum3 /= diag[pj2];
            sum2 += sum3 * sum3;
        }
        final double gNorm = FastMath.sqrt(sum2);
        double paru = gNorm / delta;
        if (paru == 0.0) {
            paru = Precision.SAFE_MIN / FastMath.min(delta, 0.1);
        }
        this.lmPar = FastMath.min(paru, FastMath.max(this.lmPar, parl));
        if (this.lmPar == 0.0) {
            this.lmPar = gNorm / dxNorm;
        }
        for (int countdown = 10; countdown >= 0; --countdown) {
            if (this.lmPar == 0.0) {
                this.lmPar = FastMath.max(Precision.SAFE_MIN, 0.001 * paru);
            }
            final double sPar = FastMath.sqrt(this.lmPar);
            for (int j2 = 0; j2 < this.solvedCols; ++j2) {
                final int pj3 = this.permutation[j2];
                work1[pj3] = sPar * diag[pj3];
            }
            this.determineLMDirection(qy, work1, work2, work3);
            dxNorm = 0.0;
            for (int j2 = 0; j2 < this.solvedCols; ++j2) {
                final int pj3 = this.permutation[j2];
                final double s3 = diag[pj3] * this.lmDir[pj3];
                work3[pj3] = s3;
                dxNorm += s3 * s3;
            }
            dxNorm = FastMath.sqrt(dxNorm);
            final double previousFP = fp;
            fp = dxNorm - delta;
            if (FastMath.abs(fp) <= 0.1 * delta || (parl == 0.0 && fp <= previousFP && previousFP < 0.0)) {
                return;
            }
            for (int j3 = 0; j3 < this.solvedCols; ++j3) {
                final int pj4 = this.permutation[j3];
                work1[pj4] = work3[pj4] * diag[pj4] / dxNorm;
            }
            for (int j3 = 0; j3 < this.solvedCols; ++j3) {
                final int n3;
                final int pj4 = n3 = this.permutation[j3];
                work1[n3] /= work2[j3];
                final double tmp = work1[pj4];
                for (int i3 = j3 + 1; i3 < this.solvedCols; ++i3) {
                    final int n4 = this.permutation[i3];
                    work1[n4] -= this.weightedJacobian[i3][pj4] * tmp;
                }
            }
            sum2 = 0.0;
            for (int j3 = 0; j3 < this.solvedCols; ++j3) {
                final double s4 = work1[this.permutation[j3]];
                sum2 += s4 * s4;
            }
            final double correction = fp / (delta * sum2);
            if (fp > 0.0) {
                parl = FastMath.max(parl, this.lmPar);
            }
            else if (fp < 0.0) {
                paru = FastMath.min(paru, this.lmPar);
            }
            this.lmPar = FastMath.max(parl, this.lmPar + correction);
        }
    }
    
    private void determineLMDirection(final double[] qy, final double[] diag, final double[] lmDiag, final double[] work) {
        for (int j = 0; j < this.solvedCols; ++j) {
            final int pj = this.permutation[j];
            for (int i = j + 1; i < this.solvedCols; ++i) {
                this.weightedJacobian[i][pj] = this.weightedJacobian[j][this.permutation[i]];
            }
            this.lmDir[j] = this.diagR[pj];
            work[j] = qy[j];
        }
        for (int j = 0; j < this.solvedCols; ++j) {
            final int pj = this.permutation[j];
            final double dpj = diag[pj];
            if (dpj != 0.0) {
                Arrays.fill(lmDiag, j + 1, lmDiag.length, 0.0);
            }
            lmDiag[j] = dpj;
            double qtbpj = 0.0;
            for (int k = j; k < this.solvedCols; ++k) {
                final int pk = this.permutation[k];
                if (lmDiag[k] != 0.0) {
                    final double rkk = this.weightedJacobian[k][pk];
                    double sin;
                    double cos;
                    if (FastMath.abs(rkk) < FastMath.abs(lmDiag[k])) {
                        final double cotan = rkk / lmDiag[k];
                        sin = 1.0 / FastMath.sqrt(1.0 + cotan * cotan);
                        cos = sin * cotan;
                    }
                    else {
                        final double tan = lmDiag[k] / rkk;
                        cos = 1.0 / FastMath.sqrt(1.0 + tan * tan);
                        sin = cos * tan;
                    }
                    this.weightedJacobian[k][pk] = cos * rkk + sin * lmDiag[k];
                    final double temp = cos * work[k] + sin * qtbpj;
                    qtbpj = -sin * work[k] + cos * qtbpj;
                    work[k] = temp;
                    for (int l = k + 1; l < this.solvedCols; ++l) {
                        final double rik = this.weightedJacobian[l][pk];
                        final double temp2 = cos * rik + sin * lmDiag[l];
                        lmDiag[l] = -sin * rik + cos * lmDiag[l];
                        this.weightedJacobian[l][pk] = temp2;
                    }
                }
            }
            lmDiag[j] = this.weightedJacobian[j][this.permutation[j]];
            this.weightedJacobian[j][this.permutation[j]] = this.lmDir[j];
        }
        int nSing = this.solvedCols;
        for (int m = 0; m < this.solvedCols; ++m) {
            if (lmDiag[m] == 0.0 && nSing == this.solvedCols) {
                nSing = m;
            }
            if (nSing < this.solvedCols) {
                work[m] = 0.0;
            }
        }
        if (nSing > 0) {
            for (int m = nSing - 1; m >= 0; --m) {
                final int pj2 = this.permutation[m];
                double sum = 0.0;
                for (int i2 = m + 1; i2 < nSing; ++i2) {
                    sum += this.weightedJacobian[i2][pj2] * work[i2];
                }
                work[m] = (work[m] - sum) / lmDiag[m];
            }
        }
        for (int m = 0; m < this.lmDir.length; ++m) {
            this.lmDir[this.permutation[m]] = work[m];
        }
    }
    
    private void qrDecomposition(final RealMatrix jacobian) throws ConvergenceException {
        this.weightedJacobian = jacobian.scalarMultiply(-1.0).getData();
        final int nR = this.weightedJacobian.length;
        final int nC = this.weightedJacobian[0].length;
        for (int k = 0; k < nC; ++k) {
            this.permutation[k] = k;
            double norm2 = 0.0;
            for (int i = 0; i < nR; ++i) {
                final double akk = this.weightedJacobian[i][k];
                norm2 += akk * akk;
            }
            this.jacNorm[k] = FastMath.sqrt(norm2);
        }
        for (int k = 0; k < nC; ++k) {
            int nextColumn = -1;
            double ak2 = Double.NEGATIVE_INFINITY;
            for (int j = k; j < nC; ++j) {
                double norm3 = 0.0;
                for (int l = k; l < nR; ++l) {
                    final double aki = this.weightedJacobian[l][this.permutation[j]];
                    norm3 += aki * aki;
                }
                if (Double.isInfinite(norm3) || Double.isNaN(norm3)) {
                    throw new ConvergenceException(LocalizedFormats.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, new Object[] { nR, nC });
                }
                if (norm3 > ak2) {
                    nextColumn = j;
                    ak2 = norm3;
                }
            }
            if (ak2 <= this.qrRankingThreshold) {
                this.rank = k;
                return;
            }
            final int pk = this.permutation[nextColumn];
            this.permutation[nextColumn] = this.permutation[k];
            this.permutation[k] = pk;
            final double akk2 = this.weightedJacobian[k][pk];
            final double alpha = (akk2 > 0.0) ? (-FastMath.sqrt(ak2)) : FastMath.sqrt(ak2);
            final double betak = 1.0 / (ak2 - akk2 * alpha);
            this.beta[pk] = betak;
            this.diagR[pk] = alpha;
            final double[] array = this.weightedJacobian[k];
            final int n = pk;
            array[n] -= alpha;
            for (int dk = nC - 1 - k; dk > 0; --dk) {
                double gamma = 0.0;
                for (int m = k; m < nR; ++m) {
                    gamma += this.weightedJacobian[m][pk] * this.weightedJacobian[m][this.permutation[k + dk]];
                }
                gamma *= betak;
                for (int m = k; m < nR; ++m) {
                    final double[] array2 = this.weightedJacobian[m];
                    final int n2 = this.permutation[k + dk];
                    array2[n2] -= gamma * this.weightedJacobian[m][pk];
                }
            }
        }
        this.rank = this.solvedCols;
    }
    
    private void qTy(final double[] y) {
        final int nR = this.weightedJacobian.length;
        for (int nC = this.weightedJacobian[0].length, k = 0; k < nC; ++k) {
            final int pk = this.permutation[k];
            double gamma = 0.0;
            for (int i = k; i < nR; ++i) {
                gamma += this.weightedJacobian[i][pk] * y[i];
            }
            gamma *= this.beta[pk];
            for (int i = k; i < nR; ++i) {
                final int n = i;
                y[n] -= gamma * this.weightedJacobian[i][pk];
            }
        }
    }
    
    private void checkParameters() {
        if (this.getLowerBound() != null || this.getUpperBound() != null) {
            throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT, new Object[0]);
        }
    }
    
    static {
        TWO_EPS = 2.0 * Precision.EPSILON;
    }
}
