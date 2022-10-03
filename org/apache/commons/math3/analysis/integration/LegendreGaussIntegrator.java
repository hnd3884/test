package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

@Deprecated
public class LegendreGaussIntegrator extends BaseAbstractUnivariateIntegrator
{
    private static final double[] ABSCISSAS_2;
    private static final double[] WEIGHTS_2;
    private static final double[] ABSCISSAS_3;
    private static final double[] WEIGHTS_3;
    private static final double[] ABSCISSAS_4;
    private static final double[] WEIGHTS_4;
    private static final double[] ABSCISSAS_5;
    private static final double[] WEIGHTS_5;
    private final double[] abscissas;
    private final double[] weights;
    
    public LegendreGaussIntegrator(final int n, final double relativeAccuracy, final double absoluteAccuracy, final int minimalIterationCount, final int maximalIterationCount) throws MathIllegalArgumentException, NotStrictlyPositiveException, NumberIsTooSmallException {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        switch (n) {
            case 2: {
                this.abscissas = LegendreGaussIntegrator.ABSCISSAS_2;
                this.weights = LegendreGaussIntegrator.WEIGHTS_2;
                break;
            }
            case 3: {
                this.abscissas = LegendreGaussIntegrator.ABSCISSAS_3;
                this.weights = LegendreGaussIntegrator.WEIGHTS_3;
                break;
            }
            case 4: {
                this.abscissas = LegendreGaussIntegrator.ABSCISSAS_4;
                this.weights = LegendreGaussIntegrator.WEIGHTS_4;
                break;
            }
            case 5: {
                this.abscissas = LegendreGaussIntegrator.ABSCISSAS_5;
                this.weights = LegendreGaussIntegrator.WEIGHTS_5;
                break;
            }
            default: {
                throw new MathIllegalArgumentException(LocalizedFormats.N_POINTS_GAUSS_LEGENDRE_INTEGRATOR_NOT_SUPPORTED, new Object[] { n, 2, 5 });
            }
        }
    }
    
    public LegendreGaussIntegrator(final int n, final double relativeAccuracy, final double absoluteAccuracy) throws MathIllegalArgumentException {
        this(n, relativeAccuracy, absoluteAccuracy, 3, Integer.MAX_VALUE);
    }
    
    public LegendreGaussIntegrator(final int n, final int minimalIterationCount, final int maximalIterationCount) throws MathIllegalArgumentException {
        this(n, 1.0E-6, 1.0E-15, minimalIterationCount, maximalIterationCount);
    }
    
    @Override
    protected double doIntegrate() throws MathIllegalArgumentException, TooManyEvaluationsException, MaxCountExceededException {
        double oldt = this.stage(1);
        int n = 2;
        double t;
        while (true) {
            t = this.stage(n);
            final double delta = FastMath.abs(t - oldt);
            final double limit = FastMath.max(this.getAbsoluteAccuracy(), this.getRelativeAccuracy() * (FastMath.abs(oldt) + FastMath.abs(t)) * 0.5);
            if (this.getIterations() + 1 >= this.getMinimalIterationCount() && delta <= limit) {
                break;
            }
            final double ratio = FastMath.min(4.0, FastMath.pow(delta / limit, 0.5 / this.abscissas.length));
            n = FastMath.max((int)(ratio * n), n + 1);
            oldt = t;
            this.incrementCount();
        }
        return t;
    }
    
    private double stage(final int n) throws TooManyEvaluationsException {
        final double step = (this.getMax() - this.getMin()) / n;
        final double halfStep = step / 2.0;
        double midPoint = this.getMin() + halfStep;
        double sum = 0.0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < this.abscissas.length; ++j) {
                sum += this.weights[j] * this.computeObjectiveValue(midPoint + halfStep * this.abscissas[j]);
            }
            midPoint += step;
        }
        return halfStep * sum;
    }
    
    static {
        ABSCISSAS_2 = new double[] { -1.0 / FastMath.sqrt(3.0), 1.0 / FastMath.sqrt(3.0) };
        WEIGHTS_2 = new double[] { 1.0, 1.0 };
        ABSCISSAS_3 = new double[] { -FastMath.sqrt(0.6), 0.0, FastMath.sqrt(0.6) };
        WEIGHTS_3 = new double[] { 0.5555555555555556, 0.8888888888888888, 0.5555555555555556 };
        ABSCISSAS_4 = new double[] { -FastMath.sqrt((15.0 + 2.0 * FastMath.sqrt(30.0)) / 35.0), -FastMath.sqrt((15.0 - 2.0 * FastMath.sqrt(30.0)) / 35.0), FastMath.sqrt((15.0 - 2.0 * FastMath.sqrt(30.0)) / 35.0), FastMath.sqrt((15.0 + 2.0 * FastMath.sqrt(30.0)) / 35.0) };
        WEIGHTS_4 = new double[] { (90.0 - 5.0 * FastMath.sqrt(30.0)) / 180.0, (90.0 + 5.0 * FastMath.sqrt(30.0)) / 180.0, (90.0 + 5.0 * FastMath.sqrt(30.0)) / 180.0, (90.0 - 5.0 * FastMath.sqrt(30.0)) / 180.0 };
        ABSCISSAS_5 = new double[] { -FastMath.sqrt((35.0 + 2.0 * FastMath.sqrt(70.0)) / 63.0), -FastMath.sqrt((35.0 - 2.0 * FastMath.sqrt(70.0)) / 63.0), 0.0, FastMath.sqrt((35.0 - 2.0 * FastMath.sqrt(70.0)) / 63.0), FastMath.sqrt((35.0 + 2.0 * FastMath.sqrt(70.0)) / 63.0) };
        WEIGHTS_5 = new double[] { (322.0 - 13.0 * FastMath.sqrt(70.0)) / 900.0, (322.0 + 13.0 * FastMath.sqrt(70.0)) / 900.0, 0.5688888888888889, (322.0 + 13.0 * FastMath.sqrt(70.0)) / 900.0, (322.0 - 13.0 * FastMath.sqrt(70.0)) / 900.0 };
    }
}
