package org.apache.commons.math3.stat.inference;

import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NotPositiveException;

public class BinomialTest
{
    public boolean binomialTest(final int numberOfTrials, final int numberOfSuccesses, final double probability, final AlternativeHypothesis alternativeHypothesis, final double alpha) {
        final double pValue = this.binomialTest(numberOfTrials, numberOfSuccesses, probability, alternativeHypothesis);
        return pValue < alpha;
    }
    
    public double binomialTest(final int numberOfTrials, final int numberOfSuccesses, final double probability, final AlternativeHypothesis alternativeHypothesis) {
        if (numberOfTrials < 0) {
            throw new NotPositiveException(numberOfTrials);
        }
        if (numberOfSuccesses < 0) {
            throw new NotPositiveException(numberOfSuccesses);
        }
        if (probability < 0.0 || probability > 1.0) {
            throw new OutOfRangeException(probability, 0, 1);
        }
        if (numberOfTrials < numberOfSuccesses) {
            throw new MathIllegalArgumentException(LocalizedFormats.BINOMIAL_INVALID_PARAMETERS_ORDER, new Object[] { numberOfTrials, numberOfSuccesses });
        }
        if (alternativeHypothesis == null) {
            throw new NullArgumentException();
        }
        final BinomialDistribution distribution = new BinomialDistribution(null, numberOfTrials, probability);
        switch (alternativeHypothesis) {
            case GREATER_THAN: {
                return 1.0 - distribution.cumulativeProbability(numberOfSuccesses - 1);
            }
            case LESS_THAN: {
                return distribution.cumulativeProbability(numberOfSuccesses);
            }
            case TWO_SIDED: {
                int criticalValueLow = 0;
                int criticalValueHigh = numberOfTrials;
                double pTotal = 0.0;
                do {
                    final double pLow = distribution.probability(criticalValueLow);
                    final double pHigh = distribution.probability(criticalValueHigh);
                    if (pLow == pHigh) {
                        pTotal += 2.0 * pLow;
                        ++criticalValueLow;
                        --criticalValueHigh;
                    }
                    else if (pLow < pHigh) {
                        pTotal += pLow;
                        ++criticalValueLow;
                    }
                    else {
                        pTotal += pHigh;
                        --criticalValueHigh;
                    }
                } while (criticalValueLow <= numberOfSuccesses && criticalValueHigh >= numberOfSuccesses);
                return pTotal;
            }
            default: {
                throw new MathInternalError(LocalizedFormats.OUT_OF_RANGE_SIMPLE, new Object[] { alternativeHypothesis, AlternativeHypothesis.TWO_SIDED, AlternativeHypothesis.LESS_THAN });
            }
        }
    }
}
