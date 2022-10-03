package org.apache.commons.math3.ode.nonstiff;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.ode.EquationsMapper;
import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;

class GraggBulirschStoerStepInterpolator extends AbstractStepInterpolator
{
    private static final long serialVersionUID = 20110928L;
    private double[] y0Dot;
    private double[] y1;
    private double[] y1Dot;
    private double[][] yMidDots;
    private double[][] polynomials;
    private double[] errfac;
    private int currentDegree;
    
    public GraggBulirschStoerStepInterpolator() {
        this.y0Dot = null;
        this.y1 = null;
        this.y1Dot = null;
        this.yMidDots = null;
        this.resetTables(-1);
    }
    
    GraggBulirschStoerStepInterpolator(final double[] y, final double[] y0Dot, final double[] y1, final double[] y1Dot, final double[][] yMidDots, final boolean forward, final EquationsMapper primaryMapper, final EquationsMapper[] secondaryMappers) {
        super(y, forward, primaryMapper, secondaryMappers);
        this.y0Dot = y0Dot;
        this.y1 = y1;
        this.y1Dot = y1Dot;
        this.yMidDots = yMidDots;
        this.resetTables(yMidDots.length + 4);
    }
    
    GraggBulirschStoerStepInterpolator(final GraggBulirschStoerStepInterpolator interpolator) {
        super(interpolator);
        final int dimension = this.currentState.length;
        this.y0Dot = null;
        this.y1 = null;
        this.y1Dot = null;
        this.yMidDots = null;
        if (interpolator.polynomials == null) {
            this.polynomials = null;
            this.currentDegree = -1;
        }
        else {
            this.resetTables(interpolator.currentDegree);
            for (int i = 0; i < this.polynomials.length; ++i) {
                this.polynomials[i] = new double[dimension];
                System.arraycopy(interpolator.polynomials[i], 0, this.polynomials[i], 0, dimension);
            }
            this.currentDegree = interpolator.currentDegree;
        }
    }
    
    private void resetTables(final int maxDegree) {
        if (maxDegree < 0) {
            this.polynomials = null;
            this.errfac = null;
            this.currentDegree = -1;
        }
        else {
            final double[][] newPols = new double[maxDegree + 1][];
            if (this.polynomials != null) {
                System.arraycopy(this.polynomials, 0, newPols, 0, this.polynomials.length);
                for (int i = this.polynomials.length; i < newPols.length; ++i) {
                    newPols[i] = new double[this.currentState.length];
                }
            }
            else {
                for (int i = 0; i < newPols.length; ++i) {
                    newPols[i] = new double[this.currentState.length];
                }
            }
            this.polynomials = newPols;
            if (maxDegree <= 4) {
                this.errfac = null;
            }
            else {
                this.errfac = new double[maxDegree - 4];
                for (int i = 0; i < this.errfac.length; ++i) {
                    final int ip5 = i + 5;
                    this.errfac[i] = 1.0 / (ip5 * ip5);
                    final double e = 0.5 * FastMath.sqrt((i + 1) / (double)ip5);
                    for (int j = 0; j <= i; ++j) {
                        final double[] errfac = this.errfac;
                        final int n = i;
                        errfac[n] *= e / (j + 1);
                    }
                }
            }
            this.currentDegree = 0;
        }
    }
    
    @Override
    protected StepInterpolator doCopy() {
        return new GraggBulirschStoerStepInterpolator(this);
    }
    
