package org.apache.commons.math3.optim.nonlinear.scalar.noderiv;

import org.apache.commons.math3.exception.NotPositiveException;
import java.util.Arrays;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.OptimizationData;
import java.util.ArrayList;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import java.util.List;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;

public class CMAESOptimizer extends MultivariateOptimizer
{
    private int lambda;
    private final boolean isActiveCMA;
    private final int checkFeasableCount;
    private double[] inputSigma;
    private int dimension;
    private int diagonalOnly;
    private boolean isMinimize;
    private final boolean generateStatistics;
    private final int maxIterations;
    private final double stopFitness;
    private double stopTolUpX;
    private double stopTolX;
    private double stopTolFun;
    private double stopTolHistFun;
    private int mu;
    private double logMu2;
    private RealMatrix weights;
    private double mueff;
    private double sigma;
    private double cc;
    private double cs;
    private double damps;
    private double ccov1;
    private double ccovmu;
    private double chiN;
    private double ccov1Sep;
    private double ccovmuSep;
    private RealMatrix xmean;
    private RealMatrix pc;
    private RealMatrix ps;
    private double normps;
    private RealMatrix B;
    private RealMatrix D;
    private RealMatrix BD;
    private RealMatrix diagD;
    private RealMatrix C;
    private RealMatrix diagC;
    private int iterations;
    private double[] fitnessHistory;
    private int historySize;
    private final RandomGenerator random;
    private final List<Double> statisticsSigmaHistory;
    private final List<RealMatrix> statisticsMeanHistory;
    private final List<Double> statisticsFitnessHistory;
    private final List<RealMatrix> statisticsDHistory;
    
    public CMAESOptimizer(final int maxIterations, final double stopFitness, final boolean isActiveCMA, final int diagonalOnly, final int checkFeasableCount, final RandomGenerator random, final boolean generateStatistics, final ConvergenceChecker<PointValuePair> checker) {
        super(checker);
        this.isMinimize = true;
        this.statisticsSigmaHistory = new ArrayList<Double>();
        this.statisticsMeanHistory = new ArrayList<RealMatrix>();
        this.statisticsFitnessHistory = new ArrayList<Double>();
        this.statisticsDHistory = new ArrayList<RealMatrix>();
        this.maxIterations = maxIterations;
        this.stopFitness = stopFitness;
        this.isActiveCMA = isActiveCMA;
        this.diagonalOnly = diagonalOnly;
        this.checkFeasableCount = checkFeasableCount;
        this.random = random;
        this.generateStatistics = generateStatistics;
    }
    
    public List<Double> getStatisticsSigmaHistory() {
        return this.statisticsSigmaHistory;
    }
    
    public List<RealMatrix> getStatisticsMeanHistory() {
        return this.statisticsMeanHistory;
    }
    
    public List<Double> getStatisticsFitnessHistory() {
        return this.statisticsFitnessHistory;
    }
    
    public List<RealMatrix> getStatisticsDHistory() {
        return this.statisticsDHistory;
    }
    
    @Override
    public PointValuePair optimize(final OptimizationData... optData) throws TooManyEvaluationsException, DimensionMismatchException {
        return super.optimize(optData);
    }
    
