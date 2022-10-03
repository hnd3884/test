package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.exception.MaxCountExceededException;
import java.io.Externalizable;

public interface StepInterpolator extends Externalizable
{
    double getPreviousTime();
    
    double getCurrentTime();
    
    double getInterpolatedTime();
    
    void setInterpolatedTime(final double p0);
    
    double[] getInterpolatedState() throws MaxCountExceededException;
    
    double[] getInterpolatedDerivatives() throws MaxCountExceededException;
    
    double[] getInterpolatedSecondaryState(final int p0) throws MaxCountExceededException;
    
    double[] getInterpolatedSecondaryDerivatives(final int p0) throws MaxCountExceededException;
    
    boolean isForward();
    
    StepInterpolator copy() throws MaxCountExceededException;
}
