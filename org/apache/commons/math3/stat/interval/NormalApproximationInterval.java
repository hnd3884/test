package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.distribution.NormalDistribution;

public class NormalApproximationInterval implements BinomialConfidenceInterval
{
    public ConfidenceInterval createInterval(final int numberOfTrials, final int numberOfSuccesses, final double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        final double mean = numberOfSuccesses / (double)numberOfTrials;
        final double alpha = (1.0 - confidenceLevel) / 2.0;
        final NormalDistribution normalDistribution = new NormalDistribution();
        final double difference = normalDistribution.inverseCumulativeProbability(1.0 - alpha) * FastMath.sqrt(1.0 / numberOfTrials * mean * (1.0 - mean));
        return new ConfidenceInterval(mean - difference, mean + difference, confidenceLevel);
    }
}
