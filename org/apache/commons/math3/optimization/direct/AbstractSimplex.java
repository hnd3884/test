package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.exception.OutOfRangeException;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.OptimizationData;

@Deprecated
public abstract class AbstractSimplex implements OptimizationData
{
    private PointValuePair[] simplex;
    private double[][] startConfiguration;
    private final int dimension;
    
    protected AbstractSimplex(final int n) {
        this(n, 1.0);
    }
    
    protected AbstractSimplex(final int n, final double sideLength) {
        this(createHypercubeSteps(n, sideLength));
    }
    
    protected AbstractSimplex(final double[] steps) {
        if (steps == null) {
            throw new NullArgumentException();
        }
        if (steps.length == 0) {
            throw new ZeroException();
        }
        this.dimension = steps.length;
        this.startConfiguration = new double[this.dimension][this.dimension];
        for (int i = 0; i < this.dimension; ++i) {
            final double[] vertexI = this.startConfiguration[i];
            for (int j = 0; j < i + 1; ++j) {
                if (steps[j] == 0.0) {
                    throw new ZeroException(LocalizedFormats.EQUAL_VERTICES_IN_SIMPLEX, new Object[0]);
                }
                System.arraycopy(steps, 0, vertexI, 0, j + 1);
            }
        }
    }
    
    protected AbstractSimplex(final double[][] referenceSimplex) {
        if (referenceSimplex.length <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SIMPLEX_NEED_ONE_POINT, referenceSimplex.length);
        }
        this.dimension = referenceSimplex.length - 1;
        this.startConfiguration = new double[this.dimension][this.dimension];
        final double[] ref0 = referenceSimplex[0];
        for (int i = 0; i < referenceSimplex.length; ++i) {
            final double[] refI = referenceSimplex[i];
            if (refI.length != this.dimension) {
                throw new DimensionMismatchException(refI.length, this.dimension);
            }
            for (int j = 0; j < i; ++j) {
                final double[] refJ = referenceSimplex[j];
                boolean allEquals = true;
                for (int k = 0; k < this.dimension; ++k) {
                    if (refI[k] != refJ[k]) {
                        allEquals = false;
                        break;
                    }
                }
                if (allEquals) {
                    throw new MathIllegalArgumentException(LocalizedFormats.EQUAL_VERTICES_IN_SIMPLEX, new Object[] { i, j });
                }
            }
            if (i > 0) {
                final double[] confI = this.startConfiguration[i - 1];
                for (int l = 0; l < this.dimension; ++l) {
                    confI[l] = refI[l] - ref0[l];
                }
            }
        }
    }
    
    public int getDimension() {
        return this.dimension;
    }
    
    public int getSize() {
        return this.simplex.length;
    }
    
    public abstract void iterate(final MultivariateFunction p0, final Comparator<PointValuePair> p1);
    
    public void build(final double[] startPoint) {
        if (this.dimension != startPoint.length) {
            throw new DimensionMismatchException(this.dimension, startPoint.length);
        }
        (this.simplex = new PointValuePair[this.dimension + 1])[0] = new PointValuePair(startPoint, Double.NaN);
        for (int i = 0; i < this.dimension; ++i) {
            final double[] confI = this.startConfiguration[i];
            final double[] vertexI = new double[this.dimension];
            for (int k = 0; k < this.dimension; ++k) {
                vertexI[k] = startPoint[k] + confI[k];
            }
            this.simplex[i + 1] = new PointValuePair(vertexI, Double.NaN);
        }
    }
    
    public void evaluate(final MultivariateFunction evaluationFunction, final Comparator<PointValuePair> comparator) {
        for (int i = 0; i < this.simplex.length; ++i) {
            final PointValuePair vertex = this.simplex[i];
            final double[] point = vertex.getPointRef();
            if (Double.isNaN(((Pair<K, Double>)vertex).getValue())) {
                this.simplex[i] = new PointValuePair(point, evaluationFunction.value(point), false);
            }
        }
        Arrays.sort(this.simplex, comparator);
    }
    
    protected void replaceWorstPoint(PointValuePair pointValuePair, final Comparator<PointValuePair> comparator) {
        for (int i = 0; i < this.dimension; ++i) {
            if (comparator.compare(this.simplex[i], pointValuePair) > 0) {
                final PointValuePair tmp = this.simplex[i];
                this.simplex[i] = pointValuePair;
                pointValuePair = tmp;
            }
        }
        this.simplex[this.dimension] = pointValuePair;
    }
    
    public PointValuePair[] getPoints() {
        final PointValuePair[] copy = new PointValuePair[this.simplex.length];
        System.arraycopy(this.simplex, 0, copy, 0, this.simplex.length);
        return copy;
    }
    
    public PointValuePair getPoint(final int index) {
        if (index < 0 || index >= this.simplex.length) {
            throw new OutOfRangeException(index, 0, this.simplex.length - 1);
        }
        return this.simplex[index];
    }
    
    protected void setPoint(final int index, final PointValuePair point) {
        if (index < 0 || index >= this.simplex.length) {
            throw new OutOfRangeException(index, 0, this.simplex.length - 1);
        }
        this.simplex[index] = point;
    }
    
    protected void setPoints(final PointValuePair[] points) {
        if (points.length != this.simplex.length) {
            throw new DimensionMismatchException(points.length, this.simplex.length);
        }
        this.simplex = points;
    }
    
    private static double[] createHypercubeSteps(final int n, final double sideLength) {
        final double[] steps = new double[n];
        for (int i = 0; i < n; ++i) {
            steps[i] = sideLength;
        }
        return steps;
    }
}
