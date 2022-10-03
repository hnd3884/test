package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.sampling.StepHandler;

public class GraggBulirschStoerIntegrator extends AdaptiveStepsizeIntegrator
{
    private static final String METHOD_NAME = "Gragg-Bulirsch-Stoer";
    private int maxOrder;
    private int[] sequence;
    private int[] costPerStep;
    private double[] costPerTimeUnit;
    private double[] optimalStep;
    private double[][] coeff;
    private boolean performTest;
    private int maxChecks;
    private int maxIter;
    private double stabilityReduction;
    private double stepControl1;
    private double stepControl2;
    private double stepControl3;
    private double stepControl4;
    private double orderControl1;
    private double orderControl2;
    private boolean useInterpolationError;
    private int mudif;
    
    public GraggBulirschStoerIntegrator(final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) {
        super("Gragg-Bulirsch-Stoer", minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        this.setStabilityCheck(true, -1, -1, -1.0);
        this.setControlFactors(-1.0, -1.0, -1.0, -1.0);
        this.setOrderControl(-1, -1.0, -1.0);
        this.setInterpolationControl(true, -1);
    }
    
    public GraggBulirschStoerIntegrator(final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) {
        super("Gragg-Bulirsch-Stoer", minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.setStabilityCheck(true, -1, -1, -1.0);
        this.setControlFactors(-1.0, -1.0, -1.0, -1.0);
        this.setOrderControl(-1, -1.0, -1.0);
        this.setInterpolationControl(true, -1);
    }
    
    public void setStabilityCheck(final boolean performStabilityCheck, final int maxNumIter, final int maxNumChecks, final double stepsizeReductionFactor) {
        this.performTest = performStabilityCheck;
        this.maxIter = ((maxNumIter <= 0) ? 2 : maxNumIter);
        this.maxChecks = ((maxNumChecks <= 0) ? 1 : maxNumChecks);
        if (stepsizeReductionFactor < 1.0E-4 || stepsizeReductionFactor > 0.9999) {
            this.stabilityReduction = 0.5;
        }
        else {
            this.stabilityReduction = stepsizeReductionFactor;
        }
    }
    
    public void setControlFactors(final double control1, final double control2, final double control3, final double control4) {
        if (control1 < 1.0E-4 || control1 > 0.9999) {
            this.stepControl1 = 0.65;
        }
        else {
            this.stepControl1 = control1;
        }
        if (control2 < 1.0E-4 || control2 > 0.9999) {
            this.stepControl2 = 0.94;
        }
        else {
            this.stepControl2 = control2;
        }
        if (control3 < 1.0E-4 || control3 > 0.9999) {
            this.stepControl3 = 0.02;
        }
        else {
            this.stepControl3 = control3;
        }
        if (control4 < 1.0001 || control4 > 999.9) {
            this.stepControl4 = 4.0;
        }
        else {
            this.stepControl4 = control4;
        }
    }
    
    public void setOrderControl(final int maximalOrder, final double control1, final double control2) {
        if (maximalOrder <= 6 || maximalOrder % 2 != 0) {
            this.maxOrder = 18;
        }
        if (control1 < 1.0E-4 || control1 > 0.9999) {
            this.orderControl1 = 0.8;
        }
        else {
            this.orderControl1 = control1;
        }
        if (control2 < 1.0E-4 || control2 > 0.9999) {
            this.orderControl2 = 0.9;
        }
        else {
            this.orderControl2 = control2;
        }
        this.initializeArrays();
    }
    
    @Override
    public void addStepHandler(final StepHandler handler) {
        super.addStepHandler(handler);
        this.initializeArrays();
    }
    
    @Override
    public void addEventHandler(final EventHandler function, final double maxCheckInterval, final double convergence, final int maxIterationCount, final UnivariateSolver solver) {
        super.addEventHandler(function, maxCheckInterval, convergence, maxIterationCount, solver);
        this.initializeArrays();
    }
    
    private void initializeArrays() {
        final int size = this.maxOrder / 2;
        if (this.sequence == null || this.sequence.length != size) {
            this.sequence = new int[size];
            this.costPerStep = new int[size];
            this.coeff = new double[size][];
            this.costPerTimeUnit = new double[size];
            this.optimalStep = new double[size];
        }
        for (int k = 0; k < size; ++k) {
            this.sequence[k] = 4 * k + 2;
        }
        this.costPerStep[0] = this.sequence[0] + 1;
        for (int k = 1; k < size; ++k) {
            this.costPerStep[k] = this.costPerStep[k - 1] + this.sequence[k];
        }
        for (int k = 0; k < size; ++k) {
            this.coeff[k] = (double[])((k > 0) ? new double[k] : null);
            for (int l = 0; l < k; ++l) {
                final double ratio = this.sequence[k] / (double)this.sequence[k - l - 1];
                this.coeff[k][l] = 1.0 / (ratio * ratio - 1.0);
            }
        }
    }
    
    public void setInterpolationControl(final boolean useInterpolationErrorForControl, final int mudifControlParameter) {
        this.useInterpolationError = useInterpolationErrorForControl;
        if (mudifControlParameter <= 0 || mudifControlParameter >= 7) {
            this.mudif = 4;
        }
        else {
            this.mudif = mudifControlParameter;
        }
    }
    
    private void rescale(final double[] y1, final double[] y2, final double[] scale) {
        if (this.vecAbsoluteTolerance == null) {
            for (int i = 0; i < scale.length; ++i) {
                final double yi = FastMath.max(FastMath.abs(y1[i]), FastMath.abs(y2[i]));
                scale[i] = this.scalAbsoluteTolerance + this.scalRelativeTolerance * yi;
            }
        }
        else {
            for (int i = 0; i < scale.length; ++i) {
                final double yi = FastMath.max(FastMath.abs(y1[i]), FastMath.abs(y2[i]));
                scale[i] = this.vecAbsoluteTolerance[i] + this.vecRelativeTolerance[i] * yi;
            }
        }
    }
    
    private boolean tryStep(final double t0, final double[] y0, final double step, final int k, final double[] scale, final double[][] f, final double[] yMiddle, final double[] yEnd, final double[] yTmp) throws MaxCountExceededException, DimensionMismatchException {
        final int n = this.sequence[k];
        final double subStep = step / n;
        final double subStep2 = 2.0 * subStep;
        double t = t0 + subStep;
        for (int i = 0; i < y0.length; ++i) {
            yTmp[i] = y0[i];
            yEnd[i] = y0[i] + subStep * f[0][i];
        }
        this.computeDerivatives(t, yEnd, f[1]);
        for (int j = 1; j < n; ++j) {
            if (2 * j == n) {
                System.arraycopy(yEnd, 0, yMiddle, 0, y0.length);
            }
            t += subStep;
            for (int l = 0; l < y0.length; ++l) {
                final double middle = yEnd[l];
                yEnd[l] = yTmp[l] + subStep2 * f[j][l];
                yTmp[l] = middle;
            }
            this.computeDerivatives(t, yEnd, f[j + 1]);
            if (this.performTest && j <= this.maxChecks && k < this.maxIter) {
                double initialNorm = 0.0;
                for (int m = 0; m < scale.length; ++m) {
                    final double ratio = f[0][m] / scale[m];
                    initialNorm += ratio * ratio;
                }
                double deltaNorm = 0.0;
                for (int l2 = 0; l2 < scale.length; ++l2) {
                    final double ratio2 = (f[j + 1][l2] - f[0][l2]) / scale[l2];
                    deltaNorm += ratio2 * ratio2;
                }
                if (deltaNorm > 4.0 * FastMath.max(1.0E-15, initialNorm)) {
                    return false;
                }
            }
        }
        for (int i = 0; i < y0.length; ++i) {
            yEnd[i] = 0.5 * (yTmp[i] + yEnd[i] + subStep * f[n][i]);
        }
        return true;
    }
    
    private void extrapolate(final int offset, final int k, final double[][] diag, final double[] last) {
        for (int j = 1; j < k; ++j) {
            for (int i = 0; i < last.length; ++i) {
                diag[k - j - 1][i] = diag[k - j][i] + this.coeff[k + offset][j - 1] * (diag[k - j][i] - diag[k - j - 1][i]);
            }
        }
        for (int l = 0; l < last.length; ++l) {
            last[l] = diag[0][l] + this.coeff[k + offset][k - 1] * (diag[0][l] - last[l]);
        }
    }
    
    @Override
    public void integrate(final ExpandableStatefulODE equations, final double t) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        this.sanityChecks(equations, t);
        this.setEquations(equations);
        final boolean forward = t > equations.getTime();
        final double[] y0 = equations.getCompleteState();
        final double[] y2 = y0.clone();
        final double[] yDot0 = new double[y2.length];
        final double[] y3 = new double[y2.length];
        final double[] yTmp = new double[y2.length];
        final double[] yTmpDot = new double[y2.length];
        final double[][] diagonal = new double[this.sequence.length - 1][];
        final double[][] y1Diag = new double[this.sequence.length - 1][];
        for (int k = 0; k < this.sequence.length - 1; ++k) {
            diagonal[k] = new double[y2.length];
            y1Diag[k] = new double[y2.length];
        }
        final double[][][] fk = new double[this.sequence.length][][];
        for (int i = 0; i < this.sequence.length; ++i) {
            (fk[i] = new double[this.sequence[i] + 1][])[0] = yDot0;
            for (int l = 0; l < this.sequence[i]; ++l) {
                fk[i][l + 1] = new double[y0.length];
            }
        }
        if (y2 != y0) {
            System.arraycopy(y0, 0, y2, 0, y0.length);
        }
        final double[] yDot2 = new double[y0.length];
        final double[][] yMidDots = new double[1 + 2 * this.sequence.length][y0.length];
        final double[] scale = new double[this.mainSetDimension];
        this.rescale(y2, y2, scale);
        final double tol = (this.vecRelativeTolerance == null) ? this.scalRelativeTolerance : this.vecRelativeTolerance[0];
        final double log10R = FastMath.log10(FastMath.max(1.0E-10, tol));
        int targetIter = FastMath.max(1, FastMath.min(this.sequence.length - 2, (int)FastMath.floor(0.5 - 0.6 * log10R)));
        final AbstractStepInterpolator interpolator = new GraggBulirschStoerStepInterpolator(y2, yDot0, y3, yDot2, yMidDots, forward, equations.getPrimaryMapper(), equations.getSecondaryMappers());
        interpolator.storeTime(equations.getTime());
        this.stepStart = equations.getTime();
        double hNew = 0.0;
        double maxError = Double.MAX_VALUE;
        boolean previousRejected = false;
        boolean firstTime = true;
        boolean newStep = true;
        boolean firstStepAlreadyComputed = false;
        this.initIntegration(equations.getTime(), y0, t);
        this.costPerTimeUnit[0] = 0.0;
        this.isLastStep = false;
        do {
            boolean reject = false;
            if (newStep) {
                interpolator.shift();
                if (!firstStepAlreadyComputed) {
                    this.computeDerivatives(this.stepStart, y2, yDot0);
                }
                if (firstTime) {
                    hNew = this.initializeStep(forward, 2 * targetIter + 1, scale, this.stepStart, y2, yDot0, yTmp, yTmpDot);
                }
                newStep = false;
            }
            this.stepSize = hNew;
            if ((forward && this.stepStart + this.stepSize > t) || (!forward && this.stepStart + this.stepSize < t)) {
                this.stepSize = t - this.stepStart;
            }
            final double nextT = this.stepStart + this.stepSize;
            this.isLastStep = (forward ? (nextT >= t) : (nextT <= t));
            int j = -1;
            boolean loop = true;
            while (loop) {
                ++j;
                if (!this.tryStep(this.stepStart, y2, this.stepSize, j, scale, fk[j], (j == 0) ? yMidDots[0] : diagonal[j - 1], (j == 0) ? y3 : y1Diag[j - 1], yTmp)) {
                    hNew = FastMath.abs(this.filterStep(this.stepSize * this.stabilityReduction, forward, false));
                    reject = true;
                    loop = false;
                }
                else {
                    if (j <= 0) {
                        continue;
                    }
                    this.extrapolate(0, j, y1Diag, y3);
                    this.rescale(y2, y3, scale);
                    double error = 0.0;
                    for (int m = 0; m < this.mainSetDimension; ++m) {
                        final double e = FastMath.abs(y3[m] - y1Diag[0][m]) / scale[m];
                        error += e * e;
                    }
                    error = FastMath.sqrt(error / this.mainSetDimension);
                    if (error > 1.0E15 || (j > 1 && error > maxError)) {
                        hNew = FastMath.abs(this.filterStep(this.stepSize * this.stabilityReduction, forward, false));
                        reject = true;
                        loop = false;
                    }
                    else {
                        maxError = FastMath.max(4.0 * error, 1.0);
                        final double exp = 1.0 / (2 * j + 1);
                        double fac = this.stepControl2 / FastMath.pow(error / this.stepControl1, exp);
                        final double pow = FastMath.pow(this.stepControl3, exp);
                        fac = FastMath.max(pow / this.stepControl4, FastMath.min(1.0 / pow, fac));
                        this.optimalStep[j] = FastMath.abs(this.filterStep(this.stepSize * fac, forward, true));
                        this.costPerTimeUnit[j] = this.costPerStep[j] / this.optimalStep[j];
                        switch (j - targetIter) {
                            case -1: {
                                if (targetIter <= 1 || previousRejected) {
                                    continue;
                                }
                                if (error <= 1.0) {
                                    loop = false;
                                    continue;
                                }
                                final double ratio = this.sequence[targetIter] * (double)this.sequence[targetIter + 1] / (this.sequence[0] * this.sequence[0]);
                                if (error <= ratio * ratio) {
                                    continue;
                                }
                                reject = true;
                                loop = false;
                                targetIter = j;
                                if (targetIter > 1 && this.costPerTimeUnit[targetIter - 1] < this.orderControl1 * this.costPerTimeUnit[targetIter]) {
                                    --targetIter;
                                }
                                hNew = this.optimalStep[targetIter];
                                continue;
                            }
                            case 0: {
                                if (error <= 1.0) {
                                    loop = false;
                                    continue;
                                }
                                final double ratio = this.sequence[j + 1] / (double)this.sequence[0];
                                if (error <= ratio * ratio) {
                                    continue;
                                }
                                reject = true;
                                loop = false;
                                if (targetIter > 1 && this.costPerTimeUnit[targetIter - 1] < this.orderControl1 * this.costPerTimeUnit[targetIter]) {
                                    --targetIter;
                                }
                                hNew = this.optimalStep[targetIter];
                                continue;
                            }
                            case 1: {
                                if (error > 1.0) {
                                    reject = true;
                                    if (targetIter > 1 && this.costPerTimeUnit[targetIter - 1] < this.orderControl1 * this.costPerTimeUnit[targetIter]) {
                                        --targetIter;
                                    }
                                    hNew = this.optimalStep[targetIter];
                                }
                                loop = false;
                                continue;
                            }
                            default: {
                                if ((firstTime || this.isLastStep) && error <= 1.0) {
                                    loop = false;
                                    continue;
                                }
                                continue;
                            }
                        }
                    }
                }
            }
            if (!reject) {
                this.computeDerivatives(this.stepStart + this.stepSize, y3, yDot2);
            }
            double hInt = this.getMaxStep();
            if (!reject) {
                for (int j2 = 1; j2 <= j; ++j2) {
                    this.extrapolate(0, j2, diagonal, yMidDots[0]);
                }
                final int mu = 2 * j - this.mudif + 3;
                for (int l2 = 0; l2 < mu; ++l2) {
                    final int l3 = l2 / 2;
                    double factor = FastMath.pow(0.5 * this.sequence[l3], l2);
                    int middleIndex = fk[l3].length / 2;
                    for (int i2 = 0; i2 < y0.length; ++i2) {
                        yMidDots[l2 + 1][i2] = factor * fk[l3][middleIndex + l2][i2];
                    }
                    for (int j3 = 1; j3 <= j - l3; ++j3) {
                        factor = FastMath.pow(0.5 * this.sequence[j3 + l3], l2);
                        middleIndex = fk[l3 + j3].length / 2;
                        for (int i3 = 0; i3 < y0.length; ++i3) {
                            diagonal[j3 - 1][i3] = factor * fk[l3 + j3][middleIndex + l2][i3];
                        }
                        this.extrapolate(l3, j3, diagonal, yMidDots[l2 + 1]);
                    }
                    for (int i2 = 0; i2 < y0.length; ++i2) {
                        final double[] array = yMidDots[l2 + 1];
                        final int n = i2;
                        array[n] *= this.stepSize;
                    }
                    for (int j3 = (l2 + 1) / 2; j3 <= j; ++j3) {
                        for (int m2 = fk[j3].length - 1; m2 >= 2 * (l2 + 1); --m2) {
                            for (int i4 = 0; i4 < y0.length; ++i4) {
                                final double[] array2 = fk[j3][m2];
                                final int n2 = i4;
                                array2[n2] -= fk[j3][m2 - 2][i4];
                            }
                        }
                    }
                }
                if (mu >= 0) {
                    final GraggBulirschStoerStepInterpolator gbsInterpolator = (GraggBulirschStoerStepInterpolator)interpolator;
                    gbsInterpolator.computeCoefficients(mu, this.stepSize);
                    if (this.useInterpolationError) {
                        final double interpError = gbsInterpolator.estimateError(scale);
                        hInt = FastMath.abs(this.stepSize / FastMath.max(FastMath.pow(interpError, 1.0 / (mu + 4)), 0.01));
                        if (interpError > 10.0) {
                            hNew = hInt;
                            reject = true;
                        }
                    }
                }
            }
            if (!reject) {
                interpolator.storeTime(this.stepStart + this.stepSize);
                interpolator.storeTime(this.stepStart = this.acceptStep(interpolator, y3, yDot2, t));
                System.arraycopy(y3, 0, y2, 0, y0.length);
                System.arraycopy(yDot2, 0, yDot0, 0, y0.length);
                firstStepAlreadyComputed = true;
                int optimalIter;
                if (j == 1) {
                    optimalIter = 2;
                    if (previousRejected) {
                        optimalIter = 1;
                    }
                }
                else if (j <= targetIter) {
                    optimalIter = j;
                    if (this.costPerTimeUnit[j - 1] < this.orderControl1 * this.costPerTimeUnit[j]) {
                        optimalIter = j - 1;
                    }
                    else if (this.costPerTimeUnit[j] < this.orderControl2 * this.costPerTimeUnit[j - 1]) {
                        optimalIter = FastMath.min(j + 1, this.sequence.length - 2);
                    }
                }
                else {
                    optimalIter = j - 1;
                    if (j > 2 && this.costPerTimeUnit[j - 2] < this.orderControl1 * this.costPerTimeUnit[j - 1]) {
                        optimalIter = j - 2;
                    }
                    if (this.costPerTimeUnit[j] < this.orderControl2 * this.costPerTimeUnit[optimalIter]) {
                        optimalIter = FastMath.min(j, this.sequence.length - 2);
                    }
                }
                if (previousRejected) {
                    targetIter = FastMath.min(optimalIter, j);
                    hNew = FastMath.min(FastMath.abs(this.stepSize), this.optimalStep[targetIter]);
                }
                else {
                    if (optimalIter <= j) {
                        hNew = this.optimalStep[optimalIter];
                    }
                    else if (j < targetIter && this.costPerTimeUnit[j] < this.orderControl2 * this.costPerTimeUnit[j - 1]) {
                        hNew = this.filterStep(this.optimalStep[j] * this.costPerStep[optimalIter + 1] / this.costPerStep[j], forward, false);
                    }
                    else {
                        hNew = this.filterStep(this.optimalStep[j] * this.costPerStep[optimalIter] / this.costPerStep[j], forward, false);
                    }
                    targetIter = optimalIter;
                }
                newStep = true;
            }
            hNew = FastMath.min(hNew, hInt);
            if (!forward) {
                hNew = -hNew;
            }
            firstTime = false;
            if (reject) {
                this.isLastStep = false;
                previousRejected = true;
            }
            else {
                previousRejected = false;
            }
        } while (!this.isLastStep);
        equations.setTime(this.stepStart);
        equations.setCompleteState(y2);
        this.resetInternalState();
    }
}