    @Override
    protected PointValuePair doOptimize() {
        this.isMinimize = this.getGoalType().equals(GoalType.MINIMIZE);
        final FitnessFunction fitfun = new FitnessFunction();
        final double[] guess = this.getStartPoint();
        this.dimension = guess.length;
        this.initializeCMA(guess);
        this.iterations = 0;
        final ValuePenaltyPair valuePenalty = fitfun.value(guess);
        double bestValue = valuePenalty.value + valuePenalty.penalty;
        push(this.fitnessHistory, bestValue);
        PointValuePair optimum = new PointValuePair(this.getStartPoint(), this.isMinimize ? bestValue : (-bestValue));
        PointValuePair lastResult = null;
        this.iterations = 1;
    Label_1236:
        while (this.iterations <= this.maxIterations) {
            this.incrementIterationCount();
            final RealMatrix arz = this.randn1(this.dimension, this.lambda);
            final RealMatrix arx = zeros(this.dimension, this.lambda);
            final double[] fitness = new double[this.lambda];
            final ValuePenaltyPair[] valuePenaltyPairs = new ValuePenaltyPair[this.lambda];
            for (int k = 0; k < this.lambda; ++k) {
                RealMatrix arxk = null;
                for (int i = 0; i < this.checkFeasableCount + 1; ++i) {
                    if (this.diagonalOnly <= 0) {
                        arxk = this.xmean.add(this.BD.multiply(arz.getColumnMatrix(k)).scalarMultiply(this.sigma));
                    }
                    else {
                        arxk = this.xmean.add(times(this.diagD, arz.getColumnMatrix(k)).scalarMultiply(this.sigma));
                    }
                    if (i >= this.checkFeasableCount) {
                        break;
                    }
                    if (fitfun.isFeasible(arxk.getColumn(0))) {
                        break;
                    }
                    arz.setColumn(k, this.randn(this.dimension));
                }
                copyColumn(arxk, 0, arx, k);
                try {
                    valuePenaltyPairs[k] = fitfun.value(arx.getColumn(k));
                }
                catch (final TooManyEvaluationsException e) {
                    break Label_1236;
                }
            }
            final double valueRange = this.valueRange(valuePenaltyPairs);
            for (int iValue = 0; iValue < valuePenaltyPairs.length; ++iValue) {
                fitness[iValue] = valuePenaltyPairs[iValue].value + valuePenaltyPairs[iValue].penalty * valueRange;
            }
            final int[] arindex = this.sortedIndices(fitness);
            final RealMatrix xold = this.xmean;
            final RealMatrix bestArx = selectColumns(arx, MathArrays.copyOf(arindex, this.mu));
            this.xmean = bestArx.multiply(this.weights);
            final RealMatrix bestArz = selectColumns(arz, MathArrays.copyOf(arindex, this.mu));
            final RealMatrix zmean = bestArz.multiply(this.weights);
            final boolean hsig = this.updateEvolutionPaths(zmean, xold);
            if (this.diagonalOnly <= 0) {
                this.updateCovariance(hsig, bestArx, arz, arindex, xold);
            }
            else {
                this.updateCovarianceDiagonalOnly(hsig, bestArz);
            }
            this.sigma *= FastMath.exp(FastMath.min(1.0, (this.normps / this.chiN - 1.0) * this.cs / this.damps));
            final double bestFitness = fitness[arindex[0]];
            final double worstFitness = fitness[arindex[arindex.length - 1]];
            if (bestValue > bestFitness) {
                bestValue = bestFitness;
                lastResult = optimum;
                optimum = new PointValuePair(fitfun.repair(bestArx.getColumn(0)), this.isMinimize ? bestFitness : (-bestFitness));
                if (this.getConvergenceChecker() != null && lastResult != null && this.getConvergenceChecker().converged(this.iterations, optimum, lastResult)) {
                    break;
                }
            }
            if (this.stopFitness != 0.0 && bestFitness < (this.isMinimize ? this.stopFitness : (-this.stopFitness))) {
                break;
            }
            final double[] sqrtDiagC = sqrt(this.diagC).getColumn(0);
            final double[] pcCol = this.pc.getColumn(0);
            for (int j = 0; j < this.dimension && this.sigma * FastMath.max(FastMath.abs(pcCol[j]), sqrtDiagC[j]) <= this.stopTolX; ++j) {
                if (j >= this.dimension - 1) {
                    break Label_1236;
                }
            }
            for (int j = 0; j < this.dimension; ++j) {
                if (this.sigma * sqrtDiagC[j] > this.stopTolUpX) {
                    break Label_1236;
                }
            }
            final double historyBest = min(this.fitnessHistory);
            final double historyWorst = max(this.fitnessHistory);
            if (this.iterations > 2 && FastMath.max(historyWorst, worstFitness) - FastMath.min(historyBest, bestFitness) < this.stopTolFun) {
                break;
            }
            if (this.iterations > this.fitnessHistory.length && historyWorst - historyBest < this.stopTolHistFun) {
                break;
            }
            if (max(this.diagD) / min(this.diagD) > 1.0E7) {
                break;
            }
            if (this.getConvergenceChecker() != null) {
                final PointValuePair current = new PointValuePair(bestArx.getColumn(0), this.isMinimize ? bestFitness : (-bestFitness));
                if (lastResult != null && this.getConvergenceChecker().converged(this.iterations, current, lastResult)) {
                    break;
                }
                lastResult = current;
            }
            if (bestValue == fitness[arindex[(int)(0.1 + this.lambda / 4.0)]]) {
                this.sigma *= FastMath.exp(0.2 + this.cs / this.damps);
            }
            if (this.iterations > 2 && FastMath.max(historyWorst, bestFitness) - FastMath.min(historyBest, bestFitness) == 0.0) {
                this.sigma *= FastMath.exp(0.2 + this.cs / this.damps);
            }
            push(this.fitnessHistory, bestFitness);
            if (this.generateStatistics) {
                this.statisticsSigmaHistory.add(this.sigma);
                this.statisticsFitnessHistory.add(bestFitness);
                this.statisticsMeanHistory.add(this.xmean.transpose());
                this.statisticsDHistory.add(this.diagD.transpose().scalarMultiply(100000.0));
            }
            ++this.iterations;
        }
        return optimum;
    }
    
