package org.apache.commons.math3.distribution;

import java.util.ArrayList;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.Pair;
import java.util.List;

public class MixtureMultivariateNormalDistribution extends MixtureMultivariateRealDistribution<MultivariateNormalDistribution>
{
    public MixtureMultivariateNormalDistribution(final double[] weights, final double[][] means, final double[][][] covariances) {
        super(createComponents(weights, means, covariances));
    }
    
    public MixtureMultivariateNormalDistribution(final List<Pair<Double, MultivariateNormalDistribution>> components) {
        super(components);
    }
    
    public MixtureMultivariateNormalDistribution(final RandomGenerator rng, final List<Pair<Double, MultivariateNormalDistribution>> components) throws NotPositiveException, DimensionMismatchException {
        super(rng, components);
    }
    
    private static List<Pair<Double, MultivariateNormalDistribution>> createComponents(final double[] weights, final double[][] means, final double[][][] covariances) {
        final List<Pair<Double, MultivariateNormalDistribution>> mvns = new ArrayList<Pair<Double, MultivariateNormalDistribution>>(weights.length);
        for (int i = 0; i < weights.length; ++i) {
            final MultivariateNormalDistribution dist = new MultivariateNormalDistribution(means[i], covariances[i]);
            mvns.add(new Pair<Double, MultivariateNormalDistribution>(weights[i], dist));
        }
        return mvns;
    }
}
