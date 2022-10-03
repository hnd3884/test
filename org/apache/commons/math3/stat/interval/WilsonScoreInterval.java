package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.distribution.NormalDistribution;

public class WilsonScoreInterval implements BinomialConfidenceInterval
{
    public ConfidenceInterval createInterval(final int numberOfTrials, final int numberOfSuccesses, final double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        final double alpha = (1.0 - confidenceLevel) / 2.0;
        final NormalDistribution normalDistribution = new NormalDistribution();
        final double z = normalDistribution.inverseCumulativeProbability(1.0 - alpha);
        final double zSquared = FastMath.pow(z, 2);
        final double mean = numberOfSuccesses / (double)numberOfTrials;
        final double factor = 1.0 / (1.0 + 1.0 / numberOfTrials * zSquared);
        final double modifiedSuccessRatio = mean + 1.0 / (2 * numberOfTrials) * zSquared;
        final double difference = z * FastMath.sqrt(1.0 / numberOfTrials * mean * (1.0 - mean) + 1.0 / (4.0 * FastMath.pow(numberOfTrials, 2)) * zSquared);
        final double lowerBound = factor * (modifiedSuccessRatio - difference);
        final double upperBound = factor * (modifiedSuccessRatio + difference);
        return new ConfidenceInterval(lowerBound, upperBound, confidenceLevel);
    }
}
