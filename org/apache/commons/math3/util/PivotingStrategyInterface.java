package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface PivotingStrategyInterface
{
    int pivotIndex(final double[] p0, final int p1, final int p2) throws MathIllegalArgumentException;
}
