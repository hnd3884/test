package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.distribution.FDistribution;

public class ClopperPearsonInterval implements BinomialConfidenceInterval
{
    public ConfidenceInterval createInterval(final int numberOfTrials, final int numberOfSuccesses, final double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        double lowerBound = 0.0;
        double upperBound = 0.0;
        final double alpha = (1.0 - confidenceLevel) / 2.0;
        final FDistribution distributionLowerBound = new FDistribution(2 * (numberOfTrials - numberOfSuccesses + 1), 2 * numberOfSuccesses);
        final double fValueLowerBound = distributionLowerBound.inverseCumulativeProbability(1.0 - alpha);
        if (numberOfSuccesses > 0) {
            lowerBound = numberOfSuccesses / (numberOfSuccesses + (numberOfTrials - numberOfSuccesses + 1) * fValueLowerBound);
        }
        final FDistribution distributionUpperBound = new FDistribution(2 * (numberOfSuccesses + 1), 2 * (numberOfTrials - numberOfSuccesses));
        final double fValueUpperBound = distributionUpperBound.inverseCumulativeProbability(1.0 - alpha);
        if (numberOfSuccesses > 0) {
            upperBound = (numberOfSuccesses + 1) * fValueUpperBound / (numberOfTrials - numberOfSuccesses + (numberOfSuccesses + 1) * fValueUpperBound);
        }
        return new ConfidenceInterval(lowerBound, upperBound, confidenceLevel);
    }
}