    @Override
    protected void parseOptimizationData(final OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (final OptimizationData data : optData) {
            if (data instanceof Sigma) {
                this.inputSigma = ((Sigma)data).getSigma();
            }
            else if (data instanceof PopulationSize) {
                this.lambda = ((PopulationSize)data).getPopulationSize();
            }
        }
        this.checkParameters();
    }
    
    private void checkParameters() {
        final double[] init = this.getStartPoint();
        final double[] lB = this.getLowerBound();
        final double[] uB = this.getUpperBound();
        if (this.inputSigma != null) {
            if (this.inputSigma.length != init.length) {
                throw new DimensionMismatchException(this.inputSigma.length, init.length);
            }
            for (int i = 0; i < init.length; ++i) {
                if (this.inputSigma[i] > uB[i] - lB[i]) {
                    throw new OutOfRangeException(this.inputSigma[i], 0, uB[i] - lB[i]);
                }
            }
        }
    }
    
    private void initializeCMA(final double[] guess) {
        if (this.lambda <= 0) {
            throw new NotStrictlyPositiveException(this.lambda);
        }
        final double[][] sigmaArray = new double[guess.length][1];
        for (int i = 0; i < guess.length; ++i) {
            sigmaArray[i][0] = this.inputSigma[i];
        }
        final RealMatrix insigma = new Array2DRowRealMatrix(sigmaArray, false);
        this.sigma = max(insigma);
        this.stopTolUpX = 1000.0 * max(insigma);
        this.stopTolX = 1.0E-11 * max(insigma);
        this.stopTolFun = 1.0E-12;
        this.stopTolHistFun = 1.0E-13;
        this.mu = this.lambda / 2;
        this.logMu2 = FastMath.log(this.mu + 0.5);
        this.weights = log(sequence(1.0, this.mu, 1.0)).scalarMultiply(-1.0).scalarAdd(this.logMu2);
        double sumw = 0.0;
        double sumwq = 0.0;
        for (int j = 0; j < this.mu; ++j) {
            final double w = this.weights.getEntry(j, 0);
            sumw += w;
            sumwq += w * w;
        }
        this.weights = this.weights.scalarMultiply(1.0 / sumw);
        this.mueff = sumw * sumw / sumwq;
        this.cc = (4.0 + this.mueff / this.dimension) / (this.dimension + 4 + 2.0 * this.mueff / this.dimension);
        this.cs = (this.mueff + 2.0) / (this.dimension + this.mueff + 3.0);
        this.damps = (1.0 + 2.0 * FastMath.max(0.0, FastMath.sqrt((this.mueff - 1.0) / (this.dimension + 1)) - 1.0)) * FastMath.max(0.3, 1.0 - this.dimension / (1.0E-6 + this.maxIterations)) + this.cs;
        this.ccov1 = 2.0 / ((this.dimension + 1.3) * (this.dimension + 1.3) + this.mueff);
        this.ccovmu = FastMath.min(1.0 - this.ccov1, 2.0 * (this.mueff - 2.0 + 1.0 / this.mueff) / ((this.dimension + 2) * (this.dimension + 2) + this.mueff));
        this.ccov1Sep = FastMath.min(1.0, this.ccov1 * (this.dimension + 1.5) / 3.0);
        this.ccovmuSep = FastMath.min(1.0 - this.ccov1, this.ccovmu * (this.dimension + 1.5) / 3.0);
        this.chiN = FastMath.sqrt(this.dimension) * (1.0 - 1.0 / (4.0 * this.dimension) + 1.0 / (21.0 * this.dimension * this.dimension));
        this.xmean = MatrixUtils.createColumnRealMatrix(guess);
        this.diagD = insigma.scalarMultiply(1.0 / this.sigma);
        this.diagC = square(this.diagD);
        this.pc = zeros(this.dimension, 1);
        this.ps = zeros(this.dimension, 1);
        this.normps = this.ps.getFrobeniusNorm();
        this.B = eye(this.dimension, this.dimension);
        this.D = ones(this.dimension, 1);
        this.BD = times(this.B, repmat(this.diagD.transpose(), this.dimension, 1));
        this.C = this.B.multiply(diag(square(this.D)).multiply(this.B.transpose()));
        this.historySize = 10 + (int)(30 * this.dimension / (double)this.lambda);
        this.fitnessHistory = new double[this.historySize];
        for (int j = 0; j < this.historySize; ++j) {
            this.fitnessHistory[j] = Double.MAX_VALUE;
        }
    }
    
