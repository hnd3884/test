package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.analysis.function.Logit;
import org.apache.commons.math3.analysis.function.Sigmoid;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public class MultivariateFunctionMappingAdapter implements MultivariateFunction
{
    private final MultivariateFunction bounded;
    private final Mapper[] mappers;
    
    public MultivariateFunctionMappingAdapter(final MultivariateFunction bounded, final double[] lower, final double[] upper) {
        MathUtils.checkNotNull(lower);
        MathUtils.checkNotNull(upper);
        if (lower.length != upper.length) {
            throw new DimensionMismatchException(lower.length, upper.length);
        }
        for (int i = 0; i < lower.length; ++i) {
            if (upper[i] < lower[i]) {
                throw new NumberIsTooSmallException(upper[i], lower[i], true);
            }
        }
        this.bounded = bounded;
        this.mappers = new Mapper[lower.length];
        for (int i = 0; i < this.mappers.length; ++i) {
            if (Double.isInfinite(lower[i])) {
                if (Double.isInfinite(upper[i])) {
                    this.mappers[i] = new NoBoundsMapper();
                }
                else {
                    this.mappers[i] = new UpperBoundMapper(upper[i]);
                }
            }
            else if (Double.isInfinite(upper[i])) {
                this.mappers[i] = new LowerBoundMapper(lower[i]);
            }
            else {
                this.mappers[i] = new LowerUpperBoundMapper(lower[i], upper[i]);
            }
        }
    }
    
    public double[] unboundedToBounded(final double[] point) {
        final double[] mapped = new double[this.mappers.length];
        for (int i = 0; i < this.mappers.length; ++i) {
            mapped[i] = this.mappers[i].unboundedToBounded(point[i]);
        }
        return mapped;
    }
    
    public double[] boundedToUnbounded(final double[] point) {
        final double[] mapped = new double[this.mappers.length];
        for (int i = 0; i < this.mappers.length; ++i) {
            mapped[i] = this.mappers[i].boundedToUnbounded(point[i]);
        }
        return mapped;
    }
    
    public double value(final double[] point) {
        return this.bounded.value(this.unboundedToBounded(point));
    }
    
    private static class NoBoundsMapper implements Mapper
    {
        NoBoundsMapper() {
        }
        
        public double unboundedToBounded(final double y) {
            return y;
        }
        
        public double boundedToUnbounded(final double x) {
            return x;
        }
    }
    
    private static class LowerBoundMapper implements Mapper
    {
        private final double lower;
        
        LowerBoundMapper(final double lower) {
            this.lower = lower;
        }
        
        public double unboundedToBounded(final double y) {
            return this.lower + FastMath.exp(y);
        }
        
        public double boundedToUnbounded(final double x) {
            return FastMath.log(x - this.lower);
        }
    }
    
    private static class UpperBoundMapper implements Mapper
    {
        private final double upper;
        
        UpperBoundMapper(final double upper) {
            this.upper = upper;
        }
        
        public double unboundedToBounded(final double y) {
            return this.upper - FastMath.exp(-y);
        }
        
        public double boundedToUnbounded(final double x) {
            return -FastMath.log(this.upper - x);
        }
    }
    
    private static class LowerUpperBoundMapper implements Mapper
    {
        private final UnivariateFunction boundingFunction;
        private final UnivariateFunction unboundingFunction;
        
        LowerUpperBoundMapper(final double lower, final double upper) {
            this.boundingFunction = new Sigmoid(lower, upper);
            this.unboundingFunction = new Logit(lower, upper);
        }
        
        public double unboundedToBounded(final double y) {
            return this.boundingFunction.value(y);
        }
        
        public double boundedToUnbounded(final double x) {
            return this.unboundingFunction.value(x);
        }
    }
    
    private interface Mapper
    {
        double unboundedToBounded(final double p0);
        
        double boundedToUnbounded(final double p0);
    }
}
