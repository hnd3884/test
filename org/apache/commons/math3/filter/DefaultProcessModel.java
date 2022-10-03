package org.apache.commons.math3.filter;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealMatrix;

public class DefaultProcessModel implements ProcessModel
{
    private RealMatrix stateTransitionMatrix;
    private RealMatrix controlMatrix;
    private RealMatrix processNoiseCovMatrix;
    private RealVector initialStateEstimateVector;
    private RealMatrix initialErrorCovMatrix;
    
    public DefaultProcessModel(final double[][] stateTransition, final double[][] control, final double[][] processNoise, final double[] initialStateEstimate, final double[][] initialErrorCovariance) throws NullArgumentException, NoDataException, DimensionMismatchException {
        this(new Array2DRowRealMatrix(stateTransition), new Array2DRowRealMatrix(control), new Array2DRowRealMatrix(processNoise), new ArrayRealVector(initialStateEstimate), new Array2DRowRealMatrix(initialErrorCovariance));
    }
    
    public DefaultProcessModel(final double[][] stateTransition, final double[][] control, final double[][] processNoise) throws NullArgumentException, NoDataException, DimensionMismatchException {
        this(new Array2DRowRealMatrix(stateTransition), new Array2DRowRealMatrix(control), new Array2DRowRealMatrix(processNoise), null, null);
    }
    
    public DefaultProcessModel(final RealMatrix stateTransition, final RealMatrix control, final RealMatrix processNoise, final RealVector initialStateEstimate, final RealMatrix initialErrorCovariance) {
        this.stateTransitionMatrix = stateTransition;
        this.controlMatrix = control;
        this.processNoiseCovMatrix = processNoise;
        this.initialStateEstimateVector = initialStateEstimate;
        this.initialErrorCovMatrix = initialErrorCovariance;
    }
    
    public RealMatrix getStateTransitionMatrix() {
        return this.stateTransitionMatrix;
    }
    
    public RealMatrix getControlMatrix() {
        return this.controlMatrix;
    }
    
    public RealMatrix getProcessNoise() {
        return this.processNoiseCovMatrix;
    }
    
    public RealVector getInitialStateEstimate() {
        return this.initialStateEstimateVector;
    }
    
    public RealMatrix getInitialErrorCovariance() {
        return this.initialErrorCovMatrix;
    }
}