    private boolean updateEvolutionPaths(final RealMatrix zmean, final RealMatrix xold) {
        this.ps = this.ps.scalarMultiply(1.0 - this.cs).add(this.B.multiply(zmean).scalarMultiply(FastMath.sqrt(this.cs * (2.0 - this.cs) * this.mueff)));
        this.normps = this.ps.getFrobeniusNorm();
        final boolean hsig = this.normps / FastMath.sqrt(1.0 - FastMath.pow(1.0 - this.cs, 2 * this.iterations)) / this.chiN < 1.4 + 2.0 / (this.dimension + 1.0);
        this.pc = this.pc.scalarMultiply(1.0 - this.cc);
        if (hsig) {
            this.pc = this.pc.add(this.xmean.subtract(xold).scalarMultiply(FastMath.sqrt(this.cc * (2.0 - this.cc) * this.mueff) / this.sigma));
        }
        return hsig;
    }
    
    private void updateCovarianceDiagonalOnly(final boolean hsig, final RealMatrix bestArz) {
        double oldFac = hsig ? 0.0 : (this.ccov1Sep * this.cc * (2.0 - this.cc));
        oldFac += 1.0 - this.ccov1Sep - this.ccovmuSep;
        this.diagC = this.diagC.scalarMultiply(oldFac).add(square(this.pc).scalarMultiply(this.ccov1Sep)).add(times(this.diagC, square(bestArz).multiply(this.weights)).scalarMultiply(this.ccovmuSep));
        this.diagD = sqrt(this.diagC);
        if (this.diagonalOnly > 1 && this.iterations > this.diagonalOnly) {
            this.diagonalOnly = 0;
            this.B = eye(this.dimension, this.dimension);
            this.BD = diag(this.diagD);
            this.C = diag(this.diagC);
        }
    }
    
