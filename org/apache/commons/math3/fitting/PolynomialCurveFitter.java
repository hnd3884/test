package org.apache.commons.math3.fitting;

import java.util.Iterator;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import java.util.Collection;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

public class PolynomialCurveFitter extends AbstractCurveFitter
{
    private static final PolynomialFunction.Parametric FUNCTION;
    private final double[] initialGuess;
    private final int maxIter;
    
    private PolynomialCurveFitter(final double[] initialGuess, final int maxIter) {
        this.initialGuess = initialGuess;
        this.maxIter = maxIter;
    }
    
    public static PolynomialCurveFitter create(final int degree) {
        return new PolynomialCurveFitter(new double[degree + 1], Integer.MAX_VALUE);
    }
    
    public PolynomialCurveFitter withStartPoint(final double[] newStart) {
        return new PolynomialCurveFitter(newStart.clone(), this.maxIter);
    }
    
    public PolynomialCurveFitter withMaxIterations(final int newMaxIter) {
        return new PolynomialCurveFitter(this.initialGuess, newMaxIter);
    }
    
    @Override
    protected LeastSquaresProblem getProblem(final Collection<WeightedObservedPoint> observations) {
        final int len = observations.size();
        final double[] target = new double[len];
        final double[] weights = new double[len];
        int i = 0;
        for (final WeightedObservedPoint obs : observations) {
            target[i] = obs.getY();
            weights[i] = obs.getWeight();
            ++i;
        }
        final TheoreticalValuesFunction model = new TheoreticalValuesFunction(PolynomialCurveFitter.FUNCTION, observations);
        if (this.initialGuess == null) {
            throw new MathInternalError();
        }
        return new LeastSquaresBuilder().maxEvaluations(Integer.MAX_VALUE).maxIterations(this.maxIter).start(this.initialGuess).target(target).weight(new DiagonalMatrix(weights)).model(model.getModelFunction(), model.getModelFunctionJacobian()).build();
    }
    
    static {
        FUNCTION = new PolynomialFunction.Parametric();
    }
}
