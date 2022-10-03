package org.apache.commons.math3.ode.nonstiff;

import java.util.Arrays;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math3.RealFieldElement;

class AdamsFieldStepInterpolator<T extends RealFieldElement<T>> extends AbstractFieldStepInterpolator<T>
{
    private T scalingH;
    private final FieldODEStateAndDerivative<T> reference;
    private final T[] scaled;
    private final Array2DRowFieldMatrix<T> nordsieck;
    
    AdamsFieldStepInterpolator(final T stepSize, final FieldODEStateAndDerivative<T> reference, final T[] scaled, final Array2DRowFieldMatrix<T> nordsieck, final boolean isForward, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldEquationsMapper<T> equationsMapper) {
        this(stepSize, (FieldODEStateAndDerivative<RealFieldElement>)reference, scaled, (Array2DRowFieldMatrix<RealFieldElement>)nordsieck, isForward, (FieldODEStateAndDerivative<RealFieldElement>)globalPreviousState, (FieldODEStateAndDerivative<RealFieldElement>)globalCurrentState, (FieldODEStateAndDerivative<RealFieldElement>)globalPreviousState, (FieldODEStateAndDerivative<RealFieldElement>)globalCurrentState, (FieldEquationsMapper<RealFieldElement>)equationsMapper);
    }
    
    private AdamsFieldStepInterpolator(final T stepSize, final FieldODEStateAndDerivative<T> reference, final T[] scaled, final Array2DRowFieldMatrix<T> nordsieck, final boolean isForward, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldODEStateAndDerivative<T> softPreviousState, final FieldODEStateAndDerivative<T> softCurrentState, final FieldEquationsMapper<T> equationsMapper) {
        super(isForward, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, equationsMapper);
        this.scalingH = stepSize;
        this.reference = reference;
        this.scaled = scaled.clone();
        this.nordsieck = new Array2DRowFieldMatrix<T>(nordsieck.getData(), false);
    }
    
    @Override
    protected AdamsFieldStepInterpolator<T> create(final boolean newForward, final FieldODEStateAndDerivative<T> newGlobalPreviousState, final FieldODEStateAndDerivative<T> newGlobalCurrentState, final FieldODEStateAndDerivative<T> newSoftPreviousState, final FieldODEStateAndDerivative<T> newSoftCurrentState, final FieldEquationsMapper<T> newMapper) {
        return new AdamsFieldStepInterpolator<T>(this.scalingH, this.reference, this.scaled, this.nordsieck, newForward, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }
    
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> equationsMapper, final T time, final T theta, final T thetaH, final T oneMinusThetaH) {
        return taylor(this.reference, time, this.scalingH, this.scaled, this.nordsieck);
    }
    
    public static <S extends RealFieldElement<S>> FieldODEStateAndDerivative<S> taylor(final FieldODEStateAndDerivative<S> reference, final S time, final S stepSize, final S[] scaled, final Array2DRowFieldMatrix<S> nordsieck) {
        final S x = time.subtract(reference.getTime());
        final S normalizedAbscissa = x.divide(stepSize);
        final S[] stateVariation = MathArrays.buildArray(time.getField(), scaled.length);
        Arrays.fill(stateVariation, time.getField().getZero());
        final S[] estimatedDerivatives = MathArrays.buildArray(time.getField(), scaled.length);
        Arrays.fill(estimatedDerivatives, time.getField().getZero());
        final S[][] nData = nordsieck.getDataRef();
        for (int i = nData.length - 1; i >= 0; --i) {
            final int order = i + 2;
            final S[] nDataI = nData[i];
            final S power = normalizedAbscissa.pow(order);
            for (int j = 0; j < nDataI.length; ++j) {
                final S d = nDataI[j].multiply(power);
                stateVariation[j] = stateVariation[j].add(d);
                estimatedDerivatives[j] = estimatedDerivatives[j].add(d.multiply(order));
            }
        }
        final S[] estimatedState = reference.getState();
        for (int k = 0; k < stateVariation.length; ++k) {
            stateVariation[k] = stateVariation[k].add(scaled[k].multiply(normalizedAbscissa));
            estimatedState[k] = estimatedState[k].add(stateVariation[k]);
            estimatedDerivatives[k] = estimatedDerivatives[k].add(scaled[k].multiply(normalizedAbscissa)).divide(x);
        }
        return new FieldODEStateAndDerivative<S>(time, estimatedState, estimatedDerivatives);
    }
}