    private void updateCovariance(final boolean hsig, final RealMatrix bestArx, final RealMatrix arz, final int[] arindex, final RealMatrix xold) {
        double negccov = 0.0;
        if (this.ccov1 + this.ccovmu > 0.0) {
            final RealMatrix arpos = bestArx.subtract(repmat(xold, 1, this.mu)).scalarMultiply(1.0 / this.sigma);
            final RealMatrix roneu = this.pc.multiply(this.pc.transpose()).scalarMultiply(this.ccov1);
            double oldFac = hsig ? 0.0 : (this.ccov1 * this.cc * (2.0 - this.cc));
            oldFac += 1.0 - this.ccov1 - this.ccovmu;
            if (this.isActiveCMA) {
                negccov = (1.0 - this.ccovmu) * 0.25 * this.mueff / (FastMath.pow(this.dimension + 2, 1.5) + 2.0 * this.mueff);
                final double negminresidualvariance = 0.66;
                final double negalphaold = 0.5;
                final int[] arReverseIndex = reverse(arindex);
                RealMatrix arzneg = selectColumns(arz, MathArrays.copyOf(arReverseIndex, this.mu));
                RealMatrix arnorms = sqrt(sumRows(square(arzneg)));
                final int[] idxnorms = this.sortedIndices(arnorms.getRow(0));
                final RealMatrix arnormsSorted = selectColumns(arnorms, idxnorms);
                final int[] idxReverse = reverse(idxnorms);
                final RealMatrix arnormsReverse = selectColumns(arnorms, idxReverse);
                arnorms = divide(arnormsReverse, arnormsSorted);
                final int[] idxInv = inverse(idxnorms);
                final RealMatrix arnormsInv = selectColumns(arnorms, idxInv);
                final double negcovMax = 0.33999999999999997 / square(arnormsInv).multiply(this.weights).getEntry(0, 0);
                if (negccov > negcovMax) {
                    negccov = negcovMax;
                }
                arzneg = times(arzneg, repmat(arnormsInv, this.dimension, 1));
                final RealMatrix artmp = this.BD.multiply(arzneg);
                final RealMatrix Cneg = artmp.multiply(diag(this.weights)).multiply(artmp.transpose());
                oldFac += 0.5 * negccov;
                this.C = this.C.scalarMultiply(oldFac).add(roneu).add(arpos.scalarMultiply(this.ccovmu + 0.5 * negccov).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose()))).subtract(Cneg.scalarMultiply(negccov));
            }
            else {
                this.C = this.C.scalarMultiply(oldFac).add(roneu).add(arpos.scalarMultiply(this.ccovmu).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose())));
            }
        }
        this.updateBD(negccov);
    }
    
    private void updateBD(final double negccov) {
        if (this.ccov1 + this.ccovmu + negccov > 0.0 && this.iterations % 1.0 / (this.ccov1 + this.ccovmu + negccov) / this.dimension / 10.0 < 1.0) {
            this.C = triu(this.C, 0).add(triu(this.C, 1).transpose());
            final EigenDecomposition eig = new EigenDecomposition(this.C);
            this.B = eig.getV();
            this.D = eig.getD();
            this.diagD = diag(this.D);
            if (min(this.diagD) <= 0.0) {
                for (int i = 0; i < this.dimension; ++i) {
                    if (this.diagD.getEntry(i, 0) < 0.0) {
                        this.diagD.setEntry(i, 0, 0.0);
                    }
                }
                final double tfac = max(this.diagD) / 1.0E14;
                this.C = this.C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac));
                this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac));
            }
            if (max(this.diagD) > 1.0E14 * min(this.diagD)) {
                final double tfac = max(this.diagD) / 1.0E14 - min(this.diagD);
                this.C = this.C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac));
                this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac));
            }
            this.diagC = diag(this.C);
            this.diagD = sqrt(this.diagD);
            this.BD = times(this.B, repmat(this.diagD.transpose(), this.dimension, 1));
        }
    }
    
    private static void push(final double[] vals, final double val) {
        for (int i = vals.length - 1; i > 0; --i) {
            vals[i] = vals[i - 1];
        }
        vals[0] = val;
    }
    
    private int[] sortedIndices(final double[] doubles) {
        final DoubleIndex[] dis = new DoubleIndex[doubles.length];
        for (int i = 0; i < doubles.length; ++i) {
            dis[i] = new DoubleIndex(doubles[i], i);
        }
        Arrays.sort(dis);
        final int[] indices = new int[doubles.length];
        for (int j = 0; j < doubles.length; ++j) {
            indices[j] = dis[j].index;
        }
        return indices;
    }
    
    private double valueRange(final ValuePenaltyPair[] vpPairs) {
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.MAX_VALUE;
        for (final ValuePenaltyPair vpPair : vpPairs) {
            if (vpPair.value > max) {
                max = vpPair.value;
            }
            if (vpPair.value < min) {
                min = vpPair.value;
            }
        }
        return max - min;
    }
    
    private static RealMatrix log(final RealMatrix m) {
        final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int r = 0; r < m.getRowDimension(); ++r) {
            for (int c = 0; c < m.getColumnDimension(); ++c) {
                d[r][c] = FastMath.log(m.getEntry(r, c));
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static RealMatrix sqrt(final RealMatrix m) {
        final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int r = 0; r < m.getRowDimension(); ++r) {
            for (int c = 0; c < m.getColumnDimension(); ++c) {
                d[r][c] = FastMath.sqrt(m.getEntry(r, c));
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static RealMatrix square(final RealMatrix m) {
        final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int r = 0; r < m.getRowDimension(); ++r) {
            for (int c = 0; c < m.getColumnDimension(); ++c) {
                final double e = m.getEntry(r, c);
                d[r][c] = e * e;
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static RealMatrix times(final RealMatrix m, final RealMatrix n) {
        final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int r = 0; r < m.getRowDimension(); ++r) {
            for (int c = 0; c < m.getColumnDimension(); ++c) {
                d[r][c] = m.getEntry(r, c) * n.getEntry(r, c);
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static RealMatrix divide(final RealMatrix m, final RealMatrix n) {
        final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int r = 0; r < m.getRowDimension(); ++r) {
            for (int c = 0; c < m.getColumnDimension(); ++c) {
                d[r][c] = m.getEntry(r, c) / n.getEntry(r, c);
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static RealMatrix selectColumns(final RealMatrix m, final int[] cols) {
        final double[][] d = new double[m.getRowDimension()][cols.length];
        for (int r = 0; r < m.getRowDimension(); ++r) {
            for (int c = 0; c < cols.length; ++c) {
                d[r][c] = m.getEntry(r, cols[c]);
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static RealMatrix triu(final RealMatrix m, final int k) {
        final double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int r = 0; r < m.getRowDimension(); ++r) {
            for (int c = 0; c < m.getColumnDimension(); ++c) {
                d[r][c] = ((r <= c - k) ? m.getEntry(r, c) : 0.0);
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static RealMatrix sumRows(final RealMatrix m) {
        final double[][] d = new double[1][m.getColumnDimension()];
        for (int c = 0; c < m.getColumnDimension(); ++c) {
            double sum = 0.0;
            for (int r = 0; r < m.getRowDimension(); ++r) {
                sum += m.getEntry(r, c);
            }
            d[0][c] = sum;
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static RealMatrix diag(final RealMatrix m) {
        if (m.getColumnDimension() == 1) {
            final double[][] d = new double[m.getRowDimension()][m.getRowDimension()];
            for (int i = 0; i < m.getRowDimension(); ++i) {
                d[i][i] = m.getEntry(i, 0);
            }
            return new Array2DRowRealMatrix(d, false);
        }
        final double[][] d = new double[m.getRowDimension()][1];
        for (int i = 0; i < m.getColumnDimension(); ++i) {
            d[i][0] = m.getEntry(i, i);
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static void copyColumn(final RealMatrix m1, final int col1, final RealMatrix m2, final int col2) {
        for (int i = 0; i < m1.getRowDimension(); ++i) {
            m2.setEntry(i, col2, m1.getEntry(i, col1));
        }
    }
    
    private static RealMatrix ones(final int n, final int m) {
        final double[][] d = new double[n][m];
        for (int r = 0; r < n; ++r) {
            Arrays.fill(d[r], 1.0);
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static RealMatrix eye(final int n, final int m) {
        final double[][] d = new double[n][m];
        for (int r = 0; r < n; ++r) {
            if (r < m) {
                d[r][r] = 1.0;
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static RealMatrix zeros(final int n, final int m) {
        return new Array2DRowRealMatrix(n, m);
    }
    
    private static RealMatrix repmat(final RealMatrix mat, final int n, final int m) {
        final int rd = mat.getRowDimension();
        final int cd = mat.getColumnDimension();
        final double[][] d = new double[n * rd][m * cd];
        for (int r = 0; r < n * rd; ++r) {
            for (int c = 0; c < m * cd; ++c) {
                d[r][c] = mat.getEntry(r % rd, c % cd);
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static RealMatrix sequence(final double start, final double end, final double step) {
        final int size = (int)((end - start) / step + 1.0);
        final double[][] d = new double[size][1];
        double value = start;
        for (int r = 0; r < size; ++r) {
            d[r][0] = value;
            value += step;
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    private static double max(final RealMatrix m) {
        double max = -1.7976931348623157E308;
        for (int r = 0; r < m.getRowDimension(); ++r) {
            for (int c = 0; c < m.getColumnDimension(); ++c) {
                final double e = m.getEntry(r, c);
                if (max < e) {
                    max = e;
                }
            }
        }
        return max;
    }
    
    private static double min(final RealMatrix m) {
        double min = Double.MAX_VALUE;
        for (int r = 0; r < m.getRowDimension(); ++r) {
            for (int c = 0; c < m.getColumnDimension(); ++c) {
                final double e = m.getEntry(r, c);
                if (min > e) {
                    min = e;
                }
            }
        }
        return min;
    }
    
    private static double max(final double[] m) {
        double max = -1.7976931348623157E308;
        for (int r = 0; r < m.length; ++r) {
            if (max < m[r]) {
                max = m[r];
            }
        }
        return max;
    }
    
    private static double min(final double[] m) {
        double min = Double.MAX_VALUE;
        for (int r = 0; r < m.length; ++r) {
            if (min > m[r]) {
                min = m[r];
            }
        }
        return min;
    }
    
    private static int[] inverse(final int[] indices) {
        final int[] inverse = new int[indices.length];
        for (int i = 0; i < indices.length; ++i) {
            inverse[indices[i]] = i;
        }
        return inverse;
    }
    
    private static int[] reverse(final int[] indices) {
        final int[] reverse = new int[indices.length];
        for (int i = 0; i < indices.length; ++i) {
            reverse[i] = indices[indices.length - i - 1];
        }
        return reverse;
    }
    
    private double[] randn(final int size) {
        final double[] randn = new double[size];
        for (int i = 0; i < size; ++i) {
            randn[i] = this.random.nextGaussian();
        }
        return randn;
    }
    
    private RealMatrix randn1(final int size, final int popSize) {
        final double[][] d = new double[size][popSize];
        for (int r = 0; r < size; ++r) {
            for (int c = 0; c < popSize; ++c) {
                d[r][c] = this.random.nextGaussian();
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
    
    public static class Sigma implements OptimizationData
    {
        private final double[] sigma;
        
        public Sigma(final double[] s) throws NotPositiveException {
            for (int i = 0; i < s.length; ++i) {
                if (s[i] < 0.0) {
                    throw new NotPositiveException(s[i]);
                }
            }
            this.sigma = s.clone();
        }
        
        public double[] getSigma() {
            return this.sigma.clone();
        }
    }
    
    public static class PopulationSize implements OptimizationData
    {
        private final int lambda;
        
        public PopulationSize(final int size) throws NotStrictlyPositiveException {
            if (size <= 0) {
                throw new NotStrictlyPositiveException(size);
            }
            this.lambda = size;
        }
        
        public int getPopulationSize() {
            return this.lambda;
        }
    }
    
    private static class DoubleIndex implements Comparable<DoubleIndex>
    {
        private final double value;
        private final int index;
        
        DoubleIndex(final double value, final int index) {
            this.value = value;
            this.index = index;
        }
        
        public int compareTo(final DoubleIndex o) {
            return Double.compare(this.value, o.value);
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || (other instanceof DoubleIndex && Double.compare(this.value, ((DoubleIndex)other).value) == 0);
        }
        
        @Override
        public int hashCode() {
            final long bits = Double.doubleToLongBits(this.value);
            return (int)((0x15F34EL ^ bits >>> 32 ^ bits) & -1L);
        }
    }
    
    private static class ValuePenaltyPair
    {
        private double value;
        private double penalty;
        
        ValuePenaltyPair(final double value, final double penalty) {
            this.value = value;
            this.penalty = penalty;
        }
    }
    
    private class FitnessFunction
    {
        private final boolean isRepairMode;
        
        FitnessFunction() {
            this.isRepairMode = true;
        }
        
        public ValuePenaltyPair value(final double[] point) {
            double penalty = 0.0;
            double value;
            if (this.isRepairMode) {
                final double[] repaired = this.repair(point);
                value = CMAESOptimizer.this.computeObjectiveValue(repaired);
                penalty = this.penalty(point, repaired);
            }
            else {
                value = CMAESOptimizer.this.computeObjectiveValue(point);
            }
            value = (CMAESOptimizer.this.isMinimize ? value : (-value));
            penalty = (CMAESOptimizer.this.isMinimize ? penalty : (-penalty));
            return new ValuePenaltyPair(value, penalty);
        }
        
        public boolean isFeasible(final double[] x) {
            final double[] lB = CMAESOptimizer.this.getLowerBound();
            final double[] uB = CMAESOptimizer.this.getUpperBound();
            for (int i = 0; i < x.length; ++i) {
                if (x[i] < lB[i]) {
                    return false;
                }
                if (x[i] > uB[i]) {
                    return false;
                }
            }
            return true;
        }
        
        private double[] repair(final double[] x) {
            final double[] lB = CMAESOptimizer.this.getLowerBound();
            final double[] uB = CMAESOptimizer.this.getUpperBound();
            final double[] repaired = new double[x.length];
            for (int i = 0; i < x.length; ++i) {
                if (x[i] < lB[i]) {
                    repaired[i] = lB[i];
                }
                else if (x[i] > uB[i]) {
                    repaired[i] = uB[i];
                }
                else {
                    repaired[i] = x[i];
                }
            }
            return repaired;
        }
        
        private double penalty(final double[] x, final double[] repaired) {
            double penalty = 0.0;
            for (int i = 0; i < x.length; ++i) {
                final double diff = FastMath.abs(x[i] - repaired[i]);
                penalty += diff;
            }
            return CMAESOptimizer.this.isMinimize ? penalty : (-penalty);
        }
    }
}
