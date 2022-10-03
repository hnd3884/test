package org.apache.commons.math3.fraction;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.ConvergenceException;

public class FractionConversionException extends ConvergenceException
{
    private static final long serialVersionUID = -4661812640132576263L;
    
    public FractionConversionException(final double value, final int maxIterations) {
        super(LocalizedFormats.FAILED_FRACTION_CONVERSION, new Object[] { value, maxIterations });
    }
    
    public FractionConversionException(final double value, final long p, final long q) {
        super(LocalizedFormats.FRACTION_CONVERSION_OVERFLOW, new Object[] { value, p, q });
    }
}
