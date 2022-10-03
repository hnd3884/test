package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;
import java.util.Arrays;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class LevenbergMarquardtOptimizer implements LeastSquaresOptimizer
{
    private static final double TWO_EPS;
    private final double initialStepBoundFactor;
    private final double costRelativeTolerance;
    private final double parRelativeTolerance;
    private final double orthoTolerance;
    private final double qrRankingThreshold;
    
    public LevenbergMarquardtOptimizer() {
        this(100.0, 1.0E-10, 1.0E-10, 1.0E-10, Precision.SAFE_MIN);
    }
    
    public LevenbergMarquardtOptimizer(final double initialStepBoundFactor, final double costRelativeTolerance, final double parRelativeTolerance, final double orthoTolerance, final double qrRankingThreshold) {
        this.initialStepBoundFactor = initialStepBoundFactor;
        this.costRelativeTolerance = costRelativeTolerance;
        this.parRelativeTolerance = parRelativeTolerance;
        this.orthoTolerance = orthoTolerance;
        this.qrRankingThreshold = qrRankingThreshold;
    }
    
    public LevenbergMarquardtOptimizer withInitialStepBoundFactor(final double newInitialStepBoundFactor) {
        return new LevenbergMarquardtOptimizer(newInitialStepBoundFactor, this.costRelativeTolerance, this.parRelativeTolerance, this.orthoTolerance, this.qrRankingThreshold);
    }
    
    public LevenbergMarquardtOptimizer withCostRelativeTolerance(final double newCostRelativeTolerance) {
        return new LevenbergMarquardtOptimizer(this.initialStepBoundFactor, newCostRelativeTolerance, this.parRelativeTolerance, this.orthoTolerance, this.qrRankingThreshold);
    }
    
    public LevenbergMarquardtOptimizer withParameterRelativeTolerance(final double newParRelativeTolerance) {
        return new LevenbergMarquardtOptimizer(this.initialStepBoundFactor, this.costRelativeTolerance, newParRelativeTolerance, this.orthoTolerance, this.qrRankingThreshold);
    }
    
    public LevenbergMarquardtOptimizer withOrthoTolerance(final double newOrthoTolerance) {
        return new LevenbergMarquardtOptimizer(this.initialStepBoundFactor, this.costRelativeTolerance, this.parRelativeTolerance, newOrthoTolerance, this.qrRankingThreshold);
    }
    
    public LevenbergMarquardtOptimizer withRankingThreshold(final double newQRRankingThreshold) {
        return new LevenbergMarquardtOptimizer(this.initialStepBoundFactor, this.costRelativeTolerance, this.parRelativeTolerance, this.orthoTolerance, newQRRankingThreshold);
    }
    
    public double getInitialStepBoundFactor() {
        return this.initialStepBoundFactor;
    }
    
    public double getCostRelativeTolerance() {
        return this.costRelativeTolerance;
    }
    
    public double getParameterRelativeTolerance() {
        return this.parRelativeTolerance;
    }
    
    public double getOrthoTolerance() {
        return this.orthoTolerance;
    }
    
    public double getRankingThreshold() {
        return this.qrRankingThreshold;
    }
    
    public Optimum optimize(final LeastSquaresProblem problem) {
        final int nR = problem.getObservationSize();
        final int nC = problem.getParameterSize();
        final Incrementor iterationCounter = problem.getIterationCounter();
        final Incrementor evaluationCounter = problem.getEvaluationCounter();
        final ConvergenceChecker<LeastSquaresProblem.Evaluation> checker = problem.getConvergenceChecker();
        final int solvedCols = FastMath.min(nR, nC);
        final double[] lmDir = new double[nC];
        double lmPar = 0.0;
        double delta = 0.0;
        double xNorm = 0.0;
        final double[] diag = new double[nC];
        final double[] oldX = new double[nC];
        double[] oldRes = new double[nR];
        final double[] qtf = new double[nR];
        final double[] work1 = new double[nC];
        final double[] work2 = new double[nC];
        final double[] work3 = new double[nC];
        evaluationCounter.incrementCount();
        LeastSquaresProblem.Evaluation current = problem.evaluate(problem.getStart());
        double[] currentResiduals = current.getResiduals().toArray();
        double currentCost = current.getCost();
        double[] currentPoint = current.getPoint().toArray();
        boolean firstIteration = true;
        while (true) {
            iterationCounter.incrementCount();
            final LeastSquaresProblem.Evaluation previous = current;
            final InternalData internalData = this.qrDecomposition(current.getJacobian(), solvedCols);
            final double[][] weightedJacobian = internalData.weightedJacobian;
            final int[] permutation = internalData.permutation;
            final double[] diagR = internalData.diagR;
            final double[] jacNorm = internalData.jacNorm;
            double[] weightedResidual = currentResiduals;
            for (int i = 0; i < nR; ++i) {
                qtf[i] = weightedResidual[i];
            }
            this.qTy(qtf, internalData);
            for (int k = 0; k < solvedCols; ++k) {
                final int pk = permutation[k];
                weightedJacobian[k][pk] = diagR[pk];
            }
            if (firstIteration) {
                xNorm = 0.0;
                for (int k = 0; k < nC; ++k) {
                    double dk = jacNorm[k];
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
                for (int j = 0; j < solvedCols; ++j) {
                    final int pj = permutation[j];
                    final double s = jacNorm[pj];
                    if (s != 0.0) {
                        double sum = 0.0;
                        for (int l = 0; l <= j; ++l) {
                            sum += weightedJacobian[l][pj] * qtf[l];
                        }
                        maxCosine = FastMath.max(maxCosine, FastMath.abs(sum) / (s * currentCost));
                    }
                }
            }
            if (maxCosine <= this.orthoTolerance) {
                return new OptimumImpl(current, evaluationCounter.getCount(), iterationCounter.getCount());
            }
            for (int j = 0; j < nC; ++j) {
                diag[j] = FastMath.max(diag[j], jacNorm[j]);
            }
            double ratio = 0.0;
            while (ratio < 1.0E-4) {
                for (final int pj2 : permutation) {
                    oldX[pj2] = currentPoint[pj2];
                }
                final double previousCost = currentCost;
                double[] tmpVec = weightedResidual;
                weightedResidual = oldRes;
                oldRes = tmpVec;
                lmPar = this.determineLMParameter(qtf, delta, diag, internalData, solvedCols, work1, work2, work3, lmDir, lmPar);
                double lmNorm = 0.0;
                for (final int pj3 : permutation) {
                    lmDir[pj3] = -lmDir[pj3];
                    currentPoint[pj3] = oldX[pj3] + lmDir[pj3];
                    final double s2 = diag[pj3] * lmDir[pj3];
                    lmNorm += s2 * s2;
                }
                lmNorm = FastMath.sqrt(lmNorm);
                if (firstIteration) {
                    delta = FastMath.min(delta, lmNorm);
                }
                evaluationCounter.incrementCount();
                current = problem.evaluate(new ArrayRealVector(currentPoint));
                currentResiduals = current.getResiduals().toArray();
                currentCost = current.getCost();
                currentPoint = current.getPoint().toArray();
                double actRed = -1.0;
                if (0.1 * currentCost < previousCost) {
                    final double r = currentCost / previousCost;
                    actRed = 1.0 - r * r;
                }
                for (int j3 = 0; j3 < solvedCols; ++j3) {
                    final int pj4 = permutation[j3];
                    final double dirJ = lmDir[pj4];
                    work1[j3] = 0.0;
                    for (int i2 = 0; i2 <= j3; ++i2) {
                        final double[] array = work1;
                        final int n = i2;
                        array[n] += weightedJacobian[i2][pj4] * dirJ;
                    }
                }
                double coeff1 = 0.0;
                for (int j4 = 0; j4 < solvedCols; ++j4) {
                    coeff1 += work1[j4] * work1[j4];
                }
                final double pc2 = previousCost * previousCost;
                coeff1 /= pc2;
                final double coeff2 = lmPar * lmNorm * lmNorm / pc2;
                final double preRed = coeff1 + 2.0 * coeff2;
                final double dirDer = -(coeff1 + coeff2);
                ratio = ((preRed == 0.0) ? 0.0 : (actRed / preRed));
                if (ratio <= 0.25) {
                    double tmp = (actRed < 0.0) ? (0.5 * dirDer / (dirDer + 0.5 * actRed)) : 0.5;
                    if (0.1 * currentCost >= previousCost || tmp < 0.1) {
                        tmp = 0.1;
                    }
                    delta = tmp * FastMath.min(delta, 10.0 * lmNorm);
                    lmPar /= tmp;
                }
                else if (lmPar == 0.0 || ratio >= 0.75) {
                    delta = 2.0 * lmNorm;
                    lmPar *= 0.5;
                }
                if (ratio >= 1.0E-4) {
                    firstIteration = false;
                    xNorm = 0.0;
                    for (int k2 = 0; k2 < nC; ++k2) {
                        final double xK = diag[k2] * currentPoint[k2];
                        xNorm += xK * xK;
                    }
                    xNorm = FastMath.sqrt(xNorm);
                    if (checker != null && checker.converged(iterationCounter.getCount(), previous, current)) {
                        return new OptimumImpl(current, evaluationCounter.getCount(), iterationCounter.getCount());
                    }
                }
                else {
                    currentCost = previousCost;
                    for (final int pj5 : permutation) {
                        currentPoint[pj5] = oldX[pj5];
                    }
                    tmpVec = weightedResidual;
                    weightedResidual = oldRes;
                    oldRes = tmpVec;
                    current = previous;
                }
                if ((FastMath.abs(actRed) <= this.costRelativeTolerance && preRed <= this.costRelativeTolerance && ratio <= 2.0) || delta <= this.parRelativeTolerance * xNorm) {
                    return new OptimumImpl(current, evaluationCounter.getCount(), iterationCounter.getCount());
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
    
    private double determineLMParameter(final double[] qy, final double delta, final double[] diag, final InternalData internalData, final int solvedCols, final double[] work1, final double[] work2, final double[] work3, final double[] lmDir, double lmPar) {
        final double[][] weightedJacobian = internalData.weightedJacobian;
        final int[] permutation = internalData.permutation;
        final int rank = internalData.rank;
        final double[] diagR = internalData.diagR;
        final int nC = weightedJacobian[0].length;
        for (int j = 0; j < rank; ++j) {
            lmDir[permutation[j]] = qy[j];
        }
        for (int j = rank; j < nC; ++j) {
            lmDir[permutation[j]] = 0.0;
        }
        for (int k = rank - 1; k >= 0; --k) {
            final int pk = permutation[k];
            final double ypk = lmDir[pk] / diagR[pk];
            for (int i = 0; i < k; ++i) {
                final int n = permutation[i];
                lmDir[n] -= ypk * weightedJacobian[i][pk];
            }
            lmDir[pk] = ypk;
        }
        double dxNorm = 0.0;
        for (final int pj : permutation) {
            final double s = diag[pj] * lmDir[pj];
            work1[pj] = s;
            dxNorm += s * s;
        }
        dxNorm = FastMath.sqrt(dxNorm);
        double fp = dxNorm - delta;
        if (fp <= 0.1 * delta) {
            lmPar = 0.0;
            return lmPar;
        }
        double parl = 0.0;
        if (rank == solvedCols) {
            for (final int n2 : permutation) {
                final int pj2 = n2;
                work1[n2] *= diag[pj2] / dxNorm;
            }
            double sum2 = 0.0;
            for (int m = 0; m < solvedCols; ++m) {
                final int pj2 = permutation[m];
                double sum3 = 0.0;
                for (int i2 = 0; i2 < m; ++i2) {
                    sum3 += weightedJacobian[i2][pj2] * work1[permutation[i2]];
                }
                final double s2 = (work1[pj2] - sum3) / diagR[pj2];
                work1[pj2] = s2;
                sum2 += s2 * s2;
            }
            parl = fp / (delta * sum2);
        }
        double sum2 = 0.0;
        for (int m = 0; m < solvedCols; ++m) {
            final int pj2 = permutation[m];
            double sum3 = 0.0;
            for (int i2 = 0; i2 <= m; ++i2) {
                sum3 += weightedJacobian[i2][pj2] * qy[i2];
            }
            sum3 /= diag[pj2];
            sum2 += sum3 * sum3;
        }
        final double gNorm = FastMath.sqrt(sum2);
        double paru = gNorm / delta;
        if (paru == 0.0) {
            paru = Precision.SAFE_MIN / FastMath.min(delta, 0.1);
        }
        lmPar = FastMath.min(paru, FastMath.max(lmPar, parl));
        if (lmPar == 0.0) {
            lmPar = gNorm / dxNorm;
        }
        for (int countdown = 10; countdown >= 0; --countdown) {
            if (lmPar == 0.0) {
                lmPar = FastMath.max(Precision.SAFE_MIN, 0.001 * paru);
            }
            final double sPar = FastMath.sqrt(lmPar);
            for (final int pj3 : permutation) {
                work1[pj3] = sPar * diag[pj3];
            }
            this.determineLMDirection(qy, work1, work2, internalData, solvedCols, work3, lmDir);
            dxNorm = 0.0;
            for (final int pj3 : permutation) {
                final double s3 = diag[pj3] * lmDir[pj3];
                work3[pj3] = s3;
                dxNorm += s3 * s3;
            }
            dxNorm = FastMath.sqrt(dxNorm);
            final double previousFP = fp;
            fp = dxNorm - delta;
            if (FastMath.abs(fp) <= 0.1 * delta || (parl == 0.0 && fp <= previousFP && previousFP < 0.0)) {
                return lmPar;
            }
            for (final int pj4 : permutation) {
                work1[pj4] = work3[pj4] * diag[pj4] / dxNorm;
            }
            for (int j3 = 0; j3 < solvedCols; ++j3) {
                final int n3;
                final int pj4 = n3 = permutation[j3];
                work1[n3] /= work2[j3];
                final double tmp = work1[pj4];
                for (int i3 = j3 + 1; i3 < solvedCols; ++i3) {
                    final int n4 = permutation[i3];
                    work1[n4] -= weightedJacobian[i3][pj4] * tmp;
                }
            }
            sum2 = 0.0;
            for (int j3 = 0; j3 < solvedCols; ++j3) {
                final double s4 = work1[permutation[j3]];
                sum2 += s4 * s4;
            }
            final double correction = fp / (delta * sum2);
            if (fp > 0.0) {
                parl = FastMath.max(parl, lmPar);
            }
            else if (fp < 0.0) {
                paru = FastMath.min(paru, lmPar);
            }
            lmPar = FastMath.max(parl, lmPar + correction);
        }
        return lmPar;
    }
    
    private void determineLMDirection(final double[] qy, final double[] diag, final double[] lmDiag, final InternalData internalData, final int solvedCols, final double[] work, final double[] lmDir) {
        final int[] permutation = internalData.permutation;
        final double[][] weightedJacobian = internalData.weightedJacobian;
        final double[] diagR = internalData.diagR;
        for (int j = 0; j < solvedCols; ++j) {
            final int pj = permutation[j];
            for (int i = j + 1; i < solvedCols; ++i) {
                weightedJacobian[i][pj] = weightedJacobian[j][permutation[i]];
            }
            lmDir[j] = diagR[pj];
            work[j] = qy[j];
        }
        for (int j = 0; j < solvedCols; ++j) {
            final int pj = permutation[j];
            final double dpj = diag[pj];
            if (dpj != 0.0) {
                Arrays.fill(lmDiag, j + 1, lmDiag.length, 0.0);
            }
            lmDiag[j] = dpj;
            double qtbpj = 0.0;
            for (int k = j; k < solvedCols; ++k) {
                final int pk = permutation[k];
                if (lmDiag[k] != 0.0) {
                    final double rkk = weightedJacobian[k][pk];
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
                    weightedJacobian[k][pk] = cos * rkk + sin * lmDiag[k];
                    final double temp = cos * work[k] + sin * qtbpj;
                    qtbpj = -sin * work[k] + cos * qtbpj;
                    work[k] = temp;
                    for (int l = k + 1; l < solvedCols; ++l) {
                        final double rik = weightedJacobian[l][pk];
                        final double temp2 = cos * rik + sin * lmDiag[l];
                        lmDiag[l] = -sin * rik + cos * lmDiag[l];
                        weightedJacobian[l][pk] = temp2;
                    }
                }
            }
            lmDiag[j] = weightedJacobian[j][permutation[j]];
            weightedJacobian[j][permutation[j]] = lmDir[j];
        }
        int nSing = solvedCols;
        for (int m = 0; m < solvedCols; ++m) {
            if (lmDiag[m] == 0.0 && nSing == solvedCols) {
                nSing = m;
            }
            if (nSing < solvedCols) {
                work[m] = 0.0;
            }
        }
        if (nSing > 0) {
            for (int m = nSing - 1; m >= 0; --m) {
                final int pj2 = permutation[m];
                double sum = 0.0;
                for (int i2 = m + 1; i2 < nSing; ++i2) {
                    sum += weightedJacobian[i2][pj2] * work[i2];
                }
                work[m] = (work[m] - sum) / lmDiag[m];
            }
        }
        for (int m = 0; m < lmDir.length; ++m) {
            lmDir[permutation[m]] = work[m];
        }
    }
    
    private InternalData qrDecomposition(final RealMatrix jacobian, final int solvedCols) throws ConvergenceException {
        final double[][] weightedJacobian = jacobian.scalarMultiply(-1.0).getData();
        final int nR = weightedJacobian.length;
        final int nC = weightedJacobian[0].length;
        final int[] permutation = new int[nC];
        final double[] diagR = new double[nC];
        final double[] jacNorm = new double[nC];
        final double[] beta = new double[nC];
        for (int k = 0; k < nC; ++k) {
            permutation[k] = k;
            double norm2 = 0.0;
            for (int i = 0; i < nR; ++i) {
                final double akk = weightedJacobian[i][k];
                norm2 += akk * akk;
            }
            jacNorm[k] = FastMath.sqrt(norm2);
        }
        for (int k = 0; k < nC; ++k) {
            int nextColumn = -1;
            double ak2 = Double.NEGATIVE_INFINITY;
            for (int j = k; j < nC; ++j) {
                double norm3 = 0.0;
                for (int l = k; l < nR; ++l) {
                    final double aki = weightedJacobian[l][permutation[j]];
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
                return new InternalData(weightedJacobian, permutation, k, diagR, jacNorm, beta);
            }
            final int pk = permutation[nextColumn];
            permutation[nextColumn] = permutation[k];
            permutation[k] = pk;
            final double akk2 = weightedJacobian[k][pk];
            final double alpha = (akk2 > 0.0) ? (-FastMath.sqrt(ak2)) : FastMath.sqrt(ak2);
            final double betak = 1.0 / (ak2 - akk2 * alpha);
            beta[pk] = betak;
            diagR[pk] = alpha;
            final double[] array = weightedJacobian[k];
            final int n = pk;
            array[n] -= alpha;
            for (int dk = nC - 1 - k; dk > 0; --dk) {
                double gamma = 0.0;
                for (int m = k; m < nR; ++m) {
                    gamma += weightedJacobian[m][pk] * weightedJacobian[m][permutation[k + dk]];
                }
                gamma *= betak;
                for (int m = k; m < nR; ++m) {
                    final double[] array2 = weightedJacobian[m];
                    final int n2 = permutation[k + dk];
                    array2[n2] -= gamma * weightedJacobian[m][pk];
                }
            }
        }
        return new InternalData(weightedJacobian, permutation, solvedCols, diagR, jacNorm, beta);
    }
    
    private void qTy(final double[] y, final InternalData internalData) {
        final double[][] weightedJacobian = internalData.weightedJacobian;
        final int[] permutation = internalData.permutation;
        final double[] beta = internalData.beta;
        final int nR = weightedJacobian.length;
        for (int nC = weightedJacobian[0].length, k = 0; k < nC; ++k) {
            final int pk = permutation[k];
            double gamma = 0.0;
            for (int i = k; i < nR; ++i) {
                gamma += weightedJacobian[i][pk] * y[i];
            }
            gamma *= beta[pk];
            for (int i = k; i < nR; ++i) {
                final int n = i;
                y[n] -= gamma * weightedJacobian[i][pk];
            }
        }
    }
    
    static {
        TWO_EPS = 2.0 * Precision.EPSILON;
    }
    
    private static class InternalData
    {
        private final double[][] weightedJacobian;
        private final int[] permutation;
        private final int rank;
        private final double[] diagR;
        private final double[] jacNorm;
        private final double[] beta;
        
        InternalData(final double[][] weightedJacobian, final int[] permutation, final int rank, final double[] diagR, final double[] jacNorm, final double[] beta) {
            this.weightedJacobian = weightedJacobian;
            this.permutation = permutation;
            this.rank = rank;
            this.diagR = diagR;
            this.jacNorm = jacNorm;
            this.beta = beta;
        }
    }
}
