package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.distribution.NormalDistribution;

public class AgrestiCoullInterval implements BinomialConfidenceInterval
{
    public ConfidenceInterval createInterval(final int numberOfTrials, final int numberOfSuccesses, final double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        final double alpha = (1.0 - confidenceLevel) / 2.0;
        final NormalDistribution normalDistribution = new NormalDistribution();
        final double z = normalDistribution.inverseCumulativeProbability(1.0 - alpha);
        final double zSquared = FastMath.pow(z, 2);
        final double modifiedNumberOfTrials = numberOfTrials + zSquared;
        final double modifiedSuccessesRatio = 1.0 / modifiedNumberOfTrials * (numberOfSuccesses + 0.5 * zSquared);
        final double difference = z * FastMath.sqrt(1.0 / modifiedNumberOfTrials * modifiedSuccessesRatio * (1.0 - modifiedSuccessesRatio));
        return new ConfidenceInterval(modifiedSuccessesRatio - difference, modifiedSuccessesRatio + difference, confidenceLevel);
    }
}
