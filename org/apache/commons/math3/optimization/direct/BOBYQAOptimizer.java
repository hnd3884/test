package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.optimization.MultivariateOptimizer;
import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public class BOBYQAOptimizer extends BaseAbstractMultivariateSimpleBoundsOptimizer<MultivariateFunction> implements MultivariateOptimizer
{
    public static final int MINIMUM_PROBLEM_DIMENSION = 2;
    public static final double DEFAULT_INITIAL_RADIUS = 10.0;
    public static final double DEFAULT_STOPPING_RADIUS = 1.0E-8;
    private static final double ZERO = 0.0;
    private static final double ONE = 1.0;
    private static final double TWO = 2.0;
    private static final double TEN = 10.0;
    private static final double SIXTEEN = 16.0;
    private static final double TWO_HUNDRED_FIFTY = 250.0;
    private static final double MINUS_ONE = -1.0;
    private static final double HALF = 0.5;
    private static final double ONE_OVER_FOUR = 0.25;
    private static final double ONE_OVER_EIGHT = 0.125;
    private static final double ONE_OVER_TEN = 0.1;
    private static final double ONE_OVER_A_THOUSAND = 0.001;
    private final int numberOfInterpolationPoints;
    private double initialTrustRegionRadius;
    private final double stoppingTrustRegionRadius;
    private boolean isMinimize;
    private ArrayRealVector currentBest;
    private double[] boundDifference;
    private int trustRegionCenterInterpolationPointIndex;
    private Array2DRowRealMatrix bMatrix;
    private Array2DRowRealMatrix zMatrix;
    private Array2DRowRealMatrix interpolationPoints;
    private ArrayRealVector originShift;
    private ArrayRealVector fAtInterpolationPoints;
    private ArrayRealVector trustRegionCenterOffset;
    private ArrayRealVector gradientAtTrustRegionCenter;
    private ArrayRealVector lowerDifference;
    private ArrayRealVector upperDifference;
    private ArrayRealVector modelSecondDerivativesParameters;
    private ArrayRealVector newPoint;
    private ArrayRealVector alternativeNewPoint;
    private ArrayRealVector trialStepPoint;
    private ArrayRealVector lagrangeValuesAtNewPoint;
    private ArrayRealVector modelSecondDerivativesValues;
    
    public BOBYQAOptimizer(final int numberOfInterpolationPoints) {
        this(numberOfInterpolationPoints, 10.0, 1.0E-8);
    }
    
    public BOBYQAOptimizer(final int numberOfInterpolationPoints, final double initialTrustRegionRadius, final double stoppingTrustRegionRadius) {
        super(null);
        this.numberOfInterpolationPoints = numberOfInterpolationPoints;
        this.initialTrustRegionRadius = initialTrustRegionRadius;
        this.stoppingTrustRegionRadius = stoppingTrustRegionRadius;
    }
    
    @Override
    protected PointValuePair doOptimize() {
        final double[] lowerBound = this.getLowerBound();
        final double[] upperBound = this.getUpperBound();
        this.setup(lowerBound, upperBound);
        this.isMinimize = (this.getGoalType() == GoalType.MINIMIZE);
        this.currentBest = new ArrayRealVector(this.getStartPoint());
        final double value = this.bobyqa(lowerBound, upperBound);
        return new PointValuePair(this.currentBest.getDataRef(), this.isMinimize ? value : (-value));
    }
    
    private double bobyqa(final double[] lowerBound, final double[] upperBound) {
        printMethod();
        for (int n = this.currentBest.getDimension(), j = 0; j < n; ++j) {
            final double boundDiff = this.boundDifference[j];
            this.lowerDifference.setEntry(j, lowerBound[j] - this.currentBest.getEntry(j));
            this.upperDifference.setEntry(j, upperBound[j] - this.currentBest.getEntry(j));
            if (this.lowerDifference.getEntry(j) >= -this.initialTrustRegionRadius) {
                if (this.lowerDifference.getEntry(j) >= 0.0) {
                    this.currentBest.setEntry(j, lowerBound[j]);
                    this.lowerDifference.setEntry(j, 0.0);
                    this.upperDifference.setEntry(j, boundDiff);
                }
                else {
                    this.currentBest.setEntry(j, lowerBound[j] + this.initialTrustRegionRadius);
                    this.lowerDifference.setEntry(j, -this.initialTrustRegionRadius);
                    final double deltaOne = upperBound[j] - this.currentBest.getEntry(j);
                    this.upperDifference.setEntry(j, FastMath.max(deltaOne, this.initialTrustRegionRadius));
                }
            }
            else if (this.upperDifference.getEntry(j) <= this.initialTrustRegionRadius) {
                if (this.upperDifference.getEntry(j) <= 0.0) {
                    this.currentBest.setEntry(j, upperBound[j]);
                    this.lowerDifference.setEntry(j, -boundDiff);
                    this.upperDifference.setEntry(j, 0.0);
                }
                else {
                    this.currentBest.setEntry(j, upperBound[j] - this.initialTrustRegionRadius);
                    final double deltaOne = lowerBound[j] - this.currentBest.getEntry(j);
                    final double deltaTwo = -this.initialTrustRegionRadius;
                    this.lowerDifference.setEntry(j, FastMath.min(deltaOne, deltaTwo));
                    this.upperDifference.setEntry(j, this.initialTrustRegionRadius);
                }
            }
        }
        return this.bobyqb(lowerBound, upperBound);
    }
    
    private double bobyqb(final double[] lowerBound, final double[] upperBound) {
        printMethod();
        final int n = this.currentBest.getDimension();
        final int npt = this.numberOfInterpolationPoints;
        final int np = n + 1;
        final int nptm = npt - np;
        final int nh = n * np / 2;
        final ArrayRealVector work1 = new ArrayRealVector(n);
        final ArrayRealVector work2 = new ArrayRealVector(npt);
        final ArrayRealVector work3 = new ArrayRealVector(npt);
        double cauchy = Double.NaN;
        double alpha = Double.NaN;
        double dsq = Double.NaN;
        double crvmin = Double.NaN;
        this.trustRegionCenterInterpolationPointIndex = 0;
        this.prelim(lowerBound, upperBound);
        double xoptsq = 0.0;
        for (int i = 0; i < n; ++i) {
            this.trustRegionCenterOffset.setEntry(i, this.interpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex, i));
            final double deltaOne = this.trustRegionCenterOffset.getEntry(i);
            xoptsq += deltaOne * deltaOne;
        }
        double fsave = this.fAtInterpolationPoints.getEntry(0);
        final int kbase = 0;
        int ntrits = 0;
        int itest = 0;
        int knew = 0;
        int nfsav = this.getEvaluations();
        double delta;
        double rho = delta = this.initialTrustRegionRadius;
        double diffa = 0.0;
        double diffb = 0.0;
        double diffc = 0.0;
        double f = 0.0;
        double beta = 0.0;
        double adelt = 0.0;
        double denom = 0.0;
        double ratio = 0.0;
        double dnorm = 0.0;
        double scaden = 0.0;
        double biglsq = 0.0;
        double distsq = 0.0;
        int state = 20;
    Label_5569:
        while (true) {
            int ih;
            double temp;
            double sum;
            double suma;
            double sumb;
            double sum3;
            double bsum;
            double dx;
            double d2;
            double sum4;
            int jp;
            double delsq;
            double hdiag;
            double d3;
            double d4;
            double den;
            double d5;
            double d6;
            double temp4;
            double d7;
            double d8;
            switch (state) {
                case 20:
                    Label_0587: {
                        printState(20);
                        if (this.trustRegionCenterInterpolationPointIndex == 0) {
                            break Label_0587;
                        }
                        ih = 0;
                        for (int j = 0; j < n; ++j) {
                            for (int k = 0; k <= j; ++k) {
                                if (k < j) {
                                    this.gradientAtTrustRegionCenter.setEntry(j, this.gradientAtTrustRegionCenter.getEntry(j) + this.modelSecondDerivativesValues.getEntry(ih) * this.trustRegionCenterOffset.getEntry(k));
                                }
                                this.gradientAtTrustRegionCenter.setEntry(k, this.gradientAtTrustRegionCenter.getEntry(k) + this.modelSecondDerivativesValues.getEntry(ih) * this.trustRegionCenterOffset.getEntry(j));
                                ++ih;
                            }
                        }
                        if (this.getEvaluations() > npt) {
                            for (int l = 0; l < npt; ++l) {
                                temp = 0.0;
                                for (int m = 0; m < n; ++m) {
                                    temp += this.interpolationPoints.getEntry(l, m) * this.trustRegionCenterOffset.getEntry(m);
                                }
                                temp *= this.modelSecondDerivativesParameters.getEntry(l);
                                for (int i2 = 0; i2 < n; ++i2) {
                                    this.gradientAtTrustRegionCenter.setEntry(i2, this.gradientAtTrustRegionCenter.getEntry(i2) + temp * this.interpolationPoints.getEntry(l, i2));
                                }
                            }
                        }
                        break Label_0587;
                    }
                case 60: {
                    printState(60);
                    final ArrayRealVector gnew = new ArrayRealVector(n);
                    final ArrayRealVector xbdi = new ArrayRealVector(n);
                    final ArrayRealVector s = new ArrayRealVector(n);
                    final ArrayRealVector hs = new ArrayRealVector(n);
                    final ArrayRealVector hred = new ArrayRealVector(n);
                    final double[] dsqCrvmin = this.trsbox(delta, gnew, xbdi, s, hs, hred);
                    dsq = dsqCrvmin[0];
                    crvmin = dsqCrvmin[1];
                    double deltaOne2 = delta;
                    final double deltaTwo = FastMath.sqrt(dsq);
                    dnorm = FastMath.min(deltaOne2, deltaTwo);
                    if (dnorm >= 0.5 * rho) {
                        ++ntrits;
                    }
                    ntrits = -1;
                    deltaOne2 = 10.0 * rho;
                    distsq = deltaOne2 * deltaOne2;
                    if (this.getEvaluations() <= nfsav + 2) {
                        state = 650;
                        continue;
                    }
                    deltaOne2 = FastMath.max(diffa, diffb);
                    final double errbig = FastMath.max(deltaOne2, diffc);
                    final double frhosq = rho * 0.125 * rho;
                    if (crvmin > 0.0 && errbig > frhosq * crvmin) {
                        state = 650;
                        continue;
                    }
                    final double bdtol = errbig / rho;
                    for (int j2 = 0; j2 < n; ++j2) {
                        double bdtest = bdtol;
                        if (this.newPoint.getEntry(j2) == this.lowerDifference.getEntry(j2)) {
                            bdtest = work1.getEntry(j2);
                        }
                        if (this.newPoint.getEntry(j2) == this.upperDifference.getEntry(j2)) {
                            bdtest = -work1.getEntry(j2);
                        }
                        if (bdtest < bdtol) {
                            double curv = this.modelSecondDerivativesValues.getEntry((j2 + j2 * j2) / 2);
                            for (int k2 = 0; k2 < npt; ++k2) {
                                final double d1 = this.interpolationPoints.getEntry(k2, j2);
                                curv += this.modelSecondDerivativesParameters.getEntry(k2) * (d1 * d1);
                            }
                            bdtest += 0.5 * curv * rho;
                            if (bdtest < bdtol) {
                                state = 650;
                                break;
                            }
                        }
                    }
                    state = 680;
                    continue;
                }
                case 90: {
                    printState(90);
                    if (dsq <= xoptsq * 0.001) {
                        final double fracsq = xoptsq * 0.25;
                        double sumpq = 0.0;
                        for (int k3 = 0; k3 < npt; ++k3) {
                            sumpq += this.modelSecondDerivativesParameters.getEntry(k3);
                            sum = -0.5 * xoptsq;
                            for (int i3 = 0; i3 < n; ++i3) {
                                sum += this.interpolationPoints.getEntry(k3, i3) * this.trustRegionCenterOffset.getEntry(i3);
                            }
                            work2.setEntry(k3, sum);
                            final double temp2 = fracsq - 0.5 * sum;
                            for (int i4 = 0; i4 < n; ++i4) {
                                work1.setEntry(i4, this.bMatrix.getEntry(k3, i4));
                                this.lagrangeValuesAtNewPoint.setEntry(i4, sum * this.interpolationPoints.getEntry(k3, i4) + temp2 * this.trustRegionCenterOffset.getEntry(i4));
                                final int ip = npt + i4;
                                for (int j3 = 0; j3 <= i4; ++j3) {
                                    this.bMatrix.setEntry(ip, j3, this.bMatrix.getEntry(ip, j3) + work1.getEntry(i4) * this.lagrangeValuesAtNewPoint.getEntry(j3) + this.lagrangeValuesAtNewPoint.getEntry(i4) * work1.getEntry(j3));
                                }
                            }
                        }
                        for (int m2 = 0; m2 < nptm; ++m2) {
                            double sumz = 0.0;
                            double sumw = 0.0;
                            for (int k4 = 0; k4 < npt; ++k4) {
                                sumz += this.zMatrix.getEntry(k4, m2);
                                this.lagrangeValuesAtNewPoint.setEntry(k4, work2.getEntry(k4) * this.zMatrix.getEntry(k4, m2));
                                sumw += this.lagrangeValuesAtNewPoint.getEntry(k4);
                            }
                            for (int j4 = 0; j4 < n; ++j4) {
                                double sum2 = (fracsq * sumz - 0.5 * sumw) * this.trustRegionCenterOffset.getEntry(j4);
                                for (int k5 = 0; k5 < npt; ++k5) {
                                    sum2 += this.lagrangeValuesAtNewPoint.getEntry(k5) * this.interpolationPoints.getEntry(k5, j4);
                                }
                                work1.setEntry(j4, sum2);
                                for (int k5 = 0; k5 < npt; ++k5) {
                                    this.bMatrix.setEntry(k5, j4, this.bMatrix.getEntry(k5, j4) + sum2 * this.zMatrix.getEntry(k5, m2));
                                }
                            }
                            for (int i4 = 0; i4 < n; ++i4) {
                                final int ip = i4 + npt;
                                final double temp3 = work1.getEntry(i4);
                                for (int j5 = 0; j5 <= i4; ++j5) {
                                    this.bMatrix.setEntry(ip, j5, this.bMatrix.getEntry(ip, j5) + temp3 * work1.getEntry(j5));
                                }
                            }
                        }
                        int ih2 = 0;
                        for (int j6 = 0; j6 < n; ++j6) {
                            work1.setEntry(j6, -0.5 * sumpq * this.trustRegionCenterOffset.getEntry(j6));
                            for (int k6 = 0; k6 < npt; ++k6) {
                                work1.setEntry(j6, work1.getEntry(j6) + this.modelSecondDerivativesParameters.getEntry(k6) * this.interpolationPoints.getEntry(k6, j6));
                                this.interpolationPoints.setEntry(k6, j6, this.interpolationPoints.getEntry(k6, j6) - this.trustRegionCenterOffset.getEntry(j6));
                            }
                            for (int i5 = 0; i5 <= j6; ++i5) {
                                this.modelSecondDerivativesValues.setEntry(ih2, this.modelSecondDerivativesValues.getEntry(ih2) + work1.getEntry(i5) * this.trustRegionCenterOffset.getEntry(j6) + this.trustRegionCenterOffset.getEntry(i5) * work1.getEntry(j6));
                                this.bMatrix.setEntry(npt + i5, j6, this.bMatrix.getEntry(npt + j6, i5));
                                ++ih2;
                            }
                        }
                        for (int i6 = 0; i6 < n; ++i6) {
                            this.originShift.setEntry(i6, this.originShift.getEntry(i6) + this.trustRegionCenterOffset.getEntry(i6));
                            this.newPoint.setEntry(i6, this.newPoint.getEntry(i6) - this.trustRegionCenterOffset.getEntry(i6));
                            this.lowerDifference.setEntry(i6, this.lowerDifference.getEntry(i6) - this.trustRegionCenterOffset.getEntry(i6));
                            this.upperDifference.setEntry(i6, this.upperDifference.getEntry(i6) - this.trustRegionCenterOffset.getEntry(i6));
                            this.trustRegionCenterOffset.setEntry(i6, 0.0);
                        }
                        xoptsq = 0.0;
                    }
                    if (ntrits == 0) {
                        state = 210;
                        continue;
                    }
                    state = 230;
                    continue;
                }
                case 210: {
                    printState(210);
                    final double[] alphaCauchy = this.altmov(knew, adelt);
                    alpha = alphaCauchy[0];
                    cauchy = alphaCauchy[1];
                    for (int i7 = 0; i7 < n; ++i7) {
                        this.trialStepPoint.setEntry(i7, this.newPoint.getEntry(i7) - this.trustRegionCenterOffset.getEntry(i7));
                    }
                }
                case 230:
                    Label_2951: {
                        printState(230);
                        for (int k7 = 0; k7 < npt; ++k7) {
                            suma = 0.0;
                            sumb = 0.0;
                            sum = 0.0;
                            for (int j7 = 0; j7 < n; ++j7) {
                                suma += this.interpolationPoints.getEntry(k7, j7) * this.trialStepPoint.getEntry(j7);
                                sumb += this.interpolationPoints.getEntry(k7, j7) * this.trustRegionCenterOffset.getEntry(j7);
                                sum += this.bMatrix.getEntry(k7, j7) * this.trialStepPoint.getEntry(j7);
                            }
                            work3.setEntry(k7, suma * (0.5 * suma + sumb));
                            this.lagrangeValuesAtNewPoint.setEntry(k7, sum);
                            work2.setEntry(k7, suma);
                        }
                        beta = 0.0;
                        for (int m3 = 0; m3 < nptm; ++m3) {
                            sum3 = 0.0;
                            for (int k8 = 0; k8 < npt; ++k8) {
                                sum3 += this.zMatrix.getEntry(k8, m3) * work3.getEntry(k8);
                            }
                            beta -= sum3 * sum3;
                            for (int k8 = 0; k8 < npt; ++k8) {
                                this.lagrangeValuesAtNewPoint.setEntry(k8, this.lagrangeValuesAtNewPoint.getEntry(k8) + sum3 * this.zMatrix.getEntry(k8, m3));
                            }
                        }
                        dsq = 0.0;
                        bsum = 0.0;
                        dx = 0.0;
                        for (int m = 0; m < n; ++m) {
                            d2 = this.trialStepPoint.getEntry(m);
                            dsq += d2 * d2;
                            sum4 = 0.0;
                            for (int k4 = 0; k4 < npt; ++k4) {
                                sum4 += work3.getEntry(k4) * this.bMatrix.getEntry(k4, m);
                            }
                            bsum += sum4 * this.trialStepPoint.getEntry(m);
                            jp = npt + m;
                            for (int i8 = 0; i8 < n; ++i8) {
                                sum4 += this.bMatrix.getEntry(jp, i8) * this.trialStepPoint.getEntry(i8);
                            }
                            this.lagrangeValuesAtNewPoint.setEntry(jp, sum4);
                            bsum += sum4 * this.trialStepPoint.getEntry(m);
                            dx += this.trialStepPoint.getEntry(m) * this.trustRegionCenterOffset.getEntry(m);
                        }
                        beta = dx * dx + dsq * (xoptsq + dx + dx + 0.5 * dsq) + beta - bsum;
                        this.lagrangeValuesAtNewPoint.setEntry(this.trustRegionCenterInterpolationPointIndex, this.lagrangeValuesAtNewPoint.getEntry(this.trustRegionCenterInterpolationPointIndex) + 1.0);
                        if (ntrits != 0) {
                            delsq = delta * delta;
                            scaden = 0.0;
                            biglsq = 0.0;
                            knew = 0;
                            for (int k6 = 0; k6 < npt; ++k6) {
                                if (k6 != this.trustRegionCenterInterpolationPointIndex) {
                                    hdiag = 0.0;
                                    for (int m4 = 0; m4 < nptm; ++m4) {
                                        d3 = this.zMatrix.getEntry(k6, m4);
                                        hdiag += d3 * d3;
                                    }
                                    d4 = this.lagrangeValuesAtNewPoint.getEntry(k6);
                                    den = beta * hdiag + d4 * d4;
                                    distsq = 0.0;
                                    for (int j5 = 0; j5 < n; ++j5) {
                                        d5 = this.interpolationPoints.getEntry(k6, j5) - this.trustRegionCenterOffset.getEntry(j5);
                                        distsq += d5 * d5;
                                    }
                                    d6 = distsq / delsq;
                                    temp4 = FastMath.max(1.0, d6 * d6);
                                    if (temp4 * den > scaden) {
                                        scaden = temp4 * den;
                                        knew = k6;
                                        denom = den;
                                    }
                                    d7 = this.lagrangeValuesAtNewPoint.getEntry(k6);
                                    biglsq = FastMath.max(biglsq, temp4 * (d7 * d7));
                                }
                            }
                            break Label_2951;
                        }
                        d8 = this.lagrangeValuesAtNewPoint.getEntry(knew);
                        denom = d8 * d8 + alpha * beta;
                        if (denom < cauchy && cauchy > 0.0) {
                            for (int i5 = 0; i5 < n; ++i5) {
                                this.newPoint.setEntry(i5, this.alternativeNewPoint.getEntry(i5));
                                this.trialStepPoint.setEntry(i5, this.newPoint.getEntry(i5) - this.trustRegionCenterOffset.getEntry(i5));
                            }
                            cauchy = 0.0;
                            state = 230;
                            continue;
                        }
                        break Label_2951;
                    }
                case 360: {
                    printState(360);
                    for (int i9 = 0; i9 < n; ++i9) {
                        final double d9 = lowerBound[i9];
                        final double d10 = this.originShift.getEntry(i9) + this.newPoint.getEntry(i9);
                        d2 = FastMath.max(d9, d10);
                        final double d11 = upperBound[i9];
                        this.currentBest.setEntry(i9, FastMath.min(d2, d11));
                        if (this.newPoint.getEntry(i9) == this.lowerDifference.getEntry(i9)) {
                            this.currentBest.setEntry(i9, lowerBound[i9]);
                        }
                        if (this.newPoint.getEntry(i9) == this.upperDifference.getEntry(i9)) {
                            this.currentBest.setEntry(i9, upperBound[i9]);
                        }
                    }
                    f = this.computeObjectiveValue(this.currentBest.toArray());
                    if (!this.isMinimize) {
                        f = -f;
                    }
                    if (ntrits == -1) {
                        fsave = f;
                        state = 720;
                        continue;
                    }
                    final double fopt = this.fAtInterpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex);
                    double vquad = 0.0;
                    int ih2 = 0;
                    for (int j6 = 0; j6 < n; ++j6) {
                        vquad += this.trialStepPoint.getEntry(j6) * this.gradientAtTrustRegionCenter.getEntry(j6);
                        for (int i5 = 0; i5 <= j6; ++i5) {
                            double temp2 = this.trialStepPoint.getEntry(i5) * this.trialStepPoint.getEntry(j6);
                            if (i5 == j6) {
                                temp2 *= 0.5;
                            }
                            vquad += this.modelSecondDerivativesValues.getEntry(ih2) * temp2;
                            ++ih2;
                        }
                    }
                    for (int k9 = 0; k9 < npt; ++k9) {
                        final double d12 = work2.getEntry(k9);
                        final double d13 = d12 * d12;
                        vquad += 0.5 * this.modelSecondDerivativesParameters.getEntry(k9) * d13;
                    }
                    final double diff = f - fopt - vquad;
                    diffc = diffb;
                    diffb = diffa;
                    diffa = FastMath.abs(diff);
                    if (dnorm > rho) {
                        nfsav = this.getEvaluations();
                    }
                    if (ntrits > 0) {
                        if (vquad >= 0.0) {
                            throw new MathIllegalStateException(LocalizedFormats.TRUST_REGION_STEP_FAILED, new Object[] { vquad });
                        }
                        ratio = (f - fopt) / vquad;
                        final double hDelta = 0.5 * delta;
                        if (ratio <= 0.1) {
                            delta = FastMath.min(hDelta, dnorm);
                        }
                        else if (ratio <= 0.7) {
                            delta = FastMath.max(hDelta, dnorm);
                        }
                        else {
                            delta = FastMath.max(hDelta, 2.0 * dnorm);
                        }
                        if (delta <= rho * 1.5) {
                            delta = rho;
                        }
                        if (f < fopt) {
                            final int ksav = knew;
                            final double densav = denom;
                            final double delsq2 = delta * delta;
                            scaden = 0.0;
                            biglsq = 0.0;
                            knew = 0;
                            for (int k10 = 0; k10 < npt; ++k10) {
                                double hdiag2 = 0.0;
                                for (int m5 = 0; m5 < nptm; ++m5) {
                                    final double d14 = this.zMatrix.getEntry(k10, m5);
                                    hdiag2 += d14 * d14;
                                }
                                final double d15 = this.lagrangeValuesAtNewPoint.getEntry(k10);
                                final double den2 = beta * hdiag2 + d15 * d15;
                                distsq = 0.0;
                                for (int j8 = 0; j8 < n; ++j8) {
                                    final double d16 = this.interpolationPoints.getEntry(k10, j8) - this.newPoint.getEntry(j8);
                                    distsq += d16 * d16;
                                }
                                final double d17 = distsq / delsq2;
                                final double temp5 = FastMath.max(1.0, d17 * d17);
                                if (temp5 * den2 > scaden) {
                                    scaden = temp5 * den2;
                                    knew = k10;
                                    denom = den2;
                                }
                                final double d18 = this.lagrangeValuesAtNewPoint.getEntry(k10);
                                final double d19 = temp5 * (d18 * d18);
                                biglsq = FastMath.max(biglsq, d19);
                            }
                            if (scaden <= 0.5 * biglsq) {
                                knew = ksav;
                                denom = densav;
                            }
                        }
                    }
                    this.update(beta, denom, knew);
                    ih2 = 0;
                    final double pqold = this.modelSecondDerivativesParameters.getEntry(knew);
                    this.modelSecondDerivativesParameters.setEntry(knew, 0.0);
                    for (int i4 = 0; i4 < n; ++i4) {
                        final double temp6 = pqold * this.interpolationPoints.getEntry(knew, i4);
                        for (int j9 = 0; j9 <= i4; ++j9) {
                            this.modelSecondDerivativesValues.setEntry(ih2, this.modelSecondDerivativesValues.getEntry(ih2) + temp6 * this.interpolationPoints.getEntry(knew, j9));
                            ++ih2;
                        }
                    }
                    for (int m4 = 0; m4 < nptm; ++m4) {
                        final double temp6 = diff * this.zMatrix.getEntry(knew, m4);
                        for (int k5 = 0; k5 < npt; ++k5) {
                            this.modelSecondDerivativesParameters.setEntry(k5, this.modelSecondDerivativesParameters.getEntry(k5) + temp6 * this.zMatrix.getEntry(k5, m4));
                        }
                    }
                    this.fAtInterpolationPoints.setEntry(knew, f);
                    for (int i4 = 0; i4 < n; ++i4) {
                        this.interpolationPoints.setEntry(knew, i4, this.newPoint.getEntry(i4));
                        work1.setEntry(i4, this.bMatrix.getEntry(knew, i4));
                    }
                    for (int k4 = 0; k4 < npt; ++k4) {
                        double suma2 = 0.0;
                        for (int m6 = 0; m6 < nptm; ++m6) {
                            suma2 += this.zMatrix.getEntry(knew, m6) * this.zMatrix.getEntry(k4, m6);
                        }
                        double sumb2 = 0.0;
                        for (int j10 = 0; j10 < n; ++j10) {
                            sumb2 += this.interpolationPoints.getEntry(k4, j10) * this.trustRegionCenterOffset.getEntry(j10);
                        }
                        final double temp7 = suma2 * sumb2;
                        for (int i10 = 0; i10 < n; ++i10) {
                            work1.setEntry(i10, work1.getEntry(i10) + temp7 * this.interpolationPoints.getEntry(k4, i10));
                        }
                    }
                    for (int i4 = 0; i4 < n; ++i4) {
                        this.gradientAtTrustRegionCenter.setEntry(i4, this.gradientAtTrustRegionCenter.getEntry(i4) + diff * work1.getEntry(i4));
                    }
                    if (f < fopt) {
                        this.trustRegionCenterInterpolationPointIndex = knew;
                        xoptsq = 0.0;
                        ih2 = 0;
                        for (int j4 = 0; j4 < n; ++j4) {
                            this.trustRegionCenterOffset.setEntry(j4, this.newPoint.getEntry(j4));
                            d3 = this.trustRegionCenterOffset.getEntry(j4);
                            xoptsq += d3 * d3;
                            for (int i11 = 0; i11 <= j4; ++i11) {
                                if (i11 < j4) {
                                    this.gradientAtTrustRegionCenter.setEntry(j4, this.gradientAtTrustRegionCenter.getEntry(j4) + this.modelSecondDerivativesValues.getEntry(ih2) * this.trialStepPoint.getEntry(i11));
                                }
                                this.gradientAtTrustRegionCenter.setEntry(i11, this.gradientAtTrustRegionCenter.getEntry(i11) + this.modelSecondDerivativesValues.getEntry(ih2) * this.trialStepPoint.getEntry(j4));
                                ++ih2;
                            }
                        }
                        for (int k4 = 0; k4 < npt; ++k4) {
                            double temp6 = 0.0;
                            for (int j9 = 0; j9 < n; ++j9) {
                                temp6 += this.interpolationPoints.getEntry(k4, j9) * this.trialStepPoint.getEntry(j9);
                            }
                            temp6 *= this.modelSecondDerivativesParameters.getEntry(k4);
                            for (int i11 = 0; i11 < n; ++i11) {
                                this.gradientAtTrustRegionCenter.setEntry(i11, this.gradientAtTrustRegionCenter.getEntry(i11) + temp6 * this.interpolationPoints.getEntry(k4, i11));
                            }
                        }
                    }
                    if (ntrits > 0) {
                        for (int k4 = 0; k4 < npt; ++k4) {
                            this.lagrangeValuesAtNewPoint.setEntry(k4, this.fAtInterpolationPoints.getEntry(k4) - this.fAtInterpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex));
                            work3.setEntry(k4, 0.0);
                        }
                        for (int j4 = 0; j4 < nptm; ++j4) {
                            double sum2 = 0.0;
                            for (int k5 = 0; k5 < npt; ++k5) {
                                sum2 += this.zMatrix.getEntry(k5, j4) * this.lagrangeValuesAtNewPoint.getEntry(k5);
                            }
                            for (int k5 = 0; k5 < npt; ++k5) {
                                work3.setEntry(k5, work3.getEntry(k5) + sum2 * this.zMatrix.getEntry(k5, j4));
                            }
                        }
                        for (int k4 = 0; k4 < npt; ++k4) {
                            double sum2 = 0.0;
                            for (int j9 = 0; j9 < n; ++j9) {
                                sum2 += this.interpolationPoints.getEntry(k4, j9) * this.trustRegionCenterOffset.getEntry(j9);
                            }
                            work2.setEntry(k4, work3.getEntry(k4));
                            work3.setEntry(k4, sum2 * work3.getEntry(k4));
                        }
                        double gqsq = 0.0;
                        double gisq = 0.0;
                        for (int i12 = 0; i12 < n; ++i12) {
                            double sum5 = 0.0;
                            for (int k11 = 0; k11 < npt; ++k11) {
                                sum5 += this.bMatrix.getEntry(k11, i12) * this.lagrangeValuesAtNewPoint.getEntry(k11) + this.interpolationPoints.getEntry(k11, i12) * work3.getEntry(k11);
                            }
                            if (this.trustRegionCenterOffset.getEntry(i12) == this.lowerDifference.getEntry(i12)) {
                                final double d20 = FastMath.min(0.0, this.gradientAtTrustRegionCenter.getEntry(i12));
                                gqsq += d20 * d20;
                                final double d21 = FastMath.min(0.0, sum5);
                                gisq += d21 * d21;
                            }
                            else if (this.trustRegionCenterOffset.getEntry(i12) == this.upperDifference.getEntry(i12)) {
                                final double d20 = FastMath.max(0.0, this.gradientAtTrustRegionCenter.getEntry(i12));
                                gqsq += d20 * d20;
                                final double d21 = FastMath.max(0.0, sum5);
                                gisq += d21 * d21;
                            }
                            else {
                                final double d20 = this.gradientAtTrustRegionCenter.getEntry(i12);
                                gqsq += d20 * d20;
                                gisq += sum5 * sum5;
                            }
                            this.lagrangeValuesAtNewPoint.setEntry(npt + i12, sum5);
                        }
                        ++itest;
                        if (gqsq < 10.0 * gisq) {
                            itest = 0;
                        }
                        if (itest >= 3) {
                            for (int i12 = 0, max = FastMath.max(npt, nh); i12 < max; ++i12) {
                                if (i12 < n) {
                                    this.gradientAtTrustRegionCenter.setEntry(i12, this.lagrangeValuesAtNewPoint.getEntry(npt + i12));
                                }
                                if (i12 < npt) {
                                    this.modelSecondDerivativesParameters.setEntry(i12, work2.getEntry(i12));
                                }
                                if (i12 < nh) {
                                    this.modelSecondDerivativesValues.setEntry(i12, 0.0);
                                }
                                itest = 0;
                            }
                        }
                    }
                    if (ntrits == 0) {
                        state = 60;
                        continue;
                    }
                    if (f <= fopt + 0.1 * vquad) {
                        state = 60;
                        continue;
                    }
                    final double d22 = 2.0 * delta;
                    final double d23 = 10.0 * rho;
                    distsq = FastMath.max(d22 * d22, d23 * d23);
                }
                case 650: {
                    printState(650);
                    knew = -1;
                    for (int k7 = 0; k7 < npt; ++k7) {
                        sum3 = 0.0;
                        for (int j11 = 0; j11 < n; ++j11) {
                            d8 = this.interpolationPoints.getEntry(k7, j11) - this.trustRegionCenterOffset.getEntry(j11);
                            sum3 += d8 * d8;
                        }
                        if (sum3 > distsq) {
                            knew = k7;
                            distsq = sum3;
                        }
                    }
                    if (knew >= 0) {
                        final double dist = FastMath.sqrt(distsq);
                        if (ntrits == -1) {
                            delta = FastMath.min(0.1 * delta, 0.5 * dist);
                            if (delta <= rho * 1.5) {
                                delta = rho;
                            }
                        }
                        ntrits = 0;
                        final double d24 = FastMath.min(0.1 * dist, delta);
                        adelt = FastMath.max(d24, rho);
                        dsq = adelt * adelt;
                        state = 90;
                        continue;
                    }
                    if (ntrits == -1) {
                        state = 680;
                        continue;
                    }
                    if (ratio > 0.0) {
                        state = 60;
                        continue;
                    }
                    if (FastMath.max(delta, dnorm) > rho) {
                        state = 60;
                        continue;
                    }
                }
                case 680: {
                    printState(680);
                    if (rho > this.stoppingTrustRegionRadius) {
                        delta = 0.5 * rho;
                        ratio = rho / this.stoppingTrustRegionRadius;
                        if (ratio <= 16.0) {
                            rho = this.stoppingTrustRegionRadius;
                        }
                        else if (ratio <= 250.0) {
                            rho = FastMath.sqrt(ratio) * this.stoppingTrustRegionRadius;
                        }
                        else {
                            rho *= 0.1;
                        }
                        delta = FastMath.max(delta, rho);
                        ntrits = 0;
                        nfsav = this.getEvaluations();
                        state = 60;
                        continue;
                    }
                    if (ntrits == -1) {
                        state = 360;
                        continue;
                    }
                    break Label_5569;
                }
                case 720: {
                    break Label_5569;
                }
                default: {
                    throw new MathIllegalStateException(LocalizedFormats.SIMPLE_MESSAGE, new Object[] { "bobyqb" });
                }
            }
        }
        printState(720);
        if (this.fAtInterpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex) <= fsave) {
            for (int i9 = 0; i9 < n; ++i9) {
                final double d9 = lowerBound[i9];
                final double d10 = this.originShift.getEntry(i9) + this.trustRegionCenterOffset.getEntry(i9);
                final double d2 = FastMath.max(d9, d10);
                final double d11 = upperBound[i9];
                this.currentBest.setEntry(i9, FastMath.min(d2, d11));
                if (this.trustRegionCenterOffset.getEntry(i9) == this.lowerDifference.getEntry(i9)) {
                    this.currentBest.setEntry(i9, lowerBound[i9]);
                }
                if (this.trustRegionCenterOffset.getEntry(i9) == this.upperDifference.getEntry(i9)) {
                    this.currentBest.setEntry(i9, upperBound[i9]);
                }
            }
            f = this.fAtInterpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex);
        }
        return f;
    }
    
    private double[] altmov(final int knew, final double adelt) {
        printMethod();
        final int n = this.currentBest.getDimension();
        final int npt = this.numberOfInterpolationPoints;
        final ArrayRealVector glag = new ArrayRealVector(n);
        final ArrayRealVector hcol = new ArrayRealVector(npt);
        final ArrayRealVector work1 = new ArrayRealVector(n);
        final ArrayRealVector work2 = new ArrayRealVector(n);
        for (int k = 0; k < npt; ++k) {
            hcol.setEntry(k, 0.0);
        }
        for (int j = 0, max = npt - n - 1; j < max; ++j) {
            final double tmp = this.zMatrix.getEntry(knew, j);
            for (int i = 0; i < npt; ++i) {
                hcol.setEntry(i, hcol.getEntry(i) + tmp * this.zMatrix.getEntry(i, j));
            }
        }
        final double alpha = hcol.getEntry(knew);
        final double ha = 0.5 * alpha;
        for (int l = 0; l < n; ++l) {
            glag.setEntry(l, this.bMatrix.getEntry(knew, l));
        }
        for (int i = 0; i < npt; ++i) {
            double tmp2 = 0.0;
            for (int m = 0; m < n; ++m) {
                tmp2 += this.interpolationPoints.getEntry(i, m) * this.trustRegionCenterOffset.getEntry(m);
            }
            tmp2 *= hcol.getEntry(i);
            for (int i2 = 0; i2 < n; ++i2) {
                glag.setEntry(i2, glag.getEntry(i2) + tmp2 * this.interpolationPoints.getEntry(i, i2));
            }
        }
        double presav = 0.0;
        double step = Double.NaN;
        int ksav = 0;
        int ibdsav = 0;
        double stpsav = 0.0;
        for (int k2 = 0; k2 < npt; ++k2) {
            if (k2 != this.trustRegionCenterInterpolationPointIndex) {
                double dderiv = 0.0;
                double distsq = 0.0;
                for (int i3 = 0; i3 < n; ++i3) {
                    final double tmp3 = this.interpolationPoints.getEntry(k2, i3) - this.trustRegionCenterOffset.getEntry(i3);
                    dderiv += glag.getEntry(i3) * tmp3;
                    distsq += tmp3 * tmp3;
                }
                double subd = adelt / FastMath.sqrt(distsq);
                double slbd = -subd;
                int ilbd = 0;
                int iubd = 0;
                final double sumin = FastMath.min(1.0, subd);
                for (int i4 = 0; i4 < n; ++i4) {
                    final double tmp4 = this.interpolationPoints.getEntry(k2, i4) - this.trustRegionCenterOffset.getEntry(i4);
                    if (tmp4 > 0.0) {
                        if (slbd * tmp4 < this.lowerDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) {
                            slbd = (this.lowerDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) / tmp4;
                            ilbd = -i4 - 1;
                        }
                        if (subd * tmp4 > this.upperDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) {
                            subd = FastMath.max(sumin, (this.upperDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) / tmp4);
                            iubd = i4 + 1;
                        }
                    }
                    else if (tmp4 < 0.0) {
                        if (slbd * tmp4 > this.upperDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) {
                            slbd = (this.upperDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) / tmp4;
                            ilbd = i4 + 1;
                        }
                        if (subd * tmp4 < this.lowerDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) {
                            subd = FastMath.max(sumin, (this.lowerDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) / tmp4);
                            iubd = -i4 - 1;
                        }
                    }
                }
                step = slbd;
                int isbd = ilbd;
                double vlag = Double.NaN;
                if (k2 == knew) {
                    final double diff = dderiv - 1.0;
                    vlag = slbd * (dderiv - slbd * diff);
                    final double d1 = subd * (dderiv - subd * diff);
                    if (FastMath.abs(d1) > FastMath.abs(vlag)) {
                        step = subd;
                        vlag = d1;
                        isbd = iubd;
                    }
                    final double d2 = 0.5 * dderiv;
                    final double d3 = d2 - diff * slbd;
                    final double d4 = d2 - diff * subd;
                    if (d3 * d4 < 0.0) {
                        final double d5 = d2 * d2 / diff;
                        if (FastMath.abs(d5) > FastMath.abs(vlag)) {
                            step = d2 / diff;
                            vlag = d5;
                            isbd = 0;
                        }
                    }
                }
                else {
                    vlag = slbd * (1.0 - slbd);
                    final double tmp5 = subd * (1.0 - subd);
                    if (FastMath.abs(tmp5) > FastMath.abs(vlag)) {
                        step = subd;
                        vlag = tmp5;
                        isbd = iubd;
                    }
                    if (subd > 0.5 && FastMath.abs(vlag) < 0.25) {
                        step = 0.5;
                        vlag = 0.25;
                        isbd = 0;
                    }
                    vlag *= dderiv;
                }
                final double tmp5 = step * (1.0 - step) * distsq;
                final double predsq = vlag * vlag * (vlag * vlag + ha * tmp5 * tmp5);
                if (predsq > presav) {
                    presav = predsq;
                    ksav = k2;
                    stpsav = step;
                    ibdsav = isbd;
                }
            }
        }
        for (int i5 = 0; i5 < n; ++i5) {
            final double tmp6 = this.trustRegionCenterOffset.getEntry(i5) + stpsav * (this.interpolationPoints.getEntry(ksav, i5) - this.trustRegionCenterOffset.getEntry(i5));
            this.newPoint.setEntry(i5, FastMath.max(this.lowerDifference.getEntry(i5), FastMath.min(this.upperDifference.getEntry(i5), tmp6)));
        }
        if (ibdsav < 0) {
            this.newPoint.setEntry(-ibdsav - 1, this.lowerDifference.getEntry(-ibdsav - 1));
        }
        if (ibdsav > 0) {
            this.newPoint.setEntry(ibdsav - 1, this.upperDifference.getEntry(ibdsav - 1));
        }
        final double bigstp = adelt + adelt;
        int iflag = 0;
        double cauchy = Double.NaN;
        double csave = 0.0;
        while (true) {
            double wfixsq = 0.0;
            double ggfree = 0.0;
            for (int i6 = 0; i6 < n; ++i6) {
                final double glagValue = glag.getEntry(i6);
                work1.setEntry(i6, 0.0);
                if (FastMath.min(this.trustRegionCenterOffset.getEntry(i6) - this.lowerDifference.getEntry(i6), glagValue) > 0.0 || FastMath.max(this.trustRegionCenterOffset.getEntry(i6) - this.upperDifference.getEntry(i6), glagValue) < 0.0) {
                    work1.setEntry(i6, bigstp);
                    ggfree += glagValue * glagValue;
                }
            }
            if (ggfree == 0.0) {
                return new double[] { alpha, 0.0 };
            }
            final double tmp7 = adelt * adelt - wfixsq;
            if (tmp7 > 0.0) {
                step = FastMath.sqrt(tmp7 / ggfree);
                ggfree = 0.0;
                for (int i4 = 0; i4 < n; ++i4) {
                    if (work1.getEntry(i4) == bigstp) {
                        final double tmp8 = this.trustRegionCenterOffset.getEntry(i4) - step * glag.getEntry(i4);
                        if (tmp8 <= this.lowerDifference.getEntry(i4)) {
                            work1.setEntry(i4, this.lowerDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4));
                            final double d6 = work1.getEntry(i4);
                            wfixsq += d6 * d6;
                        }
                        else if (tmp8 >= this.upperDifference.getEntry(i4)) {
                            work1.setEntry(i4, this.upperDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4));
                            final double d6 = work1.getEntry(i4);
                            wfixsq += d6 * d6;
                        }
                        else {
                            final double d6 = glag.getEntry(i4);
                            ggfree += d6 * d6;
                        }
                    }
                }
            }
            double gw = 0.0;
            for (int i7 = 0; i7 < n; ++i7) {
                final double glagValue2 = glag.getEntry(i7);
                if (work1.getEntry(i7) == bigstp) {
                    work1.setEntry(i7, -step * glagValue2);
                    final double min = FastMath.min(this.upperDifference.getEntry(i7), this.trustRegionCenterOffset.getEntry(i7) + work1.getEntry(i7));
                    this.alternativeNewPoint.setEntry(i7, FastMath.max(this.lowerDifference.getEntry(i7), min));
                }
                else if (work1.getEntry(i7) == 0.0) {
                    this.alternativeNewPoint.setEntry(i7, this.trustRegionCenterOffset.getEntry(i7));
                }
                else if (glagValue2 > 0.0) {
                    this.alternativeNewPoint.setEntry(i7, this.lowerDifference.getEntry(i7));
                }
                else {
                    this.alternativeNewPoint.setEntry(i7, this.upperDifference.getEntry(i7));
                }
                gw += glagValue2 * work1.getEntry(i7);
            }
            double curv = 0.0;
            for (int k3 = 0; k3 < npt; ++k3) {
                double tmp9 = 0.0;
                for (int j2 = 0; j2 < n; ++j2) {
                    tmp9 += this.interpolationPoints.getEntry(k3, j2) * work1.getEntry(j2);
                }
                curv += hcol.getEntry(k3) * tmp9 * tmp9;
            }
            if (iflag == 1) {
                curv = -curv;
            }
            if (curv > -gw && curv < -gw * (1.0 + FastMath.sqrt(2.0))) {
                final double scale = -gw / curv;
                for (int i8 = 0; i8 < n; ++i8) {
                    final double tmp10 = this.trustRegionCenterOffset.getEntry(i8) + scale * work1.getEntry(i8);
                    this.alternativeNewPoint.setEntry(i8, FastMath.max(this.lowerDifference.getEntry(i8), FastMath.min(this.upperDifference.getEntry(i8), tmp10)));
                }
                final double d7 = 0.5 * gw * scale;
                cauchy = d7 * d7;
            }
            else {
                final double d8 = gw + 0.5 * curv;
                cauchy = d8 * d8;
            }
            if (iflag != 0) {
                if (csave > cauchy) {
                    for (int i9 = 0; i9 < n; ++i9) {
                        this.alternativeNewPoint.setEntry(i9, work2.getEntry(i9));
                    }
                    cauchy = csave;
                }
                return new double[] { alpha, cauchy };
            }
            for (int i10 = 0; i10 < n; ++i10) {
                glag.setEntry(i10, -glag.getEntry(i10));
                work2.setEntry(i10, this.alternativeNewPoint.getEntry(i10));
            }
            csave = cauchy;
            iflag = 1;
        }
    }
    
    private void prelim(final double[] lowerBound, final double[] upperBound) {
        printMethod();
        final int n = this.currentBest.getDimension();
        final int npt = this.numberOfInterpolationPoints;
        final int ndim = this.bMatrix.getRowDimension();
        final double rhosq = this.initialTrustRegionRadius * this.initialTrustRegionRadius;
        final double recip = 1.0 / rhosq;
        final int np = n + 1;
        for (int j = 0; j < n; ++j) {
            this.originShift.setEntry(j, this.currentBest.getEntry(j));
            for (int k = 0; k < npt; ++k) {
                this.interpolationPoints.setEntry(k, j, 0.0);
            }
            for (int i = 0; i < ndim; ++i) {
                this.bMatrix.setEntry(i, j, 0.0);
            }
        }
        for (int l = 0, max = n * np / 2; l < max; ++l) {
            this.modelSecondDerivativesValues.setEntry(l, 0.0);
        }
        for (int m = 0; m < npt; ++m) {
            this.modelSecondDerivativesParameters.setEntry(m, 0.0);
            for (int j2 = 0, max2 = npt - np; j2 < max2; ++j2) {
                this.zMatrix.setEntry(m, j2, 0.0);
            }
        }
        int ipt = 0;
        int jpt = 0;
        double fbeg = Double.NaN;
        do {
            final int nfm = this.getEvaluations();
            final int nfx = nfm - n;
            final int nfmm = nfm - 1;
            final int nfxm = nfx - 1;
            double stepa = 0.0;
            double stepb = 0.0;
            if (nfm <= 2 * n) {
                if (nfm >= 1 && nfm <= n) {
                    stepa = this.initialTrustRegionRadius;
                    if (this.upperDifference.getEntry(nfmm) == 0.0) {
                        stepa = -stepa;
                    }
                    this.interpolationPoints.setEntry(nfm, nfmm, stepa);
                }
                else if (nfm > n) {
                    stepa = this.interpolationPoints.getEntry(nfx, nfxm);
                    stepb = -this.initialTrustRegionRadius;
                    if (this.lowerDifference.getEntry(nfxm) == 0.0) {
                        stepb = FastMath.min(2.0 * this.initialTrustRegionRadius, this.upperDifference.getEntry(nfxm));
                    }
                    if (this.upperDifference.getEntry(nfxm) == 0.0) {
                        stepb = FastMath.max(-2.0 * this.initialTrustRegionRadius, this.lowerDifference.getEntry(nfxm));
                    }
                    this.interpolationPoints.setEntry(nfm, nfxm, stepb);
                }
            }
            else {
                final int tmp1 = (nfm - np) / n;
                jpt = nfm - tmp1 * n - n;
                ipt = jpt + tmp1;
                if (ipt > n) {
                    final int tmp2 = jpt;
                    jpt = ipt - n;
                    ipt = tmp2;
                }
                final int iptMinus1 = ipt - 1;
                final int jptMinus1 = jpt - 1;
                this.interpolationPoints.setEntry(nfm, iptMinus1, this.interpolationPoints.getEntry(ipt, iptMinus1));
                this.interpolationPoints.setEntry(nfm, jptMinus1, this.interpolationPoints.getEntry(jpt, jptMinus1));
            }
            for (int j3 = 0; j3 < n; ++j3) {
                this.currentBest.setEntry(j3, FastMath.min(FastMath.max(lowerBound[j3], this.originShift.getEntry(j3) + this.interpolationPoints.getEntry(nfm, j3)), upperBound[j3]));
                if (this.interpolationPoints.getEntry(nfm, j3) == this.lowerDifference.getEntry(j3)) {
                    this.currentBest.setEntry(j3, lowerBound[j3]);
                }
                if (this.interpolationPoints.getEntry(nfm, j3) == this.upperDifference.getEntry(j3)) {
                    this.currentBest.setEntry(j3, upperBound[j3]);
                }
            }
            final double objectiveValue = this.computeObjectiveValue(this.currentBest.toArray());
            final double f = this.isMinimize ? objectiveValue : (-objectiveValue);
            final int numEval = this.getEvaluations();
            this.fAtInterpolationPoints.setEntry(nfm, f);
            if (numEval == 1) {
                fbeg = f;
                this.trustRegionCenterInterpolationPointIndex = 0;
            }
            else if (f < this.fAtInterpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex)) {
                this.trustRegionCenterInterpolationPointIndex = nfm;
            }
            if (numEval <= 2 * n + 1) {
                if (numEval >= 2 && numEval <= n + 1) {
                    this.gradientAtTrustRegionCenter.setEntry(nfmm, (f - fbeg) / stepa);
                    if (npt >= numEval + n) {
                        continue;
                    }
                    final double oneOverStepA = 1.0 / stepa;
                    this.bMatrix.setEntry(0, nfmm, -oneOverStepA);
                    this.bMatrix.setEntry(nfm, nfmm, oneOverStepA);
                    this.bMatrix.setEntry(npt + nfmm, nfmm, -0.5 * rhosq);
                }
                else {
                    if (numEval < n + 2) {
                        continue;
                    }
                    final int ih = nfx * (nfx + 1) / 2 - 1;
                    final double tmp3 = (f - fbeg) / stepb;
                    final double diff = stepb - stepa;
                    this.modelSecondDerivativesValues.setEntry(ih, 2.0 * (tmp3 - this.gradientAtTrustRegionCenter.getEntry(nfxm)) / diff);
                    this.gradientAtTrustRegionCenter.setEntry(nfxm, (this.gradientAtTrustRegionCenter.getEntry(nfxm) * stepb - tmp3 * stepa) / diff);
                    if (stepa * stepb < 0.0 && f < this.fAtInterpolationPoints.getEntry(nfm - n)) {
                        this.fAtInterpolationPoints.setEntry(nfm, this.fAtInterpolationPoints.getEntry(nfm - n));
                        this.fAtInterpolationPoints.setEntry(nfm - n, f);
                        if (this.trustRegionCenterInterpolationPointIndex == nfm) {
                            this.trustRegionCenterInterpolationPointIndex = nfm - n;
                        }
                        this.interpolationPoints.setEntry(nfm - n, nfxm, stepb);
                        this.interpolationPoints.setEntry(nfm, nfxm, stepa);
                    }
                    this.bMatrix.setEntry(0, nfxm, -(stepa + stepb) / (stepa * stepb));
                    this.bMatrix.setEntry(nfm, nfxm, -0.5 / this.interpolationPoints.getEntry(nfm - n, nfxm));
                    this.bMatrix.setEntry(nfm - n, nfxm, -this.bMatrix.getEntry(0, nfxm) - this.bMatrix.getEntry(nfm, nfxm));
                    this.zMatrix.setEntry(0, nfxm, FastMath.sqrt(2.0) / (stepa * stepb));
                    this.zMatrix.setEntry(nfm, nfxm, FastMath.sqrt(0.5) / rhosq);
                    this.zMatrix.setEntry(nfm - n, nfxm, -this.zMatrix.getEntry(0, nfxm) - this.zMatrix.getEntry(nfm, nfxm));
                }
            }
            else {
                this.zMatrix.setEntry(0, nfxm, recip);
                this.zMatrix.setEntry(nfm, nfxm, recip);
                this.zMatrix.setEntry(ipt, nfxm, -recip);
                this.zMatrix.setEntry(jpt, nfxm, -recip);
                final int ih = ipt * (ipt - 1) / 2 + jpt - 1;
                final double tmp3 = this.interpolationPoints.getEntry(nfm, ipt - 1) * this.interpolationPoints.getEntry(nfm, jpt - 1);
                this.modelSecondDerivativesValues.setEntry(ih, (fbeg - this.fAtInterpolationPoints.getEntry(ipt) - this.fAtInterpolationPoints.getEntry(jpt) + f) / tmp3);
            }
        } while (this.getEvaluations() < npt);
    }
    
    private double[] trsbox(final double delta, final ArrayRealVector gnew, final ArrayRealVector xbdi, final ArrayRealVector s, final ArrayRealVector hs, final ArrayRealVector hred) {
        printMethod();
        final int n = this.currentBest.getDimension();
        final int npt = this.numberOfInterpolationPoints;
        double dsq = Double.NaN;
        double crvmin = Double.NaN;
        double beta = 0.0;
        int iact = -1;
        int nact = 0;
        double angt = 0.0;
        double temp = 0.0;
        double xsav = 0.0;
        double xsum = 0.0;
        double angbd = 0.0;
        double dredg = 0.0;
        double sredg = 0.0;
        double resid = 0.0;
        double delsq = 0.0;
        double ggsav = 0.0;
        double tempa = 0.0;
        double tempb = 0.0;
        double redmax = 0.0;
        double dredsq = 0.0;
        double redsav = 0.0;
        double gredsq = 0.0;
        double rednew = 0.0;
        int itcsav = 0;
        double rdprev = 0.0;
        double rdnext = 0.0;
        double stplen = 0.0;
        double stepsq = 0.0;
        int itermax = 0;
        int iterc = 0;
        nact = 0;
        for (int i = 0; i < n; ++i) {
            xbdi.setEntry(i, 0.0);
            if (this.trustRegionCenterOffset.getEntry(i) <= this.lowerDifference.getEntry(i)) {
                if (this.gradientAtTrustRegionCenter.getEntry(i) >= 0.0) {
                    xbdi.setEntry(i, -1.0);
                }
            }
            else if (this.trustRegionCenterOffset.getEntry(i) >= this.upperDifference.getEntry(i) && this.gradientAtTrustRegionCenter.getEntry(i) <= 0.0) {
                xbdi.setEntry(i, 1.0);
            }
            if (xbdi.getEntry(i) != 0.0) {
                ++nact;
            }
            this.trialStepPoint.setEntry(i, 0.0);
            gnew.setEntry(i, this.gradientAtTrustRegionCenter.getEntry(i));
        }
        delsq = delta * delta;
        double qred = 0.0;
        crvmin = -1.0;
        int state = 20;
    Label_2477:
        while (true) {
            switch (state) {
                case 20: {
                    printState(20);
                    beta = 0.0;
                }
                case 30: {
                    printState(30);
                    stepsq = 0.0;
                    for (int j = 0; j < n; ++j) {
                        if (xbdi.getEntry(j) != 0.0) {
                            s.setEntry(j, 0.0);
                        }
                        else if (beta == 0.0) {
                            s.setEntry(j, -gnew.getEntry(j));
                        }
                        else {
                            s.setEntry(j, beta * s.getEntry(j) - gnew.getEntry(j));
                        }
                        final double d1 = s.getEntry(j);
                        stepsq += d1 * d1;
                    }
                    if (stepsq == 0.0) {
                        state = 190;
                        continue;
                    }
                    if (beta == 0.0) {
                        gredsq = stepsq;
                        itermax = iterc + n - nact;
                    }
                    if (gredsq * delsq <= qred * 1.0E-4 * qred) {
                        state = 190;
                        continue;
                    }
                    state = 210;
                    continue;
                }
                case 50: {
                    printState(50);
                    resid = delsq;
                    double ds = 0.0;
                    double shs = 0.0;
                    for (int j = 0; j < n; ++j) {
                        if (xbdi.getEntry(j) == 0.0) {
                            final double d1 = this.trialStepPoint.getEntry(j);
                            resid -= d1 * d1;
                            ds += s.getEntry(j) * this.trialStepPoint.getEntry(j);
                            shs += s.getEntry(j) * hs.getEntry(j);
                        }
                    }
                    if (resid <= 0.0) {
                        state = 90;
                        continue;
                    }
                    temp = FastMath.sqrt(stepsq * resid + ds * ds);
                    double blen;
                    if (ds < 0.0) {
                        blen = (temp - ds) / stepsq;
                    }
                    else {
                        blen = resid / (temp + ds);
                    }
                    stplen = blen;
                    if (shs > 0.0) {
                        stplen = FastMath.min(blen, gredsq / shs);
                    }
                    iact = -1;
                    for (int j = 0; j < n; ++j) {
                        if (s.getEntry(j) != 0.0) {
                            xsum = this.trustRegionCenterOffset.getEntry(j) + this.trialStepPoint.getEntry(j);
                            if (s.getEntry(j) > 0.0) {
                                temp = (this.upperDifference.getEntry(j) - xsum) / s.getEntry(j);
                            }
                            else {
                                temp = (this.lowerDifference.getEntry(j) - xsum) / s.getEntry(j);
                            }
                            if (temp < stplen) {
                                stplen = temp;
                                iact = j;
                            }
                        }
                    }
                    double sdec = 0.0;
                    if (stplen > 0.0) {
                        ++iterc;
                        temp = shs / stepsq;
                        if (iact == -1 && temp > 0.0) {
                            crvmin = FastMath.min(crvmin, temp);
                            if (crvmin == -1.0) {
                                crvmin = temp;
                            }
                        }
                        ggsav = gredsq;
                        gredsq = 0.0;
                        for (int j = 0; j < n; ++j) {
                            gnew.setEntry(j, gnew.getEntry(j) + stplen * hs.getEntry(j));
                            if (xbdi.getEntry(j) == 0.0) {
                                final double d1 = gnew.getEntry(j);
                                gredsq += d1 * d1;
                            }
                            this.trialStepPoint.setEntry(j, this.trialStepPoint.getEntry(j) + stplen * s.getEntry(j));
                        }
                        final double d2 = stplen * (ggsav - 0.5 * stplen * shs);
                        sdec = FastMath.max(d2, 0.0);
                        qred += sdec;
                    }
                    if (iact >= 0) {
                        ++nact;
                        xbdi.setEntry(iact, 1.0);
                        if (s.getEntry(iact) < 0.0) {
                            xbdi.setEntry(iact, -1.0);
                        }
                        final double d2 = this.trialStepPoint.getEntry(iact);
                        delsq -= d2 * d2;
                        if (delsq <= 0.0) {
                            state = 190;
                            continue;
                        }
                        state = 20;
                        continue;
                    }
                    else {
                        if (stplen >= blen)
                        if (iterc == itermax) {
                            state = 190;
                            continue;
                        }
                        if (sdec <= qred * 0.01) {
                            state = 190;
                            continue;
                        }
                        beta = gredsq / ggsav;
                        state = 30;
                        continue;
                    }
                    break;
                }
                case 90: {
                    printState(90);
                    crvmin = 0.0;
                }
                case 100: {
                    printState(100);
                    if (nact >= n - 1) {
                        state = 190;
                        continue;
                    }
                    dredsq = 0.0;
                    dredg = 0.0;
                    gredsq = 0.0;
                    for (int j = 0; j < n; ++j) {
                        if (xbdi.getEntry(j) == 0.0) {
                            double d1 = this.trialStepPoint.getEntry(j);
                            dredsq += d1 * d1;
                            dredg += this.trialStepPoint.getEntry(j) * gnew.getEntry(j);
                            d1 = gnew.getEntry(j);
                            gredsq += d1 * d1;
                            s.setEntry(j, this.trialStepPoint.getEntry(j));
                        }
                        else {
                            s.setEntry(j, 0.0);
                        }
                    }
                    itcsav = iterc;
                    state = 210;
                    continue;
                }
                case 120: {
                    printState(120);
                    ++iterc;
                    temp = gredsq * dredsq - dredg * dredg;
                    if (temp <= qred * 1.0E-4 * qred) {
                        state = 190;
                        continue;
                    }
                    temp = FastMath.sqrt(temp);
                    for (int j = 0; j < n; ++j) {
                        if (xbdi.getEntry(j) == 0.0) {
                            s.setEntry(j, (dredg * this.trialStepPoint.getEntry(j) - dredsq * gnew.getEntry(j)) / temp);
                        }
                        else {
                            s.setEntry(j, 0.0);
                        }
                    }
                    sredg = -temp;
                    angbd = 1.0;
                    iact = -1;
                    for (int j = 0; j < n; ++j) {
                        if (xbdi.getEntry(j) == 0.0) {
                            tempa = this.trustRegionCenterOffset.getEntry(j) + this.trialStepPoint.getEntry(j) - this.lowerDifference.getEntry(j);
                            tempb = this.upperDifference.getEntry(j) - this.trustRegionCenterOffset.getEntry(j) - this.trialStepPoint.getEntry(j);
                            if (tempa <= 0.0) {
                                ++nact;
                                xbdi.setEntry(j, -1.0);
                                state = 100;
                                break;
                            }
                            if (tempb <= 0.0) {
                                ++nact;
                                xbdi.setEntry(j, 1.0);
                                state = 100;
                                break;
                            }
                            double d1 = this.trialStepPoint.getEntry(j);
                            final double d3 = s.getEntry(j);
                            final double ssq = d1 * d1 + d3 * d3;
                            d1 = this.trustRegionCenterOffset.getEntry(j) - this.lowerDifference.getEntry(j);
                            temp = ssq - d1 * d1;
                            if (temp > 0.0) {
                                temp = FastMath.sqrt(temp) - s.getEntry(j);
                                if (angbd * temp > tempa) {
                                    angbd = tempa / temp;
                                    iact = j;
                                    xsav = -1.0;
                                }
                            }
                            d1 = this.upperDifference.getEntry(j) - this.trustRegionCenterOffset.getEntry(j);
                            temp = ssq - d1 * d1;
                            if (temp > 0.0) {
                                temp = FastMath.sqrt(temp) + s.getEntry(j);
                                if (angbd * temp > tempb) {
                                    angbd = tempb / temp;
                                    iact = j;
                                    xsav = 1.0;
                                }
                            }
                        }
                    }
                    state = 210;
                    continue;
                }
                case 150: {
                    printState(150);
                    double shs = 0.0;
                    double dhs = 0.0;
                    double dhd = 0.0;
                    for (int j = 0; j < n; ++j) {
                        if (xbdi.getEntry(j) == 0.0) {
                            shs += s.getEntry(j) * hs.getEntry(j);
                            dhs += this.trialStepPoint.getEntry(j) * hs.getEntry(j);
                            dhd += this.trialStepPoint.getEntry(j) * hred.getEntry(j);
                        }
                    }
                    redmax = 0.0;
                    int isav = -1;
                    redsav = 0.0;
                    final int iu = (int)(angbd * 17.0 + 3.1);
                    for (int j = 0; j < iu; ++j) {
                        angt = angbd * j / iu;
                        final double sth = (angt + angt) / (1.0 + angt * angt);
                        temp = shs + angt * (angt * dhd - dhs - dhs);
                        rednew = sth * (angt * dredg - sredg - 0.5 * sth * temp);
                        if (rednew > redmax) {
                            redmax = rednew;
                            isav = j;
                            rdprev = redsav;
                        }
                        else if (j == isav + 1) {
                            rdnext = rednew;
                        }
                        redsav = rednew;
                    }
                    if (isav < 0) {
                        state = 190;
                        continue;
                    }
                    if (isav < iu) {
                        temp = (rdnext - rdprev) / (redmax + redmax - rdprev - rdnext);
                        angt = angbd * (isav + 0.5 * temp) / iu;
                    }
                    final double cth = (1.0 - angt * angt) / (1.0 + angt * angt);
                    final double sth = (angt + angt) / (1.0 + angt * angt);
                    temp = shs + angt * (angt * dhd - dhs - dhs);
                    final double sdec = sth * (angt * dredg - sredg - 0.5 * sth * temp);
                    if (sdec <= 0.0) {
                        state = 190;
                        continue;
                    }
                    dredg = 0.0;
                    gredsq = 0.0;
                    for (int j = 0; j < n; ++j) {
                        gnew.setEntry(j, gnew.getEntry(j) + (cth - 1.0) * hred.getEntry(j) + sth * hs.getEntry(j));
                        if (xbdi.getEntry(j) == 0.0) {
                            this.trialStepPoint.setEntry(j, cth * this.trialStepPoint.getEntry(j) + sth * s.getEntry(j));
                            dredg += this.trialStepPoint.getEntry(j) * gnew.getEntry(j);
                            final double d1 = gnew.getEntry(j);
                            gredsq += d1 * d1;
                        }
                        hred.setEntry(j, cth * hred.getEntry(j) + sth * hs.getEntry(j));
                    }
                    qred += sdec;
                    if (iact >= 0 && isav == iu) {
                        ++nact;
                        xbdi.setEntry(iact, xsav);
                        state = 100;
                        continue;
                    }
                    if (sdec > qred * 0.01) {
                        state = 120;
                        continue;
                    }
                    break Label_2477;
                }
                case 190: {
                    break Label_2477;
                }
                case 210: {
                    printState(210);
                    int ih = 0;
                    for (int k = 0; k < n; ++k) {
                        hs.setEntry(k, 0.0);
                        for (int l = 0; l <= k; ++l) {
                            if (l < k) {
                                hs.setEntry(k, hs.getEntry(k) + this.modelSecondDerivativesValues.getEntry(ih) * s.getEntry(l));
                            }
                            hs.setEntry(l, hs.getEntry(l) + this.modelSecondDerivativesValues.getEntry(ih) * s.getEntry(k));
                            ++ih;
                        }
                    }
                    final RealVector tmp = this.interpolationPoints.operate(s).ebeMultiply(this.modelSecondDerivativesParameters);
                    for (int m = 0; m < npt; ++m) {
                        if (this.modelSecondDerivativesParameters.getEntry(m) != 0.0) {
                            for (int i2 = 0; i2 < n; ++i2) {
                                hs.setEntry(i2, hs.getEntry(i2) + tmp.getEntry(m) * this.interpolationPoints.getEntry(m, i2));
                            }
                        }
                    }
                    if (crvmin != 0.0) {
                        state = 50;
                        continue;
                    }
                    if (iterc > itcsav) {
                        state = 150;
                        continue;
                    }
                    for (int l = 0; l < n; ++l) {
                        hred.setEntry(l, hs.getEntry(l));
                    }
                    state = 120;
                    continue;
                }
                default: {
                    throw new MathIllegalStateException(LocalizedFormats.SIMPLE_MESSAGE, new Object[] { "trsbox" });
                }
            }
        }
        printState(190);
        dsq = 0.0;
        for (int j = 0; j < n; ++j) {
            final double min = FastMath.min(this.trustRegionCenterOffset.getEntry(j) + this.trialStepPoint.getEntry(j), this.upperDifference.getEntry(j));
            this.newPoint.setEntry(j, FastMath.max(min, this.lowerDifference.getEntry(j)));
            if (xbdi.getEntry(j) == -1.0) {
                this.newPoint.setEntry(j, this.lowerDifference.getEntry(j));
            }
            if (xbdi.getEntry(j) == 1.0) {
                this.newPoint.setEntry(j, this.upperDifference.getEntry(j));
            }
            this.trialStepPoint.setEntry(j, this.newPoint.getEntry(j) - this.trustRegionCenterOffset.getEntry(j));
            final double d4 = this.trialStepPoint.getEntry(j);
            dsq += d4 * d4;
        }
        return new double[] { dsq, crvmin };
    }
    
    private void update(final double beta, final double denom, final int knew) {
        printMethod();
        final int n = this.currentBest.getDimension();
        final int npt = this.numberOfInterpolationPoints;
        final int nptm = npt - n - 1;
        final ArrayRealVector work = new ArrayRealVector(npt + n);
        double ztest = 0.0;
        for (int k = 0; k < npt; ++k) {
            for (int j = 0; j < nptm; ++j) {
                ztest = FastMath.max(ztest, FastMath.abs(this.zMatrix.getEntry(k, j)));
            }
        }
        ztest *= 1.0E-20;
        for (int i = 1; i < nptm; ++i) {
            final double d1 = this.zMatrix.getEntry(knew, i);
            if (FastMath.abs(d1) > ztest) {
                final double d2 = this.zMatrix.getEntry(knew, 0);
                final double d3 = this.zMatrix.getEntry(knew, i);
                final double d4 = FastMath.sqrt(d2 * d2 + d3 * d3);
                final double d5 = this.zMatrix.getEntry(knew, 0) / d4;
                final double d6 = this.zMatrix.getEntry(knew, i) / d4;
                for (int l = 0; l < npt; ++l) {
                    final double d7 = d5 * this.zMatrix.getEntry(l, 0) + d6 * this.zMatrix.getEntry(l, i);
                    this.zMatrix.setEntry(l, i, d5 * this.zMatrix.getEntry(l, i) - d6 * this.zMatrix.getEntry(l, 0));
                    this.zMatrix.setEntry(l, 0, d7);
                }
            }
            this.zMatrix.setEntry(knew, i, 0.0);
        }
        for (int m = 0; m < npt; ++m) {
            work.setEntry(m, this.zMatrix.getEntry(knew, 0) * this.zMatrix.getEntry(m, 0));
        }
        final double alpha = work.getEntry(knew);
        final double tau = this.lagrangeValuesAtNewPoint.getEntry(knew);
        this.lagrangeValuesAtNewPoint.setEntry(knew, this.lagrangeValuesAtNewPoint.getEntry(knew) - 1.0);
        final double sqrtDenom = FastMath.sqrt(denom);
        final double d8 = tau / sqrtDenom;
        final double d9 = this.zMatrix.getEntry(knew, 0) / sqrtDenom;
        for (int i2 = 0; i2 < npt; ++i2) {
            this.zMatrix.setEntry(i2, 0, d8 * this.zMatrix.getEntry(i2, 0) - d9 * this.lagrangeValuesAtNewPoint.getEntry(i2));
        }
        for (int j2 = 0; j2 < n; ++j2) {
            final int jp = npt + j2;
            work.setEntry(jp, this.bMatrix.getEntry(knew, j2));
            final double d10 = (alpha * this.lagrangeValuesAtNewPoint.getEntry(jp) - tau * work.getEntry(jp)) / denom;
            final double d11 = (-beta * work.getEntry(jp) - tau * this.lagrangeValuesAtNewPoint.getEntry(jp)) / denom;
            for (int i3 = 0; i3 <= jp; ++i3) {
                this.bMatrix.setEntry(i3, j2, this.bMatrix.getEntry(i3, j2) + d10 * this.lagrangeValuesAtNewPoint.getEntry(i3) + d11 * work.getEntry(i3));
                if (i3 >= npt) {
                    this.bMatrix.setEntry(jp, i3 - npt, this.bMatrix.getEntry(i3, j2));
                }
            }
        }
    }
    
    private void setup(final double[] lowerBound, final double[] upperBound) {
        printMethod();
        final double[] init = this.getStartPoint();
        final int dimension = init.length;
        if (dimension < 2) {
            throw new NumberIsTooSmallException(dimension, 2, true);
        }
        final int[] nPointsInterval = { dimension + 2, (dimension + 2) * (dimension + 1) / 2 };
        if (this.numberOfInterpolationPoints < nPointsInterval[0] || this.numberOfInterpolationPoints > nPointsInterval[1]) {
            throw new OutOfRangeException(LocalizedFormats.NUMBER_OF_INTERPOLATION_POINTS, this.numberOfInterpolationPoints, nPointsInterval[0], nPointsInterval[1]);
        }
        this.boundDifference = new double[dimension];
        final double requiredMinDiff = 2.0 * this.initialTrustRegionRadius;
        double minDiff = Double.POSITIVE_INFINITY;
        for (int i = 0; i < dimension; ++i) {
            this.boundDifference[i] = upperBound[i] - lowerBound[i];
            minDiff = FastMath.min(minDiff, this.boundDifference[i]);
        }
        if (minDiff < requiredMinDiff) {
            this.initialTrustRegionRadius = minDiff / 3.0;
        }
        this.bMatrix = new Array2DRowRealMatrix(dimension + this.numberOfInterpolationPoints, dimension);
        this.zMatrix = new Array2DRowRealMatrix(this.numberOfInterpolationPoints, this.numberOfInterpolationPoints - dimension - 1);
        this.interpolationPoints = new Array2DRowRealMatrix(this.numberOfInterpolationPoints, dimension);
        this.originShift = new ArrayRealVector(dimension);
        this.fAtInterpolationPoints = new ArrayRealVector(this.numberOfInterpolationPoints);
        this.trustRegionCenterOffset = new ArrayRealVector(dimension);
        this.gradientAtTrustRegionCenter = new ArrayRealVector(dimension);
        this.lowerDifference = new ArrayRealVector(dimension);
        this.upperDifference = new ArrayRealVector(dimension);
        this.modelSecondDerivativesParameters = new ArrayRealVector(this.numberOfInterpolationPoints);
        this.newPoint = new ArrayRealVector(dimension);
        this.alternativeNewPoint = new ArrayRealVector(dimension);
        this.trialStepPoint = new ArrayRealVector(dimension);
        this.lagrangeValuesAtNewPoint = new ArrayRealVector(dimension + this.numberOfInterpolationPoints);
        this.modelSecondDerivativesValues = new ArrayRealVector(dimension * (dimension + 1) / 2);
    }
    
    private static String caller(final int n) {
        final Throwable t = new Throwable();
        final StackTraceElement[] elements = t.getStackTrace();
        final StackTraceElement e = elements[n];
        return e.getMethodName() + " (at line " + e.getLineNumber() + ")";
    }
    
    private static void printState(final int s) {
    }
    
    private static void printMethod() {
    }
    
    private static class PathIsExploredException extends RuntimeException
    {
        private static final long serialVersionUID = 745350979634801853L;
        private static final String PATH_IS_EXPLORED = "If this exception is thrown, just remove it from the code";
        
        PathIsExploredException() {
            super("If this exception is thrown, just remove it from the code " + caller(3));
        }
    }
}
