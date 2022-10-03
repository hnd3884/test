package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.ode.AbstractIntegrator;
import org.apache.commons.math3.ode.ExpandableStatefulODE;

public abstract class EmbeddedRungeKuttaIntegrator extends AdaptiveStepsizeIntegrator
{
    private final boolean fsal;
    private final double[] c;
    private final double[][] a;
    private final double[] b;
    private final RungeKuttaStepInterpolator prototype;
    private final double exp;
    private double safety;
    private double minReduction;
    private double maxGrowth;
    
    protected EmbeddedRungeKuttaIntegrator(final String name, final boolean fsal, final double[] c, final double[][] a, final double[] b, final RungeKuttaStepInterpolator prototype, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) {
        super(name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        this.fsal = fsal;
        this.c = c;
        this.a = a;
        this.b = b;
        this.prototype = prototype;
        this.exp = -1.0 / this.getOrder();
        this.setSafety(0.9);
        this.setMinReduction(0.2);
        this.setMaxGrowth(10.0);
    }
    
    protected EmbeddedRungeKuttaIntegrator(final String name, final boolean fsal, final double[] c, final double[][] a, final double[] b, final RungeKuttaStepInterpolator prototype, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) {
        super(name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.fsal = fsal;
        this.c = c;
        this.a = a;
        this.b = b;
        this.prototype = prototype;
        this.exp = -1.0 / this.getOrder();
        this.setSafety(0.9);
        this.setMinReduction(0.2);
        this.setMaxGrowth(10.0);
    }
    
    public abstract int getOrder();
    
    public double getSafety() {
        return this.safety;
    }
    
    public void setSafety(final double safety) {
        this.safety = safety;
    }
    
    @Override
    public void integrate(final ExpandableStatefulODE equations, final double t) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        this.sanityChecks(equations, t);
        this.setEquations(equations);
        final boolean forward = t > equations.getTime();
        final double[] y0 = equations.getCompleteState();
        final double[] y2 = y0.clone();
        final int stages = this.c.length + 1;
        final double[][] yDotK = new double[stages][y2.length];
        final double[] yTmp = y0.clone();
        final double[] yDotTmp = new double[y2.length];
        final RungeKuttaStepInterpolator interpolator = (RungeKuttaStepInterpolator)this.prototype.copy();
        interpolator.reinitialize(this, yTmp, yDotK, forward, equations.getPrimaryMapper(), equations.getSecondaryMappers());
        interpolator.storeTime(equations.getTime());
        this.stepStart = equations.getTime();
        double hNew = 0.0;
        boolean firstTime = true;
        this.initIntegration(equations.getTime(), y0, t);
        this.isLastStep = false;
        do {
            interpolator.shift();
            double error = 10.0;
            while (error >= 1.0) {
                if (firstTime || !this.fsal) {
                    this.computeDerivatives(this.stepStart, y2, yDotK[0]);
                }
                if (firstTime) {
                    final double[] scale = new double[this.mainSetDimension];
                    if (this.vecAbsoluteTolerance == null) {
                        for (int i = 0; i < scale.length; ++i) {
                            scale[i] = this.scalAbsoluteTolerance + this.scalRelativeTolerance * FastMath.abs(y2[i]);
                        }
                    }
                    else {
                        for (int i = 0; i < scale.length; ++i) {
                            scale[i] = this.vecAbsoluteTolerance[i] + this.vecRelativeTolerance[i] * FastMath.abs(y2[i]);
                        }
                    }
                    hNew = this.initializeStep(forward, this.getOrder(), scale, this.stepStart, y2, yDotK[0], yTmp, yDotK[1]);
                    firstTime = false;
                }
                this.stepSize = hNew;
                if (forward) {
                    if (this.stepStart + this.stepSize >= t) {
                        this.stepSize = t - this.stepStart;
                    }
                }
                else if (this.stepStart + this.stepSize <= t) {
                    this.stepSize = t - this.stepStart;
                }
                for (int k = 1; k < stages; ++k) {
                    for (int j = 0; j < y0.length; ++j) {
                        double sum = this.a[k - 1][0] * yDotK[0][j];
                        for (int l = 1; l < k; ++l) {
                            sum += this.a[k - 1][l] * yDotK[l][j];
                        }
                        yTmp[j] = y2[j] + this.stepSize * sum;
                    }
                    this.computeDerivatives(this.stepStart + this.c[k - 1] * this.stepSize, yTmp, yDotK[k]);
                }
                for (int m = 0; m < y0.length; ++m) {
                    double sum2 = this.b[0] * yDotK[0][m];
                    for (int l2 = 1; l2 < stages; ++l2) {
                        sum2 += this.b[l2] * yDotK[l2][m];
                    }
                    yTmp[m] = y2[m] + this.stepSize * sum2;
                }
                error = this.estimateError(yDotK, y2, yTmp, this.stepSize);
                if (error >= 1.0) {
                    final double factor = FastMath.min(this.maxGrowth, FastMath.max(this.minReduction, this.safety * FastMath.pow(error, this.exp)));
                    hNew = this.filterStep(this.stepSize * factor, forward, false);
                }
            }
            interpolator.storeTime(this.stepStart + this.stepSize);
            System.arraycopy(yTmp, 0, y2, 0, y0.length);
            System.arraycopy(yDotK[stages - 1], 0, yDotTmp, 0, y0.length);
            this.stepStart = this.acceptStep(interpolator, y2, yDotTmp, t);
            System.arraycopy(y2, 0, yTmp, 0, y2.length);
            if (!this.isLastStep) {
                interpolator.storeTime(this.stepStart);
                if (this.fsal) {
                    System.arraycopy(yDotTmp, 0, yDotK[0], 0, y0.length);
                }
                final double factor = FastMath.min(this.maxGrowth, FastMath.max(this.minReduction, this.safety * FastMath.pow(error, this.exp)));
                final double scaledH = this.stepSize * factor;
                final double nextT = this.stepStart + scaledH;
                final boolean nextIsLast = forward ? (nextT >= t) : (nextT <= t);
                hNew = this.filterStep(scaledH, forward, nextIsLast);
                final double filteredNextT = this.stepStart + hNew;
                final boolean filteredNextIsLast = forward ? (filteredNextT >= t) : (filteredNextT <= t);
                if (!filteredNextIsLast) {
                    continue;
                }
                hNew = t - this.stepStart;
            }
        } while (!this.isLastStep);
        equations.setTime(this.stepStart);
        equations.setCompleteState(y2);
        this.resetInternalState();
    }
    
    public double getMinReduction() {
        return this.minReduction;
    }
    
    public void setMinReduction(final double minReduction) {
        this.minReduction = minReduction;
    }
    
    public double getMaxGrowth() {
        return this.maxGrowth;
    }
    
    public void setMaxGrowth(final double maxGrowth) {
        this.maxGrowth = maxGrowth;
    }
    
    protected abstract double estimateError(final double[][] p0, final double[] p1, final double[] p2, final double p3);
}
