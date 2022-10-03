package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public interface BinomialConfidenceInterval
{
    ConfidenceInterval createInterval(final int p0, final int p1, final double p2) throws NotStrictlyPositiveException, NotPositiveException, NumberIsTooLargeException, OutOfRangeException;
}
