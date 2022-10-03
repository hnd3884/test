package org.apache.commons.math3.optim.nonlinear.vector;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;

@Deprecated
public abstract class JacobianMultivariateVectorOptimizer extends MultivariateVectorOptimizer
{
    private MultivariateMatrixFunction jacobian;
    
    protected JacobianMultivariateVectorOptimizer(final ConvergenceChecker<PointVectorValuePair> checker) {
        super(checker);
    }
    
    protected double[][] computeJacobian(final double[] params) {
        return this.jacobian.value(params);
    }
    
    @Override
    public PointVectorValuePair optimize(final OptimizationData... optData) throws TooManyEvaluationsException, DimensionMismatchException {
        return super.optimize(optData);
    }
    
    @Override
    protected void parseOptimizationData(final OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (final OptimizationData data : optData) {
            if (data instanceof ModelFunctionJacobian) {
                this.jacobian = ((ModelFunctionJacobian)data).getModelFunctionJacobian();
                break;
            }
        }
    }
}
