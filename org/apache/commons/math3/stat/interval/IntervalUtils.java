package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public final class IntervalUtils
{
    private static final BinomialConfidenceInterval AGRESTI_COULL;
    private static final BinomialConfidenceInterval CLOPPER_PEARSON;
    private static final BinomialConfidenceInterval NORMAL_APPROXIMATION;
    private static final BinomialConfidenceInterval WILSON_SCORE;
    
    private IntervalUtils() {
    }
    
    public static ConfidenceInterval getAgrestiCoullInterval(final int numberOfTrials, final int numberOfSuccesses, final double confidenceLevel) {
        return IntervalUtils.AGRESTI_COULL.createInterval(numberOfTrials, numberOfSuccesses, confidenceLevel);
    }
    
    public static ConfidenceInterval getClopperPearsonInterval(final int numberOfTrials, final int numberOfSuccesses, final double confidenceLevel) {
        return IntervalUtils.CLOPPER_PEARSON.createInterval(numberOfTrials, numberOfSuccesses, confidenceLevel);
    }
    
    public static ConfidenceInterval getNormalApproximationInterval(final int numberOfTrials, final int numberOfSuccesses, final double confidenceLevel) {
        return IntervalUtils.NORMAL_APPROXIMATION.createInterval(numberOfTrials, numberOfSuccesses, confidenceLevel);
    }
    
    public static ConfidenceInterval getWilsonScoreInterval(final int numberOfTrials, final int numberOfSuccesses, final double confidenceLevel) {
        return IntervalUtils.WILSON_SCORE.createInterval(numberOfTrials, numberOfSuccesses, confidenceLevel);
    }
    
    static void checkParameters(final int numberOfTrials, final int numberOfSuccesses, final double confidenceLevel) {
        if (numberOfTrials <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_TRIALS, numberOfTrials);
        }
        if (numberOfSuccesses < 0) {
            throw new NotPositiveException(LocalizedFormats.NEGATIVE_NUMBER_OF_SUCCESSES, numberOfSuccesses);
        }
        if (numberOfSuccesses > numberOfTrials) {
            throw new NumberIsTooLargeException(LocalizedFormats.NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE, numberOfSuccesses, numberOfTrials, true);
        }
        if (confidenceLevel <= 0.0 || confidenceLevel >= 1.0) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_CONFIDENCE_LEVEL, confidenceLevel, 0, 1);
        }
    }
    
    static {
        AGRESTI_COULL = new AgrestiCoullInterval();
        CLOPPER_PEARSON = new ClopperPearsonInterval();
        NORMAL_APPROXIMATION = new NormalApproximationInterval();
        WILSON_SCORE = new WilsonScoreInterval();
    }
}
