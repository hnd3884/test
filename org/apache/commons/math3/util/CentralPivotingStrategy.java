package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.io.Serializable;

public class CentralPivotingStrategy implements PivotingStrategyInterface, Serializable
{
    private static final long serialVersionUID = 20140713L;
    
    public int pivotIndex(final double[] work, final int begin, final int end) throws MathIllegalArgumentException {
        MathArrays.verifyValues(work, begin, end - begin);
        return begin + (end - begin) / 2;
    }
}
