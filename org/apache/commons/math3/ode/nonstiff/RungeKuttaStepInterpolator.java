package org.apache.commons.math3.ode.nonstiff;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.commons.math3.ode.EquationsMapper;
import org.apache.commons.math3.ode.AbstractIntegrator;
import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;

abstract class RungeKuttaStepInterpolator extends AbstractStepInterpolator
{
    protected double[] previousState;
    protected double[][] yDotK;
    protected AbstractIntegrator integrator;
    
    protected RungeKuttaStepInterpolator() {
        this.previousState = null;
        this.yDotK = null;
        this.integrator = null;
    }
    
    RungeKuttaStepInterpolator(final RungeKuttaStepInterpolator interpolator) {
        super(interpolator);
        if (interpolator.currentState != null) {
            this.previousState = interpolator.previousState.clone();
            this.yDotK = new double[interpolator.yDotK.length][];
            for (int k = 0; k < interpolator.yDotK.length; ++k) {
                this.yDotK[k] = interpolator.yDotK[k].clone();
            }
        }
        else {
            this.previousState = null;
            this.yDotK = null;
        }
        this.integrator = null;
    }
    
    public void reinitialize(final AbstractIntegrator rkIntegrator, final double[] y, final double[][] yDotArray, final boolean forward, final EquationsMapper primaryMapper, final EquationsMapper[] secondaryMappers) {
        this.reinitialize(y, forward, primaryMapper, secondaryMappers);
        this.previousState = null;
        this.yDotK = yDotArray;
        this.integrator = rkIntegrator;
    }
    
    @Override
    public void shift() {
        this.previousState = this.currentState.clone();
        super.shift();
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        this.writeBaseExternal(out);
        final int n = (this.currentState == null) ? -1 : this.currentState.length;
        for (int i = 0; i < n; ++i) {
            out.writeDouble(this.previousState[i]);
        }
        final int kMax = (this.yDotK == null) ? -1 : this.yDotK.length;
        out.writeInt(kMax);
        for (int k = 0; k < kMax; ++k) {
            for (int j = 0; j < n; ++j) {
                out.writeDouble(this.yDotK[k][j]);
            }
        }
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final double t = this.readBaseExternal(in);
        final int n = (this.currentState == null) ? -1 : this.currentState.length;
        if (n < 0) {
            this.previousState = null;
        }
        else {
            this.previousState = new double[n];
            for (int i = 0; i < n; ++i) {
                this.previousState[i] = in.readDouble();
            }
        }
        final int kMax = in.readInt();
        this.yDotK = (double[][])((kMax < 0) ? null : new double[kMax][]);
        for (int k = 0; k < kMax; ++k) {
            this.yDotK[k] = (double[])((n < 0) ? null : new double[n]);
            for (int j = 0; j < n; ++j) {
                this.yDotK[k][j] = in.readDouble();
            }
        }
        this.integrator = null;
        if (this.currentState != null) {
            this.setInterpolatedTime(t);
        }
        else {
            this.interpolatedTime = t;
        }
    }
}
