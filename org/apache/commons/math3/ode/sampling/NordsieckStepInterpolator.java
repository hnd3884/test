package org.apache.commons.math3.ode.sampling;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.commons.math3.util.FastMath;
import java.util.Arrays;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.EquationsMapper;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

public class NordsieckStepInterpolator extends AbstractStepInterpolator
{
    private static final long serialVersionUID = -7179861704951334960L;
    protected double[] stateVariation;
    private double scalingH;
    private double referenceTime;
    private double[] scaled;
    private Array2DRowRealMatrix nordsieck;
    
    public NordsieckStepInterpolator() {
    }
    
    public NordsieckStepInterpolator(final NordsieckStepInterpolator interpolator) {
        super(interpolator);
        this.scalingH = interpolator.scalingH;
        this.referenceTime = interpolator.referenceTime;
        if (interpolator.scaled != null) {
            this.scaled = interpolator.scaled.clone();
        }
        if (interpolator.nordsieck != null) {
            this.nordsieck = new Array2DRowRealMatrix(interpolator.nordsieck.getDataRef(), true);
        }
        if (interpolator.stateVariation != null) {
            this.stateVariation = interpolator.stateVariation.clone();
        }
    }
    
    @Override
    protected StepInterpolator doCopy() {
        return new NordsieckStepInterpolator(this);
    }
    
    public void reinitialize(final double[] y, final boolean forward, final EquationsMapper primaryMapper, final EquationsMapper[] secondaryMappers) {
        super.reinitialize(y, forward, primaryMapper, secondaryMappers);
        this.stateVariation = new double[y.length];
    }
    
    public void reinitialize(final double time, final double stepSize, final double[] scaledDerivative, final Array2DRowRealMatrix nordsieckVector) {
        this.referenceTime = time;
        this.scalingH = stepSize;
        this.scaled = scaledDerivative;
        this.nordsieck = nordsieckVector;
        this.setInterpolatedTime(this.getInterpolatedTime());
    }
    
    public void rescale(final double stepSize) {
        final double ratio = stepSize / this.scalingH;
        for (int i = 0; i < this.scaled.length; ++i) {
            final double[] scaled = this.scaled;
            final int n = i;
            scaled[n] *= ratio;
        }
        final double[][] nData = this.nordsieck.getDataRef();
        double power = ratio;
        for (int j = 0; j < nData.length; ++j) {
            power *= ratio;
            final double[] nDataI = nData[j];
            for (int k = 0; k < nDataI.length; ++k) {
                final double[] array = nDataI;
                final int n2 = k;
                array[n2] *= power;
            }
        }
        this.scalingH = stepSize;
    }
    
    public double[] getInterpolatedStateVariation() throws MaxCountExceededException {
        this.getInterpolatedState();
        return this.stateVariation;
    }
    
    @Override
    protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {
        final double x = this.interpolatedTime - this.referenceTime;
        final double normalizedAbscissa = x / this.scalingH;
        Arrays.fill(this.stateVariation, 0.0);
        Arrays.fill(this.interpolatedDerivatives, 0.0);
        final double[][] nData = this.nordsieck.getDataRef();
        for (int i = nData.length - 1; i >= 0; --i) {
            final int order = i + 2;
            final double[] nDataI = nData[i];
            final double power = FastMath.pow(normalizedAbscissa, order);
            for (int j = 0; j < nDataI.length; ++j) {
                final double d = nDataI[j] * power;
                final double[] stateVariation = this.stateVariation;
                final int n = j;
                stateVariation[n] += d;
                final double[] interpolatedDerivatives = this.interpolatedDerivatives;
                final int n2 = j;
                interpolatedDerivatives[n2] += order * d;
            }
        }
        for (int k = 0; k < this.currentState.length; ++k) {
            final double[] stateVariation2 = this.stateVariation;
            final int n3 = k;
            stateVariation2[n3] += this.scaled[k] * normalizedAbscissa;
            this.interpolatedState[k] = this.currentState[k] + this.stateVariation[k];
            this.interpolatedDerivatives[k] = (this.interpolatedDerivatives[k] + this.scaled[k] * normalizedAbscissa) / x;
        }
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        this.writeBaseExternal(out);
        out.writeDouble(this.scalingH);
        out.writeDouble(this.referenceTime);
        final int n = (this.currentState == null) ? -1 : this.currentState.length;
        if (this.scaled == null) {
            out.writeBoolean(false);
        }
        else {
            out.writeBoolean(true);
            for (int j = 0; j < n; ++j) {
                out.writeDouble(this.scaled[j]);
            }
        }
        if (this.nordsieck == null) {
            out.writeBoolean(false);
        }
        else {
            out.writeBoolean(true);
            out.writeObject(this.nordsieck);
        }
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final double t = this.readBaseExternal(in);
        this.scalingH = in.readDouble();
        this.referenceTime = in.readDouble();
        final int n = (this.currentState == null) ? -1 : this.currentState.length;
        final boolean hasScaled = in.readBoolean();
        if (hasScaled) {
            this.scaled = new double[n];
            for (int j = 0; j < n; ++j) {
                this.scaled[j] = in.readDouble();
            }
        }
        else {
            this.scaled = null;
        }
        final boolean hasNordsieck = in.readBoolean();
        if (hasNordsieck) {
            this.nordsieck = (Array2DRowRealMatrix)in.readObject();
        }
        else {
            this.nordsieck = null;
        }
        if (hasScaled && hasNordsieck) {
            this.stateVariation = new double[n];
            this.setInterpolatedTime(t);
        }
        else {
            this.stateVariation = null;
        }
    }
}