    public void computeCoefficients(final int mu, final double h) {
        if (this.polynomials == null || this.polynomials.length <= mu + 4) {
            this.resetTables(mu + 4);
        }
        this.currentDegree = mu + 4;
        for (int i = 0; i < this.currentState.length; ++i) {
            final double yp0 = h * this.y0Dot[i];
            final double yp2 = h * this.y1Dot[i];
            final double ydiff = this.y1[i] - this.currentState[i];
            final double aspl = ydiff - yp2;
            final double bspl = yp0 - ydiff;
            this.polynomials[0][i] = this.currentState[i];
            this.polynomials[1][i] = ydiff;
            this.polynomials[2][i] = aspl;
            this.polynomials[3][i] = bspl;
            if (mu < 0) {
                return;
            }
            final double ph0 = 0.5 * (this.currentState[i] + this.y1[i]) + 0.125 * (aspl + bspl);
            this.polynomials[4][i] = 16.0 * (this.yMidDots[0][i] - ph0);
            if (mu > 0) {
                final double ph2 = ydiff + 0.25 * (aspl - bspl);
                this.polynomials[5][i] = 16.0 * (this.yMidDots[1][i] - ph2);
                if (mu > 1) {
                    final double ph3 = yp2 - yp0;
                    this.polynomials[6][i] = 16.0 * (this.yMidDots[2][i] - ph3 + this.polynomials[4][i]);
                    if (mu > 2) {
                        final double ph4 = 6.0 * (bspl - aspl);
                        this.polynomials[7][i] = 16.0 * (this.yMidDots[3][i] - ph4 + 3.0 * this.polynomials[5][i]);
                        for (int j = 4; j <= mu; ++j) {
                            final double fac1 = 0.5 * j * (j - 1);
                            final double fac2 = 2.0 * fac1 * (j - 2) * (j - 3);
                            this.polynomials[j + 4][i] = 16.0 * (this.yMidDots[j][i] + fac1 * this.polynomials[j + 2][i] - fac2 * this.polynomials[j][i]);
                        }
                    }
                }
            }
        }
    }
    
    public double estimateError(final double[] scale) {
        double error = 0.0;
        if (this.currentDegree >= 5) {
            for (int i = 0; i < scale.length; ++i) {
                final double e = this.polynomials[this.currentDegree][i] / scale[i];
                error += e * e;
            }
            error = FastMath.sqrt(error / scale.length) * this.errfac[this.currentDegree - 5];
        }
        return error;
    }
    
    @Override
    protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {
        final int dimension = this.currentState.length;
        final double oneMinusTheta = 1.0 - theta;
        final double theta2 = theta - 0.5;
        final double tOmT = theta * oneMinusTheta;
        final double t4 = tOmT * tOmT;
        final double t4Dot = 2.0 * tOmT * (1.0 - 2.0 * theta);
        final double dot1 = 1.0 / this.h;
        final double dot2 = theta * (2.0 - 3.0 * theta) / this.h;
        final double dot3 = ((3.0 * theta - 4.0) * theta + 1.0) / this.h;
        for (int i = 0; i < dimension; ++i) {
            final double p0 = this.polynomials[0][i];
            final double p2 = this.polynomials[1][i];
            final double p3 = this.polynomials[2][i];
            final double p4 = this.polynomials[3][i];
            this.interpolatedState[i] = p0 + theta * (p2 + oneMinusTheta * (p3 * theta + p4 * oneMinusTheta));
            this.interpolatedDerivatives[i] = dot1 * p2 + dot2 * p3 + dot3 * p4;
            if (this.currentDegree > 3) {
                double cDot = 0.0;
                double c = this.polynomials[this.currentDegree][i];
                for (int j = this.currentDegree - 1; j > 3; --j) {
                    final double d = 1.0 / (j - 3);
                    cDot = d * (theta2 * cDot + c);
                    c = this.polynomials[j][i] + c * d * theta2;
                }
                final double[] interpolatedState = this.interpolatedState;
                final int n = i;
                interpolatedState[n] += t4 * c;
                final double[] interpolatedDerivatives = this.interpolatedDerivatives;
                final int n2 = i;
                interpolatedDerivatives[n2] += (t4 * cDot + t4Dot * c) / this.h;
            }
        }
        if (this.h == 0.0) {
            System.arraycopy(this.yMidDots[1], 0, this.interpolatedDerivatives, 0, dimension);
        }
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        final int dimension = (this.currentState == null) ? -1 : this.currentState.length;
        this.writeBaseExternal(out);
        out.writeInt(this.currentDegree);
        for (int k = 0; k <= this.currentDegree; ++k) {
            for (int l = 0; l < dimension; ++l) {
                out.writeDouble(this.polynomials[k][l]);
            }
        }
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final double t = this.readBaseExternal(in);
        final int dimension = (this.currentState == null) ? -1 : this.currentState.length;
        final int degree = in.readInt();
        this.resetTables(degree);
        this.currentDegree = degree;
        for (int k = 0; k <= this.currentDegree; ++k) {
            for (int l = 0; l < dimension; ++l) {
                this.polynomials[k][l] = in.readDouble();
            }
        }
        this.setInterpolatedTime(t);
    }
}
