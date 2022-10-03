package org.apache.commons.math3.distribution;

import java.util.ArrayList;
import org.apache.commons.math3.util.Pair;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class EnumeratedIntegerDistribution extends AbstractIntegerDistribution
{
    private static final long serialVersionUID = 20130308L;
    protected final EnumeratedDistribution<Integer> innerDistribution;
    
    public EnumeratedIntegerDistribution(final int[] singletons, final double[] probabilities) throws DimensionMismatchException, NotPositiveException, MathArithmeticException, NotFiniteNumberException, NotANumberException {
        this(new Well19937c(), singletons, probabilities);
    }
    
    public EnumeratedIntegerDistribution(final RandomGenerator rng, final int[] singletons, final double[] probabilities) throws DimensionMismatchException, NotPositiveException, MathArithmeticException, NotFiniteNumberException, NotANumberException {
        super(rng);
        this.innerDistribution = new EnumeratedDistribution<Integer>(rng, createDistribution(singletons, probabilities));
    }
    
    public EnumeratedIntegerDistribution(final RandomGenerator rng, final int[] data) {
        super(rng);
        final Map<Integer, Integer> dataMap = new HashMap<Integer, Integer>();
        for (final int value : data) {
            Integer count = dataMap.get(value);
            if (count == null) {
                count = 0;
            }
            dataMap.put(value, ++count);
        }
        final int massPoints = dataMap.size();
        final double denom = data.length;
        final int[] values = new int[massPoints];
        final double[] probabilities = new double[massPoints];
        int index = 0;
        for (final Map.Entry<Integer, Integer> entry : dataMap.entrySet()) {
            values[index] = entry.getKey();
            probabilities[index] = entry.getValue() / denom;
            ++index;
        }
        this.innerDistribution = new EnumeratedDistribution<Integer>(rng, createDistribution(values, probabilities));
    }
    
    public EnumeratedIntegerDistribution(final int[] data) {
        this(new Well19937c(), data);
    }
    
    private static List<Pair<Integer, Double>> createDistribution(final int[] singletons, final double[] probabilities) {
        if (singletons.length != probabilities.length) {
            throw new DimensionMismatchException(probabilities.length, singletons.length);
        }
        final List<Pair<Integer, Double>> samples = new ArrayList<Pair<Integer, Double>>(singletons.length);
        for (int i = 0; i < singletons.length; ++i) {
            samples.add(new Pair<Integer, Double>(singletons[i], probabilities[i]));
        }
        return samples;
    }
    
    public double probability(final int x) {
        return this.innerDistribution.probability(x);
    }
    
    public double cumulativeProbability(final int x) {
        double probability = 0.0;
        for (final Pair<Integer, Double> sample : this.innerDistribution.getPmf()) {
            if (sample.getKey() <= x) {
                probability += sample.getValue();
            }
        }
        return probability;
    }
    
    public double getNumericalMean() {
        double mean = 0.0;
        for (final Pair<Integer, Double> sample : this.innerDistribution.getPmf()) {
            mean += sample.getValue() * sample.getKey();
        }
        return mean;
    }
    
    public double getNumericalVariance() {
        double mean = 0.0;
        double meanOfSquares = 0.0;
        for (final Pair<Integer, Double> sample : this.innerDistribution.getPmf()) {
            mean += sample.getValue() * sample.getKey();
            meanOfSquares += sample.getValue() * sample.getKey() * sample.getKey();
        }
        return meanOfSquares - mean * mean;
    }
    
    public int getSupportLowerBound() {
        int min = Integer.MAX_VALUE;
        for (final Pair<Integer, Double> sample : this.innerDistribution.getPmf()) {
            if (sample.getKey() < min && sample.getValue() > 0.0) {
                min = sample.getKey();
            }
        }
        return min;
    }
    
    public int getSupportUpperBound() {
        int max = Integer.MIN_VALUE;
        for (final Pair<Integer, Double> sample : this.innerDistribution.getPmf()) {
            if (sample.getKey() > max && sample.getValue() > 0.0) {
                max = sample.getKey();
            }
        }
        return max;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
    
    @Override
    public int sample() {
        return this.innerDistribution.sample();
    }
}
