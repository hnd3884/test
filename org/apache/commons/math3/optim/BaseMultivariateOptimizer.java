package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;

public abstract class BaseMultivariateOptimizer<PAIR> extends BaseOptimizer<PAIR>
{
    private double[] start;
    private double[] lowerBound;
    private double[] upperBound;
    
    protected BaseMultivariateOptimizer(final ConvergenceChecker<PAIR> checker) {
        super(checker);
    }
    
    @Override
    public PAIR optimize(final OptimizationData... optData) {
        return super.optimize(optData);
    }
    
    @Override
    protected void parseOptimizationData(final OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (final OptimizationData data : optData) {
            if (data instanceof InitialGuess) {
                this.start = ((InitialGuess)data).getInitialGuess();
            }
            else if (data instanceof SimpleBounds) {
                final SimpleBounds bounds = (SimpleBounds)data;
                this.lowerBound = bounds.getLower();
                this.upperBound = bounds.getUpper();
            }
        }
        this.checkParameters();
    }
    
    public double[] getStartPoint() {
        return (double[])((this.start == null) ? null : ((double[])this.start.clone()));
    }
    
    public double[] getLowerBound() {
        return (double[])((this.lowerBound == null) ? null : ((double[])this.lowerBound.clone()));
    }
    
    public double[] getUpperBound() {
        return (double[])((this.upperBound == null) ? null : ((double[])this.upperBound.clone()));
    }
    
    private void checkParameters() {
        if (this.start != null) {
            final int dim = this.start.length;
            if (this.lowerBound != null) {
                if (this.lowerBound.length != dim) {
                    throw new DimensionMismatchException(this.lowerBound.length, dim);
                }
                for (int i = 0; i < dim; ++i) {
                    final double v = this.start[i];
                    final double lo = this.lowerBound[i];
                    if (v < lo) {
                        throw new NumberIsTooSmallException(v, lo, true);
                    }
                }
            }
            if (this.upperBound != null) {
                if (this.upperBound.length != dim) {
                    throw new DimensionMismatchException(this.upperBound.length, dim);
                }
                for (int i = 0; i < dim; ++i) {
                    final double v = this.start[i];
                    final double hi = this.upperBound[i];
                    if (v > hi) {
                        throw new NumberIsTooLargeException(v, hi, true);
                    }
                }
            }
        }
    }
}
