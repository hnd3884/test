package org.apache.commons.math3.fitting;

import java.util.Iterator;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import java.util.Collection;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;

public class SimpleCurveFitter extends AbstractCurveFitter
{
    private final ParametricUnivariateFunction function;
    private final double[] initialGuess;
    private final int maxIter;
    
    private SimpleCurveFitter(final ParametricUnivariateFunction function, final double[] initialGuess, final int maxIter) {
        this.function = function;
        this.initialGuess = initialGuess;
        this.maxIter = maxIter;
    }
    
    public static SimpleCurveFitter create(final ParametricUnivariateFunction f, final double[] start) {
        return new SimpleCurveFitter(f, start, Integer.MAX_VALUE);
    }
    
    public SimpleCurveFitter withStartPoint(final double[] newStart) {
        return new SimpleCurveFitter(this.function, newStart.clone(), this.maxIter);
    }
    
    public SimpleCurveFitter withMaxIterations(final int newMaxIter) {
        return new SimpleCurveFitter(this.function, this.initialGuess, newMaxIter);
    }
    
    @Override
    protected LeastSquaresProblem getProblem(final Collection<WeightedObservedPoint> observations) {
        final int len = observations.size();
        final double[] target = new double[len];
        final double[] weights = new double[len];
        int count = 0;
        for (final WeightedObservedPoint obs : observations) {
            target[count] = obs.getY();
            weights[count] = obs.getWeight();
            ++count;
        }
        final TheoreticalValuesFunction model = new TheoreticalValuesFunction(this.function, observations);
        return new LeastSquaresBuilder().maxEvaluations(Integer.MAX_VALUE).maxIterations(this.maxIter).start(this.initialGuess).target(target).weight(new DiagonalMatrix(weights)).model(model.getModelFunction(), model.getModelFunctionJacobian()).build();
    }
}
